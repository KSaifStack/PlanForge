package com.ksaifstack.docktask.ui;

import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.layout.Region;
import javafx.stage.WindowEvent;

public interface WindowActions {
    void setContent(Region newContent);
    void colorChange(String cssColor);
    void toFront();
    Scene getScene();
    void setTitleLabel(String space);
    void setOnCloseRequest(EventHandler<WindowEvent> handler);
    void hide();
    void setHeight(double i);
    void setWidth(double i);
    void setTitle(String s);
    void show();
    ReadOnlyBooleanProperty iconifiedProperty();
}