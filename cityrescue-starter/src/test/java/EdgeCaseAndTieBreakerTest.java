package cityrescue;
import cityrescue.enums.*;
import cityrescue.exceptions.*;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;


public class EdgeCaseAndTieBreakerTest {
    @Test
    public void testInvlaidGridSize() {
        CityRescue sim = new CityRescueImpl();
        try{
            sim.initialise(0, 5);
            fail("Expected InvalidGridException");
        } catch(InvalidGridException e) {

        }
    }
    @Test
    public void testBlockedStationLocation() throws Exception {
        CityRescue sim = new CityRescueImpl();
        sim.initialise(5, 5);
        sim.addObstacle(2, 2);
        try {
            sim.addStation("Test", 2, 2);
            fail("Expected InvalidLocationException");
        } catch (InvalidLocationException e) {

        }
    }
    @Test
    public void testLowestIDWhenTieBreak() throws Exception {
        CityRescue sim = new CityRescueImpl();
        sim.initialise(10, 10);
        int s1 = sim.addStation("A", 0, 1);
        int s2 = sim.addStation("B", 2, 1);

        int u1 = sim.addUnit(s1, UnitType.FIRE_ENGINE);
        int u2 = sim.addUnit(s2, UnitType.FIRE_ENGINE);

        int incidentId = sim.reportIncident(IncidentType.FIRE, 3, 1, 1);

        sim.dispatch();
        String incident = sim.viewIncident(incidentId);

        assertTrue(incident.contains("Unit=" + u1));
        assertTrue(incident.contains("Unit=" + u2));
    }
}