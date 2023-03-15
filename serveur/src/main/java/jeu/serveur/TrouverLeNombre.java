package jeu.serveur;


import jeu.Coup;
import jeu.Identification;
import jeu.Protocole;
import jeu.outils.GenerateurDeNombre;
import jeu.serveur.ihm.Vue;
import jeu.serveur.reseau.ConnexionServeur;
import jeu.serveur.reseau.ConnexionServeurIO;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.logging.Logger;

/**
 * attend une connexion, on envoie une question puis on attend une réponse, jusqu'à la découverte de la bonne réponse
 * le client s'identifie (som, niveau)
 */

public class TrouverLeNombre {

    private int aDecouvrir = 42;
    Identification leClient;

    ArrayList<Coup> coups = new ArrayList<>();

    ConnexionServeur connexion;
    private Vue vue;

    private Stats stats;

    public ConnexionServeur getConnexion() {
        return connexion;
    }

    public void setConnexion(ConnexionServeur connexion) {
        this.connexion = connexion;
    }

    // par exemple...
    public TrouverLeNombre() {
        this(new GenerateurDeNombre().generate(0, 100));
    }

    public TrouverLeNombre(int valeurVoulue) {
        this.aDecouvrir = valeurVoulue;
    }

    public void setLeClient(Identification leClient) {
        this.leClient = leClient;
    }

    public Identification getLeClient() {
        return leClient;
    }


    public void demarrer() {

        connexion.demarrer();

    }


    private void poserUneQuestion() {
        getConnexion().envoyerMessage(leClient, Protocole.QUESTION);
    }

    private void poserUneQuestion(boolean plusGrand) {
        getConnexion().envoyerMessage(leClient, Protocole.QUESTION, plusGrand, coups);
    }


    public static final void main(String[] args) throws UnsupportedEncodingException, UnknownHostException {

        // logger
        Logger logger = Logger.getLogger("jeu.serveur");
        logger.getParent().getHandlers()[0].setEncoding("UTF-8");

        final String ip = InetAddress.getLocalHost().getHostAddress();

        System.out.println(ip);

        String adresseIPLocale = "127.0.0.1";
        if ((args.length == 2) && args[0].equals("-linux")) adresseIPLocale=args[1];
        else if (args.length == 1)  adresseIPLocale=ip;

        TrouverLeNombre serveur = new TrouverLeNombre();
        ConnexionServeur connexion = new ConnexionServeurIO(adresseIPLocale, 10101);
        connexion.setMoteur(serveur);
        serveur.setConnexion(connexion);



        Vue v = new Vue(logger);
        serveur.setVue(v);

        serveur.demarrer();


    }




    public synchronized Identification nouveauJoueur(Identification identification) {
        if (leClient == null) {
            leClient = new Identification(identification.getNom(), identification.getNiveau());
            leClient.setDescription(identification.getDescription());
            vue.accueilClient(leClient);
            stats = new Stats(leClient, aDecouvrir);
            // on enchaine sur une question
            new Thread(this::poserUneQuestion).start();
            return leClient;
        } else {
            vue.plusDePlace(identification);
            return null;
        }
    }

    public void recoitReponse(Integer integer) {
        vue.afficheReponse(leClient, integer);
        if (stats != null) {
            if (stats.getNbCoup() == 0) {
                stats.setPropositionInitiale(integer);
            }
            stats.incrementeNbCoup();
        }

        if (integer == aDecouvrir) {
            vue.fin();
            try {
                if (stats != null)  {
                    stats.setTrouve(true);
                    stats.export("./stats.csv");
                }
            } catch (IOException e) {
                vue.afficheErreur("plantage fichier");
            }
            // fin brutale
            getConnexion().arreter();

        } else {
            Coup coup = new Coup(integer, integer > aDecouvrir);
            coups.add(coup);
            vue.pasEncore();
            poserUneQuestion(coup.isPlusGrand());
        }
    }

    public void setVue(Vue vue) {
        this.vue = vue;
    }

    public Vue getVue() {
        return vue;
    }

    public void transfereMessage(String s) {
        getVue().afficheMessage(s);
    }

}
