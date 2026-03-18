package com.ksaifstack.docktask.ui;
import javafx.scene.text.Font;
/*
* Font Loader
 */

public class FontLoader {
    public Font lexend(int size) {
        return Font.loadFont(FontLoader.class.getResourceAsStream("/fonts/Lexend.ttf"), size);
    }
    public static Font setFont(int size){
        return Font.loadFont(FontLoader.class.getResourceAsStream("/fonts/Lexend.ttf"), size);
    }
}
