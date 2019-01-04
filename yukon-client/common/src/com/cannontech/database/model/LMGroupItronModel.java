package com.cannontech.database.model;

import java.util.List;

import com.cannontech.common.pao.PaoCategory;
import com.cannontech.common.pao.PaoClass;
import com.cannontech.common.pao.PaoType;
import com.cannontech.yukon.IDatabaseCache;

public class LMGroupItronModel extends AbstractDeviceTreeModel {

    public LMGroupItronModel() {
        super(new DBTreeNode(PaoType.LM_GROUP_ITRON.getDbString()));
    }

    @Override
    public synchronized List getCacheList(IDatabaseCache cache) {
        return cache.getAllLoadManagement();
    }

    @Override
    public boolean isDeviceValid(PaoCategory paoCategory, PaoClass paoClass, PaoType paoType) {
        return (paoType == PaoType.LM_GROUP_ITRON);
    }
}
