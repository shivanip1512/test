package com.cannontech.stars.xml.serialize;

public class StarsDeleteThermostatSchedule {
    private int _scheduleID;
    private boolean _has_scheduleID;

    public StarsDeleteThermostatSchedule() {
   
    }

    public void deleteScheduleID()
    {
        this._has_scheduleID= false;
    } 

    public int getScheduleID()
    {
        return this._scheduleID;
    }
    
    public boolean hasScheduleID()
    {
        return this._has_scheduleID;
    } 

    public void setScheduleID(int scheduleID)
    {
        this._scheduleID = scheduleID;
        this._has_scheduleID = true;
    }

}
