package com.cannontech.database.model;

/**
 * Insert the type's description here.
 * Creation date: (4/18/00 11:39:02 AM)
 * @author: 
 * @Version: <version>
 */
public class TDCDeviceTreeModel extends DeviceTreeModel
{
/**
 * NoEmptyDeviceTreeModel constructor comment.
 * @param root javax.swing.tree.TreeNode
 */
public TDCDeviceTreeModel() 
{
	super( true, new DBTreeNode("Point Attachable Objects") );
}
/**
 * Insert the method's description here.
 * Creation date: (4/22/2002 4:11:23 PM)
 * @param deviceType int
 */
public boolean isDeviceValid( int cateogry_, int class_, int type_ )
{
	return( class_ != com.cannontech.database.data.pao.DeviceClasses.SYSTEM );
}

protected synchronized java.util.List getCacheList(
        com.cannontech.database.cache.DefaultDatabaseCache cache ) 
{
    return cache.getAllYukonPAObjects();
}

/**
 * update method comment.
 */
/*public void update() {
	com.cannontech.database.cache.DefaultDatabaseCache cache =
					com.cannontech.database.cache.DefaultDatabaseCache.getInstance();

	synchronized(cache)
	{
		java.util.List devices = cache.getAllYukonPAObjects();
		java.util.List points = cache.getAllPoints();

		java.util.Collections.sort( devices, com.cannontech.database.data.lite.LiteComparators.liteStringComparator );
		java.util.Collections.sort( points, 
				com.cannontech.database.data.lite.LiteComparators.liteStringComparator );


		javax.swing.tree.DefaultMutableTreeNode rootNode = (javax.swing.tree.DefaultMutableTreeNode) getRoot();
		rootNode.removeAllChildren();
		
		int deviceDevID;
		int deviceClass;
		for( int i = 0; i < devices.size(); i++ )
		{
			deviceClass = ((com.cannontech.database.data.lite.LiteYukonPAObject)devices.get(i)).getPaoClass();
			
			if( isDeviceValid(-1, deviceClass, -1) )
			{								
				DBTreeNode deviceNode = new DBTreeNode( devices.get(i));
				deviceDevID = ((com.cannontech.database.data.lite.LiteYukonPAObject)devices.get(i)).getYukonID();
				boolean rootNeedsAdding = true;
				boolean pointsFound = false;
				
				for( int j = 0; j < points.size(); j++ )
				{
					if( ((com.cannontech.database.data.lite.LitePoint)points.get(j)).getPaobjectID() == deviceDevID )
					{
						pointsFound = true;
						
						if( rootNeedsAdding )						
						{ 	//only add device if it has some points
							rootNeedsAdding = false;
							rootNode.add( deviceNode );
						}

						deviceNode.add( new DBTreeNode( points.get(j)) );
					}
//					else if( pointsFound )  // used to optimize the iterations
//					{						
//						pointsFound = false;
//						break;
//					}					
				}
			}
		}
	}

	reload();	
}
*/
}
