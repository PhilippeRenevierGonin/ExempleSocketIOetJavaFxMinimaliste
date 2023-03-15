package jeu.serveur.reseau;

import com.corundumstudio.socketio.SocketIOClient;
import com.corundumstudio.socketio.SocketIOServer;
import jeu.Identification;
import jeu.Protocole;
import jeu.serveur.TrouverLeNombre;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.stubbing.Answer;

import static org.junit.jupiter.api.Assertions.*;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.inOrder;

@ExtendWith(MockitoExtension.class)
class ConnexionServeurIOTest {

    @Mock
    SocketIOServer serverIO;

    @Mock
    TrouverLeNombre moteur;


    @Mock
    SocketIOClient joueur;


    Identification id;


    ConnexionServeurIO connexion;

    @BeforeEach
    void setUp() {
        connexion = new ConnexionServeurIO(serverIO);
        connexion.setMoteur(moteur);

        // on verifie les abonnenement
        verify(serverIO, times(1)).addEventListener(eq(Protocole.IDENTIFICATION), eq(Identification.class), any());
        verify(serverIO, times(1)).addEventListener(eq(Protocole.REPONSE), eq(int.class), any());

        id = new Identification("joeur test", 66, "pour le test");
    }


    @Test
    void traitementIdentificationAcceptee() {
        when(moteur.nouveauJoueur(id)).thenReturn(id);
        ConnexionServeurIO spy = spy(connexion);

        verify(spy, times(0)).associer(id, joueur);
        spy.traitementIdentification(joueur, id);
        verify(spy, times(1)).associer(id, joueur);
        verify(joueur,times(0)).disconnect();

    }

    @Test
    void traitementIdentificationRefusee() {
        ConnexionServeurIO spy = spy(connexion);

        when(moteur.nouveauJoueur(id)).thenReturn(null);
        Object synchro = new Object();
        doAnswer((invocationOnMock) -> {
            synchronized (synchro) {
                synchro.notifyAll();
            }
            return null;
        }).when(joueur).disconnect();

        spy.traitementIdentification(joueur, id);

        synchronized (synchro) {
            try {
                synchro.wait();
            } catch (InterruptedException e) {
                fail("InterruptedException");
            }
        }

        verify(spy, times(0)).associer(id, joueur);
        verify(joueur,times(1)).disconnect();
    }

    @Test
    void traitementReponse() {
        connexion.traitementReponse(12);
        verify(moteur, times(1)).recoitReponse(12);
    }

    @Test
    void demarrer() {
        verify(serverIO, never()).start();
        connexion.demarrer();
        verify(serverIO, times(1)).start();
    }

    @Test
    void envoyerMessage() {
        connexion.associer(id, joueur);
        connexion.envoyerMessage(id, "un message");
        verify(joueur, times(1)).sendEvent("un message");
    }

    @Test
    void testEnvoyerMessage() {
        Object piecejointe = new Object();
        connexion.associer(id, joueur);
        connexion.envoyerMessage(id, "un message", piecejointe);
        verify(joueur, times(1)).sendEvent("un message", piecejointe);
    }

    @Test
    void arreter() {
        Object synchro = new Object();

        InOrder ordreAppels = inOrder(moteur, serverIO, joueur);

        // on est obligé de couvrir tout les cas de stubbing (appelés)
        doNothing().when(moteur).transfereMessage(anyString());
        doAnswer((invocationOnMock) -> {
                synchronized (synchro) {
                    synchro.notifyAll();
                }
                return null;
        }).when(moteur).transfereMessage("fin du serveur - fin");
        /*
        doNothing().when(moteur).transfereMessage("fin du serveur - début");
        doNothing().when(moteur).transfereMessage("fin du serveur - stop");
        doNothing().when(moteur).transfereMessage("fin du serveur - désabonnement"); */

        connexion.associer(id, joueur);
        connexion.arreter();

        synchronized (synchro) {
            try {
                synchro.wait();
            } catch (InterruptedException e) {
                fail("InterruptedException");
            }
        }

        ordreAppels.verify(moteur, times(1)).transfereMessage("fin du serveur - début");
        ordreAppels.verify(joueur, times(1)).disconnect();
        ordreAppels.verify(moteur, times(1)).transfereMessage("fin du serveur - stop");
        ordreAppels.verify(moteur, times(1)).transfereMessage("fin du serveur - désabonnement");
        ordreAppels.verify(serverIO, times(1)).removeAllListeners(Protocole.REPONSE);
        ordreAppels.verify(serverIO, times(1)).removeAllListeners(Protocole.IDENTIFICATION);
        ordreAppels.verify(serverIO, times(1)).stop();
        ordreAppels.verify(moteur, times(1)).transfereMessage("fin du serveur - fin");

    }
}