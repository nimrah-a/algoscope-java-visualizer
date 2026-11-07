@echo off
cd /d "%~dp0"
if not exist build\classes mkdir build\classes
(for /r src\main\java %%F in (*.java) do @echo %%F)> build\sources-main.txt
javac --release 17 -d build\classes @build\sources-main.txt
if errorlevel 1 exit /b 1
java -cp build\classes com.algoscope.Main
