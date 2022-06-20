package com.ipiecoles.java.java350.repository;

import com.ipiecoles.java.java350.model.Employe;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.util.Arrays;

@SpringBootTest
public class EmployeRepositoryTest {
    @Autowired
    EmployeRepository employeRepository;

    @AfterEach
    @BeforeEach
    public void purge() {
        employeRepository.deleteAll();
    }

    @Test
    public void testFindLastMatriculeWithoutEmploye(){
        //Given
        purge();

        //When
        String lastMatricule = employeRepository.findLastMatricule();

        //Then
        Assertions.assertThat(lastMatricule).isNull();
    }

    @Test
    public void testFindLastMatriculeWithEmploye() {
        //Given
        purge();
        Employe employe = new Employe("DOe", "John", "M12345" ,LocalDate.now(),2500d,1,1.0);
        employeRepository.save(employe);


        //When
        String lastMatricule = employeRepository.findLastMatricule();

        //Then
        Assertions.assertThat(lastMatricule).isEqualTo("12345");

    }

    @Test
    public void testFindLastMatriculeWith3Employe() {
        //Given
        purge();
        Employe employe = new Employe("DOe", "John", "M12345" ,LocalDate.now(),2500d,1,1.0);
        Employe employe2 = new Employe("DOe", "John", "M12345" ,LocalDate.now(),2500d,1,1.0);
        Employe employe3 = new Employe("DOe", "John", "M12345" ,LocalDate.now(),2500d,1,1.0);
        employeRepository.saveAll(Arrays.asList(employe,employe2,employe3));


        //When
        String lastMatricule = employeRepository.findLastMatricule();

        //Then
        Assertions.assertThat(lastMatricule).isEqualTo("12345");

    }


}
