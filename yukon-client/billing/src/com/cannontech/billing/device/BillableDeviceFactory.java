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
            device = new MCT410();
            break;

        case DeviceTypes.MCT430A:
        case DeviceTypes.MCT430S:
            device = new MCT430();
            break;

        case DeviceTypes.MCT470:
            device = new MCT470();
            break;

        default:
            device = new GenericMCT();
            break;

        }

        return device;
    }

}
