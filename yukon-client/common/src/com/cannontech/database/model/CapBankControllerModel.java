package com.cannontech.database.model;

import com.cannontech.common.pao.PaoCategory;
import com.cannontech.common.pao.PaoClass;
import com.cannontech.common.pao.PaoType;

public class CapBankControllerModel extends AbstractDeviceTreeModel {

    public CapBankControllerModel() {
    	super( new DBTreeNode("Cap Bank Controllers") );
    }

    public CapBankControllerModel( boolean showPointNodes) {
    	super( showPointNodes, new DBTreeNode("Cap Bank Controllers") );
    }

    public boolean isDeviceValid( PaoCategory paoCategory, PaoClass paoClass, PaoType paoType ) {
    	return paoType.isCbc();
    }
}
