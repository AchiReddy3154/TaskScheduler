import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Modern Swing UI for Smart Task Scheduler with clean design
 * Features: Modal dialogs, clean layout, modern styling
 */
public class ModernSwingUI extends JFrame {
    
    private TaskManager taskManager;
    private StorageHandler storageHandler;
    private TaskSchedulerTray taskTray;
    
    // UI Components
    private JTable taskTable;
    private DefaultTableModel tableModel;
    private JLabel statusLabel;
    private JLabel taskCountLabel;
    private JComboBox<String> filterComboBox;
    private JButton refreshButton;
    
    // Colors and styling
    private static final Color PRIMARY_COLOR = new Color(70, 130, 180);
    private static final Color SUCCESS_COLOR = new Color(46, 125, 50);
    private static final Color WARNING_COLOR = new Color(255, 152, 0);
    private static final Color ERROR_COLOR = new Color(244, 67, 54);
    
    // Table column names
    private final String[] columnNames = {"Priority", "Title", "Deadline", "Status"};
    
    public ModernSwingUI() {
        // Set modern look and feel
        try {
            // Try to set a modern look and feel
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception e) {
            // Use default look and feel
        }
        
        taskManager = new TaskManager();
        storageHandler = new StorageHandler();
        
        initializeUI();
        loadTasksOnStartup();
        refreshTaskTable();
        
        // Initialize system tray after UI is ready
        taskTray = new TaskSchedulerTray(taskManager, this);
    }
    
    private void initializeUI() {
        setTitle("Smart Task Scheduler");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));
        
        // Create main sections
        add(createHeaderPanel(), BorderLayout.NORTH);
        add(createMainContentPanel(), BorderLayout.CENTER);
        add(createStatusPanel(), BorderLayout.SOUTH);
        
        // Set window properties
        setSize(900, 700);
        setLocationRelativeTo(null);
        setMinimumSize(new Dimension(800, 600));
        
        // Add window closing behavior - minimize to tray if supported
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                saveTasks();
                if (taskTray != null && taskTray.isTraySupported()) {
                    // Minimize to tray
                    setVisible(false);
                    taskTray.showMessage("Task Scheduler", 
                        "Application minimized to system tray", 
                        java.awt.TrayIcon.MessageType.INFO);
                } else {
                    // Exit if tray not supported
                    if (taskTray != null) taskTray.cleanup();
                    System.exit(0);
                }
            }
        });
        
        // Apply modern styling
        getRootPane().setBorder(new EmptyBorder(10, 10, 10, 10));
    }
    
    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel(new BorderLayout(15, 0));
        headerPanel.setBorder(new EmptyBorder(0, 0, 20, 0));
        
        // Title section
        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        JLabel titleLabel = new JLabel("Smart Task Scheduler");
        titleLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 24));
        titleLabel.setForeground(PRIMARY_COLOR);
        titlePanel.add(titleLabel);
        
        taskCountLabel = new JLabel("0 tasks");
        taskCountLabel.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 14));
        taskCountLabel.setForeground(Color.GRAY);
        taskCountLabel.setBorder(new EmptyBorder(5, 15, 0, 0));
        titlePanel.add(taskCountLabel);
        
        // Action buttons panel
        JPanel actionPanel = createActionButtonsPanel();
        
        headerPanel.add(titlePanel, BorderLayout.WEST);
        headerPanel.add(actionPanel, BorderLayout.EAST);
        
        return headerPanel;
    }
    
    private JPanel createActionButtonsPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
        
        // Task management buttons
        JButton addButton = createStyledButton("+ Add Task", PRIMARY_COLOR);
        JButton editButton = createStyledButton("Edit", null);
        JButton deleteButton = createStyledButton("Delete", ERROR_COLOR);
        JButton completeButton = createStyledButton("Complete", SUCCESS_COLOR);
        
        // File operations
        JButton saveButton = createStyledButton("Save", null);
        JButton loadButton = createStyledButton("Load", null);
        
        // Add action listeners
        addButton.addActionListener(e -> showAddTaskDialog());
        editButton.addActionListener(e -> editTask());
        deleteButton.addActionListener(e -> deleteTask());
        completeButton.addActionListener(e -> markTaskComplete());
        saveButton.addActionListener(e -> saveTasks());
        loadButton.addActionListener(e -> loadTasks());
        
        // Group buttons with spacing
        panel.add(addButton);
        panel.add(Box.createHorizontalStrut(10));
        panel.add(editButton);
        panel.add(deleteButton);
        panel.add(completeButton);
        panel.add(Box.createHorizontalStrut(15));
        panel.add(saveButton);
        panel.add(loadButton);
        
        return panel;
    }
    
    private JButton createStyledButton(String text, Color backgroundColor) {
        JButton button = new JButton(text);
        button.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 12));
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
        
        if (backgroundColor != null) {
            button.setBackground(backgroundColor);
            button.setForeground(Color.WHITE);
            button.setOpaque(true);
        }
        
        return button;
    }
    
    private JPanel createMainContentPanel() {
        JPanel mainPanel = new JPanel(new BorderLayout(0, 15));
        
        // Filter section
        mainPanel.add(createFilterPanel(), BorderLayout.NORTH);
        
        // Task table
        mainPanel.add(createTaskTablePanel(), BorderLayout.CENTER);
        
        return mainPanel;
    }
    
    private JPanel createFilterPanel() {
        JPanel filterPanel = new JPanel(new GridBagLayout());
        filterPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(Color.LIGHT_GRAY), "Filters"));
        filterPanel.setBackground(new Color(248, 249, 250));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 10, 5, 10);
        
        // Filter dropdown
        gbc.gridx = 0; gbc.gridy = 0;
        filterPanel.add(new JLabel("Show:"), gbc);
        
        String[] filterOptions = {
            "All Tasks",
            "Today's Tasks", 
            "High Priority (8+)",
            "Medium Priority (4-7)",
            "Low Priority (1-3)",
            "Completed Tasks",
            "Pending Tasks",
            "Overdue Tasks",
            "This Week's Tasks"
        };
        
        filterComboBox = new JComboBox<>(filterOptions);
        filterComboBox.setPreferredSize(new Dimension(180, 30));
        gbc.gridx = 1;
        filterPanel.add(filterComboBox, gbc);
        
        // Apply filter button
        JButton filterButton = createStyledButton("Apply Filter", PRIMARY_COLOR);
        filterButton.addActionListener(e -> applyFilter());
        gbc.gridx = 2;
        filterPanel.add(filterButton, gbc);
        
        // Refresh button
        refreshButton = createStyledButton("↻ Refresh", null);
        refreshButton.addActionListener(e -> refreshTaskTable());
        gbc.gridx = 3;
        filterPanel.add(refreshButton, gbc);
        
        return filterPanel;
    }
    
    private JPanel createTaskTablePanel() {
        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(Color.LIGHT_GRAY), "Tasks"));
        
        // Create table model
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
            
            @Override
            public Class<?> getColumnClass(int column) {
                if (column == 0) return Integer.class; // Priority column for sorting
                return String.class;
            }
        };
        
        taskTable = new JTable(tableModel);
        taskTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        taskTable.setRowHeight(30);
        taskTable.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 13));
        taskTable.getTableHeader().setFont(new Font(Font.SANS_SERIF, Font.BOLD, 13));
        taskTable.setGridColor(new Color(230, 230, 230));
        
        // Custom cell renderer for status colors
        taskTable.getColumnModel().getColumn(3).setCellRenderer(new StatusCellRenderer());
        
        // Set column widths
        taskTable.getColumnModel().getColumn(0).setPreferredWidth(80);  // Priority
        taskTable.getColumnModel().getColumn(1).setPreferredWidth(300); // Title
        taskTable.getColumnModel().getColumn(2).setPreferredWidth(150); // Deadline
        taskTable.getColumnModel().getColumn(3).setPreferredWidth(100); // Status
        
        // Double-click to edit
        taskTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    editTask();
                }
            }
        });
        
        JScrollPane scrollPane = new JScrollPane(taskTable);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        tablePanel.add(scrollPane, BorderLayout.CENTER);
        
        return tablePanel;
    }
    
    private JPanel createStatusPanel() {
        JPanel statusPanel = new JPanel(new BorderLayout());
        statusPanel.setBorder(new EmptyBorder(10, 0, 0, 0));
        
        statusLabel = new JLabel("Ready");
        statusLabel.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 12));
        statusLabel.setForeground(Color.GRAY);
        
        // Add tray status on the right
        JLabel trayStatusLabel = new JLabel();
        if (SystemTray.isSupported()) {
            trayStatusLabel.setText("System Tray Active - Close to minimize");
            trayStatusLabel.setForeground(SUCCESS_COLOR);
        } else {
            trayStatusLabel.setText("System Tray Not Available");
            trayStatusLabel.setForeground(Color.GRAY);
        }
        trayStatusLabel.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 11));
        
        statusPanel.add(statusLabel, BorderLayout.WEST);
        statusPanel.add(trayStatusLabel, BorderLayout.EAST);
        
        return statusPanel;
    }
    
    private void showAddTaskDialog() {
        TaskDialog dialog = new TaskDialog(this, "Add New Task", null);
        dialog.setVisible(true);
        
        if (dialog.isConfirmed()) {
            Task newTask = dialog.getTask();
            taskManager.addTask(newTask);
            refreshTaskTable();
            setStatus("Task added: " + newTask.getTitle());
            
            // Update tray with new task manager state
            if (taskTray != null) {
                taskTray.updateTaskManager(taskManager);
            }
        }
    }
    
    private void editTask() {
        int selectedRow = taskTable.getSelectedRow();
        if (selectedRow == -1) {
            showError("Please select a task to edit.");
            return;
        }
        
        List<Task> allTasks = taskManager.getAllTasks();
        if (selectedRow >= allTasks.size()) {
            showError("Invalid task selection.");
            return;
        }
        
        Task task = allTasks.get(selectedRow);
        TaskDialog dialog = new TaskDialog(this, "Edit Task", task);
        dialog.setVisible(true);
        
        if (dialog.isConfirmed()) {
            Task updatedTask = dialog.getTask();
            task.setTitle(updatedTask.getTitle());
            task.setPriority(updatedTask.getPriority());
            task.setDeadline(updatedTask.getDeadline());
            
            refreshTaskTable();
            setStatus("Task updated: " + task.getTitle());
            
            // Update tray with new task manager state
            if (taskTray != null) {
                taskTray.updateTaskManager(taskManager);
            }
        }
    }
    
    private void deleteTask() {
        int selectedRow = taskTable.getSelectedRow();
        if (selectedRow == -1) {
            showError("Please select a task to delete.");
            return;
        }
        
        List<Task> allTasks = taskManager.getAllTasks();
        if (selectedRow >= allTasks.size()) {
            showError("Invalid task selection.");
            return;
        }
        
        Task task = allTasks.get(selectedRow);
        int result = JOptionPane.showConfirmDialog(
            this,
            "Are you sure you want to delete:\n" + task.getTitle() + "?",
            "Confirm Delete",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE
        );
        
        if (result == JOptionPane.YES_OPTION) {
            taskManager.removeTask(task);
            refreshTaskTable();
            setStatus("Task deleted: " + task.getTitle());
            
            // Update tray with new task manager state
            if (taskTray != null) {
                taskTray.updateTaskManager(taskManager);
            }
        }
    }
    
    private void markTaskComplete() {
        int selectedRow = taskTable.getSelectedRow();
        if (selectedRow == -1) {
            showError("Please select a task to mark complete.");
            return;
        }
        
        List<Task> allTasks = taskManager.getAllTasks();
        if (selectedRow >= allTasks.size()) {
            showError("Invalid task selection.");
            return;
        }
        
        Task task = allTasks.get(selectedRow);
        task.setCompleted(true);
        refreshTaskTable();
        setStatus("Task completed: " + task.getTitle());
        
        // Update tray with new task manager state
        if (taskTray != null) {
            taskTray.updateTaskManager(taskManager);
        }
    }
    
    private void applyFilter() {
        String selectedFilter = (String) filterComboBox.getSelectedItem();
        List<Task> allTasks = taskManager.getAllTasks();
        List<Task> filteredTasks;
        
        switch (selectedFilter) {
            case "All Tasks":
                filteredTasks = allTasks;
                break;
            case "Today's Tasks":
                filteredTasks = FilterUtils.filterTodaysTasks(allTasks);
                break;
            case "High Priority (8+)":
                filteredTasks = FilterUtils.filterHighPriorityTasks(allTasks);
                break;
            case "Medium Priority (4-7)":
                filteredTasks = FilterUtils.filterMediumPriorityTasks(allTasks);
                break;
            case "Low Priority (1-3)":
                filteredTasks = FilterUtils.filterLowPriorityTasks(allTasks);
                break;
            case "Completed Tasks":
                filteredTasks = FilterUtils.filterCompletedTasks(allTasks);
                break;
            case "Pending Tasks":
                filteredTasks = FilterUtils.filterPendingTasks(allTasks);
                break;
            case "Overdue Tasks":
                filteredTasks = FilterUtils.filterOverdueTasks(allTasks);
                break;
            case "This Week's Tasks":
                filteredTasks = FilterUtils.filterThisWeeksTasks(allTasks);
                break;
            default:
                filteredTasks = allTasks;
        }
        
        updateTableWithTasks(filteredTasks);
        setStatus("Filter applied: " + selectedFilter + " (" + filteredTasks.size() + " tasks shown)");
    }
    
    private void saveTasks() {
        try {
            List<Task> allTasks = taskManager.getAllTasks();
            storageHandler.saveTasks(allTasks);
            setStatus("Tasks saved successfully (" + allTasks.size() + " tasks)");
        } catch (Exception e) {
            showError("Error saving tasks: " + e.getMessage());
        }
    }
    
    private void loadTasks() {
        try {
            List<Task> loadedTasks = storageHandler.loadTasks();
            
            taskManager = new TaskManager();
            for (Task task : loadedTasks) {
                taskManager.addTask(task);
            }
            
            refreshTaskTable();
            setStatus("Tasks loaded successfully (" + loadedTasks.size() + " tasks)");
        } catch (Exception e) {
            showError("Error loading tasks: " + e.getMessage());
        }
    }
    
    private void loadTasksOnStartup() {
        try {
            List<Task> existingTasks = storageHandler.loadTasks();
            for (Task task : existingTasks) {
                taskManager.addTask(task);
            }
            if (!existingTasks.isEmpty()) {
                setStatus("Loaded " + existingTasks.size() + " existing tasks");
            }
        } catch (Exception e) {
            setStatus("No existing tasks found - starting fresh");
        }
    }
    
    private void refreshTaskTable() {
        List<Task> allTasks = taskManager.getAllTasks();
        updateTableWithTasks(allTasks);
        updateTaskCount(allTasks.size());
    }
    
    private void updateTableWithTasks(List<Task> tasks) {
        tableModel.setRowCount(0);
        
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM dd, yyyy HH:mm");
        
        for (Task task : tasks) {
            Object[] rowData = {
                task.getPriority(),
                task.getTitle(),
                task.getDeadline().format(formatter),
                task.isCompleted() ? "Completed" : "Pending"
            };
            tableModel.addRow(rowData);
        }
    }
    
    private void updateTaskCount(int count) {
        taskCountLabel.setText(count + " task" + (count != 1 ? "s" : ""));
    }
    
    private void setStatus(String message) {
        statusLabel.setText(message);
        // Auto-clear status after 5 seconds
        Timer timer = new Timer(5000, e -> statusLabel.setText("Ready"));
        timer.setRepeats(false);
        timer.start();
    }
    
    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
    }
    
    // Custom cell renderer for status column
    private static class StatusCellRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
            
            Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            
            if (!isSelected) {
                String status = value.toString();
                if ("Completed".equals(status)) {
                    c.setForeground(new Color(46, 125, 50)); // Green
                } else {
                    c.setForeground(new Color(255, 152, 0)); // Orange
                }
            }
            
            return c;
        }
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new ModernSwingUI().setVisible(true);
        });
    }
}
