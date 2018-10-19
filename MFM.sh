#!/bin/bash
# Command Switches: -list (list only view); -d (debug logging); -m (memory usage logging);

if [[ $(uname -m) == 'x86_64' ]]; then
  java -XX:+UseG1GC -XX:MaxGCPauseMillis=200 -Xms1g -Xmx3g -jar ./MFM.jar
else
  java -XX:+UseG1GC -XX:MaxGCPauseMillis=200 -Xms1280m -Xmx1280m -jar ./MFM.jar
fi

# Next entry is for full debug
#java -XX:+UseG1GC -XX:MaxGCPauseMillis=200 -Xloggc:./Logs/MFM_GC_log.txt -Xms1g -Xmx3g -jar ./MFM.jar -d -s -m