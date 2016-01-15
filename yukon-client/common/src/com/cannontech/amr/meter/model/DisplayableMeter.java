package com.cannontech.amr.meter.model;

import com.cannontech.amr.meter.dao.impl.MeterDisplayFieldEnum;
import com.cannontech.common.pao.DisplayablePao;
import com.cannontech.common.pao.PaoIdentifier;

public class DisplayableMeter implements DisplayablePao {

    private YukonMeter meter;
    private MeterDisplayFieldEnum meterDisplayFieldEnum;

    @Override
    public String getName() {
        String formattedName = meterDisplayFieldEnum.getField(meter);
        if (formattedName == null) {
            String defaultName = "n/a";
            if (!meterDisplayFieldEnum.equals(MeterDisplayFieldEnum.DEVICE_NAME)) {
                return defaultName + " (" + meter.getName() + ")";
            }
            return defaultName;
        }
        return formattedName;
    }

    @Override
    public PaoIdentifier getPaoIdentifier() {
        return meter.getPaoIdentifier();
    }

    public DisplayableMeter(YukonMeter meter, MeterDisplayFieldEnum meterDisplayFieldEnum) {
        super();
        this.meter = meter;
        this.meterDisplayFieldEnum = meterDisplayFieldEnum;
    }

    public YukonMeter getMeter() {
        return meter;
    }
}