package jeu.client.connexion;

import io.socket.client.Socket;
import jeu.Coup;
import jeu.Identification;
import jeu.Protocole;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import jeu.client.Joueur;

import java.util.ArrayList;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ConnexionClientIOTest {

    @Mock
    Joueur joueur;

    @Mock
    Socket socket;

    ConnexionClientIO connexion;


    @BeforeEach
    void setUp() {
        connexion = new ConnexionClientIO(joueur, socket);
    }

    @Test
    void sAbonner() {
        // c'est appelé directement par le constructeur
        verify(socket, times(1)).on(eq("connect"), any());
        verify(socket, times(1)).on(eq("disconnect"), any());
        verify(socket, times(1)).on(eq("error"), any());
        verify(socket, times(1)).on(eq("reconnect_attempt"), any());
        verify(socket, times(1)).on(eq(Protocole.QUESTION), any());
    }

    @Test
    void traitementConnect() {
        connexion.traitementConnect();
        verify(joueur, times(1)).transfereMessage("après connexion");
        verify(joueur, times(1)).apresConnexion();
    }

    @Test
    void traitementDisconnect() {
        connexion.traitementDisconnect();
        verify(joueur, times(1)).transfereMessage(" !! on est déconnecté !! ");
        verify(joueur, times(1)).finPartie();
    }

    @Test
    void traitementErreur() {
        connexion.traitementErreur();
        verify(joueur, times(1)).transfereMessage(" !! erreur de connexion !! ");
        verify(joueur, times(1)).finPartie();
    }

    @Test
    void traitementReconnectAttempt() {
        Object[] casInf60 = {1};
        connexion.traitementReconnectAttempt(casInf60);
        verify(joueur, times(1)).transfereMessage(" !! reconnect_attempt !! 1");
        verify(joueur, never()).finPartie();

        Object[] cas60 = {60};
        connexion.traitementReconnectAttempt(cas60);
        verify(joueur, times(1)).transfereMessage(" !! reconnect_attempt !! 60");
        verify(joueur, never()).finPartie();

        Object[] casSup60 = {61};
        connexion.traitementReconnectAttempt(casSup60);
        verify(joueur, times(1)).transfereMessage(" !! reconnect_attempt !! 61");
        verify(joueur, times(1)).transfereMessage(" !! reconnect_attempt !! on n'arrive pas à se connecter...");
        verify(joueur, times(1)).finPartie();

    }

    @Test
    void traitementQuestionPremierCoup() {
        connexion.traitementQuestion(new Object[0]);
        verify(joueur, times(1)).premierCoup();
    }

    @Test
    void traitementQuestion() {
        reset(joueur);
        ArrayList<Coup> coups = new ArrayList<>() ;
        coups.add(new Coup(42, true));
        coups.add(new Coup(41, true));
        JSONArray tab = new JSONArray();
        for(Coup c : coups) {
            tab.put(new JSONObject(c));
        }

        Object [] params = { true, tab};
        connexion.traitementQuestion(params);
        verify(joueur, times(1)).rejouer(true, coups);
    }

    @Test
    void seConnecter() {
        connexion.seConnecter();
        verify(socket, times(1)).connect();
    }

    @Test
    void envoyerId() {
        reset(socket);
        Identification id = new Identification("Michel", 33, "Mi");
        JSONObject pieceJointe = new JSONObject(id) {
            @Override
            public boolean equals(Object obj) {
                if ((obj != null) && (obj instanceof JSONObject)) {
                    JSONObject o = (JSONObject) obj;
                    // try {
                        // boolean val = o.get("nom").equals("Michel") && o.get("description").equals("Mi") && o.get("niveau").equals(33);
                        boolean val = o.toString().equals(toString());
                        return val;
                    // } catch (JSONException e) {
                    //     return false;
                    // }
                } else return false;
            }
        };
        connexion.envoyerId(id);
        verify(socket, times(1)).emit(Protocole.IDENTIFICATION, pieceJointe);
    }

    @Test
    void envoyerCoup() {
        int coup = 33;
        connexion.envoyerCoup(coup);
        verify(socket, times(1)).emit(Protocole.REPONSE, coup);
    }

    @Test
    void stop() {
        connexion.stop();
        verify(socket, times(1)).off("connect");
        verify(socket, times(1)).off(Protocole.QUESTION);
        verify(socket, times(1)).off("error");
        verify(socket, times(1)).disconnect();
    }


}