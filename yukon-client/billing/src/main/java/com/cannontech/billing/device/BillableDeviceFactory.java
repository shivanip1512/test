package com.cannontech.billing.device;

import com.cannontech.billing.device.base.BillableDevice;
import com.cannontech.common.pao.PaoType;
import com.cannontech.database.data.pao.DeviceTypes;

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

        int intType = PaoType.getPaoTypeId(type);

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
            
        case DeviceTypes.MCT440_2131B:
        case DeviceTypes.MCT440_2132B:
        case DeviceTypes.MCT440_2133B:
            device = new MCT440();
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
        case DeviceTypes.RFN420FRX:
        case DeviceTypes.RFN420FRD:
        case DeviceTypes.RFN410CL:
        case DeviceTypes.RFN420CL:
        case DeviceTypes.RFN420CD:
        case DeviceTypes.RFN430A3D:
        case DeviceTypes.RFN430A3K:
        case DeviceTypes.RFN430A3R:
        case DeviceTypes.RFN430A3T:
        case DeviceTypes.RFN430KV:
//        case DeviceTypes.RFN440_2131T:
        case DeviceTypes.RFN440_2131TD:
//        case DeviceTypes.RFN440_2132T:
        case DeviceTypes.RFN440_2132TD:
//        case DeviceTypes.RFN440_2133T:
        case DeviceTypes.RFN440_2133TD:
        case DeviceTypes.RFWMETER:
        case DeviceTypes.RFW201:
        case DeviceTypes.RFG201:
        case DeviceTypes.RFG301:
        case DeviceTypes.RFN430SL0:
        case DeviceTypes.RFN430SL1:
        case DeviceTypes.RFN430SL2:
        case DeviceTypes.RFN430SL3:
        case DeviceTypes.RFN430SL4:
        case DeviceTypes.RFN510FL:
        case DeviceTypes.RFN520FAX:
        case DeviceTypes.RFN520FRX:
        case DeviceTypes.RFN520FAXD:
        case DeviceTypes.RFN520FRXD:
        case DeviceTypes.RFN530FAX:
        case DeviceTypes.RFN530FRX:
        case DeviceTypes.RFN530S4X:
        case DeviceTypes.RFN530S4EAX:
        case DeviceTypes.RFN530S4EAXR:
        case DeviceTypes.RFN530S4ERX:
        case DeviceTypes.RFN530S4ERXR:
            device = new RFN410();
            break;

        default:
            device = new GenericMCT();
            break;

        }

        return device;
    }

}
