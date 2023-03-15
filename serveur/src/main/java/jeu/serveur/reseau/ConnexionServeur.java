package jeu.serveur.reseau;


import jeu.Identification;
import jeu.serveur.TrouverLeNombre;

public interface ConnexionServeur {

    public TrouverLeNombre getMoteur() ;

    public void setMoteur(TrouverLeNombre moteur) ;

    public void demarrer()  ;

    public void envoyerMessage(Identification leClient, String question)  ;

    public void envoyerMessage(Identification leClient, String question, Object... attachement)  ;

    public void arreter() ;
}