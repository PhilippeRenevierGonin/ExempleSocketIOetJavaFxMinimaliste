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
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.stubbing.Answer;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
class JoueurAvecIAPasAPasTropPetitTest {

    Joueur joueur;

    @Mock
    ConnexionClient connexion;

    @Mock
    Vue vue;

    @Mock
    GenerateurDeNombre alea;

    IA ia ;

    @BeforeEach
    void setUp() {
        ia = new IAPasAPas(alea);
        joueur = new Joueur(ia);
        // client = spy(client);
        // vue = spy(new Vue(client)); // si vue n'est pas un mock
        joueur.setVue(vue);
        joueur.setConnexion(connexion);


    }



    @Test
    void testScenarioTropPetit(){
        final int bonneRéponse = 26;
        ArrayList<Coup> coups = new ArrayList<>();

        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocationOnMock) throws Throwable {

                Object [] args = invocationOnMock.getArguments();
                int val = (int) args[0];

                if (bonneRéponse != val) {
                    boolean tropGrand = (val > bonneRéponse);
                    coups.add(new Coup(val, tropGrand));
                    joueur.rejouer(tropGrand, coups);
                }
                else {
                    joueur.finPartie();
                }

                return null;
            }
        }).when(connexion).envoyerCoup(anyInt());


        // client.setPropositionCourante(10);
        when(alea.generate(0, 100)).thenReturn(10);


        // un ordre pour les messages textuels
        InOrder ordreMsg = inOrder(vue);

        joueur.premierCoup();

        // envoie du premier nombre
        verify(connexion, times(1)).envoyerCoup(10);

        // rejouer a été appele après 40... 39.. 35... donc 6 fois
        for(int i = 11; i < bonneRéponse; i++) {
            verify(connexion, times(1)).envoyerCoup(i);
            ordreMsg.verify(vue).afficheMessage("la réponse précédente était : trop petite");
            ordreMsg.verify(vue).afficheMessage("on répond "+i);
        }

        // 6 trop grand, 16 on répond et on a gagné
        verify(vue, times(32)).afficheMessage(anyString());

        verify(connexion, times(1)).envoyerCoup(bonneRéponse);
        ordreMsg.verify(vue).finit();

        assertEquals(bonneRéponse, joueur.getPropositionCourante(), "normalement on a trouvé "+bonneRéponse);

    }
}