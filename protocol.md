# Communication protocol

This document describes the protocol used for communication between the different nodes of the
distributed application.

## Terminology

* Sensor - a device which senses the environment and describes it with a value (an integer value in
  the context of this project). Examples: temperature sensor, humidity sensor.
* Actuator - a device which can influence the environment. Examples: a fan, a window opener/closer,
  door opener/closer, heater.
* Sensor and actuator node - a computer which has direct access to a set of sensors, a set of
  actuators and is connected to the Internet.
* Control-panel node - a device connected to the Internet which visualizes status of sensor and
  actuator nodes and sends control commands to them.
* Graphical User Interface (GUI) - A graphical interface where users of the system can interact with
  it.

## The underlying transport protocol

TODO - what transport-layer protocol do you use? TCP? UDP? What port number(s)? Why did you choose this transport layer protocol?

We decided to go for TCP as our transport-layer protocol. The reason behind this is that a tcp socket connection allows for two way communication between a server and connected clients. This makes it easier to have real time updates as the state of a greenhouse changes.

## The architecture

TODO - show the general architecture of your network. Which part is a server? Who are clients? 
Do you have one or several servers? Perhaps include a picture here. 

The architecture of our application is a greenhouseNode, a server and multiple control panels. Both the greenhouseNode and the control panels are clients of the server.

## The flow of information and events

TODO - describe what each network node does and when. Some periodic events? Some reaction on 
incoming packets? Perhaps split into several subsections, where each subsection describes one 
node type (For example: one subsection for sensor/actuator nodes, one for control panel nodes).

GreenhouseNode:
Once a greenhouseNode starts it will connect to the server and start sending periodic updates as new information gets read in from its sensors. 

Server:
The server works as a binder between the greenhouse and the controlpanels. It's main goal is to recieve information from the greenhouse node and update the connected control panel nodes as needed

Control panel:
Control panels are started up individualy and automatically connects to the server. The get updates periodically from the server which they display in a GUI


## Connection and state

TODO - is your communication protocol connection-oriented or connection-less? Is it stateful or 
stateless? 
We use a stateless connection-oriented approach. This allows us to get two way communication from and to both control panels and the greenhouse node from the server.

## Types, constants

TODO - Do you have some specific value types you use in several messages? They you can describe 
them here.


## Message format

TODO - describe the general format of all messages. Then describe specific format for each 
message type in your protocol.

Every message contains a commandword at the start, this tells the receiver what it should do and if it should send data as a response. 

### Error messages

TODO - describe the possible error messages that nodes can send in your system.

We have no errors, there is never an error :smile:

## An example scenario

TODO - describe a typical scenario. How would it look like from communication perspective? When 
are connections established? Which packets are sent? How do nodes react on the packets? An 
example scenario could be as follows:
1. A sensor node with ID=1 is started. It has a temperature sensor, two humidity sensors. It can also open a window.
2. A sensor node with ID=2 is started. It has a single temperature sensor and can control two fans and a heater.
3. A control panel node is started.
4. Another control panel node is started.
5. A sensor node with ID=3 is started. It has a two temperature sensors and no actuators.
6. After 5 seconds all three sensor/actuator nodes broadcast their sensor data.
7. The user of the first-control panel presses on the button "ON" for the first fan of sensor/actuator node with ID=2.
8. The user of the second control-panel node presses on the button "turn off all actuators".


Server Starts
1. Server opens port 8765 and waits for clients to connect.

Connection of greenhouseNode
1. GreenhouseNode requests to connect to serverSocket on greenhouseServer (Port 8765)
2. GreenhouseNode sends a handshake request saying "I am Greenhouse"
3. Server then sets the connected socket as the greenhouse (All future control commands are sent through this socket)

Connection of control panel
1. control panel request to connecto to serverSocket greenhouseServer (Port 8765)
2. control panel sends a handshake request saying "I am controlPanel"
3. Server sets connection as a controlpanel (All updates from the greenhouse will be sent through this socket)
4. Server sends initial information about available nodes.

Control panel requests an actuator to open or close
1. Message gets sent from control panel to the server containing the nodeId and actuatorId of the actuator to be changed and the wanted state.
2. When message gets recieved on the server it gets sent along to the greenhouse
3. The greenhouse then changes the state of the actuator 

Actuator changes on a sensorActuatorNode
1. The greenhouseNode sends an update message to the server that the actuator state was changed
2. This state change then gets sent along to all connected control panels so that all states get synced up with the new state


## Reliability and security

TODO - describe the reliability and security mechanisms your solution supports.

Reliability comes from TCP reliability.
Packages are encrypted with symetric keys.
