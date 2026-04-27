package com.ksaifstack.docktask.util;

import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.Region;
import javafx.scene.text.Font;

public class DraggableWidget {

    public interface SavePositionCallback {
        void onSave(double x, double y, double size);
    }

    public interface ActionCallback {
        void onAction();
    }

    /**
     * Makes a Button a draggable, resizable (via scroll wheel), and lockable widget.
     * Right-click to unlock. Left-click & drag to move. Scroll to resize. Left-click twice to lock.
     */
    public static void makeDraggable(Button widgetNode, Node followerNode, double initialX, double initialY, double initialSize, SavePositionCallback saveCallback, ActionCallback actionCallback) {
        
        widgetNode.setLayoutX(initialX);
        widgetNode.setLayoutY(initialY);
        widgetNode.setPrefWidth(initialSize);
        widgetNode.setPrefHeight(initialSize);
        widgetNode.setFont(Font.font(initialSize * 0.4));
        
        Runnable syncFollower = () -> {
            if (followerNode != null) {
                double fWidth = 100;
                if (followerNode instanceof Region) {
                    fWidth = ((Region) followerNode).getPrefWidth();
                    if (fWidth <= 0) fWidth = 100;
                }
                followerNode.setLayoutX(widgetNode.getLayoutX() + (widgetNode.getPrefWidth() - fWidth) / 2);
                followerNode.setLayoutY(widgetNode.getLayoutY() + widgetNode.getPrefHeight() + 5);
            }
        };

        syncFollower.run();

        final double[] dragDelta = new double[2];
        final boolean[] isMoveable = {false};
        final boolean[] wasJustMadeNormal = {false};
        final boolean[] wasDragged = {false};

        widgetNode.setOnMousePressed(evt -> {
            if (evt.getButton() == MouseButton.SECONDARY) {
                if (!isMoveable[0]) {
                    isMoveable[0] = true;
                    widgetNode.setStyle("-fx-border-color: #4CAF50; -fx-border-width: 2px; -fx-border-style: dashed;");
                }
                evt.consume();
            }
            dragDelta[0] = widgetNode.getLayoutX() - evt.getSceneX();
            dragDelta[1] = widgetNode.getLayoutY() - evt.getSceneY();
            wasDragged[0] = false;
            widgetNode.toFront();
            if (followerNode != null) followerNode.toFront();
        });

        widgetNode.setOnMouseDragged(evt -> {
            if (!isMoveable[0]) return; 
            
            wasDragged[0] = true;
            double newX = evt.getSceneX() + dragDelta[0];
            double newY = evt.getSceneY() + dragDelta[1];
            
            // Clamp within bounds (Assuming 980x493 for DockTask)
            newX = Math.max(0, Math.min(newX, 980 - widgetNode.getPrefWidth()));
            newY = Math.max(0, Math.min(newY, 493 - widgetNode.getPrefHeight()));
            
            widgetNode.setLayoutX(newX);
            widgetNode.setLayoutY(newY);
            syncFollower.run();
            
            evt.consume();
        });

        widgetNode.setOnMouseReleased(evt -> {
            if (evt.getButton() == MouseButton.PRIMARY) {
                if (isMoveable[0]) {
                    if (!wasDragged[0]) {
                        isMoveable[0] = false;
                        widgetNode.setStyle("");
                        wasJustMadeNormal[0] = true;
                    }
                    if (saveCallback != null) {
                        saveCallback.onSave(widgetNode.getLayoutX(), widgetNode.getLayoutY(), widgetNode.getPrefWidth());
                    }
                    evt.consume();
                } else if (wasJustMadeNormal[0]) {
                    evt.consume();
                }
            }
        });

        widgetNode.setOnScroll(evt -> {
            if (isMoveable[0]) {
                double delta = evt.getDeltaY() > 0 ? 10 : -10;
                double newSize = widgetNode.getPrefWidth() + delta;
                newSize = Math.max(50, Math.min(newSize, 300));
                widgetNode.setPrefWidth(newSize);
                widgetNode.setPrefHeight(newSize);
                widgetNode.setFont(Font.font(newSize * 0.4));
                syncFollower.run();
                evt.consume();
            }
        });

        widgetNode.setOnAction(e -> {
            if (wasJustMadeNormal[0]) {
                wasJustMadeNormal[0] = false;
                return;
            }
            if (isMoveable[0]) {
                return;
            }
            if (actionCallback != null) {
                actionCallback.onAction();
            }
        });
    }
}
