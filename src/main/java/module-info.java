module com.ksaifstack.docktask {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;
    requires java.desktop;

    requires org.apache.commons.io;

    requires com.sun.jna;
    requires com.sun.jna.platform;
    requires SystemTray;
    requires com.dustinredmond.fxtrayicon;
    requires nfx.core;
    requires java.net.http;
    requires kotlin.stdlib;

    uses com.ksaifstack.docktask.plugins.DockTaskPlugin;

    opens com.ksaifstack.docktask to javafx.graphics;
    opens com.ksaifstack.docktask.ui to javafx.graphics, javafx.fxml;
    opens com.ksaifstack.docktask.model to javafx.base;

    exports com.ksaifstack.docktask;
    exports com.ksaifstack.docktask.ui;
    exports com.ksaifstack.docktask.model;
    exports com.ksaifstack.docktask.plugins;
    exports com.ksaifstack.docktask.util;
    opens com.ksaifstack.docktask.util to javafx.fxml, javafx.graphics;
}