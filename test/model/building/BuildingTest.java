package model.building;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import model.building.enums.ElevatorSystemStatus;
import model.elevator.Elevator;
import model.elevator.ElevatorReport;
import model.scanerzus.Request;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * This class tests the Building class.
 */
public class BuildingTest {
  private Building testBuilding;
  private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
  private final PrintStream originalOut = System.out;

  /**
   * Set up the test.
   */
  @Before
  public void setUp() {
    // reset the static counter in Elevator
    // so that the model.elevator IDs are consistent across tests
    Elevator.resetStaticCounter();
    // redirect the output stream
    System.setOut(new PrintStream(outContent));
    testBuilding = new Building(11, 8, 3);
  }

  @After
  public void tearDown() {
    // reset the output stream
    System.setOut(originalOut);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testConstructorWithTooFewFloors() {
    new Building(1, 8, 3);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testConstructorWithTooFewElevators() {
    new Building(11, 0, 3);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testConstructorWithTooFewCapacity() {
    new Building(11, 8, 0);
  }

  @Test
  public void testConstructorValid() {
    assertEquals(11, testBuilding.getNumberOfFloors());
    assertEquals(8, testBuilding.getNumberOfElevators());
    assertEquals(3, testBuilding.getElevatorCapacity());
  }

  @Test
  public void testStartElevatorSystemAlreadyStarted() {
    testBuilding.startElevatorSystem();
    assertFalse(testBuilding.startElevatorSystem());
  }

  @Test(expected = IllegalStateException.class)
  public void testStartElevatorSystemStopping() {
    testBuilding.startElevatorSystem();
    testBuilding.stopElevatorSystem();
    assertEquals(ElevatorSystemStatus.stopping, testBuilding.getElevatorStatus());
    testBuilding.startElevatorSystem();
  }

  @Test(expected = IllegalArgumentException.class)
  public void testAddNullRequest() {
    testBuilding.addRequest(null);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testAddRequestWithInvalidStartFloor() {
    testBuilding.addRequest(new Request(12, 1));
  }

  @Test(expected = IllegalArgumentException.class)
  public void testAddRequestWithInvalidEndFloor() {
    testBuilding.addRequest(new Request(1, 12));
  }

  @Test(expected = IllegalArgumentException.class)
  public void testAddRequestWithSameStartAndEndFloor() {
    testBuilding.addRequest(new Request(1, 1));
  }

  @Test(expected = IllegalStateException.class)
  public void testAddRequestOutOfService() {
    // model.building not started yet
    testBuilding.addRequest(new Request(1, 2));
  }

  @Test(expected = IllegalStateException.class)
  public void testAddRequestStopping() {
    testBuilding.startElevatorSystem();
    testBuilding.stopElevatorSystem();
    assertEquals(ElevatorSystemStatus.stopping, testBuilding.getElevatorStatus());
    testBuilding.addRequest(new Request(1, 2));
  }

  @Test
  public void testAddRequestValid() {
    testBuilding.startElevatorSystem();
    assertTrue(testBuilding.addRequest(new Request(1, 2)));
    assertTrue(testBuilding.addRequest(new Request(2, 1)));
  }

  @Test
  public void testTakeElevatorOutOfService() {
    testBuilding.startElevatorSystem();
    int elevatorId = 0;
    testBuilding.takeElevatorOutOfService(elevatorId);
    boolean isOutOfService =
        testBuilding.getElevatorSystemStatus().getElevatorReports()[elevatorId].isOutOfService();
    System.out.println(testBuilding.getElevatorSystemStatus().getElevatorReports()[elevatorId]);
    assertTrue(isOutOfService);
  }

  @Test
  public void testTakeAllElevatorsOutOfService() {
    testBuilding.startElevatorSystem();
    testBuilding.takeAllElevatorsOutOfService();
    for (ElevatorReport report : testBuilding.getElevatorSystemStatus().getElevatorReports()) {
      assertTrue(report.isOutOfService());
    }
  }

  @Test
  public void testTriggerElevatorStepStopping() {
    testBuilding.triggerElevatorStep();
    // empty step/return as not started
    assertEquals(ElevatorSystemStatus.outOfService, testBuilding.getElevatorStatus());
  }

  @Test
  public void testTriggerElevatorStep() {
    testBuilding.startElevatorSystem();
    // trigger 6 times, as no requests, all elevators should be at floor 1
    for (int i = 0; i < 6; i++) {
      testBuilding.triggerElevatorStep();
    }
    // check all elevators are at floor 1
    for (ElevatorReport report : testBuilding.getElevatorSystemStatus().getElevatorReports()) {
      assertEquals(1, report.getCurrentFloor());
    }
  }

  @Test
  public void testClearRequests() {
    testBuilding.startElevatorSystem();
    testBuilding.addRequest(new Request(1, 2));
    testBuilding.addRequest(new Request(2, 1));
    testBuilding.clearRequests();
    assertTrue(testBuilding.getElevatorSystemStatus().getUpRequests().isEmpty());
    assertTrue(testBuilding.getElevatorSystemStatus().getDownRequests().isEmpty());
  }

  @Test
  public void testUpRequestsComplete() {
    testBuilding.startElevatorSystem();
    testBuilding.addRequest(new Request(0, 2));
    // step 6 times, should be at 3rd floor
    // count down 3s, close dorr for 1s,  then up 2 floor
    for (int i = 0; i < 6; i++) {
      testBuilding.triggerElevatorStep();
    }
    // should be the first model.elevator that completes the request
    assertEquals(2,
        testBuilding.getElevatorSystemStatus().getElevatorReports()[0].getCurrentFloor());
  }

  @Test
  public void testDownRequestsComplete() {
    testBuilding.startElevatorSystem();
    testBuilding.addRequest(new Request(10, 8));
    // step 5 countdown + 1 wait + 10 levels up
    // step 5 countdown + 1 wait + 3 for open door + 2 levels down
    // 5 + 1 + 10 + 5 + 1 + 3 + 2 = 27
    for (int i = 0; i < 27; i++) {
      testBuilding.triggerElevatorStep();
    }
    // should be the first model.elevator that completes the request
    assertEquals(8,
        testBuilding.getElevatorSystemStatus().getElevatorReports()[0].getCurrentFloor());
    assertTrue(testBuilding.getElevatorSystemStatus().getElevatorReports()[0].isDoorClosed());
    // one more step, door should be open
    testBuilding.triggerElevatorStep();
    assertFalse(testBuilding.getElevatorSystemStatus().getElevatorReports()[0].isDoorClosed());
  }

  @Test
  public void testPrintElevatorStatuses() {
    testBuilding.startElevatorSystem();
    testBuilding.printElevatorStatuses();
    String output = "Current Elevator Statuses:\n"
        + "Elevator 0: Waiting[Floor 0, Time 5]\n"
        + "Elevator 1: Waiting[Floor 0, Time 5]\n"
        + "Elevator 2: Waiting[Floor 0, Time 5]\n"
        + "Elevator 3: Waiting[Floor 0, Time 5]\n"
        + "Elevator 4: Waiting[Floor 0, Time 5]\n"
        + "Elevator 5: Waiting[Floor 0, Time 5]\n"
        + "Elevator 6: Waiting[Floor 0, Time 5]\n"
        + "Elevator 7: Waiting[Floor 0, Time 5]\n"
        + "Up Requests: None\n"
        + "Down Requests: None\n";
    assertEquals(output, outContent.toString());
  }

  @Test
  public void testPrintElevatorStatusesWithStep() {
    testBuilding.startElevatorSystem();
    testBuilding.addRequest(new Request(1, 2));
    // step 6 times, should be at floor 2
    for (int i = 0; i < 6; i++) {
      testBuilding.triggerElevatorStep();
    }
    testBuilding.printElevatorStatuses();
    String output = "Current Elevator Statuses:\n"
        + "Elevator 0: [2|^|C  ]< -- --  2 -- -- -- -- -- -- -- -->\n"
        + "Elevator 1: [1|^|C  ]< -- -- -- -- -- -- -- -- -- -- -->\n"
        + "Elevator 2: [1|^|C  ]< -- -- -- -- -- -- -- -- -- -- -->\n"
        + "Elevator 3: [1|^|C  ]< -- -- -- -- -- -- -- -- -- -- -->\n"
        + "Elevator 4: [1|^|C  ]< -- -- -- -- -- -- -- -- -- -- -->\n"
        + "Elevator 5: [1|^|C  ]< -- -- -- -- -- -- -- -- -- -- -->\n"
        + "Elevator 6: [1|^|C  ]< -- -- -- -- -- -- -- -- -- -- -->\n"
        + "Elevator 7: [1|^|C  ]< -- -- -- -- -- -- -- -- -- -- -->\n"
        + "Up Requests: None\n"
        + "Down Requests: None\n";
    assertEquals(output, outContent.toString());
  }

  @Test
  public void testElevatorReturnToGroundWhenStopping() {
    Building newBuilding = new Building(3, 2, 3);
    newBuilding.startElevatorSystem();
    // countdown 5s
    for (int i = 0; i < 6; i++) {
      newBuilding.triggerElevatorStep();
    }
    newBuilding.stopElevatorSystem();
    // enough steps so model.elevator can return to ground
    for (int i = 0; i < 5; i++) {
      newBuilding.triggerElevatorStep();
    }
    // check all elevators are at floor 0 and out of service
    for (ElevatorReport report : newBuilding.getElevatorSystemStatus().getElevatorReports()) {
      assertEquals(0, report.getCurrentFloor());
      assertTrue(report.isOutOfService());
    }
  }

  @Test
  public void testElevatorReturnToGroundWhenStoppingWithRequests() {
    Building newBuilding = new Building(3, 2, 3);
    newBuilding.startElevatorSystem();
    newBuilding.addRequest(new Request(1, 2));
    // countdown 5s
    for (int i = 0; i < 6; i++) {
      newBuilding.triggerElevatorStep();
    }
    newBuilding.stopElevatorSystem();
    // enough steps so model.elevator can return to ground
    for (int i = 0; i < 10; i++) {
      newBuilding.triggerElevatorStep();
    }
    // check all elevators are at floor 0 and out of service
    for (ElevatorReport report : newBuilding.getElevatorSystemStatus().getElevatorReports()) {
      assertEquals(0, report.getCurrentFloor());
      assertTrue(report.isOutOfService());
    }
  }

  @Test
  public void testSingleElevatorRequest() {
    Building newBuilding = new Building(3, 1, 3);
    newBuilding.startElevatorSystem();
    newBuilding.addRequest(new Request(1, 2));
    // countdown 5s
    for (int i = 0; i < 6; i++) {
      newBuilding.triggerElevatorStep();
    }
    // should be at floor 2
    assertEquals(
        2, newBuilding.getElevatorSystemStatus().getElevatorReports()[0].getCurrentFloor());
  }

  @Test
  public void testTakeOutOfServiceWithSingleElevatorDoorOpened() {
    Building newBuilding = new Building(3, 2, 3);
    newBuilding.startElevatorSystem();
    newBuilding.addRequest(new Request(1, 2));
    for (int i = 0; i < 7; i++) {
      newBuilding.triggerElevatorStep();
    }
    newBuilding.takeAllElevatorsOutOfService();
    // should be at floor 2
    assertEquals(
        2, newBuilding.getElevatorSystemStatus().getElevatorReports()[0].getCurrentFloor());
    // count more steps
    for (int i = 0; i < 5; i++) {
      newBuilding.triggerElevatorStep();
    }
    // should be all out of service and on ground floor
    for (ElevatorReport report : newBuilding.getElevatorSystemStatus().getElevatorReports()) {
      assertEquals(0, report.getCurrentFloor());
      assertTrue(report.isOutOfService());
    }

  }
}
