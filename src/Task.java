import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Task {
    private String title;
    private int priority;
    private LocalDateTime deadline;
    private boolean completed;
    
    // Constructor
    public Task(String title, int priority, LocalDateTime deadline) {
        this.title = title;
        this.priority = priority;
        this.deadline = deadline;
        this.completed = false; // Default to not completed
    }
    
    // Getter methods
    public String getTitle() {
        return title;
    }
    
    public int getPriority() {
        return priority;
    }
    
    public LocalDateTime getDeadline() {
        return deadline;
    }
    
    public boolean isCompleted() {
        return completed;
    }
    
    // Setter methods
    public void setTitle(String title) {
        this.title = title;
    }
    
    public void setPriority(int priority) {
        this.priority = priority;
    }
    
    public void setDeadline(LocalDateTime deadline) {
        this.deadline = deadline;
    }
    
    public void setCompleted(boolean completed) {
        this.completed = completed;
    }
    
    // toString() override
    @Override
    public String toString() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        return String.format("Task{title='%s', priority=%d, deadline=%s, completed=%s}", 
                           title, priority, deadline.format(formatter), completed);
    }
}