package model.building;

import java.util.List;
import model.building.enums.ElevatorSystemStatus;
import model.elevator.ElevatorInterface;
import model.scanerzus.Request;


/**
 * Represents a model.building with a specified number of floors and elevators.
 * This interface defines the operations for managing the elevators within the model.building,
 * including handling requests to move between floors.
 */
public interface BuildingInterface {

  /**
   * Adds a new request for an model.elevator to move between two floors.
   *
   * @param request A {@link Request} object containing the start and end floors.
   * @return true if the request is successfully added, false otherwise.
   * @throws IllegalStateException If the model.elevator system is not accepting requests.
   * @throws IllegalArgumentException If the request is invalid.
   */
  boolean addRequest(Request request) throws IllegalStateException;

  /**
   * Starts the model.elevator system, allowing it to accept and process requests.
   *
   * @return true if the system starts successfully, false if it is already running.
   * @throws IllegalStateException If the system is in an unexpected state.
   */
  boolean startElevatorSystem() throws IllegalStateException;

  /**
   * Stops the model.elevator system. All elevators will finish their current tasks and then
   * cease to accept new requests.
   */
  void stopElevatorSystem();

  /**
   * Retrieves the current status of the model.elevator system, including
   * each model.elevator's state.
   *
   * @return A {@link BuildingReport} object containing detailed system status information.
   */
  BuildingReport getElevatorSystemStatus();

  /**
   * Takes a specific model.elevator out of service. The model.elevator will complete
   * its current task and then stop operating.
   *
   * @param elevatorId The ID of the model.elevator to take out of service.
   */
  void takeElevatorOutOfService(int elevatorId);

  /**
   * Takes all elevators in the model.building out of service. Each model.elevator will complete its
   * current task and then stop operating.
   */
  void takeAllElevatorsOutOfService();

  /**
   * Triggers a step in the operation of all elevators, processing any pending requests
   * and moving the elevators accordingly. This method handles both running and stopping
   * phases of the model.elevator system.
   */
  void triggerElevatorStep();

  /**
   * Clears all pending requests for the model.elevator system.
   */
  void clearRequests();

  /**
   * Prints the current status of all elevators and any pending requests.
   */
  void printElevatorStatuses();

  /**
   * Returns the total number of floors in the model.building.
   *
   * @return The total number of floors.
   */
  int getNumberOfFloors();

  /**
   * Returns the total number of elevators in the model.building.
   *
   * @return The total number of elevators.
   */
  int getNumberOfElevators();

  /**
   * Returns the maximum capacity of each model.elevator in the model.building.
   *
   * @return The maximum capacity of each model.elevator.
   */
  int getElevatorCapacity();

  /**
   * Returns the current status of the model.elevator system.
   *
   * @return The current status of the model.elevator system.
   */
  ElevatorSystemStatus getElevatorStatus();

  /**
   * Returns a list of all elevators in the model.building.
   *
   * @return A list of all elevators in the model.building.
   */
  List<ElevatorInterface> getElevators();
}
