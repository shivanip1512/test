package com.cannontech.web.capcontrol.models;

import java.util.ArrayList;
import java.util.List;

import com.cannontech.database.data.point.AccumulatorPoint;
import com.cannontech.database.data.point.AnalogPoint;
import com.cannontech.database.data.point.CalcStatusPoint;
import com.cannontech.database.data.point.CalculatedPoint;
import com.cannontech.database.data.point.PointBase;
import com.cannontech.database.data.point.ScalarPoint;
import com.cannontech.database.data.point.StatusPoint;
import com.cannontech.database.db.point.fdr.FDRTranslation;
import com.cannontech.web.editor.point.AlarmTableEntry;
import com.cannontech.web.editor.point.StaleData;

public class PointModel<T extends PointBase> {
    
    private T pointBase;
    private StaleData staleData;
    private List<AlarmTableEntry> alarmTableEntries;
    
    public PointModel() {
        this(null, null, new ArrayList<>());
    }

    public PointModel (T base, StaleData staleData, List<AlarmTableEntry> alarmTableEntries) {
        this.pointBase = base;
        this.staleData = staleData;
        this.alarmTableEntries = alarmTableEntries;
    }

    public T getPointBase() {
        return pointBase;
    }

    public void setPointBase(T pointBase) {
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
    
    public void finishSetup() {
        
        int id = getId();
        PointBase base = getPointBase();
        
        for (FDRTranslation fdrTranslation : base.getPointFDRList()) {
            fdrTranslation.setPointID(id);
        }
        
        if (base instanceof AnalogPoint) {
            AnalogPoint analogPoint = (AnalogPoint) base;
            
            analogPoint.getPointAnalog().setPointID(id);
            analogPoint.getPointAnalogControl().setPointID(id);
        }
        
        if (base instanceof StatusPoint) {
            StatusPoint statusPoint = (StatusPoint) base;
            
            statusPoint.getPointStatus().setPointID(id);
            statusPoint.getPointStatusControl().setPointID(id);
        }
        
        
        if (base instanceof AccumulatorPoint) {
            AccumulatorPoint accumulatorPoint = (AccumulatorPoint) base;
            
            accumulatorPoint.getPointAccumulator().setPointID(id);
        }
        
        if (base instanceof ScalarPoint) {
            ScalarPoint scalar = (ScalarPoint) base;
            
            scalar.getPointUnit().setPointID(id);
            scalar.getLimitOne().setPointID(id);
            scalar.getLimitTwo().setPointID(id);
            
        }
        
        if (base instanceof CalcStatusPoint) {
            CalcStatusPoint calcStatus = (CalcStatusPoint) base;
            calcStatus.getCalcBase().setPointID(id);
        }
        
        if (base instanceof CalculatedPoint) {
            CalculatedPoint calcPoint = (CalculatedPoint) base;
            calcPoint.getCalcBase().setPointID(id);
        }
    }
    
}
