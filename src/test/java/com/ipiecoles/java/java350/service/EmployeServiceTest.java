package com.ipiecoles.java.java350.service;

import com.ipiecoles.java.java350.exception.EmployeException;
import com.ipiecoles.java.java350.model.Employe;
import com.ipiecoles.java.java350.model.Entreprise;
import com.ipiecoles.java.java350.model.NiveauEtude;
import com.ipiecoles.java.java350.model.Poste;
import com.ipiecoles.java.java350.repository.EmployeRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.persistence.EntityExistsException;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class EmployeServiceTest {

    @InjectMocks
    private EmployeService employeService;

    @Mock
    private EmployeRepository employeRepository;

    @BeforeEach
    public void setup(){
        MockitoAnnotations.initMocks(this.getClass());
    }

    @Test
    public void testEmbaucheTechnicienBTSMiTemps() throws EmployeException {
        //Given
        String nom = "Doe";
        String prenom = "John";
        Poste poste = Poste.COMMERCIAL;
        NiveauEtude niveauEtude = NiveauEtude.LICENCE;
        Double tempsPartiel = 0.5;

        //When
        employeService.embaucheEmploye(nom, prenom, poste, niveauEtude, tempsPartiel);

        //Then
        Mockito.verify(employeRepository).findByMatricule("C00001");
        ArgumentCaptor<Employe> employeArgumentCaptor = ArgumentCaptor.forClass(Employe.class);
        Mockito.verify(employeRepository).save(employeArgumentCaptor.capture());
        Employe employe = employeArgumentCaptor.getValue();
        Assertions.assertThat(employe.getNom()).isEqualTo(nom);
        Assertions.assertThat(employe.getPrenom()).isEqualTo(prenom);
        Assertions.assertThat(employe.getDateEmbauche()).isEqualTo(LocalDate.now());
        Assertions.assertThat(employe.getPerformance()).isEqualTo(Entreprise.PERFORMANCE_BASE);
        Assertions.assertThat(employe.getMatricule()).isEqualTo("C00001");
        //1521.22 * 1.2 * 0.5 = 912.732
        Assertions.assertThat(employe.getSalaire()).isEqualTo(912.73);


    }

    @Test
    public void testEmbaucheTechnicienBTSPleinTemps() throws EmployeException {
        //Given
        String nom = "Doe";
        String prenom = "John";
        Poste poste = Poste.TECHNICIEN;
        NiveauEtude niveauEtude = NiveauEtude.BTS_IUT;
        Double tempsPartiel = 1.0;
        Mockito.when(employeRepository.findLastMatricule()).thenReturn("00345");
        //Mockito.when(employeRepository.findByMatricule(Mockito.anyString())).thenReturn("00345");

        //When
        employeService.embaucheEmploye(nom, prenom, poste, niveauEtude, tempsPartiel);

        //Then
        Mockito.verify(employeRepository).findByMatricule("T00346");
        Mockito.verify(employeRepository, Mockito.never()).avgPerformanceWhereMatriculeStartsWith(Mockito.anyString());
        ArgumentCaptor<Employe> employeArgumentCaptor = ArgumentCaptor.forClass(Employe.class);
        Mockito.verify(employeRepository).save(employeArgumentCaptor.capture());
        Employe employe = employeArgumentCaptor.getValue();
        Assertions.assertThat(employe.getNom()).isEqualTo(nom);
        Assertions.assertThat(employe.getPrenom()).isEqualTo(prenom);
        Assertions.assertThat(employe.getDateEmbauche()).isEqualTo(LocalDate.now());
        Assertions.assertThat(employe.getPerformance()).isEqualTo(Entreprise.PERFORMANCE_BASE);
        Assertions.assertThat(employe.getMatricule()).isEqualTo("T00346");
        //1521.22 * 1.2 = 1825.464
        Assertions.assertThat(employe.getSalaire()).isEqualTo(1825.46);


    }

    @Test
    public void testEmbaucheTechnicienBTSPleinTempsLimiteMatricule() {
        //Given
        String nom = "Doe";
        String prenom = "John";
        Poste poste = Poste.TECHNICIEN;
        NiveauEtude niveauEtude = NiveauEtude.BTS_IUT;
        Double tempsPartiel = 1.0;
        Mockito.when(employeRepository.findLastMatricule()).thenReturn("99999");
        //Mockito.when(employeRepository.findByMatricule(Mockito.anyString())).thenReturn("00345");

        //When
        /*try {
            employeService.embaucheEmploye(nom, prenom, poste, niveauEtude, tempsPartiel);
            Assertions.fail("Une exception aurait dû être levée");
        } catch (EmployeException e) {
            //Then
            Assertions.assertThat(e.getMessage()).isEqualTo("Limite des 100000 matricules atteinte !");
        }*/

        //When
        Throwable exception = Assertions.catchThrowable(() ->
                employeService.embaucheEmploye(nom, prenom, poste, niveauEtude, tempsPartiel));

        //Then
        Assertions.assertThat(exception).isInstanceOf(EmployeException.class);
        Assertions.assertThat(exception.getMessage()).isEqualTo("Limite des 100000 matricules atteinte !");
    }

    @Test
    public void testEmbaucheManagerMasterEmployeExistant() {
        //Given
        String nom = "Doe";
        String prenom = "John";
        Poste poste = Poste.MANAGER;
        NiveauEtude niveauEtude = NiveauEtude.MASTER;
        Double tempsPartiel = 1.0;
        Mockito.when(employeRepository.findLastMatricule()).thenReturn("00345");
        Mockito.when(employeRepository.findByMatricule("M00346")).thenReturn(new Employe());

        //When
        Throwable exception = Assertions.catchThrowable(() ->
                employeService.embaucheEmploye(nom, prenom, poste, niveauEtude, tempsPartiel));

        //Then
        Assertions.assertThat(exception).isInstanceOf(EntityExistsException.class);
        Assertions.assertThat(exception.getMessage()).isEqualTo("L'employé de matricule M00346 existe déjà en BDD");
    }


}