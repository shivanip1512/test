package com.cannontech.database.model;

import com.cannontech.common.pao.PaoCategory;
import com.cannontech.common.pao.PaoClass;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.definition.dao.PaoDefinitionDao;
import com.cannontech.common.pao.definition.model.PaoTag;
import com.cannontech.database.data.pao.DeviceClasses;
import com.cannontech.spring.YukonSpringHook;

/**
 * This class replaced DeviceTreeModel (who is now an abstract class called AbstractDeviceTreeModel) as the 
 * model that represents devices. Doing this allowed us to properly provide
 * some default behavior for all TreeModels in DeviceTreeModel
 */
public class DeviceTreeModel extends AbstractDeviceTreeModel {

    private static final PaoDefinitionDao paoDefinitionDao = YukonSpringHook.getBean("paoDefinitionDao", PaoDefinitionDao.class);

    public DeviceTreeModel() {
        this(true);
    } 

    public DeviceTreeModel(boolean showPointNodes, DBTreeNode rootNode) {
        super(showPointNodes, rootNode);
    }

    public DeviceTreeModel(boolean showPointNodes) {
        super(showPointNodes, new DBTreeNode("Devices"));
    }

    public DeviceTreeModel(DBTreeNode rootNode) {
        super(rootNode);
    }

    @Override
    public boolean isDeviceValid(PaoCategory paoCategory, PaoClass paoClass, PaoType paoType) {
        
        // if not compatible with DBEditor (representing any "hard client"), return false.
        if (paoDefinitionDao.isTagSupported(paoType, PaoTag.DB_EDITOR_INCOMPATIBLE)) {
            return false;
        }
        
        if (paoType.isMeter()) {    // "meters" are of valid "type", but need to do a little more checking
            
            if (isPorterDevicesOnly()) {    // for "meters", need to check if we're limiting only to porter supported devices
                return paoDefinitionDao.isTagSupported(paoType, PaoTag.PORTER_COMMAND_REQUESTS);
            } else {
                return true;
            }
        } else if (DeviceClasses.isCoreDeviceClass(paoClass.getPaoClassId()) 
                && paoCategory == PaoCategory.DEVICE) {
            return true;
        }

        return false;
    }
}
