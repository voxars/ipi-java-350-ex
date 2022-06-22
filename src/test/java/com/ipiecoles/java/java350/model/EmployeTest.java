package com.ipiecoles.java.java350.model;

import com.ipiecoles.java.java350.exception.EmployeException;
import com.ipiecoles.java.java350.model.Employe;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.time.LocalDate;

public class EmployeTest {

    @Test
    public void testGetNombreAnneeAncienneteNow(){
        //Given
        Employe employe = new Employe();
        employe.setDateEmbauche(LocalDate.now());

        //When
        Integer nbAnneeAnciennete = employe.getNombreAnneeAnciennete();

        //Then
        Assertions.assertThat(nbAnneeAnciennete).isZero();
    }

    @Test
    public void testGetNombreAnneeAncienneteAfterNow(){
        //Given
        Employe employe = new Employe();
        employe.setDateEmbauche(LocalDate.now().minusYears(10));

        //When
        Integer nbAnneeAnciennete = employe.getNombreAnneeAnciennete();

        //Then
        Assertions.assertThat(nbAnneeAnciennete).isEqualTo(10);
    }

    @Test
    public void testGetNombreAnneeAncienneteBeforeNow(){
        //Given
        Employe employe = new Employe();
        employe.setDateEmbauche(LocalDate.now().plusYears(2));

        //When
        Integer nbAnneeAnciennete = employe.getNombreAnneeAnciennete();

        //Then
        Assertions.assertThat(nbAnneeAnciennete).isZero();
    }

    @Test
    public void testGetNombreAnneeAncienneteNull(){
        //Given
        Employe employe = new Employe();
        employe.setDateEmbauche(null);

        //When
        Integer nbAnneeAnciennete = employe.getNombreAnneeAnciennete();

        //Then
        Assertions.assertThat(nbAnneeAnciennete).isZero();
    }

    //test pour getPrimeAnuelle
    //tester matricule null, qui commence par "M", qui ne commence pas par "M", qui n'est pas valide
    // tester performance null ou valeur positive / négative / vaut 0
    // tester temps partiel null négatif, positif, vaut 0
    @ParameterizedTest
    @CsvSource({
            "'M123456',0,1,1.0,1700.0",
            "'M123456',1,1,1.0,1800.0",
            "'T123456',0,1,1.0,1000.0",
            ",0,1,1.0,1000.0",
            "'T123456',0,,1.0,1000.0",
            "'T123456',0,1,0.5,500.0",
            "'T123456',0,2,1.0,2300.0",
            "'T123456',1,2,1.0,2400.0",
            "'T123456',1,1,1.0,1100.0"
    })
    public void testGetPrimeAnuelle(
            String matricule,
            Integer nbAnneesAnciennete,
            Integer performance,
            Double tauxActivite,
            Double prime
    ){
        //Given
        Employe employe = new Employe("Doe", "John", matricule,
                LocalDate.now().minusYears(nbAnneesAnciennete), 2500d, performance, tauxActivite);

        //When
        Double primeObtenue = employe.getPrimeAnnuelle();

        //Then
        Assertions.assertThat(primeObtenue).isEqualTo(prime);
    }

    @Test
    public void testAugmenterSalaireAvecPourcentageNegatif() {
        //Given
        Employe employe = new Employe("Doe", "John", "M123456", LocalDate.now().minusYears(10), 2500d, 0, 1.0);

        //When
        Throwable thrown = Assertions.catchThrowable(() -> employe.augmenterSalaire(-10));

        //Then
        Assertions.assertThat(thrown).isInstanceOf(EmployeException.class).hasMessage("Pourcentage invalide");
    }

    @Test
    public void testAugmenterSalaireAvecPourcentageSupperieurA100() {
        //Given
        Employe employe = new Employe("Doe", "John", "M123456", LocalDate.now().minusYears(10), 2500d, 0, 1.0);

        //When
        Throwable thrown = Assertions.catchThrowable(() -> employe.augmenterSalaire(110));

        //Then
        Assertions.assertThat(thrown).isInstanceOf(EmployeException.class).hasMessage("Pourcentage invalide");
    }

    @Test
    public void testAugmenterSalaireAvecPourcentageValide() {
        //Given
        Employe employe = new Employe("Doe", "John", "M123456", LocalDate.now().minusYears(10), 2500d, 0, 1.0);

        //When
        Throwable thrown = Assertions.catchThrowable(() -> employe.augmenterSalaire(10));

        //Then
        Assertions.assertThat(employe.getSalaire()).isEqualTo(2750d);
    }

    @Test
    public void testAugmenterSalaireAvecSalaireNull() {
        //Given
        Employe employe = new Employe("Doe", "John", "M123456", LocalDate.now().minusYears(10), null, 0, 1.0);

        //When
        Throwable thrown = Assertions.catchThrowable(() -> employe.augmenterSalaire(10));

        //Then
        Assertions.assertThat(thrown).isInstanceOf(EmployeException.class).hasMessage("Salaire null");
    }

    //test paramettrer getNbRtt
    @ParameterizedTest
    @CsvSource({
            "2019,4,0.5",
            "2020,10,1.0",
            "2021,11,1.0",
            "2022,5,0.5",
            "2023,9,1.0",
            "2024,9,1.0",
            "2025,4,0.5"
    })
    public void testGetNbRtt( Integer annee, Integer nbRtt, Double tempsPartiel) {
        //given
        LocalDate dateTest = LocalDate.of(annee, 1, 1);
        Employe employe = new Employe();
        employe.setTempsPartiel(tempsPartiel);

        //when
        Integer nbRttObtenue = employe.getNbRtt(dateTest);

        //then
        Assertions.assertThat(nbRttObtenue).isEqualTo(nbRtt);
    }

    //test paramettrer getNbRtt avec le premier jour de l'annee
    @ParameterizedTest
    @CsvSource({
            "2019,4,0.5,1",
            "2020,10,1.0,1",
            "2021,11,1.0,1",
            "2022,5,0.5,1",
            "2023,9,1.0,1",
            "2024,9,1.0,1",
            "2025,4,0.5,1"
    })
    public void testGetNbRttJourDeLaSemaine( Integer annee, Integer nbRtt, Double tempsPartiel, Integer jour) {
        //given
        LocalDate dateTest = LocalDate.of(annee, 1, jour);
        Employe employe = new Employe();
        employe.setTempsPartiel(tempsPartiel);


        //when
        Integer nbRttObtenue = employe.getNbRtt(dateTest);

        //then
        Assertions.assertThat(nbRttObtenue).isEqualTo(nbRtt);
    }



}
