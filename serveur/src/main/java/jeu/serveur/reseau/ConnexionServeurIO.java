package jeu.serveur.reseau;

import com.corundumstudio.socketio.AckRequest;
import com.corundumstudio.socketio.Configuration;
import com.corundumstudio.socketio.SocketIOClient;
import com.corundumstudio.socketio.SocketIOServer;
import com.corundumstudio.socketio.listener.ConnectListener;
import com.corundumstudio.socketio.listener.DataListener;
import jeu.Identification;
import jeu.Protocole;
import jeu.serveur.TrouverLeNombre;

import java.util.HashMap;
import java.util.concurrent.TimeUnit;

public class ConnexionServeurIO implements ConnexionServeur{

    SocketIOServer serveur;
    TrouverLeNombre moteur;
    private HashMap<Identification, SocketIOClient> map;

    SocketIOServer getServeur() {
        return serveur;
    }

    void setServeur(SocketIOServer serveur) {
        this.serveur = serveur;
    }


    ConnexionServeurIO(SocketIOServer server) {
        map = new HashMap<>();
        setServeur(server);
        sAbonner();
    }

    public ConnexionServeurIO(String ip, int port) {
        map = new HashMap<>();
        Configuration config = new Configuration();
        config.setHostname(ip);
        config.setPort(port);

        setServeur(new SocketIOServer(config));
        sAbonner();
    }


    private void  sAbonner() {
        System.out.println("on s'abonne");
        serveur.addConnectListener(new ConnectListener() {
            @Override
            public void onConnect(SocketIOClient socketIOClient) {
                System.out.println("connexion");
            }
        });
        // réception d'une identification
        serveur.addEventListener(Protocole.IDENTIFICATION, Identification.class, new DataListener<Identification>() {
            @Override
            public void onData(SocketIOClient socketIOClient, Identification identification, AckRequest ackRequest) throws Exception {
                traitementIdentification(socketIOClient, identification);
            }
        });


        // on attend une réponse
        serveur.addEventListener(Protocole.REPONSE, int.class, new DataListener<Integer>() {
            @Override
            public void onData(SocketIOClient socketIOClient, Integer integer, AckRequest ackRequest) throws Exception {
                traitementReponse(integer);
            }
        });
    }


    void traitementIdentification(SocketIOClient socketIOClient, Identification identification) {
        moteur.transfereMessage("connexion de "+socketIOClient.getRemoteAddress());
        Identification accepte = moteur.nouveauJoueur(identification);
        if (accepte == null) {
            // il n'est pas accepté on disconnecte...
            // bricolage car code initialement fait pour 1 - 1 , il faudrait revoir le protocole.
            new Thread(socketIOClient::disconnect).start();
        } else {
            associer(accepte, socketIOClient);
        }
    }

    void traitementReponse(int reponse) {
        moteur.recoitReponse(reponse);
    }


    public TrouverLeNombre getMoteur() {
        return moteur;
    }

    public void setMoteur(TrouverLeNombre moteur) {
        this.moteur = moteur;
    }

    public void demarrer() {
        getServeur().start();
    }

    public void envoyerMessage(Identification leClient, String question) {
        map.get(leClient).sendEvent(question);
    }

    public void associer(Identification leClient, SocketIOClient socketIOClient) {
        map.put(leClient,socketIOClient);
    }

    public void envoyerMessage(Identification leClient, String question, Object... attachement) {
        map.get(leClient).sendEvent(question, attachement);
    }

    public void arreter() {
        moteur.transfereMessage("fin du serveur - début");
        for(SocketIOClient c : map.values()) c.disconnect();

        new Thread(() -> {
                try {
                    TimeUnit.MILLISECONDS.sleep(3000); // attente de seconde pour laisser le temps aux autres de se connecter.... pour la démo du scale
                } catch (InterruptedException e) {
                    // ici il n'y a rien à faire de spécial... on fait ce que sonarq indique
                    Thread.currentThread().interrupt();
                }
                finally {
                    moteur.transfereMessage("fin du serveur - stop");

                    moteur.transfereMessage("fin du serveur - désabonnement");

                    getServeur().removeAllListeners("réponse");
                    getServeur().removeAllListeners("identification");

                    getServeur().stop(); // à faire sur un autre thread que sur le thread de SocketIO
                }
            moteur.transfereMessage("fin du serveur - fin");
        }).start();

    }
}