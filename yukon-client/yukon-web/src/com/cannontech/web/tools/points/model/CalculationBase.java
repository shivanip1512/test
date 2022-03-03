package com.cannontech.web.tools.points.model;

import com.cannontech.common.device.port.DBPersistentConverter;
import com.cannontech.database.db.point.calculation.CalcBase;

public class CalculationBase implements DBPersistentConverter<com.cannontech.database.db.point.calculation.CalcBase> {

    private CalcUpdateType updateType;
    private Integer periodicRate;

    public CalcUpdateType getUpdateType() {
        return updateType;
    }

    public void setUpdateType(CalcUpdateType updateType) {
        this.updateType = updateType;
    }

    public Integer getPeriodicRate() {
        return periodicRate;
    }

    public void setPeriodicRate(Integer periodicRate) {
        this.periodicRate = periodicRate;
    }

    @Override
    public void buildDBPersistent(CalcBase calcBase) {
        if (getUpdateType() != null) {
            calcBase.setUpdateType(getUpdateType().getCalcUpdateType());
            if (getPeriodicRate() != null
                    && (CalcUpdateType.getCalcUpdateType(calcBase.getUpdateType()) == CalcUpdateType.ON_TIMER || CalcUpdateType.getCalcUpdateType(calcBase.getUpdateType()) == CalcUpdateType.ON_TIMER_AND_CHANGE)) {
                calcBase.setPeriodicRate(getPeriodicRate());
            }
        }
    }

    @Override
    public void buildModel(CalcBase calcBase) {
        setUpdateType(CalcUpdateType.getCalcUpdateType(calcBase.getUpdateType()));
        setPeriodicRate(calcBase.getPeriodicRate());
    }
}
