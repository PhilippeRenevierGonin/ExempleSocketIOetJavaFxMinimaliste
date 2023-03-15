package jeu.serveur.ihm;

import jeu.Identification;

import java.util.logging.Level;
import java.util.logging.Logger;

public class Vue {

    private static final String PROMPT = "server > ";
    private final Logger logger;

    public Vue(Logger logger) {
        this.logger = logger;
    }
    public void fin() {
        logger.log(Level.INFO, () -> PROMPT+"le client a trouvé ! ");
    }

    public void pasEncore() {
        logger.log(Level.INFO, () -> PROMPT+"le client doit encore cherché ");
    }

    public void accueilClient(Identification leClient) {
        logger.log(Level.INFO, () -> PROMPT+"Le client est " + leClient.getNom());
    }

    public void plusDePlace(Identification identification) {
        logger.log(Level.INFO, () -> PROMPT+"ce serveur n'est fait que pour un client, il n'y a plus de place pour "+identification);
    }

    public void afficheReponse(Identification leClient, Integer reponse) {
        logger.log(Level.INFO, () -> PROMPT+"La réponse de  " + leClient.getNom() + " est " + reponse);
    }

    public void afficheErreur(String erreur) {
        logger.log(Level.SEVERE, () -> PROMPT+erreur);
    }

    public void afficheMessage(String msg) {
        logger.log(Level.INFO, () -> PROMPT+msg);
    }
}
