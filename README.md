# P2P_CI_Index# P2P
Project for P2P server-client towards CSC/ECE 573
In this project, we implemented a peer-to-peer (P2P) system with a centralized index (CI) for downloading RFCs. 

A server runs on a well-known port, 7734, and serves multiple clients. A client can open a connection with a server and register itself with the server by providing it with the RFCs available to client. The client can also request the server to present it with a list of peers of possess the desired RFC. The client can then make a connection with the respective peer to download the RFC text file.  We wrote all the code in Java and used thread programming to serve multiple requests to the Server as well as to the peers. We run the Client to Server communication on the main process and the upload part on a separate thread. In the following sections, we provide details as to how we designed the project structure and provide certain code screenshots to supplement that.

HOW TO RUN THE CODE
To make the code: in the main directory, just type make jar. This will create a JAR file called server-client.jar
To run a server at port 7734: java -jar server-client.jar server
This will spawn a server bound to 7734.

To run a client: java -jar server-client.jar client
This will spawn a client. The interface will ask to provide a hostname and upload port. After that, an index of options will be provided for which you can follow the instructions on your screen.
