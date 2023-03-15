package jeu.serveur;

import jeu.Identification;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.*;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

class StatsTest {

    @TempDir
    File temp;
    String testFileName = "tmpFileTest.csv";

    Stats stats;
    Identification joueur;
    int cible = 75;

    private String ligne1 = "description_du_joueur;nombre_à_trouver;proposition_initial;nombre_de_coup;trouvé";

    @BeforeEach
    void setUp() {
        joueur = new Identification("Jack", 85, "bon joueur");
        stats = new Stats(joueur, cible);
    }

    @AfterEach
    void tearDown() {
        File testFile = new File(temp, testFileName);
        if (testFile.exists()) testFile.delete();
    }

    @Test
    void testToString() {
        // toString initial
        String expected = joueur.getDescription() + ";" + cible + ";-1;0;false";
        assertEquals(expected, stats.toString());

        // toString après une proposition initial
        stats.setPropositionInitiale(12);
        expected = joueur.getDescription() + ";" + cible + ";12;0;false";
        assertEquals(expected, stats.toString());

        // toString après incrémenter le nombre de coup
        for (int i = 1; i <= 10; i++) {
            stats.incrementeNbCoup();
            expected = joueur.getDescription() + ";" + cible + ";12;" + i + ";false";
            assertEquals(expected, stats.toString());
        }

        // on change le nombre à trouver
        stats.setNombreAtrouver(57);
        expected = joueur.getDescription() + ";57;12;10;false";
        assertEquals(expected, stats.toString());

        // on passe à trouver
        stats.setTrouve(true);
        expected = joueur.getDescription() + ";57;12;10;true";
        assertEquals(expected, stats.toString());

        // différent cas avec la description et la suppression des ; et des retours à la ligne
        stats.getJoueur().setDescription("a;b;c;d;e");
        expected = "a,b,c,d,e" + ";57;12;10;true";
        assertEquals(expected, stats.toString());

        stats.getJoueur().setDescription("\n;a;\nb;\nc;\nd;\ne;\nf\n");
        expected = ",a,b,c,d,e,f" + ";57;12;10;true";
        assertEquals(expected, stats.toString());

        stats.getJoueur().setDescription(";\n\n\n;\n;\n;\n;\n;\n;\n;\n\n\n");
        expected = ",,,,,,,," + ";57;12;10;true";
        assertEquals(expected, stats.toString());

        stats.getJoueur().setDescription(";");
        expected = "," + ";57;12;10;true";
        assertEquals(expected, stats.toString());

        stats.getJoueur().setDescription("\n");
        expected = ";57;12;10;true";
        assertEquals(expected, stats.toString());
    }

    @Test
    void incrementeNbCoup() {
        assertEquals(0, stats.getNbCoup());

        for (int i = 1; i <= 10; i++) {
            stats.incrementeNbCoup();
            assertEquals(i, stats.getNbCoup());
        }
    }



    // pour éviter la duplication de code
    private void verifContenu(String[] lignes, File testFile) {
        try {
            stats.export(testFile.getAbsolutePath());
        } catch (IOException e) {
            fail("exception IOException à l'export");
        }

        try {
            BufferedReader reader = new BufferedReader(new FileReader(testFile, StandardCharsets.UTF_8));
            for(String ligneAttendue : lignes) {
                String ligneLue = reader.readLine();
                assertEquals(ligneAttendue, ligneLue);
            }
            assertFalse(reader.ready());
            reader.close();
        } catch (FileNotFoundException e) {
            fail("exception FileNotFoundException");
        } catch (IOException e) {
            fail("exception IOException à la lecture pour verification");
        }
    }

    @Test
    void exportStatsInitiales() {
        File testFile = new File(temp, testFileName);
        assertFalse(testFile.exists());

       // ecriture "initiale"
       // chemin qui ressemble à C:\Users\<user>\AppData\Local\Temp\junit<un time stamp>\tmpFileTest.csv sur windows

        String [] lignesAttendues = {ligne1, stats.toString()};
        verifContenu(lignesAttendues, testFile);
    }


    @Test
    void exportStatsAvecIncrement() {
        String ligne2 = stats.toString();
        exportStatsInitiales(); // pour refaire le fichier

        File testFile = new File(temp, testFileName);
        assertTrue(testFile.exists());

        stats.setPropositionInitiale(12);
        String ligne3 = stats.toString();
        verifContenu(new String[]{ligne1, ligne2, ligne3}, testFile);

        stats.getJoueur().setDescription("a;b;\nc;d;\ne\n");
        String ligne4 = stats.toString();
        verifContenu(new String[]{ligne1, ligne2, ligne3, ligne4}, testFile);
    }
}