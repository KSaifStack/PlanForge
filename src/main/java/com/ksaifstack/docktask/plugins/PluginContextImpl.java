package com.ksaifstack.docktask.plugins;

import com.ksaifstack.docktask.model.UserData;
import com.ksaifstack.docktask.util.FontLoader;
import com.ksaifstack.docktask.util.themeManager;
import javafx.scene.layout.Region;
import javafx.scene.text.Font;
import java.net.URL;

public class PluginContextImpl implements PluginContext {
    private final String username;
    private final String pluginName;
    private final FontLoader fontLoader;

    public PluginContextImpl(String username, String pluginName) {
        this.username = username;
        this.pluginName = pluginName;
        this.fontLoader = new FontLoader();
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public Font getFont(int size) {
        return fontLoader.lexend(size);
    }

    @Override
    public void saveState(String key, String value) {
        UserData.savePluginState(username, pluginName, key, value);
    }

    @Override
    public String loadState(String key, String defaultValue) {
        return UserData.loadPluginState(username, pluginName, key, defaultValue);
    }

    @Override
    public void loadPluginCss(Region node, String resourcePath) {
        URL resource = getClass().getResource(resourcePath);
        if (resource == null) {
            System.err.println("[PluginContext] CSS not found: " + resourcePath);
            return;
        }
        String css = resource.toExternalForm();
        if (!node.getStylesheets().contains(css)) {
            node.getStylesheets().add(css);
        }
    }

    @Override
    public boolean isDarkMode() {
        return themeManager.isDarkMode();
    }
}
