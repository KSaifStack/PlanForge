package com.ksaifstack.docktask.util;

import com.ksaifstack.docktask.ui.LoginUi;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.Tooltip;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.text.Font;

public class HyperlinkTextArea extends TextArea {
    private Pane rootPane; 
    private Button expandBtn;

    public HyperlinkTextArea(Pane rootPane, String promptText, double prefWidth, double prefHeight) {
        this.rootPane = rootPane;

        this.setFont(FontLoader.setFont(14));
        this.setPromptText(promptText);
        this.setPrefSize(prefWidth, prefHeight);
        this.setWrapText(true);
        this.setTooltip(new Tooltip("Hold Ctrl and click a link to open it!"));

        this.addEventFilter(MouseEvent.MOUSE_CLICKED, e -> {
            if (e.isControlDown()) {
                handleCtrlClick(this);
                e.consume();
            }
        });

        expandBtn = new Button("⤢");
        expandBtn.setTooltip(new Tooltip("Full Screen"));
        expandBtn.getStyleClass().add("hyperlink-expand-btn");
        
        expandBtn.setOnAction(e -> openFullscreenOverlay());
    }

    public Button getExpandButton() {
        return expandBtn;
    }

    private void handleCtrlClick(TextArea area) {
        int caret = area.getCaretPosition();
        String text = area.getText();
        if (text == null || text.isEmpty() || caret < 0 || caret >= text.length()) return;

        int start = caret;
        while (start > 0 && !Character.isWhitespace(text.charAt(start - 1))) {
            start--;
        }

        int end = caret;
        while (end < text.length() && !Character.isWhitespace(text.charAt(end))) {
            end++;
        }

        if (start < end) {
            String word = text.substring(start, end).trim();
            if (word.startsWith("http://") || word.startsWith("https://")) {
                LoginUi.openURL(word);
            }
        }
    }

    private void openFullscreenOverlay() {
        Pane overlay = new Pane();
        overlay.setPrefSize(980, 493);
        overlay.getStyleClass().add("hyperlink-overlay");

        TextArea fullTextArea = new TextArea(this.getText());
        fullTextArea.setFont(FontLoader.setFont(18));
        fullTextArea.setWrapText(true);
        fullTextArea.setPrefSize(800, 400);
        fullTextArea.setLayoutX(90);
        fullTextArea.setLayoutY(46); // Centered vertically in the 493px height container
        fullTextArea.setTooltip(new Tooltip("Hold Ctrl and click a link to open it!"));
        
        fullTextArea.addEventFilter(MouseEvent.MOUSE_CLICKED, e -> {
            if (e.isControlDown()) {
                handleCtrlClick(fullTextArea);
                e.consume();
            }
        });

        Button closeBtn = new Button("X");
        closeBtn.setFont(FontLoader.setFont(16));
        closeBtn.setPrefSize(30, 30);
        closeBtn.setLayoutX(895); // Just to the right of the textbox
        closeBtn.setLayoutY(46);  // Aligned with the top of the textbox
        closeBtn.getStyleClass().add("hyperlink-close-btn");

        closeBtn.setOnAction(e -> {
            this.setText(fullTextArea.getText());
            rootPane.getChildren().remove(overlay);
        });

        overlay.getChildren().addAll(fullTextArea, closeBtn);
        rootPane.getChildren().add(overlay);
        overlay.toFront();
    }
}
