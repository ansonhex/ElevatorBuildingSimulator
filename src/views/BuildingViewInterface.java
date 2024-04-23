package views;

import controller.BuildingController;
import model.building.BuildingReport;

/**
 * Interface defining the required methods for views in the building elevator simulator.
 * This interface ensures that all views can display configuration dialogs, update their display,
 * show error messages, and interact with a controller.
 */
public interface BuildingViewInterface {

  /**
   * Displays a dialog to allow the user to input initial configuration parameters such as
   * the number of floors, elevators, and elevator capacity. This method is intended to be
   * called when the simulation first starts to gather necessary setup information from the user.
   */
  void displayInitialConfigDialog();

  /**
   * Sets the controller for the view. This method is used to establish a connection between
   * the view and its controller, allowing for communication and control flow between the
   * MVC components.
   *
   * @param controller The controller that this view will interact with.
   */
  void setController(BuildingController controller);

  /**
   * Updates the view based on the latest status of the building as provided by the building report.
   * This method is called whenever there is a change in the building's state that needs to
   * be reflected in the view.
   *
   * @param report The latest building report containing the status of all elevators.
   */
  void updateView(BuildingReport report);

  /**
   * Displays an error message in the view. This method is used to inform the user of any errors
   * that occur during the operation of the elevator system, such as invalid requests
   * or system malfunctions.
   *
   * @param message The error message to be displayed to the user.
   */
  void showErrorMessage(String message);
}
