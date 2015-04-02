package com.cannontech.database.model;

import com.cannontech.common.pao.PaoCategory;
import com.cannontech.common.pao.PaoClass;
import com.cannontech.common.pao.PaoType;

public class TransmitterTreeModel extends AbstractDeviceTreeModel {

    public TransmitterTreeModel() {
        this(true);
    }

    public TransmitterTreeModel(boolean showPointNodes) {
        this(showPointNodes, new DBTreeNode("Transmitters"));
    }

    public TransmitterTreeModel(boolean showPointNodes, DBTreeNode rootNode_) {
        super(showPointNodes, rootNode_);
    }

    @Override
    public boolean isDeviceValid(PaoCategory paoCategory, PaoClass paoClass, PaoType paoType) {
        return (paoType.isTransmitter() && paoType != PaoType.DIGIGATEWAY);
    }
}
