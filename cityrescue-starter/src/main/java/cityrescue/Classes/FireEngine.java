package cityrescue.Classes;

import cityrescue.enums.*;

public class FireEngine extends Unit {
    public FireEngine(int id, int homeStationId, int startX, int startY) {
        super(id, UnitType.FIRE_ENGINE, homeStationId, startX, startY);
    }
    @Override
    public boolean canHandle(IncidentType incidentType){
        return incidentType == IncidentType.FIRE;
    }
    @Override
    public int getTicksToResolve(int severity) {
        return 4;
    }
}
