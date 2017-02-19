#!/bin/bash
# === named_pipe.sh ===
#
# accepts two arguments: FILENAME and FEEDNAME
# requires $ITCH_FILES_DIR

function cleanup {
  echo `date` "killing PID_UNZIP $PID_UNZIP" >> named_pipe.log
  kill $PID_UNZIP
  exit
}
trap cleanup SIGINT SIGTERM

PIPE=/tmp/$2.txt
echo "named_pipe.sh started on "`date`", PIPE="$PIPE > named_pipe.log
mkfifo $PIPE
unzip -p $ITCH_FILES_DIR/$1.zip > $PIPE &
PID_UNZIP=$!

echo "PID_UNZIP = $PID_UNZIP" >> named_pipe.log
read

