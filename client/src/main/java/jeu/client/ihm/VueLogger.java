package jeu.client.ihm;

import jeu.client.Joueur;

import java.util.logging.Level;
import java.util.logging.Logger;

public class VueLogger implements Vue {


    private final Joueur client;
    private final Logger logger;

    public VueLogger(Joueur client, Logger logger) {
            this.client = client;
            this.logger = logger;
            client.setVue(this);
        }


        public void afficheMessage(String msg) {
            logger.log(Level.INFO, () -> client.getIdentification().getNom()+"> "+msg);

        }


        public void finit() {
            logger.log(Level.INFO,() -> client.getIdentification().getNom()+"> j'ai gagné !");
        }

        public void afficheMessageErreur(String s) {
            logger.log(Level.SEVERE,() -> client.getIdentification().getNom()+"> "+s);
        }
}
