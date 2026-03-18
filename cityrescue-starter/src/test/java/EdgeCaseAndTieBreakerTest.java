package cityrescue;
import cityrescue.CityRescue;
import cityrescue.enums.*;
import cityrescue.exceptions.*;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.beans.Transient;


public class EdgeCaseAndTieBreakerTest {
    @Test
    // tests that invalid grid sizes are rejected
    public void testInvalidGridSize() {
        CityRescue sim = new CityRescueImpl();
        try{
            sim.initialise(0, 5);
            fail("Expected InvalidGridException");
        } catch(InvalidGridException e) {

        }
    }
    @Test
    //tests that a station cannot be added on a blocked location
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
    //tests that the nearest suitable unit is chosen
    public void testNearestUnitChosen() throws Exception {
        CityRescue sim = new CityRescueImpl();
        sim.initialise(10, 10);
        int s1 = sim.addStation("A", 0, 0);
        int s2 = sim.addStation("B", 8, 8);

        int u1 = sim.addUnit(s1, UnitType.FIRE_ENGINE);
        int u2 = sim.addUnit(s2, UnitType.FIRE_ENGINE);

        int incidentId = sim.reportIncident(IncidentType.FIRE, 3, 1, 0);

        sim.dispatch();
        String incident = sim.viewIncident(incidentId);

        assertTrue(incident.contains("UNIT=" + u1));
        assertFalse(incident.contains("UNIT=" + u2));
    }
    @Test
    //tests that a station name needs a name
    public void testBlankStationName() throws Exception {
        CityRescue sim = new CityRescueImpl();
        sim.initialise(5, 5);
        try {
            sim.addStation("", 1, 1);
            fail("Expected InvalidNameException"); 
        } catch (InvalidNameException e) {

            }
        }
    @Test
    // tests that a unit moves closer to an incident after one tick 
    public void testTickSequencing() throws Exception {
     CityRescue sim = new CityRescueImpl();
    sim.initialise(10, 10);
    int stationId = sim.addStation("station1", 0, 0);
    int unitId = sim.addUnit(stationId, UnitType.FIRE_ENGINE);
    int incidentId = sim.reportIncident(IncidentType.FIRE, 3, 2, 0);

    sim.dispatch();
    sim.tick();

    String unit = sim.viewUnit(unitId);
    String incident = sim.viewIncident(incidentId);

    assertTrue(unit.contains("LOC=(1,0)") || unit.contains("LOC=(2,0)"));
    assertTrue(incident.contains("UNIT=" + unitId));
    
    }
}
