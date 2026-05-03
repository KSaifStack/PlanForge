package com.ksaifstack.docktask.ui;

import com.ksaifstack.docktask.plugins.DockTaskPlugin;
import com.ksaifstack.docktask.plugins.MenuPlugin;
import com.ksaifstack.docktask.plugins.PluginManager;
import com.ksaifstack.docktask.plugins.WidgetPlugin;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;

public class PluginUi {
    private final String username;

    public PluginUi(String username) {
        this.username = username;
    }

    public Pane getContent(Font lexend14, Font lexend32) {
        Pane pane = new Pane();
        pane.setPrefSize(980, 493);
        pane.getStyleClass().add("plugin-pane");

        Region background = new Region();
        background.setLayoutX(165);
        background.setLayoutY(10);
        background.setPrefSize(605, 372);
        background.getStyleClass().add("plugin-background");
        pane.getChildren().add(background);

        // Auto-sizing, centered title button — text never truncates
        Button pluginLabel = new Button("Plugins");
        pluginLabel.setFont(lexend32);
        pluginLabel.setMaxWidth(580);
        pluginLabel.setLayoutY(17);
        // Re-center horizontally within the background panel whenever text/size changes
        pluginLabel.layoutBoundsProperty().addListener((obs, oldVal, newVal) -> {
            pluginLabel.setLayoutX(165 + (605 - newVal.getWidth()) / 2.0);
        });
        pane.getChildren().add(pluginLabel);

        // Single back button — hidden when viewing a plugin menu, restored on back
        Button back = new Button("Back");
        back.setPrefSize(73, 60);
        back.setFont(lexend14);
        back.setLayoutX(172);
        back.setLayoutY(317);
        back.setOnAction(e -> pane.setVisible(false));
        pane.getChildren().add(back);

        VBox pluginList = new VBox(10);
        pluginList.setPadding(new Insets(10));

        ScrollPane scrollPane = new ScrollPane(pluginList);
        scrollPane.setLayoutX(175);
        scrollPane.setLayoutY(85);
        scrollPane.setPrefSize(585, 220);
        scrollPane.getStyleClass().add("plugin-scroll");
        pane.getChildren().add(scrollPane);

        for (DockTaskPlugin plugin : PluginManager.getPlugins()) {
            VBox card = new VBox(5);
            card.getStyleClass().add("plugin-card");

            Label nameLabel = new Label(plugin.getName() + " (v" + plugin.getVersion() + ")");
            nameLabel.setFont(Font.font("System Bold", 16));

            Label descLabel = new Label(plugin.getDescription());
            descLabel.setFont(lexend14);
            descLabel.setWrapText(true);

            HBox buttonBox = new HBox(10);

            if (plugin instanceof MenuPlugin) {
                Button openMenuBtn = new Button("Open Menu");
                openMenuBtn.setFont(lexend14);
                openMenuBtn.setOnAction(e -> {
                    com.ksaifstack.docktask.plugins.PluginContext context =
                            new com.ksaifstack.docktask.plugins.PluginContextImpl(username, plugin.getName());
                    Pane menuContent = ((MenuPlugin) plugin).getMenuContent(context);
                    menuContent.setLayoutX(175);
                    menuContent.setLayoutY(85);

                    // Hide the list and the original back button; update title
                    scrollPane.setVisible(false);
                    back.setVisible(false);
                    pluginLabel.setText(plugin.getName());
                    // Re-centering is triggered automatically by layoutBoundsProperty listener

                    Button menuBack = new Button("Back");
                    menuBack.setFont(lexend14);
                    menuBack.setPrefSize(73, 60);
                    menuBack.setLayoutX(172);
                    menuBack.setLayoutY(317);
                    menuBack.setOnAction(b -> {
                        pane.getChildren().removeAll(menuContent, menuBack);
                        scrollPane.setVisible(true);
                        back.setVisible(true);
                        pluginLabel.setText("Plugins");
                    });

                    pane.getChildren().addAll(menuContent, menuBack);
                });
                buttonBox.getChildren().add(openMenuBtn);
            }

            if (plugin instanceof WidgetPlugin) {
                com.ksaifstack.docktask.plugins.PluginContext ctx =
                        new com.ksaifstack.docktask.plugins.PluginContextImpl(username, plugin.getName());

                boolean currentlyEnabled = Boolean.parseBoolean(ctx.loadState("widget.enabled", "true"));

                Button toggleBtn = new Button(currentlyEnabled ? "Hide Widget" : "Show Widget");
                toggleBtn.setFont(lexend14);
                toggleBtn.setOnAction(e -> {
                    boolean nowEnabled = Boolean.parseBoolean(ctx.loadState("widget.enabled", "true"));
                    boolean newState = !nowEnabled;
                    ctx.saveState("widget.enabled", String.valueOf(newState));
                    toggleBtn.setText(newState ? "Hide Widget" : "Show Widget");
                    com.ksaifstack.docktask.plugins.PluginManager.notifyWidgetVisibilityChanged();
                });
                buttonBox.getChildren().add(toggleBtn);
            }

            card.getChildren().addAll(nameLabel, descLabel, buttonBox);
            pluginList.getChildren().add(card);
        }

        return pane;
    }
}
