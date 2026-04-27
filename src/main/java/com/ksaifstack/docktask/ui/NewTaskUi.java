package com.ksaifstack.docktask.ui;

import com.dustinredmond.fxtrayicon.FXTrayIcon;
import com.ksaifstack.docktask.model.UserData;
import com.ksaifstack.docktask.util.AppTray;
import com.ksaifstack.docktask.util.themeManager;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    private Label Createtasktext = new Label("(Create Task)");

    private final Image sunImg       = new Image(getClass().getResourceAsStream("/images/sun.png"));
    private final Image moonImg      = new Image(getClass().getResourceAsStream("/images/moon.png"));
    private final Image whiteSunImg  = new Image(getClass().getResourceAsStream("/images/whiteSun.png"));
    private final Image whiteMoonImg = new Image(getClass().getResourceAsStream("/images/whiteMoon.png"));

    private Timeline clockTimeline;
    private Timeline countdownTimeline;
    private Runnable themeListener;
    private taskListUi currentTaskListUi;
    private newCalendarUi currentCalendar;
    private final Map<String, String> lastNotifiedStage = new HashMap<>();


    public NewTaskUi(String username) {
        this.username = username;
    }

    public void start(WindowActions window, LoginUi loginUi) {
        setupUI(window, window.getScene(), loginUi);
    }


    public void setupUI(WindowActions window, Scene scene, LoginUi loginUi) {
        cleanup();

        Pane pane = new Pane();
        window.setTitleLabel(" ");
        window.colorChange(" ");
        pane.setPrefSize(980, 493);
        tray.setup((Stage) window, "DockTask", "/images/logo.png");
        trayIcon = tray.getTrayIcon();

        // Calendar UI — stored as field so countdown can call updateCalender()
        currentCalendar = new newCalendarUi(username);
        Pane calendarPane = currentCalendar.getPane();

        // Task list UI — stored as field so cleanup() and countdown can reach it
        currentTaskListUi = new taskListUi(
                username,
                overlay -> rootStack.getChildren().add(overlay),
                rootStack,
                () -> currentCalendar.updateCalender()
        );

        // Create Task button
        Button createtaskb = new Button("+");
        createtaskb.setFont(setFont(60));
        createtaskb.setLayoutX(775);
        createtaskb.setLayoutY(140.00);
        createtaskb.setPrefWidth(150);
        createtaskb.setPrefHeight(150);
        createtaskb.setOnAction(e -> {
            if (createTaskUi == null) {
                createTaskUi = new CreateTaskUi(username, () -> {
                    currentTaskListUi.refreshTaskList();
                    currentCalendar.updateCalender();
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
        Createtasktext.setFont(setFont(14));
        Createtasktext.setLayoutX(805);
        Createtasktext.setLayoutY(300);
        pane.getChildren().add(Createtasktext);

        pane.getChildren().add(createtaskb);

        // Welcome label
        String firstWelcome = username != null && username.length() > 0 ? username.substring(0, 1).toUpperCase() : "";
        String secondWelcome = username != null && username.length() > 1 ? username.substring(1) : "";
        Label Welcome = new Label("Welcome " + firstWelcome + secondWelcome + "!");
        Welcome.setFont(setFont(16));
        Welcome.setMaxWidth(Double.MAX_VALUE);
        Welcome.setAlignment(Pos.CENTER);

        // Task list
        String[][] tasksArr = UserData.ReturnData(username);
        StackPane taskContainer = currentTaskListUi.TaskList(tasksArr);
        Region leftSpacer = new Region();
        VBox.setVgrow(leftSpacer, Priority.ALWAYS);

        // Logout button
        Button logoutBtn = new Button("Logout");
        logoutBtn.setFont(setFont(12));
        logoutBtn.setPrefSize(67, 30);
        logoutBtn.setOnAction(e -> {
            cleanup();
            loginUi.showBack((Stage) window);
        });

        Region topSpace = new Region();
        topSpace.setPrefHeight(10);

        VBox leftPanel = new VBox(12, topSpace, Welcome, taskContainer, leftSpacer, logoutBtn);
        leftPanel.setPadding(new Insets(8));
        leftPanel.setPrefWidth(200);

        // Settings button — same pattern as TaskUi
        Button settingsBtn = new Button("Settings");
        settingsBtn.setFont(setFont(14));
        settingsBtn.setLayoutX(893.00);
        settingsBtn.setLayoutY(4.46);
        settingsBtn.setPrefWidth(76.00);
        settingsBtn.setPrefHeight(30.00);
        settingsBtn.setOnAction(e -> {
            Settings settings = new Settings(username);
            NewTaskUi self = this;
            Pane settingsPane = settings.getContent(setFont(14), setFont(32), window, self);
            pane.getChildren().add(settingsPane);
        });
        pane.getChildren().add(settingsBtn);

        // Clock
        Label timeLabel = new Label(LocalDateTime.now().format(DateTimeFormatter.ofPattern("M/d/yyyy h:mm:ss a")));
        timeLabel.setFont(setFont(32));
        timeLabel.setPrefHeight(45);

        int currentHour = LocalDateTime.now().getHour();
        boolean isNight = currentHour >= 18 || currentHour < 6;
        ImageView iconView = isNight ? getMoon() : getSun();
        iconView.setTranslateX(5);
        iconView.setTranslateY(-5);

        HBox timeLabelBox = new HBox(timeLabel, iconView);
        timeLabelBox.setLayoutX(255);
        timeLabelBox.setLayoutY(8);
        timeLabelBox.setPrefWidth(455);
        timeLabelBox.setAlignment(Pos.CENTER);
        timeLabelBox.setTranslateY(isNight ? -5 : -10);

        // Clock timeline — stored as field so cleanup() can stop it
        clockTimeline = dateLine(timeLabel, iconView, timeLabelBox);
        clockTimeline.play();

        // Countdown timeline — updates task card due/warning labels + sends notifications
        countdownTimeline = buildCountdownTimeline();
        countdownTimeline.play();

        pane.getChildren().add(timeLabelBox);
        pane.getChildren().add(leftPanel);
        pane.getChildren().add(calendarPane);

        Platform.setImplicitExit(false);
        window.setOnCloseRequest(event -> {
            event.consume();
            Platform.runLater(window::hide);
        });

        // Pause timelines when minimized to save resources
        window.iconifiedProperty().addListener((obs, wasIcon, isIcon) -> {
            if (clockTimeline != null) {
                if (isIcon) clockTimeline.pause(); else clockTimeline.play();
            }
            if (countdownTimeline != null) {
                if (isIcon) countdownTimeline.pause(); else countdownTimeline.play();
            }
        });

        rootStack.getChildren().clear();
        rootStack.getChildren().add(pane);
        pane.setPrefSize(980, 493);
        scene = window.getScene();
        window.setContent(rootStack);
        window.setTitle("DockTask - Home");
        window.setWidth(990);
        window.setHeight(531);
    }

    // -------------------------------------------------------------------------
    // Countdown — updates due/warning labels and fires tray notifications
    // -------------------------------------------------------------------------
    private Timeline buildCountdownTimeline() {
        Timeline tl = new Timeline(new KeyFrame(Duration.seconds(1), e -> {
            if (currentTaskListUi == null) return;

            // Defensive copy to avoid ConcurrentModificationException
            List<taskListUi.TaskEntry> snapshot = new ArrayList<>(currentTaskListUi.getTaskEntries());
            LocalDateTime now = LocalDateTime.now();

            // Clean up stale notification keys
            lastNotifiedStage.keySet().removeIf(key -> {
                String[] parts = key.split("_", 2);
                if (parts.length < 2) return false;
                try {
                    LocalDateTime due = LocalDateTime.parse(parts[1]);
                    String stage = lastNotifiedStage.get(key);
                    return stage != null && !stage.equals("Overdue") && due.isBefore(now.minusHours(1));
                } catch (Exception ignored) {
                    return false;
                }
            });

            for (taskListUi.TaskEntry entry : snapshot) {
                long secondsUntilDue = now.until(entry.dueTime, ChronoUnit.SECONDS);
                long hoursUntilDue   = now.until(entry.dueTime, ChronoUnit.HOURS);

                // Due label
                if (hoursUntilDue > 168) {
                    DateTimeFormatter fmt = DateTimeFormatter.ofPattern("M/d/yyyy");
                    entry.dueLabel.setText(entry.dueTime.format(fmt));
                } else if (secondsUntilDue > 0) {
                    long hours   = secondsUntilDue / 3600;
                    long minutes = (secondsUntilDue % 3600) / 60;
                    long seconds = secondsUntilDue % 60;
                    entry.dueLabel.setText(String.format("%02d:%02d:%02d", hours, minutes, seconds));
                } else {
                    entry.dueLabel.setText("00:00:00");
                }

                // Warning label + notification stage
                String stage = null;
                if (secondsUntilDue <= 0) {
                    stage = "Overdue";
                    entry.warningLabel.setText("Overdue");
                } else if (secondsUntilDue <= 60) {
                    stage = "1min";
                    entry.warningLabel.setText("1 minute!");
                } else if (secondsUntilDue <= 10 * 60) {
                    stage = "10min";
                    entry.warningLabel.setText("10 minutes left!");
                } else if (secondsUntilDue <= 30 * 60) {
                    stage = "30min";
                    entry.warningLabel.setText("30 minutes left!");
                } else if (secondsUntilDue <= 60 * 60) {
                    stage = "1hr";
                    entry.warningLabel.setText("1 hour!");
                } else if (secondsUntilDue <= 5 * 60 * 60) {
                    stage = "5hr";
                    entry.warningLabel.setText("5 hours");
                } else if (secondsUntilDue <= 24 * 60 * 60) {
                    stage = "24hr";
                    entry.warningLabel.setText("24 hours");
                } else {
                    entry.warningLabel.setText("Due Soon!");
                }

                // Fire tray notification once per stage transition
                String taskKey = entry.taskName + "_" + entry.dueTime.toString();
                if (stage != null && !stage.equals(lastNotifiedStage.get(taskKey))) {
                    lastNotifiedStage.put(taskKey, stage);
                    showNotification("DockTask - " + entry.taskName, buildMessage(stage, entry.taskName));
                }
            }

            // Keep scroll policy in sync
            currentTaskListUi.updateScrollPolicy();
        }));

        tl.setCycleCount(Timeline.INDEFINITE);
        return tl;
    }


    private void showNotification(String title, String message) {
        if (trayIcon != null) {
            trayIcon.showWarningMessage(title, message);
        }
    }

    private String buildMessage(String stage, String taskName) {
        switch (stage) {
            case "Overdue" -> { return taskName + " is overdue!"; }
            case "1min"    -> { return taskName + " is due in 1 minute! Lock in."; }
            case "10min"   -> { return taskName + " is due in 10 minutes! Lock in."; }
            case "30min"   -> { return taskName + " is due in 30 minutes! Lock in."; }
            case "1hr"     -> { return taskName + " is due in 1 hour! Lock in."; }
            case "5hr"     -> { return taskName + " is due in 5 hours! Lock in."; }
            case "24hr"    -> { return taskName + " is due in 24 hours! Lock in."; }
            default        -> { return taskName + " is due soon!"; }
        }
    }

    // -------------------------------------------------------------------------
    // Clock
    // -------------------------------------------------------------------------
    public ImageView getSun() {
        ImageView sun = new ImageView(themeManager.isDarkMode() ? whiteSunImg : sunImg);
        sun.setFitWidth(65);
        sun.setFitHeight(65);
        sun.setPreserveRatio(false);
        // Moved down slightly compared to before
        HBox.setMargin(sun, new Insets(4, 0, 0, 0));
        return sun;
    }

    public ImageView getMoon() {
        ImageView moon = new ImageView(themeManager.isDarkMode() ? whiteMoonImg : moonImg);
        moon.setFitWidth(55);
        moon.setFitHeight(55);
        moon.setPreserveRatio(false);
        // Moved down slightly compared to before
        HBox.setMargin(moon, new Insets(4, 0, 0, 0));
        return moon;
    }

    public Timeline dateLine(Label timeLabel, ImageView iconView, HBox timeLabelBox) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("M/d/yyyy h:mm:ss a");

        timeLabel.setMinWidth(Region.USE_PREF_SIZE);
        timeLabel.setPrefWidth(Region.USE_COMPUTED_SIZE);
        HBox.setHgrow(timeLabel, Priority.ALWAYS);
        timeLabelBox.setClip(null);

        // Store as field so cleanup() can remove it — prevents listener accumulation
        themeListener = () -> {
            int hour = LocalDateTime.now().getHour();
            if (hour >= 18 || hour < 6) {
                iconView.setImage(themeManager.isDarkMode() ? whiteMoonImg : moonImg);
            } else {
                iconView.setImage(themeManager.isDarkMode() ? whiteSunImg : sunImg);
            }
        };
        themeManager.addThemeChangeListener(themeListener);

        Timeline clock = new Timeline(new KeyFrame(Duration.seconds(1), e -> {
            timeLabel.setText(LocalDateTime.now().format(formatter));
            int hour = LocalDateTime.now().getHour();
            if (hour >= 18 || hour < 6) {
                iconView.setImage(themeManager.isDarkMode() ? whiteMoonImg : moonImg);
                iconView.setFitWidth(55);
                iconView.setFitHeight(55);
                timeLabelBox.setTranslateY(-5);
            } else {
                iconView.setImage(themeManager.isDarkMode() ? whiteSunImg : sunImg);
                iconView.setFitWidth(65);
                iconView.setFitHeight(65);
                timeLabelBox.setTranslateY(-10);
            }
        }));

        clock.setCycleCount(Timeline.INDEFINITE);
        return clock;
    }

    public void cleanup() {
        if (clockTimeline != null) {
            clockTimeline.stop();
            clockTimeline = null;
        }

        if (countdownTimeline != null) {
            countdownTimeline.stop();
            countdownTimeline = null;
        }



        lastNotifiedStage.clear();
        currentTaskListUi = null;
        currentCalendar = null;
        createTaskUi = null;
        createOverlay = null;
        rootStack.getChildren().clear();
    }

    // Called by Settings when theme or data changes so the UI refreshes
    public void updateRun() {
        if (currentTaskListUi != null) currentTaskListUi.refreshTaskList();
        if (currentCalendar != null) currentCalendar.updateCalender();
        lastNotifiedStage.clear();
        System.gc();
    }
    public void createTaskVisabitly(){
        if(Createtasktext.isVisible()){
            Createtasktext.setVisible(false);
        }else{
            Createtasktext.setVisible(true);
        }
    }
}