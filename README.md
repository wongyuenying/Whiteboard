# Distributed System and Application
## A Simple Shared Whiteboard
### Introduction
It is created by implementing a client-server architecture that allows
multiple users to draw simultaneously on the canvas. Communication between the
server and clients is facilitated through the use of Java RMI. Synchronized methods are
used to ensure thread safety and to ensure proper handling of the states of the objects.
The project is implemented in Java. The first user who creates the application will become
the whiteboard manager. It starts the server by registering the remote objects to the RMI
registry and the program by typing “java CreateWhiteBoard <serverIPAddress>
<serverPort> “username”. Other clients can join in by typing “java JoinWhiteBoard
<serverIPAddress> <serverPort> “username” and they can join in after the manager’s
approval.
