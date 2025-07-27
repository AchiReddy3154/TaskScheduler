import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;

/**
 * Modal dialog for adding/editing tasks
 * Features modern layout with spinners and preset buttons
 */
public class TaskDialog extends JDialog {
    
    private boolean confirmed = false;
    private Task task;
    
    // Input components
    private JTextField titleField;
    private JSpinner prioritySpinner;
    private JSpinner dateSpinner;
    private JSpinner timeSpinner;
    
    // Colors
    private static final Color PRIMARY_COLOR = new Color(70, 130, 180);
    private static final Color SUCCESS_COLOR = new Color(46, 125, 50);
    private static final Color ERROR_COLOR = new Color(244, 67, 54);
    
    public TaskDialog(JFrame parent, String title, Task existingTask) {
        super(parent, title, true);
        this.task = existingTask;
        
        initializeDialog();
        if (existingTask != null) {
            populateFields(existingTask);
        }
    }
    
    private void initializeDialog() {
        setLayout(new BorderLayout(10, 10));
        setSize(600, 520);
        setLocationRelativeTo(getParent());
        setResizable(true);
        setMinimumSize(new Dimension(550, 480));
        
        // Create main content
        add(createHeaderPanel(), BorderLayout.NORTH);
        add(createInputPanel(), BorderLayout.CENTER);
        add(createButtonPanel(), BorderLayout.SOUTH);
        
        // Style the dialog
        getRootPane().setBorder(new EmptyBorder(20, 20, 20, 20));
        
        // Set default button
        getRootPane().setDefaultButton(createSaveButton());
        
        // Focus the title field when dialog opens
        SwingUtilities.invokeLater(() -> {
            titleField.requestFocusInWindow();
            titleField.selectAll();
        });
    }
    
    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        headerPanel.setBorder(new EmptyBorder(0, 0, 15, 0));
        
        JLabel headerLabel = new JLabel(getTitle());
        headerLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 18));
        headerLabel.setForeground(PRIMARY_COLOR);
        
        headerPanel.add(headerLabel);
        return headerPanel;
    }
    
    private JPanel createInputPanel() {
        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 5, 10, 5);
        gbc.anchor = GridBagConstraints.WEST;
        
        // Title input
        gbc.gridx = 0; gbc.gridy = 0;
        mainPanel.add(createLabel("Task Title:"), gbc);
        
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        titleField = new JTextField(30);
        titleField.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 16));
        titleField.setPreferredSize(new Dimension(350, 40));
        titleField.setMinimumSize(new Dimension(300, 40));
        titleField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(150, 150, 150), 2),
            BorderFactory.createEmptyBorder(10, 15, 10, 15)));
        titleField.setBackground(Color.WHITE);
        titleField.setForeground(Color.BLACK);
        titleField.setCaretColor(Color.BLACK);
        
        // Add focus behavior for better visibility
        titleField.addFocusListener(new java.awt.event.FocusListener() {
            @Override
            public void focusGained(java.awt.event.FocusEvent e) {
                titleField.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(PRIMARY_COLOR, 2),
                    BorderFactory.createEmptyBorder(10, 15, 10, 15)));
            }
            
            @Override
            public void focusLost(java.awt.event.FocusEvent e) {
                titleField.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(150, 150, 150), 2),
                    BorderFactory.createEmptyBorder(10, 15, 10, 15)));
            }
        });
        
        mainPanel.add(titleField, gbc);
        
        // Priority section
        gbc.gridx = 0; gbc.gridy = 1; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        mainPanel.add(createLabel("Priority:"), gbc);
        
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        JPanel priorityPanel = createPriorityPanel();
        mainPanel.add(priorityPanel, gbc);
        
        // Date section
        gbc.gridx = 0; gbc.gridy = 2; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        mainPanel.add(createLabel("Deadline Date:"), gbc);
        
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        dateSpinner = createDateSpinner();
        mainPanel.add(dateSpinner, gbc);
        
        // Time section
        gbc.gridx = 0; gbc.gridy = 3; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        mainPanel.add(createLabel("Deadline Time:"), gbc);
        
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        timeSpinner = createTimeSpinner();
        mainPanel.add(timeSpinner, gbc);
        
        // Quick presets section
        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 2; gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(20, 5, 10, 5);
        mainPanel.add(createPresetsPanel(), gbc);
        
        return mainPanel;
    }
    
    private JLabel createLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 13));
        return label;
    }
    
    private JPanel createPriorityPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        
        prioritySpinner = new JSpinner(new SpinnerNumberModel(5, 1, 10, 1));
        prioritySpinner.setPreferredSize(new Dimension(90, 40));
        prioritySpinner.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 14));
        
        JComponent editor = prioritySpinner.getEditor();
        if (editor instanceof JSpinner.DefaultEditor) {
            ((JSpinner.DefaultEditor) editor).getTextField().setFont(new Font(Font.SANS_SERIF, Font.BOLD, 16));
            ((JSpinner.DefaultEditor) editor).getTextField().setHorizontalAlignment(JTextField.CENTER);
        }
        
        panel.add(prioritySpinner);
        panel.add(Box.createHorizontalStrut(15));
        
        // Priority preset buttons
        String[] priorities = {"Low", "Med", "High", "Urgent"};
        int[] values = {2, 5, 8, 10};
        Color[] colors = {Color.GRAY, PRIMARY_COLOR, new Color(255, 152, 0), ERROR_COLOR};
        
        for (int i = 0; i < priorities.length; i++) {
            JButton btn = createPresetButton(priorities[i], colors[i]);
            btn.setPreferredSize(new Dimension(65, 30));
            final int value = values[i];
            btn.addActionListener(e -> prioritySpinner.setValue(value));
            panel.add(btn);
            if (i < priorities.length - 1) {
                panel.add(Box.createHorizontalStrut(8));
            }
        }
        
        return panel;
    }
    
    private JSpinner createDateSpinner() {
        JSpinner spinner = new JSpinner(new SpinnerDateModel());
        JSpinner.DateEditor dateEditor = new JSpinner.DateEditor(spinner, "MMM dd, yyyy");
        spinner.setEditor(dateEditor);
        spinner.setPreferredSize(new Dimension(220, 40));
        spinner.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 14));
        
        // Style the spinner
        JComponent editor = spinner.getEditor();
        if (editor instanceof JSpinner.DefaultEditor) {
            ((JSpinner.DefaultEditor) editor).getTextField().setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 14));
            ((JSpinner.DefaultEditor) editor).getTextField().setHorizontalAlignment(JTextField.CENTER);
        }
        
        // Set default to tomorrow
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_MONTH, 1);
        spinner.setValue(cal.getTime());
        
        return spinner;
    }
    
    private JSpinner createTimeSpinner() {
        JSpinner spinner = new JSpinner(new SpinnerDateModel());
        JSpinner.DateEditor timeEditor = new JSpinner.DateEditor(spinner, "HH:mm");
        spinner.setEditor(timeEditor);
        spinner.setPreferredSize(new Dimension(140, 40));
        spinner.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 14));
        
        // Style the spinner
        JComponent editor = spinner.getEditor();
        if (editor instanceof JSpinner.DefaultEditor) {
            ((JSpinner.DefaultEditor) editor).getTextField().setFont(new Font(Font.SANS_SERIF, Font.BOLD, 14));
            ((JSpinner.DefaultEditor) editor).getTextField().setHorizontalAlignment(JTextField.CENTER);
        }
        
        // Set default to 12:00
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, 12);
        cal.set(Calendar.MINUTE, 0);
        spinner.setValue(cal.getTime());
        
        return spinner;
    }
    
    private JPanel createPresetsPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createTitledBorder("Quick Deadline Presets"));
        panel.setPreferredSize(new Dimension(450, 80));
        
        JPanel buttonPanel = new JPanel(new GridLayout(2, 2, 10, 8));
        buttonPanel.setBorder(new EmptyBorder(5, 15, 10, 15));
        
        String[] presets = {"Today 6PM", "Tomorrow 12PM", "Next Week", "Urgent (2h)"};
        ActionListener[] actions = {
            e -> setQuickPreset(0, 18, 0),
            e -> setQuickPreset(1, 12, 0),
            e -> setQuickPreset(7, 9, 0),
            e -> setQuickPreset(0, 0, 2)
        };
        
        for (int i = 0; i < presets.length; i++) {
            JButton btn = createPresetButton(presets[i], PRIMARY_COLOR);
            btn.setPreferredSize(new Dimension(120, 32));
            btn.addActionListener(actions[i]);
            buttonPanel.add(btn);
        }
        
        panel.add(buttonPanel);
        return panel;
    }
    
    private JButton createPresetButton(String text, Color backgroundColor) {
        JButton button = new JButton(text);
        button.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 11));
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));
        button.setBackground(backgroundColor);
        button.setForeground(Color.WHITE);
        button.setOpaque(true);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        // Add hover effect
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            Color originalColor = backgroundColor;
            
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                button.setBackground(originalColor.darker());
            }
            
            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                button.setBackground(originalColor);
            }
        });
        
        return button;
    }
    
    private void setQuickPreset(int daysFromNow, int hour, int hoursFromNow) {
        Calendar cal = Calendar.getInstance();
        
        if (hoursFromNow > 0) {
            cal.add(Calendar.HOUR_OF_DAY, hoursFromNow);
        } else {
            cal.add(Calendar.DAY_OF_MONTH, daysFromNow);
            cal.set(Calendar.HOUR_OF_DAY, hour);
            cal.set(Calendar.MINUTE, 0);
        }
        
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        
        dateSpinner.setValue(cal.getTime());
        timeSpinner.setValue(cal.getTime());
    }
    
    private JPanel createButtonPanel() {
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        buttonPanel.setBorder(new EmptyBorder(10, 0, 0, 0));
        
        JButton cancelButton = new JButton("Cancel");
        cancelButton.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 13));
        cancelButton.setPreferredSize(new Dimension(100, 35));
        cancelButton.addActionListener(e -> {
            confirmed = false;
            dispose();
        });
        
        JButton saveButton = createSaveButton();
        
        buttonPanel.add(cancelButton);
        buttonPanel.add(saveButton);
        
        return buttonPanel;
    }
    
    private JButton createSaveButton() {
        JButton saveButton = new JButton(task == null ? "Add Task" : "Update Task");
        saveButton.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 13));
        saveButton.setPreferredSize(new Dimension(120, 35));
        saveButton.setBackground(SUCCESS_COLOR);
        saveButton.setForeground(Color.WHITE);
        saveButton.setOpaque(true);
        saveButton.setFocusPainted(false);
        
        saveButton.addActionListener(e -> {
            if (validateAndSave()) {
                confirmed = true;
                dispose();
            }
        });
        
        return saveButton;
    }
    
    private boolean validateAndSave() {
        String title = titleField.getText().trim();
        if (title.isEmpty()) {
            showError("Task title cannot be empty.");
            titleField.requestFocus();
            return false;
        }
        
        try {
            int priority = (Integer) prioritySpinner.getValue();
            
            // Combine date and time
            java.util.Date dateValue = (java.util.Date) dateSpinner.getValue();
            java.util.Date timeValue = (java.util.Date) timeSpinner.getValue();
            
            Calendar dateCal = Calendar.getInstance();
            dateCal.setTime(dateValue);
            
            Calendar timeCal = Calendar.getInstance();
            timeCal.setTime(timeValue);
            
            dateCal.set(Calendar.HOUR_OF_DAY, timeCal.get(Calendar.HOUR_OF_DAY));
            dateCal.set(Calendar.MINUTE, timeCal.get(Calendar.MINUTE));
            dateCal.set(Calendar.SECOND, 0);
            dateCal.set(Calendar.MILLISECOND, 0);
            
            LocalDateTime deadline = LocalDateTime.ofInstant(
                dateCal.toInstant(), java.time.ZoneId.systemDefault());
            
            // Create or update task
            if (task == null) {
                task = new Task(title, priority, deadline);
            } else {
                task.setTitle(title);
                task.setPriority(priority);
                task.setDeadline(deadline);
            }
            
            return true;
            
        } catch (Exception e) {
            showError("Error processing task data: " + e.getMessage());
            return false;
        }
    }
    
    private void populateFields(Task existingTask) {
        titleField.setText(existingTask.getTitle());
        prioritySpinner.setValue(existingTask.getPriority());
        
        LocalDateTime deadline = existingTask.getDeadline();
        Calendar cal = Calendar.getInstance();
        cal.set(deadline.getYear(), deadline.getMonthValue() - 1, deadline.getDayOfMonth());
        dateSpinner.setValue(cal.getTime());
        
        cal.set(Calendar.HOUR_OF_DAY, deadline.getHour());
        cal.set(Calendar.MINUTE, deadline.getMinute());
        timeSpinner.setValue(cal.getTime());
    }
    
    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Validation Error", JOptionPane.ERROR_MESSAGE);
    }
    
    public boolean isConfirmed() {
        return confirmed;
    }
    
    public Task getTask() {
        return task;
    }
}
