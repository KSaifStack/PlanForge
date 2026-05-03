package com.ksaifstack.docktask.model;
/**
 * Todolist 
 * Tasks: Title(Name of Task), --
*Description(An option to put more information or hyperlinks for the task),--
 Due Date(What day this task is due or is happening), 
Priority via Ranks
it will put that task over other task depending in what category/group that the task is in ), --
 Task are removable at any given moment
Categories: Made by the user allowing complete experience--
Reminders: Notifications for upcoming deadlines
Search & Filter: Categorization, Priority-based Filtering --
Progress Tracking: Data Persistence will allow logins --

 * 
 * @author KsaifStack
 * @version 0.5.0
 */

 import java.time.LocalDateTime;
 import java.time.format.DateTimeFormatter;
 import java.util.Scanner;


 public class Todolist {
 
     public static void main(String[] args) {
         Scanner scanner = new Scanner(System.in);
         //Checks if you have a account already
         boolean LoggedIn = false;
         String cuser = "";


         while (LoggedIn == false) {
             System.out.println("Do you already have a account?");
             System.out.print("Y for Yes and N for No: ");
             String answer = scanner.nextLine();
             answer = answer.toLowerCase();
             System.out.println(answer);
             //Sign-up
             if (answer.equals("n")) {
                 System.out.println("--Sign-Up--");
                 //Sign up Username
                 System.out.print("Pick a Username: ");
                 String newuser = scanner.nextLine();
                 newuser = newuser.toLowerCase();

                 //Sign up Password
                 System.out.print("Pick a Password: ");
                 String newpassword = scanner.nextLine();
                 newpassword = newpassword.toLowerCase();

                 //Saves Data to file
                 if(UserData.usernameExists(newuser)==true){
                    System.out.println("ERROR: User is already in the system.");
                 }
                 //Make it find the user in the user file and Print a error if otherwise.
                 else
                 {
                    UserData.saveUser(newuser, newpassword);
                 System.out.println("Welcome " + newuser + "!");
                 LoggedIn = true;
                 cuser = newuser; 
                 }
             }
             //Login
             else if (answer.equals("y")) {
                 System.out.println("--Log-in--");
                 //Login Username
                 System.out.print("Username: ");
                 String user = scanner.nextLine();
                 user = user.toLowerCase();
                 //Login Password
                 System.out.print("Password: ");
                 String password = scanner.nextLine();
                 password = password.toLowerCase();
                 if (UserData.findUser(user, password)) {
                     System.out.println("Welcome " + user + "!");
                     LoggedIn = true;
                     cuser = user;
                     //Makes sure current user meet requrments. 
                     cuser.trim().toLowerCase();
                 } else {
                     System.out.println("ERROR: Cannot find user within file.");
                 }
             }
             //if the user didnt pick Y or N
             else {
                 System.out.println("Please pick from Y or N");
             }
         }




         //make varaible called login and make it become true if they are connected to a user or return in error if false.
         while (LoggedIn==true) {
             String[][] tasks = UserData.ReturnData(cuser);

             System.out.println("To-Do List");
             System.out.println("Options: ");
             System.out.println("1. Create Task");
             System.out.println("2. Show all Task");
             System.out.println("3. Remove Task");
             System.out.println("4. Update Task");
             System.out.println("5. Track Progress");
             System.out.println("6. Exit");
             
             int choice = scanner.nextInt();
             scanner.nextLine();
             // Creating Task
              if (choice == 1) {
                //Title
                 System.out.print("What would you like to name your Task?: ");
                  String taskname = scanner.nextLine();
                 taskname = taskname.toLowerCase();
                  for(String[] task:tasks) {
                      if (task[0].equals(taskname)) {
//                          System.out.println("ERROR: Cannot use same task name.");
                          taskname = scanner.nextLine();
                      } else {
                      }
                  }

                 //Description 
                 System.out.print("Please list a description of this task: ");
                 String taskdescription = scanner.nextLine();
                 taskdescription = taskdescription.toLowerCase();
                 //Priority 
                 System.out.print("Please rank this task importance from 1-5: ");
                 int taskrank = scanner.nextInt();
                 scanner.nextLine();
                 //What group of task it belongs in
                 System.out.print("What type of task does this fall under?: ");
                 String taskgroup = scanner.nextLine();
                 //Due Date 
                 System.out.print("Enter due date and time (yyyy MM DD hh mm AM/PM): ");
                 String dueInput = scanner.nextLine();
                 LocalDateTime dueDate = LocalDateTime.parse(dueInput,DateTimeFormatter.ofPattern("yyyy MM dd hh mm a"));

                 System.out.println(taskname + ", " + taskdescription + ", " + taskrank);
                 UserData.SaveTask(cuser,taskname, taskdescription, taskrank, taskgroup,dueDate);
                 //Due Date(will work in a hour basis will ask the user how many days )

             }
             // Print all Task
             else if (choice == 2) {
                
                UserData.FindData(cuser);
             }
             // Remove Task
             else if (choice == 3) {
                System.out.println("What task would you like to remove?: ");
                String task = scanner.nextLine();
                UserData.removeTask(cuser, task);
             }
             // Track the progress of tasks due and not due
             else if (choice == 4) {
                 //UserData.updateTask(newuser, taskname, NewDescription, NewRank,date);
                 System.out.println("What is the name of the task youll like to change: ");
                 String taskname = scanner.nextLine();
                  System.out.println("What is the new name youll like to give for the task: ");
                  String newtaskname= scanner.nextLine();
                 System.out.println("What is the new description of the task?: ");
                 String ndes = scanner.nextLine();
                 System.out.println("What is the new rank youll like to give: ");
                 int NewRank = scanner.nextInt();
                 scanner.nextLine();
                 System.out.println("What group would you like to change it to: ");
                 String TaskGroup = scanner.nextLine();
                 System.out.println("New due date and time (yyyy MM DD hh mm AM/PM): ");
                 String NewDatein = scanner.nextLine();
                 LocalDateTime dueDate = LocalDateTime.parse(NewDatein,DateTimeFormatter.ofPattern("yyyy MM dd hh mm a"));
                 UserData.updateTask(cuser, taskname,newtaskname, ndes, NewRank,TaskGroup,dueDate);
             }
             
             // Track Progress 
             else if (choice == 5) {
                 UserData.FindData(cuser);
                 System.out.println("What task would you like track?: ");
                 String taskname = scanner.nextLine();
                 taskname.trim().toLowerCase();
                 UserData.DateChecker(taskname);
             }

             //Exit the program
             else if (choice == 6) {
                System.out.println("Exiting the program...");
                 scanner.close();
                 break;
             } 
             else {
                System.out.println("Invalid choice. Please enter a valid option.");
             }




         } //end while 
         
     }//end main
 }// end class
 