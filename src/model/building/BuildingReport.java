package model.building;

import java.util.List;
import model.building.enums.ElevatorSystemStatus;
import model.elevator.ElevatorReport;
import model.scanerzus.Request;


/**
 * This is the reporting class for the model.building.
 */
public class BuildingReport {
  int numFloors;
  int numElevators;

  int elevatorCapacity;

  ElevatorReport[] elevatorReports;

  List<Request> upRequests;

  List<Request> downRequests;

  ElevatorSystemStatus systemStatus;

  /**
   * This constructor is used to create a new BuildingReport object.
   *
   * @param numFloors        The number of floors in the model.building.
   * @param numElevators     The number of elevators in the model.building.
   * @param elevatorCapacity The capacity of the elevators.
   * @param elevatorsReports The status of the elevators.
   * @param upRequests       The up requests for the elevators.
   * @param downRequests     The down requests for the elevators.
   * @param systemStatus     The status of the model.elevator system.
   */
  public BuildingReport(int numFloors,
                        int numElevators,
                        int elevatorCapacity,
                        ElevatorReport[] elevatorsReports,
                        List<Request> upRequests,
                        List<Request> downRequests,
                        ElevatorSystemStatus systemStatus) {
    this.numFloors = numFloors;
    this.numElevators = numElevators;
    this.elevatorCapacity = elevatorCapacity;
    this.elevatorReports = elevatorsReports;
    this.upRequests = upRequests;
    this.downRequests = downRequests;
    this.systemStatus = systemStatus;
  }

  /**
   * This method is used to get the number of floors in the model.building.
   *
   * @return the number of floors in the model.building
   */
  public int getNumFloors() {
    return this.numFloors;
  }

  /**
   * This method is used to get the number of elevators in the model.building.
   *
   * @return the number of elevators in the model.building
   */
  public int getNumElevators() {
    return this.numElevators;
  }

  /**
   * This method is used to get the max occupancy of the model.elevator.
   *
   * @return the max occupancy of the model.elevator.
   */
  public int getElevatorCapacity() {
    return this.elevatorCapacity;
  }

  /**
   * This method is used to get the status of the elevators.
   *
   * @return the status of the elevators.
   */
  public ElevatorReport[] getElevatorReports() {
    return this.elevatorReports;
  }

  /**
   * This method is used to get the up requests for the elevators.
   *
   * @return the requests for the elevators.
   */
  public List<Request> getUpRequests() {
    return this.upRequests;
  }

  /**
   * This method is used to get the down requests for the elevators.
   *
   * @return the requests for the elevators.
   */
  public List<Request> getDownRequests() {
    return this.downRequests;
  }

  /**
   * This method is used to get the status of the model.elevator system.
   *
   * @return the status of the model.elevator system.
   */
  public ElevatorSystemStatus getSystemStatus() {
    return this.systemStatus;
  }

  /**
   * This method is used to get the status of the model.elevator system.
   *
   * @return the status of the model.elevator system.
   */
  @Override
  public String toString() {
    StringBuilder report = new StringBuilder();
    report.append("Current Elevator Statuses:\n");
    for (int i = 0; i < elevatorReports.length; i++) {
      report.append("Elevator ").append(i).append(": ").append(
          elevatorReports[i].toString()).append("\n");
    }

    report.append("Up Requests: ");
    if (upRequests.isEmpty()) {
      report.append("None\n");
    } else {
      for (Request request : upRequests) {
        report.append("[").append(request.getStartFloor()).append("->").append(
            request.getEndFloor()).append("] ");
      }
      report.append("\n");
    }

    report.append("Down Requests: ");
    if (downRequests.isEmpty()) {
      report.append("None\n");
    } else {
      for (Request request : downRequests) {
        report.append("[").append(request.getStartFloor()).append("->").append(
            request.getEndFloor()).append("] ");
      }
      report.append("\n");
    }

    return report.toString();
  }
}
