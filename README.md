# netty-named-pipe
This netty example shows how to use named pipes under Linux and/or Mac OS. It is based on the [FileServer](http://netty.io/4.1/xref/io/netty/example/file/FileServer.html) example.

## How to run the example
You will need three terminal windows to run this example. In the first window, you start the TCP server:

```
alec@mba ~/project/minetats-all/netty-named-pipe (master) $ ./gradlew run
```
The TCP server listens on port 8023. When it starts, you connect to it in the second terminal window:

```
alec@mba ~/process $ telnet 127.0.0.1 8023
Trying 127.0.0.1...
Connected to localhost.
Escape character is '^]'.
HELO: Type the path of the file to retrieve.
```
Now it is time to start the named pipe in the third terminal window:

```
alec@mba ~/project/minetats-all/netty-named-pipe/named_pipe (master) $ ./named_pipe_test.sh && \
          tail -f named_pipe.log 
named_pipe.sh started on Sun Feb 19 14:28:41 EST 2017, PIPE=/tmp/S043004-v2-1.txt
PID_UNZIP = 23260
```
Now you copy and paste `/tmp/S043004-v2-1.txt` (the name of the named pipe) into the second window and press Enter (in the second window) to start the example. On my mac, it runs for about 30 seconds. When it stops, you see the `Connection closed by foreign host.` message in the second window. It is now time to enter Ctrl-C in the first and in the third window to stop the TCP server and the `unzip` process. To complete the cleanup, run `rm /tmp/S043004-v2-1.txt`.

## The use case
The use case behind this example is serving the market feed data (a tiny portion of some historical ITCH in our case) to multiple clients in parallel. The data is being kept in a zip file. When a client requests the feed, a new process starts unzipping the data into a new named pipe. The TCP server reads the data from this named pipe and passes it on to the client.

I have a project called [Market Feed on Demand](https://docs.google.com/document/d/1QHlrI3dMpesnTSzXpNVuQgpZRZRtslh__NX4PAlumpE/edit) that is based on the approach presented in this example.

## Presently using http, not https, in my gradle files
On my mac, I tried to take care of the certificates used by my mac's JRE. Although I did that in the past more than once, I need guidance each time I use `keytool`. This time, I found an excellent resource: [Joshua Davies](http://commandlinefanatic.com/cgi-bin/showarticle.cgi?article=art032) covered this in his blog. So I just had to follow his lead (or so I thought).

```
alec@mba ~/project/minetats-all/netty-named-pipe/named_pipe (master) $ sudo find / -type f -name cacerts
...
/Library/Java/JavaVirtualMachines/jdk1.7.0_21.jdk/Contents/Home/jre/lib/security/cacerts

alec@mba ~/project/minetats-all/netty-named-pipe/named_pipe (master) $ keytool -list -keystore /Library/Java/JavaVirtualMachines/jdk1.7.0_21.jdk/Contents/Home/jre/lib/security/cacerts

alec@mba ~/project/minetats-all/netty-named-pipe/named_pipe (master) $ keytool -printcert -rfc -sslserver jcenter.bintray.com > certs
alec@mba ~/project/minetats-all/netty-named-pipe/named_pipe (master) $ sudo keytool -importcert -file ./certs -keystore /Library/Java/JavaVirtualMachines/jdk1.7.0_21.jdk/Contents/Home/jre/lib/security/cacerts -alias jcenter
Password:
Enter keystore password:  
...
Trust this certificate? [no]:  yes
Certificate was added to keystore
```

Should have worked, but didn't. So I just switched from `https` to `http` in my gradle files. I wish I knew what was (and still is) wrong with `cacerts` on my mac...