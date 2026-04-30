package com.ksaifstack.docktask.model;

import java.util.ArrayList;
/**
 *
 * @author  Kareem 
 * @version August 26, 2025
 */
public class TaskManagement {
    private ArrayList<Task> tasks;

    public TaskManagement() {
        tasks = new ArrayList<>();
    }

    //Add Task 
    public String AddTask(Task task) {
        for (Task t : tasks) {
            if (t.getTaskName().equals(task.getTaskName())) {
                return "A task with the same name already exists.";
            }
        }
        tasks.add(task);
        return "Task added successfully.";
    }

    //Remove Task
    public String RemoveTask(Task task) {
        for (Task t : tasks) {
            if (t.getTaskName().equals(task.getTaskName())) {
                tasks.remove(t);
                return "Task removed successfully.";
            }
        }
        return "Task not found";
    }

    public boolean taskExists(Task task) {
        return tasks.contains(task);
    }






    //Prints all Tasks
    public void printallTask() {
        if (tasks.isEmpty()) {
            System.out.println("No Tasks found.");
        } else {
            for (Task task : tasks) {
                System.out.println(task.taskInfo());
            }
        }
    }




}
