package jeu.client.connexion;


import jeu.Identification;


public interface ConnexionClient {
    public void seConnecter() ;

    public void envoyerId(Identification moi)  ;

    public void envoyerCoup(int val)  ;

    public void stop()  ;

    public void finishing()  ;

}
