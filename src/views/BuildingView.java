package views;

import controller.BuildingController;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dialog;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import model.building.BuildingReport;
import model.building.enums.ElevatorSystemStatus;
import model.elevator.ElevatorReport;
import model.scanerzus.Request;

/**
 * The main view class for the Building Elevator Simulator GUI.
 * This class handles the GUI elements necessary to display and control
 * the elevator simulation, including floors, elevator shafts, and control panels.
 */
public class BuildingView extends JFrame implements BuildingViewInterface {
  private BuildingController controller;
  private JPanel floorPanel;
  private JPanel elevatorShaftsPanel;
  private JPanel controlPanel;
  private JPanel statusPanel;
  private JTextArea requestDisplay;
  private JButton startButton;
  private JButton stopButton;
  private JButton stepButton;
  private JButton requestButton;

  /**
   * Constructs a new BuildingView, initializing the user interface components
   * and setting up the main frame.
   */
  public BuildingView() {
    super("Building Elevator Simulator");
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    setSize(1024, 768);
    setLocationRelativeTo(null);
    initializeUi();
    // System.out.println("BuildingView created");
  }

  /**
   * Displays the initial configuration dialog to gather user inputs for the number
   * of floors, elevators, and elevator capacity before initializing the building simulation.
   */
  @Override
  public void displayInitialConfigDialog() {
    // create a dialog to ask for the initial configuration
    JDialog configDialog = new JDialog(
        this, "Configure Building", Dialog.ModalityType.APPLICATION_MODAL);

    // set the dialog to close the application when the user closes the dialog
    configDialog.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
    configDialog.addWindowListener(new WindowAdapter() {
      public void windowClosing(WindowEvent e) {
        System.exit(0);
      }
    });

    configDialog.setLayout(new GridLayout(4, 2, 5, 5));

    // create labels and text fields for the user to input the number of floors,
    // elevators, and elevator capacity
    final JLabel floorsLabel = new JLabel(" Number of floors:");
    JTextField floorsField = new JTextField("11");

    final JLabel elevatorsLabel = new JLabel(" Number of elevators:");
    JTextField elevatorsField = new JTextField("8");

    final JLabel capacityLabel = new JLabel(" Elevator capacity:");
    JTextField capacityField = new JTextField("3");

    // create a button to enter
    JButton enterButton = new JButton("Enter");
    enterButton.addActionListener(e -> {
      try {
        // get the values from the spinners
        int floors = Integer.parseInt(floorsField.getText());
        int elevators = Integer.parseInt(elevatorsField.getText());
        int capacity = Integer.parseInt(capacityField.getText());
        // update to the controller
        if (controller.tryInitializeBuilding(floors, elevators, capacity)) {
          configDialog.dispose();
        }
      } catch (NumberFormatException ex) {
        showErrorMessage("Please enter valid integers.");
      }
    });

    // add components to the dialog
    configDialog.add(floorsLabel);
    configDialog.add(floorsField);
    configDialog.add(elevatorsLabel);
    configDialog.add(elevatorsField);
    configDialog.add(capacityLabel);
    configDialog.add(capacityField);
    // add an empty label to fill the grid
    configDialog.add(new JLabel());
    configDialog.add(enterButton);

    // set the size of the dialog
    configDialog.pack();
    configDialog.setLocationRelativeTo(this);
    configDialog.setVisible(true);
  }

  /**
   * Initializes the user interface, setting up panels for floors, elevator shafts,
   * status, and control elements.
   */
  private void initializeUi() {
    // System.out.println("Initializing UI");
    setLayout(new BorderLayout());

    // left: floors
    floorPanel = new JPanel();
    floorPanel.setLayout(new GridLayout(0, 1));
    add(floorPanel, BorderLayout.WEST);

    // center: elevator shafts
    elevatorShaftsPanel = new JPanel();
    elevatorShaftsPanel.setLayout(new GridLayout(1, 0)); // dynamically add elevators
    // add the elevator shafts panel to a scroll pane
    JScrollPane shaftScrollPane = new JScrollPane(elevatorShaftsPanel);
    shaftScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
    shaftScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
    add(shaftScrollPane, BorderLayout.CENTER);

    statusPanel = new JPanel(new BorderLayout());
    add(statusPanel, BorderLayout.NORTH);

    requestDisplay = new JTextArea(3, 20);
    requestDisplay.setEditable(false);
    JScrollPane scrollPane = new JScrollPane(requestDisplay);
    statusPanel.add(scrollPane, BorderLayout.CENTER);

    // bottom: control panel
    initializeControlPanel();

    // System.out.println("UI Initialized");
  }

  /**
   * Sets the controller for this view, enabling the view to communicate with the
   * controller for processing user actions.
   *
   * @param controller The building controller associated with this view.
   */
  public void setController(BuildingController controller) {
    this.controller = controller;
  }

  /**
   * Initializes the control panel, including buttons for stepping through the simulation,
   * sending elevator requests, starting, stopping, and quitting the simulation.
   */
  private void initializeControlPanel() {
    controlPanel = new JPanel();
    controlPanel.setLayout(new BorderLayout());
    add(controlPanel, BorderLayout.SOUTH);

    // sub-panel for information
    JPanel infoPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
    JLabel infoLabel = new JLabel("#ID [Direction|Door Status|Wait Timer|Door Timer]");
    infoLabel.setForeground(new Color(178, 178, 178));
    infoPanel.add(infoLabel);
    controlPanel.add(infoPanel, BorderLayout.NORTH);

    // sub-panel for buttons
    JPanel buttonsPanel;
    buttonsPanel = new JPanel(new FlowLayout());
    stepButton = new JButton("Step");
    stepButton.addActionListener(e -> controller.stepBuilding());

    JTextField fromField = new JTextField(10);
    JTextField toField = new JTextField(10);

    requestButton = new JButton("Send Request");
    requestButton.addActionListener(e -> {
      try {
        int from = Integer.parseInt(fromField.getText());
        int to = Integer.parseInt(toField.getText());
        controller.requestElevator(from, to);
        fromField.setText("");
        toField.setText("");
        fromField.requestFocus();
      } catch (NumberFormatException ex) {
        showErrorMessage("Please enter valid integers.");
      }
    });

    startButton = new JButton("Start Building");
    startButton.addActionListener(e -> {
      controller.startBuilding();
    });

    stopButton = new JButton("Stop Building");
    stopButton.addActionListener(e -> {
      controller.stopBuilding();
    });

    JButton quitButton = new JButton("Quit");
    quitButton.addActionListener(e -> System.exit(0));

    buttonsPanel.add(new JLabel("From Floor:"));
    buttonsPanel.add(fromField);
    buttonsPanel.add(new JLabel("To Floor:"));
    buttonsPanel.add(toField);
    buttonsPanel.add(requestButton);
    buttonsPanel.add(stepButton);
    buttonsPanel.add(startButton);
    buttonsPanel.add(stopButton);
    buttonsPanel.add(quitButton);

    // add the buttons panel to the control panel
    controlPanel.add(buttonsPanel, BorderLayout.SOUTH);
  }

  /**
   * Updates the GUI based on the latest status of the building report.
   * This includes refreshing the display of floors, elevators, and requests.
   *
   * @param report The latest building report containing the status of all elevators
   *               and requests in the building.
   */
  @Override
  public void updateView(BuildingReport report) {
    // System.out.println("Updating view");

    // update floors
    floorPanel.removeAll();
    int floors = controller.getFloors();
    for (int i = floors - 1; i >= 0; i--) {
      JLabel floorLabel = new JLabel(" Floor " + i + " ");
      floorLabel.setHorizontalAlignment(SwingConstants.CENTER);
      floorLabel.setForeground(new Color(178, 178, 178));
      floorPanel.add(floorLabel);
    }

    // update elevator shafts
    elevatorShaftsPanel.removeAll();
    int elevatorsCount = report.getElevatorReports().length;
    elevatorShaftsPanel.setLayout(
        new GridLayout(1, elevatorsCount)); // Set layout based on number of elevators
    for (ElevatorReport elevatorReport : report.getElevatorReports()) {
      JPanel elevatorPanel = new JPanel(new BorderLayout());
      elevatorPanel.setBorder(BorderFactory.createLineBorder(Color.GRAY, 1));

      // Create a sub-panel for the elevator to place status
      JPanel floorsPanel = new JPanel(new GridLayout(floors, 1));
      JLabel[] floorLabels = new JLabel[floors];
      boolean[] floorRequests = elevatorReport.getFloorRequests();
      for (int i = 0; i < floors; i++) {
        floorLabels[i] = new JLabel();
        floorLabels[i].setHorizontalAlignment(SwingConstants.CENTER);
        floorLabels[i].setOpaque(true);
        int arrayIndex = floors - 1 - i; // Reverse the array index to match the floor order
        if (floorRequests != null && floorRequests[arrayIndex]) {
          floorLabels[i].setBackground(new Color(188, 244, 178));
        } else {
          floorLabels[i].setBackground(new Color(0, 0, 0, 0)); // Transparent
        }
        floorsPanel.add(floorLabels[i]);
      }

      // Place an elevator marker on the current floor
      int currentFloor = elevatorReport.getCurrentFloor();
      String doorClosedColor = "<font color='#FF71CD'>C</font>";
      String doorOpenColor = "<font color='#8B93FF'>O</font>";
      String doorStatus =
          elevatorReport.isDoorClosed() ? doorClosedColor : doorOpenColor;
      floorLabels[floors - 1 - currentFloor].setText(
          "<html>#" + elevatorReport.getElevatorId()
          + " [" + elevatorReport.getDirection() + "|"
          + doorStatus
          + "|W: " + elevatorReport.getEndWaitTimer()
          + "|D: " + elevatorReport.getDoorOpenTimer() + "]</html>"
      );

      // if ElevatorSystemStatus is outOfService, display "Out of Service" on the elevator
      if (report.getSystemStatus() == ElevatorSystemStatus.outOfService) {
        floorLabels[floors - 1 - currentFloor].setText("Out of Service");
        floorLabels[floors - 1 - currentFloor].setForeground(Color.RED);
      }

      elevatorPanel.add(floorsPanel, BorderLayout.CENTER);
      elevatorShaftsPanel.add(elevatorPanel);
    }

    // Update the request display
    updateRequests(report.getUpRequests(), report.getDownRequests());

    // Enable or disable buttons based on the current state
    startButton.setEnabled(controller.canStartBuilding());
    stopButton.setEnabled(!controller.canStartBuilding());
    stepButton.setEnabled(controller.canStepBuilding());
    requestButton.setEnabled(controller.canRequestBuilding());

    // Refresh the GUI
    revalidate();
    repaint();
  }

  /**
   * Updates the display of up and down requests in the building.
   *
   * @param upRequests   List of up requests currently active.
   * @param downRequests List of down requests currently active.
   */
  private void updateRequests(List<Request> upRequests, List<Request> downRequests) {
    StringBuilder sb = new StringBuilder();

    // Display the up and down requests
    sb.append("^ Request [(");
    sb.append(String.format("%03d", upRequests.size()));
    sb.append(")] ");
    if (upRequests.isEmpty()) {
      sb.append("None");
    } else {
      for (Request req : upRequests) {
        sb.append(req.toString()).append("  ");
      }
    }

    sb.append("\n");

    sb.append("v Request [(");
    sb.append(String.format("%03d", downRequests.size()));
    sb.append(")] ");
    if (downRequests.isEmpty()) {
      sb.append("None\n");
    } else {
      for (Request req : downRequests) {
        sb.append(req.toString()).append("  ");
      }
    }
    requestDisplay.setText(sb.toString());
  }

  /**
   * Displays an error message dialog with the specified message.
   *
   * @param message The error message to display.
   */
  @Override
  public void showErrorMessage(String message) {
    JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
  }
}
