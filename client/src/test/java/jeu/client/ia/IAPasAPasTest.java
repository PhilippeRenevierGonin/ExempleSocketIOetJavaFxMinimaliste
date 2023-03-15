package jeu.client.ia;

import jeu.Coup;
import jeu.outils.GenerateurDeNombre;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class IAPasAPasTest {
    @Mock
    GenerateurDeNombre alea;

    IAPasAPas ia;

    @BeforeEach
    void setUp() {
        ia = new IAPasAPas(alea);
    }

    @Test
    void choisirPremierNombreTest() {
        when(alea.generate(0, 100)).thenReturn(42, 24, 68, 0, -1, 100, 101);
        assertEquals(42, ia.choisirPremierNombre());
        assertEquals(24, ia.choisirPremierNombre());
        assertEquals(68, ia.choisirPremierNombre());
        assertEquals(0, ia.choisirPremierNombre());
        assertEquals(0, ia.choisirPremierNombre());
        assertEquals(100, ia.choisirPremierNombre());
        assertEquals(100, ia.choisirPremierNombre());
    }

    @Test
    void choisirNombreTestListeNullOuVide() {
        when(alea.generate(0, 100)).thenReturn(12, 49, 99, 1);
        assertEquals(12, ia.choisirNombre(true, null));
        assertEquals(49, ia.choisirNombre(true, new ArrayList<Coup>()));
        assertEquals(99, ia.choisirNombre(false, new ArrayList<Coup>()));
        assertEquals(1, ia.choisirNombre(false, null));
    }

    @Test
    void choisirNombreTest() {
        ArrayList<Coup> coups = new ArrayList<>();
        coups.add(new Coup(42, true));
        assertEquals(41, ia.choisirNombre(true, coups));

        coups.add(new Coup(34, false));
        assertEquals(35, ia.choisirNombre(false, coups));


        // tests à la borne 0
        coups.add(new Coup(-1, false));
        assertEquals(0, ia.choisirNombre(false, coups)); // "cas normal"
        coups.add(new Coup(-1, true));
        assertEquals(0, ia.choisirNombre(true, coups));

        coups.add(new Coup(-10, true));
        assertEquals(0, ia.choisirNombre(true, coups));
        coups.add(new Coup(-10, false));
        assertEquals(0, ia.choisirNombre(false, coups));


        // test à la borne 100
        coups.add(new Coup(101, true));
        assertEquals(100, ia.choisirNombre(true, coups)); // cas normal
        coups.add(new Coup(101, false));
        assertEquals(100, ia.choisirNombre(false, coups));

        coups.add(new Coup(110, true));
        assertEquals(100, ia.choisirNombre(true, coups));
        coups.add(new Coup(110, false));
        assertEquals(100, ia.choisirNombre(false, coups));

    }
}
