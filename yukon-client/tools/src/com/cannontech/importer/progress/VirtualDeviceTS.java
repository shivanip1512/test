package com.cannontech.importer.progress;

import com.cannontech.database.data.device.VirtualDevice;

public class VirtualDeviceTS extends VirtualDevice {

    public VirtualDeviceTS() {
        super();
    }
    /**
     * This method was created in VisualAge.
     * @param obj com.cannontech.database.data.device.DeviceBase
     */
    public boolean equals(Object obj) 
    {
        String one, two;
        one = this.getPAOName();
        two = ((VirtualDeviceTS)obj).getPAOName();
        if ( one.compareTo(two) == 0 )
            return true;
        else
            return false;
    }    
}
