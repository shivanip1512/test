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

        if (isPorterDevicesOnly() && paoType == PaoType.LM_GROUP_DIGI_SEP) {
            // DIGI_SEP groups are not compatible with porter
            return false;
        } else {
            return (paoClass == PaoClass.GROUP 
                    && paoCategory == PaoCategory.DEVICE);
        }
    }
}