package com.ksaifstack.docktask.model;

/**
* Saves the Username and Password from the file
* Allows the user to Manage/login to get,change,modfiy and delete tasks
* Kinda replaced the whole idea of the Arraylist...
* GOALS:
* REMOVEALLTASKS: will delete all tasks saved in file based off username.
* REMOVEUSERS: will remove Selected users saved in file along with task.
* RESETDATA: will remove ALL data by files giving the app a clean slate.
* DataFile: will take DataFile path to allow Extraction.
* Plugins: Add a feature that would allow the user to store the name or a id for the plugin based off the user
* Support new lines for task desc- should be able to make new lines when saving the desc.
@param username the user's name
@param password the user's password
* @author KSaifStack
* @version November 17, 2025
*/

import java.io.*;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import org.apache.commons.io.FileUtils;

public class UserData {

    // Data Files
    private static final String LogData = "Data/User.txt";
    private static final String TaskData = "Data/Task.txt";
    private static final String SettData = "Data/Plugins.txt";

    // Makes data incase app cant detect data.
    public static void initializeData() {
        try {
            File dataDir = new File("Data");

            if (!dataDir.exists()) {
                boolean created = dataDir.mkdirs();
                if (created) {
                    System.out.println("Data directory created.");
                }
            }

            createFileIfMissing(LogData);
            createFileIfMissing(TaskData);
            createFileIfMissing(SettData);

        } catch (IOException e) {
            System.out.println("Failed to initialize data directory: " + e.getMessage());
        }
    }

    // Creates files in case not in folder already
    private static void createFileIfMissing(String path) throws IOException {
        File file = new File(path);
        if (!file.exists()) {
            boolean created = file.createNewFile();
            if (created) {
                System.out.println("Created file: " + path);
            }
        }
    }

    // Returns data folder
    public static File getDatapath() {
        return new File("Data");
    }

    // Creates Setting Data
    public static void createSet(String username) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(SettData, true))) {
            // username<SEP>theme<SEP>textTrue<SEP>widgetX<SEP>widgetY<SEP>widgetSize
            writer.write(username + "<SEP>" + "Light" + "<SEP>" + "textTrue" + "<SEP>" + "775" + "<SEP>" + "140"
                    + "<SEP>" + "150");
            writer.newLine();
            writer.close();
            System.out.println("Setting data created!.");
        } catch (IOException e) {
            System.out.println("An error occurred while saving setting data: " + e.getMessage());
        }
    }

    public static void updateSet(String username, String theme,boolean Vis) {
        List<String> lines = new ArrayList<>();
        String CreateCheck = "textTrue";
        if(!Vis){
            CreateCheck="textFalse";
        }
        try (BufferedReader reader = new BufferedReader(new FileReader(SettData))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("<SEP>");
                if (parts[0].equals(username)) {
                    // Preserve widget position and size if it exists, otherwise use defaults
                    String wx = parts.length >= 5 ? parts[3] : "775";
                    String wy = parts.length >= 5 ? parts[4] : "140";
                    String wsize = parts.length >= 6 ? parts[5] : "150";
                    lines.add(username + "<SEP>" + theme + "<SEP>" + CreateCheck + "<SEP>" + wx + "<SEP>" + wy + "<SEP>"
                            + wsize);
                } else {
                    lines.add(line);
                }
            }

        } catch (IOException e) {
            System.out.println("Error updating file!: " + e.getMessage());
        }
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(SettData))) {
            for (String line : lines) {
                writer.write(line);
                writer.newLine();
            }

        } catch (IOException e) {
            System.out.println("Error with changing Setting data!: " + e.getMessage());
        }
    }

    // Saves the widget (+ button) position and size for a user
    // Stores this position allowing for change upon login
    public static void saveWidgetPosition(String username, double x, double y, double size) {
        List<String> lines = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(SettData))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("<SEP>");
                if (parts[0].equals(username)) {
                    String theme = parts.length >= 2 ? parts[1] : "Light";
                    String textFlag = parts.length >= 3 ? parts[2] : "textTrue";
                    lines.add(username + "<SEP>" + theme + "<SEP>" + textFlag + "<SEP>" + (int) x + "<SEP>" + (int) y
                            + "<SEP>" + (int) size);
                } else {
                    lines.add(line);
                }
            }
        } catch (IOException e) {
            System.out.println("Error reading settings for widget pos: " + e.getMessage());
        }
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(SettData))) {
            for (String line : lines) {
                writer.write(line);
                writer.newLine();
            }
        } catch (IOException e) {
            System.out.println("Error saving widget position: " + e.getMessage());
        }
    }

    // Loads the widget (+ button) position and size for a user; returns default
    // {775, 140, 150} if not found
    public static double[] loadWidgetPosition(String username) {
        try (BufferedReader reader = new BufferedReader(new FileReader(SettData))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("<SEP>");
                if (parts[0].equals(username) && parts.length >= 5) {
                    double wx = Double.parseDouble(parts[3].trim());
                    double wy = Double.parseDouble(parts[4].trim());
                    double wsize = parts.length >= 6 ? Double.parseDouble(parts[5].trim()) : 150.0;
                    return new double[] { wx, wy, wsize };
                }
            }
        } catch (Exception e) {
            System.out.println("Error loading widget position: " + e.getMessage());
        }
        return new double[] { 775, 140, 150 };
    }

    public static String importSetting(String username,int SettingOption){
        String set = null;
        try (BufferedReader reader = new BufferedReader(new FileReader(SettData))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("<SEP>");
                if (parts[0].equals(username)) {
                    set = parts[SettingOption];
                }
            }
        } catch (FileNotFoundException e) {
            System.out.println("File not found!: " + e.getMessage());
        } catch (IOException e) {
            System.out.println("Error loading Setting!" + e.getMessage());
        }
        if (set == null) {
            createSet(username);
            return set;
        } else {
            return set;
        }
    }
    // Imports theme changes to settings
    public static String importTheme(String username) {
        String theme = null;
        try (BufferedReader reader = new BufferedReader(new FileReader(SettData))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("<SEP>");
                if (parts[0].equals(username)) {
                    theme = parts[1];
                }
            }
        } catch (FileNotFoundException e) {
            System.out.println("File not found!: " + e.getMessage());
        } catch (IOException e) {
            System.out.println("Error loading theme!" + e.getMessage());
        }
        if (theme == null) {
            createSet(username);
            return "Light";
        } else {
            return theme;
        }
    }

    /**
     * Data is exported using the following:
     * 1. have the user pick where they would like the data to be cloned to.
     * 2. clone the current data into a new file
     * 3. import the newly made file into the dir.
     * Uses FileUtils from apache.commons.io library.
     * 
     * @param nFile
     * new files dir
     */
    public static boolean exportData(File nFile) {
        File Data = getDatapath();
        try {
            String timestamp = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss")
                    .format(new Date());
            File exportFolder = new File(nFile, "Data " + timestamp);
            FileUtils.copyDirectory(Data, exportFolder);
            return true;

        } catch (Exception e) {
            System.out.println(e);
            return false;
        }
    }

    /**
     * {@code checkDataPath}
     * 'checkDataPath' checks if the selected file is the correct file for the app
     * it checks for the name of the folder,checks if its empty,then checks its
     * contents for the following:
     * User.txt,Plugin.txt,Settings.txt
     * if all three are met it will
     * 
     * @return true
     *         otherwise it will
     * @return false
     **/
    public static boolean checkDataPath(File newFile) {
        if (newFile.getName().equals("Data")) {
            File[] listedFiles = newFile.listFiles();
            if (listedFiles == null || listedFiles.length == 0) {
                System.out.println("Its empty");
                return false;
            }
            boolean hasUser = false, hasTask = false, hasPlug = false;
            for (File data : listedFiles) {
                String name = data.getName();
                switch (name) {
                    case "User.txt" -> hasUser = true;
                    case "Task.txt" -> hasTask = true;
                    case "Plugins.txt" -> hasPlug = true;
                }
            }
            return hasUser && hasTask && hasPlug;
        }
        return false;
    }

    /**
     * {@code setDataPath}
     * This functions allows us to set the DataPath to the imported folder if
     * checkDataPath is true.
     * Since all settings are meant to be user based this will only affect the
     * current users data
     * eg: Kareem cant import James data.
     * eg: Kareem can import Kareem related data.
     * to do this we can get the newFile and current user to read the new folders
     * 'Task.txt' file.
     * if it spots the current users name and with new data it will call saveTask to
     * save the data onto the account.
     * if it detects tasks that are the same it will skip over this process.
     **/
    public static void setDataPath(File newFile, String username) throws IOException {
        if (checkDataPath(newFile)) {
            File[] listedFiles = newFile.listFiles();
            if (listedFiles == null)
                return;

            for (File sourceFile : listedFiles) {
                if (sourceFile.getName().equals("Task.txt")) {
                    int importedCount = 0;
                    int skippedCount = 0;

                    try (BufferedReader reader = new BufferedReader(new FileReader(sourceFile))) {

                        String line;
                        while ((line = reader.readLine()) != null) {
                            String[] parts = line.split("<SEP>");

                            if (parts.length >= 6 && parts[0].trim().equalsIgnoreCase(username)) {
                                String taskname = parts[1].trim();

                                if (taskExists(username, taskname)) {
                                    System.out.println("Skipping duplicate task: " + taskname);
                                    skippedCount++;
                                    continue;
                                }

                                String description = parts[2].replace("\\n", "\n").trim();
                                int rank = Integer.parseInt(parts[3].trim());
                                String group = parts[4].trim();

                                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy MM dd hh mm a");
                                LocalDateTime dueDate = LocalDateTime.parse(parts[5].trim(), formatter);

                                // Save this task to current data file
                                SaveTask(username, taskname, description, rank, group, dueDate);
                                importedCount++;
                            }
                        }
                        System.out.println("Import complete: " + importedCount + " tasks imported, " + skippedCount
                                + " duplicates skipped");

                    } catch (IOException e) {
                        System.err.println("Error importing tasks: " + e.getMessage());
                        throw e;
                    }
                }
            }
        } else {
            throw new IOException("Invalid Data folder structure");
        }
    }

    // Helper method to check if a task already exists
    private static boolean taskExists(String username, String taskname) {
        try (BufferedReader reader = new BufferedReader(new FileReader(TaskData))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("<SEP>");
                if (parts.length >= 2 &&
                        parts[0].trim().equalsIgnoreCase(username) &&
                        parts[1].trim().equals(taskname)) {
                    return true;
                }
            }
        } catch (IOException e) {
            System.out.println("Task could not be found in file!: '" + e.getMessage() + "'");
        }
        return false;
    }

    // Storing Dates/Time
    // SaveData("JohnDoe", "SecurePassword123");
    public static void saveUser(String newuser, String newpassword) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(LogData, true))) {
            writer.write(newuser + "<SEP>" + newpassword);
            writer.newLine();
            System.out.println("User credentials saved successfully.");
            createSet(newuser);
        } catch (IOException e) {
            System.out.println("An error occurred while saving user credentials: " + e.getMessage());
        }
    }

    // Finds the User within the file using BufferedReader and Arrays
    public static boolean findUser(String user, String password) {
        try (BufferedReader reader = new BufferedReader(new FileReader(LogData))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] credentials = line.split("<SEP>");
                if (credentials.length == 2 && credentials[0].equals(user) && credentials[1].equals(password)) {
                    return true;
                }
            }
            // Throws Exception otherwise.
        } catch (IOException e) {
            System.out.println("Error While checking" + e.getMessage());
        }
        return false;
    }

    // Add Task to data
    public static void SaveTask(String username, String taskname, String taskdescription, int taskrank,
            String taskgroup, LocalDateTime dueDate) {

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(TaskData, true))) {
            DateTimeFormatter dataformatted = DateTimeFormatter.ofPattern("yyyy MM dd hh mm a");
            String formattedDate = dueDate.format(dataformatted);

            String escapedDescription = taskdescription.replace("\n", "\\n");
            writer.write(username + "<SEP>" + taskname + "<SEP>" + escapedDescription + "<SEP>" + taskrank + "<SEP>"
                    + taskgroup + "<SEP>" + formattedDate);

            writer.newLine();
            writer.close();
            System.out.println("Task data saved successfully.");
        } catch (IOException e) {
            System.out.println("An error occurred while saving task data: " + e.getMessage());
        }
    }

    // Remove Task inside data
    // allow it to read the lines within the file
    // make it find the task lines your looking to delete
    public static void removeTask(String username, String taskname) {
        File inputFile = new File(TaskData);
        File tempFile = new File("temp.txt");

        try (BufferedReader reader = new BufferedReader(new FileReader(inputFile));
                BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile))) {

            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("<SEP>");
                if (parts.length >= 4 && parts[0].equals(username) && parts[1].equals(taskname)) {
                    continue; // skip the task to be removed
                }
                writer.write(line);
                writer.newLine();
            }
            System.out.println("Task removed successfully");
        } catch (IOException e) {
            System.out.println("Error removing task: " + e.getMessage());
        }

        if (!inputFile.delete() || !tempFile.renameTo(inputFile)) {
            System.out.println("Error finalizing removal.");
        }
    }

    // Update Task thats in data
    // Will be done by Getting User,taskname,desc and rank
    // Read lines, if line meets goal then update
    public static void updateTask(String username, String taskname, String newTaskname, String newDescription,
            int newRank, String TaskGroup, LocalDateTime dueDate) {
        File inputFile = new File(TaskData);
        File tempFile = new File("temp.txt");
        boolean updated = false;

        try (BufferedReader reader = new BufferedReader(new FileReader(inputFile));
                BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile))) {

            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("<SEP>");

                if (parts.length >= 4 && parts[0].equals(username) && parts[1].equals(taskname)) {
                    // Update task info
                    taskname = newTaskname;
                    DateTimeFormatter dataformatted = DateTimeFormatter.ofPattern("yyyy MM dd hh mm a");
                    String formattedDate = dueDate.format(dataformatted);
                    String escapedDescription = newDescription.replace("\n", "\\n");
                    writer.write(username + "<SEP>" + taskname + "<SEP>" + escapedDescription + "<SEP>" + newRank
                            + "<SEP>" + TaskGroup + "<SEP>" + formattedDate);
                    updated = true;
                } else {
                    writer.write(line);
                }
                writer.newLine();
            }

        } catch (IOException e) {
            System.out.println("Error updating task: " + e.getMessage());
            return;
        }

        // Ensures files and previous data are there
        if (!inputFile.delete() || !tempFile.renameTo(inputFile)) {
            System.out.println("Error finalizing task update.");
        } else if (updated) {
            System.out.println("Task updated successfully.");
        } else {
            System.out.println("Task not found.");
        }
    }

    // Find Tasks
    public static void FindData(String Username) {
        try (BufferedReader reader = new BufferedReader(new FileReader(TaskData))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("<SEP>");
                if (parts.length >= 6 && parts[0].trim().toLowerCase().equals(Username)) {
                    // String username, String taskname, String taskdescription, int taskrank,
                    // String taskgroup,DueDate
                    String description = parts[2].replace("\\n", "\n");
                    System.out.println("Task: " + parts[1] + ", Description: " + description + ", Rank: " + parts[3]
                            + ", Group: " + parts[4] + ", Due: " + parts[5]);
                }
            }
        } catch (IOException e) {
            System.out.println("Error While checking" + e.getMessage());
        }
    }

    // Returns data in a 2d Array(groupOfTasks(Tasks)==String[][])
    public static String[][] ReturnData(String Username) {
        List<String[]> taskList = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(TaskData))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("<SEP>");
                if (parts.length >= 6 && parts[0].trim().equalsIgnoreCase(Username)) {
                    // Add task info to the list (excluding username)
                    String[] task = new String[] {
                            parts[1].trim(), // taskname
                            parts[2].replace("\\n", "\n").trim(), // taskdescription (unescaped)
                            parts[3].trim(), // taskrank
                            parts[4].trim(), // taskgroup
                            parts[5].trim() // dueDate

                    };
                    taskList.add(task);
                }
            }
        } catch (IOException e) {
            System.out.println("Error while checking: " + e.getMessage());
        }

        return taskList.toArray(new String[0][0]);
    }

    // Username checker
    public static boolean usernameExists(String Username) {
        try (BufferedReader reader = new BufferedReader(new FileReader(LogData))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] credentials = line.split("<SEP>");
                if (credentials.length == 2 && credentials[0].equals(Username)) {
                    return true;
                }
            }
        } catch (IOException e) {
            System.out.println("Error While checking" + e.getMessage());
        }
        return false;

        // Checks Data using BufferedReader and LocalDataTime
    }

    // Checks date of task if its passed its given date.
    public static void DateChecker(String TaskName) {
        try (BufferedReader reader = new BufferedReader(new FileReader(TaskData))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("<SEP>");
                if (parts.length >= 6 && parts[1].equalsIgnoreCase(TaskName)) {
                    DateTimeFormatter dataformatted = DateTimeFormatter.ofPattern("yyyy MM dd hh mm a");
                    LocalDateTime dueDate = LocalDateTime.parse(parts[5], dataformatted);
                    String formattedDate = dueDate.format(dataformatted);
                    LocalDateTime now = LocalDateTime.now();

                    System.out.println(" This assignment is due at: " + formattedDate);

                    if (dueDate.isBefore(now)) {
                        System.out.println("This task is past due!");
                    } else if (dueDate.minusHours(24).isBefore(now)) {
                        System.out.println("Due in less than 24 hours!");
                    }
                    return;
                }
            }
            System.out.println("Task not found.");
        } catch (Exception e) {
            System.out.println("Error checking task: " + e.getMessage());
        }

    }

    // Find task time
    public static LocalDateTime DataCheckerUI(String TaskName) {
        LocalDateTime dueDate = null;
        try (BufferedReader reader = new BufferedReader(new FileReader(TaskData))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("<SEP>");
                if (parts.length >= 6 && parts[1].equalsIgnoreCase(TaskName)) {
                    DateTimeFormatter dataformatted = DateTimeFormatter.ofPattern("yyyy MM dd hh mm a");
                    dueDate = LocalDateTime.parse(parts[5], dataformatted);
                    String formattedDate = dueDate.format(dataformatted);
                    LocalDateTime now = LocalDateTime.now();
                    System.out.println(TaskName + " is due at: " + formattedDate);

                }
            }
            System.out.println("Task not found.");
        } catch (Exception e) {
            System.out.println("Error checking task: " + e.getMessage());
        }
        return dueDate;
    }
}
