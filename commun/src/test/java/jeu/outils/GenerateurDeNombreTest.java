package jeu.outils;

import jeu.outils.GenerateurDeNombre;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.security.SecureRandom;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GenerateurDeNombreTest {

    @Mock
    Random rand;

    GenerateurDeNombre generateurTesté;

    @BeforeEach
    void setUp() {
        generateurTesté = new GenerateurDeNombre();
        generateurTesté.setAlea(rand);
    }

    @Test
    void testGenerate() {
        when(rand.nextInt(anyInt())).thenReturn(5);
        assertEquals(10, generateurTesté.generate(5,15));
        // assertEquals(20, generateurTesté.generate(15,15));
    }

    @Test
    void testMaxEgalMinAvecMock() {
        when(rand.nextInt(0)).thenThrow(IllegalArgumentException.class);
        assertThrows(IllegalArgumentException.class, () -> generateurTesté.generate(15, 15), "Rand(0) n'existe pas");
    }

    @Test
    void testMaxEgalMin() {
        generateurTesté.setAlea(new SecureRandom());
        assertThrows(IllegalArgumentException.class,  () -> generateurTesté.generate(15,15), "Rand(0) n'existe pas");
    }


    @Test
    void testMaxInfMinAvecMock() {
        when(rand.nextInt(-4)).thenThrow(IllegalArgumentException.class);
        assertThrows(IllegalArgumentException.class, () -> generateurTesté.generate(5, 1), "Rand(-4) n'existe pas");
    }

    @Test
    void testMaxInfMin() {
        generateurTesté.setAlea(new SecureRandom());
        assertThrows(IllegalArgumentException.class,  () -> generateurTesté.generate(5,1), "Rand(-4) n'existe pas");
    }




}