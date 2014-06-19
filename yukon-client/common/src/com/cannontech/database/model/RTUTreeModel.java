package com.cannontech.database.model;

import com.cannontech.common.pao.PaoCategory;
import com.cannontech.common.pao.PaoClass;
import com.cannontech.common.pao.PaoType;
import com.cannontech.database.data.device.DeviceTypesFuncs;

public class RTUTreeModel extends AbstractDeviceTreeModel {

    /**
     * RtuTreeModel constructor comment.
     */
    public RTUTreeModel() {
        super(new DBTreeNode("RTUs"));
    }

    @Override
    public boolean isDeviceValid(PaoCategory paoCategory, PaoClass paoClass, PaoType paoType) {
        return (paoClass == PaoClass.LOADMANAGEMENT || paoClass == PaoClass.IED || paoClass == PaoClass.METER || paoClass == PaoClass.RTU || paoClass == PaoClass.TRANSMITTER || paoClass == PaoClass.VIRTUAL || paoClass == PaoClass.RTU || paoClass == PaoClass.SYSTEM) && (DeviceTypesFuncs.isRTU(paoType.getDeviceTypeId()) || paoType == PaoType.DAVISWEATHER) && paoCategory == PaoCategory.DEVICE && !DeviceTypesFuncs.isIon(paoType.getDeviceTypeId());
    }
}
