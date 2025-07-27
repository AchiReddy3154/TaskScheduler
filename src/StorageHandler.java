import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StorageHandler {
    
    private static final String TASKS_FILE = "tasks.json";
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    
    /**
     * Saves a list of tasks to the tasks.json file
     * @param tasks List of tasks to save
     * @return true if save was successful, false otherwise
     */
    public boolean saveTasks(List<Task> tasks) {
        try {
            // Create backup of existing file if it exists
            createBackup();
            
            // Convert tasks to JSON manually
            String json = tasksToJson(tasks);
            
            // Write to file
            try (FileWriter writer = new FileWriter(TASKS_FILE)) {
                writer.write(json);
                writer.flush();
            }
            
            System.out.println("✓ Successfully saved " + tasks.size() + " tasks to " + TASKS_FILE);
            return true;
            
        } catch (IOException e) {
            System.err.println("✗ Error saving tasks to file: " + e.getMessage());
            restoreBackup(); // Try to restore backup if save failed
            return false;
        } catch (Exception e) {
            System.err.println("✗ Unexpected error during save: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Loads tasks from the tasks.json file
     * @return List of tasks loaded from file, empty list if file doesn't exist or error occurs
     */
    public List<Task> loadTasks() {
        File file = new File(TASKS_FILE);
        
        // Return empty list if file doesn't exist
        if (!file.exists()) {
            System.out.println("ℹ Tasks file not found. Starting with empty task list.");
            return new ArrayList<>();
        }
        
        try {
            // Read file content
            StringBuilder content = new StringBuilder();
            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    content.append(line).append("\n");
                }
            }
            
            // Check if file is empty
            if (content.length() == 0) {
                System.out.println("ℹ Tasks file is empty. Starting with empty task list.");
                return new ArrayList<>();
            }
            
            // Parse JSON manually
            List<Task> tasks = jsonToTasks(content.toString());
            
            System.out.println("✓ Successfully loaded " + tasks.size() + " tasks from " + TASKS_FILE);
            return tasks;
            
        } catch (Exception e) {
            System.err.println("✗ Error loading tasks from file: " + e.getMessage());
            return new ArrayList<>();
        }
    }
    
    /**
     * Converts a list of tasks to JSON format
     */
    private String tasksToJson(List<Task> tasks) {
        StringBuilder json = new StringBuilder();
        json.append("[\n");
        
        for (int i = 0; i < tasks.size(); i++) {
            Task task = tasks.get(i);
            json.append("  {\n");
            json.append("    \"title\": \"").append(escapeJson(task.getTitle())).append("\",\n");
            json.append("    \"priority\": ").append(task.getPriority()).append(",\n");
            json.append("    \"deadline\": \"").append(task.getDeadline().format(DATE_FORMATTER)).append("\",\n");
            json.append("    \"completed\": ").append(task.isCompleted()).append("\n");
            json.append("  }");
            
            if (i < tasks.size() - 1) {
                json.append(",");
            }
            json.append("\n");
        }
        
        json.append("]");
        return json.toString();
    }
    
    /**
     * Converts JSON string to list of tasks
     */
    private List<Task> jsonToTasks(String json) {
        List<Task> tasks = new ArrayList<>();
        
        // Remove whitespace and newlines for easier parsing
        json = json.replaceAll("\\s+", " ").trim();
        
        // Find all task objects in the JSON array
        Pattern taskPattern = Pattern.compile("\\{[^}]+\\}");
        Matcher taskMatcher = taskPattern.matcher(json);
        
        while (taskMatcher.find()) {
            String taskJson = taskMatcher.group();
            Task task = parseTaskFromJson(taskJson);
            if (task != null) {
                tasks.add(task);
            }
        }
        
        return tasks;
    }
    
    /**
     * Parses a single task from JSON object string
     */
    private Task parseTaskFromJson(String taskJson) {
        try {
            // Extract values using regex patterns
            String title = extractJsonValue(taskJson, "title");
            String priorityStr = extractJsonValue(taskJson, "priority");
            String deadlineStr = extractJsonValue(taskJson, "deadline");
            String completedStr = extractJsonValue(taskJson, "completed");
            
            if (title == null || priorityStr == null || deadlineStr == null) {
                System.err.println("⚠ Incomplete task data, skipping: " + taskJson);
                return null;
            }
            
            // Parse values
            int priority = Integer.parseInt(priorityStr);
            LocalDateTime deadline = LocalDateTime.parse(deadlineStr, DATE_FORMATTER);
            boolean completed = Boolean.parseBoolean(completedStr != null ? completedStr : "false");
            
            // Create task
            Task task = new Task(unescapeJson(title), priority, deadline);
            task.setCompleted(completed);
            
            return task;
            
        } catch (Exception e) {
            System.err.println("✗ Error parsing task: " + e.getMessage() + " - " + taskJson);
            return null;
        }
    }
    
    /**
     * Extracts a value from JSON object string
     */
    private String extractJsonValue(String json, String key) {
        Pattern pattern = Pattern.compile("\"" + key + "\"\\s*:\\s*\"?([^,}\"]+)\"?");
        Matcher matcher = pattern.matcher(json);
        
        if (matcher.find()) {
            return matcher.group(1).trim();
        }
        return null;
    }
    
    /**
     * Escapes special characters for JSON
     */
    private String escapeJson(String str) {
        if (str == null) return "";
        return str.replace("\\", "\\\\")
                  .replace("\"", "\\\"")
                  .replace("\n", "\\n")
                  .replace("\r", "\\r")
                  .replace("\t", "\\t");
    }
    
    /**
     * Unescapes JSON special characters
     */
    private String unescapeJson(String str) {
        if (str == null) return "";
        return str.replace("\\\\", "\\")
                  .replace("\\\"", "\"")
                  .replace("\\n", "\n")
                  .replace("\\r", "\r")
                  .replace("\\t", "\t");
    }
    
    /**
     * Saves tasks from a TaskManager to file
     * @param taskManager TaskManager containing tasks to save
     * @return true if save was successful, false otherwise
     */
    public boolean saveTaskManager(TaskManager taskManager) {
        if (taskManager == null) {
            System.err.println("✗ Cannot save null TaskManager");
            return false;
        }
        
        return saveTasks(taskManager.getAllTasks());
    }
    
    /**
     * Loads tasks into a TaskManager
     * @param taskManager TaskManager to load tasks into
     * @return number of tasks loaded
     */
    public int loadIntoTaskManager(TaskManager taskManager) {
        if (taskManager == null) {
            System.err.println("✗ Cannot load into null TaskManager");
            return 0;
        }
        
        List<Task> tasks = loadTasks();
        
        // Clear existing tasks and add loaded ones
        taskManager.clearAllTasks();
        for (Task task : tasks) {
            taskManager.addTask(task);
        }
        
        return tasks.size();
    }
    
    /**
     * Checks if the tasks file exists
     * @return true if tasks.json exists, false otherwise
     */
    public boolean fileExists() {
        return new File(TASKS_FILE).exists();
    }
    
    /**
     * Gets the size of the tasks file in bytes
     * @return file size in bytes, -1 if file doesn't exist
     */
    public long getFileSize() {
        File file = new File(TASKS_FILE);
        return file.exists() ? file.length() : -1;
    }
    
    /**
     * Deletes the tasks file
     * @return true if file was deleted or didn't exist, false if deletion failed
     */
    public boolean deleteTasksFile() {
        File file = new File(TASKS_FILE);
        if (!file.exists()) {
            System.out.println("ℹ Tasks file doesn't exist, nothing to delete.");
            return true;
        }
        
        boolean deleted = file.delete();
        if (deleted) {
            System.out.println("✓ Tasks file deleted successfully.");
        } else {
            System.err.println("✗ Failed to delete tasks file.");
        }
        return deleted;
    }
    
    /**
     * Creates a backup of the current tasks file
     */
    private void createBackup() {
        File originalFile = new File(TASKS_FILE);
        if (originalFile.exists()) {
            try {
                File backupFile = new File(TASKS_FILE + ".backup");
                
                // Copy content to backup
                try (BufferedReader reader = new BufferedReader(new FileReader(originalFile));
                     FileWriter writer = new FileWriter(backupFile)) {
                    
                    String line;
                    while ((line = reader.readLine()) != null) {
                        writer.write(line + "\n");
                    }
                }
                
            } catch (IOException e) {
                System.err.println("⚠ Warning: Could not create backup: " + e.getMessage());
            }
        }
    }
    
    /**
     * Restores the backup file if it exists
     */
    private void restoreBackup() {
        File backupFile = new File(TASKS_FILE + ".backup");
        if (backupFile.exists()) {
            try {
                File originalFile = new File(TASKS_FILE);
                
                // Copy backup content back to original
                try (BufferedReader reader = new BufferedReader(new FileReader(backupFile));
                     FileWriter writer = new FileWriter(originalFile)) {
                    
                    String line;
                    while ((line = reader.readLine()) != null) {
                        writer.write(line + "\n");
                    }
                }
                
                System.out.println("✓ Backup restored successfully.");
                
            } catch (IOException e) {
                System.err.println("✗ Failed to restore backup: " + e.getMessage());
            }
        }
    }
    
    /**
     * Gets information about the storage file
     * @return formatted string with file information
     */
    public String getStorageInfo() {
        File file = new File(TASKS_FILE);
        
        if (!file.exists()) {
            return "Storage Info: tasks.json file does not exist";
        }
        
        long size = file.length();
        String lastModified = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
            .format(new java.util.Date(file.lastModified()));
        
        return String.format("Storage Info: File size: %d bytes, Last modified: %s", size, lastModified);
    }
    
    /**
     * Validates the JSON structure of the tasks file
     * @return true if file has valid JSON structure, false otherwise
     */
    public boolean validateTasksFile() {
        if (!fileExists()) {
            System.out.println("ℹ Tasks file doesn't exist, nothing to validate.");
            return true;
        }
        
        try {
            List<Task> tasks = loadTasks();
            System.out.println("✓ Tasks file validation successful. Found " + tasks.size() + " valid tasks.");
            return true;
        } catch (Exception e) {
            System.err.println("✗ Tasks file validation failed: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Creates a sample tasks.json file for testing
     */
    public void createSampleFile() {
        List<Task> sampleTasks = new ArrayList<>();
        
        LocalDateTime now = LocalDateTime.now();
        sampleTasks.add(new Task("Sample Task 1", 8, now.plusHours(2)));
        sampleTasks.add(new Task("Sample Task 2", 5, now.plusDays(1)));
        sampleTasks.add(new Task("Sample Task 3", 9, now.plusMinutes(30)));
        
        // Mark one as completed
        sampleTasks.get(1).setCompleted(true);
        
        if (saveTasks(sampleTasks)) {
            System.out.println("✓ Sample tasks file created successfully.");
        }
    }
}