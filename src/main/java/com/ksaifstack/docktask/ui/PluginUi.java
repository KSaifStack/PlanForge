package com.ksaifstack.docktask.ui;

//Plugin Ui for allowing extra features to be used
//TO BE WORKED ON
import com.ksaifstack.docktask.plugins.DockTaskPlugin;
import com.ksaifstack.docktask.plugins.MenuPlugin;
import com.ksaifstack.docktask.plugins.PluginInstaller;
import com.ksaifstack.docktask.plugins.PluginManager;
import com.ksaifstack.docktask.plugins.PluginRegistryService;
import com.ksaifstack.docktask.plugins.RegistryPlugin;
import com.ksaifstack.docktask.plugins.WidgetPlugin;
import com.ksaifstack.docktask.plugins.PluginContext;
import com.ksaifstack.docktask.plugins.PluginContextImpl;
import com.ksaifstack.docktask.util.FontLoader;
import com.ksaifstack.docktask.util.themeManager;

import javafx.geometry.Pos;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.OverrunStyle;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Window;

import java.io.File;
import java.nio.file.Path;
import java.util.List;
import java.awt.Desktop;
import java.net.URI;

public class PluginUi {
    private final String username;
    private Label pluginName;
    private Button settingsButton;
    private Button widgetToggleButton;
    private Button pluginAuthor;
    private Button pluginVersion;
    private Button pluginType;
    private Button pluginUninstall;
    private Button pluginSource;
    private Button pluginAccess;
    private Label pluginDescription;
    private Label placeholder;
    private VBox infoContent;
    private DockTaskPlugin selectedPlugin;
    private Pane rootPane;
    private final ConfirmationUi confirmation = new ConfirmationUi();

    public PluginUi(String username) {
        this.username = username;
    }

    public Pane getContent() {
        // Allows for grayed out background
        rootPane = new Pane();
        rootPane.setPrefSize(980, 493);
        rootPane.setStyle("-fx-background-color: rgba(0, 0, 0, 0.15);");

        // Main Setting Region
        Region background = new Region();
        background.setLayoutX(165);
        background.setLayoutY(10);
        background.setPrefSize(668, 382);
        background.getStyleClass().add("region");

        rootPane.getChildren().add(background);
        // Title row: "Plugins" + add JAR (+)
        Label titleLabel = new Label("Plugins");
        titleLabel.setFont(FontLoader.setFont(32));

        Button addJarBtn = new Button();
        Tooltip x = new Tooltip();
        x.setFont(FontLoader.setFont(14));
        x.setText("Add local Plugins");
        addJarBtn.setTooltip(x);
        Label plusGlyph = new Label("+");
        plusGlyph.setFont(FontLoader.setFont(28));
        addJarBtn.setGraphic(plusGlyph);
        addJarBtn.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
        addJarBtn.setPadding(Insets.EMPTY);
        addJarBtn.setPrefSize(42, 40);
        addJarBtn.setMinSize(42, 40);
        addJarBtn.setTranslateX(5);
        addJarBtn.setTranslateY(3);
        addJarBtn.setOnAction(e -> openJarFileChooser());

        HBox titleBar = new HBox(10, titleLabel, addJarBtn);
        titleBar.setAlignment(Pos.CENTER_LEFT);
        titleBar.setLayoutX(180);
        titleBar.setLayoutY(14);
        // Panes
        VBox detailPanes = new VBox(10);
        Pane leftPane = getList();
        leftPane.setLayoutX(170);
        leftPane.setLayoutY(65);

        Pane rightPane = getInfo();
        rightPane.setLayoutX(380);
        rightPane.setLayoutY(65);
        // Online plugin button
        Button browseBtn = new Button("Community Plugins");
        browseBtn.setPrefSize(180, 35);
        browseBtn.setFont(FontLoader.setFont(16));
        browseBtn.setLayoutY(20);
        // Right-align within the modal background
        browseBtn.setLayoutX(165 + 668 - 180 - 20);
        // Back
        Button back = new Button("Back");
        back.setFont(FontLoader.setFont(14));
        back.setPrefSize(73, 24);
        back.setLayoutX(172);
        back.setLayoutY(355);
        back.setOnAction(e -> {
            rootPane.setVisible(false);
        });

        rootPane.getChildren().addAll(leftPane, rightPane);
        rootPane.getChildren().addAll(titleBar, detailPanes, browseBtn, back);

        // Community registry view (match this UI layout)
        browseBtn.setOnAction(e -> {
            leftPane.setVisible(false);
            rightPane.setVisible(false);
            browseBtn.setVisible(false);
            addJarBtn.setVisible(false);
            back.setVisible(false);
            titleLabel.setText("Plugin Registry");

            // Registry panes (left list + right info), same sizing as main view
            Pane regLeftPane = new Pane();
            regLeftPane.setId("plugin-background");
            regLeftPane.setPrefSize(200, 280);
            regLeftPane.setLayoutX(170);
            regLeftPane.setLayoutY(65);

            Pane regRightPane = new Pane();
            regRightPane.setId("plugin-background");
            regRightPane.setPrefSize(440, 280);
            regRightPane.setLayoutX(380);
            regRightPane.setLayoutY(65);

            Label regPlaceholder = new Label("Select a plugin to view more options.");
            regPlaceholder.setFont(FontLoader.setFont(16));
            regPlaceholder.setLayoutX(85);
            regPlaceholder.setLayoutY(15);

            Button regBack = new Button("Back");
            regBack.setFont(FontLoader.setFont(14));
            regBack.setPrefSize(73, 24);
            regBack.setLayoutX(172);
            regBack.setLayoutY(355);

            Button regRefresh = new Button("Refresh");
            regRefresh.setFont(FontLoader.setFont(14));
            regRefresh.setPrefSize(100, 24);
            regRefresh.setLayoutX(255);
            regRefresh.setLayoutY(355);

            // Right info (community)
            Label regName = new Label();
            regName.setFont(FontLoader.setFont(18));
            regName.setTextOverrun(OverrunStyle.ELLIPSIS);
            regName.setMaxWidth(290);

            Button installBtn = new Button("Install");
            installBtn.setFont(FontLoader.setFont(14));
            installBtn.setPrefSize(110, 28);
            installBtn.setDisable(true);

            Button githubBtn = new Button("GitHub");
            githubBtn.setFont(FontLoader.setFont(14));
            githubBtn.setPrefSize(90, 28);
            githubBtn.setDisable(true);

            HBox regRightButtons = new HBox(10, installBtn, githubBtn);
            regRightButtons.setAlignment(Pos.CENTER_RIGHT);

            BorderPane regTopRow = new BorderPane();
            regTopRow.setLeft(regName);
            regTopRow.setRight(regRightButtons);
            regTopRow.setPrefWidth(440 - 20);
            regTopRow.setMaxWidth(Double.MAX_VALUE);

            Label regDescTag = new Label("Description");
            regDescTag.setFont(FontLoader.setFont(12));
            regDescTag.setOpacity(0.65);

            Label regDesc = new Label();
            regDesc.setFont(FontLoader.setFont(13));
            regDesc.setWrapText(true);
            regDesc.setPrefWidth(410);
            regDesc.setOpacity(0.9);

            Button regType = infoButton("Type");
            Button regPerms = infoButton("Permissions");
            Button regAuthor = infoButton("Author");
            Button regVersion = infoButton("Version");

            GridPane regCards = new GridPane();
            regCards.setHgap(12);
            regCards.setVgap(12);
            regCards.add(regType, 0, 0);
            regCards.add(regPerms, 1, 0);
            regCards.add(regAuthor, 0, 1);
            regCards.add(regVersion, 1, 1);
            VBox.setMargin(regCards, new Insets(10, 0, 0, 0));
            regCards.setAlignment(Pos.CENTER);

            HBox regCardsHolder = new HBox(regCards);
            regCardsHolder.setAlignment(Pos.CENTER);

            VBox regInfoContent = new VBox(10, regTopRow, regDescTag, regDesc, regCardsHolder);
            regInfoContent.setPadding(new Insets(10));
            regInfoContent.setPrefSize(440, 280);
            regInfoContent.setFillWidth(true);
            regInfoContent.setVisible(false);

            regRightPane.getChildren().addAll(regInfoContent, regPlaceholder);

            Runnable loadRegistry = () -> {
                VBox regCardList = new VBox(8);
                regCardList.setAlignment(Pos.TOP_CENTER);
                regCardList.setPrefWidth(200);
                regCardList.setLayoutX(0);
                regCardList.setLayoutY(10);

                Label loadingLabel = new Label("Loading registry...");
                loadingLabel.setFont(FontLoader.setFont(14));
                regCardList.getChildren().add(loadingLabel);
                regLeftPane.getChildren().setAll(regCardList);

                javafx.concurrent.Task<List<RegistryPlugin>> fetchTask = new javafx.concurrent.Task<>() {
                    @Override
                    protected List<RegistryPlugin> call() {
                        return PluginRegistryService.fetch();
                    }
                };

                fetchTask.setOnSucceeded(evt -> {
                    List<RegistryPlugin> fetched = fetchTask.getValue();
                    if (fetched == null || fetched.isEmpty()) {
                        regCardList.getChildren().clear();
                        Label none = new Label("No plugins found or failed to load.");
                        none.setFont(FontLoader.setFont(14));
                        regCardList.getChildren().add(none);
                        return;
                    }

                    regCardList.getChildren().clear();
                    for (RegistryPlugin rp : fetched) {
                        Label nameLabel = new Label(rp.name());
                        nameLabel.setFont(FontLoader.setFont(13));
                        nameLabel.setTextOverrun(OverrunStyle.ELLIPSIS);
                        nameLabel.setMaxWidth(160);

                        Label verLabel = new Label("v" + rp.version());
                        verLabel.setFont(FontLoader.setFont(11));
                        verLabel.setTranslateX(5);

                        Label authLabel = new Label(rp.author());
                        authLabel.setFont(FontLoader.setFont(10));
                        authLabel.setPrefWidth(55);
                        authLabel.setMaxWidth(55);
                        authLabel.setWrapText(false);
                        //authLabel.setTextOverrun(OverrunStyle.);
                        authLabel.setAlignment(Pos.BOTTOM_RIGHT);
                        authLabel.setTranslateY(5);
                        authLabel.setTranslateX(-20);

                        Region rowSpacer = new Region();
                        HBox.setHgrow(rowSpacer, Priority.ALWAYS);
                        HBox bottomRow = new HBox(verLabel, rowSpacer, authLabel);
                        bottomRow.setAlignment(Pos.BOTTOM_LEFT);

                        VBox content = new VBox(4, nameLabel, bottomRow);
                        content.setAlignment(Pos.TOP_LEFT);
                        content.setPrefSize(160, 45);

                        Button card = new Button();
                        card.getStyleClass().clear();
                        card.getStyleClass().add("plugin-card");
                        card.setPrefSize(180, 55);
                        card.setMaxSize(180, 55);
                        card.setMinSize(180, 55);
                        card.setGraphic(content);

                        card.setOnAction(click -> {
                            regPlaceholder.setVisible(false);
                            regInfoContent.setVisible(true);

                            regName.setText(rp.name());
                            regDesc.setText(rp.description());
                            setInfoValue(regAuthor, "@"+rp.author());
                            setInfoValue(regVersion, rp.version());
                            setInfoValue(regType, rp.type());
                            setInfoValue(regPerms, inferPermissionsFromType(rp.type()));

                            boolean installed = PluginInstaller.isInstalled(rp.name());
                            installBtn.setText(installed ? "Installed" : "Install");
                            installBtn.setDisable(installed);
                            installBtn.setOnAction(installEvt -> {
                                installBtn.setText("Installing...");
                                installBtn.setDisable(true);
                                new Thread(() -> {
                                    boolean success = PluginInstaller.install(rp);
                                    javafx.application.Platform.runLater(() -> {
                                        if (success) {
                                            installBtn.setText("Installed (Restart needed)");
                                        } else {
                                            installBtn.setText("Install Failed");
                                            installBtn.setDisable(false);
                                        }
                                    });
                                }).start();
                            });

                            githubBtn.setDisable(false);
                            githubBtn.setOnAction(gitEvt -> {
                                try {
                                    Desktop.getDesktop().browse(URI.create(rp.url()));
                                } catch (Exception ex) {
                                    ex.printStackTrace();
                                }
                            });
                        });

                        regCardList.getChildren().add(card);
                    }
                });

                new Thread(fetchTask).start();
            };

            regBack.setOnAction(b -> {
                rootPane.getChildren().removeAll(regLeftPane, regRightPane, regBack, regRefresh);
                leftPane.setVisible(true);
                rightPane.setVisible(true);
                browseBtn.setVisible(true);
                addJarBtn.setVisible(true);
                back.setVisible(true);
                titleLabel.setText("Plugins");
            });

            regRefresh.setOnAction(b -> loadRegistry.run());
            rootPane.getChildren().addAll(regLeftPane, regRightPane, regBack, regRefresh);
            loadRegistry.run();
        });

        return rootPane;
    }

    private Pane getList() {
        Pane pane = new Pane();
        pane.setId("plugin-background");
        pane.setPrefSize(200, 280);

        VBox cardList = new VBox(8);
        cardList.setAlignment(Pos.TOP_CENTER);
        cardList.setPrefWidth(200);
        cardList.setLayoutX(0);
        cardList.setLayoutY(10);

        if (PluginManager.getPlugins().isEmpty()) {
            Label placeholder = new Label("No Plugins Installed!");
            placeholder.setFont(FontLoader.setFont(16));
            placeholder.setWrapText(true);
            placeholder.setPrefWidth(180);
            cardList.getChildren().add(placeholder);
        }

        for (DockTaskPlugin plugin : PluginManager.getPlugins()) {
            // Top: plugin name
            Label nameLabel = new Label(plugin.getName());
            nameLabel.setFont(FontLoader.setFont(13));
            nameLabel.setTextOverrun(OverrunStyle.ELLIPSIS);
            nameLabel.setMaxWidth(160);

            // Bottom row: version (grows) + author (pinned right)
            Label verLabel = new Label("v" + plugin.getVersion());
            verLabel.setFont(FontLoader.setFont(11));
            verLabel.setTranslateX(5);

            Label authLabel = new Label(plugin.getAuthor());
            authLabel.setFont(FontLoader.setFont(10));
            authLabel.setPrefWidth(55);
            authLabel.setMaxWidth(55);
            authLabel.setWrapText(false);
            authLabel.setTextOverrun(OverrunStyle.ELLIPSIS);
            authLabel.setAlignment(Pos.BOTTOM_RIGHT);
            authLabel.setTranslateY(5);
            authLabel.setTranslateX(-20);

            Region rowSpacer = new Region();
            HBox.setHgrow(rowSpacer, Priority.ALWAYS);
            HBox bottomRow = new HBox(verLabel, rowSpacer, authLabel);
            bottomRow.setAlignment(Pos.BOTTOM_LEFT);

            // Stack name on top, bottom row underneath
            VBox content = new VBox(4, nameLabel, bottomRow);
            content.setAlignment(Pos.TOP_LEFT);
            content.setPrefSize(160, 45);

            Button card = new Button();
            // Clear the default 'button' style class to avoid conflicting hover animations,
            // then apply plugin-card as the sole CSS rule for this button.
            card.getStyleClass().clear();
            card.getStyleClass().add("plugin-card");
            card.setPrefSize(180, 55);
            card.setMaxSize(180, 55);
            card.setMinSize(180, 55);
            card.setGraphic(content);
            card.setOnAction(e -> {
                //Update info
                placeholder.setVisible(false);
                infoContent.setVisible(true);
                selectedPlugin = plugin;

                pluginName.setText(plugin.getName());
                pluginDescription.setText(plugin.getDescription()); 
                setInfoValue(pluginAuthor, plugin.getAuthor());
                setInfoValue(pluginVersion, plugin.getVersion());
                setInfoValue(pluginType, plugin.getType());
                setInfoValue(pluginAccess, getPluginGithubUrl(plugin));
                updateTopButtonsForSelectedPlugin();

            });

            cardList.getChildren().add(card);
        }

        pane.getChildren().add(cardList);
        return pane;
    }

    private Pane getInfo() {
        Pane pane = new Pane();
        pane.setId("plugin-background");
        pane.setPrefSize(440, 280);

        placeholder = new Label("Select a plugin to view more options.");
        placeholder.setFont(FontLoader.setFont(16));
        placeholder.setLayoutX(85);
        placeholder.setLayoutY(15);

        // Top row: plugin name (left) + settings (right)
        pluginName = new Label();
        pluginName.setFont(FontLoader.setFont(18));
        pluginName.setTextOverrun(OverrunStyle.ELLIPSIS);
        pluginName.setMaxWidth(290);

        settingsButton = new Button("Settings");
        settingsButton.setFont(FontLoader.setFont(14));
        settingsButton.setPrefSize(90, 28);
        settingsButton.setDisable(true);
        settingsButton.setOnAction(e -> openPluginSettings(pane));

        widgetToggleButton = new Button("Widget");
        widgetToggleButton.setFont(FontLoader.setFont(14));
        widgetToggleButton.setPrefSize(110, 28);
        widgetToggleButton.setDisable(true);
        widgetToggleButton.setOnAction(e -> toggleSelectedWidget());

        HBox rightButtons = new HBox(10, widgetToggleButton, settingsButton);
        rightButtons.setAlignment(Pos.CENTER_RIGHT);

        BorderPane topRow = new BorderPane();
        topRow.setLeft(pluginName);
        topRow.setRight(rightButtons);
        topRow.setPrefWidth(440 - 20);
        topRow.setMaxWidth(Double.MAX_VALUE);

        // Description section
        Label aboutTag = new Label("Description");
        aboutTag.setFont(FontLoader.setFont(12));
        aboutTag.setOpacity(0.65);

        pluginDescription = new Label();
        pluginDescription.setFont(FontLoader.setFont(13));
        pluginDescription.setWrapText(true);
        pluginDescription.setPrefWidth(410);
        pluginDescription.setOpacity(0.9);

        // Info buttons (each button uses a VBox graphic: title + value)
        pluginType = infoButton("Type");
        pluginAccess = infoButton("GitHub");
        pluginAuthor = infoButton("Author");
        pluginVersion = infoButton("Version");

        GridPane cards = new GridPane();
        cards.setHgap(12);
        cards.setVgap(12);
        cards.add(pluginType, 0, 0);
        cards.add(pluginAccess, 1, 0);
        cards.add(pluginAuthor, 0, 1);
        cards.add(pluginVersion, 1, 1);
        VBox.setMargin(cards, new Insets(10, 0, 0, 0));
        cards.setAlignment(Pos.CENTER);

        HBox cardsHolder = new HBox(cards);
        cardsHolder.setAlignment(Pos.CENTER);

        infoContent = new VBox(10, topRow, aboutTag, pluginDescription, cardsHolder);
        infoContent.setPadding(new Insets(10));
        infoContent.setPrefSize(440, 280);
        infoContent.setFillWidth(true);
        infoContent.setVisible(false);

        pane.getChildren().add(infoContent);
        pane.getChildren().add(placeholder);
        return pane;
    }

    private void setInfoValue(Button btn, String value) {
        String safe = (value == null || value.isBlank()) ? "-" : value;
        Object ud = btn.getUserData();
        if (ud instanceof VBox box && box.getChildren().size() >= 2 && box.getChildren().get(1) instanceof Label valueLabel) {
            valueLabel.setText(safe);
        } else {
            btn.setText(safe);
        }
    }

    private Button infoButton(String title){
        Button btn = new Button();
        btn.setId("noHoverBtn");
        btn.setPrefSize(176, 58);

        Label titleLabel = new Label(title);
        titleLabel.setFont(FontLoader.setFont(11));

        Label valueLabel = new Label("-");
        valueLabel.setFont(FontLoader.setFont(14));
        valueLabel.setTextOverrun(OverrunStyle.ELLIPSIS);
        valueLabel.setMaxWidth(150);
        valueLabel.setTextFill(themeManager.isDarkMode() ? Color.WHITE : Color.BLACK);

        VBox content = new VBox(4, titleLabel, valueLabel);
        content.setAlignment(Pos.TOP_LEFT);
        content.setPrefSize(160, 45);

        btn.setGraphic(content);
        btn.setUserData(content);
        return btn;
    }

    private void updateTopButtonsForSelectedPlugin() {
        if (selectedPlugin == null) {
            widgetToggleButton.setDisable(true);
            settingsButton.setDisable(true);
            return;
        }

        // Widget toggle applies only to WidgetPlugin implementations
        if (selectedPlugin instanceof WidgetPlugin) {
            PluginContext ctx = new PluginContextImpl(username, selectedPlugin.getName());
            boolean enabled = Boolean.parseBoolean(ctx.loadState("widget.enabled", "true"));
            widgetToggleButton.setText(enabled ? "Hide Widget" : "Show Widget");
            widgetToggleButton.setDisable(false);
        } else {
            widgetToggleButton.setText("No Widget");
            widgetToggleButton.setDisable(true);
        }

        // Settings applies only to MenuPlugin implementations
        settingsButton.setDisable(!(selectedPlugin instanceof MenuPlugin));
    }

    private void toggleSelectedWidget() {
        if (!(selectedPlugin instanceof WidgetPlugin)) return;

        PluginContext ctx = new PluginContextImpl(username, selectedPlugin.getName());
        boolean enabled = Boolean.parseBoolean(ctx.loadState("widget.enabled", "true"));
        boolean newState = !enabled;
        ctx.saveState("widget.enabled", String.valueOf(newState));

        widgetToggleButton.setText(newState ? "Hide Widget" : "Show Widget");
        PluginManager.notifyWidgetVisibilityChanged();
    }

    /**
     * Replaces the right-pane content with the selected MenuPlugin's settings UI.
     * A "Back" button is injected at the top of the plugin-provided pane to
     * restore the original info view.
     */
    private void openPluginSettings(Pane rightPane) {
        if (!(selectedPlugin instanceof MenuPlugin menuPlugin)) return;

        PluginContext ctx = new PluginContextImpl(username, selectedPlugin.getName());
        Pane pluginContent = menuPlugin.getMenuContent(ctx);

        // Back button – restores info view
        Button backBtn = new Button("Back");
        backBtn.setFont(FontLoader.setFont(13));
        backBtn.setPrefSize(80, 26);
        backBtn.setOnAction(ev -> {
            rightPane.getChildren().setAll(infoContent, placeholder);
        });

        // Wrap: back button on top, then plugin content below
        VBox wrapper = new VBox(8, backBtn, pluginContent);
        wrapper.setPadding(new Insets(8));
        wrapper.setPrefSize(440, 280);

        rightPane.getChildren().setAll(wrapper);
    }

    private void openJarFileChooser() {
        Window win = rootPane.getScene() != null ? rootPane.getScene().getWindow() : null;
        if (win == null) return;

        FileChooser chooser = new FileChooser();
        chooser.setTitle("Choose plugin JAR");
        chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("JAR files", "*.jar"));
        File picked = chooser.showOpenDialog(win);
        if (picked == null) return;

        Pane confirmPane = confirmation.check(
                "install this plugin",
                picked.getAbsolutePath(),
                FontLoader.setFont(14),
                () -> {
                    Path dest = PluginInstaller.installLocalJar(picked);
                    if (dest == null) {
                        System.out.println("Failed to copy plugin JAR: " + picked.getAbsolutePath());
                    } else {
                        System.out.println("Copied plugin JAR to: " + dest);
                    }
                }
        );
        rootPane.getChildren().add(confirmPane);
    }

    private String inferPermissionsFromType(String type) {
        if (type == null) return "NONE";
        String t = type.trim().toLowerCase();
        boolean hasWidget = t.contains("widget") || t.contains("hybrid");
        boolean hasMenu = t.contains("menu") || t.contains("hybrid");
        List<String> perms = new java.util.ArrayList<>();
        if (hasWidget) perms.add("TASK_READ");
        if (hasMenu) perms.add("STORAGE");
        return perms.isEmpty() ? "NONE" : String.join(" · ", perms);
    }

    private String getPluginGithubUrl(DockTaskPlugin plugin) {
        String fromInstall = PluginInstaller.getInstalledUrl(plugin.getName());
        if (fromInstall != null && !fromInstall.isBlank()) return fromInstall;
        // Fallback for built-in / unsigned plugins
        return "—";
    }

}