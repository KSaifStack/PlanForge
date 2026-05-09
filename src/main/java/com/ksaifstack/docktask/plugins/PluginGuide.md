# DockTask Plugin Guide

> This guide is for developers who want to build plugins for DockTask.
> DockTask v0.8.0 ships with a fully functional, sandboxed plugin architecture.

---

## Table of Contents

- [Overview](#overview)
- [Plugin Types](#plugin-types)
- [The PluginContext API](#the-plugincontext-api)
- [Theme-Aware CSS](#theme-aware-css)
- [Saving & Loading State](#saving--loading-state)
- [Widget Visibility](#widget-visibility)
- [Step-by-Step: Creating a Plugin](#step-by-step-creating-a-plugin)
  - [Menu Plugin](#menu-plugin)
  - [Widget Plugin](#widget-plugin)
  - [Hybrid Plugin](#hybrid-plugin)
- [Registering Your Plugin](#registering-your-plugin)
- [Internal Architecture](#internal-architecture)

---

## Overview

DockTask plugins are Java classes that implement one or more plugin interfaces. They are given a `PluginContext` sandbox object that provides controlled access to app internals — fonts, user data, theming — without ever exposing the underlying implementation.

**The core design goals are:**
- Plugins cannot corrupt core data files (`Task.txt`, `Settings.txt`)
- Plugins automatically get the right font and respect the active theme with zero extra work
- The `PluginContext` interface can be extended without breaking existing plugins
- A JAR-based loader can be added in the future to allow community plugins without recompiling the core app

---

## Plugin Types

There are three types of plugins you can create:

| Type | Implements | Where it appears |
|------|-----------|-----------------|
| **Menu Plugin** | `MenuPlugin` | Plugin Hub only — a settings or info panel opened from the Plugins screen |
| **Widget Plugin** | `WidgetPlugin` | Home screen — a draggable, resizable component placed on the main canvas |
| **Hybrid Plugin** | `MenuPlugin` + `WidgetPlugin` | Both — has a home screen widget AND a settings panel in the Plugin Hub |

The built-in **Pomodoro Timer** is an example of a Hybrid Plugin.

---

## The PluginContext API

Every plugin receives a `PluginContext` object. This is your only interface to the DockTask application. You should never import or call `UserData`, `FontLoader`, or `themeManager` directly.

```java
public interface PluginContext {
    /** Returns the username of the currently logged-in user. */
    String getUsername();

    /** Returns the DockTask official font (Lexend) at the given size. */
    Font getFont(int size);

    /** Saves a value under the given key, scoped to this plugin and user. */
    void saveState(String key, String value);

    /** Loads a previously saved value, or returns defaultValue if not found. */
    String loadState(String key, String defaultValue);

    /**
     * Loads a CSS file from the given resource path and attaches it to the node.
     * The CSS can use DockTask's -dt-* theme variables for automatic light/dark support.
     */
    void loadPluginCss(Region node, String resourcePath);

    /** Returns true if the app is currently in dark mode. */
    boolean isDarkMode();

    /** Returns a read-only list of the user's tasks. */
    List<PluginTask> getTasks();
}
```

### Example usage

```java
// In your widget or menu method:
Label title = new Label("My Plugin");
title.setFont(context.getFont(24));   // Lexend 24

String savedValue = context.loadState("myKey", "default");
context.saveState("myKey", "newValue");

context.loadPluginCss(myBox, "/plugins/myplugin.css");

// Reading tasks safely
List<PluginTask> tasks = context.getTasks();
for (PluginTask t : tasks) {
    System.out.println("Task: " + t.name() + " Due: " + t.dueDate());
}
```

> [!NOTE]
> **Read-Only by Design:** The `PluginTask` is a Java Record (DTO). Plugins cannot modify, complete, or delete core app tasks. This ensures community plugins can never accidentally corrupt a user's data.

---

## Theme-Aware CSS

Plugins ship their **own** CSS file. They do **not** modify DockTask's `LightTheme.css` or `DarkTheme.css`.

To make your plugin automatically switch colors when the user toggles dark mode, use the global DockTask CSS variables. These are defined in `.root` in both theme files, so every node in the scene tree inherits them:

| Variable | Light Mode | Dark Mode |
|----------|-----------|----------|
| `-dt-bg-color` | `#ffffff` | `#333333` |
| `-dt-border-color` | `#626262` | `#888888` |
| `-dt-card-bg` | `#fafafa` | `#2b2b2b` |
| `-dt-text-color` | `#000000` | `#ffffff` |
| `-dt-overlay-bg` | `rgba(0,0,0,0.15)` | `rgba(0,0,0,0.5)` |
| `-dt-success-color` | `green` | `#55ff55` |

**How it works:** When the user switches themes, `themeManager` swaps the scene stylesheet. Because your plugin's CSS inherits these variables from `.root`, every node in your widget re-renders in the correct colors automatically — zero extra code needed.

### Example plugin CSS

```css
/* src/main/resources/plugins/myplugin.css */
.my-plugin-box {
    -fx-background-color: -dt-bg-color;
    -fx-border-color: -dt-border-color;
    -fx-border-radius: 8px;
    -fx-background-radius: 8px;
    -fx-padding: 10px;
}

.my-plugin-title {
    -fx-text-fill: -dt-text-color;
}
```

Then in your plugin Java code:

```java
box.getStyleClass().add("my-plugin-box");
context.loadPluginCss(box, "/plugins/myplugin.css");
```

---

## Saving & Loading State

Plugin state is stored per-user and per-plugin in `Data/PluginState.txt` using the `<SEP>` format. You never access this file directly — you go through `PluginContext`.

```java
// Save a setting
context.saveState("timerDuration", "25");

// Load it back (returns "25" if not found)
String duration = context.loadState("timerDuration", "25");
```

The key is automatically namespaced to your plugin name and the current user, so two plugins using the same key will never conflict.

> **Widget position and size are managed automatically** by `DraggableWidget` and `TaskUi` — your plugin does not need to save `x`, `y`, or `size` manually.

---

## Widget Visibility

Widget plugins can be shown or hidden by the user from the **Plugin Hub** at any time without restarting the app.

- When hidden, the widget node is removed from the main canvas.
- When shown again, the same node (with its existing position) is added back.
- The enabled state is persisted per-user in `PluginState.txt` under the key `widget.enabled`.

This happens automatically through a listener pattern — `PluginUi` calls `PluginManager.notifyWidgetVisibilityChanged()` on toggle, and `TaskUi` reacts by adding or removing nodes in real time.

**As a plugin developer, you do not need to implement anything for this** — it works out of the box for all `WidgetPlugin` implementations.

---

## Step-by-Step: Creating a Plugin

### Menu Plugin

A Menu Plugin adds a panel accessible from the **Plugin Hub** (the Plugins button on the home screen).

**1. Create your class:**

```java
package com.ksaifstack.docktask.plugins.firstparty;

import com.ksaifstack.docktask.plugins.DockTaskPlugin;
import com.ksaifstack.docktask.plugins.MenuPlugin;
import com.ksaifstack.docktask.plugins.PluginContext;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

public class MyMenuPlugin implements MenuPlugin {

    @Override
    public String getName() { return "My Menu Plugin"; }

    @Override
    public String getVersion() { return "1.0.0"; }

    @Override
    public String getDescription() { return "A plugin that only shows in the Plugin Hub."; }

    @Override
    public Pane getMenuContent(PluginContext context) {
        VBox box = new VBox(15);
        context.loadPluginCss(box, "/plugins/mymenu.css");
        box.getStyleClass().add("my-menu-box");

        Label title = new Label("My Plugin Settings");
        title.setFont(context.getFont(32));

        Label info = new Label("Hello, " + context.getUsername() + "!");
        info.setFont(context.getFont(14));

        box.getChildren().addAll(title, info);
        return box;
    }
}
```

**2. Create `src/main/resources/plugins/mymenu.css`:**

```css
.my-menu-box {
    -fx-padding: 20px;
    -fx-background-color: -dt-bg-color;
}
```

---

### Widget Plugin

A Widget Plugin renders a draggable, resizable component on the **home screen**.

```java
public class MyWidgetPlugin implements WidgetPlugin {

    @Override
    public String getName() { return "My Widget"; }

    @Override
    public String getVersion() { return "1.0.0"; }

    @Override
    public String getDescription() { return "A draggable widget on the home screen."; }

    @Override
    public Region getWidgetContent(PluginContext context) {
        VBox box = new VBox(10);
        context.loadPluginCss(box, "/plugins/mywidget.css");
        box.getStyleClass().add("my-widget-box");
        box.setPrefSize(120, 80);

        Label label = new Label("Widget");
        label.setFont(context.getFont(18));
        box.getChildren().add(label);
        return box;
    }

    // Default position and size on first launch
    @Override public double getDefaultX()    { return 600; }
    @Override public double getDefaultY()    { return 200; }
    @Override public double getDefaultSize() { return 120; }
}
```

---

### Hybrid Plugin

A Hybrid Plugin implements **both** `MenuPlugin` and `WidgetPlugin`. It gets a home screen widget AND an "Open Menu" button in the Plugin Hub.

```java
public class MyHybridPlugin implements MenuPlugin, WidgetPlugin {

    @Override public String getName()        { return "My Hybrid Plugin"; }
    @Override public String getVersion()     { return "1.0.0"; }
    @Override public String getDescription() { return "Widget on home screen + settings in Plugin Hub."; }

    @Override
    public Region getWidgetContent(PluginContext context) {
        VBox box = new VBox(10);
        context.loadPluginCss(box, "/plugins/myhybrid.css");
        box.getStyleClass().add("my-hybrid-widget");
        box.setPrefSize(120, 80);
        // Build your widget UI here
        return box;
    }

    @Override
    public Pane getMenuContent(PluginContext context) {
        Pane pane = new Pane();
        Label title = new Label("Settings");
        title.setFont(context.getFont(32));
        title.setLayoutX(20);
        title.setLayoutY(20);
        pane.getChildren().add(title);
        return pane;
    }

    @Override public double getDefaultX()    { return 600; }
    @Override public double getDefaultY()    { return 200; }
    @Override public double getDefaultSize() { return 120; }
}
```

---

## Compiling and Publishing Your Plugin

With the v0.8.0 release, plugins are dynamically loaded from JAR files. You don't need to modify DockTask's source code to register your plugin!

### 1. The `ServiceLoader` Pattern

For DockTask to find your plugin inside your JAR, you must declare it using Java's `ServiceLoader` standard. 

1. Create a directory in your `src/main/resources` folder: `META-INF/services/`
2. Create a file inside named exactly: `com.ksaifstack.docktask.plugins.DockTaskPlugin`
3. Inside that file, put the fully qualified name of your plugin class:
   ```text
   com.yourname.myplugin.MyHybridPlugin
   ```

### 2. Compiling

Compile your project into a `.jar` file. If your plugin requires external libraries, you must create a "fat jar" (e.g., using Maven shade plugin) that includes those dependencies, *except* for the DockTask core interfaces which are provided by the main app.

> [!TIP]
> **Kotlin Support:** DockTask bundles `kotlin-stdlib` internally! You can write your entire plugin in Kotlin. Your compiled Kotlin JAR will work flawlessly as a DockTask plugin, and you do not need to bundle the Kotlin standard library into your JAR (keeping your plugin size tiny).

### 3. Testing Your Plugin Locally

Before publishing, you'll want to test your compiled `.jar` file to make sure it loads and looks right in DockTask.

It's extremely simple:
1. Compile your plugin into a `.jar` file.
2. Locate your local DockTask installation folder.
3. Open the `Data/plugins/` directory (if it doesn't exist, create it).
4. Drop your `.jar` file directly into that folder.
5. Run DockTask! 

The `PluginManager` automatically scans the `Data/plugins/` directory on startup. If you implemented `ServiceLoader` correctly, your plugin will instantly appear in the Plugin Hub alongside your other installed plugins.

### 4. Submitting to the Community Registry

DockTask features a **"Browse Community Plugins"** hub that fetches directly from the official GitHub registry.

To publish your plugin so any DockTask user can install it with one click:
1. Upload your `.jar` file to a permanent link (e.g., as a GitHub Release asset on your repository).
2. Go to the [DockTask-Plugins Repository](https://github.com/KSaifStack/DockTask-Plugins).
3. Fork the repo, add your plugin to `plugin-registry.json`, and submit a Pull Request!

```json
{
  "name": "My Hybrid Plugin",
  "description": "An amazing plugin for DockTask.",
  "version": "1.0.0",
  "author": "YourName",
  "type": "hybrid",
  "url": "https://github.com/YourName/my-plugin",
  "downloadUrl": "https://github.com/YourName/my-plugin/releases/download/v1.0/myplugin.jar"
}
```

When users click **Install** in DockTask, it will download your JAR from `downloadUrl`, save it to their `Data/plugins/` folder, and automatically load it on the next startup.
---

## Internal Architecture

Understanding how the pieces connect:

```
PluginManager
│   registry: List<DockTaskPlugin>
│   widgetVisibilityListeners: List<Runnable>
│
├── getPlugins()         → all plugins (used by PluginUi for cards)
├── getWidgetPlugins()   → WidgetPlugin only (used by TaskUi)
├── getMenuPlugins()     → MenuPlugin only
├── addWidgetVisibilityListener(Runnable)
└── notifyWidgetVisibilityChanged()
         │
         ▼
      TaskUi.syncPluginWidgets (registered Runnable)
         Reads widget.enabled from PluginState.txt
         Adds or removes the widget node from the pane live
```

```
PluginContext (interface)
└── PluginContextImpl (core app implementation)
        username       → from login session
        pluginName     → scopes all saveState/loadState calls
        FontLoader     → powers getFont()
        UserData       → powers saveState/loadState()
        themeManager   → powers isDarkMode()
```

```
Data/PluginState.txt  (<SEP> format)
│
│  username<SEP>pluginName<SEP>key<SEP>value
│
├── user1<SEP>Pomodoro Timer<SEP>x<SEP>700.0
├── user1<SEP>Pomodoro Timer<SEP>y<SEP>300.0
├── user1<SEP>Pomodoro Timer<SEP>size<SEP>120.0
└── user1<SEP>Pomodoro Timer<SEP>widget.enabled<SEP>true
```

### CSS Inheritance Chain

```
Scene stylesheet (LightTheme.css or DarkTheme.css)
  └── .root { -dt-bg-color: #fff; -dt-border-color: #626262; ... }
        └── Any node in the scene (including plugin widgets)
              └── plugin CSS uses -dt-bg-color → inherits #fff (or #333 in dark)
```

When `themeManager.changeTheme()` is called, the scene stylesheet is swapped and JavaFX re-evaluates all CSS — plugin widgets update automatically with no listener or callback needed.

---

*Built by KSaifStack · DockTask v0.8.0*
