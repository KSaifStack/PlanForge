package com.ksaifstack.docktask.ui;

import com.dustinredmond.fxtrayicon.FXTrayIcon;
import com.ksaifstack.docktask.model.UserData;
import com.ksaifstack.docktask.util.AppTray;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import static com.ksaifstack.docktask.ui.FontLoader.setFont;

/*
* Main Application Page
*/
public class NewTaskUi {
    private String username;
    private final StackPane rootStack = new StackPane();
    private FXTrayIcon trayIcon;
    private final AppTray tray = AppTray.getInstance();
    private CreateTaskUi createTaskUi;
    private Pane createOverlay;


    public NewTaskUi(String username) {
        this.username = username;
    }

    public void start(WindowActions window, LoginUi loginUi) {
        setupUI(window, window.getScene(),loginUi);
    }


    public void setupUI(WindowActions window, Scene scene,LoginUi loginUi) {
        Pane pane = new Pane();
        window.setTitleLabel(" ");
        window.colorChange(" ");
        pane.setPrefSize(980, 493);
        tray.setup((Stage) window, "DockTask", "/images/logo.png");
        trayIcon = tray.getTrayIcon();
        //Calander Ui
        newCalendarUi calendar = new newCalendarUi(username);
        Pane calendarPane = calendar.getPane();
        // Create Task(Button),
        taskListUi taskListUi = new taskListUi(
                username,
                overlay -> rootStack.getChildren().add(overlay),rootStack, // onShowOverlay
                () -> System.out.println("hi")                     // onTaskUpdated — calendar refresh is TaskUi's job
        );
        Button createtaskb = new Button("+");
        createtaskb.setFont(setFont(60));
        createtaskb.setLayoutX(775);
        createtaskb.setLayoutY(140.00);
        createtaskb.setPrefWidth(150);
        createtaskb.setPrefHeight(150);
        createtaskb.setOnAction(e -> {
            if (createTaskUi == null) {
                createTaskUi = new CreateTaskUi(username, () -> {
                    taskListUi.refreshTaskList();
                    rootStack.getChildren().remove(createOverlay);
                });
                createOverlay = createTaskUi.getContent();
            }
            if (!createTaskUi.isOpen()) {
                createOverlay.setVisible(true);
                if (!rootStack.getChildren().contains(createOverlay)) {
                    rootStack.getChildren().add(createOverlay);
                }
            }
        });
        pane.getChildren().add(createtaskb);
        //Upper App
        // Welcome "user"(Text)
        String firstWelcome = username != null && username.length() > 0 ? username.substring(0, 1).toUpperCase() : "";
        String secondWelcome = username != null && username.length() > 1 ? username.substring(1) : "";
        Label Welcome = new Label("Welcome " + firstWelcome + secondWelcome + "!");
        Welcome.setFont(setFont(16));
        Welcome.setPadding(new Insets(20, 0, 0, 48));

        HBox upperLayout = new HBox(-12,Welcome);
        pane.getChildren().add(upperLayout);
        // live clock(Method)
        // task-pane(Class)
        String[][] tasksArr = UserData.ReturnData(username);
        StackPane taskContainer = taskListUi.TaskList(tasksArr);
        Region leftSpacer = new Region();
        VBox.setVgrow(leftSpacer, Priority.ALWAYS);

        Button logoutBtn = new Button("Logout");

        Region topSpace = new Region();
        topSpace.setPrefHeight(40);
        VBox leftPanel = new VBox(12, topSpace,taskContainer, leftSpacer, logoutBtn);
        leftPanel.setPadding(new Insets(8));
        leftPanel.setPrefWidth(200);

        pane.getChildren().add(leftPanel);
        //Ui Elements end here
        pane.getChildren().add(calendarPane);
        Platform.setImplicitExit(false);
        window.setOnCloseRequest(event -> {
            event.consume();
            Platform.runLater(window::hide);
        });
        rootStack.getChildren().clear();
        rootStack.getChildren().addAll(pane);
        pane.setPrefSize(980, 493);
        scene = window.getScene();
        window.setContent(rootStack);
        window.setTitle("DockTask - Home");
        window.setWidth(990);
        window.setHeight(531);
    }

}
