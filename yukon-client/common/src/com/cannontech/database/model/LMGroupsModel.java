package com.cannontech.database.model;

import java.util.List;

import com.cannontech.common.pao.PaoCategory;
import com.cannontech.common.pao.PaoClass;
import com.cannontech.common.pao.PaoType;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.yukon.IDatabaseCache;

public class LMGroupsModel extends AbstractDeviceTreeModel {

    public LMGroupsModel() {
        super(new DBTreeNode("Load Group"));
    }

    public LMGroupsModel(boolean showPointNodes) {
        super(showPointNodes, new DBTreeNode("Load Group"));
    }

    @Override
    public synchronized List<LiteYukonPAObject> getCacheList(IDatabaseCache cache) {
        return cache.getAllLoadManagement();
    }

    @Override
    public boolean isDeviceValid(PaoCategory paoCategory, PaoClass paoClass, PaoType paoType) {

        if (isCommanderDevicesOnly() && paoType == PaoType.LM_GROUP_DIGI_SEP) {
            // DIGI_SEP groups are not compatible with porter
            return false;
        } 

        return (paoClass == PaoClass.GROUP && paoCategory == PaoCategory.DEVICE);
    }

    @Override
    protected DBTreeNode getNewDeviceNode(LiteYukonPAObject pao) {
        if (pao.getPaoType() == PaoType.LM_GROUP_METER_DISCONNECT) {
            // Meter Disconnect programs can only be edited on the web 
            DBTreeNode warningNode = new DBTreeNode(new WebOnlyEditWarning(pao));
            warningNode.setIsSystemReserved(true);
            return warningNode;
        }
        return super.getNewDeviceNode(pao);
    }
}