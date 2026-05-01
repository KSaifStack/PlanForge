package com.ksaifstack.docktask.ui;//Ui for updating tasks/changing tasks
import com.ksaifstack.docktask.model.UserData;
import com.ksaifstack.docktask.util.CustomDatePicker;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import java.util.List;
import java.util.ArrayList;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class UpdateTaskUi {
    private final String username;
    private  final String taskname;
    private final Runnable onUpdate;
    public UpdateTaskUi(String username,String taskname,Runnable onUpdate){
        this.username= username;
        this.taskname =taskname;
        this.onUpdate = onUpdate;
    }
    Font lexend14 = Font.loadFont(getClass().getResourceAsStream("/fonts/Lexend.ttf"), 14);
    Font lexend32 = Font.loadFont(getClass().getResourceAsStream("/fonts/Lexend.ttf"), 32);
    Font lexend30 = Font.loadFont(getClass().getResourceAsStream("/fonts/Lexend.ttf"), 20);
    public Pane getContent()
    {
        //Gets Tasks and copys it to a array so it display data to change.
        //updateTask(String username, String taskname, String newDescription, int newRank,String TaskGroup,LocalDateTime dueDate)
        String[] pressedtasks = new String[4];
        System.out.println("This is what update task says: "+taskname);
        String[][]tasks= UserData.ReturnData(username);
        for(String[] task: tasks){
            if(task[0].equals(taskname)){
                pressedtasks=task;
            }
        }
        final String[] upName = {pressedtasks[0]};
        final String[] upDesc= {pressedtasks[1]};
        final int[] upRank = {Integer.parseInt(pressedtasks[2])};
        final String[] upGroup = {pressedtasks[3]};
        final String[] upDateinput = {pressedtasks[4]};
        final LocalDateTime[] upDate = {LocalDateTime.parse(upDateinput[0], DateTimeFormatter.ofPattern("yyyy MM dd hh mm a"))};

        Pane pane = new Pane();
        pane.setPrefSize(980, 493);
        pane.setStyle("-fx-background-color: rgba(0, 0, 0, 0.15);");




        Region background = new Region();
        background.setLayoutX(38);
        background.setLayoutY(38);
        background.setPrefSize(522, 386);
        background.getStyleClass().add("region");
        pane.getChildren().add(background);


        TextField taskNamein = new TextField();
        taskNamein.setLayoutX(135);
        taskNamein.setLayoutY(59);
        taskNamein.setPrefSize(139, 24);
        taskNamein.setText(taskname);
        taskNamein.setFont(lexend14);
        pane.getChildren().add(taskNamein);

        Button taskCheck = createStyledButton("Done", 180, 96, 52, 26, Font.font(lexend14.getName(), 12));

        taskCheck.setOnAction(e->{
            String input = taskNamein.getText().trim();
            boolean doubled = false;
            for(String [] task:tasks){
                if(task[0].equals(input)){
                    doubled=true;
                }
            }
            if(doubled==true) {
                System.out.println("ERROR: Cannot use same task name.");

            }
            else if(input.isEmpty()){
                System.out.println("ERROR: No task name printed.");
            }
            else{
                upName[0]=input;
                UserData.updateTask(username, taskname,upName[0], upDesc[0],upRank[0],upGroup[0], upDate[0]);
            }
        });

        pane.getChildren().add(taskCheck);

        Label nameLabel = new Label("Task Name:");
        nameLabel.setLayoutX(50);
        nameLabel.setLayoutY(60);
        nameLabel.setFont(lexend14);
        pane.getChildren().add(nameLabel);

        com.ksaifstack.docktask.util.HyperlinkTextArea descArea = new com.ksaifstack.docktask.util.HyperlinkTextArea(pane, "Write a description!", 246, 100);
        descArea.setLayoutX(65);
        descArea.setLayoutY(146);
        descArea.setText(upDesc[0]);
        pane.getChildren().add(descArea);

        Button expandDesc = descArea.getExpandButton();
        expandDesc.setLayoutX(315);
        expandDesc.setLayoutY(146);
        pane.getChildren().add(expandDesc);

        Button checkDesc = createStyledButton("Done", 164, 250, 52, 26, Font.font(lexend14.getName(), 12));
        //Button taskCheck = createStyledButton("Done", 180, 96, 48, 26, Font.font(lexend14.getName(), 12));

        checkDesc.setOnAction(e ->{
            String input = descArea.getText().trim();
            upDesc[0]=input;
            UserData.updateTask(username, taskname,upName[0], upDesc[0],upRank[0],upGroup[0], upDate[0]);

        });
        pane.getChildren().add(checkDesc);


        Label descLabel = new Label("Task Description:");
        descLabel.setLayoutX(64.8);
        descLabel.setLayoutY(115.96);
        descLabel.setFont(lexend14);
        pane.getChildren().add(descLabel);

        Label rankLabel = new Label("Task Rank:");
        rankLabel.setLayoutX(314.8);
        rankLabel.setLayoutY(52.96);
        rankLabel.setFont(lexend14);
        pane.getChildren().add(rankLabel);

        //Rankings
        double x = 320;
        List<Button> rankButtons = new ArrayList<>();

        for (int i = 1; i <= 5; i++) {
            Button rankBtn = createStyledButton(String.valueOf(i), x, 78, 27, 27, lexend14);
            int num = Integer.parseInt(rankBtn.getText());
            rankButtons.add(rankBtn);
            updateButtonStyle(rankBtn,num==upRank[0]);

            rankBtn.setOnAction(e -> {
                System.out.println(num);
                upRank[0]=num;
                UserData.updateTask(username, taskname,upName[0], upDesc[0],upRank[0],upGroup[0], upDate[0]);
                for(int j =0; j<rankButtons.size(); j++){
                    updateButtonStyle(rankButtons.get(j),(j+1)==upRank[0]);
                }


            });


            pane.getChildren().add(rankBtn);
            x += 37.8;
        }


        Label groupLabel = new Label("Task Group:");
        groupLabel.setLayoutX(338.8);
        groupLabel.setLayoutY(126.95);
        groupLabel.setFont(lexend14);
        pane.getChildren().add(groupLabel);

        TextArea groupText = new TextArea();
        groupText.setLayoutX(341.8);
        groupText.setLayoutY(150.46);
        groupText.setPrefSize(145, 41);
        groupText.setText(upGroup[0]);
        groupText.setFont(lexend14);
        groupText.setStyle("  -fx-border-color: #626262; -fx-border-width: 1px; -fx-border-radius: 2px; -fx-prompt-text-fill: #737674;");
        pane.getChildren().add(groupText);

        Button groupCheck = createStyledButton("Done", 390, 200.5,52, 26, Font.font(lexend14.getName(), 12));

        groupCheck.setOnAction(e ->{
            String input = groupText.getText().trim();
            upGroup[0]=input;
            UserData.updateTask(username, taskname,upName[0], upDesc[0],upRank[0],upGroup[0], upDate[0]);
        });
        pane.getChildren().add(groupCheck);

        Button trash = createStyledButton("Trash", 487, 359, 62, 54, Font.font(lexend14.getName(), 12));

        Font finalLexend1 = lexend14;
        Font finalLexend2 = lexend30;
        trash.setOnAction(e-> {
            System.out.println("Trash button was pressed.");
            RemoveTaskUi RemoveTaskUi = new RemoveTaskUi(username,taskname,()->{onUpdate.run();});
            Pane RemovePane = new Pane();
            RemovePane = RemoveTaskUi.getContent();
            pane.getChildren().add(RemovePane);
        });
        pane.getChildren().add(trash);

        String[] parts = upDateinput[0].split(" ");
        String dateDay= parts[0]+"-"+parts[1]+"-"+parts[2];
        String dateTime=parts[3]+":"+parts[4]+" "+parts[5];

        Button dueDatetext = new Button(dateTime);
        dueDatetext.setPrefSize(100,2);
        dueDatetext.setLayoutX(90);
        dueDatetext.setLayoutY(290);
        dueDatetext.setFont(lexend14);
        pane.getChildren().add(dueDatetext);

        final Button[] dueDate = {new Button(dateDay)};
        dueDate[0].setPrefSize(142, 24);
        dueDate[0].setLayoutX(70);
        dueDate[0].setLayoutY(322);
        dueDate[0].setFont(lexend14);
        pane.getChildren().add(dueDate[0]);
        CustomDatePicker datapicker = new CustomDatePicker();
        datapicker.setLayoutX(-45);
        datapicker.setLayoutY(255);
        Button timeInput = createStyledButton("Done", 300, 353, 52, 26, Font.font(lexend14.getName(), 12));

        pane.getChildren().addAll(datapicker,timeInput);
        timeInput.setOnAction(e-> {
            if(datapicker.getDateTime()==null){
                System.out.println("ERROR: Please enter proper time.");
            }else {
                String input = CustomDatePicker.formatDateTime(datapicker.getDateTime());
                upDateinput[0]=input;
                upDate[0] = LocalDateTime.parse(upDateinput[0],DateTimeFormatter.ofPattern("yyyy MM dd hh mm a"));
                UserData.updateTask(username, taskname, upName[0], upDesc[0], upRank[0], upGroup[0], upDate[0]);
            }

        });
        checkDesc.toFront();



        Button goBack = createStyledButton("Back", 48.8, 359, 64, 56, lexend14);


        goBack.setOnAction(e-> {
            onUpdate.run();

            pane.setVisible(false);

        });

        pane.getChildren().add(goBack);

        return pane;
    }

    private Button createStyledButton(String text, double x, double y, double w, double h, Font font) {
        Button btn = new Button(text);
        btn.setLayoutX(x);
        btn.setLayoutY(y);
        btn.setPrefSize(w, h);
        btn.setFont(font);
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