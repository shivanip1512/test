package com.cannontech.database.model;

import com.cannontech.common.pao.PaoCategory;
import com.cannontech.common.pao.PaoClass;
import com.cannontech.common.pao.PaoType;
import com.cannontech.database.data.pao.DeviceClasses;

/**
 * This class replaced DeviceTreeModel (who is now an abstract class called AbstractDeviceTreeModel) as the 
 * model that represents devices. Doing this allowed us to properly provide
 * some default behavior for all TreeModels in DeviceTreeModel
 */
public class DeviceTreeModel extends AbstractDeviceTreeModel {

    public DeviceTreeModel() {
        this(true);
    } 

    public DeviceTreeModel(boolean showPointNodes, DBTreeNode rootNode) {
        super(showPointNodes, rootNode);
    }

    public DeviceTreeModel(boolean showPointNodes) {
        super(showPointNodes, new DBTreeNode("Devices"));
    }

    public DeviceTreeModel(DBTreeNode rootNode) {
        super(rootNode);
    }

    public boolean isDeviceValid(PaoCategory paoCategory, PaoClass paoClass, PaoType paoType) {
        return DeviceClasses.isCoreDeviceClass(paoClass.getPaoClassId())
               && paoCategory == PaoCategory.DEVICE
               && paoType != PaoType.MCTBROADCAST;
    }
}
