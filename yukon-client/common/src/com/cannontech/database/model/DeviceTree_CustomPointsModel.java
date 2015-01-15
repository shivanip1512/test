package com.cannontech.database.model;

import com.cannontech.common.pao.PaoCategory;
import com.cannontech.common.pao.PaoClass;
import com.cannontech.common.pao.PaoType;
import com.cannontech.database.data.lite.LitePoint;

public class DeviceTree_CustomPointsModel extends AbstractDeviceTreeModel {

    private long includePoints = 0x00000000;

    public DeviceTree_CustomPointsModel() {
        super(new DBTreeNode("Devices"));
    }

    public DeviceTree_CustomPointsModel(boolean showPointNodes) {
        super(showPointNodes, new DBTreeNode("Devices"));
    }

    @Override
    public boolean isDeviceValid(PaoCategory paoCategory, PaoClass paoClass, PaoType paoType) {
        return ((paoClass.isCore() || paoClass == PaoClass.CAPCONTROL) 
        		&& paoCategory == PaoCategory.DEVICE);
    }

    public void setIncludeUOFMType(long pointUOFMMask) {
        includePoints = pointUOFMMask;
    }

    @Override
    public String toString() {
        if ((includePoints & LitePoint.POINT_UOFM_GRAPH) == LitePoint.POINT_UOFM_GRAPH) {
            return "Graph Points";
        } else if ((includePoints & LitePoint.POINT_UOFM_USAGE) == LitePoint.POINT_UOFM_USAGE) {
            return "Usage Points";
        }

        return "Device";
    }

    @Override
    protected boolean isPointValid(LitePoint lp) {
        if (lp == null) {
            return false;
        }else {
                return (lp.getTags() == includePoints);
        }
    }
}
