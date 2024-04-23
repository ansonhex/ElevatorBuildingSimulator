package controller;

import model.building.Building;
import model.building.BuildingInterface;
import model.building.enums.ElevatorSystemStatus;
import model.scanerzus.Request;
import views.BuildingView;

/**
 * The BuildingController is responsible for managing the interaction between the view and the model
 * in the elevator simulation application. It handles user actions, updates the model accordingly,
 * and refreshes the view to reflect changes in the model.
 */
public class BuildingController implements BuildingControllerInterface {
  private BuildingInterface model;
  private final BuildingView view;

  /**
   * Constructs a BuildingController with the specified view.
   * It initializes the controller within the view to ensure bidirectional communication.
   *
   * @param view The view component this controller will manage.
   */
  public BuildingController(BuildingView view) {
    this.view = view;
    this.view.setController(this);
  }

  /**
   * Initiates the process to display the initial configuration dialog for the building.
   */
  public void start() {
    view.displayInitialConfigDialog();
  }

  /**
   * Attempts to initialize the building model with the specified parameters. If successful,
   * starts the elevator system and updates the view to reflect the initialized state.
   *
   * @param floors The number of floors in the building.
   * @param elevators The number of elevators in the building.
   * @param capacity The capacity of each elevator.
   * @return true if the building was successfully initialized, false if an error occurred.
   */
  public boolean tryInitializeBuilding(int floors, int elevators, int capacity) {
    try {
      model = new Building(floors, elevators, capacity);
      // default to start building
      model.startElevatorSystem();
      view.updateView(model.getElevatorSystemStatus());
      view.setVisible(true);
      return true;
    } catch (IllegalArgumentException e) {
      view.showErrorMessage(e.getMessage());
      return false;
    }
  }

  /**
   * Returns the number of floors in the building managed by this controller.
   *
   * @return The total number of floors in the building.
   */
  public int getFloors() {
    return model.getNumberOfFloors();
  }

  /**
   * Advances the state of the elevator by one step and updates the view to reflect any changes.
   */
  @Override
  public void stepBuilding() {
    model.triggerElevatorStep();
    view.updateView(model.getElevatorSystemStatus());
  }

  /**
   * Handles a request to move an elevator from a specified start floor to a target floor.
   *
   * @param from The floor number where the elevator request is made.
   * @param to The target floor number to which the elevator should go.
   */
  @Override
  public void requestElevator(int from, int to) {
    try {
      model.addRequest(new Request(from, to));
      view.updateView(model.getElevatorSystemStatus());
    } catch (IllegalArgumentException | IllegalStateException e) {
      view.showErrorMessage(e.getMessage());
    }
  }

  /**
   * Starts the elevator system and updates the view.
   */
  @Override
  public void startBuilding() {
    try {
      model.startElevatorSystem();
      view.updateView(model.getElevatorSystemStatus());
    } catch (IllegalStateException e) {
      view.showErrorMessage(e.getMessage());
    }
  }

  /**
   * Stops the elevator system and updates the view.
   */
  @Override
  public void stopBuilding() {
    try {
      model.stopElevatorSystem();
      view.updateView(model.getElevatorSystemStatus());
    } catch (IllegalStateException e) {
      view.showErrorMessage(e.getMessage());
    }
  }

  /**
   * Determines whether the building is currently able to start building operations.
   *
   * @return true if the building can start building, false otherwise.
   */
  @Override
  public boolean canStartBuilding() {
    return model.getElevatorSystemStatus().getSystemStatus() == ElevatorSystemStatus.outOfService;
  }

  /**
   * Determines whether the building is currently able to step building operations.
   *
   * @return true if the building can step building, false otherwise.
   */
  @Override
  public boolean canStepBuilding() {
    ElevatorSystemStatus status = model.getElevatorSystemStatus().getSystemStatus();
    return status == ElevatorSystemStatus.running || status == ElevatorSystemStatus.stopping;
  }

  /**
   * Determines whether the building is currently able to request building operations.
   *
   * @return true if the building can request building, false otherwise.
   */
  @Override
  public boolean canRequestBuilding() {
    ElevatorSystemStatus status = model.getElevatorSystemStatus().getSystemStatus();
    return status == ElevatorSystemStatus.running;
  }
}
