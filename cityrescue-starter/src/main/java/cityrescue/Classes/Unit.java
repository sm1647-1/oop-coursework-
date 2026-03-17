package cityrescue.Classes;

import cityrescue.enums.*;

public abstract class Unit {
    protected final int id;
    protected final UnitType type;
    protected int homeStationId;
    protected int x,y;
    protected UnitStatus status;
    protected int incidentId; // -1 if none
    protected int targetX, targetY; // For EN_ROUTE
    protected int workRemaining; // For AT_SCENE

    public Unit(int id, UnitType type, int homeStationId, int startX, int startY) {
        this.id = id;
        this.type = type; 
        this.homeStationId = homeStationId;
        this.x = startX;
        this.y = startY;
        this.status = UnitStatus.IDLE;
        this.incidentId = -1;
    }

    public abstract boolean canHandle(IncidentType incidentType);
    public abstract int getTicksToResolve(int severity); 

    public int getId() { return id; }
    public UnitType getType() { return type; }
    public int getHomeStationId() { return homeStationId; }
    public void setHomeStationId(int homeStationId) {this.homeStationId = homeStationId; }
    public int getX() { return x; }
    public int getY() { return y; }
    public void setLocation(int x, int y) { this.x = x; this.y = y;}
    public UnitStatus getStatus() { return status; }
    public void setStatus(UnitStatus status) { this.status = status; }
    public int getIncidentId() { return incidentId; }
    public void setIncidentId(int incidentId) {this.incidentId = incidentId;}
    public int getTargetX() { return targetX; }
    public int getTargetY() { return targetY; }
    public void setTarget(int x, int y) { this.targetX = x; this.targetY = y;}
    public int getWorkRemaining() { return workRemaining; }
    public void setWorkRemaining(int workRemaining) {this.workRemaining = workRemaining;}
}







