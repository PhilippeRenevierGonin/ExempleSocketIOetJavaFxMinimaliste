package jeu.serveur;

import jeu.Identification;
import jeu.serveur.ihm.Vue;
import jeu.serveur.reseau.ConnexionServeur;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class TrouverLeNombreTest {

    TrouverLeNombre serveurTesté;
    int valeurÀTrouvée = 42;
    Identification joueurTest;

    @Mock
    ConnexionServeur connexion;

    @Mock
    Vue v;

    @BeforeEach
    void setUp() {
        serveurTesté = new TrouverLeNombre(valeurÀTrouvée);
        serveurTesté.setConnexion(connexion);
        joueurTest = new Identification("joueur pour le test", 99);
        serveurTesté.setLeClient(joueurTest);
        serveurTesté.setVue(v);
    }

    @Test
    void testRéponseTrouvée() {
        serveurTesté.recoitReponse(valeurÀTrouvée);
        verify(connexion, times(1)).arreter();
    }

    @Test
    void testRéponseTropPetite() {
        serveurTesté.recoitReponse(valeurÀTrouvée-2);
        //    -> appel de getConnexion().envoyerMessage(leClient, "question", plusGrand, coups);
        verify(connexion, times(1)).envoyerMessage(eq(joueurTest), eq("question"), eq(false), any());
    }

    @Test
    void testRéponseTropGrande() {
        serveurTesté.recoitReponse(valeurÀTrouvée+2);
        //    -> appel de getConnexion().envoyerMessage(leClient, "question", plusGrand, coups);
        verify(connexion, times(1)).envoyerMessage(eq(joueurTest), eq("question"), eq(true), any());
    }
}