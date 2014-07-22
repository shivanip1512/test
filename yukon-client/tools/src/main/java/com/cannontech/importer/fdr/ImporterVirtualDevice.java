package com.cannontech.importer.fdr;

import com.cannontech.database.data.device.VirtualDevice;

public class ImporterVirtualDevice extends VirtualDevice {

    public ImporterVirtualDevice() {
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
        two = ((ImporterVirtualDevice)obj).getPAOName();
        if ( one.compareTo(two) == 0 )
            return true;
        else
            return false;
    }    
}
