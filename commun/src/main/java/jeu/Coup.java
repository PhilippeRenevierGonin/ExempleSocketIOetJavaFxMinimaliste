package jeu;

import java.util.Objects;

public class Coup {
    private int coupJoue;
    private boolean plusGrand;

    public int getCoupJoue() {
        return coupJoue;
    }

    public void setCoupJoue(int coupJoue) {
        this.coupJoue = coupJoue;
    }

    public boolean isPlusGrand() {
        return plusGrand;
    }

    public void setPlusGrand(boolean plusGrand) {
        this.plusGrand = plusGrand;
    }

    /**
     *  constructeur par défaut pour le mapping JSOn - Objet java
     */
    public Coup() {}

    /**
     * constructeur pour créer un coup joué avec la réponse (si ce n'est pas la bonne)
     * @param val la valeur qui a été proposée par le.a joueur.se
     * @param sup indique si la valeur est plus grande que le nombre recherché
     */
    public Coup(int val, boolean sup) {
        setCoupJoue(val);
        setPlusGrand(sup);
    }

    @Override
    public String toString() {
        return ""+ getCoupJoue()+"/"+(isPlusGrand()?">":"<");
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (! (o instanceof Coup)) return false;
        Coup coup1 = (Coup) o;
        return coupJoue == coup1.coupJoue && plusGrand == coup1.plusGrand;
    }

    @Override
    public int hashCode() {
        return Objects.hash(coupJoue, plusGrand);
    }
}
