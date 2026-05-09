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

        // Load community plugins from Data/Plugins.txt
        loadLocalPlugins();
    }

    private static void loadLocalPlugins() {
        try {
            java.io.File pluginsDir = new java.io.File("Data/plugins");
            if (!pluginsDir.exists()) return;

            java.io.File[] jarFiles = pluginsDir.listFiles((dir, name) -> name.toLowerCase().endsWith(".jar"));
            if (jarFiles == null || jarFiles.length == 0) return;

            java.util.List<java.net.URL> urls = new ArrayList<>();
            for (java.io.File jarFile : jarFiles) {
                urls.add(jarFile.toURI().toURL());
            }

            if (!urls.isEmpty()) {
                java.net.URLClassLoader cl = new java.net.URLClassLoader(urls.toArray(new java.net.URL[0]), PluginManager.class.getClassLoader());
                java.util.ServiceLoader<DockTaskPlugin> loader = java.util.ServiceLoader.load(DockTaskPlugin.class, cl);
                for (DockTaskPlugin plugin : loader) {
                    registry.add(plugin);
                    System.out.println("Loaded community plugin: " + plugin.getName());
                }
            }
        } catch (Exception e) {
            System.err.println("Failed to load community plugins: " + e.getMessage());
            e.printStackTrace();
        }
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

