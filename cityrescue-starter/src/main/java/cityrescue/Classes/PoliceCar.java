package cityrescue.Classes;

import cityrescue.enums.*;

public class PoliceCar extends Unit {
    public PoliceCar(int id, int homeStationId, int startX, int startY) {
        super(id, UnitType.POLICE_CAR, homeStationId, startX, startY);
    }
    @Override
    public boolean canHandle(IncidentType incidentType){
        return incidentType == IncidentType.CRIME;
    }
    @Override
    public int getTicksToResolve(int severity) {
        return 3;
    }
}