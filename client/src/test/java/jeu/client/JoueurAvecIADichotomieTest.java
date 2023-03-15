package jeu.client;

import jeu.Coup;
import jeu.client.connexion.ConnexionClient;
import jeu.client.ia.IA;
import jeu.client.ia.IADichotomie;
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JoueurAvecIADichotomieTest {

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
        ia = new IADichotomie(alea);
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
        assertEquals(dernierCoupJouer/2, joueur.getPropositionCourante(), "normalement on a divisé par deux "+dernierCoupJouer);
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
        assertEquals((100+dernierCoupJouer)/2, joueur.getPropositionCourante(), "normalement on est au milieu de 100 et de "+dernierCoupJouer);
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
        assertEquals(dernierCoupJouer/2, joueur.getPropositionCourante(), "normalement on a divisé par deux "+dernierCoupJouer);
        verify(connexion, times(1)).envoyerCoup(dernierCoupJouer/2);
        verify(vue, times(1)).afficheMessage("on répond "+(dernierCoupJouer/2));
        // ni assert ni rien, juste pour voir que cela passe
    }

    /**
     * juste pour illustrer le then return
     */
    @Test
    void illustrationWhenThenReturn() {
        when(alea.generate(anyInt(), anyInt())).thenReturn(3);

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
        InOrder ordre = inOrder(vue,connexion);

        // on appel le premier coup
        joueur.premierCoup();
        // envoie du premier nombre
        verify(connexion, times(1)).envoyerCoup(40);

        // rejouer a été appele après 40... 20... 30... 35.. 32... 33... 34
        int[] rep = {20, 30, 35, 32, 33, 34};
        String[] msg = {"la réponse précédente était : trop grande", "la réponse précédente était : trop petite", "la réponse précédente était : trop petite",
                "la réponse précédente était : trop grande", "la réponse précédente était : trop petite", "la réponse précédente était : trop petite"};
        for(int i = 0; i < rep.length; i++) {
            ordre.verify(vue).afficheMessage(msg[i]);
            ordre.verify(vue).afficheMessage("on répond " + rep[i]);
            ordre.verify(connexion).envoyerCoup(rep[i]);
        }

        // 6 trop grand, 6 on répond et on a gagné
        verify(vue, times(12)).afficheMessage(anyString());

        ordre.verify(vue).finit();

        assertEquals(bonneRéponse, joueur.getPropositionCourante(), "normalement on a trouvé "+bonneRéponse);

    }




    @ParameterizedTest(name = "{index} => bonneRéponse={0}, init={1}")
    @CsvSource({
            "34, 40",
            "40, 34",
            "78, 99",
            "99, 78",
            "00, 100",
            "100, 00",
            "24, 25",
            "25, 24",
            "01, 99",
            "99, 01"
    })
    void unScénarioComplet_testParamétrisé(final int bonneRéponse, int init) {
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


        // oracle :
        ArrayList<Integer> rep = new ArrayList<>();
        ArrayList<String> msg = new ArrayList<>();

        int calcul = init;
        int borneMin = 0;
        int borneMax = 100;
        while (calcul != bonneRéponse) {
            if (calcul < bonneRéponse) {
                msg.add("la réponse précédente était : trop petite");
                borneMin = calcul;
            }
            else {
                msg.add("la réponse précédente était : trop grande");
                borneMax = calcul;
            }
            int tmp = calcul;
            calcul = (borneMax+borneMin)/2;
            if (calcul == tmp) calcul += 1;
            rep.add(calcul);
        }

        // un ordre pour les messages textuels
        InOrder ordre = inOrder(vue,connexion);

        // on appel le premier coup
        joueur.premierCoup();
        // envoie du premier nombre
        verify(connexion, times(1)).envoyerCoup(init);

        // rejouer a été appele après ce qui a été calculé dans rep pour l'oracle
        for(int i = 0 ; i < rep.size(); i++) {
            ordre.verify(vue).afficheMessage(msg.get(i));
            ordre.verify(vue).afficheMessage("on répond " + rep.get(i));
            ordre.verify(connexion).envoyerCoup(rep.get(i));
        }

        // 6 trop grand, 6 on répond et on a gagné car 6 = init - bonneReponse
        verify(vue, times(rep.size()*2)).afficheMessage(anyString());

        //verify(vue, times((1))).finit(); // ou
        ordre.verify(vue).finit();

        assertEquals(bonneRéponse, joueur.getPropositionCourante(), "normalement on a trouvé "+bonneRéponse);

    }


}