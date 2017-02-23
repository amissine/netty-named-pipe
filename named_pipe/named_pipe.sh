#!/bin/bash
# === named_pipe.sh ===
#
# accepts two arguments: FILENAME and FEEDNAME
# requires $ITCH_FILES_DIR

function cleanup {
  echo `date` "killing PID_UNZIP $PID_UNZIP" >> $2.log
  kill $PID_UNZIP
  exit
}
trap cleanup SIGINT SIGTERM

PIPE=/tmp/$2.txt
echo "named_pipe.sh started on "`date`", PIPE="$PIPE > $2.log
mkfifo $PIPE
unzip -p $ITCH_FILES_DIR/$1.zip > $PIPE 2>>$2.log &
PID_UNZIP=$!

echo "PID_UNZIP = $PID_UNZIP Unzipping:" >> $2.log
ls -la $ITCH_FILES_DIR/$1.zip >> $2.log
tail -f $2.log

