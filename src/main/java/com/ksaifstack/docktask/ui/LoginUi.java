package com.ksaifstack.docktask.ui;

import com.ksaifstack.docktask.model.UserData;
import com.ksaifstack.docktask.util.WindowActions;
import javafx.animation.*;
import javafx.application.Application;
import javafx.application.HostServices;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;

import java.io.InputStream;

public class LoginUi extends Application {


    private static final String BG_COLOR     = "#ffffff";
    private static final String TEXT_COLOR   = "#1b1b1b";
    private static final String BORDER_COLOR = "#626262";
    private static final String ERROR_COLOR  = "#bd1111";
    private static final String HOVER_COLOR  = "#d3d3d3";
    private static final String FONT_PATH    = "/fonts/lexend.ttf";

    private static final double WINDOW_WIDTH  = 766;
    private static final double WINDOW_HEIGHT = 378;


    private static HostServices hostServices;
    private static boolean      startup = true;

    private WindowActions appWindow;
    private final Label  errorLabel = new Label();


    public LoginUi() {}

    @Override
    public void start(Stage primaryStage) {
        if (startup) {
            startup = false;
            showSplash(() -> showMainApp(primaryStage));
        } else {
            showMainApp(primaryStage);
        }
    }

    // Splash Screen

    private void showSplash(Runnable onFinish) {
        UserData.initializeData();

        InputStream logoStream = getClass().getResourceAsStream("/images/logo.png");
        if (logoStream == null) {
            System.err.println("Splash image not found.");
            onFinish.run();
            return;
        }

        ImageView logoView = new ImageView(new Image(logoStream));
        logoView.setFitWidth(250);
        logoView.setPreserveRatio(true);

        StackPane root = new StackPane(logoView);
        root.setStyle("-fx-background-color: transparent;");

        Stage splashStage = new Stage(StageStyle.TRANSPARENT);
        splashStage.setScene(new Scene(root, 300, 300, Color.TRANSPARENT));
        splashStage.show();

        FadeTransition fadeIn  = fade(logoView, 1.0, 0.0, 1.0);
        FadeTransition fadeOut = fade(logoView, 1.0, 1.0, 0.0);
        PauseTransition pause  = new PauseTransition(Duration.seconds(1.5));

        SequentialTransition sequence = new SequentialTransition(fadeIn, pause, fadeOut);
        sequence.setOnFinished(e -> {
            splashStage.close();
            onFinish.run();
        });
        sequence.play();
    }

    private FadeTransition fade(ImageView node, double seconds, double from, double to) {
        FadeTransition ft = new FadeTransition(Duration.seconds(seconds), node);
        ft.setFromValue(from);
        ft.setToValue(to);
        return ft;
    }


    public void showMainApp(Stage primaryStage) {
        hostServices = getHostServices();

        if (appWindow != null) {
            appWindow.toFront();
            return;
        }

        setStageIcon(primaryStage);
        primaryStage.setTitle("DockTask");
        primaryStage.setResizable(false);

         appWindow = WindowBorderUi.create("DockTask", buildHomePage(primaryStage), WINDOW_WIDTH, WINDOW_HEIGHT);
        ((Stage)appWindow).setWidth(WINDOW_WIDTH);
        ((Stage)appWindow).setHeight(WINDOW_HEIGHT);
        ((Stage)appWindow).show();

    }

    private Pane buildHomePage(Stage primaryStage) {
        Pane root = new Pane();
        root.setStyle("-fx-background-color: " + BG_COLOR + ";");
        root.setPrefSize(WINDOW_WIDTH, WINDOW_HEIGHT);

        ImageView logo = loadImage("/images/lightIcon.png", 150);
        logo.setLayoutX(308);
        logo.setLayoutY(50);

        Font lexend32 = loadFont(32);
        Label titleLabel = styledLabel("DockTask", lexend32, TEXT_COLOR);
        titleLabel.setLayoutX(308);
        titleLabel.setLayoutY(0);
        titleLabel.setPrefSize(300, 60);

        Label orLabel = styledLabel("--OR--", null, TEXT_COLOR);
        orLabel.setFont(loadFont(12));
        orLabel.setLayoutX(365);
        orLabel.setLayoutY(241);

        Button loginBtn  = buildNavButton("Log-In",  405, 203.66, () -> showLoginPage(primaryStage));
        Button signUpBtn = buildNavButton("Sign-Up", 245, 203.66, () -> showSignupPage(primaryStage));
        root.getChildren().addAll(logo, titleLabel, orLabel, loginBtn, signUpBtn);
        return root;
    }


    private void showLoginPage(Stage primaryStage) {
        appWindow.colorChange(" ");
        appWindow.setContent(buildAuthForm("Welcome!", "Log-In", false, primaryStage));
    }

    private void showSignupPage(Stage primaryStage) {
        appWindow.colorChange(" ");
        appWindow.setContent(buildAuthForm("Welcome!", "Sign-Up", true, primaryStage));
    }

    public void showBack(Stage primaryStage) {
        appWindow.setContent(buildHomePage(primaryStage));
    }

    /**
     * Builds a unified login/signup form.
     * @param isSignup true = sign-up mode, false = login mode
     */
    private VBox buildAuthForm(String titleText, String buttonText, boolean isSignup, Stage primaryStage) {
        TextField     usernameField = createTextField("Username");
        PasswordField passwordField = createPasswordField("Password");
        Button        actionButton  = createButton(buttonText, 140, 45);
        Button        backButton    = createBackButton(primaryStage);

        resetError();

        actionButton.setOnAction(e -> handleAuth(usernameField, passwordField, isSignup, primaryStage));
        actionButton.setOnKeyPressed(ev -> { if (ev.getCode() == KeyCode.ENTER) actionButton.fire(); });
        usernameField.setOnAction(e -> actionButton.fire());
        passwordField.setOnAction(e -> actionButton.fire());

        GridPane form = new GridPane();
        form.setHgap(10);
        form.setVgap(15);
        form.setAlignment(Pos.CENTER);
        form.add(usernameField, 0, 0);
        form.add(passwordField, 0, 1);

        Label titleLabel = styledLabel(titleText, loadFont(28), TEXT_COLOR);

        VBox layout = new VBox(20, titleLabel, form, actionButton, backButton, errorLabel);
        layout.setPadding(new Insets(20));
        layout.setAlignment(Pos.TOP_CENTER);
        layout.setStyle("-fx-background-color: " + BG_COLOR + ";");
        return layout;
    }

    private void handleAuth(TextField usernameField, PasswordField passwordField,
                            boolean isSignup, Stage primaryStage) {
        String username = usernameField.getText().trim().toLowerCase();
        String password = passwordField.getText().trim().toLowerCase();

        if (username.isEmpty() || password.isEmpty()) {
            showError("No username or password input!");
            return;
        }

        if (isSignup) {
            handleSignup(username, password);
        } else {
            handleLogin(username, password);
        }
    }

    private void handleLogin(String username, String password) {
        if (!UserData.usernameExists(username)) {
            showError("ERROR: Cannot find username or password.");
        } else if (UserData.findUser(username, password)) {
            launchTaskUi(username);
        } else {
            showError("ERROR: Wrong password.");
        }
    }

    private void handleSignup(String username, String password) {
        if (UserData.usernameExists(username)) {
            showError("ERROR: Username is already registered.");
        } else {
            UserData.saveUser(username, password);
            launchTaskUi(username);
        }
    }

    private void launchTaskUi(String username) {
        TaskUi taskUi = new TaskUi(username);
        taskUi.start(appWindow, this);
    }


    private void showError(String message) {
        errorLabel.setText(message);
        errorLabel.setFont(loadFont(14));
        errorLabel.setStyle("-fx-text-fill: " + ERROR_COLOR + ";");
        errorLabel.setVisible(true);
        System.err.println(message);
    }

    private void resetError() {
        errorLabel.setText("");
        errorLabel.setVisible(false);
    }


    private Button buildNavButton(String text, double x, double y, Runnable action) {
        Button btn = new Button(text);
        btn.setFont(loadFont(14));
        btn.setLayoutX(x);
        btn.setLayoutY(y);
        btn.setPrefSize(116, 104);
        btn.setOnAction(e -> action.run());
        return btn;
    }

    private Button createButton(String text, double width, double height) {
        Button btn = new Button(text);
        btn.setFont(loadFont(14));
        btn.setPrefSize(width, height);
        return btn;
    }

    private Button createBackButton(Stage primaryStage) {
        Button btn = new Button("Back");
        btn.setFont(loadFont(12));
        btn.setStyle("-fx-background-color: transparent; -fx-text-fill: " + TEXT_COLOR + ";");
        btn.setOnAction(e -> showBack(primaryStage));
        return btn;
    }



    private String buttonStyle(String bgColor) {
        return String.format(
                "-fx-background-color: %s; -fx-text-fill: %s; " +
                        "-fx-border-color: %s; -fx-border-radius: 4px; " +
                        "-fx-background-radius: 4px; -fx-border-width: 1px;",
                bgColor, TEXT_COLOR, BORDER_COLOR
        );
    }

    private TextField createTextField(String placeholder) {
        TextField tf = new TextField();
        tf.setPromptText(placeholder);
        tf.setFont(loadFont(14));
        tf.setPrefWidth(200);
        tf.setStyle(inputStyle());
        return tf;
    }

    private PasswordField createPasswordField(String placeholder) {
        PasswordField pf = new PasswordField();
        pf.setPromptText(placeholder);
        pf.setPrefWidth(200);
        pf.setStyle(inputStyle());
        return pf;
    }

    private String inputStyle() {
        return """
            -fx-background-color: #ffffff;
            -fx-text-fill: #1b1b1b;
            -fx-border-color: #626262;
            -fx-border-radius: 2px;
            -fx-border-width: 1px;
            -fx-prompt-text-fill: #737674;
        """;
    }

    private Label styledLabel(String text, Font font, String color) {
        Label label = new Label(text);
        if (font != null) label.setFont(font);
        label.setStyle("-fx-text-fill: " + color + ";");
        return label;
    }

    private ImageView loadImage(String path, double fitWidth) {
        ImageView iv = new ImageView(new Image(getClass().getResourceAsStream(path)));
        iv.setFitWidth(fitWidth);
        iv.setPreserveRatio(true);
        return iv;
    }


    private Font loadFont(double size) {
        return Font.loadFont(getClass().getResourceAsStream("/fonts/Lexend.ttf"), size);
    }

    private void setStageIcon(Stage stage) {
        InputStream iconStream = getClass().getResourceAsStream("/images/logo.png");
        if (iconStream != null) {
            stage.getIcons().add(new Image(iconStream));
        } else {
            System.err.println("Window icon not found.");
        }
    }

    public static HostServices getHost() { return hostServices; }

    public static void openURL(String link) {
        if (hostServices == null) {
            System.err.println("HostServices not initialized.");
            return;
        }
        try {
            hostServices.showDocument(link);
        } catch (Exception e) {
            System.err.println("Failed to open link: '" + link + "'");
        }
    }

    public static void main(String[] args) { launch(args); }
}