package com.cannontech.billing.device;

import com.cannontech.billing.device.base.BillableDevice;
import com.cannontech.database.data.pao.DeviceTypes;
import com.cannontech.database.data.pao.PAOGroups;

/**
 * Factory class to generate BillableDevices
 */
public class BillableDeviceFactory {

    /**
     * Factory method to create a billable device for the given category and
     * type
     * @param category - device category
     * @param type - device type
     * @return a new instance of the billable device type
     */
    public static BillableDevice createBillableDevice(String category, String type) {
        BillableDevice device = null;

        int intType = PAOGroups.getPAOType(category, type);

        switch (intType) {

        case DeviceTypes.MCT310:
        case DeviceTypes.MCT310CT:
        case DeviceTypes.MCT310ID:
        case DeviceTypes.MCT310IDL:
        case DeviceTypes.MCT310IL:
        case DeviceTypes.MCT310IM:
            device = new MCT310();
            break;

        case DeviceTypes.MCT318:
        case DeviceTypes.MCT318L:
            device = new MCT318();
            break;

        case DeviceTypes.MCT360:
            device = new MCT360();
            break;

        case DeviceTypes.MCT370:
            device = new MCT370();
            break;

        case DeviceTypes.MCT410CL:
        case DeviceTypes.MCT410IL:
        case DeviceTypes.MCT410FL:
        case DeviceTypes.MCT410GL:
            device = new MCT410();
            break;

        case DeviceTypes.MCT420CD:
        case DeviceTypes.MCT420CL:
        case DeviceTypes.MCT420FD:
        case DeviceTypes.MCT420FL:
            device = new MCT410();  // The 420 billable fields are the same as the 410, let's just use that object
            break;
            
        case DeviceTypes.MCT430A:
        case DeviceTypes.MCT430S4:
        case DeviceTypes.MCT430SL:
        case DeviceTypes.MCT430A3:
            device = new MCT430();
            break;

        case DeviceTypes.MCT470:
            device = new MCT470();
            break;

        case DeviceTypes.SENTINEL:
            device = new Sentinel();
            break;
        case DeviceTypes.FOCUS:
            device = new Focus();
            break;

        case DeviceTypes.RFN410FL:
        case DeviceTypes.RFN410FX:
        case DeviceTypes.RFN410FD:
        case DeviceTypes.RFN420FL:
        case DeviceTypes.RFN420FX:
        case DeviceTypes.RFN420FD:
        case DeviceTypes.RFN420CL:
        case DeviceTypes.RFN420CD:
        case DeviceTypes.RFN430A3:
        case DeviceTypes.RFN430KV:
        case DeviceTypes.RFWMETER:
            device = new RFN410();
            break;

        default:
            device = new GenericMCT();
            break;

        }

        return device;
    }

}
