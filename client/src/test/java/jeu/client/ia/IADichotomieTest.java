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
class IADichotomieTest {
    @Mock
    GenerateurDeNombre alea;

    IADichotomie ia;

    @BeforeEach
    void setUp() {
        ia = new IADichotomie(alea);
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
    void choisirNombreTestAprèsUnCoup() {
        ArrayList<Coup> coups = new ArrayList<>();
        coups.add(new Coup(42, true));
        assertEquals(21, ia.choisirNombre(true, coups));

        coups = new ArrayList<>();
        coups.add(new Coup(34, false));
        assertEquals(67, ia.choisirNombre(false, coups));


        // tests à la borne 0
        coups = new ArrayList<>();
        coups.add(new Coup(-1, false));
        assertEquals(49, ia.choisirNombre(false, coups)); // "cas normal"

        coups = new ArrayList<>();
        coups.add(new Coup(-1, true));
        assertEquals(0, ia.choisirNombre(true, coups)); // "cas quasi normal"

        coups = new ArrayList<>();
        coups.add(new Coup(-10, true));
        assertEquals(0, ia.choisirNombre(true, coups));

        coups = new ArrayList<>();
        coups.add(new Coup(-10, false));
        assertEquals(45, ia.choisirNombre(false, coups)); // "cas normal"


        // test à la borne 100
        coups = new ArrayList<>();
        coups.add(new Coup(101, true));
        assertEquals(50, ia.choisirNombre(true, coups)); // cas normal
        coups = new ArrayList<>();
        coups.add(new Coup(101, false));
        assertEquals(100, ia.choisirNombre(false, coups)); // cas quasi normal

        coups = new ArrayList<>();
        coups.add(new Coup(110, true));
        assertEquals(55, ia.choisirNombre(true, coups)); // cas normal
        coups = new ArrayList<>();
        coups.add(new Coup(110, false));
        assertEquals(100, ia.choisirNombre(false, coups));

    }


    /* scenario de l'énoncé */
    @Test
    void choisirNombreTestScenarioEnonce() {
        when(alea.generate(0, 100)).thenReturn(42);
        ArrayList<Coup> coups = new ArrayList<>();
        assertEquals(42, ia.choisirPremierNombre());
        coups.add(new Coup(42, true));
        assertEquals(21, ia.choisirNombre(true, coups));
        coups.add(new Coup(21, false));
        assertEquals(31, ia.choisirNombre(false, coups));
        coups.add(new Coup(31, true));
        assertEquals(26, ia.choisirNombre(true, coups));
        coups.add(new Coup(26, false));
        assertEquals(28, ia.choisirNombre(false, coups));
        coups.add(new Coup(28, false));
        assertEquals(29, ia.choisirNombre(false, coups));
        coups.add(new Coup(29, false));
        assertEquals(30, ia.choisirNombre(false, coups));
    }

    @Test
    void choisirNombreTestFourchette() {
        // 2 trop grands puis un trop petit
        ArrayList<Coup> coups = new ArrayList<>();
        coups.add(new Coup(50, true));
        coups.add(new Coup(25, true));
        coups.add(new Coup(12, false));
        assertEquals(18, ia.choisirNombre(false, coups));

        // 2 trop petit puis un trop grand
        coups = new ArrayList<>();
        coups.add(new Coup(50, false));
        coups.add(new Coup(75, false));
        coups.add(new Coup(87, true));
        assertEquals(81, ia.choisirNombre(true, coups));

        // que des trop petits
        coups = new ArrayList<>();
        coups.add(new Coup(12, false));
        coups.add(new Coup(56, false));
        coups.add(new Coup(78, false));
        assertEquals(89, ia.choisirNombre(false, coups));

        // que des trop grands
        coups = new ArrayList<>();
        coups.add(new Coup(80, true));
        coups.add(new Coup(40, true));
        coups.add(new Coup(20, true));
        assertEquals(10, ia.choisirNombre(true, coups));

    }

}
