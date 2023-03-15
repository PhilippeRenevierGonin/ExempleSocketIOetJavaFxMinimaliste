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
import jeu.client.ihm.VueGraphique;
import jeu.outils.GenerateurDeNombre;

public class JoueurHumain extends Joueur {



    public JoueurHumain() {
        super(null);
    }


    @Override
    public void rejouer(boolean plusGrand, List<Coup> coups) {
        getVue().afficheMessage("c'était trop grand ? "+plusGrand+" \n"+coups);

    }


    public void premierCoup() {
        getVue().afficheMessage("choissiez le premier coup");
    }


    public void demarrerConnexion() {
        String serveurIp = "127.0.0.1";
        final String serveurUrl = "http://"+serveurIp+":10101";
        new ConnexionClientIO(serveurUrl, this); // l'objet connexion appelle setConnexion du joueur
        seConnecter();
    }

    public void joueurHumain(String text) {
        int propositionCourante = Integer.parseInt(text);
        connexion.envoyerCoup(propositionCourante);
    }


}
