package main;

import controller.BuildingController;
import javax.swing.SwingUtilities;
import views.BuildingView;

/**
 * The MainConsole class serves as the entry point for the elevator simulation application.
 * This class initializes the user interface and sets up the components for the application
 * to function, tying together the view and controller components in a MVC architecture.
 */
public class MainConsole {
  /**
   * The main method that sets up the application's user interface and controllers.
   * It ensures that the GUI creation is done on the Event Dispatch Thread (EDT),
   * which is the recommended practice for Swing applications to prevent thread-safety issues.
   *
   * @param args command-line arguments passed to the application (not used).
   */
  public static void main(String[] args) {
    SwingUtilities.invokeLater(() -> {
      BuildingView view = new BuildingView();
      // model is also created from view from user input
      BuildingController controller = new BuildingController(view);
      controller.start();
    });
  }
}