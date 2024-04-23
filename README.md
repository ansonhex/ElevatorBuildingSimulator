# Elevator Building Simulator

## Overview
The Elevator Building Simulator is a Java application designed to simulate the operation of multiple elevators in a building. It allows users to configure the building settings, control elevator movement, and monitor the system's behavior in real-time.

## Usage
To run the simulator, ensure you have Java installed on your system. 

- Navigate to the directory `res` containing the `ElevatorBuildingSimulator.jar` file and run the following command:

```shell
$ java -jar ElevatorBuildingSimulator.jar
```

### Building Configuration

Upon launching the simulator, you will be prompted to configure the building settings. You can specify the number of floors in the building, the number of elevators, and the maximum capacity of each elevator.

<img width="500" src="https://raw.githubusercontent.com/ansonhe97/rawimages/master/img/initial.png">

### Elevator Control

After configuring the building settings, you will be presented with the elevator control panel. 

- **`Send Request`**: Request `From Floor` -> `To Floor` to the building.
- **`Step`**: Advance the building one step time.
- **`Start Building`**: (Default): Start the building.
- **`Stop Building`**: Stop the building.
- **`Quit`**: Exit the simulator.

<img src="https://raw.githubusercontent.com/ansonhe97/rawimages/master/img/new-default.png">

## License

---