# Smart Task Scheduler

A modern Java Swing application for managing tasks with deadline alerts, system tray integration, and persistent storage.

## Features

### 🎯 Core Features
- **Task Management**: Add, edit, delete, and mark tasks as complete
- **Priority System**: Set task priorities (1-10 scale)
- **Deadline Tracking**: Set specific deadlines with date and time
- **Persistent Storage**: Automatic saving/loading from JSON file

### 🔔 System Tray Integration
- **Background Monitoring**: Runs in system tray when minimized
- **Smart Notifications**: Automatic deadline alerts
  - 🔴 **OVERDUE** - Tasks past their deadline (up to 1 hour)
  - 🟡 **URGENT** - Tasks due within 5 minutes
  - 🔵 **REMINDER** - Tasks due within 15 minutes
- **Tray Menu**: Quick access to app functions and task summary

### 🎨 Modern UI
- **Clean Design**: Modern Swing interface with FlatLaf styling
- **Modal Dialogs**: Dedicated dialogs for adding/editing tasks
- **Quick Presets**: Buttons for common deadline times (1 hour, 1 day, etc.)
- **Advanced Filtering**: Filter tasks by status, priority, and deadline
- **Responsive Layout**: Adaptive interface that works on different screen sizes

### 📊 Smart Features
- **Task Filtering**: View tasks by completion status, priority ranges, or time periods
- **Automatic Monitoring**: Background service checks for due tasks every 2 minutes
- **Status Tracking**: Real-time status updates and task counts
- **Data Validation**: Input validation for dates, priorities, and required fields

### Screenshots

<img width="1101" height="818" alt="image" src="https://github.com/user-attachments/assets/67b0bf32-a983-4fa1-9c09-06413185689d" />
<img width="1104" height="857" alt="image" src="https://github.com/user-attachments/assets/438fa713-286e-40a0-bff0-fa278b64ae4b" />
<img width="433" height="125" alt="image" src="https://github.com/user-attachments/assets/3b230119-a84c-4faf-8798-f5dd9e59acad" />
<img width="472" height="152" alt="image" src="https://github.com/user-attachments/assets/5d641a1a-78b6-432d-ac03-73f27d8cdb01" />



## Installation & Usage

### Prerequisites
- Java 8 or higher
- Windows/Linux/macOS with system tray support

### Running the Application

#### Option 1: Pre-built JAR
```bash
java -jar SmartTaskScheduler.jar
```

#### Option 2: From Source
```bash
# Compile
javac -encoding UTF-8 -cp src -d out src/*.java

# Run
java -cp out ModernSwingUI
```

#### Option 3: Build Script (Windows)
```cmd
build-jar.bat
run-jar.bat
```

### First Time Setup
1. Launch the application
2. The main window will appear with an empty task list
3. Click "**+ Add Task**" to create your first task
4. Set a deadline and priority
5. Close the window to minimize to system tray
6. Watch for notifications as deadlines approach!

## System Tray Usage

### Tray Icon Features
- **Double-click**: Show/hide main window
- **Right-click**: Access context menu

### Tray Menu Options
- **Show Task Scheduler**: Bring window to front
- **Hide to Tray**: Minimize window
- **Check Tasks**: Show quick task summary
- **Reminder Settings**: Configure notification preferences
- **Exit**: Close application completely

### Notification Types
The system shows different types of notifications based on urgency:

| Type | Trigger | Appearance |
|------|---------|------------|
| **OVERDUE** | Past deadline (up to 1 hour) | Red error notification |
| **URGENT** | Due within 5 minutes | Yellow warning notification |
| **REMINDER** | Due within 15 minutes | Blue info notification |

## File Structure

```
SmartTaskScheduler/
├── src/
│   ├── ModernSwingUI.java      # Main application window
│   ├── TaskSchedulerTray.java  # System tray integration
│   ├── TaskDialog.java         # Add/edit task dialog
│   ├── Task.java              # Task data model
│   ├── TaskManager.java       # Task management logic
│   ├── StorageHandler.java    # JSON persistence
│   ├── FilterUtils.java       # Task filtering utilities
│   └── LocalDateTimeAdapter.java # JSON date serialization
├── META-INF/
│   └── MANIFEST.MF           # JAR manifest
├── screenshots/              # Application screenshots
├── SmartTaskScheduler.jar   # Executable JAR
├── build-jar.bat           # Windows build script
├── run-jar.bat            # Windows run script
├── tasks.json             # Task data file (auto-created)
├── .gitignore            # Git ignore rules
└── README.md             # This file
```

## Technical Details

### Architecture
- **UI Layer**: Modern Swing with custom styling
- **Business Logic**: Task management and filtering
- **Persistence**: JSON-based storage with Gson
- **System Integration**: Java AWT SystemTray API

### Key Classes
- `ModernSwingUI`: Main application window and UI coordination
- `TaskSchedulerTray`: System tray icon and notification management
- `TaskManager`: Core task operations and business logic
- `StorageHandler`: File I/O and JSON serialization
- `Task`: Data model with validation

### Dependencies
- **Java Standard Library**: Swing, AWT, Time API
- **Gson**: JSON serialization (included)
- **No external UI libraries required** (falls back to Nimbus L&F)

## Development

### Building from Source
```bash
# Compile all classes
javac -encoding UTF-8 -cp src -d out src/*.java

# Create JAR
jar cfm SmartTaskScheduler.jar META-INF/MANIFEST.MF -C out .

# Run
java -jar SmartTaskScheduler.jar
```

### Code Style
- Java 8+ compatible
- Swing best practices
- Clean separation of concerns
- Comprehensive error handling
- UTF-8 encoding for international character support

## Configuration

### Task Data
Tasks are automatically saved to `tasks.json` in the application directory. The format is:

```json
[
  {
    "title": "Sample Task",
    "priority": 5,
    "deadline": "2025-07-27 16:30:00",
    "completed": false
  }
]
```

### System Tray Settings
- Notification frequency: Every 2 minutes
- Alert window: 15 minutes before deadline
- Overdue tracking: Up to 1 hour past deadline
- Icon behavior: Flashes red for urgent alerts

## Troubleshooting

### Common Issues

**System tray not working:**
- Ensure your OS supports system tray
- Check if notifications are enabled in OS settings
- Verify Java has permission for system notifications

**Tasks not saving:**
- Check write permissions in application directory
- Ensure sufficient disk space
- Verify `tasks.json` is not locked by another process

**Notifications not appearing:**
- Confirm system tray support with the demo
- Check Windows notification settings
- Ensure app is not blocked by antivirus

**UI appearance issues:**
- Application falls back to Nimbus look and feel
- Works on all Java-supported platforms
- UI scales with system DPI settings

## License

This project is open source. Feel free to use, modify, and distribute.

## Contributing

1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Test thoroughly
5. Submit a pull request

---

**Smart Task Scheduler** - Never miss a deadline again! 🎯⏰
