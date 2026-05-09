package com.ksaifstack.docktask.util;

import javafx.scene.control.*;
import javafx.scene.layout.Pane;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class CustomDatePicker extends Pane {

    private final DatePicker        datePicker = new DatePicker();
    private final ComboBox<Integer> hourBox    = new ComboBox<>();
    private final ComboBox<Integer> minuteBox  = new ComboBox<>();
    private final ComboBox<String>  amPmBox    = new ComboBox<>();

    public CustomDatePicker() {
        setPrefSize(450, 100);
        // Only real child controls should consume clicks; let empty pane area pass through.
        setPickOnBounds(false);

        for (int i = 1;  i <= 12; i++)    hourBox.getItems().add(i);
        for (int i = 0;  i <  60; i += 5) minuteBox.getItems().add(i);
        amPmBox.getItems().addAll("AM", "PM");

        hourBox.setValue(12);
        minuteBox.setValue(0);
        amPmBox.setValue("AM");

        // Positions
        hourBox.setLayoutX(285);
        hourBox.setLayoutY(35);
        minuteBox.setLayoutX(345);
        minuteBox.setLayoutY(35);
        amPmBox.setLayoutX(405);
        amPmBox.setLayoutY(35);
        datePicker.setLayoutX(288);
        datePicker.setLayoutY(65);

        datePicker.setOnAction(e -> updateSelectedText());
        hourBox.setOnAction(e    -> updateSelectedText());
        minuteBox.setOnAction(e  -> updateSelectedText());
        amPmBox.setOnAction(e    -> updateSelectedText());

        getChildren().addAll(datePicker, hourBox, minuteBox, amPmBox);
    }

    private void updateSelectedText() {
        getDateTime();
    }

    public LocalDateTime getDateTime() {
        LocalDate date   = datePicker.getValue();
        Integer   hour12 = hourBox.getValue();
        Integer   minute = minuteBox.getValue();
        String    amPm   = amPmBox.getValue();

        if (date == null || hour12 == null || minute == null || amPm == null) return null;
        return LocalDateTime.of(date, LocalTime.of(convertTo24Hour(hour12, amPm), minute));
    }

    public void setDateTime(LocalDateTime dateTime) {
        if (dateTime == null) return;
        datePicker.setValue(dateTime.toLocalDate());
        int hour24 = dateTime.getHour();
        hourBox.setValue(convertTo12Hour(hour24));
        minuteBox.setValue(dateTime.getMinute());
        amPmBox.setValue(hour24 < 12 ? "AM" : "PM");
        updateSelectedText();
    }

    public static String formatDateTime(LocalDateTime dt) {
        return dt.format(DateTimeFormatter.ofPattern("yyyy MM dd hh mm a"));
    }

    private int convertTo24Hour(int hour12, String amPm) {
        if (amPm.equalsIgnoreCase("AM")) return hour12 == 12 ? 0  : hour12;
        else                             return hour12 == 12 ? 12 : hour12 + 12;
    }

    private int convertTo12Hour(int hour24) {
        int h = hour24 % 12;
        return h == 0 ? 12 : h;
    }
}