# Overview

This contains my semester long project for Data Communications at Baylor University.

# Overview
This is a very large project that is meticulously commented using Javadoc which is how I would recommend familiarizing oneself.

This project implements the SHIIP and Jack protocols. SHIIP is a slightly simplified version of HTTP/2. They are similar enough that after enabling TLS I was able to communicate with various websites using my SHIIP client, and connect to my SHIIP server using a web browser. Included in this project are two different Shiip servers. One synchronous and the other asynchronous.

Also implemented is the Jack Protocol which is an HTTP discovery protocol. Both server and client are implemented for this.

# Built With
* Junit 5
* Java 12
* [com.twitter.hpack](https://github.com/twitter/hpack)

# Running the project

Here are the instructions on how to run the various servers and clients located in this repository.

## SHIIP Server
The Shiip server is used for transferring files over the internet. By default the TLS server uses TLS. Keep this in mind when choosing a client to use.

It has three arguments:
+ port: the port to use for accepting connections
+ threadNum: the number of threads to use for fulfilling requests
    + this number determines the size of the thread pool
+ directory base: what to use as the root directory for supplying files

example
```
java shiip.server.Server 5422 10 /home
```

## SHIIP AIO Server

This server performs the same way as the previous server but instead of using a thread pool it acts asynchronous using call backs.
It has two arguments:
+ port: the port to use for accepting connections
+ directory base: what to use as the root directory for supplying files

example
```
java shiip.server.ServerAIO 5422 /home
```

## SHIIP Client

To use the TLS client which you will need to communicate with the bundled server. This client can connect to normal websites, but also works with the shiip server.

Params:
+ server address (ip address)
+ port: the port that the server is running on
+ paths of the files that you would like to retrieve from the server

Example
```
java shiip.client.Client localhost 5422 example.txt example2.txt example3.txt
```

## SHIIP Client no TLS
functions the same way as TLS client except that TLS is not used over the TCP connection.

## Jack Server
Since jack is a HTTP discovery service it is stateful. It keeps track of hosts that are registered with it from the jack client.

Params:
Port: the port to run the server on

Example
```
java jack.server.Server 5433
```

## Jack Client

The Jack client is used to send the desired message to the jack server

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
    
## Jack Multicast Client
Joins and receives messages from a Multicast group

Params:
+ Multicast address
+ port number to run client on
    

# Author
Ian Laird
