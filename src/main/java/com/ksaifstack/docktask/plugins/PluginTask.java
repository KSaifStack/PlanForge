package com.ksaifstack.docktask.plugins;

/**
 * A read-only representation of a DockTask task provided to plugins.
 * Plugins can read task data but cannot modify data.
 */
public record PluginTask(
    String name,
    String description,
    String dueDate,
    String color
) {}
