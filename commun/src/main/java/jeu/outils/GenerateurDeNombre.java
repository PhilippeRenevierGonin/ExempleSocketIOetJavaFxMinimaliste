package jeu.outils;

import java.security.SecureRandom;
import java.util.Random;

public class GenerateurDeNombre {

    private Random alea = new SecureRandom(); // changement de type de random + modification de la visibilité des getters / setters

    Random getAlea() {
        return alea;
    }

    void setAlea(Random alea) {
        this.alea = alea;
    }

    /**
     * méthode pour illustrer le mock et when / thenReturn
     * @param min
     * @param max
     * @return
     * @throws IllegalArgumentException si max <= min
     */
    public int generate(int min, int max) throws IllegalArgumentException {
        return min+alea.nextInt(max-min);
    }

}
