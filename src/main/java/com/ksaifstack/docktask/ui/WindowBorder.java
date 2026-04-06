package com.ksaifstack.docktask.ui;

import com.ksaifstack.docktask.util.themeManager;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import xss.it.nfx.NfxStage;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.*;

import java.io.InputStream;

public class WindowBorder extends NfxStage implements WindowActions {

    private static final boolean IS_WINDOWS =
            System.getProperty("os.name", "").toLowerCase().contains("win");

    public static WindowActions create(String title, Region content, double width, double height) {
        if (IS_WINDOWS) {
            return new WindowBorder(title, content, width, height);
        } else {
            return new DefaultWindow(title, content, width, height);
        }
    }

    private HBox titleBar;
    private Button minBtn, closeBtn;
    private Button icon;
    private Label titleLabel;
    private static BorderPane rootPane;

    private double xOffset = 0;
    private double yOffset = 0;

    private static final Image whiteIcon = new Image(WindowBorder.class.getResourceAsStream("/images/lightIcon.png"));
    private static final Image darkIcon  = new Image(WindowBorder.class.getResourceAsStream("/images/darkIcon.png"));
    private static final ImageView pic   = new ImageView(whiteIcon);

    public WindowBorder(String title, Region content, double width, double height) {
        setTitle(title);

        rootPane = new BorderPane();
        rootPane.setCenter(content);

        titleBar = new HBox();
        titleBar.setPadding(new Insets(0, 10, 0, 10));
        titleBar.setAlignment(Pos.CENTER_LEFT);
        titleBar.setSpacing(10);

        titleLabel = new Label(title);
        titleLabel.setAlignment(Pos.CENTER);
        HBox.setHgrow(titleLabel, Priority.ALWAYS);

        icon = createButton("");
        updateIcon();
        themeManager.addThemeChangeListener(this::updateIcon);

        pic.setFitHeight(32);
        pic.setFitWidth(32);
        icon.setPadding(Insets.EMPTY);
        icon.setTooltip(new Tooltip("DockTask"));
        icon.setGraphic(pic);

        minBtn   = createButton("—");
        closeBtn = createButton("✕");
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        titleBar.getChildren().addAll(icon, spacer, minBtn, closeBtn);
        rootPane.setTop(titleBar);

        makeDraggable(titleBar);
        setResizable(false);

        addClientAreas(titleBar);
        setMinControl(minBtn);
        setCloseControl(closeBtn);

        InputStream logoStream = getClass().getResourceAsStream("/images/logo.png");
        getIcons().add(new Image(logoStream));

        Scene scene = new Scene(rootPane, width, height);
        scene.setFill(null);
        scene.getStylesheets().add(getClass().getResource("/css/LightTheme.css").toExternalForm());
        themeManager.setScene(scene);
        setScene(scene);
    }

    private void updateIcon() {
        pic.setImage(themeManager.isDarkMode() ? darkIcon : whiteIcon);
    }

    @Override public void setTitleLabel(String text)   { this.titleLabel.setText(text); }
    @Override public void colorChange(String cssColor) { rootPane.setStyle(cssColor); }
    @Override public void setContent(Region newContent) { rootPane.setCenter(newContent); }

    private Button createButton(String text) {
        Button btn = new Button(text);
        btn.setId("noBorderBtn");
        btn.setFocusTraversable(false);
        return btn;
    }

    private void makeDraggable(HBox bar) {
        bar.setOnMousePressed(e -> { xOffset = e.getSceneX(); yOffset = e.getSceneY(); });
        bar.setOnMouseDragged(e -> { setX(e.getScreenX() - xOffset); setY(e.getScreenY() - yOffset); });
    }

    @Override
    protected double getTitleBarHeight() { return 40; }

    public static void logOut(Scene scene) {
        scene.getStylesheets().add(WindowBorder.class.getResource("/css/LightTheme.css").toExternalForm());
    }

    // ── Nested fallback window ───────────────────────────────────────────────────

    public static class DefaultWindow extends Stage implements WindowActions {

        private final BorderPane rootPane;

        public DefaultWindow(String title, Region content, double width, double height) {
            setTitle(title);
            setResizable(false);

            rootPane = new BorderPane();
            rootPane.setPadding(new Insets(15, 0, 0, 0));
            rootPane.setCenter(content);

            InputStream logoStream = getClass().getResourceAsStream("/images/logo.png");
            if (logoStream != null) {
                getIcons().add(new Image(logoStream));
            }

            Scene scene = new Scene(rootPane, width, height);
            scene.getStylesheets().add(getClass().getResource("/css/LightTheme.css").toExternalForm());
            themeManager.setScene(scene);
            setScene(scene);

        }


        @Override public void setContent(Region newContent)  { rootPane.setCenter(newContent); }
        @Override public void colorChange(String cssColor)   { rootPane.setStyle(cssColor); }
        @Override public void setTitleLabel(String text)     { setTitle(text); }
    }

}