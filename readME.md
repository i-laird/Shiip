# Overview

This contains my semester long project for Data Communications at Baylor University.

# Overview
This is a very large project that is meticlously commented using Javadoc which is how I would recommend familiarzing oneself.

This project implements the SHIIP and Jack protocols. SHIIP is a slightly simplified version of HTTP/2. They are similar enough that after enabling TLS I was able to communicate with various websites using my SHIIP server, and connect to my SHIIP server using a web browser. Included in this project are two different Shiip servers. One synchrnous and the other asynchrnous.

Also implemented is the Jack Protocl which is an HTTP discovery protocol. Both server and client are implemented for this.

# Built With
* Junit 5
* Java 12
* [com.twitter.hpack](https://github.com/twitter/hpack)

# Author
Ian Laird
