package com.ksaifstack.docktask.model;
/**
 * Allows the user to Create Tasks.
 * This should have the function to:
 * -Create tasks with there own ids
 * -Allow the user the give a title for the task
 * -Allow the user to give a description for a task 
 * -Allow the user the put a task over the other
 * -Allow the user to change/update its status
 * @author  Kareem
 * @version August 26, 2025
 */
import java.util.ArrayList;

public class Task {
    private String TaskName;
    private String TaskDescription;
    private String TaskGroup;
    private int TaskRank;
    private ArrayList<String> tasks;


    public Task(String taskname, String taskdescription, int taskrank,String TaskGroup) {
        this.TaskName = taskname;
        this.TaskDescription = taskdescription;
        this.TaskGroup = TaskGroup;
        this.TaskRank = taskrank;
        this.tasks = new ArrayList<>();
    }

    //Add Task
    public String addTask(String task) {
        if (!tasks.contains(task)) {
            tasks.add(task);
            return task;
        }
        return "-99;";
    }
    //Remove Task
    public String removeTask(String task) {
        if (tasks.contains(task)) {
            tasks.remove(task);
            return task;
        }
        return "-99";
    }

    //Get Task Rank 
    public int getTaskrank() {
        return TaskRank;
    }

    //Get Task Name 
    public String getTaskName() {
        return TaskName;
    }
    
    //Get Task Group
    public String getTaskGroup() {
        return TaskGroup;
    }
    

    //Task info 
    public String taskInfo() {
        return "Task: " + TaskName + "\nTask Desciption: " + TaskDescription + "\ntask current ranking: " + TaskRank + "\nTask Group: " + TaskGroup;
    }



    
}
