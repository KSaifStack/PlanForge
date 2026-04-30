// taskListUi.java
package com.ksaifstack.docktask.ui;

import com.ksaifstack.docktask.model.UserData;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

import static com.ksaifstack.docktask.util.FontLoader.setFont;

public class taskListUi {

    public static class TaskEntry {
        public final Button backgroundButton;
        public final Label dueLabel;
        public final Label warningLabel;
        public final String taskName;
        public LocalDateTime dueTime;

        TaskEntry(Button backgroundButton, Label dueLabel, Label warningLabel, String taskName, LocalDateTime dueTime) {
            this.backgroundButton = backgroundButton;
            this.dueLabel = dueLabel;
            this.warningLabel = warningLabel;
            this.taskName = taskName;
            this.dueTime = dueTime;
        }
    }

    private VBox taskListContainer;
    private final List<TaskEntry> taskEntries = new ArrayList<>();
    private String username;
    private Consumer<Pane> onShowOverlay;
    private Runnable onTaskUpdated;
    private ScrollPane scrollPane;
    private StackPane rootStack = new StackPane();

    public taskListUi(String username, Consumer<Pane> onShowOverlay, StackPane rootStack ,Runnable onTaskUpdated) {
        this.username = username;
        this.onShowOverlay = onShowOverlay;
        this.onTaskUpdated = onTaskUpdated;
        this.rootStack = rootStack;
    }

    public List<TaskEntry> getTaskEntries() {
        return taskEntries;
    }

    public StackPane TaskList(String[][] tasksArr) {
        StackPane taskContainerWrapper = new StackPane();
        taskContainerWrapper.setPrefSize(208, 359);
        taskContainerWrapper.setMinSize(208, 359);

        taskListContainer = new VBox(10);
        taskListContainer.setPrefWidth(208);
        taskListContainer.setPadding(new Insets(5, 0, 5, 0));

        refreshTaskList();

        scrollPane = new ScrollPane(taskListContainer);
        scrollPane.setPrefSize(208, 359);
        scrollPane.setMinSize(208, 359);
        scrollPane.setMaxSize(208, 359);
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(false);
        scrollPane.setPannable(false);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setStyle("-fx-border-color: #626262; -fx-border-radius: 2px; -fx-border-width: 1px;");

        if (tasksArr.length > 3) {
            scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        }

        taskContainerWrapper.getChildren().add(scrollPane);
        return taskContainerWrapper;
    }

    public void updateScrollPolicy() {
        if (scrollPane == null) return;
        String[][] tasks = UserData.ReturnData(username);
        if (tasks != null && tasks.length > 3) {
            scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        } else {
            scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        }
    }

    public void refreshTaskList() {
        if (taskListContainer != null) {
            taskListContainer.getChildren().clear();
        } else {
            taskListContainer = new VBox(10);
        }
        taskEntries.clear();

        String[][] tasks = UserData.ReturnData(username);
        if (tasks == null || tasks.length == 0) {
            Label noTask = new Label("No Tasks found.");
            Label noTask2 = new Label("Press + to add a task!");
            noTask.setFont(setFont(14));
            noTask2.setFont(setFont(14));
            noTask.setTranslateX(50);
            noTask2.setTranslateX(30);
            noTask2.setTranslateY(-15);
            noTask.setPrefSize(208, 50);
            noTask2.setPrefSize(208, 0);
            taskListContainer.getChildren().addAll(noTask, noTask2);
            return;
        }

        Arrays.sort(tasks, (a, b) -> {
            int groupCompare = b[3].compareToIgnoreCase(a[3]);
            if (groupCompare != 0) return groupCompare;
            int pa = Integer.parseInt(a[2]);
            int pb = Integer.parseInt(b[2]);
            return Integer.compare(pb, pa);
        });

        for (String[] task : tasks) {
            Button btn = buildTaskButton(task);
            taskListContainer.getChildren().add(btn);
        }

        updateScrollPolicy();
    }

    private Button buildTaskButton(String[] task) {
        Label group = new Label(task[3]);
        group.setFont(setFont(12));
        group.setTranslateX(-10);

        Label duetime = new Label("----");
        duetime.setFont(setFont(12));

        Label warning = new Label("----");
        warning.setFont(setFont(12));

        Label name = new Label(task[0]);
        name.setFont(setFont(12));

        LocalDateTime timec = UserData.DataCheckerUI(task[0]);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        HBox topRow = new HBox(group, spacer, duetime);
        topRow.setAlignment(Pos.CENTER_LEFT);
        topRow.setTranslateY(-10);

        warning.setTranslateY(10);
        Region spacer2 = new Region();
        HBox.setHgrow(spacer2, Priority.ALWAYS);
        HBox bottomRow = new HBox(spacer2, warning);
        bottomRow.setAlignment(Pos.CENTER_RIGHT);

        VBox content = new VBox(4, topRow, name, bottomRow);
        content.setPrefHeight(69);
        content.setPrefSize(184, 69);
        content.setPadding(new Insets(8));
        content.setAlignment(Pos.TOP_LEFT);

        Button background = new Button();
        background.setPrefHeight(69);
        background.setPrefSize(184, 69);
        background.setGraphic(content);

        String bgColor = GroupGiver(task[3]);
        if (timec.isBefore(LocalDateTime.now())) {
            bgColor = "#FF4C4C";
        }
        background.setStyle("-fx-border-color: " + bgColor + ";");

        TaskEntry entry = new TaskEntry(background, duetime, warning, task[0], timec);
        taskEntries.add(entry);

        background.setOnMouseClicked((MouseEvent e) -> {
            final Pane[] updateOverlay = new Pane[1];
            UpdateTaskUi updateTaskUi = new UpdateTaskUi(username, task[0], () -> {
                refreshTaskList();
                if (onTaskUpdated != null) onTaskUpdated.run();
                rootStack.getChildren().remove(updateOverlay[0]);
            });
            updateOverlay[0] = updateTaskUi.getContent();
            onShowOverlay.accept(updateOverlay[0]);
        });
        String[][] dtasks = UserData.ReturnData(username);
        if (dtasks.length > 3) {
            background.setTranslateX(3);
        } else {
            background.setTranslateX(10);
        }

        return background;
    }

    private String GroupGiver(String groupName) {
        String[] colors = {
                "#FF8C00",
                "#228B22",
                "#4169E1",
                "#2F4F4F",
                "#D2691E",
                "#20B2AA",
                "#FFD700",
        };
        int index = Math.abs(groupName.toLowerCase().hashCode()) % colors.length;
        return colors[index];
    }
}