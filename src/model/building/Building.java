package model.building;

import java.util.ArrayList;
import java.util.List;
import model.building.enums.Direction;
import model.building.enums.ElevatorSystemStatus;
import model.elevator.Elevator;
import model.elevator.ElevatorInterface;
import model.elevator.ElevatorReport;
import model.scanerzus.Request;

/**
 * Represents a model.building with a specified number of floors and elevators.
 * This class manages the operation of elevators within the model.building,
 * including handling requests to move between floors.
 */
public class Building implements BuildingInterface {

  private final int numberOfFloors;
  private final int numberOfElevators;
  private final int elevatorCapacity;
  private final List<ElevatorInterface> elevators;
  private ElevatorSystemStatus elevatorStatus;
  private final List<Request> upRequests;
  private final List<Request> downRequests;

  /**
   * Constructs a new Building instance with the specified parameters.
   *
   * @param numberOfFloors The total number of floors in the model.building.
   * @param numberOfElevators The total number of elevators in the model.building.
   * @param elevatorCapacity The maximum capacity of each model.elevator in the model.building.
   * @throws IllegalArgumentException If any parameter is out of the expected range.
   */
  public Building(int numberOfFloors, int numberOfElevators, int elevatorCapacity) {
    if (numberOfFloors < 2) {
      throw new IllegalArgumentException("The number of floors must be at least 2.");
    }
    if (numberOfElevators < 1) {
      throw new IllegalArgumentException("The number of elevators must be at least 1.");
    }
    if (elevatorCapacity < 1) {
      throw new IllegalArgumentException("The model.elevator capacity must be at least 1.");
    }

    this.numberOfFloors = numberOfFloors;
    this.numberOfElevators = numberOfElevators;
    this.elevatorCapacity = elevatorCapacity;
    this.elevators = new ArrayList<>();
    this.elevatorStatus = ElevatorSystemStatus.outOfService; // default status
    this.upRequests = new ArrayList<>();
    this.downRequests = new ArrayList<>();

    // Create elevators and add them to the list
    for (int i = 0; i < numberOfElevators; i++) {
      ElevatorInterface elevator = new Elevator(numberOfFloors, elevatorCapacity);
      elevators.add(elevator);
    }
  }

  /**
   * Adds a new request for an model.elevator to move between two floors.
   *
   * @param request A {@link Request} object containing the start and end floors.
   * @return true if the request is successfully added, false otherwise.
   * @throws IllegalStateException If the model.elevator system is not accepting requests.
   * @throws IllegalArgumentException If the request is invalid.
   */
  @Override
  public boolean addRequest(Request request) throws IllegalStateException {
    // defensive coding
    if (request == null) {
      throw new IllegalArgumentException("Request cannot be null");
    }

    if (request.getStartFloor() < 0 || request.getStartFloor() >= numberOfFloors) {
      throw new IllegalArgumentException(
          "Start floor must be between 0 and " + (numberOfFloors - 1));
    }
    if (request.getEndFloor() < 0 || request.getEndFloor() >= numberOfFloors) {
      throw new IllegalArgumentException(
          "End floor must be between 0 and " + (numberOfFloors - 1));
    }
    if (request.getStartFloor() == request.getEndFloor()) {
      throw new IllegalArgumentException("Start floor and end floor cannot be the same");
    }

    // Add the request to the appropriate list based on the direction
    switch (this.elevatorStatus) {
      case running:
        if (request.getStartFloor() < request.getEndFloor()) {
          upRequests.add(request);
        } else {
          downRequests.add(request);
        }
        return true;
      case outOfService:
      case stopping:
        throw new IllegalStateException("Elevator system is not accepting requests");
      default:
        // defensive coding
        throw new IllegalStateException(
            "Unexpected model.elevator system status: " + elevatorStatus);
    }
  }

  /**
   * Starts the model.elevator system, allowing it to accept and process requests.
   *
   * @return true if the system starts successfully, false if it is already running.
   * @throws IllegalStateException If the system is in an unexpected state.
   */
  @Override
  public boolean startElevatorSystem() throws IllegalStateException {
    switch (this.elevatorStatus) {
      case running:
        return false;
      case stopping:
        throw new IllegalStateException("Elevator system is stopping");
      case outOfService:
        for (ElevatorInterface elevator : elevators) {
          elevator.start();
        }
        this.elevatorStatus = ElevatorSystemStatus.running;
        return true;
      default:
        // defensive coding
        throw new IllegalStateException(
            "Unexpected model.elevator system status: " + elevatorStatus);
    }
  }

  /**
   * Stops the model.elevator system. All elevators will finish their current tasks and then
   * cease to accept new requests.
   */
  @Override
  public void stopElevatorSystem() {
    if (this.elevatorStatus == ElevatorSystemStatus.outOfService
        || this.elevatorStatus == ElevatorSystemStatus.stopping) {
      return;
    }

    this.elevatorStatus = ElevatorSystemStatus.stopping;
    for (ElevatorInterface elevator : elevators) {
      elevator.takeOutOfService();
    }
    // empty all requests
    clearRequests();
  }

  /**
   * Retrieves the current status of the model.elevator system, including each
   * model.elevator's state.
   *
   * @return A {@link BuildingReport} object containing detailed system status information.
   */
  @Override
  public BuildingReport getElevatorSystemStatus() {
    ElevatorReport[] elevatorReports = new ElevatorReport[elevators.size()];
    for (int i = 0; i < elevators.size(); i++) {
      elevatorReports[i] = elevators.get(i).getElevatorStatus();
    }

    return new BuildingReport(
        numberOfFloors,
        numberOfElevators,
        elevatorCapacity,
        elevatorReports,
        upRequests,
        downRequests,
        elevatorStatus
    );
  }

  /**
   * Takes a specific model.elevator out of service. The model.elevator will complete
   * its current task and then stop operating.
   *
   * @param elevatorId The ID of the model.elevator to take out of service.
   */
  @Override
  public void takeElevatorOutOfService(int elevatorId) {
    for (ElevatorInterface elevator : elevators) {
      if (elevator.getElevatorId() == elevatorId) {
        elevator.takeOutOfService();
        break;
      }
    }
  }

  /**
   * Takes all elevators in the model.building out of service. Each model.elevator will complete its
   * current task and then stop operating.
   */
  @Override
  public void takeAllElevatorsOutOfService() {
    for (ElevatorInterface elevator : elevators) {
      elevator.takeOutOfService();
    }
  }

  /**
   * Distributes requests to elevators based on their current floors and directions.
   * Requests for upward movement are assigned to elevators on the ground floor,
   * while requests for downward movement are assigned to elevators on the top floor.
   */
  private void distributeRequests() {
    // If there are no requests, return
    if (upRequests.isEmpty() && downRequests.isEmpty()) {
      return;
    }

    // If the model.elevator system is not running, return
    if (this.elevatorStatus != ElevatorSystemStatus.running) {
      return;
    }

    // main flow: distribute upRequest on top floor and downRequest on bottom floor
    for (ElevatorInterface elevator : elevators) {
      if (elevator.getCurrentFloor() == 0 && !this.upRequests.isEmpty()) {
        processElevatorRequests(elevator, this.upRequests, Direction.UP);
      } else if (elevator.getCurrentFloor() == numberOfFloors - 1 && !this.downRequests.isEmpty()) {
        processElevatorRequests(elevator, this.downRequests, Direction.DOWN);
      }
    }
  }

  /**
   * Processes requests for a given model.elevator, based on its direction.
   * Requests that match the model.elevator's current direction are processed.
   *
   * @param elevator The model.elevator to process requests for.
   * @param requests The list of requests to process.
   * @param direction The direction of the requests.
   */
  private void processElevatorRequests(
       ElevatorInterface elevator,
       List<Request> requests,
       Direction direction
  ) {
    // if model.elevator is going in the same direction as the request
    if (elevator.getDirection() == direction) {
      // retrieve requests for model.elevator
      List<Request> toProcess = getRequestsForElevator(requests);
      try {
        // process requests with model.elevator and remove from main list
        elevator.processRequests(toProcess);
        requests.removeAll(toProcess);
      } catch (IllegalStateException e) {
        System.out.println("Elevator is not accepting requests." + e.getMessage());
      }
    }
  }

  /**
   * Retrieves a subset of requests for an model.elevator, ensuring the number of
   * requests does not exceed the model.elevator's capacity.
   *
   * @param requests The list of requests to parse.
   * @return A list of requests for the model.elevator, constrained by its capacity.
   */
  private List<Request> getRequestsForElevator(List<Request> requests) {
    List<Request> requestsForElevator = new ArrayList<>();
    // parse requests via capacity
    for (int i = 0; i < Math.min((requests.size()), this.elevatorCapacity); i++) {
      requestsForElevator.add(requests.get(i));
    }
    return requestsForElevator;
  }

  /**
   * Triggers a step in the operation of all elevators, processing any pending requests
   * and moving the elevators accordingly. This method handles both running and stopping
   * phases of the model.elevator system.
   */
  @Override
  public void triggerElevatorStep() {
    // Two cases for step: running and stopping
    if (this.elevatorStatus == ElevatorSystemStatus.running) {
      // Distribute requests to elevators
      distributeRequests();
      // Step through each model.elevator
      for (ElevatorInterface elevator : elevators) {
        elevator.step();
      }
    } else if (this.elevatorStatus == ElevatorSystemStatus.stopping) {
      // Step through each model.elevator to go back down to ground floor
      for (ElevatorInterface elevator : elevators) {
        elevator.step();
      }
      // Consider special case when on the ground floor
      // convert stopping to out of service if all elevators are on ground floor
      checkAndStopElevatorSystem();
    }
  }

  /**
   * Checks if all elevators have returned to the ground floor and stops the
   * model.elevator system if so. This method is called during the stopping phase of the system.
   */
  private void checkAndStopElevatorSystem() {
    if (this.elevatorStatus == ElevatorSystemStatus.stopping) {
      boolean allOnGround = true;
      for (ElevatorInterface elevator : this.elevators) {
        if (elevator.getCurrentFloor() != 0) {
          allOnGround = false;
          break;
        }
      }
      if (allOnGround) {
        this.elevatorStatus = ElevatorSystemStatus.outOfService;
      }
    }
  }

  /**
   * Clears all pending requests for the model.elevator system.
   */
  @Override
  public void clearRequests() {
    this.upRequests.clear();
    this.downRequests.clear();
  }

  /**
   * Prints the current status of all elevators and any pending requests.
   */
  @Override
  public void printElevatorStatuses() {
    BuildingReport report = getElevatorSystemStatus();
    // Simply calling the toString method of the BuildingReport object
    System.out.printf(report.toString());
  }

  /**
   * Returns the total number of floors in the model.building.
   *
   * @return The total number of floors.
   */
  @Override
  public int getNumberOfFloors() {
    return this.numberOfFloors;
  }

  /**
   * Returns the total number of elevators in the model.building.
   *
   * @return The total number of elevators.
   */
  @Override
  public int getNumberOfElevators() {
    return this.numberOfElevators;
  }

  /**
   * Returns the maximum capacity of each model.elevator in the model.building.
   *
   * @return The maximum capacity of each model.elevator.
   */
  @Override
  public int getElevatorCapacity() {
    return this.elevatorCapacity;
  }

  /**
   * Returns the current status of the model.elevator system.
   *
   * @return The current status of the model.elevator system.
   */
  @Override
  public ElevatorSystemStatus getElevatorStatus() {
    return this.elevatorStatus;
  }

  /**
   * Returns the list of elevators in the model.building.
   *
   * @return The list of elevators.
   */
  public List<ElevatorInterface> getElevators() {
    return elevators;
  }
}
