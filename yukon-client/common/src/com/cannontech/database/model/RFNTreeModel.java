package com.cannontech.database.model;

import com.cannontech.common.pao.PaoCategory;
import com.cannontech.common.pao.PaoClass;
import com.cannontech.common.pao.PaoType;

public class RFNTreeModel extends AbstractDeviceTreeModel {

    public RFNTreeModel() {
        super(new DBTreeNode("RF Mesh"));
    }

    /**
     * Valid devices have PaoClass.RFMESH (includes water/electric meters, and RF LCRS).
     */
    @Override
    public boolean isDeviceValid(PaoCategory paoCategory, PaoClass paoClass, PaoType paoType) {
        return (paoClass == PaoClass.RFMESH);
    }
}
