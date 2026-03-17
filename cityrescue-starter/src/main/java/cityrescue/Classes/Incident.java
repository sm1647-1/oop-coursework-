package cityrescue.Classes;

import cityrescue.enums.*;

public class Incident {
    private final int id;
    private final IncidentType type;
    private int severity;
    private final int x, y;
    private IncidentStatus status;
    private int unitId; //set to -1 if no unit

    public Incident(int id, IncidentType type, int severity, int x, int y) {
        this.id = id;
        this.type = type; 
        this.severity = severity;
        this.x = x;
        this.y = y;
        this.status = IncidentStatus.REPORTED;
        this.unitId = -1;
    }

    public int getId() { return id; }
    public IncidentType getType() { return type; }
    public int getSeverity() { return severity; }
    public void setSeverity(int severity) { this.severity = severity; }
    public int getX() { return x; }
    public int getY() { return y; }
    public IncidentStatus getStatus() { return status; }
    public void setStatus(IncidentStatus status) { this.status = status; }
    public int getUnitId() { return unitId; }
    public void setUnitId(int unitId) { this.unitId = unitId; }
}
