package cityrescue;

import cityrescue.Classes.*;
import cityrescue.enums.*;
import cityrescue.exceptions.*;
/**
 * This class implements the CityRescue simulation system. It stores the city map, stations, units, incidents as arrays.
 * It also has ids assigned incrementally as new objects are added.
 */
public class CityRescueImpl implements CityRescue {
    //Constants
    private static final int MAX_STATIONS = 20;
    private static final int MAX_UNITS = 50;
    private static final int MAX_INCIDENTS = 200;
    
    //Variables
    private cityMap map;
    private int tick;

    //Storage Arrays
    private Station[] stations = new Station[MAX_STATIONS];
    private Unit[] units = new Unit[MAX_UNITS];
    private Incident[] incidents = new Incident[MAX_INCIDENTS];

    // Next ID Counters
    private int nextStationId = 1;
    private int nextUnitId = 1;
    private int nextIncidentId = 1;

    //Active Counts
    private int stationCount = 0;
    private int unitCount = 0;
    private int incidentCount = 0;

    //__INIT__
    /**
     * Initialises the CityRescue simulation with a new map of the given size. 
     * It resets the simulation state, including the tick counter, units , incidents, all stored stations and the id counters. 
     * @param width the width of the city map
     * @param height the height of the city map
     * @throws InvalidGridException if the width or height is not positive 
    */
    @Override
    public void initialise(int width, int height) throws InvalidGridException{
        if (width <= 0 || height <= 0) throw new InvalidGridException("Width and height must be positive");
        map = new cityMap(width, height);
        tick = 0;
        stations = new Station[MAX_STATIONS];
        units = new Unit[MAX_UNITS];
        incidents = new Incident[MAX_INCIDENTS];
        nextStationId = 1;
        nextUnitId = 1;
        nextIncidentId = 1;
        stationCount = 0;
        unitCount = 0;
        incidentCount = 0;
    }
    /**
     * Returns the size of the city grid.
     * @return an array containing the width and height of the map. 
     */
    @Override
    public int[] getGridSize(){
        return new int[]{map.getWidth(), map.getHeight()};
    }

    /**
     * adds an obstacle at the given map location. 
     * the obstacles block movement through the given cell.
     * @param x the coordinate of the obstacle on the x axis 
     * @param y the coordinate of the obstacle on the y axis. 
     * @throws InvalidLocationException if the location is outside the map or unavailable to be used as a map location. 
     */
    @Override
    public void addObstacle(int x, int y) throws InvalidLocationException{
        map.addObstacle(x, y);
    }
    /**
     * rewmoves an abstacle from the map at a given location. 
     * @param x the coorinate of the obstacle on the x axis that will be removed
     * @param y the coorinate of the obstacle on the y axis that will be removed
     * @throws InvalidLocationException if the location is outside the map or there is not a removeable obstacle at the location. 
     */
    @Override
    public void removeObstacle(int x, int y) throws InvalidLocationException{
        map.removeObstacle(x, y);
    }
    /**
     * Adds a station to the simulation with an id and its stored i nthe first available slot in the station array. The station must be on the map, chosen coordinate not blocked 
     * and the name cannot be blank. 
     * @param name the name of the station 
     * @param x the cooridnate of the station
     * @param y the coordinate of the station 
     * @return the id for the new station 
     * @throws InvalidNameException if the stations name is blank. 
     * @throws InvalidLocationException if the location is outsdie the map
     * @throws CapacityExceededException if the max stations is reached

     */
    @Override
    public int addStation(String name, int x, int y) throws InvalidNameException, InvalidLocationException {
        if (name == null || name.trim().isEmpty()) throw new InvalidNameException("Name cannot be blank");
        if (!map.isInBounds(x, y)) throw new InvalidLocationException("Out of bounds");
        if (map.isBlocked(x, y)) throw new InvalidLocationException("Location is blocked");
        if (stationCount >= MAX_STATIONS) throw new CapacityExceededException("Maximum stations reached");

        int id = nextStationId++;
        Station s = new Station(id, name, x, y, 5);
        // Find first null slot
        for (int i = 0; i < MAX_STATIONS; i++) {
            if (stations[i] == null) {
                stations[i] = s;
                stationCount++;
                return id;
            }
        }
        throw new CapacityExceededException("No free station slot"); // should not happen
    }

    /**
     * removes a station from the simulation. 
     * its only able to be removed if no units currently have that station set as their home station. 
     * @param stationId the id of the station to remove
     * @throws IDNotRecognisedException if the station id deos nto match any station. 
     * @throws IllegalStateException if their are still units assigned to a station. 
     */
    @Override
    public void removeStation(int stationId) throws IDNotRecognisedException, IllegalStateException {
        Station s = findStationById(stationId);
        if (s == null) throw new IDNotRecognisedException("Station not found");
        // Check if any unit has this station as home
        for (Unit u : units) {
            if (u != null && u.getHomeStationId() == stationId) {
                throw new IllegalStateException("Station still has units");
            }
        }
        // Remove
        for (int i = 0; i < MAX_STATIONS; i++) {
            if (stations[i] != null && stations[i].getId() == stationId) {
                stations[i] = null;
                stationCount--;
                return;
            }
        }
    }
    /**
     * sets the maximum unit capacity for a station 
     * new capacity must be positive and larger than the current units assigned to that station. 
     * @param stationID the id of the station which capacity will change
     * @param maxUnits the new maximum number of units allowed at the station
     * @throws IDNotrecognisedException if the station ID does nto match any station
     * @throws InvalidCapacityException if the new station capacity is not positive or smaller than number of units assigned to it. 
     */
    @Override
    public void setStationCapacity(int stationId, int maxUnits) throws IDNotRecognisedException, InvalidCapacityException {
        Station s = findStationById(stationId);
        if (s == null) throw new IDNotRecognisedException("Station not found");
        if (maxUnits <= 0) throw new InvalidCapacityException("Capacity Must be Positive");
        //Count units at this station
        int current = 0;
        for (Unit u : units) {
            if (u != null && u.getHomeStationId() == stationId) current++;

        }
        if (maxUnits < current) throw new InvalidCapacityException("New capacity too low for current units");
        s.setCapacity(maxUnits);
    }
    /**
     * returns the ids of all current stations. 
     * The returned array has the id of evervy station in the simulation sorted in ascending order. 
     * @return an array of station ids in ascending order
     */
    @Override
    public int[] getStationIds() {
        int[] ids = new int[stationCount];
        int idx = 0;
        for (Station s : stations) {
            if (s != null) ids[idx++] = s.getId();
        }
        for (int i = 0; i < idx-1; i++) {
            for (int j = i+1; j < idx; j++){
                if (ids[i] > ids[j]){
                    int tmp = ids[i];
                    ids[i] = ids[j];
                    ids[j] = tmp;
                }
            }
        }
        return ids;
    }
    
    /**
     * adds a new emergency unit to a station
     * The station must have enough capacity spare to hold another unit, then a new unit id is assignedand the new unit subclass created depends on what 
     * the supplied unti type was. 
     * @param stationId the id of the station the unit will belong to.
     * @param type the type of unit to create
     * @return the id assigned to the new unit
     * @throws IDNotRecognisedException if the station id does nto match any exsisting station 
     * @throws InvalidUnitExceptino if the unit type is invalid
     * @throws IllegalStateException if te station is already at max capacity
     * @throws CapacityExceededException if the max number of units in the simulation has been reached. 
     */
    @Override
    public int addUnit(int stationId, UnitType type) throws IDNotRecognisedException, InvalidUnitException, IllegalStateException {
        Station s = findStationById(stationId);
        if (s == null) throw new IDNotRecognisedException("Station not found");
        //Check station capacity
        int currentAtStation = 0;
        for (Unit u : units) {
            if (u != null && u.getHomeStationId() == stationId) currentAtStation++;
        }
        if (currentAtStation >= s.getCapacity()) throw new IllegalStateException("Station at capacity");
        if (unitCount >= MAX_UNITS) throw new CapacityExceededException("Maximum units reached");

        int id = nextUnitId++;
        Unit u;
        switch (type) {
            case AMBULANCE: u = new Ambulance(id, stationId, s.getX(), s.getY());break;
            case FIRE_ENGINE: u = new FireEngine(id, stationId, s.getX(), s.getY());break;
            case POLICE_CAR: u = new PoliceCar(id, stationId, s.getX(), s.getY());break;
            default: throw new InvalidUnitException("Unknown Unit Type");
        }
        for (int i=0; i<MAX_UNITS; i++) {
            if (units[i] == null) {
                units[i] = u;
                unitCount++;
                return id;
            }
        }
        throw new CapacityExceededException("No free unit slot");
    }

    @Override
    public void decommissionUnit(int unitId) throws IDNotRecognisedException, IllegalStateException {
        Unit u = findUnitById(unitId);
        if (u == null) throw new IDNotRecognisedException("Unit not found");
        if (u.getStatus() == UnitStatus.EN_ROUTE || u.getStatus() == UnitStatus.AT_SCENE) {
            throw new IllegalStateException("Unit is not idle");
        }
        for (int i = 0; i < MAX_UNITS; i++) {
            if (units[i] != null && units[i].getId() == unitId) {
                units[i] = null;
                unitCount--;
                return;
            }
        }
    }
    
    @Override
    public void transferUnit(int unitId, int newStationId) throws IDNotRecognisedException, IllegalStateException {
        Unit u = findUnitById(unitId);
        Station newS = findStationById(newStationId);
        if (u == null) throw new IDNotRecognisedException("Unit not found");
        if (newS == null) throw new IDNotRecognisedException("New station not found");
        if (u.getStatus() != UnitStatus.IDLE) throw new IllegalStateException("Unit is not idle");
        // Check capacity of new station
        int currentAtNew = 0;
        for (Unit unit : units) {
            if (unit != null && unit.getHomeStationId() == newStationId) currentAtNew++;
        }
        if (currentAtNew >= newS.getCapacity()) throw new IllegalStateException("New station at capacity");
        // Update home station and location
        u.setHomeStationId(newStationId);
        u.setLocation(newS.getX(), newS.getY());
    }

    @Override
    public void setUnitOutOfService(int unitId, boolean outOfService) throws IDNotRecognisedException, IllegalStateException {
        Unit u = findUnitById(unitId);
        if (u == null) throw new IDNotRecognisedException("Unit not found");
        if (outOfService) {
            if (u.getStatus() != UnitStatus.IDLE) throw new IllegalStateException("Unit must be idle to go out of service");
            u.setStatus(UnitStatus.OUT_OF_SERVICE);
        } else {
            if (u.getStatus() != UnitStatus.OUT_OF_SERVICE) throw new IllegalStateException("Unit is not out of service");
            u.setStatus(UnitStatus.IDLE);
        }
    }

    @Override
    public int[] getUnitIds() {
        int[] ids = new int[unitCount];
        int idx = 0;
        for (Unit u : units) {
            if (u != null) ids[idx++] = u.getId();
        }
        // sort ascending
        for (int i = 0; i < idx-1; i++) {
            for (int j = i+1; j < idx; j++) {
                if (ids[i] > ids[j]) {
                    int tmp = ids[i];
                    ids[i] = ids[j];
                    ids[j] = tmp;
                }
            }
        }
        return ids;
    }

    @Override
    public String viewUnit(int unitId) throws IDNotRecognisedException {
        Unit u = findUnitById(unitId);
        if (u == null) throw new IDNotRecognisedException("Unit not found");
        return formatUnit(u);
    }
    
    //Incidents
    public int reportIncident(IncidentType type, int severity, int x, int y) throws InvalidSeverityException, InvalidLocationException {
        if (severity < 1 || severity > 5) throw new InvalidSeverityException("Severity must be 1-5");
        if (!map.isInBounds(x, y)) throw new InvalidLocationException("Out of bounds");
        if (map.isBlocked(x, y)) throw new InvalidLocationException("Location is blocked");
        if (incidentCount >= MAX_INCIDENTS) throw new CapacityExceededException("Maximum incidents reached");

        int id = nextIncidentId++;
        Incident inc = new Incident(id, type, severity, x, y);
        for (int i = 0; i < MAX_INCIDENTS; i++) {
            if (incidents[i] == null) {
                incidents[i] = inc;
                incidentCount++;
                return id;
            }
        }
        throw new CapacityExceededException("No free incident slot");
    }

    @Override
    public void cancelIncident(int incidentId) throws IDNotRecognisedException, IllegalStateException {
        Incident inc = findIncidentById(incidentId);
        if (inc == null) throw new IDNotRecognisedException("Incident not found");
        if (inc.getStatus() == IncidentStatus.RESOLVED || inc.getStatus() == IncidentStatus.CANCELLED) {
            throw new IllegalStateException("Cannot cancel resolved or cancelled incident");
        }
        if (inc.getStatus() == IncidentStatus.DISPATCHED) {
            // Release the unit
            int unitId = inc.getUnitId();
            Unit u = findUnitById(unitId);
            if (u != null) {
                u.setStatus(UnitStatus.IDLE);
                u.setIncidentId(-1);
                // unit stays at current location
            }
        }
        inc.setStatus(IncidentStatus.CANCELLED);
        inc.setUnitId(-1);
    }

    @Override
    public void escalateIncident(int incidentId, int newSeverity) throws IDNotRecognisedException, InvalidSeverityException, IllegalStateException {
        Incident inc = findIncidentById(incidentId);
        if (inc == null) throw new IDNotRecognisedException("Incident not found");
        if (newSeverity < 1 || newSeverity > 5) throw new InvalidSeverityException("Severity must be 1-5");
        if (inc.getStatus() == IncidentStatus.RESOLVED || inc.getStatus() == IncidentStatus.CANCELLED) {
            throw new IllegalStateException("Cannot escalate resolved or cancelled incident");
        }
        inc.setSeverity(newSeverity);
    }

    @Override
    public int[] getIncidentIds() {
        int[] ids = new int[incidentCount];
        int idx = 0;
        for (Incident inc : incidents) {
            if (inc != null) ids[idx++] = inc.getId();
        }
        // sort
        for (int i = 0; i < idx-1; i++) {
            for (int j = i+1; j < idx; j++) {
                if (ids[i] > ids[j]) {
                    int tmp = ids[i];
                    ids[i] = ids[j];
                    ids[j] = tmp;
                }
            }
        }
        return ids;
    }

    @Override
    public String viewIncident(int incidentId) throws IDNotRecognisedException {
        Incident inc = findIncidentById(incidentId);
        if (inc == null) throw new IDNotRecognisedException("Incident not found");
        return formatIncident(inc);
    }

    //Sim
    @Override
    public void dispatch() {
        // Process incidents in ascending ID order
        int[] incIds = getIncidentIds(); // already sorted
        for (int id : incIds) {
            Incident inc = findIncidentById(id);
            if (inc.getStatus() != IncidentStatus.REPORTED) continue;
            // Find best eligible unit
            Unit best = null;
            int bestDist = Integer.MAX_VALUE;
            int bestUnitId = Integer.MAX_VALUE;
            int bestStationId = Integer.MAX_VALUE;
            for (Unit u : units) {
                if (u == null) continue;
                if (u.getStatus() == UnitStatus.OUT_OF_SERVICE || u.getStatus() != UnitStatus.IDLE) continue;
                if (!u.canHandle(inc.getType())) continue;
                int dist = Math.abs(u.getX() - inc.getX()) + Math.abs(u.getY() - inc.getY());
                if (best == null ||
                    dist < bestDist ||
                    (dist == bestDist && u.getId() < bestUnitId) ||
                    (dist == bestDist && u.getId() == bestUnitId && u.getHomeStationId() < bestStationId)) {
                    best = u;
                    bestDist = dist;
                    bestUnitId = u.getId();
                    bestStationId = u.getHomeStationId();
                }
            }
            if (best != null) {
                // Assign
                inc.setStatus(IncidentStatus.DISPATCHED);
                inc.setUnitId(best.getId());
                best.setStatus(UnitStatus.EN_ROUTE);
                best.setIncidentId(inc.getId());
                best.setTarget(inc.getX(), inc.getY());
            }
        }
    }

    @Override
    public void tick() {
        tick++;
        //Move EN_ROUTE units in ascending unitID order
        Unit[] sortedUnits = getUnitsSortedById();
        for (Unit u : sortedUnits) {
            if (u == null) continue;
            if (u.getStatus() == UnitStatus.EN_ROUTE) {
                moveUnit(u);
            }
        }

        //Mark units that reached target
        for (Unit u : sortedUnits) {
            if (u == null) continue;
            if (u.getStatus() == UnitStatus.EN_ROUTE && u.getX() == u.getTargetX() && u.getY() == u.getTargetY()) {
                // Arrived
                Incident inc = findIncidentById(u.getIncidentId());
                if (inc != null && inc.getStatus() == IncidentStatus.DISPATCHED) {
                    inc.setStatus(IncidentStatus.IN_PROGRESS);
                    u.setStatus(UnitStatus.AT_SCENE);
                    u.setWorkRemaining(u.getTicksToResolve(inc.getSeverity()));
                } else {
                    // Should not happen, but if incident was cancelled while en route, unit becomes idle
                    u.setStatus(UnitStatus.IDLE);
                    u.setIncidentId(-1);
                }
            }
        }
        for (Unit u : sortedUnits) {
            if (u == null) continue;
            if (u.getStatus() == UnitStatus.AT_SCENE) {
                u.setWorkRemaining(u.getWorkRemaining() - 1);
            }
        }

        //Resolve completed incidents in ascending incidentId order
        int[] incIds = getIncidentIds();
        for (int id : incIds) {
            Incident inc = findIncidentById(id);
            if (inc.getStatus() == IncidentStatus.IN_PROGRESS) {
                Unit u = findUnitById(inc.getUnitId());
                if (u != null && u.getWorkRemaining() == 0) {
                    inc.setStatus(IncidentStatus.RESOLVED);
                    u.setStatus(UnitStatus.IDLE);
                    u.setIncidentId(-1);
                    // unit stays at location
                }
            }
        }
    }
    @Override
    public String getStatus() {
        StringBuilder sb = new StringBuilder();
        sb.append("TICK=").append(tick).append("\n");
        sb.append("STATIONS=").append(stationCount).append(" UNITS=").append(unitCount)
          .append(" INCIDENTS=").append(incidentCount).append(" OBSTACLES=").append(countObstacles()).append("\n");

        sb.append("INCIDENTS\n");
        int[] incIds = getIncidentIds();
        for (int id : incIds) {
            Incident inc = findIncidentById(id);
            sb.append(formatIncident(inc)).append("\n");
        }

        sb.append("UNITS\n");
        int[] unitIds = getUnitIds();
        for (int id : unitIds) {
            Unit u = findUnitById(id);
            sb.append(formatUnit(u)).append("\n");
        }
        // Remove trailing newline if needed? The spec shows no extra blank line at end?
        return sb.toString();
    }
    //Helper Methods
    private Station findStationById(int id) {
        for (Station s : stations) {
            if (s != null && s.getId() == id) return s;
        }
        return null;
    }

    private Unit findUnitById(int id) {
        for (Unit u : units) {
            if (u != null && u.getId() == id) return u;
        }
        return null;
    }

    private Incident findIncidentById(int id) {
        for (Incident i : incidents) {
            if (i != null && i.getId() == id) return i;
        }
        return null;
    }

    private Unit[] getUnitsSortedById() {
        Unit[] active = new Unit[unitCount];
        int idx = 0;
        for (Unit u : units) {
            if (u != null) active[idx++] = u;
        }
        // simple sort
        for (int i = 0; i < idx-1; i++) {
            for (int j = i+1; j < idx; j++) {
                if (active[i].getId() > active[j].getId()) {
                    Unit tmp = active[i];
                    active[i] = active[j];
                    active[j] = tmp;
                }
            }
        }
        return active;
    }

    private void moveUnit(Unit u) {
        int x = u.getX();
        int y = u.getY();
        int tx = u.getTargetX();
        int ty = u.getTargetY();
        int currentDist = Math.abs(x - tx) + Math.abs(y - ty);

        // Directions in order N, E, S, W
        int[] dx = {0, 1, 0, -1};
        int[] dy = {-1, 0, 1, 0};
        String[] dirs = {"N", "E", "S", "W"};

        // First, try to find a move that reduces distance
        for (int d = 0; d < 4; d++) {
            int nx = x + dx[d];
            int ny = y + dy[d];
            if (map.isInBounds(nx, ny) && !map.isBlocked(nx, ny)) {
                int newDist = Math.abs(nx - tx) + Math.abs(ny - ty);
                if (newDist < currentDist) {
                    u.setLocation(nx, ny);
                    return;
                }
            }
        }
        // If none reduce, take first legal move in order
        for (int d = 0; d < 4; d++) {
            int nx = x + dx[d];
            int ny = y + dy[d];
            if (map.isInBounds(nx, ny) && !map.isBlocked(nx, ny)) {
                u.setLocation(nx, ny);
                return;
            }
        }
        // No legal move: stay put
    }

    private int countObstacles() {
        int count = 0;
        for (int x = 0; x < map.getWidth(); x++) {
            for (int y = 0; y < map.getHeight(); y++) {
                if (map.isBlocked(x, y)) count++;
            }
        }
        return count;
    }

    private String formatUnit(Unit u) {
        // U#2 TYPE=FIRE_ENGINE HOME=2 LOC=(3,1) STATUS=AT_SCENE INCIDENT=1 WORK=2
        StringBuilder sb = new StringBuilder();
        sb.append("U#").append(u.getId()).append(" TYPE=").append(u.getType())
          .append(" HOME=").append(u.getHomeStationId())
          .append(" LOC=(").append(u.getX()).append(",").append(u.getY()).append(")")
          .append(" STATUS=").append(u.getStatus());
        if (u.getIncidentId() != -1) {
            sb.append(" INCIDENT=").append(u.getIncidentId());
        } else {
            sb.append(" INCIDENT=-");
        }
        if (u.getStatus() == UnitStatus.AT_SCENE) {
            sb.append(" WORK=").append(u.getWorkRemaining());
        }
        return sb.toString();
    }

    private String formatIncident(Incident inc) {
        // I#1 TYPE=FIRE SEV=4 LOC=(3,1) STATUS=IN_PROGRESS UNIT=2
        StringBuilder sb = new StringBuilder();
        sb.append("I#").append(inc.getId()).append(" TYPE=").append(inc.getType())
          .append(" SEV=").append(inc.getSeverity())
          .append(" LOC=(").append(inc.getX()).append(",").append(inc.getY()).append(")")
          .append(" STATUS=").append(inc.getStatus());
        if (inc.getUnitId() != -1) {
            sb.append(" UNIT=").append(inc.getUnitId());
        } else {
            sb.append(" UNIT=-");
        }
        return sb.toString();
    }
}


