package jeu.client.connexion;

import io.socket.client.IO;
import io.socket.client.Socket;
import jeu.Coup;
import jeu.Identification;
import jeu.Protocole;
import jeu.client.Joueur;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;
import java.util.ArrayList;

public class ConnexionClientIO implements ConnexionClient {

    private final Joueur controleur;
    Socket connexion;

    public ConnexionClientIO(String urlServeur, Joueur ctrl) {
        this.controleur = ctrl;
        controleur.setConnexion(this);

        try {
            IO.Options opts = new IO.Options();
            opts.timeout = 1000; // en ms
            connexion = IO.socket(urlServeur, opts);
        } catch (URISyntaxException e) {
            controleur.transfereErreur(e.getMessage());
        }

        sAbonner();

    }

    protected ConnexionClientIO(Joueur ctrl, Socket connexion) {
        this.controleur = ctrl;
        controleur.setConnexion(this);

        this.connexion = connexion;

        sAbonner();

    }


    protected void sAbonner() {
            this.controleur.transfereMessage("on s'abonne à la connection / déconnection ");

            connexion.on("connect", objects ->  traitementConnect());

            connexion.on("disconnect", objects -> traitementDisconnect());

            connexion.on("error", objects -> traitementErreur());

            connexion.on("reconnect_attempt", this::traitementReconnectAttempt);

            // on recoit une question
            connexion.on(Protocole.QUESTION, this::traitementQuestion);


    }

    protected void traitementConnect() {
        // déplacement du message dans Client/Controleur
        // on s'identifie
        controleur.transfereMessage("après connexion");
        controleur.apresConnexion();
    }

    protected void traitementDisconnect() {
        controleur.transfereMessage(" !! on est déconnecté !! ");

        controleur.finPartie();
    }

    protected void traitementErreur() {
        controleur.transfereMessage(" !! erreur de connexion !! ");
        controleur.finPartie();
    }

    protected void traitementReconnectAttempt(Object[] objects) {
        controleur.transfereMessage(" !! reconnect_attempt !! "+ objects[0]);
        int nbAttempt = (int) objects[0];
        /// 60s max d'attente
        if (nbAttempt > 60) {
            controleur.transfereMessage(" !! reconnect_attempt !! on n'arrive pas à se connecter...");
            controleur.finPartie();
        }
    }

    protected void traitementQuestion(Object[] objects) {
        if (objects.length > 0 ) {
            boolean plusGrand = (Boolean) objects[0];
            // false, c'est plus petit... !! erreur... dans les commit d'avant

            // conversion local en ArrayList, juste pour montrer
            JSONArray tab = (JSONArray) objects[1];
            ArrayList<Coup> coups = new ArrayList<>();
            for(int i = 0; i < tab.length(); i++) {

                try {
                    coups.add(new Coup(tab.getJSONObject(i).getInt("coupJoue"), tab.getJSONObject(i).getBoolean("plusGrand")));
                } catch (JSONException e) {
                    controleur.transfereErreur(e.getMessage());
                }
            }
            controleur.rejouer(plusGrand, coups);

        } else controleur.premierCoup();
    }

    public void seConnecter() {
        // on se connecte
        connexion.connect();
    }

    public void envoyerId(Identification moi) {
        // conversion automatique obj <-> json
        JSONObject pieceJointe = new JSONObject(moi);
        connexion.emit(Protocole.IDENTIFICATION, pieceJointe);
    }

    public void envoyerCoup(int val) {
        connexion.emit(Protocole.REPONSE,val);
    }

    public void stop() {
        connexion.off("connect");
        connexion.off(Protocole.QUESTION);
        connexion.off("error");
        connexion.disconnect();
    }

    public void finishing() {
        // pour ne pas être sur le thread de SocketIO
        new Thread(() -> {
                connexion.off("disconnect");
                connexion.close();
                // hack pour arrêter plus vite (sinon attente de plusieurs secondes
                System.exit(0);
            }).start() ;
    }
}
