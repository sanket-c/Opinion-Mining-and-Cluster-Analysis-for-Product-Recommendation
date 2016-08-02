@echo off

SET LIBRARY_PATH=.\lib

echo Setting the Classpath

setLocal EnableDelayedExpansion

for /R .\lib %%a in (*.jar)  do (
	set CLASSPATH=!CLASSPATH!;%%a
)

set CLASSPATH=!CLASSPATH!"

echo Running the Java command

java -Xms1024m -Xmx2048m com.project.alda.execute.main.Main