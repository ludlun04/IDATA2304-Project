# Project

Course project for the
course [IDATA2304 Computer communication and network programming (2023)](https://www.ntnu.edu/studies/courses/IDATA2304/2023).

Project theme: a distributed smart greenhouse application, consisting of:

* Sensor-actuator nodes
* Visualization nodes

See protocol description in [protocol.md](protocol.md).

## Getting started

There are several runnable classes in the project.

To run the greenhouse part (with sensor/actuator nodes):

* Command line version: run the `main` method inside `CommandLineGreenhouse` class.
* GUI version: run the `main` method inside `GreenhouseGuiStarter` class. Note - if you run the
  `GreenhouseApplication` class directly, JavaFX will complain that it can't find necessary modules.

To run the control panel (only GUI-version is available): run the `main` method inside the
`ControlPanelStarter` class

## Simulating events

If you want to simulate fake communication (just some periodic events happening), you can run
both the greenhouse and control panel parts with a command line parameter `fake`. Check out
classes in the [`no.ntnu.run` package](src/main/java/no/ntnu/run) for more details. 


## How to run the application
Main methods for running the application can be found inside the run package of the application

### How to start the control-panel
Note: To run multiple control-panels in intelij, you have to enable the "Allow multiple instances" setting on ControlPanelStarter.

Navigate to the ControlPanelStarter class inside of the run package, from there run the main method.

### How to start the greenhouse
Navigate to the GreenhouseGuiStarter class inside of the run package, from there run the main method.

### How to start the greenhouse server
Navigate to the GreenhouseServerStarter class inside of the run package, from there run the main method.
