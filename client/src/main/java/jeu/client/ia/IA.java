package jeu.client.ia;

import jeu.Coup;
import java.util.List;

public interface IA {
    int choisirPremierNombre();
    int choisirNombre(boolean plusGrand, List<Coup> coups);
}
