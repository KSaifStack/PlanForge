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
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

import java.awt.*;
import java.io.InputStream;
import javafx.scene.input.MouseEvent;

import java.time.LocalDateTime;
import java.time.Month;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.List;
// Main ui page
/**
 * This page holds all data for
 * blah blah blah
 * blah blah blah
 */

public class TaskUi {
    private final StackPane rootStack = new StackPane();
    Font lexend14 = Font.loadFont(getClass().getResourceAsStream("/fonts/Lexend.ttf"), 14);
    Font lexend12 = Font.loadFont(getClass().getResourceAsStream("/fonts/Lexend.ttf"), 12);
    Font lexend16 = Font.loadFont(getClass().getResourceAsStream("/fonts/Lexend.ttf"), 16);
    Font lexend32 = Font.loadFont(getClass().getResourceAsStream("/fonts/Lexend.ttf"), 32);
    Font lexend30 = Font.loadFont(getClass().getResourceAsStream("/fonts/Lexend.ttf"), 20);
    private String username;
    private FXTrayIcon trayIcon;
    private Label Createtasktext = new Label("(Create Task)");
    private final Map<String, String> lastNotifiedStage = new HashMap<>();
    private final CalendarUi calendarUi = new CalendarUi();
    private final AppTray tray = AppTray.getInstance();

    // UI state
    private VBox taskListContainer;
    private Timeline clockTimeline;
    private Timeline globalCountdownTimeline;
    private Stage primaryStage;
    private CreateTaskUi createTaskUi;
    private Pane createOverlay;
    private final Image sunImg = new Image(getClass().getResourceAsStream("/images/sun.png"));
    private final Image moonImg = new Image(getClass().getResourceAsStream("/images/moon.png"));
    private final Image whiteSunImg = new Image(getClass().getResourceAsStream("/images/whiteSun.png"));
    private final Image whiteMoonImg = new Image(getClass().getResourceAsStream("/images/whiteMoon.png"));



    private static class TaskEntry  {
        final Button backgroundButton;
        final Label dueLabel;
        final Label warningLabel;
        final String taskName;
        LocalDateTime dueTime;


        TaskEntry(Button backgroundButton, Label dueLabel, Label warningLabel, String taskName, LocalDateTime dueTime) {
            this.backgroundButton = backgroundButton;
            this.dueLabel = dueLabel;
            this.warningLabel = warningLabel;
            this.taskName = taskName;
            this.dueTime = dueTime;
        }
    }

    private final List<TaskEntry> visibleTasks = new ArrayList<>();


    public TaskUi(String username) {
        this.username = username;
    }



    public void start(WindowActions window, LoginUi loginUi) {
        setupUI(window, window.getScene(),loginUi);
    }
    public void start(Stage primaryStage, LoginUi loginUi) {
        WindowBorder windowBorder = new WindowBorder("DockTask - Home", rootStack, 980, 493);
        setupUI(windowBorder, windowBorder.getScene(),loginUi);
        windowBorder.show();
    }



    public void setupUI(WindowActions window, Scene scene,LoginUi loginUi) {



        Pane pane = new Pane();
        window.setTitleLabel(" ");
        window.colorChange(" ");
        pane.setPrefSize(980, 493);
        tray.setup((Stage) window, "DockTask", "/images/logo.png");
        trayIcon = tray.getTrayIcon();

        calendarUi.setCalendar(username);
        Pane calander = calendarUi.getPane();

        Platform.setImplicitExit(false);
        window.setOnCloseRequest(event -> {
            event.consume();
            Platform.runLater(window::hide);
        });
        rootStack.getChildren().clear();
        rootStack.getChildren().addAll(pane);

        StackPane taskContainerWrapper = new StackPane();
        taskContainerWrapper.setLayoutX(12);
        taskContainerWrapper.setLayoutY(66);
        taskContainerWrapper.setPrefSize(208, 359);

        taskListContainer = new VBox(10);
        taskListContainer.setLayoutY(100);
        taskListContainer.setLayoutX(12.0);
        taskListContainer.setPrefWidth(220);
        taskListContainer.setPrefHeight(359);
        taskListContainer.setPadding(new Insets(5, 0, 5, 0));

        // Load tasks and populate the UI
        refreshTaskList();

        ScrollPane scrollPane = new ScrollPane(taskListContainer);
        scrollPane.setPrefSize(208, 359);
        scrollPane.setMaxSize(208, 359);
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(true);
        scrollPane.setPannable(true);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setStyle("-fx-border-color: #626262; -fx-border-radius: 2px; -fx-border-width: 1px;");

        String[][] tasksArr = UserData.ReturnData(username);
        if (tasksArr.length > 3) {
            scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        }
        scrollPane.setPannable(false);

        taskContainerWrapper.getChildren().addAll(scrollPane);
        pane.getChildren().add(taskContainerWrapper);

        // Welcome label
        String firstWelcome = username != null && username.length() > 0 ? username.substring(0, 1).toUpperCase() : "";
        String secondWelcome = username != null && username.length() > 1 ? username.substring(1) : "";
        Label Welcome = new Label("Welcome " + firstWelcome + secondWelcome + "!");
        //Used to be 14
        Welcome.setLayoutX(45);
        Welcome.setLayoutY(14);
        Welcome.setPrefWidth(158);
        Welcome.setPrefHeight(33);
        Welcome.setFont(lexend16);
        pane.getChildren().add(Welcome);

        Button lobutton = new Button("Logout");
        lobutton.setFont(lexend12);
        lobutton.setLayoutX(13.19);
        lobutton.setLayoutY(451.50);
        lobutton.setPrefWidth(67.00);
        lobutton.setPrefHeight(30.00);
        pane.getChildren().add(lobutton);
        Scene finalScene = scene;
        lobutton.setOnAction(e -> {
            System.out.println("Logout Button was pressed.");
            loginUi.showBack((Stage)window);
            window.setHeight(378);
            window.setWidth(766);
            WindowBorder.logOut(finalScene);
            cleanup(true);
        });

        pane.getChildren().add(calander);

        Button SettingsIcon = new Button("Settings");
        SettingsIcon.setFont(lexend14);
        SettingsIcon.setLayoutX(893.00);
        SettingsIcon.setLayoutY(4.46);
        SettingsIcon.setPrefWidth(76.00);
        SettingsIcon.setPrefHeight(30.00);
        pane.getChildren().add(SettingsIcon);
        SettingsIcon.setOnAction(e -> {
            System.out.println("Settings Button was pressed.");
            Settings Settings = new Settings(username);
            TaskUi self = this;
            Pane SettingPane = Settings.getContent(lexend14, lexend32,window,self);
            pane.getChildren().add(SettingPane);
        });

        Button PlButton = new Button("Plugins");
        PlButton.setLayoutX(819.00);
        PlButton.setLayoutY(4.46);
        PlButton.setPrefWidth(70.00);
        PlButton.setPrefHeight(30.00);
        //Removed until api calls and whatnot are worked on
        //pane.getChildren().add(PlButton);
        PlButton.setOnAction(e -> {
            System.out.println("Plugin Button was pressed.");
            Plugins Plugins = new Plugins(username);
            Pane pluginPane = Plugins.getContent(lexend14, lexend32);
            pane.getChildren().add(pluginPane);
        });

        Button createtaskb = new Button("+");
        createtaskb.setFont(Font.font(60));
        createtaskb.setLayoutX(775);
        createtaskb.setLayoutY(140.00);
        createtaskb.setPrefWidth(150);
        createtaskb.setPrefHeight(150);
        pane.getChildren().add(createtaskb);

        createtaskb.setOnAction(e -> {
            if (createTaskUi == null) {
                createTaskUi = new CreateTaskUi(username, () -> {
                    refreshTaskList();
                    calendarUi.updateCal();
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


        Createtasktext.setFont(lexend14);
        Createtasktext.setLayoutX(805);
        Createtasktext.setLayoutY(300);
        pane.getChildren().add(Createtasktext);

        // Clock + sun/moon
        HBox timeLabelBox = new HBox();
        timeLabelBox.setLayoutX(255.00);
        timeLabelBox.setPrefWidth(455);
        timeLabelBox.setLayoutY(8);
        timeLabelBox.setAlignment(Pos.CENTER);
        Label timeLabel = new Label(LocalDateTime.now().format(DateTimeFormatter.ofPattern("M/d/yyyy h:mm:ss a")));
        LocalDateTime current = LocalDateTime.now();
        int currentHour = current.getHour();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("M/d/yyyy h:mm:ss a");


        ImageView sun = new ImageView();
        ImageView moon = new ImageView();
        ImageView iconView = new ImageView();
        iconView.setPreserveRatio(false);

        if (themeManager.isDarkMode()) {
            sun.setImage(whiteSunImg);
            moon.setImage(whiteMoonImg);
        } else {
            sun.setImage(sunImg);
            moon.setImage(moonImg);
        }

        themeManager.addThemeChangeListener(() -> {
            if (themeManager.isDarkMode()) {
                sun.setImage(whiteSunImg);
                moon.setImage(whiteMoonImg);
            } else {
                sun.setImage(sunImg);
                moon.setImage(moonImg);
            }
            int hour = LocalDateTime.now().getHour();
            if (hour >= 18 || hour < 6) {
                iconView.setImage(moon.getImage());
                iconView.setFitWidth(55);
                iconView.setFitHeight(55);
                iconView.setTranslateY(-2);
            } else {
                iconView.setImage(sun.getImage());
                iconView.setFitWidth(65);
                iconView.setFitHeight(65);
                iconView.setTranslateY(-4);
            }

        });

        // sun and moon sizes revamped

        sun.setFitWidth(65);
        sun.setFitHeight(65);
        sun.setPreserveRatio(false);

        moon.setFitWidth(55);
        moon.setFitHeight(55);
        moon.setPreserveRatio(false);

        HBox.setMargin(sun, new Insets(-9, 0, 0, 0));
        HBox.setMargin(moon, new Insets(-7, 0, 0, 0));


        int x = timeLabel.getText().length();
        timeLabel.setPrefWidth(351);
        timeLabel.setPrefHeight(45);
        timeLabel.setFont(lexend32);
        if(x<21){
            timeLabel.setTranslateX(20);
        }

        timeLabelBox.getChildren().add(timeLabel);


        if (currentHour >= 18|| currentHour < 6) {
            timeLabelBox.setSpacing(2);
            iconView.setImage(moon.getImage());
            iconView.setTranslateY(-4);
            iconView.setFitWidth(55);
            iconView.setFitHeight(55);
            timeLabelBox.setTranslateY(-5);
        } else {
            iconView.setImage(sun.getImage());
            iconView.setTranslateY(-4);
            iconView.setFitWidth(65);
            iconView.setFitHeight(65);
            timeLabelBox.setTranslateY(-10);
            timeLabelBox.setTranslateX(5);
        }
        timeLabelBox.getChildren().add(iconView);
        clockTimeline = new Timeline(new KeyFrame(Duration.seconds(1), e -> {
            String[][] tasks = UserData.ReturnData(username);
            if (scrollPane != null) {
                if (tasks.length > 3) {
                    scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
                } else {
                    scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
                }
            }
            timeLabel.setText(LocalDateTime.now().format(formatter));
            int hour = LocalDateTime.now().getHour();
            if (timeLabel.getText().length()>21) {
                timeLabel.setPrefWidth(380);
                timeLabel.setTranslateX(-5);
            }
            else if(timeLabel.getText().length()==21){
                timeLabel.setPrefWidth(351);
                iconView.setTranslateX(5);
            }
            else {
                timeLabel.setPrefWidth(351);
                timeLabel.setTranslateX(15);
            }
            if (hour >= 18 || hour < 6) {
                iconView.setImage(moon.getImage());
                iconView.setFitWidth(55);
                iconView.setFitHeight(55);
                iconView.setTranslateY(-4);
                timeLabelBox.setTranslateY(-5);
            } else {
                iconView.setImage(sun.getImage());
                iconView.setFitWidth(65);
                iconView.setFitHeight(65);
                iconView.setTranslateY(-4);
                timeLabelBox.setTranslateY(-10);

            }


        }));
        clockTimeline.setCycleCount(Timeline.INDEFINITE);
        clockTimeline.play();

        pane.getChildren().add(timeLabelBox);


        pane.setPrefSize(980, 493);
        scene = window.getScene();
        window.setContent(rootStack);
        window.setTitle("DockTask - Home");
        window.setWidth(990);
        window.setHeight(531);
        scene.addEventFilter(KeyEvent.KEY_PRESSED, ev -> {
            if (ev.isControlDown() && ev.isShiftDown() && ev.getCode() == KeyCode.C) {
                if (createTaskUi == null || !createTaskUi.isOpen()) {
                    createtaskb.fire();
                }
                ev.consume();
            }
        });

        scene.getStylesheets().clear();
        String pick=UserData.importTheme(username);

        boolean shouldBeDark = pick.equals("Dark");

        scene.getStylesheets().clear();
        String cssFile = shouldBeDark ? "/css/DarkTheme.css" : "/css/LightTheme.css";
        scene.getStylesheets().add(getClass().getResource(cssFile).toExternalForm());

        if (themeManager.isDarkMode() != shouldBeDark) {
            themeManager.changeTheme();
        }

        themeManager.setScene(scene);


        //primaryStage.setScene(scene);
        //primaryStage.show();
        window.show();

        startGlobalCountdown();


        //Stops app from building up unwanted memory while idle.
        window.iconifiedProperty().addListener((obs, wasIcon, isIcon) -> {
            if (globalCountdownTimeline != null) {
                if (isIcon) globalCountdownTimeline.pause();
                else globalCountdownTimeline.play();
            }
            if (clockTimeline != null) {
                if (isIcon) clockTimeline.pause();
                else clockTimeline.play();
            }
        });

    }


    public void updateRun(){
        visibleTasks.clear();
        lastNotifiedStage.clear();
        refreshTaskList();
        calendarUi.updateCal();
        System.gc();
    }
    private void refreshTaskList() {
        // Clear UI and data
        if (taskListContainer != null) {
            taskListContainer.getChildren().clear();
        } else {
            taskListContainer = new VBox(10);
        }
        visibleTasks.clear();

        String[][] tasks = UserData.ReturnData(username);
        if (tasks == null || tasks.length == 0) {
            Label noTask = new Label("No Tasks found.");
            Label noTask2 = new Label("Press + to add a task!");
            noTask2.setFont(lexend14);
            noTask.setFont(lexend14);
            noTask.setTranslateX(50);
            noTask2.setTranslateX(30);
            noTask2.setTranslateY(-15);
            noTask.setPrefSize(208, 50);
            noTask2.setPrefSize(208,0);
            taskListContainer.getChildren().add(noTask);
            taskListContainer.getChildren().add(noTask2);
            return;
        }

        // Sorts out tasks based off group
        Arrays.sort(tasks, (a, b) -> {
            int groupCompare = b[3].compareToIgnoreCase(a[3]);
            if (groupCompare != 0) return groupCompare;
            int pa = Integer.parseInt(a[2]);
            int pb = Integer.parseInt(b[2]);
            return Integer.compare(pb, pa);
        });


        for (String[] task : tasks) {
            Button pane = createTaskPane(task);
            taskListContainer.getChildren().add(pane);
        }

    }

    private Button createTaskPane(String[] task) {
        Label group = new Label(task[3]);
        group.setFont(lexend12);
        group.setTranslateX(-10);
        Label duetime = new Label("----");
        duetime.setFont(lexend12);
        Label warning = new Label("----");
        warning.setFont(lexend12);
        Label name = new Label(task[0]);
        name.setFont(lexend12);

        LocalDateTime timec = UserData.DataCheckerUI(task[0]);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        HBox topRow = new HBox(group, spacer, duetime);
        topRow.setPrefWidth(184);
        topRow.setAlignment(Pos.CENTER_LEFT);
        topRow.setTranslateY(-10);

        warning.setTranslateY(10);
        Region spacer2 = new Region();
        HBox.setHgrow(spacer2, Priority.ALWAYS);
        HBox bottomRow = new HBox(spacer2, warning);
        bottomRow.setAlignment(Pos.CENTER_RIGHT);

        VBox content = new VBox(4, topRow, name, bottomRow);
        content.setPrefSize(184, 69);
        content.setPadding(new Insets(8));
        content.setAlignment(Pos.TOP_LEFT);

        Button background = new Button();
        background.setPrefSize(184, 69);

        String bgColor = GroupGiver(task[3]);
        if (timec.isBefore(LocalDateTime.now())) {
            bgColor = "#FF4C4C";
        }
        background.setStyle("-fx-border-color:" + bgColor + "; ");

        String[][] dtasks = UserData.ReturnData(username);
        if (dtasks.length > 3) {
            background.setTranslateX(3);
        } else {
            background.setTranslateX(10);
        }

        background.setGraphic(content);

        background.setOnMouseClicked((MouseEvent e) -> {
            final Pane[] updateOverlay = new Pane[1];
            UpdateTaskUi updateTaskUi = new UpdateTaskUi(username, task[0], () -> {
                rootStack.getChildren().remove(updateOverlay[0]);
                refreshTaskList();
                calendarUi.updateCal();
            });
            updateOverlay[0] = updateTaskUi.getContent();
            rootStack.getChildren().add(updateOverlay[0]);
        });

        TaskEntry entry = new TaskEntry(background, duetime, warning, task[0], timec);
        visibleTasks.add(entry);

        return background;
    }

    private void startGlobalCountdown() {
        if (globalCountdownTimeline != null) {
            globalCountdownTimeline.stop();
        }

        globalCountdownTimeline = new Timeline(new KeyFrame(Duration.seconds(1), event -> {
            LocalDateTime now = LocalDateTime.now();

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

            for (TaskEntry entry : visibleTasks) {
                LocalDateTime due = entry.dueTime;
                long hoursUntilDue = now.until(due,ChronoUnit.HOURS);
                long secondsUntilDue = now.until(due, ChronoUnit.SECONDS);
                if(hoursUntilDue>168){
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("M/d/yyyy");
                    entry.dueLabel.setText(String.format(due.format(formatter)));
                }
                else if (secondsUntilDue > 0) {
                    long hours = secondsUntilDue / 3600;
                    long minutes = (secondsUntilDue % 3600) / 60;
                    long seconds = secondsUntilDue % 60;
                    entry.dueLabel.setText(String.format("%02d:%02d:%02d", hours, minutes, seconds));
                } else {
                    entry.dueLabel.setText("00:00:00");
                }

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

                String taskKey = entry.taskName + "_" + due.toString();
                if (stage != null && !stage.equals(lastNotifiedStage.get(taskKey))) {
                    lastNotifiedStage.put(taskKey, stage);
                    showNotification("DockTask - " + entry.taskName, buildMessage(stage, entry.taskName));
                }
            }
        }));

        globalCountdownTimeline.setCycleCount(Timeline.INDEFINITE);
        globalCountdownTimeline.play();
    }

    public void createTaskVisabitly(){
        if(Createtasktext.isVisible()){
            Createtasktext.setVisible(false);
        }else{
            Createtasktext.setVisible(true);
        }
    }
    private String GroupGiver(String groupName) {
        String[] colors = {
                "#FF8C00", // Dark Orange
                "#228B22", // Forest Green
                "#4169E1", // Royal Blue
                "#2F4F4F", // Dark Slate Gray
                "#D2691E", // Chocolate
                "#20B2AA", // Light Sea Green
                "#FFD700", // Gold
        };
        int index = Math.abs(groupName.toLowerCase().hashCode()) % colors.length;
        return colors[index];
    }

    private void showNotification(String title, String message) {
        if (trayIcon != null) {
            trayIcon.showWarningMessage(title, message);
        }
    }

    private String buildMessage(String stage, String taskname) {
        switch (stage) {
            case "Overdue" -> {
                return taskname + " is overdue!";
            }
            case "1min" -> {
                return taskname + " is due in 1 minute! Lock in.";
            }
            case "10min" -> {
                return taskname + " is due in 10 minutes! Lock in.";
            }
            case "30min" -> {
                return taskname + " is due in 30 minutes! Lock in.";
            }
            case "1hr" -> {
                return taskname + " is due in 1 hour! Lock in.";
            }
            case "5hr" -> {
                return taskname + " is due in 5 hours! Lock in.";
            }
            case "24hr" -> {
                return taskname + " is due in 24 hours! Lock in.";
            }
            default -> {
                return taskname + " is due soon!";
            }
        }
    }

    public void cleanup(boolean fullCleanup) {
        if (fullCleanup) {
            if (clockTimeline != null) {
                clockTimeline.stop();
                clockTimeline = null;
            }
            if (globalCountdownTimeline != null) {
                globalCountdownTimeline.stop();
                globalCountdownTimeline = null;
            }
        }
        visibleTasks.clear();
        lastNotifiedStage.clear();
        if (taskListContainer != null) {
            taskListContainer.getChildren().clear();
        }
        rootStack.getChildren().clear();
        createTaskUi = null;
        createOverlay = null;
    }

}

