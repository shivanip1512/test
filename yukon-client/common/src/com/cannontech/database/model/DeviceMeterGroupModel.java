package com.cannontech.database.model;

import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.definition.dao.PaoDefinitionDao;
import com.cannontech.common.pao.definition.model.PaoTag;
import com.cannontech.database.cache.DefaultDatabaseCache;
import com.cannontech.database.data.device.DeviceTypesFuncs;
import com.cannontech.database.data.lite.LiteBase;
import com.cannontech.database.data.lite.LiteComparators;
import com.cannontech.database.data.lite.LiteDeviceMeterNumber;
import com.cannontech.database.data.lite.LiteTypes;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.spring.YukonSpringHook;
import com.cannontech.yukon.IDatabaseCache;

public class DeviceMeterGroupModel extends DBTreeModel 
{
    private static final PaoDefinitionDao paoDefinitionDao = YukonSpringHook.getBean("paoDefinitionDao", PaoDefinitionDao.class);
    
    public DeviceMeterGroupModel() {
    	super( new DBTreeNode("Meter Numbers") );
    }
    
    @Override
    protected LiteBase convertLiteBase(LiteBase lb) {
        if (lb instanceof LiteYukonPAObject) {
            return findLiteDMG((LiteYukonPAObject)lb);
        }
        return lb;
    }
    
    private LiteBase findLiteDMG( LiteYukonPAObject liteYuk )
    {
    	LiteBase lBase = null;
    
    	IDatabaseCache cache = DefaultDatabaseCache.getInstance();
    	synchronized(cache) {
    		java.util.List<LiteDeviceMeterNumber> deviceMeterGroupsList = cache.getAllDeviceMeterGroups();
    		
    		for (LiteDeviceMeterNumber liteDeviceMeterNumber : deviceMeterGroupsList) {
    			lBase = liteDeviceMeterNumber;
    			if( lBase.equals(liteYuk) ) {
    				lBase = liteDeviceMeterNumber;
    				break;
    			}
    			else
    				lBase = null;
    		}
    	}
    	
    	return lBase;
    }
    
    
    public boolean isLiteTypeSupported( int liteType ) {
    	return( liteType == LiteTypes.YUKON_PAOBJECT
    			   || liteType == LiteTypes.DEVICE_METERNUMBER );	
    }
    
    @Override
    public boolean isTreePrimaryForObject(LiteBase lb) {
        return false;
    }
    
    private boolean isDeviceValid(PaoType paoType)
    {
        if (isPorterDevicesOnly()) {
            if (!paoDefinitionDao.isTagSupported(paoType, PaoTag.PORTER_COMMAND_REQUESTS)) {
                // only devices read through porter
                return false;
            }
        }
    	return DeviceTypesFuncs.usesDeviceMeterGroup(paoType.getDeviceTypeId());
    }
    
    public void update()
    {
    	IDatabaseCache cache = DefaultDatabaseCache.getInstance();
    
    	synchronized(cache) {
    		java.util.List<LiteDeviceMeterNumber> deviceMeterGroupsList = cache.getAllDeviceMeterGroups();
    		java.util.Collections.sort( deviceMeterGroupsList, LiteComparators.liteStringComparator );
    		
    		DBTreeNode rootNode = (DBTreeNode) getRoot();
    		rootNode.removeAllChildren();
    		
    		for (LiteDeviceMeterNumber liteDeviceMeterNumber : deviceMeterGroupsList) {
    			DBTreeNode deviceNode = new DBTreeNode( liteDeviceMeterNumber);
    			if (isDeviceValid(liteDeviceMeterNumber.getPaoType())) {
    			    rootNode.add( deviceNode );
    			}
    		}
    	}
    
    	reload();
    }
}
