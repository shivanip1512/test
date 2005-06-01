package com.cannontech.database.model;

import com.cannontech.database.data.lite.LiteBase;
import com.cannontech.database.data.lite.LiteDeviceMeterNumber;

/**
 * This type was created in VisualAge.
 */

public class DeviceMeterGroupModel extends DBTreeModel 
{
/**
 * DeviceTreeModel constructor comment.
 * @param root javax.swing.tree.TreeNode
 */
public DeviceMeterGroupModel() {
	super( new DBTreeNode("Meter Numbers") );
}


/**
 * Insert the method's description here.
 * Creation date: (4/17/2002 1:58:45 PM)
 * @param lite com.cannontech.database.data.lite.LiteBase
 */
public boolean insertTreeObject( LiteBase lb ) 
{
	if( lb == null || !isLiteTypeSupported(lb.getLiteType()) )
		return false;

	DBTreeNode rootNode = (DBTreeNode) getRoot();
		
	if ( lb instanceof com.cannontech.database.data.lite.LiteYukonPAObject )
	{
		com.cannontech.database.data.lite.LiteYukonPAObject liteYuk =
				(com.cannontech.database.data.lite.LiteYukonPAObject)lb;

		if( isDeviceValid(liteYuk.getCategory(), liteYuk.getPaoClass(), liteYuk.getType() ) )
		{
			//this is the case that makes this model so special, we must map the current
			//  PAOObject to its owner LiteDeviceMeterGroup
			LiteBase lBase = findLiteDMG( liteYuk );
			
			
			DBTreeNode node = new DBTreeNode(lBase);

			//add all new tree nodes to the top, for now
			int[] ind = { 0 };
			
			rootNode.insert( node, ind[0] );
			
			nodesWereInserted(
				rootNode,
				ind );

			return true;
		}

	}

	return false;
}

private LiteBase findLiteDMG( com.cannontech.database.data.lite.LiteYukonPAObject liteYuk )
{
	LiteBase lBase = null;

	com.cannontech.database.cache.DefaultDatabaseCache cache =
					com.cannontech.database.cache.DefaultDatabaseCache.getInstance();
	synchronized(cache)
	{
		java.util.List deviceMeterGroupsList = cache.getAllDeviceMeterGroups();
		
		int deviceDevID;
		for( int i = 0; i < deviceMeterGroupsList.size(); i++ )
		{
			lBase = (LiteBase)deviceMeterGroupsList.get(i);
			if( lBase.equals(liteYuk) )
			{
				lBase = (LiteDeviceMeterNumber)deviceMeterGroupsList.get(i);
				break;
			}
			else
				lBase = null;
		}
	}
	
	return lBase;
}


/**
 * Insert the method's description here.
 * Creation date: (4/22/2002 2:05:03 PM)
 * @return com.cannontech.database.data.lite.LiteBase[]
 */
public boolean isLiteTypeSupported( int liteType )
{
	return( liteType == com.cannontech.database.data.lite.LiteTypes.YUKON_PAOBJECT
			   || liteType == com.cannontech.database.data.lite.LiteTypes.DEVICE_METERNUMBER );	
}

public boolean isDeviceValid( int category_, int class_, int type_ )
{
	return ( (com.cannontech.database.data.device.DeviceTypesFuncs.isMCT(type_)
					|| com.cannontech.database.data.device.DeviceTypesFuncs.isMeter(type_))
			    && category_ == com.cannontech.database.data.pao.PAOGroups.CAT_DEVICE );
}

/**
 * This method was created in VisualAge.
 */
public void update()
{
	com.cannontech.database.cache.DefaultDatabaseCache cache =
					com.cannontech.database.cache.DefaultDatabaseCache.getInstance();

	synchronized(cache)
	{
		java.util.List deviceMeterGroupsList = cache.getAllDeviceMeterGroups();
		java.util.Collections.sort( deviceMeterGroupsList, com.cannontech.database.data.lite.LiteComparators.liteStringComparator );
		
		DBTreeNode rootNode = (DBTreeNode) getRoot();
		rootNode.removeAllChildren();
		
		int deviceDevID;
		for( int i = 0; i < deviceMeterGroupsList.size(); i++ )
		{
			DBTreeNode deviceNode = new DBTreeNode( deviceMeterGroupsList.get(i));
			rootNode.add( deviceNode );
		}
	}

	reload();
}
}
