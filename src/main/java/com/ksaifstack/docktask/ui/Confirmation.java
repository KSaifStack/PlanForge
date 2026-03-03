package com.ksaifstack.docktask.ui;

import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;

/**
 *This class will open panes to help the user pick actions within the ui.
 *
 **/
public class Confirmation {


    /**
     * {@code check}
     *  This functions allows the user to check if they agree to the selected action or not.
     * @return Pane
     */
    public Pane check(String question, String note, Font font, Runnable onConfirm) {

        Pane pane = new Pane();
        pane.setPrefSize(980, 493);
        pane.setStyle("-fx-background-color: rgba(0, 0, 0, 0.15);");

        // Background dialog box
        Pane background = new StackPane();
        background.setLayoutX(320);
        background.setLayoutY(130);
        background.setPrefSize(310, 176);
        background.getStyleClass().add("region");
        pane.getChildren().add(background);

        // Question label
        Label questionLabel = new Label("Are you sure you would like to " + question + "?");
        questionLabel.setAlignment(Pos.TOP_CENTER);
        questionLabel.setTranslateY(-15);
        questionLabel.setFont(font);
        questionLabel.setPrefSize(425, 95);
        background.getChildren().add(questionLabel);

        // Optional note/context
        String checkedNote=note;
        if(note!=null && note.length()>=40){
            checkedNote= note.substring(0,40)+"...";
        }
        Label noteLabel = new Label("'" + checkedNote + "'");
        noteLabel.setAlignment(Pos.TOP_CENTER);
        noteLabel.setTranslateY(-30);
        noteLabel.setFont(Font.loadFont(getClass().getResourceAsStream("/fonts/Lexend-Italic.ttf"), 12));
        noteLabel.setVisible(note != null && !note.isEmpty());
        noteLabel.setWrapText(true);
        noteLabel.setMaxWidth(300);
        background.getChildren().add(noteLabel);

        //Holds the yes and no buttons in place.
        HBox Options= new HBox(60);
        Options.setAlignment(Pos.BOTTOM_CENTER);
        Options.setTranslateY(-15);
        background.getChildren().add(Options);
        // Yes button
        Button yesButton = new Button("Yes");
        yesButton.setFont(font);
        yesButton.setPrefSize(99, 68);
        yesButton.setOnAction(e -> {
            pane.setVisible(false);
            if (onConfirm != null) {
                onConfirm.run();
            }
        });

        // No button
        Button noButton = new Button("No");
        noButton.setFont(font);
        noButton.setPrefSize(99, 68);
        noButton.setOnAction(e -> pane.setVisible(false));

        Options.getChildren().addAll(yesButton, noButton);

        return pane;
    }
}