package com.ksaifstack.docktask.plugins;

import javafx.scene.layout.Region;
import javafx.scene.text.Font;

/**
 * Interface provided to plugins to securely interact with the DockTask application.
 */
public interface PluginContext {
    /**
     * @return The username of the currently active user.
     */
    String getUsername();

    /**
     * Loads a font using the standard DockTask font loader.
     * @param size The size of the font.
     * @return The loaded Font object.
     */
    Font getFont(int size);

    /**
     * Saves a state variable for this specific plugin.
     * @param key The key to save.
     * @param value The value to save.
     */
    void saveState(String key, String value);

    /**
     * Loads a state variable for this specific plugin.
     * @param key The key to load.
     * @param defaultValue The default value if the key does not exist.
     * @return The loaded state or defaultValue.
     */
    String loadState(String key, String defaultValue);

    /**
     * Loads an external CSS file specifically for this plugin and attaches it to the provided Node.
     * The resourcePath should be relative to the resources folder (e.g. "/plugins/myplugin.css").
     * The CSS can use DockTask's built-in variables (e.g. -dt-bg-color) to automatically
     * adapt to the current theme.
     * @param node The JavaFX Region to style.
     * @param resourcePath The path to the CSS file.
     */
    void loadPluginCss(Region node, String resourcePath);

    /**
     * @return True if the application is currently in dark mode.
     */
    boolean isDarkMode();
}
