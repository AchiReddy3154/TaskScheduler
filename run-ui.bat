@echo off
echo Starting Smart Task Scheduler (Enhanced UI)...
cd /d "%~dp0"
javac -cp src src\SwingSchedulerUI.java src\Task.java src\TaskManager.java src\StorageHandler.java src\LocalDateTimeAdapter.java src\FilterUtils.java
if %errorlevel% neq 0 (
    echo Compilation failed!
    pause
    exit /b 1
)
echo Compilation successful! Starting Enhanced UI...
echo.
echo New Features:
echo - Priority spinner with quick preset buttons (Low/Med/High/Urgent)
echo - Separate date and time spinners for easy selection
echo - Quick deadline presets (Today 6PM, Tomorrow, Next Week, Urgent)
echo - No more typing dates/times manually!
echo.
java -cp src SwingSchedulerUI
pause
