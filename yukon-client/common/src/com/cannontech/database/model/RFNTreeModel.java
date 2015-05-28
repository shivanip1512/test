package com.cannontech.database.model;

import com.cannontech.common.pao.PaoCategory;
import com.cannontech.common.pao.PaoClass;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.definition.dao.PaoDefinitionDao;
import com.cannontech.common.pao.definition.model.PaoTag;
import com.cannontech.spring.YukonSpringHook;

public class RFNTreeModel extends AbstractDeviceTreeModel {

    private static final PaoDefinitionDao paoDefinitionDao = YukonSpringHook.getBean("paoDefinitionDao", PaoDefinitionDao.class);

    public RFNTreeModel() {
        super(new DBTreeNode("RF Mesh"));
    }

    /**
     * Valid devices have PaoClass.RFMESH (includes water/electric meters, and RF LCRS).
     */
    @Override
    public boolean isDeviceValid(PaoCategory paoCategory, PaoClass paoClass, PaoType paoType) {
        if (paoDefinitionDao.isTagSupported(paoType, PaoTag.DB_EDITOR_INCOMPATIBLE)) {
            return false;
        }
        return (paoClass == PaoClass.RFMESH);
    }
}
