package com.progetto.utils;

import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;

public class BadgeUtils {

    // --- FIX S1118: Costruttore privato per impedire l'istanza della classe ---
    private BadgeUtils() {
        throw new IllegalStateException("Classe di utilità: non può essere istanziata");
    }

    // Metodo statico che fa tutto il lavoro "sporco"
    public static HBox generaBadgeGeneri(String genere) {
        HBox contenitoreIcone = new HBox(15);
        contenitoreIcone.setAlignment(Pos.CENTER);
        
        String g = genere != null ? genere.toUpperCase() : "";
        boolean genereTrovato = false;
        
        if (g.contains("ACTION") || g.contains("AZIONE")) { contenitoreIcone.getChildren().add(creaMiniIcona("⚔️", "cover-action")); genereTrovato = true; }
        if (g.contains("RPG") || g.contains("GDR") || g.contains("FANTASY")) { contenitoreIcone.getChildren().add(creaMiniIcona("🔮", "cover-rpg")); genereTrovato = true; }
        if (g.contains("SPORT")) { contenitoreIcone.getChildren().add(creaMiniIcona("⚽", "cover-sports")); genereTrovato = true; }
        if (g.contains("SHOOTER") || g.contains("SPARATUTTO")) { contenitoreIcone.getChildren().add(creaMiniIcona("🎯", "cover-shooter")); genereTrovato = true; }
        if (g.contains("RACING") || g.contains("CORSE")) { contenitoreIcone.getChildren().add(creaMiniIcona("🏎️", "cover-racing")); genereTrovato = true; }
        if (g.contains("STRATEGY") || g.contains("STRATEGIA")) { contenitoreIcone.getChildren().add(creaMiniIcona("♟️", "cover-strategy")); genereTrovato = true; }
        if (g.contains("HORROR")) { contenitoreIcone.getChildren().add(creaMiniIcona("💀", "cover-horror")); genereTrovato = true; }
        
        if (!genereTrovato) {
            contenitoreIcone.getChildren().add(creaMiniIcona("🎮", "cover-default"));
        }
        
        return contenitoreIcone;
    }

    private static Label creaMiniIcona(String icona, String classeCss) {
        Label miniLabel = new Label(icona);
        miniLabel.getStyleClass().addAll("cover-base", classeCss);
        miniLabel.setPrefSize(70, 70);
        miniLabel.setMinSize(70, 70);
        miniLabel.setMaxSize(70, 70);
        miniLabel.setStyle("-fx-font-size: 28px; -fx-padding: 0;"); 
        miniLabel.setTextOverrun(javafx.scene.control.OverrunStyle.CLIP);
        miniLabel.setAlignment(Pos.CENTER);
        return miniLabel;
    }
}