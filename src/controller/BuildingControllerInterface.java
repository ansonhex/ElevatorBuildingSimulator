package controller;

/**
 * Interface defining the operations that can be performed by a building controller.
 * This interface outlines the methods necessary for managing the initialization and
 * control of a building simulation, including elevator operations.
 */
public interface BuildingControllerInterface {

  /**
   * Attempts to initialize the building with the specified number of floors, elevators,
   * and elevator capacity. This method is used to set up the initial parameters for
   * the building simulation.
   *
   * @param floors The number of floors in the building.
   * @param elevators The number of elevators in the building.
   * @param capacity The maximum capacity of each elevator.
   * @return true if the initialization is successful, false otherwise.
   */
  boolean tryInitializeBuilding(int floors, int elevators, int capacity);

  /**
   * Starts the building operations, usually by displaying an initial configuration dialog.
   */
  void start();

  /**
   * Retrieves the number of floors in the building managed by this controller.
   *
   * @return The total number of floors.
   */
  int getFloors();

  /**
   * Advances the state of the building by one step, typically simulating the movement of elevators
   * or processing of requests for one time unit.
   */
  void stepBuilding();

  /**
   * Requests that an elevator move from one specified floor to another.
   * This method handles the creation and processing of elevator requests.
   *
   * @param from The starting floor of the request.
   * @param to The destination floor of the request.
   */
  void requestElevator(int from, int to);

  /**
   * Starts the elevator system within the building, allowing it to begin processing requests
   * and moving elevators.
   */
  void startBuilding();

  /**
   * Stops the elevator system, typically preventing any further elevator movements or
   * request processing.
   */
  void stopBuilding();

  /**
   * Determines whether the building is currently able to start building operations.
   *
   * @return true if the building can start building, false otherwise.
   */
  boolean canStartBuilding();

  /**
   * Determines whether the building is currently able to step building operations.
   *
   * @return true if the building can step building, false otherwise.
   */
  boolean canStepBuilding();


  boolean canRequestBuilding();
}
