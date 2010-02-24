package com.cannontech.database.model;

import com.cannontech.database.data.pao.DeviceClasses;
import com.cannontech.database.data.pao.PAOGroups;

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

    public boolean isDeviceValid(int category_, int class_, int type_) {
        return DeviceClasses.isCoreDeviceClass(class_)
               && category_ == PAOGroups.CAT_DEVICE
               && type_ != PAOGroups.MCTBROADCAST;
    }
}
