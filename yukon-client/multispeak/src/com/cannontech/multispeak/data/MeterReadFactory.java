package com.cannontech.multispeak.data;

import com.cannontech.amr.meter.model.Meter;
import com.cannontech.database.data.pao.DeviceTypes;

/**
 * Factory class to generate BillableDevices
 */
public class MeterReadFactory {

    /**
     * Factory method to create a billable device for the given category and
     * type
     * @param category - device category
     * @param type - device type
     * @return a new instance of the billable device type
     */
    public static MeterReadBase createMeterReadObject(int paoType, String meterNumber) {
        MeterReadBase meterReadObject = null;

        switch (paoType) {

        case DeviceTypes.MCT210:
        case DeviceTypes.MCT213:
        case DeviceTypes.MCT240:
        case DeviceTypes.MCT248:
        case DeviceTypes.MCT250:
            meterReadObject = new MCT2XX();
            break;
            
        case DeviceTypes.MCT310:
        case DeviceTypes.MCT310CT:
        case DeviceTypes.MCT310ID:
        case DeviceTypes.MCT310IDL:
        case DeviceTypes.MCT310IL:
        case DeviceTypes.MCT310IM:
            meterReadObject = new MCT310();
            break;

        case DeviceTypes.MCT318:
        case DeviceTypes.MCT318L:
            meterReadObject = new MCT318();
            break;

        case DeviceTypes.MCT360:
            meterReadObject = new MCT360();
            break;

        case DeviceTypes.MCT370:
            meterReadObject = new MCT370();
            break;

        case DeviceTypes.MCT410CL:
        case DeviceTypes.MCT410IL:
        case DeviceTypes.MCT410FL:
        case DeviceTypes.MCT410GL:
            meterReadObject = new MCT410();
            break;

        case DeviceTypes.MCT430A:
        case DeviceTypes.MCT430S4:
        case DeviceTypes.MCT430SN:
            meterReadObject = new MCT430();
            break;

        case DeviceTypes.MCT470:
            meterReadObject = new MCT470();
            break;
        }

        if (meterReadObject != null)
            meterReadObject.setMeterNumber(meterNumber);
        
        return meterReadObject;
    }
    
    public static MeterReadBase createMeterReadObject(Meter meter) {
        return createMeterReadObject(meter.getType(), meter.getMeterNumber());
    }
}
