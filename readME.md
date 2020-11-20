# Overview

This contains my semester long project for Data Communications at Baylor University.

# Overview
This is a very large project that is meticulously commented using Javadoc. Please read through the javadoc to famiarize yourself with the code.

This project implements the SHIIP and Jack protocols. SHIIP is a slightly simplified version of HTTP/2. They are similar enough that after enabling TLS I was able to communicate with various websites using my SHIIP client, and connect to my SHIIP server using a web browser. 

Included in this project are two different Shiip servers. One synchronous and the other asynchronous.

Also implemented is the Jack Protocol which is an HTTP discovery protocol. Both server and client are implemented for this.

# Built With
* Junit 5
* Java 12
* [com.twitter.hpack](https://github.com/twitter/hpack)

# Running the project

Here are the instructions on how to run the various servers and clients located in this repository.

## SHIIP Server

Params:
+ port: the port to use for accepting connections
+ threadNum: the number of threads to use for fulfilling requests
    + this number determines the size of the thread pool
+ directory base: what to use as the root directory for supplying files

The Shiip server is used for transferring files over the internet. This server uses TLS.

### Example
```
java shiip.server.Server 9999 10 /home
```

## SHIIP AIO Server

Params:
+ port: the port to use for accepting connections
+ directory base: what to use as the root directory for supplying files

While the previous server used multithreading to service multiple clients at the same time, this server uses java callbacks. This server also does not use TLS.

### Example
```
java shiip.server.ServerAIO 9999 /home
```

## SHIIP Client

Params:
+ server address (ip address)
+ port: the port that the server is running on
+ paths of the files that you would like to retrieve from the server

A SHIIP client that uses TLS. This client can connect to normal websites, but you can also connect it to the SHIIP server. Say for example you wish to retrieve the file /home/example.txt, /home/example2.txt and /home/example3.txt from a shiip server running on your local machine at port 9999 with root directory /home, you would use the following command. If you wish to see how to configure a shiip server to act this way look at the example for SHIIP Server.

### Example
```
java shiip.client.Client localhost 9999 example.txt example2.txt example3.txt
```

## SHIIP Client no TLS
Functions exactly the same as previous client but does not use TLS for the connection.

### Example
```
java shiip.client.ClientNoTLS localhost 9999 example.txt example2.txt example3.txt
```

## Jack Server

Params:
Port: the port to run the server on

Jack is a HTTP discovery service. Unlike SHIIP it is stateful, and also unlike SHIIP, it uses UDP for its communication instead of TCP. It receives messages from the Jack Server in UDP packets and then acts on them. Read the Jack Client section to see more about the message types.

### Example
```
java jack.server.Server 5433
```

## Jack Client

params:
+ server: address of the server
+ port: port of the server
+ OP: the type of message to send to the Jack server
    + N: new : indicates a new record for the server
    + Q query : query for the server
    + A: ACK : 
    + R: Response : 
    + E: error : 
+ the string for the message type indicated by OP
    + NEW: give host followed by port of the SHIIP server i.e. localhost 5422
    + QUERY: give string to match on for the host
        + think of the string as being surrounded by wildcards i.e. %string%
    + Error: do not send this message
    + ACK: do not send this message
    + Response: do not send this message
    
The Jack client is used to send the desired message to the jack server

### Example 1
This example registers a new SHIIP server with a JACK server on port 5433.
```
java jack.client.Client localhost 5433 N localhost 9999
```

### Example 2
This example queries the server to see what SHIIP servers are available. Note that sending a '*' means to return all.
```
java jack.client.Client localhost 5433 Q *

```
    
## Jack Multicast Client
Joins and receives messages from a Multicast group

Params:
+ Multicast address
+ port number to run client on

### Example
```
java jack.client.MulticastClient 224.0.0.1 4534
```

# Author
Ian Laird
