package com.ksaifstack.docktask.ui;//Plugin Ui for allowing extra features to be used
//TO BE WORKED ON
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;

public class PluginUi {
    private final String username;
    public PluginUi(String username){
        this.username=username;
    }
    public Pane getContent(Font lexend14,Font lexend32){
        Pane pane = new Pane();
        pane.setPrefSize(980, 493);
        pane.setStyle("-fx-background-color: rgba(0, 0, 0, 0.15);");

        Region background = new Region();
        background.setLayoutX(165);
        background.setLayoutY(10);
        background.setPrefSize(605, 372);
        background.setStyle("-fx-background-color: #ffffff; -fx-border-color: #626262; -fx-border-radius: 4px; -fx-background-radius: 4px; -fx-border-width: 1px;");
        pane.getChildren().add(background);
        //--
        Button pluginLabel = new Button("Plugins");
        pluginLabel.setStyle("-fx-background-color: #ffffff; -fx-border-color: #626262; -fx-border-radius: 4px; -fx-background-radius: 4px; -fx-border-width: 1px;");
        pluginLabel.setPrefSize(192,59);
        pluginLabel.setFont(lexend32);
        pluginLabel.setLayoutX(369);
        pluginLabel.setLayoutY(17);
        pane.getChildren().add(pluginLabel);

        //--
        Button back = new Button("Back");
        back.setStyle("-fx-background-color: #ffffff; -fx-border-color: #626262; -fx-border-radius: 4px; -fx-background-radius: 4px; -fx-border-width: 1px;");
        back.setPrefSize(73,60);
        back.setFont(lexend14);
        back.setLayoutX(172);
        back.setLayoutY(317);
        back.setOnMouseEntered(e -> back.setStyle(
                "-fx-background-color: #d3d3d3; -fx-text-fill: #1b1b1b; -fx-border-color: #626262; -fx-border-radius: 4px; -fx-background-radius: 4px; -fx-border-width: 1px;"
        ));
        back.setOnMouseExited(e -> back.setStyle(
                "-fx-background-color: #ffffff; -fx-text-fill: #1b1b1b; -fx-border-color: #626262; -fx-border-radius: 4px; -fx-background-radius: 4px; -fx-border-width: 1px;"
        ));
        back.setOnAction(e->{
            pane.setVisible(false);
        });
        pane.getChildren().add(back);
        //--
        return pane;
    }


}
