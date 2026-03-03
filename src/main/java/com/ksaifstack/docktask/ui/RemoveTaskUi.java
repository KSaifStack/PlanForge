// UI for Removing tasks
package com.ksaifstack.docktask.ui;
import com.ksaifstack.docktask.model.UserData;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;

import java.io.InputStream;

public class RemoveTaskUi {
    private final String username;
    private final String taskname;
    private final Runnable onUpdate;
    Font lexend14 = Font.loadFont(getClass().getResourceAsStream("/fonts/Lexend.ttf"), 14);
    public RemoveTaskUi(String username, String taskname,Runnable onUpdate) {
        this.username = username;
        this.taskname = taskname;
        this.onUpdate = onUpdate;
    }
    public Pane getContent(){
        Pane pane = new Pane();
        pane.setPrefSize(980, 493);
        pane.setStyle("-fx-background-color: rgba(0, 0, 0, 0.15);");
        InputStream fontStream = getClass().getResourceAsStream("/fonts/Lato.ttf");

        Region background = new Region();
        background.setLayoutX(150);
        background.setLayoutY(155);
        background.setPrefSize(299, 176);
        background.getStyleClass().add("region");
        pane.getChildren().add(background);

        Label Label = new Label("Are you sure you would like to remove:");
        Label.setAlignment(Pos.CENTER);
        Label.setFont(lexend14);
        Label.setPrefSize(425,85);
        Label.setLayoutX(85);
        Label.setLayoutY(130);
        pane.getChildren().add(Label);

        Label Name = new Label("'"+taskname+"'");
        Name.setAlignment(Pos.CENTER);
        Name.setFont(lexend14);
        Name.setPrefSize(425,85);
        Name.setLayoutX(85);
        Name.setLayoutY(150);
        pane.getChildren().add(Name);


        Button Yes = new Button("Yes");
        Yes.setLayoutX(173);
        Yes.setLayoutY(230);
        Yes.setFont(lexend14);
        Yes.setPrefSize(99,78);

        Yes.setOnAction(e-> {
            UserData.removeTask(username, taskname);
            onUpdate.run();

        });

        Button No = new Button("No");
        No.setLayoutX(325);
        No.setLayoutY(230);
        No.setFont(lexend14);
        No.setPrefSize(99,78);

        No.setOnAction(e ->{
            pane.setVisible(false);
        });
        pane.getChildren().addAll(Yes,No);

        return pane;
    }
}