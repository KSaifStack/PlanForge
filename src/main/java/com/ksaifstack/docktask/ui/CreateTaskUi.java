package com.ksaifstack.docktask.ui;// UI for creating tasks

import com.ksaifstack.docktask.model.UserData;
import com.ksaifstack.docktask.util.CustomDatePicker;
import javafx.application.Platform;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.text.Font;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * One pane like updateui,error meg button(if user is missing one of the
 * following)
 * back button if user wants to go back
 * have calander and taaks refresh if user makes task with no issues
 * Confirm button if user wants to create task
 */
public class CreateTaskUi {
    private final String username;
    private final Runnable onUpdate;
    private Pane overlayPane;

    public CreateTaskUi(String username, Runnable onUpdate) {
        this.username = username;
        this.onUpdate = onUpdate;
    }

    Font lexend14 = Font.loadFont(getClass().getResourceAsStream("/fonts/Lexend.ttf"), 14);
    Font lexend32 = Font.loadFont(getClass().getResourceAsStream("/fonts/Lexend.ttf"), 32);
    Font lexend30 = Font.loadFont(getClass().getResourceAsStream("/fonts/Lexend.ttf"), 20);

    int storeNum;
    LocalDateTime taskTime;

    public boolean isOpen() {
        return overlayPane != null && overlayPane.isVisible();
    }

    public Pane getContent() {
        Pane pane = new Pane();
        pane.setPrefSize(980, 493);
        pane.setStyle("-fx-background-color: rgba(0, 0, 0, 0.15);");

        // Background
        Region background = new Region();
        background.setLayoutX(400);
        background.setLayoutY(20);
        background.setPrefSize(556, 431);
        background.getStyleClass().add("region");
        pane.getChildren().add(background);

        // Title
        Label Title = new Label("Create Task");
        Title.setLayoutX(415);
        Title.setLayoutY(30);
        Title.setFont(lexend32);
        pane.getChildren().add(Title);

        // Task Name
        Label nameLabel = new Label("Task Name:");
        nameLabel.setLayoutX(415);
        nameLabel.setLayoutY(85);
        nameLabel.setFont(lexend14);
        pane.getChildren().add(nameLabel);

        TextField taskNamein = new TextField();
        taskNamein.setFont(lexend14);
        taskNamein.setPromptText("Give your task a name!");
        taskNamein.setLayoutX(415);
        taskNamein.setLayoutY(110);
        taskNamein.setPrefSize(220, 28);
        pane.getChildren().add(taskNamein);
        //

        // Task Description
        Label descLabel = new Label("Task Description:");
        descLabel.setLayoutX(415);
        descLabel.setLayoutY(155);
        descLabel.setFont(lexend14);
        pane.getChildren().add(descLabel);

        com.ksaifstack.docktask.util.HyperlinkTextArea descArea = new com.ksaifstack.docktask.util.HyperlinkTextArea(
                pane, "Write a description!", 300, 110);
        descArea.setLayoutX(415);
        descArea.setLayoutY(180);
        pane.getChildren().add(descArea);

        Button expandDesc = descArea.getExpandButton();
        expandDesc.setLayoutX(720);
        expandDesc.setLayoutY(264);
        pane.getChildren().add(expandDesc);

        // Task Group
        Label groupLabel = new Label("Task Group:");
        groupLabel.setLayoutX(415);
        groupLabel.setLayoutY(310);
        groupLabel.setFont(lexend14);
        pane.getChildren().add(groupLabel);

        TextArea groupText = new TextArea();
        groupText.setPromptText("Give your task a group!");
        groupText.setLayoutX(415);
        groupText.setLayoutY(335);
        groupText.setPrefSize(220, 45);
        groupText.setFont(lexend14);
        groupText.setStyle(
                "-fx-border-color: #626262; -fx-border-width: 1px; -fx-border-radius: 2px; -fx-prompt-text-fill: #737674;");
        pane.getChildren().add(groupText);

        double x = 760;
        List<Button> rankButtons = new ArrayList<>();

        for (int i = 1; i <= 5; i++) {
            Button rankBtn = createStyledButton(String.valueOf(i), x, 110, 27, 27, lexend14);
            int num = Integer.parseInt(rankBtn.getText());
            rankButtons.add(rankBtn);
            updateButtonStyle(rankBtn, num == storeNum);
            rankBtn.setOnAction(e -> {
                storeNum = num;
                for (int j = 0; j < rankButtons.size(); j++) {
                    updateButtonStyle(rankButtons.get(j), (j + 1) == storeNum);
                }
            });
            pane.getChildren().add(rankBtn);
            x += 37.8;
        }

        Label rankLabel = new Label("Task Rank:");
        rankLabel.setLayoutX(760);
        rankLabel.setLayoutY(85);
        rankLabel.setFont(lexend14);
        pane.getChildren().add(rankLabel);

        CustomDatePicker datapicker = new CustomDatePicker();
        datapicker.setLayoutX(470);
        datapicker.setLayoutY(280);
        pane.getChildren().addAll(datapicker);

        Label ErrorMeg = new Label();
        ErrorMeg.setLayoutX(625);
        ErrorMeg.setLayoutY(40);
        ErrorMeg.setFont(lexend30);
        ErrorMeg.setStyle("-fx-text-fill: red;");
        ErrorMeg.setVisible(false);
        pane.getChildren().add(ErrorMeg);

        // Create
        Button goBack = new Button("Back");
        goBack.setFont(lexend14);
        goBack.setPrefSize(70, 45);
        goBack.setLayoutX(410);
        goBack.setLayoutY(395);
        goBack.setOnAction(e -> {
            pane.setVisible(false);
            ErrorMeg.setVisible(false);
        });
        pane.getChildren().add(goBack);

        Button Done = createStyledButton("Create!", 615, 390, 130, 50, Font.font(lexend14.getName(), 12));
        Done.setOnAction(e -> {
            String taskName = taskNamein.getText().trim();
            String taskDesc = descArea.getText().trim();
            String taskGroup = groupText.getText().trim();
            if (datapicker.getDateTime() == null) {
                ErrorMeg.setText("Please select a date!");
                ErrorMeg.setVisible(true);
            } else {
                String input = CustomDatePicker.formatDateTime(datapicker.getDateTime());
                taskTime = LocalDateTime.parse(input, DateTimeFormatter.ofPattern("yyyy MM dd hh mm a"));
            }
            // String taskDue;
            int taskRank = storeNum;
            System.out.println(
                    "Name: " + taskName + " Desc: " + taskDesc + " Group: " + taskGroup + " Rank: " + storeNum);
            System.out.println("Time: " + taskTime);

            if (taskName != null && !taskName.isEmpty() &&
                    taskDesc != null && !taskDesc.isEmpty() &&
                    taskGroup != null && !taskGroup.isEmpty() &&
                    taskRank != 0 &&
                    taskTime != null) {
                String[][] Data = UserData.ReturnData(username);
                boolean checker = true;
                for (String[] bits : Data) {
                    if (bits[0].equals(taskName)) {
                        checker = false;
                        break;
                    }
                }
                if (checker) {
                    UserData.SaveTask(username, taskName, taskDesc, taskRank, taskGroup, taskTime);
                    Platform.runLater(onUpdate);
                    pane.setVisible(false);
                } else if (!checker) {
                    ErrorMeg.setVisible(false);
                    ErrorMeg.setText("Cant have same task name!");
                    ErrorMeg.setVisible(true);
                }
            } else {
                ErrorMeg.setText("Make sure everything is filled out!");
                ErrorMeg.setVisible(true);
            }
        });
        Done.setFont(lexend30);
        pane.getChildren().add(Done);

        return pane;
    }

    private Button createStyledButton(String text, double x, double y, double w, double h, Font font) {
        Button btn = new Button(text);
        btn.setLayoutX(x);
        btn.setLayoutY(y);
        btn.setPrefSize(w, h);
        btn.setFont(font);
        btn.setStyle(
                "  -fx-border-color: #626262; -fx-border-radius: 4px; -fx-background-radius: 4px; -fx-border-width: 1px;");
        return btn;
    }

    private void updateButtonStyle(Button btn, boolean isSelected) {
        if (isSelected) {
            btn.setStyle("-fx-background-color: #767676; " +
                    "-fx-border-color: #626262; " +
                    "-fx-border-radius: 4px; " +
                    "-fx-background-radius: 4px; " +
                    "-fx-border-width: 1px;");

        } else {
            btn.setStyle(

                    "-fx-border-color: #626262; " +
                            "-fx-border-radius: 4px; " +
                            "-fx-background-radius: 4px; " +
                            "-fx-border-width: 1px;");
        }
    }

}
