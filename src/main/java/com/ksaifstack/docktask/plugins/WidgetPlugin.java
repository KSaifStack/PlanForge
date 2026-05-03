package com.ksaifstack.docktask.plugins;

import javafx.scene.layout.Region;

public interface WidgetPlugin extends DockTaskPlugin {
    /**
     * Returns the Region that will be rendered on the main TaskUi canvas.
     */
    Region getWidgetContent(PluginContext context);
    
    /**
     * Default starting position on the canvas.
     */
    double getDefaultX();
    double getDefaultY();
    double getDefaultSize();
}
