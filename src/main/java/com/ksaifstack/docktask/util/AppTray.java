package com.ksaifstack.docktask.util;
import com.dustinredmond.fxtrayicon.FXTrayIcon;
import javafx.stage.Stage;
import javafx.scene.control.MenuItem;
import java.net.URL;
/*
Uses fxicontray imports to allow the use of SystemTray calls.
These calls make it possible to iconfiy the app upon exit.
 */

public class AppTray {
    private static AppTray instance;
    private FXTrayIcon trayIcon;
    private boolean firstHide = true;

    private AppTray() {}

    public static AppTray getInstance() {
        if (instance == null) {
            instance = new AppTray();
        }
        return instance;
    }

    public FXTrayIcon getTrayIcon() {
        return trayIcon;
    }

    public void setup(Stage stage, String tooltip, String iconPath) {
        if (!FXTrayIcon.isSupported()) {
            System.out.println("System tray not supported");
            return;
        }

        if (trayIcon != null) {
            System.out.println("Tray icon already exists, skipping setup...");
            return;
        }

        URL iconUrl = getClass().getResource(iconPath);
        if (iconUrl == null) {
            System.out.println("Tray icon image not found: " + iconPath);
            return;
        }



        MenuItem exitItem = new MenuItem("Exit");
        exitItem.setOnAction(e -> {
            trayIcon.hide();
            javafx.application.Platform.exit();
            System.exit(0);
        });

        trayIcon = new FXTrayIcon.Builder(stage, iconUrl)
                .applicationTitle(tooltip)
                .separator()
                .menuItem(exitItem)
                .show()
                .build();

        // Toggle show/hide on tray icon click
        trayIcon.setOnAction(e -> {
            try {
                if (stage.isShowing() && stage.getOpacity() > 0) {
                    hideToTray(stage);
                } else {
                    stage.setOpacity(1);
                    stage.setIconified(false);
                    stage.show();
                    stage.requestFocus();
                }
            } catch (Exception bad) {
                System.out.println("Error while opening taskui: " + bad.getMessage());
            }
        });
    }

    public void hideToTray(Stage stage) {
        stage.setIconified(true);
        stage.setOpacity(0);
        if (firstHide && trayIcon != null) {
            trayIcon.showInfoMessage("DockTask", "DockTask is still running in the background.");
            firstHide = false;
        }
    }
}