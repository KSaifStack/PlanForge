package com.ksaifstack.docktask.plugins;

import com.ksaifstack.docktask.plugins.firstparty.PomodoroPlugin;
import java.util.ArrayList;
import java.util.List;

public class PluginManager {
    private static final List<DockTaskPlugin> registry = new ArrayList<>();
    private static final List<Runnable> widgetVisibilityListeners = new ArrayList<>();

    static {
        // Hardcoded registration for v0.8.0 foundation
        registry.add(new PomodoroPlugin());
    }

    public static List<DockTaskPlugin> getPlugins() {
        return registry;
    }

    public static List<MenuPlugin> getMenuPlugins() {
        List<MenuPlugin> menuPlugins = new ArrayList<>();
        for (DockTaskPlugin plugin : registry) {
            if (plugin instanceof MenuPlugin) {
                menuPlugins.add((MenuPlugin) plugin);
            }
        }
        return menuPlugins;
    }

    public static List<WidgetPlugin> getWidgetPlugins() {
        List<WidgetPlugin> widgetPlugins = new ArrayList<>();
        for (DockTaskPlugin plugin : registry) {
            if (plugin instanceof WidgetPlugin) {
                widgetPlugins.add((WidgetPlugin) plugin);
            }
        }
        return widgetPlugins;
    }

    /** Called when any plugin widget's enabled state changes. */
    public static void addWidgetVisibilityListener(Runnable listener) {
        widgetVisibilityListeners.add(listener);
    }

    /** Notify all registered listeners that widget visibility has changed. */
    public static void notifyWidgetVisibilityChanged() {
        for (Runnable r : widgetVisibilityListeners) r.run();
    }
}

