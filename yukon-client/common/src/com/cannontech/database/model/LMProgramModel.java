package com.cannontech.database.model;

import java.util.List;

import com.cannontech.common.pao.PaoCategory;
import com.cannontech.common.pao.PaoClass;
import com.cannontech.common.pao.PaoType;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.yukon.IDatabaseCache;

public class LMProgramModel extends AbstractDeviceTreeModel {

    public LMProgramModel() {
        super(new DBTreeNode("Load Program"));
    }

    @Override
    public synchronized List<LiteYukonPAObject> getCacheList(IDatabaseCache cache) {
        return cache.getAllLoadManagement();
    }

    @Override
    public boolean isDeviceValid(PaoCategory paoCategory, PaoClass paoClass, PaoType paoType) {
        return paoType.isLmProgram();
    }

    @Override
    protected DBTreeNode getNewDeviceNode(LiteYukonPAObject pao) {
        if (pao.getPaoType() == PaoType.LM_METER_DISCONNECT_PROGRAM) {
            // Meter Disconnect programs can only be edited on the web 
            DBTreeNode warningNode = new DBTreeNode(new WebOnlyEditWarning(pao));
            warningNode.setIsSystemReserved(true);
            return warningNode;
        }
        return super.getNewDeviceNode(pao);
    }
}