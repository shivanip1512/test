package com.cannontech.database.model;

/**
 * This type was created in VisualAge.
 */
import javax.swing.tree.TreePath;

import com.cannontech.common.util.CtiUtilities;
import com.cannontech.database.cache.functions.PAOFuncs;
import com.cannontech.database.data.lite.LiteBase;
import com.cannontech.database.data.lite.LiteComparators;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.database.data.pao.PAOGroups;
//This models has the following:
//		1st Level = Ports YukonPAOBjects
//		2nd Level = Devices YukonPAOBjects

public class CommChannelTreeModel extends DBTreeModel 
{
	
	//a mutable lite point used for comparisons
	private static final LiteYukonPAObject DUMMY_LITE_PAO = 
					new LiteYukonPAObject(Integer.MIN_VALUE, "**DUMMY**");

	//a Vector only needed to store temporary things
	private java.util.List tempList = new java.util.Vector(32);

/**
 * PortModel constructor comment.
 * @param root javax.swing.tree.TreeNode
 */
public CommChannelTreeModel() {
	super( new DBTreeNode("Comm Channels") );
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
				LiteYukonPAObject liteYuk = (LiteYukonPAObject)devices.get(j);
				
				if( isDeviceValid(liteYuk.getCategory(), liteYuk.getPaoClass(), liteYuk.getType() ) )
				{
					devicePortID = ((LiteYukonPAObject)devices.get(j)).getPortID();
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
public boolean insertTreeObject( LiteBase lb ) 
{
	if( lb == null || !isLiteTypeSupported(lb.getLiteType()) )
		return false;

	DBTreeNode rootNode = (DBTreeNode) getRoot();

	if( lb instanceof LiteYukonPAObject 
		 && ((LiteYukonPAObject)lb).getPortID() > PAOGroups.INVALID )
	{
		int devID = ((LiteYukonPAObject)lb).getPortID();

		rootNode = findLiteObject( null, 
				PAOFuncs.getLiteYukonPAO(devID) );

		if( rootNode != null )
		{

			//this will force us to reload ALL the children for this PAObject
			rootNode.setWillHaveChildren(true);
			TreePath rootPath = new TreePath( rootNode );
			treePathWillExpand( rootPath );

			updateTreeNodeStructure( rootNode );

			return true;
		}
	}
	else if( lb instanceof LiteYukonPAObject )
	{
		LiteYukonPAObject liteYuk = (LiteYukonPAObject)lb;

		if( isDeviceValid(liteYuk.getCategory(), liteYuk.getPaoClass(), liteYuk.getType() ) )
		{
			DBTreeNode node = new DBTreeNode(lb);

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

/**
 * Insert the method's description here.
 * Creation date: (4/22/2002 4:11:23 PM)
 * @param deviceType int
 */
public boolean isDeviceValid( int category_, int deviceClass, int type_ )
{
	return( deviceClass == com.cannontech.database.data.pao.DeviceClasses.LOADMANAGEMENT 
			|| deviceClass == com.cannontech.database.data.pao.DeviceClasses.IED 
			|| deviceClass == com.cannontech.database.data.pao.DeviceClasses.METER 
			|| deviceClass == com.cannontech.database.data.pao.DeviceClasses.RTU
			|| deviceClass == com.cannontech.database.data.pao.DeviceClasses.TRANSMITTER
			|| deviceClass == com.cannontech.database.data.pao.DeviceClasses.VIRTUAL
			|| deviceClass == com.cannontech.database.data.pao.DeviceClasses.RTU
			|| deviceClass == com.cannontech.database.data.pao.DeviceClasses.SYSTEM );

}


/**
 * Insert the method's description here.
 * Creation date: (2/27/2002 10:37:56 AM)
 * @param points java.util.List
 * @param destList java.util.Vector
 */
private boolean createPaoChildList(java.util.List paos_, java.util.List destList_ )
{
	//searches and sorts the list!
	CtiUtilities.binarySearchRepetition( 
					paos_,
					DUMMY_LITE_PAO, //must have the needed PortID set!!
					LiteComparators.litePaoPortIDComparator,
					destList_ );
						
	for( int i = destList_.size()-1; i >= 0; i-- )
	{
		LiteYukonPAObject lp = (LiteYukonPAObject)destList_.get(i);
	}

	return destList_.size() > 0;
}

/**
 * Insert the method's description here.
 * Creation date: (4/25/2002 12:35:32 PM)
 * @param path javax.swing.tree.TreePath
 */
public synchronized void treePathWillExpand(javax.swing.tree.TreePath path)
{
	//Watch out, this reloads the children every TIME!!!
	DBTreeNode node = (DBTreeNode)path.getLastPathComponent();

	if( node.willHaveChildren() &&
		 node.getUserObject() instanceof LiteYukonPAObject )
	{
		com.cannontech.database.cache.DefaultDatabaseCache cache =
			com.cannontech.database.cache.DefaultDatabaseCache.getInstance();

		synchronized (cache)
		{
			int portID = ((LiteYukonPAObject)node.getUserObject()).getYukonID();
			java.util.List paos = cache.getAllYukonPAObjects();

			//change our dummy points device ID to the current DeviceID
			DUMMY_LITE_PAO.setPortID( portID );
			
			//lock our point list down
			synchronized( tempList )
			{
				node.removeAllChildren();
				tempList.clear();
				
				//makes a list of points associated with the current deviceNode
				createPaoChildList( paos, tempList );

				//add all points and point types to the deviceNode
				addPaos( node );
			}
		}
	}

	node.setWillHaveChildren(false);
}



/**
 * Insert the method's description here.
 * Creation date: (4/19/2002 1:35:08 PM)
 * @param deviceNode DBTreeNode
 */
private void addPaos( DBTreeNode deviceNode )
{
	for (int j = 0; j < tempList.size(); j++)
	{
		deviceNode.add( new DBTreeNode(tempList.get(j)) );
	}

	//tempList is cleared
	tempList.clear();
}


}
