import java.time.LocalDateTime;
import java.time.LocalDate;
import java.util.List;
import java.util.ArrayList;

public class FilterUtils {
    
    // Priority thresholds
    public static final int HIGH_PRIORITY_THRESHOLD = 8;
    public static final int MEDIUM_PRIORITY_THRESHOLD = 5;
    public static final int LOW_PRIORITY_THRESHOLD = 1;
    
    /**
     * Filters tasks that are due today
     * @param tasks List of tasks to filter
     * @return List of tasks due today
     */
    public static List<Task> filterTodaysTasks(List<Task> tasks) {
        LocalDate today = LocalDate.now();
        List<Task> todaysTasks = new ArrayList<>();
        
        for (Task task : tasks) {
            if (task.getDeadline().toLocalDate().equals(today)) {
                todaysTasks.add(task);
            }
        }
        
        return todaysTasks;
    }
    
    /**
     * Filters tasks with high priority (>= 8)
     * @param tasks List of tasks to filter
     * @return List of high priority tasks
     */
    public static List<Task> filterHighPriorityTasks(List<Task> tasks) {
        return filterTasksByPriority(tasks, HIGH_PRIORITY_THRESHOLD, 10);
    }
    
    /**
     * Filters tasks with medium priority (5-7)
     * @param tasks List of tasks to filter
     * @return List of medium priority tasks
     */
    public static List<Task> filterMediumPriorityTasks(List<Task> tasks) {
        return filterTasksByPriority(tasks, MEDIUM_PRIORITY_THRESHOLD, HIGH_PRIORITY_THRESHOLD - 1);
    }
    
    /**
     * Filters tasks with low priority (1-4)
     * @param tasks List of tasks to filter
     * @return List of low priority tasks
     */
    public static List<Task> filterLowPriorityTasks(List<Task> tasks) {
        return filterTasksByPriority(tasks, LOW_PRIORITY_THRESHOLD, MEDIUM_PRIORITY_THRESHOLD - 1);
    }
    
    /**
     * Filters tasks by priority range
     * @param tasks List of tasks to filter
     * @param minPriority Minimum priority (inclusive)
     * @param maxPriority Maximum priority (inclusive)
     * @return List of tasks within priority range
     */
    public static List<Task> filterTasksByPriority(List<Task> tasks, int minPriority, int maxPriority) {
        List<Task> filteredTasks = new ArrayList<>();
        
        for (Task task : tasks) {
            int priority = task.getPriority();
            if (priority >= minPriority && priority <= maxPriority) {
                filteredTasks.add(task);
            }
        }
        
        return filteredTasks;
    }
    
    /**
     * Filters completed tasks
     * @param tasks List of tasks to filter
     * @return List of completed tasks
     */
    public static List<Task> filterCompletedTasks(List<Task> tasks) {
        List<Task> completedTasks = new ArrayList<>();
        
        for (Task task : tasks) {
            if (task.isCompleted()) {
                completedTasks.add(task);
            }
        }
        
        return completedTasks;
    }
    
    /**
     * Filters pending (incomplete) tasks
     * @param tasks List of tasks to filter
     * @return List of pending tasks
     */
    public static List<Task> filterPendingTasks(List<Task> tasks) {
        List<Task> pendingTasks = new ArrayList<>();
        
        for (Task task : tasks) {
            if (!task.isCompleted()) {
                pendingTasks.add(task);
            }
        }
        
        return pendingTasks;
    }
    
    /**
     * Filters overdue tasks (deadline has passed and not completed)
     * @param tasks List of tasks to filter
     * @return List of overdue tasks
     */
    public static List<Task> filterOverdueTasks(List<Task> tasks) {
        LocalDateTime now = LocalDateTime.now();
        List<Task> overdueTasks = new ArrayList<>();
        
        for (Task task : tasks) {
            if (!task.isCompleted() && task.getDeadline().isBefore(now)) {
                overdueTasks.add(task);
            }
        }
        
        return overdueTasks;
    }
    
    /**
     * Filters tasks due within the next specified hours
     * @param tasks List of tasks to filter
     * @param hours Number of hours from now
     * @return List of tasks due within the specified time
     */
    public static List<Task> filterTasksDueWithinHours(List<Task> tasks, int hours) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime threshold = now.plusHours(hours);
        List<Task> upcomingTasks = new ArrayList<>();
        
        for (Task task : tasks) {
            if (!task.isCompleted() && 
                task.getDeadline().isAfter(now) && 
                task.getDeadline().isBefore(threshold)) {
                upcomingTasks.add(task);
            }
        }
        
        return upcomingTasks;
    }
    
    /**
     * Filters tasks due this week
     * @param tasks List of tasks to filter
     * @return List of tasks due this week
     */
    public static List<Task> filterThisWeeksTasks(List<Task> tasks) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime endOfWeek = now.plusDays(7);
        List<Task> thisWeeksTasks = new ArrayList<>();
        
        for (Task task : tasks) {
            if (task.getDeadline().isAfter(now.minusDays(1)) && 
                task.getDeadline().isBefore(endOfWeek)) {
                thisWeeksTasks.add(task);
            }
        }
        
        return thisWeeksTasks;
    }
    
    /**
     * Filters tasks by title containing search text (case-insensitive)
     * @param tasks List of tasks to filter
     * @param searchText Text to search for in task titles
     * @return List of tasks with matching titles
     */
    public static List<Task> filterTasksByTitle(List<Task> tasks, String searchText) {
        if (searchText == null || searchText.trim().isEmpty()) {
            return new ArrayList<>(tasks);
        }
        
        String searchLower = searchText.toLowerCase().trim();
        List<Task> matchingTasks = new ArrayList<>();
        
        for (Task task : tasks) {
            if (task.getTitle().toLowerCase().contains(searchLower)) {
                matchingTasks.add(task);
            }
        }
        
        return matchingTasks;
    }
    
    /**
     * Combines multiple filters - returns tasks that match ALL criteria
     * @param tasks List of tasks to filter
     * @param filters Array of filter conditions
     * @return List of tasks matching all filters
     */
    public static List<Task> applyMultipleFilters(List<Task> tasks, TaskFilter... filters) {
        List<Task> result = new ArrayList<>(tasks);
        
        for (TaskFilter filter : filters) {
            result = filter.apply(result);
        }
        
        return result;
    }
    
    /**
     * Gets a summary of filter results
     * @param originalTasks Original list of tasks
     * @param filteredTasks Filtered list of tasks
     * @param filterName Name of the filter applied
     * @return Summary string
     */
    public static String getFilterSummary(List<Task> originalTasks, List<Task> filteredTasks, String filterName) {
        int original = originalTasks.size();
        int filtered = filteredTasks.size();
        double percentage = original > 0 ? (filtered * 100.0 / original) : 0;
        
        return String.format("Filter '%s': %d/%d tasks (%.1f%%)", 
            filterName, filtered, original, percentage);
    }
    
    /**
     * Interface for custom filter implementations
     */
    public interface TaskFilter {
        List<Task> apply(List<Task> tasks);
    }
    
    /**
     * Predefined filter implementations
     */
    public static class Filters {
        public static final TaskFilter TODAY = FilterUtils::filterTodaysTasks;
        public static final TaskFilter HIGH_PRIORITY = FilterUtils::filterHighPriorityTasks;
        public static final TaskFilter MEDIUM_PRIORITY = FilterUtils::filterMediumPriorityTasks;
        public static final TaskFilter LOW_PRIORITY = FilterUtils::filterLowPriorityTasks;
        public static final TaskFilter COMPLETED = FilterUtils::filterCompletedTasks;
        public static final TaskFilter PENDING = FilterUtils::filterPendingTasks;
        public static final TaskFilter OVERDUE = FilterUtils::filterOverdueTasks;
        public static final TaskFilter THIS_WEEK = FilterUtils::filterThisWeeksTasks;
        
        public static TaskFilter dueWithinHours(int hours) {
            return tasks -> filterTasksDueWithinHours(tasks, hours);
        }
        
        public static TaskFilter titleContains(String searchText) {
            return tasks -> filterTasksByTitle(tasks, searchText);
        }
        
        public static TaskFilter priorityRange(int min, int max) {
            return tasks -> filterTasksByPriority(tasks, min, max);
        }
    }
    
    /**
     * Gets all available filter options with descriptions
     * @return Array of filter option descriptions
     */
    public static String[] getFilterOptions() {
        return new String[] {
            "All Tasks",
            "Today's Tasks",
            "High Priority (8-10)",
            "Medium Priority (5-7)", 
            "Low Priority (1-4)",
            "Completed Tasks",
            "Pending Tasks",
            "Overdue Tasks",
            "This Week's Tasks",
            "Due Within 1 Hour",
            "Due Within 24 Hours"
        };
    }
    
    /**
     * Applies the selected filter based on filter index
     * @param tasks List of tasks to filter
     * @param filterIndex Index from getFilterOptions()
     * @return Filtered list of tasks
     */
    public static List<Task> applySelectedFilter(List<Task> tasks, int filterIndex) {
        switch (filterIndex) {
            case 0: return new ArrayList<>(tasks); // All Tasks
            case 1: return filterTodaysTasks(tasks);
            case 2: return filterHighPriorityTasks(tasks);
            case 3: return filterMediumPriorityTasks(tasks);
            case 4: return filterLowPriorityTasks(tasks);
            case 5: return filterCompletedTasks(tasks);
            case 6: return filterPendingTasks(tasks);
            case 7: return filterOverdueTasks(tasks);
            case 8: return filterThisWeeksTasks(tasks);
            case 9: return filterTasksDueWithinHours(tasks, 1);
            case 10: return filterTasksDueWithinHours(tasks, 24);
            default: return new ArrayList<>(tasks);
        }
    }
}