package jeu.serveur;

import jeu.Identification;

import java.io.*;
import java.nio.charset.StandardCharsets;

public class Stats {

    private Identification joueur;
    private Integer nombreAtrouver;
    private int propositionInitiale;
    private int nbCoup;
    private boolean trouve;

    public Stats(Identification joueur, Integer cible) {
        setJoueur(joueur);
        setNombreAtrouver(cible);
        setNbCoup(0);
        setPropositionInitiale(-1);
        setTrouve(false);
    }

    public String toString() {
        return getJoueur().getDescription().replace(";", ",").replace("\n", "")+";"+getNombreAtrouver()+";"+getPropositionInitiale()+";"+getNbCoup()+";"+ isTrouve();
    }

    public void incrementeNbCoup() {
        setNbCoup(getNbCoup()+1);
    }

    public void setJoueur(Identification joueur) {
        this.joueur = joueur;
    }

    public Identification getJoueur() {
        return joueur;
    }

    public void setNombreAtrouver(Integer nombreAtrouver) {
        this.nombreAtrouver = nombreAtrouver;
    }

    public Integer getNombreAtrouver() {
        return nombreAtrouver;
    }

    public void setPropositionInitiale(int propositionInitiale) {
        this.propositionInitiale = propositionInitiale;
    }

    public int getPropositionInitiale() {
        return propositionInitiale;
    }

    public void setNbCoup(int nbCoup) {
        this.nbCoup = nbCoup;
    }

    public int getNbCoup() {
        return nbCoup;
    }

    public void setTrouve(boolean trouve) {
        this.trouve = trouve;
    }

    public boolean isTrouve() {
        return trouve;
    }

    public void export(String nomFichier) throws IOException {
        String aExporter = "";
        File fichier = new File(nomFichier);
        if (! fichier.exists()) {
            aExporter = "description_du_joueur;nombre_à_trouver;proposition_initial;nombre_de_coup;trouvé\n";
        }
        aExporter = aExporter+this+"\n";

        FileOutputStream fos = new FileOutputStream(fichier, true);
        Writer writer = new OutputStreamWriter(fos, StandardCharsets.UTF_8);
        try {
            writer.append(aExporter);
        }
        finally {
            writer.close();
            fos.close();
        }

    }
}
