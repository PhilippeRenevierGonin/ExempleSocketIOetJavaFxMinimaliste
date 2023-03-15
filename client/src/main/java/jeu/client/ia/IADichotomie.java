package jeu.client.ia;

import jeu.Coup;
import jeu.outils.GenerateurDeNombre;

import java.util.List;

public class IADichotomie extends IAPasAPas {

    class Bornes {
        int borneMax = 100;
        int borneMin = 0;
    }

    public IADichotomie(GenerateurDeNombre alea) {
        super(alea);
    }

    @Override
    public int choisirNombre(boolean plusGrand, List<Coup> coups) {
        if ((coups == null) || coups.isEmpty()) {
            return nombreBorne(choisirPremierNombre());
        } else {
            Bornes bornes = new Bornes();

            int tailleCoups = coups.size();
            if (plusGrand) {
                bornes.borneMax = coups.get(tailleCoups - 1).getCoupJoue();
            } else {
                bornes.borneMin = coups.get(tailleCoups - 1).getCoupJoue();
            }

            calculerBornes(coups, bornes, plusGrand);

            int reponse = nombreBorne((bornes.borneMin + bornes.borneMax) / 2);
            if (reponse == coups.get(tailleCoups - 1).getCoupJoue()) reponse += 1;
            return nombreBorne(reponse);
        }
    }

    private void calculerBornes(List<Coup> coups, Bornes bornes, boolean plusGrand) {
        int tailleCoups = coups.size();

        if (tailleCoups >= 2) {
            int i = tailleCoups - 2;
            while ((i >= 0) && (coups.get(i).isPlusGrand() == plusGrand) ) {
                i--;
            }

            if (i >= 0) {
                if (plusGrand) bornes.borneMin = coups.get(i).getCoupJoue();
                else bornes.borneMax = coups.get(i).getCoupJoue();
            }
        }

    }
}
