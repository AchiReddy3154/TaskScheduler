@echo off
echo Building Smart Task Scheduler JAR...
cd /d "%~dp0"

echo Step 1: Creating output directory...
if not exist out mkdir out

echo Step 2: Compiling core Java sources...
javac -encoding UTF-8 -cp src -d out src\ModernSwingUI.java src\TaskSchedulerTray.java src\TaskDialog.java src\Task.java src\TaskManager.java src\StorageHandler.java src\LocalDateTimeAdapter.java src\FilterUtils.java
if %errorlevel% neq 0 (
    echo Compilation failed!
    pause
    exit /b 1
)

echo Step 3: Creating JAR with manifest...
jar cfm SmartTaskScheduler.jar META-INF\MANIFEST.MF -C out .
if %errorlevel% neq 0 (
    echo JAR creation failed!
    pause
    exit /b 1
)

echo.
echo ===================================
echo JAR created successfully: SmartTaskScheduler.jar
echo ===================================
echo.
echo To run the application:
echo   Method 1: Double-click SmartTaskScheduler.jar
echo   Method 2: java -jar SmartTaskScheduler.jar
echo.
echo Features included in JAR:
echo   - Enhanced Swing UI with date/time spinners
echo   - Priority preset buttons
echo   - Quick deadline presets
echo   - Task filtering and management
echo   - Persistent storage (tasks.json)
echo.
pause
