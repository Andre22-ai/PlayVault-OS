package com.progetto;

import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * JavaFX App - PLAYVAULT Main Entry Point
 */
public class App extends Application {

    private static Scene scene;

    @SuppressWarnings("java:s2696")
    @Override
    public void start(Stage stage) throws IOException {
        // All'avvio carichiamo la schermata di login
        // Dimensioni impostate a 1000x700 come abbiamo progettato i file FXML
        scene = new Scene(loadFXML("login"), 1000, 600);
        stage.setScene(scene);
        stage.setTitle("PLAYVAULT - Arcade Game Vault");
        stage.setResizable(false); // Impedisce di ridimensionare la finestra per non rovinare il layout neon
        stage.show();
    }

    /**
     * Metodo fondamentale: Cambia la schermata attuale con una nuova.
     * Viene chiamato dai Controller. Es: App.setRoot("dashboard");
     */
    static void setRoot(String fxml) throws IOException {
        scene.setRoot(loadFXML(fxml));
    }

    /**
     * Carica il file FXML dalla cartella resources
     */
    private static Parent loadFXML(String fxml) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource(fxml + ".fxml"));
        return fxmlLoader.load();
    }

    public static void main(String[] args) {
        launch();
    }

}