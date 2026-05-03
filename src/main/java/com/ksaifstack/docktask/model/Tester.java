package com.ksaifstack.docktask.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

public class Tester {
    public static void main(String[] args) {
        String[][] unorganizedTasks = {
                {"Fix_Sink_802", "Leaky faucet", "5", "Personal", "2026 05 03 04 00 PM"},
                {"Email_Client_267", "Quarterly stats", "1", "Work", "2026 05 01 11 00 AM"},
                {"Submit_Report_344", "Urgent fix", "10", "Work", "2026 05 07 09 00 AM"},
                {"Buy_Milk_234", "Grocery run", "3", "Personal", "2026 05 09 06 00 AM"},
                {"Update_Docs_610", "Markdown files", "5", "Personal", "2026 05 07 11 00 AM"},
                {"Fix_Bug_571", "Agenda creation", "8", "Work", "2026 05 13 01 00 AM"},
                {"Gym_Session_262", "Leg day", "5", "Personal", "2026 05 05 04 00 PM"},
                {"Update_Docs_935", "Follow up", "1", "Admin", "2026 05 07 02 00 AM"}
        };

        // Use the exact same pattern as in TaskManagement
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy MM dd hh mm a");
        // Set "now" to May 3, 2026 at 12:00 PM (noon)
        LocalDateTime now = LocalDateTime.parse("2026 05 03 12 00 PM", fmt);

        TaskManagement manager = new TaskManagement(unorganizedTasks);

        System.out.println("--- Sorted Task List (by Priority) ---");
        for (Map.Entry<String, String[]> entry : manager.returnList().entrySet()) {
            String[] data = entry.getValue();
            System.out.printf("Task: %-18s | Group: %-10s | Importance: %s | Due: %s%n",
                    entry.getKey(), data[2], data[1], data[3]);
        }
    }
}