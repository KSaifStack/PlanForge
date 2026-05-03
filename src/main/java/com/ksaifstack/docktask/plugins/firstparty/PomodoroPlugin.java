package com.ksaifstack.docktask.plugins.firstparty;

import com.ksaifstack.docktask.plugins.MenuPlugin;
import com.ksaifstack.docktask.plugins.WidgetPlugin;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.util.Duration;

public class PomodoroPlugin implements MenuPlugin, WidgetPlugin {

    private int timeRemainingSeconds = 25 * 60; // 25 mins
    private boolean isRunning = false;
    private Timeline timeline;
    private Label timeLabel;

    @Override
    public String getName() {
        return "Pomodoro Timer";
    }

    @Override
    public String getVersion() {
        return "1.0.0";
    }

    @Override
    public String getDescription() {
        return "A simple focus timer widget and menu settings.";
    }

    @Override
    public Pane getMenuContent(com.ksaifstack.docktask.plugins.PluginContext context) {
        Pane pane = new Pane();
        pane.setPrefSize(400, 300);

        Label title = new Label("Pomodoro Settings");
        title.setFont(context.getFont(32));
        title.setLayoutX(20);
        title.setLayoutY(20);

        Label info = new Label("Future feature: Adjust work and break times here.");
        info.setFont(context.getFont(14));
        info.setLayoutX(20);
        info.setLayoutY(80);

        pane.getChildren().addAll(title, info);
        return pane;
    }

    @Override
    public javafx.scene.layout.Region getWidgetContent(com.ksaifstack.docktask.plugins.PluginContext context) {
        VBox box = new VBox(10);
        box.getStyleClass().add("pomodoro-box");
        context.loadPluginCss(box, "/plugins/firstparty/pomodoro.css");
        box.setPrefSize(120, 80);

        timeLabel = new Label("25:00");
        timeLabel.setFont(context.getFont(24));
        
        Button toggleBtn = new Button("Start");
        toggleBtn.setFont(context.getFont(12));
        toggleBtn.setOnAction(e -> {
            if (isRunning) {
                timeline.stop();
                toggleBtn.setText("Start");
                isRunning = false;
            } else {
                startTimer();
                toggleBtn.setText("Stop");
                isRunning = true;
            }
        });

        box.getChildren().addAll(timeLabel, toggleBtn);
        return box;
    }

    private void startTimer() {
        if (timeline != null) timeline.stop();
        timeline = new Timeline(new KeyFrame(Duration.seconds(1), e -> {
            if (timeRemainingSeconds > 0) {
                timeRemainingSeconds--;
                int minutes = timeRemainingSeconds / 60;
                int seconds = timeRemainingSeconds % 60;
                timeLabel.setText(String.format("%02d:%02d", minutes, seconds));
            } else {
                timeline.stop();
                isRunning = false;
                timeLabel.setText("00:00");
            }
        }));
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
    }

    @Override
    public double getDefaultX() {
        return 700;
    }

    @Override
    public double getDefaultY() {
        return 300;
    }

    @Override
    public double getDefaultSize() {
        return 120;
    }
}
