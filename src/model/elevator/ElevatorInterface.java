package model.elevator;

import java.util.List;
import model.building.enums.Direction;
import model.scanerzus.Request;


/**
 * An interface for an model.elevator.
 */
public interface ElevatorInterface {

  /**
   * ElevatorStatus ID getter.
   *
   * @return the model.elevator ID as a string.
   */
  int getElevatorId();

  /**
   * Returns the maximum number of floors the model.elevator can go to.
   *
   * @return the maximum number of floors the model.elevator can go to.
   */
  int getMaxFloor();


  /**
   * maxOccupancy getter
   * Notice that it is not the responsibility of the model.elevator to
   * keep track of the people in the model.elevator.
   *
   * @return the maximum number of people that can fit in the model.elevator.
   */
  int getMaxOccupancy();

  /**
   * Returns the current floor of the model.elevator.
   *
   * @return the current floor of the model.elevator.
   */

  int getCurrentFloor();


  /**
   * Returns the direction the model.elevator is moving in.
   *
   * @return the direction the model.elevator is moving in.
   */
  Direction getDirection();

  /**
   * Returns the door status of the model.elevator.
   *
   * @return the door status of the model.elevator.
   */
  boolean isDoorClosed();

  /**
   * Return the current stop requests.
   *
   * @return the current stop requests.
   */
  boolean[] getFloorRequests();

  /**
   * start model.elevator.
   * This will start the model.elevator if the model.elevator is on the ground floor.
   * This means the model.elevator will accept requests and will start its up and down routine.
   */
  void start();


  /**
   * Take out of service.
   */
  void takeOutOfService();

  /**
   * Moves the model.elevator by one floor.
   * The model.elevator is going to move by one floor in the direction it is currently moving.
   * If the model.elevator is stopped, it will not move.
   * If the model.elevator arrives at a floor where it is supposed to stop then it will open
   * its doors and let people out.
   * The model.elevator will stop for 3 steps then it will close its doors and move on.
   * If the model.elevator arrives at the top floor, it will wait for 5 steps then go down.
   * If the model.elevator arrives at the bottom floor, it will wait for 5 steps then go up.
   */
  void step();

  /**
   * processUpRequests.
   * This will tell the model.elevator to process these upRequests on the next run.
   * These are only accepted when the model.elevator is at the bottom floor.
   *
   * @param requests the request to add to the model.elevator.
   */
  void processRequests(List<Request> requests) throws IllegalArgumentException;


  /**
   * isTakingRequests.
   * This will tell the model.building if the model.elevator is taking requests.
   *
   * @return true if the model.elevator is taking requests, false otherwise.
   */
  boolean isTakingRequests();

  /**
   * This method is used to get the model.elevator status ElevatorReport.
   *
   * @return the model.elevator status.
   */
  ElevatorReport getElevatorStatus();
}
