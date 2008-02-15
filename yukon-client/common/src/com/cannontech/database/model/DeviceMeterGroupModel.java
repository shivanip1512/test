package com.cannontech.database.model;

import com.cannontech.database.cache.DefaultDatabaseCache;
import com.cannontech.database.data.device.DeviceTypesFuncs;
import com.cannontech.database.data.lite.LiteBase;
import com.cannontech.database.data.lite.LiteComparators;
import com.cannontech.database.data.lite.LiteDeviceMeterNumber;
import com.cannontech.database.data.lite.LiteTypes;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.database.data.pao.PAOGroups;
import com.cannontech.yukon.IDatabaseCache;

public class DeviceMeterGroupModel extends DBTreeModel 
{

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
    			lBase = (LiteBase)liteDeviceMeterNumber;
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
    
    public boolean isDeviceValid( int category_, int class_, int type_ )
    {
    	return ( (DeviceTypesFuncs.isMCT(type_) || DeviceTypesFuncs.isMeter(type_))
    			    && category_ == PAOGroups.CAT_DEVICE );
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
    			rootNode.add( deviceNode );
    		}
    	}
    
    	reload();
    }
}
