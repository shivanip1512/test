package com.cannontech.multispeak.data.v4;

import com.cannontech.amr.meter.model.YukonMeter;
import com.cannontech.common.pao.PaoType;

/**
 * Factory class to generate BillableDevices
 */
public class MeterReadFactory {

    /**
     * Factory method to create a billable device for the given category and
     * type
     * 
     * @param category - device category
     * @param type - device type
     * @return a new instance of the billable device type
     */
    private static MeterReadBase createMeterReadObject(PaoType paoType, String meterNumber) {
        MeterReadBase meterReadObject = null;

        switch (paoType) {

        case MCT210:
        case MCT213:
        case MCT240:
        case MCT248:
        case MCT250:
            meterReadObject = new MCT2XX();
            break;

        case MCT310:
        case MCT310CT:
        case MCT310ID:
        case MCT310IDL:
        case MCT310IL:
        case MCT310IM:
            meterReadObject = new MCT310();
            break;

        case MCT318:
        case MCT318L:
            meterReadObject = new MCT318();
            break;

        case MCT360:
            meterReadObject = new MCT360();
            break;

        case MCT370:
            meterReadObject = new MCT370();
            break;

        case MCT410CL:
        case MCT410IL:
        case MCT410FL:
        case MCT410GL:
        case MCT420CD:
        case MCT420CL:
        case MCT420FD:
        case MCT420FL:
            meterReadObject = new MCT410();
            break;

        case MCT430A:
        case MCT430S4:
        case MCT430SL:
        case MCT430A3:
            meterReadObject = new MCT430();
            break;

        case MCT470:
            meterReadObject = new MCT470();
            break;
        }

        if (meterReadObject != null) {
            meterReadObject.setMeterNumber(meterNumber);
        }

        return meterReadObject;
    }

    public static MeterReadBase createMeterReadObject(YukonMeter meter) {
        return createMeterReadObject(meter.getPaoIdentifier().getPaoType(), meter.getMeterNumber());
    }
}
