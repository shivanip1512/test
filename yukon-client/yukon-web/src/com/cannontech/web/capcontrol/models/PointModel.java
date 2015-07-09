package com.cannontech.web.capcontrol.models;

import java.util.List;

import com.cannontech.database.data.point.PointBase;
import com.cannontech.web.editor.point.AlarmTableEntry;
import com.cannontech.web.editor.point.StaleData;

public class PointModel {
    
    private PointBase pointBase;
    private StaleData staleData;
    private List<AlarmTableEntry> alarmTableEntries;

    public PointModel (PointBase base, StaleData staleData, List<AlarmTableEntry> alarmTableEntries) {
        this.pointBase = base;
        this.staleData = staleData;
        this.alarmTableEntries = alarmTableEntries;
    }

    public PointBase getPointBase() {
        return pointBase;
    }

    public void setPointBase(PointBase pointBase) {
        this.pointBase = pointBase;
    }

    public StaleData getStaleData() {
        return staleData;
    }

    public void setStaleData(StaleData staleData) {
        this.staleData = staleData;
    }

    public List<AlarmTableEntry> getAlarmTableEntries() {
        return alarmTableEntries;
    }

    public void setAlarmTableEntries(List<AlarmTableEntry> alarmTableEntries) {
        this.alarmTableEntries = alarmTableEntries;
    }
    
    
    public Integer getId() {
        return pointBase.getPoint().getPointID();
    }
    
}
