package com.cannontech.database.model;

import com.cannontech.common.pao.PaoCategory;
import com.cannontech.common.pao.PaoClass;
import com.cannontech.common.pao.PaoType;

public class IEDTreeModel extends AbstractDeviceTreeModel {

    /**
     * IEDTreeModel constructor comment.
     */
    public IEDTreeModel() {
        super(new DBTreeNode("IEDs"));
    }

    @Override
    public boolean isDeviceValid(PaoCategory paoCategory, PaoClass paoClass, PaoType paoType) {
        return paoType.isIed() || paoType == PaoType.DAVISWEATHER;
    }
}
