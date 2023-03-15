package jeu;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class IdentificationTest {

    private Identification id1;
    private Identification id2Identifque1;
    private Identification id5Identique1;
    private Identification id3;
    private Identification id4;

    @BeforeEach
    void setUp() {
        String nom = "nom du premier";
        int level = 24;
        String description = "une description comme une autre";
        id1 = new Identification(nom, level, description);
        id2Identifque1 = new Identification(nom, level, description);
        id5Identique1 = new Identification(nom, level, description);
        id3 = new Identification(nom, level);
        id4 = new Identification(nom+nom, level, description+description);
    }

    @Test
    void testEquals() {
        // reflexive
        assertEquals(id2Identifque1, id2Identifque1);
        // symetruc
        assertEquals(id1, id2Identifque1);
        assertEquals(id2Identifque1, id1);
        // transitif
        assertEquals(id1.equals(id2Identifque1), id2Identifque1.equals(id5Identique1));
        assertEquals(id2Identifque1.equals(id5Identique1), id1.equals(id5Identique1));
        // for null
        assertNotEquals(null, id1);

        //  des cas avec des valeurs différentes
        assertNotEquals(id1, id3);
        assertNotEquals(id3, id1);
        assertNotEquals(id1, id4);
        assertNotEquals(id4, id1);
        assertNotEquals(id3, id4);
        assertNotEquals(id4, id3);
    }

    @Test
    void testHashCode() {
        // https://docs.oracle.com/en/java/javase/17/docs/api/java.base/java/lang/Object.html#hashCode()
        int hash = id1.hashCode();

        // reflexif
        assertEquals(id1.hashCode(), id1.hashCode());
        // 2 objets "equals" même hash
        assertEquals(id1.hashCode(), id2Identifque1.hashCode());
        // plusieurs appels, tjs la même valeur
        assertEquals(hash, id1.hashCode());
        assertEquals(hash, id2Identifque1.hashCode());
    }
}