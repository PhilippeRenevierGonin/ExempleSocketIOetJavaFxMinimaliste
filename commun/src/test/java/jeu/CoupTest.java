package jeu;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CoupTest {

    private Coup coup1;
    private Coup coup2identique1;
    private Coup coup5identique1;
    private Coup coup3different;
    private Coup coup4different;

    @BeforeEach
    void setUp() {
        coup1 = new Coup(11, true);
        coup2identique1 = new Coup(11, true);
        coup5identique1 = new Coup(11, true);
        coup3different = new Coup(11, false);
        coup4different = new Coup(91, false);
    }

    @Test
    void testEquals() {
        // reflexive
        assertEquals(coup2identique1, coup2identique1);
        // symetruc
        assertEquals(coup1, coup2identique1);
        assertEquals(coup2identique1, coup1);
        // transitif
        assertEquals(coup1.equals(coup2identique1), coup2identique1.equals(coup5identique1));
        assertEquals(coup2identique1.equals(coup5identique1), coup1.equals(coup5identique1));
        // for null
        assertNotEquals(null, coup1);

        //  des cas avec des valeurs différentes
        assertNotEquals(coup1, coup3different);
        assertNotEquals(coup3different, coup1);
        assertNotEquals(coup1, coup4different);
        assertNotEquals(coup4different, coup1);
        assertNotEquals(coup3different, coup4different);
        assertNotEquals(coup4different, coup3different);
    }

    @Test
    void testHashCode() {
        // https://docs.oracle.com/en/java/javase/17/docs/api/java.base/java/lang/Object.html#hashCode()
        int hash = coup1.hashCode();

        // reflexif
        assertEquals(coup1.hashCode(), coup1.hashCode());
        // 2 objets "equals" même hash
        assertEquals(coup1.hashCode(), coup2identique1.hashCode());
        // plusieurs appels, tjs la même valeur
        assertEquals(hash, coup1.hashCode());
        assertEquals(hash, coup2identique1.hashCode());
    }
}