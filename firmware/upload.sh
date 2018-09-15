#! /bin/bash

filename=$1

echo "$filename"

# port=`cat /home/pi/degasser/temp/serialPortFile.txt`

port=`cat /Users/elimorris/documents/eclipse-workspace/degasser/temp/serialPortFile.txt`

echo "$port"

modem=/dev/$port

echo "$modem"

#stty -f "$modem" 1200;stty stop "$modem";
/home/pi/arduino/hardware/tools/bossac/1.6.1-arduino/bossac \
-a -i -d --port="$port" -U false -e -w -v -b "$filename"

