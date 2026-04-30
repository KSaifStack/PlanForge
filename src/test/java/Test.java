import com.ksaifstack.docktask.model.Task;
import com.ksaifstack.docktask.model.UserData;
import com.ksaifstack.docktask.model.TaskManagement;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * NOTE: THIS IS A PROTYPE JUST A FRAMEWORK HOW IT SHOULD BE
 * Design classes for task management, user preferences, and data persistence
 * Implement core functionality such as adding, updating, deleting, and organizing tasks
 * Integrate advanced features such as task prioritization, deadlines, and recurring tasks, and finalize the system
 * @author  Kareem 
 * @version November 7th, 2025
 */
public class Test {
    public static void main(String[] args) {
        TaskManagement TaskManagement = new TaskManagement();
        //Test Counter
        int ExpectedScore = 10;
        int counting = 0;

        //Test Tasks
        String taskname = "Testing";
        String taskdescription = "This is a test";
        String TaskGroup = "Homework";
        int taskrank = 1;
        Task test = new Task(taskname, taskdescription, taskrank,TaskGroup);
        //Test Date
        String InputDate = "2025 04 27 11 59 PM";
        LocalDateTime dueDate = LocalDateTime.parse(InputDate, DateTimeFormatter.ofPattern("yyyy MM dd hh mm a"));
        //Test User
        String newuser = "Apple";
        newuser.toLowerCase();
        String newpassword = "Applesarereallygoodforyou1";

        //Check if you can add Task
        System.out.println("Adding Task....");
        TaskManagement.AddTask(test);
        if (TaskManagement.taskExists(test)) {
            counting += 1;
            System.out.println("Task was added. "+counting+"/5");
        } else {
            System.out.println("Task was not added please fix");
        }
        //Check if you can remove Task
        System.out.println("Removing Task...");
        TaskManagement.RemoveTask(test);
        if (TaskManagement.taskExists(test)) {
            System.out.println("Task was not remove please fix");
        } else {
            counting += 1;
            System.out.println("Task was removed. "+counting+"/5");
        }
        //Check if you can make a user
        System.out.println("Adding User...");
        UserData.saveUser(newuser, newpassword);
        //Check if that data saves
        System.out.println("Finding User...");
        if (UserData.findUser(newuser, newpassword)) {
            counting += 2;
            System.out.println("User was Added and Found. "+counting+"/5");
        }
        //Check if you can print all task 
        TaskManagement.AddTask(test);
        System.out.println("printing Tasks...");
        TaskManagement.printallTask();
        counting += 1;
        System.out.println("Task printed.");
        TaskManagement.RemoveTask(test);

        //Add Task to data
        UserData.SaveTask(newuser, taskname, taskdescription, taskrank, TaskGroup,dueDate);
        counting += 1;

        //Update Task in data
        String NewDescription = "javajavajava";
        int NewRank = 5;
        String newName="Updated name";
        UserData.updateTask(newuser, taskname,newName, NewDescription, NewRank,TaskGroup,dueDate);
        counting += 1;

        //Remove Task to data
        UserData.removeTask(newuser, taskname);
        UserData.SaveTask(newuser, taskname, taskdescription, taskrank, TaskGroup,dueDate);
        counting += 1;

        //Find task in data
        System.out.println("Finding Task... ");
        UserData.FindData(newuser);
        counting +=1;

        //Check Assign date
        UserData.DateChecker(taskname);
        counting+=1;

        System.out.println("Expected score was "+ExpectedScore+"/"+ExpectedScore+ " score was "+counting+"/"+ExpectedScore+"!");
        System.out.println("Cleaning up...");
        UserData.removeTask(newuser,taskname);
        UserData.removeTask(newuser,newName);
    }
}
