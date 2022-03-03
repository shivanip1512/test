package com.cannontech.web.tools.points.model;

import org.apache.commons.lang3.BooleanUtils;

import com.cannontech.database.db.point.calculation.CalcBase;

public class CalcAnalogBase extends CalculationBase {

    private Boolean calculateQuality;

    public Boolean isCalculateQuality() {
        return calculateQuality;
    }

    public void setCalculateQuality(Boolean calculateQuality) {
        this.calculateQuality = calculateQuality;
    }

    public void buildDBPersistent(CalcBase calcBase) {
        super.buildDBPersistent(calcBase);
        if (isCalculateQuality() != null) {
            calcBase.setCalculateQuality(BooleanUtils.isTrue(isCalculateQuality()) ? 'Y' : 'N');
        }
    }

    @Override
    public void buildModel(CalcBase calcBase) {
        super.buildModel(calcBase);
        setCalculateQuality(calcBase.getCalculateQuality() == 'Y' ? true : false);
    }
}
