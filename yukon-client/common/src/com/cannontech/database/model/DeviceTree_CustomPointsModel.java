package com.cannontech.database.model;

import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.database.data.pao.DeviceClasses;
import com.cannontech.database.data.pao.PAOGroups;

public class DeviceTree_CustomPointsModel extends DeviceTreeModel {

    private long includePoints = 0x00000000;

    public DeviceTree_CustomPointsModel() {
        super(new DBTreeNode("Devices"));
    }

    public DeviceTree_CustomPointsModel(boolean showPointNodes) {
        super(showPointNodes, new DBTreeNode("Devices"));
    }

    public boolean isDeviceValid(int category_, int class_, int type_) {
        return ((DeviceClasses.isCoreDeviceClass(class_) || class_ == PAOGroups.CAT_CAPCONTROL) && category_ == PAOGroups.CAT_DEVICE);
    }

    public void setIncludeUOFMType(long pointUOFMMask) {
        includePoints = pointUOFMMask;
    }

    public String toString() {
        if ((includePoints & LitePoint.POINT_UOFM_GRAPH) == LitePoint.POINT_UOFM_GRAPH) {
            return "Graph Points";
        } else if ((includePoints & LitePoint.POINT_UOFM_USAGE) == LitePoint.POINT_UOFM_USAGE) {
            return "Usage Points";
        }

        return "Device";
    }

    protected boolean isPointValid(LitePoint lp) {
        if (lp == null) {
            return false;
        }else {
                return (lp.getTags() == includePoints);
        }
    }
}
