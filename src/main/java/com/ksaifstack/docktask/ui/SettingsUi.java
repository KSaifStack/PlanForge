package com.ksaifstack.docktask.ui;
/**
Allows the user to modify the following options:
@code Dark Mode-Allows user to change themes
@code Export data-Allows user to get a clone of their current data to store
@code Import data-Allows user to import data folders
@code Refresh Data-Allows Allows users to refresh taskui in case of ui issues
@code Remove CreateTask Text-Gives user the option to display createtask text or not
@code Exit app-Allows user to exit the app
Remove create task text at the bottom
 **/

import com.ksaifstack.docktask.model.UserData;
import com.ksaifstack.docktask.util.WindowActions;
import com.ksaifstack.docktask.util.themeManager;
import javafx.application.Platform;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Window;

import java.io.File;
import java.io.IOException;


public class SettingsUi {
    private String username = null;
    private boolean createTaskVis = true;
    private ConfirmationUi confirmation = new ConfirmationUi();

    //Runnable
    public SettingsUi(String username){
        this.username=username;
    }
    public Pane getContent(Font lexend14, Font lexend32, WindowActions window, TaskUi t){
        //Allows for grayed out background
        Pane pane = new Pane();
        pane.setPrefSize(2000, 2000);
        pane.setStyle("-fx-background-color: rgba(0, 0, 0, 0.15);");

        //Main Setting Region
        Region background = new Region();
        background.setLayoutX(165);
        background.setLayoutY(10);
        background.setPrefSize(628, 382);
        background.getStyleClass().add("region");

        pane.getChildren().add(background);

        Button settingsLabel = new Button("Settings");
        settingsLabel.setPrefSize(200,60);
        settingsLabel.setFont(lexend32);
        settingsLabel.setLayoutX(372);
        settingsLabel.setLayoutY(15);
        settingsLabel.setId("noHoverBtn");
        pane.getChildren().add(settingsLabel);

        //Back to the Main pane(TaskUi)

        /*
        This will use a Hbox to store the setting buttons.
        There are 6 setting buttons I want to add, so I will make a Hbox.
        which will hold two VBoxs each.
        afterwords I will take the Hbox and bind it to a scrollPane.
         */
        HBox settingHolder= new HBox(90);
        ScrollPane settingsView = new ScrollPane(settingHolder);
        settingsView.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        settingsView.setFitToWidth(true);
        settingsView.setStyle("-fx-border-color: transparent");
        settingsView.setPrefSize(510,180);
        //settingsView.setMaxHeight(200);
        settingsView.setLayoutX(255);
        settingsView.setLayoutY(115);
        //Left Side
        VBox leftSide = new VBox(30);
        //Right Side
        VBox rightSide = new VBox(30);




        //Button one: Dark Mode
        //Allows user to switch between the Dark theme and Light theme
        //Subject to change for plugin migration
        Button darkswitch = createSet("Dark Theme", lexend14);
        if(themeManager.isDarkMode()){
            darkswitch.setText("Light Theme");
        }
        else{
            darkswitch.setText("Dark Theme");
        }
        darkswitch.setOnAction(e -> {
            themeManager.changeTheme();
        });

        themeManager.addThemeChangeListener(() -> {
            if(themeManager.isDarkMode()){
                darkswitch.setText("Light Theme");

            }
            else{
                darkswitch.setText("Dark Theme");

            }
        });



        //Button two: Export Data
        //Allows user to clone current data and save it on their computer.
        Button exportData =createSet("Export Data",lexend14);
        exportData.setOnAction(e->{
            FileChooser filePick = new FileChooser();
            filePick.setTitle("Export Data");
            filePick.getExtensionFilters().add(new FileChooser.ExtensionFilter("Text Files", "*.txt"));
            filePick.setInitialFileName(username + "_DockTask_Export.txt");
            File selectedFile = filePick.showSaveDialog((Window) window);
            if(selectedFile!=null){
                Pane n=confirmation.check("Export Data",selectedFile.getPath(),lexend14,()->{
                    if(UserData.exportData(selectedFile, username)){
                        System.out.println("Data exported!");
                    }
                });
                pane.getChildren().add(n);


            }
        });

        //Button three: Import Data
        //Allows user to Import Data into the application.
        //Since this will require a restart this will need the (confirmation class).
        Button importData =createSet("Import Data",lexend14);
        importData.setOnAction(e->{
            FileChooser filePick = new FileChooser();
            filePick.setTitle("Import Data");
            filePick.getExtensionFilters().add(new FileChooser.ExtensionFilter("Text Files", "*.txt"));
            File selectedFile = filePick.showOpenDialog((Window) window);
            if(selectedFile != null) {
                    Pane conPane= confirmation.check("Import Data",selectedFile.getPath(),lexend14,()->{
                        try {
                            UserData.importDataFromFile(selectedFile,username);
                            t.updateRun();
                        } catch (IOException ex) {
                            System.out.println("Error with getting file!");
                            System.out.println(ex.getMessage());
                        }

                    });
                     pane.getChildren().add(conPane);
            }

            //Conformation Class here
            //Then user Data will handle it
            //Once handled application will restart
        });

        //Button four: Refresh Data
        //Allows user to reopen taskui as if its being open for the first time
        //Solves ui issues
        Button refreshApp = createSet("Refresh App",lexend14);
        refreshApp.setOnAction(e->{
            t.updateRun();
        });



        //Button six: Exit App
        //Allows the user the close the application through settings.
        //(Needs conformation class)
        Button exitApp =createSet("Exit",lexend14);
        exitApp.setOnAction(e->{
            Pane c = confirmation.check("Exit",null,lexend14,()->{
                Platform.exit();
                System.exit(0);

            });
            pane.getChildren().add(c);
        });

        //Button seven: Remove Create Task text.
        //In main ui there is a button below which as '(Create Task)'.
        //This button will allow user to remove it or not.
        Button textToggle = createSet("Hide Create Text",lexend14);

        textToggle.setOnAction(e->{
            boolean viss = t.createTaskVisabitly();
            createTaskVis=viss;
        });

        //Button eight: Reset Widgets
        //Resets pos of buttons that fall under a 'Widget'
        Button resetWidgetPos = createSet("Reset Widgets", lexend14);
        resetWidgetPos.setOnAction(e -> {
            t.resetWidgetPosition();
        });

        leftSide.getChildren().addAll(darkswitch,importData,refreshApp);
        rightSide.getChildren().addAll(textToggle,exportData,resetWidgetPos,exitApp);
        settingHolder.getChildren().addAll(leftSide,rightSide);

        //Version Button
        Button version = new Button("v0.7.0");
        version.setId("noHoverBtn");
        version.setPrefSize(60,29);
        version.setFont(lexend14);
        version.setLayoutX(722);
        version.setLayoutY(355);
        pane.getChildren().add(version);

        //WaterMark Button
        Button WaterMark = getWaterMark(lexend14);

        //Back Button
        Button back = new Button("Back");
        back.setPrefSize(73,45);
        back.setFont(lexend14);
        back.setLayoutX(172);
        back.setLayoutY(340);
        back.setOnAction(e->{
            String currentTheme="Light";
            if(themeManager.isDarkMode()){
                currentTheme="Dark";
            }
            else{
                currentTheme="Light";
            }
            UserData.updateSet(username, currentTheme,createTaskVis);
            pane.setVisible(false);
        });

        pane.getChildren().add(back);

        pane.getChildren().add(WaterMark);

        pane.getChildren().add(settingsView);
        return pane;
    }

    private static Button getWaterMark(Font lexend14) {
        Button WaterMark = new Button("@KSaifStack");
        WaterMark.setPrefSize(110,25);
        WaterMark.setFont(lexend14);
        WaterMark.setLayoutY(15);
        WaterMark.setLayoutX(675);
        WaterMark.setId("noHoverBtn");
        WaterMark.setTooltip(new Tooltip("Visit my GitHub!"));
        WaterMark.setOnMouseEntered(e->{
            WaterMark.setStyle("-fx-underline: true;-fx-cursor: hand;");
        });
        WaterMark.setOnMouseExited(e->{
            WaterMark.setStyle("-fx-underline: false;");
        });
        WaterMark.setOnAction(e->{
            LoginUi.openURL("https://github.com/KSaifStack");
        });
        return WaterMark;
    }

    public Button createSet(String text,Font font){
        Button btn = new Button(text);
        btn.setPrefSize(164,68);
        btn.setFont(font);
        return btn;

    }


}
