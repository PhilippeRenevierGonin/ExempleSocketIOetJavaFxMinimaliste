package jeu.client.ihm;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import jeu.client.Joueur;
import jeu.client.JoueurHumain;

import java.util.logging.Level;
import java.util.logging.Logger;

public class VueGraphique extends Application implements Vue {

    private  JoueurHumain client;
    private  Logger logger;

    private TextArea messages = new TextArea();
    private TextField saisie = new TextField();



    public void afficheMessage(String msg) {
        logger.log(Level.INFO, () -> client.getIdentification().getNom()+"> "+msg);
        messages.setText(msg);

    }


    public void finit() {
        logger.log(Level.INFO,() -> client.getIdentification().getNom()+"> j'ai gagné !");
        messages.setText("fini, on a trouvé");

        // Platform.exit(); // fin brutale ailleurs
    }

    public void afficheMessageErreur(String s) {
        logger.log(Level.SEVERE,() -> client.getIdentification().getNom()+"> "+s);
        messages.setText(s);

    }



    public static void lancement(String[] args) {
        VueGraphique.launch(args);

    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        primaryStage.setTitle("Trouver le Nombre - client");
        primaryStage.setWidth(400);
        primaryStage.setHeight(200);
        BorderPane primaryPane = new BorderPane();
        Scene primaryScene = new Scene(primaryPane);
        primaryStage.setScene(primaryScene);



        logger = Logger.getLogger("jeu.client");
        logger.getParent().getHandlers()[0].setEncoding("UTF-8");

        logger.info("début du main pour le client");



        primaryPane.setCenter(messages);
        primaryPane.setBottom(saisie);


        saisie.setOnAction( (e) -> client.joueurHumain(saisie.getText()));

        // l'ihm a finit son init
        client = new JoueurHumain();
        client.setVue(this);
        client.demarrerConnexion();


        primaryStage.show();

    }
}
