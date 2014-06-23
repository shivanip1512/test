package com.cannontech.database.model;

import com.cannontech.common.pao.PaoCategory;
import com.cannontech.common.pao.PaoClass;
import com.cannontech.common.pao.PaoType;

public class MCTTreeModel extends AbstractDeviceTreeModel {

    public MCTTreeModel() {
        super(new DBTreeNode("MCTs"));
    }

    @Override
    public boolean isDeviceValid(PaoCategory paoCategory, PaoClass paoClass, PaoType paoType) {
        return paoType.isMct();
    }
}
