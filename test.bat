@echo off
cd /d "%~dp0"
if not exist build\classes mkdir build\classes
(for /r src\main\java %%F in (*.java) do @echo %%F)> build\sources-test.txt
(for /r src\test\java %%F in (*.java) do @echo %%F)>> build\sources-test.txt
javac --release 17 -d build\classes @build\sources-test.txt
if errorlevel 1 exit /b 1
java -ea -cp build\classes com.algoscope.EngineTest
