package cityrescue.Classes;

import cityrescue.enums.*;

public class Ambulance extends Unit {
    public Ambulance(int id, int homeStationId, int startX, int startY) {
        super(id, UnitType.AMBULANCE, homeStationId, startX, startY);
    }
    @Override
    public boolean canHandle(IncidentType incidentType){
        return incidentType == IncidentType.MEDICAL;
    }
    @Override
    public int getTicksToResolve(int severity) {
        return 2;
    }
}
