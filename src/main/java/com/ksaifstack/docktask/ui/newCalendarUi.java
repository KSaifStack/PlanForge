package com.ksaifstack.docktask.ui;
import com.ksaifstack.docktask.model.UserData;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import java.time.*;
import static com.ksaifstack.docktask.ui.FontLoader.setFont;

public class newCalendarUi {
    private String username;
    private YearMonth currMonth;
    private Label monthLabel = new Label();
    private GridPane calendarGrid = new GridPane();
    private String cellStyle = "-fx-border-color: #626262; -fx-border-radius: 4px; -fx-border-width: 1px;";
    private String cellHover = "-fx-background-color: #d3d3d3; -fx-border-color: #626262; -fx-border-radius: 4px; -fx-background-radius: 4px; -fx-border-width: 1px;";
    private String cellExit = "-fx-border-color: #626262; -fx-border-radius: 4px; -fx-border-width: 1px;";
    private Pane[] cells = new Pane[42];
    private Label[] dayLabels = new Label[42];
    public newCalendarUi(String username){
        this.username=username;
        currMonth= YearMonth.now();
    }
    public Pane getPane(){
        Pane background = new Pane();
        background.setPrefSize(455, 382);
        background.setLayoutX(243.00);
        background.setLayoutY(56.46);
        background.setStyle(" -fx-border-color: #626262; -fx-border-radius: 2px; -fx-border-width: 1px;");

        HBox monthLabelBox = new HBox();
        monthLabelBox.setPrefWidth(455);
        monthLabelBox.setTranslateY(1);
        monthLabelBox.setAlignment(Pos.CENTER);

        monthLabel.setFont(setFont(32));
        monthLabelBox.getChildren().add(monthLabel);
        background.getChildren().add(monthLabelBox);

        Button backArrow = new Button("<");
        backArrow.setFont(setFont(14));
        backArrow.setPrefSize(45, 30);
        backArrow.setTranslateX(5);
        backArrow.setTranslateY(8);

        Button nextArrow = new Button(">");
        nextArrow.setFont(setFont(14));
        nextArrow.setPrefSize(45,30);
        nextArrow.setTranslateX(405);
        nextArrow.setTranslateY(8);

        backArrow.setOnAction(e->{
            currMonth = currMonth.minusMonths(1);
            updateCalender();
        });

        nextArrow.setOnAction(e->{
            currMonth=currMonth.plusMonths(1);
            updateCalender();
        });
        background.getChildren().addAll(backArrow, nextArrow);

        String[] days = {"Sun", "Mon", "Tues", "Wed", "Thurs", "Fri", "Sat"};
        for(int i=0;i< days.length;i++){
            Button colBg = new Button();
            colBg.setFont(setFont(14));
            colBg.setPrefSize(65, 330);
            colBg.setTranslateX(i * 65);
            colBg.setTranslateY(50);
            colBg.setId("CalendarBut");
            background.getChildren().add(colBg);

            Label dayLabel = new Label(days[i]);
            dayLabel.setFont(setFont(12));
            dayLabel.setTranslateX(i * 65);
            dayLabel.setTranslateY(45);
            dayLabel.setAlignment(Pos.CENTER);
            dayLabel.setPrefSize(65, 26);
            dayLabel.setId("Daylabel");
            dayLabel.setStyle("-fx-border-color: #626262; -fx-border-width: 1;");
            background.getChildren().add(dayLabel);
        }
        calendarGrid.setHgap(13);
        calendarGrid.setVgap(6);
        calendarGrid.setLayoutX(6);
        calendarGrid.setLayoutY(75);
        background.getChildren().add(calendarGrid);

        // 42 is the total number of cells a calandar should have
        for(int i=0;i<42;i++){
            Pane cell = new Pane();
            cell.setStyle(cellExit);
            cell.setPrefSize(52, 45);
            cell.setOnMouseEntered(e->cell.setStyle(cellHover));
            cell.setOnMouseExited(e->cell.setStyle(cellExit));

            Label dayOf =  new Label();
            dayOf.setFont(setFont(12));
             cells[i]=cell;
             dayLabels[i]=dayOf;

             int col = i % 7;
             int row = i / 7;
             calendarGrid.add(cell, col, row);
        }

        updateCalender();
        return background;

    }
    /* updates current Calendar data.
    * First it should check if user is null.
    * if not null the function will populate months/cells with the right info.
     */
    public void updateCalender() {
        if (username == null) {
            monthLabel.setText("No user :c");
            return;
        }

        LocalDate today = LocalDate.now();
        LocalDate firstOfMonth = currMonth.atDay(1);
        LocalDate startDate = firstOfMonth.minusDays(firstOfMonth.getDayOfWeek().getValue() % 7);

        String monthName = currMonth.getMonth().toString();
        monthLabel.setText(monthName.charAt(0) + monthName.substring(1).toLowerCase() + " " + currMonth.getYear());

        for (int i = 0; i < 42; i++) {
            Pane cell = cells[i];
            Label dayOf = dayLabels[i];
            LocalDate cellDate = startDate.plusDays(i);

            // Update day number
            dayOf.setText(String.valueOf(cellDate.getDayOfMonth()));
            dayOf.setTranslateX(5);

            // Gray out days not in current month
            dayOf.setStyle(cellDate.getMonth().equals(currMonth.getMonth())
                    ? "" : "-fx-text-fill: #a0a0a0;");

            // Reset cell children to just the day label
            cell.getChildren().clear();
            cell.getChildren().add(dayOf);

            // Today highlighting
            boolean isToday = cellDate.equals(today);
            if (isToday) {
                cell.setStyle("-fx-background-color: #767676; -fx-text-fill: #1b1b1b; -fx-border-color: #626262; -fx-border-radius: 4px; -fx-background-radius: 4px; -fx-border-width: 1px;");
                cell.setOnMouseEntered(e -> cell.setStyle("-fx-background-color: #767676; -fx-border-color: #626262; -fx-border-radius: 4px; -fx-background-radius: 4px; -fx-border-width: 1px;"));
                cell.setOnMouseExited(e -> cell.setStyle("-fx-background-color: #767676; -fx-border-color: #626262; -fx-border-radius: 4px; -fx-background-radius: 4px; -fx-border-width: 1px;"));
            } else {
                cell.setStyle(cellExit);
                cell.setOnMouseEntered(e -> cell.setStyle(cellHover));
                cell.setOnMouseExited(e -> cell.setStyle(cellExit));
            }
        }
    }

}
