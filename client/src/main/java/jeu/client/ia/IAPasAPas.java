package jeu.client.ia;

import jeu.Coup;
import jeu.outils.GenerateurDeNombre;
import java.util.List;

public class IAPasAPas implements IA {
    private GenerateurDeNombre generateurDeNombre ;

    public IAPasAPas(GenerateurDeNombre alea) {
        generateurDeNombre = alea;
    }

    @Override
    public int choisirPremierNombre() {
        return nombreBorne( generateurDeNombre.generate(0,100));
    }

    @Override
    public int choisirNombre(boolean plusGrand, List<Coup> coups) {
        int pas = 1;

        if (plusGrand)  pas=-1;
        else pas=+1;

        if ((coups != null) && !coups.isEmpty())
            return nombreBorne(coups.get(coups.size()-1).getCoupJoue()+pas);
        else
            return nombreBorne(choisirPremierNombre());
    }

    protected int nombreBorne(int nb) {
        return Math.min(Math.max(nb, 0), 100);
    }
}
