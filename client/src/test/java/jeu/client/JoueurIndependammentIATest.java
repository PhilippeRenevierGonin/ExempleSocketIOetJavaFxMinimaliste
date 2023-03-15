package jeu.client;

import jeu.Coup;
import jeu.Identification;
import jeu.client.connexion.ConnexionClient;
import jeu.client.ia.IA;
import jeu.client.ihm.Vue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JoueurIndependammentIATest {

    private Joueur joueur;

    @Mock
    private ConnexionClient connexion;

    @Mock
    private Vue vue;

    @Mock
    IA ia;

    @BeforeEach
    public void setUp() {
        joueur = new Joueur(ia);
        joueur.setConnexion(connexion);
        joueur.setVue(vue);
    }

    @Test
    void testSeConnecter() {
        joueur.seConnecter();
        verify(connexion, times(1)).seConnecter();
        verify(vue, times(1)).afficheMessage("en attente de déconnexion");
    }

    @Test
    void testAprèsConnexion() {
        joueur.apresConnexion();
        Identification idAttendue = new Identification("Michel B", 42);
        verify(vue, times(1)).afficheMessage("on est connecté ! et on s'identifie");
        verify(connexion, times(1)).envoyerId(idAttendue);
    }

    @Test
    void testTransfèreMessage() {
        String message = "message";
        joueur.transfereMessage(message);
        verify(vue, times(1)).afficheMessage(message);
    }

    // qyestion 5 : les tests indépendamment de l'IA
    @Test
    void rejouerTest() {
        ArrayList<Coup> coups = new ArrayList<>();
        coups.add(new Coup(42, true));
        int nbJoué = 24;
        when(ia.choisirNombre(true, coups)).thenReturn(nbJoué);

        joueur.rejouer(true, coups);

        verify(ia, times(1)).choisirNombre(true, coups);
        verify(vue, times(1)).afficheMessage("on répond "+nbJoué);
        verify(connexion, times(1)).envoyerCoup(nbJoué);
        assertEquals(nbJoué, joueur.getPropositionCourante(), "normalement, il a du jouer "+nbJoué);
    }

    @Test
    void premierCoupTest() {
        int nbJoué = 24;
        when(ia.choisirPremierNombre()).thenReturn(nbJoué);
        joueur.premierCoup();
        verify(connexion, times(1)).envoyerCoup(nbJoué);
        assertEquals(nbJoué, joueur.getPropositionCourante(), "normalement, il a du jouer "+nbJoué);

    }

    // les autres tests sont des tests d'intégrations IAPasAPAs + Joueur
}
