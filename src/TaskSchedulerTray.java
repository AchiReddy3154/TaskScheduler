import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import javax.swing.*;

/**
 * System Tray integration for Smart Task Scheduler
 * Shows deadline alerts and provides quick access to the application
 */
public class TaskSchedulerTray {
    
    private SystemTray systemTray;
    private TrayIcon trayIcon;
    private TaskManager taskManager;
    private JFrame mainWindow;
    private Timer reminderTimer;
    private boolean traySupported;
    
    // Icons (using simple shapes for compatibility)
    private static final String TRAY_TOOLTIP = "Smart Task Scheduler";
    
    public TaskSchedulerTray(TaskManager taskManager, JFrame mainWindow) {
        this.taskManager = taskManager;
        this.mainWindow = mainWindow;
        this.traySupported = SystemTray.isSupported();
        
        if (traySupported) {
            initializeSystemTray();
            startReminderService();
        } else {
            System.out.println("System tray not supported on this platform");
        }
    }
    
    private void initializeSystemTray() {
        try {
            systemTray = SystemTray.getSystemTray();
            
            // Create tray icon image
            Image trayImage = createTrayIconImage();
            
            // Create popup menu
            PopupMenu trayMenu = createTrayMenu();
            
            // Create tray icon
            trayIcon = new TrayIcon(trayImage, TRAY_TOOLTIP, trayMenu);
            trayIcon.setImageAutoSize(true);
            
            // Add click listener to show/hide main window
            trayIcon.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    if (e.getClickCount() == 2) {
                        showMainWindow();
                    }
                }
            });
            
            // Add to system tray
            systemTray.add(trayIcon);
            
            System.out.println("System tray initialized successfully");
            
        } catch (AWTException e) {
            System.err.println("Failed to initialize system tray: " + e.getMessage());
            traySupported = false;
        }
    }
    
    private Image createTrayIconImage() {
        // Create a simple colored square icon
        int size = 16;
        BufferedImage image = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = image.createGraphics();
        
        // Enable antialiasing
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        // Draw background circle
        g2d.setColor(new Color(70, 130, 180)); // Steel blue
        g2d.fillOval(1, 1, size-2, size-2);
        
        // Draw clock icon
        g2d.setColor(Color.WHITE);
        g2d.setStroke(new BasicStroke(1.5f));
        
        // Clock hands
        int centerX = size / 2;
        int centerY = size / 2;
        
        // Hour hand (pointing to 3)
        g2d.drawLine(centerX, centerY, centerX + 3, centerY);
        
        // Minute hand (pointing to 12)
        g2d.drawLine(centerX, centerY, centerX, centerY - 4);
        
        g2d.dispose();
        return image;
    }
    
    private PopupMenu createTrayMenu() {
        PopupMenu menu = new PopupMenu();
        
        // Show/Hide window
        MenuItem showItem = new MenuItem("Show Task Scheduler");
        showItem.addActionListener(e -> showMainWindow());
        menu.add(showItem);
        
        MenuItem hideItem = new MenuItem("Hide to Tray");
        hideItem.addActionListener(e -> hideMainWindow());
        menu.add(hideItem);
        
        menu.addSeparator();
        
        // Quick task info
        MenuItem taskCountItem = new MenuItem("Check Tasks");
        taskCountItem.addActionListener(e -> showTaskSummary());
        menu.add(taskCountItem);
        
        menu.addSeparator();
        
        // Settings
        MenuItem settingsItem = new MenuItem("Reminder Settings");
        settingsItem.addActionListener(e -> showReminderSettings());
        menu.add(settingsItem);
        
        menu.addSeparator();
        
        // Exit
        MenuItem exitItem = new MenuItem("Exit");
        exitItem.addActionListener(e -> {
            stopReminderService();
            if (traySupported && trayIcon != null) {
                systemTray.remove(trayIcon);
            }
            System.exit(0);
        });
        menu.add(exitItem);
        
        return menu;
    }
    
    private void startReminderService() {
        if (reminderTimer != null) {
            reminderTimer.cancel();
        }
        
        reminderTimer = new Timer(true); // Daemon timer
        
        // Check for due tasks every 2 minutes
        reminderTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                checkForDueTasks();
            }
        }, 10000, 120000); // Start after 10 seconds, then every 2 minutes
        
        System.out.println("Reminder service started - checking every 2 minutes");
    }
    
    private void checkForDueTasks() {
        if (!traySupported || taskManager == null) return;
        
        try {
            List<Task> allTasks = taskManager.getAllTasks();
            LocalDateTime now = LocalDateTime.now();
            
            for (Task task : allTasks) {
                if (!task.isCompleted()) {
                    long minutesUntilDue = ChronoUnit.MINUTES.between(now, task.getDeadline());
                    
                    // Show alert for tasks due within 15 minutes or overdue
                    if (minutesUntilDue <= 15 && minutesUntilDue >= -60) {
                        showDeadlineAlert(task, minutesUntilDue);
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Error checking for due tasks: " + e.getMessage());
        }
    }
    
    private void showDeadlineAlert(Task task, long minutesUntilDue) {
        if (!traySupported) return;
        
        String title;
        String message;
        TrayIcon.MessageType messageType;
        
        if (minutesUntilDue <= 0) {
            // Overdue task
            title = "OVERDUE TASK";
            message = String.format("'%s' was due %d minutes ago!\nPriority: %d", 
                task.getTitle(), Math.abs(minutesUntilDue), task.getPriority());
            messageType = TrayIcon.MessageType.ERROR;
        } else if (minutesUntilDue <= 5) {
            // Due very soon
            title = "URGENT TASK";
            message = String.format("'%s' is due in %d minutes!\nPriority: %d", 
                task.getTitle(), minutesUntilDue, task.getPriority());
            messageType = TrayIcon.MessageType.WARNING;
        } else {
            // Due soon
            title = "TASK REMINDER";
            message = String.format("'%s' is due in %d minutes\nPriority: %d", 
                task.getTitle(), minutesUntilDue, task.getPriority());
            messageType = TrayIcon.MessageType.INFO;
        }
        
        // Show the alert
        trayIcon.displayMessage(title, message, messageType);
        
        // Also log to console
        System.out.printf("[ALERT] %s - %s\n", title, task.getTitle());
        
        // Change tray icon color temporarily to indicate alert
        flashTrayIcon();
    }
    
    private void flashTrayIcon() {
        // Create urgent icon (red)
        Image urgentImage = createUrgentTrayIcon();
        trayIcon.setImage(urgentImage);
        
        // Revert to normal icon after 3 seconds
        Timer flashTimer = new Timer();
        flashTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                trayIcon.setImage(createTrayIconImage());
            }
        }, 3000);
    }
    
    private Image createUrgentTrayIcon() {
        int size = 16;
        BufferedImage image = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = image.createGraphics();
        
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        // Red background for urgency
        g2d.setColor(new Color(220, 53, 69)); // Bootstrap danger red
        g2d.fillOval(1, 1, size-2, size-2);
        
        // Exclamation mark
        g2d.setColor(Color.WHITE);
        g2d.setStroke(new BasicStroke(2f));
        
        int centerX = size / 2;
        // Exclamation line
        g2d.drawLine(centerX, 3, centerX, 10);
        // Exclamation dot
        g2d.fillOval(centerX-1, 12, 2, 2);
        
        g2d.dispose();
        return image;
    }
    
    private void showMainWindow() {
        if (mainWindow != null) {
            mainWindow.setVisible(true);
            mainWindow.setExtendedState(JFrame.NORMAL);
            mainWindow.toFront();
            mainWindow.requestFocus();
        }
    }
    
    private void hideMainWindow() {
        if (mainWindow != null) {
            mainWindow.setVisible(false);
        }
        
        if (traySupported) {
            trayIcon.displayMessage("Task Scheduler", 
                "Application minimized to system tray", 
                TrayIcon.MessageType.INFO);
        }
    }
    
    private void showTaskSummary() {
        if (taskManager == null) return;
        
        try {
            List<Task> allTasks = taskManager.getAllTasks();
            long pendingCount = allTasks.stream().mapToLong(t -> t.isCompleted() ? 0 : 1).sum();
            long overdueCount = 0;
            long dueTodayCount = 0;
            
            LocalDateTime now = LocalDateTime.now();
            for (Task task : allTasks) {
                if (!task.isCompleted()) {
                    long minutesUntilDue = ChronoUnit.MINUTES.between(now, task.getDeadline());
                    if (minutesUntilDue <= 0) {
                        overdueCount++;
                    } else if (minutesUntilDue <= 24 * 60) { // Within 24 hours
                        dueTodayCount++;
                    }
                }
            }
            
            String title = "Task Summary";
            String message = String.format(
                "Total Tasks: %d\n" +
                "Pending: %d\n" +
                "Due Today: %d\n" +
                "Overdue: %d",
                allTasks.size(), pendingCount, dueTodayCount, overdueCount
            );
            
            trayIcon.displayMessage(title, message, TrayIcon.MessageType.INFO);
            
        } catch (Exception e) {
            trayIcon.displayMessage("Error", "Could not retrieve task summary", 
                TrayIcon.MessageType.ERROR);
        }
    }
    
    private void showReminderSettings() {
        int option = JOptionPane.showOptionDialog(
            null,
            "Reminder Settings:\n\n" +
            "• Alerts shown for tasks due within 15 minutes\n" +
            "• Overdue tasks shown for up to 1 hour\n" +
            "• Checks every 2 minutes\n" +
            "• Double-click tray icon to show main window\n\n" +
            "Would you like to disable reminders?",
            "Reminder Settings",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.INFORMATION_MESSAGE,
            null,
            new String[]{"Keep Enabled", "Disable"},
            "Keep Enabled"
        );
        
        if (option == 1) { // Disable selected
            stopReminderService();
            trayIcon.displayMessage("Reminders Disabled", 
                "Task reminders have been turned off", 
                TrayIcon.MessageType.INFO);
        }
    }
    
    private void stopReminderService() {
        if (reminderTimer != null) {
            reminderTimer.cancel();
            reminderTimer = null;
            System.out.println("Reminder service stopped");
        }
    }
    
    public void updateTaskManager(TaskManager newTaskManager) {
        this.taskManager = newTaskManager;
    }
    
    public boolean isTraySupported() {
        return traySupported;
    }
    
    public void showMessage(String title, String message, TrayIcon.MessageType type) {
        if (traySupported && trayIcon != null) {
            trayIcon.displayMessage(title, message, type);
        }
    }
    
    public void cleanup() {
        stopReminderService();
        if (traySupported && trayIcon != null) {
            systemTray.remove(trayIcon);
        }
    }
}
