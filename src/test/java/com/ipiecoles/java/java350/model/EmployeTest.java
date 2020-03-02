package com.ipiecoles.java.java350.model;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

public class EmployeTest {

    @Test
    public void testGetNbAnneeAncienneteDtEmbaucheNull(){
        //Given
        Employe employe = new Employe();
        employe.setDateEmbauche(null);

        //When
        Integer nbAnneeAnciennete = employe.getNombreAnneeAnciennete();

        //Then
        Assertions.assertThat(nbAnneeAnciennete).isEqualTo(0);
    }

}
