package com.ksaifstack.docktask.plugins;

import javafx.scene.layout.Pane;
import javafx.scene.text.Font;

public interface MenuPlugin extends DockTaskPlugin {
    /**
     * Returns the Pane to be displayed inside the PluginUi when opened.
     */
    Pane getMenuContent(PluginContext context);
}
