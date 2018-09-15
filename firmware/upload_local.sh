#! /bin/bash

filename='/Users/elimorris/documents/eclipse-workspace/degasser/degasser_gui.ino.arduino_due_x.bin'

echo "$filename"

modem=`ls /dev/cu.usbmodem*`
echo "$modem"

port=`echo "$modem" | cut -d'/' -f 3`

echo "$port"

# echo "Enter the filename including the path: "

# read -r filename

echo "$filename"

stty -f "$modem" 1200;stty stop "$modem";
~/Library/Arduino15/packages/arduino/tools/bossac/1.6.1-arduino/bossac \
-a -i -d --port="$port" -U false -e -w -v -b "$filename"

