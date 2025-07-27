@echo off
echo Starting Smart Task Scheduler from JAR...
cd /d "%~dp0"
java -jar SmartTaskScheduler.jar
if %errorlevel% neq 0 (
    echo.
    echo Error: Could not start the application.
    echo Make sure Java is installed and the JAR file exists.
    echo.
    pause
)
