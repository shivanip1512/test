package com.cannontech.database.model;

/**
 * This type was created in VisualAge.
 */
import com.cannontech.database.data.lite.LiteBase;
//This models has the following:
//		1st Level = Ports YukonPAOBjects
//		2nd Level = Devices YukonPAOBjects

public class CommChannelTreeModel extends DBTreeModel 
{
/**
 * PortModel constructor comment.
 * @param root javax.swing.tree.TreeNode
 */
public CommChannelTreeModel() {
	super( new DBTreeNode("Comm Channels") );
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

	update();

	return false;
}
/**
 * Insert the method's description here.
 * Creation date: (4/22/2002 2:05:03 PM)
 * @return com.cannontech.database.data.lite.LiteBase[]
 */
public boolean isLiteTypeSupported( int liteType )
{
	return ( liteType == com.cannontech.database.data.lite.LiteTypes.YUKON_PAOBJECT );
}
/**
 * This method was created in VisualAge.
 * @return java.lang.String
 */
public String toString() {
	return "Comm Channel        ";
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
		java.util.List devices = cache.getAllYukonPAObjects();
		java.util.List ports = cache.getAllPorts();

		java.util.Collections.sort( devices, com.cannontech.database.data.lite.LiteComparators.liteYukonPAObjectPortComparator );
		java.util.Collections.sort( ports, com.cannontech.database.data.lite.LiteComparators.liteStringComparator );

		DBTreeNode rootNode = (DBTreeNode) getRoot();
		rootNode.removeAllChildren();
		
		int portID;
		int devicePortID;
		int deviceClass;
		int deviceType;
		for( int i = 0; i < ports.size(); i++ )
		{
			DBTreeNode portNode = new DBTreeNode( ports.get(i) );
			rootNode.add( portNode );
			portID = ((com.cannontech.database.data.lite.LiteYukonPAObject)ports.get(i)).getYukonID();

			boolean devicesFound = false;
			
			for( int j = 0; j < devices.size(); j++ )
			{
				deviceClass = ((com.cannontech.database.data.lite.LiteYukonPAObject)devices.get(j)).getPaoClass();
				
				if( deviceClass == com.cannontech.database.data.pao.DeviceClasses.LOADMANAGEMENT 
						|| deviceClass == com.cannontech.database.data.pao.DeviceClasses.IED 
						|| deviceClass == com.cannontech.database.data.pao.DeviceClasses.METER 
						|| deviceClass == com.cannontech.database.data.pao.DeviceClasses.RTU
						|| deviceClass == com.cannontech.database.data.pao.DeviceClasses.TRANSMITTER
						|| deviceClass == com.cannontech.database.data.pao.DeviceClasses.VIRTUAL
						|| deviceClass == com.cannontech.database.data.pao.DeviceClasses.RTU
						|| deviceClass == com.cannontech.database.data.pao.DeviceClasses.SYSTEM );

				{
					devicePortID = ((com.cannontech.database.data.lite.LiteYukonPAObject)devices.get(j)).getPortID();
					if( devicePortID == portID )
					{
						devicesFound = true;
						portNode.add( new DBTreeNode( devices.get(j)) );
					}
					else if( devicesFound )  // used to optimize the iterations
					{						
						devicesFound = false;
						break;
					}					
				}
			}
		}
	}

	reload();
}
/**
 * Insert the method's description here.
 * Creation date: (4/17/2002 1:58:45 PM)
 * @param lite com.cannontech.database.data.lite.LiteBase
 */
public boolean updateTreeObject(LiteBase lb) 
{
	if( lb == null || !isLiteTypeSupported(lb.getLiteType()) )
		return false;

	update();

	return false;
}
}
