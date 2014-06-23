package com.cannontech.database.model;

import com.cannontech.common.pao.PaoCategory;
import com.cannontech.common.pao.PaoClass;
import com.cannontech.common.pao.PaoType;

public class RTUTreeModel extends AbstractDeviceTreeModel {

    /**
     * RtuTreeModel constructor comment.
     */
    public RTUTreeModel() {
        super(new DBTreeNode("RTUs"));
    }

    @Override
    public boolean isDeviceValid(PaoCategory paoCategory, PaoClass paoClass, PaoType paoType) {
        return paoType.isRtu() || paoType == PaoType.DAVISWEATHER;
    }
}
