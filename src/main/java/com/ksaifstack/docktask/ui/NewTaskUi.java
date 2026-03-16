package com.ksaifstack.docktask.ui;

import com.dustinredmond.fxtrayicon.FXTrayIcon;
import com.ksaifstack.docktask.util.AppTray;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Font;
import javafx.stage.Stage;

// Main ui page
public class NewTaskUi {

    private String username;
    private final StackPane rootStack = new StackPane();
    private FXTrayIcon trayIcon;
    private final AppTray tray = AppTray.getInstance();
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
        //Create Task,
        // Welcome "user", live clock, task-pane
        //Ui Elements end here
        Platform.setImplicitExit(false);
        window.setOnCloseRequest(event -> {
            event.consume();
            Platform.runLater(window::hide);
        });
        rootStack.getChildren().clear();
        rootStack.getChildren().addAll(pane);
    }

}
