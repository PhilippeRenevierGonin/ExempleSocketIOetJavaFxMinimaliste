package jeu.client;

import jeu.Coup;
import jeu.client.connexion.ConnexionClient;
import jeu.client.ia.IA;
import jeu.client.ia.IAPasAPas;
import jeu.client.ihm.Vue;
import jeu.outils.GenerateurDeNombre;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.stubbing.Answer;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JoueurAvecIAPasAPasTest {

    private Joueur joueur;

    @Mock
    private ConnexionClient connexion;

    @Mock
    private Vue vue;

    @Mock
    GenerateurDeNombre alea;

    IA ia;

    @BeforeEach
    public void setUp() {
        ia = new IAPasAPas(alea);
        joueur = new Joueur(ia);
        joueur.setConnexion(connexion);
        joueur.setVue(vue);
    }


    @Test
    void coupDAvantTropGrand() {
        // ici accès package à une propriété de Client...
        // on peut ajouter une méthode "dernierCoupJouer"
        int dernierCoupJouer = 30;
        joueur.setPropositionCourante(dernierCoupJouer);
        ArrayList<Coup> coups = new ArrayList<>();
        coups.add(new Coup(dernierCoupJouer, true));
        joueur.rejouer(true, coups);
        assertEquals(dernierCoupJouer-1, joueur.getPropositionCourante(), "normalement on a diminué de 1 par rapport à "+dernierCoupJouer);
        // ni assert ni rien, juste pour voir que cela passe
    }

    @Test
    void coupDAvantTroPetit() {
        // ici accès package à une propriété de Client...
        // on peut ajouter une méthode "dernierCoupJouer"
        int dernierCoupJouer = joueur.getPropositionCourante();
        ArrayList<Coup> coups = new ArrayList<>();
        coups.add(new Coup(dernierCoupJouer, false));
        joueur.rejouer(false, coups);
        assertEquals(dernierCoupJouer+1, joueur.getPropositionCourante(), "normalement on a augmenté de 1 par rapport à "+dernierCoupJouer);
        // ni assert ni rien, juste pour voir que cela passe
    }


    @Test
    void coupDAvantTropGrandEtVerifDuProtocol() {
        // ici accès package à une propriété de Client...
        // on peut ajouter une méthode "dernierCoupJouer"
        int dernierCoupJouer = joueur.getPropositionCourante();
        ArrayList<Coup> coups = new ArrayList<>();
        coups.add(new Coup(dernierCoupJouer, true));
        joueur.rejouer(true, coups);
        assertEquals(dernierCoupJouer-1, joueur.getPropositionCourante(), "normalement on a diminué de 1 par rapport à "+ joueur.propositionCourante);
        verify(connexion, times(1)).envoyerCoup(dernierCoupJouer-1);
        verify(vue, times(1)).afficheMessage("on répond "+(dernierCoupJouer-1));
        // ni assert ni rien, juste pour voir que cela passe
    }

    /**
     * juste pour illustrer le then return
     */
    @Test
    void illustrationWhenThenReturn() {
        when(alea.generate(anyInt(), anyInt())).thenReturn(4,3);
        // le test qui test mockito
        // normalement connexion.calcule devrait être appelé dans une méthode de Client
        assertEquals(4, alea.generate(0,100));
        assertEquals(3, alea.generate(0, 100));

        joueur.premierCoup(); // alea va être appelé
        assertEquals(3, joueur.getPropositionCourante(), "normalement on a commencé avec 3; c'est le mock");

    }


    /**
     * On teste les échanges avec le serveur (fake) dans le cas où la réponse est 34
     * et le joueur commence par 40.
     */
    @Test
    void unScénarioCompletInitTropGrand() {
        final int bonneRéponse = 34;

        ArrayList<Coup> coups = new ArrayList<>();

        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocationOnMock) throws Throwable {
                Object [] args = invocationOnMock.getArguments();
                int val = (int) args[0];

                if (bonneRéponse != val) {
                    boolean tropGrand = (val > bonneRéponse);
                    coups.add(new Coup(val, tropGrand));
                    joueur.rejouer(tropGrand, coups);                }
                else joueur.finPartie();

                return null;
            }
        }).when(connexion).envoyerCoup(anyInt());

        // initialisation ici sauvage, on pourrait/devrait mettre un setter
        // client.propositionCourante = 40;
        when(alea.generate(anyInt(), anyInt())).thenReturn(40);

        // un ordre pour les messages textuels
        InOrder ordreMsg = inOrder(vue);

        // on appel le premier coup
        joueur.premierCoup();
        // envoie du premier nombre
        verify(connexion, times(1)).envoyerCoup(40);

        // rejouer a été appele après 40... 39.. 35... donc 6 fois
        for(int i = 39; i > bonneRéponse; i--) {
            verify(connexion, times(1)).envoyerCoup(i);
            ordreMsg.verify(vue).afficheMessage("la réponse précédente était : trop grande");
            ordreMsg.verify(vue).afficheMessage("on répond "+i);
        }

        // 6 trop grand, 6 on répond et on a gagné
        verify(vue, times(12)).afficheMessage(anyString());

        ordreMsg.verify(vue).finit();

        assertEquals(bonneRéponse, joueur.propositionCourante, "normalement on a trouvé "+bonneRéponse);

    }




    @ParameterizedTest(name = "{index} => bonneRéponse={0}, init={1}")
    @CsvSource({
            "34, 40",
            "78, 99",
            "00,100",
            "24, 25",
            "01, 99"
    })
    void unScénarioCompletInitTropGrand_testParamétrisé(final int bonneRéponse, int init) {
        // commentaire avec 34, 40

        ArrayList<Coup> coups = new ArrayList<>();

        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocationOnMock) throws Throwable {
                Object [] args = invocationOnMock.getArguments();
                int val = (int) args[0];

                if (bonneRéponse != val) {

                    boolean tropGrand = (val > bonneRéponse);
                    coups.add(new Coup(val, tropGrand));
                    joueur.rejouer(tropGrand, coups);                }
                else joueur.finPartie();

                return null;
            }
        }).when(connexion).envoyerCoup(anyInt());

        // initialisation ici sauvage, on pourrait/devrait mettre un setter
        // client.propositionCourante = init;
        when(alea.generate(anyInt(), anyInt())).thenReturn(init);

        // un ordre pour les messages textuels
        InOrder ordreMsg = inOrder(vue);

        // on appel le premier coup
        joueur.premierCoup();
        // envoie du premier nombre
        verify(connexion, times(1)).envoyerCoup(init);

        // rejouer a été appele après 40... 39.. 35... donc 6 fois
        for(int i = init-1; i > bonneRéponse; i--) {
            verify(connexion, times(1)).envoyerCoup(i);
            ordreMsg.verify(vue).afficheMessage("la réponse précédente était : trop grande");
            ordreMsg.verify(vue).afficheMessage("on répond "+i);
        }

        // 6 trop grand, 6 on répond et on a gagné car 6 = init - bonneReponse
        verify(vue, times((init-bonneRéponse)*2)).afficheMessage(anyString());
        verify(vue, times((1))).finit();


        assertEquals(bonneRéponse, joueur.propositionCourante, "normalement on a trouvé "+bonneRéponse);

    }


}