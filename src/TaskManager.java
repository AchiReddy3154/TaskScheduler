import java.util.PriorityQueue;
import java.util.Comparator;
import java.util.List;
import java.util.ArrayList;

public class TaskManager {
    private PriorityQueue<Task> taskQueue;
    
    // Constructor
    public TaskManager() {
        // Create PriorityQueue with custom comparator
        // First compare by priority (highest first), then by deadline (earliest first)
        this.taskQueue = new PriorityQueue<>(new Comparator<Task>() {
            @Override
            public int compare(Task t1, Task t2) {
                // Compare by priority first (higher priority comes first)
                int priorityComparison = Integer.compare(t2.getPriority(), t1.getPriority());
                if (priorityComparison != 0) {
                    return priorityComparison;
                }
                // If priorities are equal, compare by deadline (earlier deadline comes first)
                return t1.getDeadline().compareTo(t2.getDeadline());
            }
        });
    }
    
    // Add a task to the queue
    public void addTask(Task task) {
        taskQueue.offer(task);
    }
    
    // Remove and return the highest priority task
    public Task getNextTask() {
        return taskQueue.poll();
    }
    
    // Peek at the highest priority task without removing it
    public Task peekNextTask() {
        return taskQueue.peek();
    }
    
    // Remove a specific task from the queue
    public boolean removeTask(Task task) {
        return taskQueue.remove(task);
    }
    
    // Get the number of tasks in the queue
    public int getTaskCount() {
        return taskQueue.size();
    }
    
    // Check if the queue is empty
    public boolean isEmpty() {
        return taskQueue.isEmpty();
    }
    
    // Get all tasks as a list (for display purposes)
    public List<Task> getAllTasks() {
        return new ArrayList<>(taskQueue);
    }
    
    // Clear all tasks
    public void clearAllTasks() {
        taskQueue.clear();
    }
    
    // Get all completed tasks
    public List<Task> getCompletedTasks() {
        List<Task> completedTasks = new ArrayList<>();
        for (Task task : taskQueue) {
            if (task.isCompleted()) {
                completedTasks.add(task);
            }
        }
        return completedTasks;
    }
    
    // Get all pending (incomplete) tasks
    public List<Task> getPendingTasks() {
        List<Task> pendingTasks = new ArrayList<>();
        for (Task task : taskQueue) {
            if (!task.isCompleted()) {
                pendingTasks.add(task);
            }
        }
        return pendingTasks;
    }
}