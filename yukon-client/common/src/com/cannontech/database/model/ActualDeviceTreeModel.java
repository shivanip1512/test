package com.cannontech.database.model;

/**
 * This class replaced DeviceTreeModel (who is now an abstract class) as the 
 * model that represents devices. Doing this allowed us to properly provide
 * some default behavior for all TreeModels in DeviceTreeModel
 */
public class ActualDeviceTreeModel extends DeviceTreeModel {

    public ActualDeviceTreeModel() {
        this(true);
    } 

    public ActualDeviceTreeModel(boolean showPointNodes, DBTreeNode rootNode) {
        super(showPointNodes, rootNode);
    }

    public ActualDeviceTreeModel(boolean showPointNodes) {
        super(showPointNodes, new DBTreeNode("Devices"));
    }

    public ActualDeviceTreeModel(DBTreeNode rootNode) {
        super(rootNode);
    }

    public boolean isDeviceValid(int category_, int class_, int type_) {
        return com.cannontech.database.data.pao.DeviceClasses.isCoreDeviceClass(class_)
               && category_ == com.cannontech.database.data.pao.PAOGroups.CAT_DEVICE
               && type_ != com.cannontech.database.data.pao.PAOGroups.MCTBROADCAST;
    }
}
