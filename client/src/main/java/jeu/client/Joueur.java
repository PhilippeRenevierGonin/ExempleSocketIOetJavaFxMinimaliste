package jeu.client;


import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.logging.Logger;

import jeu.Coup;
import jeu.Identification;
import jeu.client.connexion.ConnexionClient;
import jeu.client.connexion.ConnexionClientIO;
import jeu.client.ia.IA;
import jeu.client.ia.IADichotomie;
import jeu.client.ihm.Vue;
import jeu.client.ihm.VueLogger;
import jeu.outils.GenerateurDeNombre;

public class Joueur {

    Identification moi = new Identification("Michel B", 42);

    ConnexionClient connexion;
    int propositionCourante = 30;

    // Objet de synchro
    private Vue vue;
    private IA ia;

    public Joueur(IA ia) {
        setIa(ia);
    }


    /** un ensemble de getter et setter **/
    public void setConnexion(ConnexionClient connexion) {
        this.connexion = connexion;
    }

    private ConnexionClient getConnexion() {
        return connexion;
    }

    public int getPropositionCourante() {
        return propositionCourante;
    }

    public void setPropositionCourante(int propositionCourante) {
        this.propositionCourante = propositionCourante;
    }

    public Identification getIdentification() {
        return moi;
    }

    public void setVue(Vue vue) {
        this.vue = vue;
    }

    public Vue getVue() {
        return vue;
    }

    public void seConnecter() {
        // on se connecte
        this.connexion.seConnecter();

        getVue().afficheMessage("en attente de déconnexion");

    }







    public void apresConnexion() {
        getVue().afficheMessage("on est connecté ! et on s'identifie");
        this.connexion.envoyerId(moi);
    }

    public void finPartie() {
        getVue().finit();

        getConnexion().stop();
        getConnexion().finishing();
    }


    public void rejouer(boolean plusGrand, List<Coup> coups) {
        getVue().afficheMessage("la réponse précédente était : "+(plusGrand?"trop grande":"trop petite"));
        propositionCourante = ia.choisirNombre(plusGrand, coups);
        getVue().afficheMessage("on répond "+propositionCourante);
        getConnexion().envoyerCoup(propositionCourante);
    }



    public void premierCoup() {
        // au premier coup, on envoie le nombre initial
        propositionCourante = ia.choisirPremierNombre();
        connexion.envoyerCoup(propositionCourante);
    }


    public void transfereMessage(String s) {
        getVue().afficheMessage(s);
    }
    public void transfereErreur(String e) {
        getVue().afficheMessageErreur(e);
    }
    public void setIa(IA ia) {
        this.ia = ia;
    }

    public IA getIa() {
        return ia;
    }


    public static final void main(String []args) throws UnsupportedEncodingException {
        Logger logger = Logger.getLogger("jeu.client");
        // changement d'encoding du handler par défaut, qui est d'écrire dans la console.
        // le handler par défaut est attaché au logger "racine", qui est le parent de tout les loggers, y compris celui-ci-dessus
        // doc sur logging : https://docs.oracle.com/en/java/javase/17/docs/api/java.logging/java/util/logging/package-summary.html
        logger.getParent().getHandlers()[0].setEncoding("UTF-8");

        logger.info("début du main pour le client");

        String serveurIp = "127.0.0.1";
        if (args.length > 0) serveurIp = args[0];

        GenerateurDeNombre alea = new GenerateurDeNombre();
        IA ia = new IADichotomie(alea);
        Joueur client = new Joueur(ia);
        client.getIdentification().setDescription(ia.getClass().getCanonicalName());
        new VueLogger(client, logger); // l'objet vue appelle setVue du joueur
        final String serveurUrl = "http://"+serveurIp+":10101";
        logger.info(() -> "config du serveur "+serveurUrl);
        new ConnexionClientIO(serveurUrl, client); // l'objet connexion appelle setConnexion du joueur
        client.seConnecter();

        logger.info("fin du main pour le client");
    }


}
