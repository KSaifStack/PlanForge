package com.ksaifstack.docktask.model;

import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TaskManagementTest {

    @Test
    public void testTaskSortingAlgorithm() {
        // Use the exact date formatter the app uses
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy MM dd hh mm a");
        LocalDateTime now = LocalDateTime.now();

        // Dynamically generate dates relative to right now so the test never fails due to time passing
        String dueIn1Hour = now.plusHours(1).format(fmt);
        String dueIn5Hours = now.plusHours(5).format(fmt);
        String dueIn10Hours = now.plusHours(10).format(fmt);
        String overdue2Hours = now.minusHours(2).format(fmt);

        // Task Array Format expected by TaskManagement constructor:
        // [taskname, description, rank, group, dueDate]
        String[][] mockTasks = new String[][] {
            // Task A: Score = 2.0 (Work group weight 2, rank 2, time 1) -> (2 * 2) / (1 + 1) = 2.0
            {"Task A", "Desc A", "2", "Work", dueIn1Hour},
            
            // Task B: Score = 1.33 (Work group weight 2, rank 4, time 5) -> (2 * 4) / (5 + 1) = 1.33
            {"Task B", "Desc B", "4", "Work", dueIn5Hours},
            
            // Task C: Score = 0.09 (Home group weight 1, rank 1, time 10) -> (1 * 1) / (10 + 1) = 0.09
            {"Task C", "Desc C", "1", "Home", dueIn10Hours},
            
            // Task D: Score = 1.66 (Urgent group weight 1, rank 5, overdue -2) -> (1 * 5) / (-2 + 1 + 4) = 1.66
            {"Task D", "Desc D", "5", "Urgent", overdue2Hours}
        };

        // Initialize TaskManagement with the mock data (this triggers the sort algorithm)
        TaskManagement tm = new TaskManagement(mockTasks);
        LinkedHashMap<String, String[]> sortedMap = tm.returnList();
        List<String> sortedTaskNames = new ArrayList<>(sortedMap.keySet());

        // Assert the exact expected order from highest score to lowest
        assertEquals(4, sortedTaskNames.size(), "Should have exactly 4 tasks");
        assertEquals("Task A", sortedTaskNames.get(0), "Task A should be first (Score 2.0)");
        assertEquals("Task D", sortedTaskNames.get(1), "Task D should be second (Score 1.66)");
        assertEquals("Task B", sortedTaskNames.get(2), "Task B should be third (Score 1.33)");
        assertEquals("Task C", sortedTaskNames.get(3), "Task C should be last (Score 0.09)");
    }
}
