@echo off
REM Command Switches: -list (list only view); -d (debug logging); -m (memory usage logging);

java -XX:+UseG1GC -XX:MaxGCPauseMillis=200 -Xms1g -Xmx3g -jar .\MFM.jar

REM Next entry is for 32 bit Java if 64 bit is not possible
REM java -XX:+UseG1GC -XX:MaxGCPauseMillis=200 -Xms1280m -Xmx1280m -jar .\MFM.jar

REM Next entry is for full debug
REM java -XX:+UseG1GC -XX:MaxGCPauseMillis=200 -Xloggc:./Logs/MFM_GC_log.txt -Xms1g -Xmx3g -jar .\MFM.jar -d -s -m
