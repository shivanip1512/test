package com.cannontech.database.model;
/**
 * This type was created in VisualAge.
 */
import java.util.List;

import com.cannontech.common.pao.PaoType;
import com.cannontech.database.data.device.DeviceTypesFuncs;
import com.cannontech.database.data.lite.LiteBase;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.yukon.IDatabaseCache;
 
public class MCTBroadcastGroupTreeModel extends DBTreeModel 
{
/**
 * MeterTreeModel constructor comment.
 * @param root javax.swing.tree.TreeNode
 */
public MCTBroadcastGroupTreeModel() {
	super( new DBTreeNode("MCT Broadcast") );
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

@Override
public boolean isTreePrimaryForObject(LiteBase lb) {
    if (lb instanceof LiteYukonPAObject) {
        PaoType paoType = ((LiteYukonPAObject) lb).getPaoIdentifier().getPaoType();
        return paoType == PaoType.MCTBROADCAST;
    }
    return false;
}

/**
 * This method was created in VisualAge. (and changed by the minion.)
 */
public void update() {

	IDatabaseCache cache =
					com.cannontech.database.cache.DefaultDatabaseCache.getInstance();

	synchronized(cache)
	{
		//This is a crappy and inefficient way to do this if there are many devices...
		List<LiteYukonPAObject> devicesList = cache.getAllDevices();
		java.util.Vector broadcastersList = new java.util.Vector();
		java.util.Vector mctList = new java.util.Vector();
		//This hopefully helps with the speed issue somewhat
		com.cannontech.common.util.NativeIntVector mctIDIntList = new com.cannontech.common.util.NativeIntVector(30);

		DBTreeNode rootNode = (DBTreeNode) getRoot();
		rootNode.removeAllChildren();

		int mctBroadcastID;
		com.cannontech.database.data.device.MCT_Broadcast necessaryEvil = new com.cannontech.database.data.device.MCT_Broadcast();
		
		//wow, this just keeps getting fouler...Just wait, it gets even worse...keep going
		
		for (LiteYukonPAObject liteYukonPAObject : devicesList) {
			if(liteYukonPAObject.getPaoType() == PaoType.MCTBROADCAST)
				broadcastersList.add(liteYukonPAObject);
			else if(DeviceTypesFuncs.isMCT(liteYukonPAObject.getPaoType().getDeviceTypeId()))
				mctList.add(liteYukonPAObject);
		}

		/*This is a bit slow because the database must be hit to find out what MCTs are owned
		by each broadcast group.  There is no way to get this info using Lite objects, so
		a new chubby must be created in order to call its internal MCT ownership methods*/
		
		for( int i = 0; i < broadcastersList.size(); i++ )
		{
			DBTreeNode broadcastGroupNode = new DBTreeNode( broadcastersList.get(i));	
			mctBroadcastID =  ((com.cannontech.database.data.lite.LiteYukonPAObject)broadcastersList.get(i)).getYukonId();

			try
			{
				mctIDIntList = necessaryEvil.getAllMCTsIDList(new Integer(mctBroadcastID));
				
				for( int j = 0; j < mctList.size(); j++ )
				{
					if(mctIDIntList.contains(((com.cannontech.database.data.lite.LiteYukonPAObject)mctList.elementAt(j)).getYukonId()))
						{
							broadcastGroupNode.add( new DBTreeNode( ((com.cannontech.database.data.lite.LiteYukonPAObject)mctList.elementAt(j) )));
						}
				}
			}
			catch(java.sql.SQLException e )
			{
			//not necessarily an error
			}
			rootNode.add( broadcastGroupNode );		
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