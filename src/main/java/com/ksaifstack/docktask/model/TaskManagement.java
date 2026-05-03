package com.ksaifstack.docktask.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * @author  Kareem 
 * @version May 3rd, 2026
 * This class will handle all Task data using linkedhashmaps allow easy pulling and sorting of data.
 */
public class TaskManagement {

    private String[][] Tasks;
    private LinkedHashMap<String, String[]> Stasks = new LinkedHashMap<>();

    private static LinkedHashMap<String, String[]> cache = null;
    private static boolean isDirty = true;
    private static String lastUsername = null;

    public static void markDirty() {
        isDirty = true;
    }

    public static LinkedHashMap<String, String[]> getSortedTasks(String username) {
        if (!isDirty && cache != null && username.equals(lastUsername)) {
            return cache;
        }
        String[][] tasks = UserData.ReturnData(username);
        if (tasks == null || tasks.length == 0) {
            cache = new LinkedHashMap<>();
            lastUsername = username;
            isDirty = false;
            return cache;
        }
        TaskManagement tm = new TaskManagement(tasks);
        cache = tm.returnList();
        lastUsername = username;
        isDirty = false;
        return cache;
    }

    public TaskManagement(String[][] Tasks) {
        this.Tasks = Tasks;
        add();
    }

    public LinkedHashMap<String, String[]> returnList() {

        return Stasks;
    }

    private String[] returnTask(String taskname){

        return Stasks.get(taskname);
    }

    private double calcGroup(String groupName) {
        int totalImportance = 0;
        int count = 0;

        for (String[] task : Stasks.values()) {
            if (task[2].equals(groupName)) {
                totalImportance += Integer.parseInt(task[1]); // importance
                count++;
            }
        }

        if (count == 0) return 1; // fallback

        return (double) totalImportance / count;
    }

    private double calcOverdue(long time){
        long OverduePenalty = 0;
        if(time<0){
            OverduePenalty= Math.abs(time) * 2;

        }
        return OverduePenalty;
    }

    //(groupWeight × Rank) / (timeRemaining + 1 + OverduePenalty)
    // If a task is overdue already it will be given a calcuation that will
    private double calcTasks(String[] Task, DateTimeFormatter fmt, LocalDateTime now){
        double groupWeight = calcGroup(Task[2]);
        int importance = Integer.parseInt(Task[1]);
        LocalDateTime deadline = LocalDateTime.parse(Task[3], fmt);
        long timeRemaining = java.time.Duration.between(now, deadline).toHours();
        double OverduePenalty = calcOverdue(timeRemaining);
        return (groupWeight * importance) / (timeRemaining + 1 + OverduePenalty);
    }

    private void add() {
        for (String[] row : Tasks) {
            Stasks.put(row[0], new String[]{row[1], row[2], row[3], row[4]});
        }
        sort();
    }
    private void sort(){
        List<Map.Entry<String, String[]>> entryList = new ArrayList<>(Stasks.entrySet());
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy MM dd hh mm a");
        LocalDateTime now = LocalDateTime.now();
        
        Map<String, Double> scores = new HashMap<>();
        for (Map.Entry<String, String[]> entry : entryList) {
            scores.put(entry.getKey(), calcTasks(entry.getValue(), fmt, now));
        }

        entryList.sort((e1, e2) -> Double.compare(
            scores.get(e2.getKey()),
            scores.get(e1.getKey())
        ));
        LinkedHashMap<String, String[]> sorted = new LinkedHashMap<>();
        for (Map.Entry<String, String[]> entry : entryList) {
            sorted.put(entry.getKey(), entry.getValue());
        }
        Stasks.clear();
        Stasks = sorted;

    }

}

