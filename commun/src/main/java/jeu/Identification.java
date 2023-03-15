package jeu;

import java.util.Objects;

public class Identification {

    private String nom ;
    private int niveau;

    private String description ;

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Identification() {}

    public Identification(String nom, int level) {
       this(nom, level, "bot par defaut");
    }

    public Identification(String nom, int level, String description) {
        this.nom = nom;
        niveau = level;
        this.description = description;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public int getNiveau() {
        return niveau;
    }

    public void setNiveau(int niveau) {
        this.niveau = niveau;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Identification id) {
            if (id.getNom() != null) {
                if (id.getDescription() != null) {
                    return id.getNom().equals(getNom()) && id.getDescription().equals(getDescription()) && (id.getNiveau() == getNiveau());
                }
                else {
                    return id.getNom().equals(getNom()) && (getDescription() == null) && (id.getNiveau() == getNiveau());
                }
            } else {
                if (id.getDescription() != null) {
                    return (getNom() == null) && id.getDescription().equals(getDescription()) && (id.getNiveau() == getNiveau());
                }
                else {
                    return (getNom() == null) && (getDescription() == null) && (id.getNiveau() == getNiveau());
                }
            }
        }
        else return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(nom, niveau, description);
    }
}
