package com.ksaifstack.docktask.plugins;

public interface DockTaskPlugin {
    String getName();
    String getVersion();
    String getDescription();
    String getType();

    /**
     * Human-readable access/permissions summary shown in the plugin hub UI.
     * Currently inferred from implemented plugin interfaces.
     */
    default String getAccess() {
        java.util.List<String> perms = new java.util.ArrayList<>();
        if (this instanceof WidgetPlugin) perms.add("TASK_READ");
        if (this instanceof MenuPlugin) perms.add("STORAGE");
        return perms.isEmpty() ? "NONE" : String.join(" · ", perms);
    }

    default String getAuthor() {
        return "KsaifStack";
    }
}
