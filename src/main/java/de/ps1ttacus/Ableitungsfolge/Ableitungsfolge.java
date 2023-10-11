package de.ps1ttacus.Ableitungsfolge;

import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.text.Font;
import javafx.scene.text.TextFlow;

import java.util.ArrayList;

public class Ableitungsfolge {
    ArrayList<String> ableitungen;
    HBox hbox;
    public Ableitungsfolge(ArrayList<String> ableitungen, HBox hbox) {
        this.ableitungen = ableitungen;
        this.hbox = hbox;
    }

    public void generateAbleitungsfolge() {
        //remove all children so it's clean again
        hbox.getChildren().clear();
        for(int i = 0; i < ableitungen.size(); i++) {
            Label label = new Label(ableitungen.get(i));
            label.setFont(new Font("System", 20));
            label.setMinWidth(Region.USE_PREF_SIZE);
            hbox.getChildren().add(label);
            if(i + 1 < ableitungen.size()) {
                Label labelArrow = new Label("=>");
                labelArrow.setFont(new Font("System", 20));
                labelArrow.setMinWidth(Region.USE_PREF_SIZE);
                hbox.getChildren().add(labelArrow);
            }
        }
    }

}
