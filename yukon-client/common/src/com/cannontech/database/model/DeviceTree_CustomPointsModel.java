package com.cannontech.database.model;

/**
 * This type was created in VisualAge.
 */
public class DeviceTree_CustomPointsModel extends DBTreeModel 
{
	private boolean showPoints = true;

	public static final String[] POINT_UNIT_ARRAY =
	{
		"KWH",
		"KVAH",
		"KVARH",
		"MWH",
		"MWVAH",
		"MWVARH"
	};

	private long includePoints = 0x00000000;
/**
 * DeviceTreeModel constructor comment.
 * @param root javax.swing.tree.TreeNode
 */
public DeviceTree_CustomPointsModel() {
	super( new DBTreeNode("Devices") );
}
/**
 * DeviceTreeModel constructor comment.
 * @param root javax.swing.tree.TreeNode
 */
public DeviceTree_CustomPointsModel( boolean showPointNodes )
{
	super( new DBTreeNode("Devices") );
	showPoints = showPointNodes;
}
/**
 * Insert the method's description here.
 * Creation date: (4/17/2002 1:58:45 PM)
 * @param lite com.cannontech.database.data.lite.LiteBase
 */
public boolean insertTreeObject( com.cannontech.database.data.lite.LiteBase lb ) 
{
	if( lb == null || !isLiteTypeSupported(lb.getLiteType()) )
		return false;

	//do inserts the old way by reloading the tree
	update();

	return false;
}
/**
 * This method was created in VisualAge.
 * @return java.lang.String
 */
public static boolean isInPointUnitArray( String value )
{
	for( int i = 0; i < POINT_UNIT_ARRAY.length; i++ )
	{
		if( POINT_UNIT_ARRAY[i].equalsIgnoreCase( value ) )
			return true;
	}
	
	return false;
}
/**
 * Insert the method's description here.
 * Creation date: (4/22/2002 2:05:03 PM)
 * @return com.cannontech.database.data.lite.LiteBase[]
 */
public boolean isLiteTypeSupported( int liteType )
{
	return ( liteType == com.cannontech.database.data.lite.LiteTypes.YUKON_PAOBJECT
		  		|| liteType == com.cannontech.database.data.lite.LiteTypes.POINT );
}

public void setIncludeUOFMType(long pointUOFMMask)
{
	includePoints = pointUOFMMask;
}
/**
 * This method was created in VisualAge.
 * @return java.lang.String
 */
public String toString() {
	if( (includePoints & com.cannontech.database.data.lite.LitePoint.POINT_UOFM_GRAPH) == com.cannontech.database.data.lite.LitePoint.POINT_UOFM_GRAPH)
	{
		return "Graph Points";
	}
	else if( (includePoints & com.cannontech.database.data.lite.LitePoint.POINT_UOFM_USAGE) == com.cannontech.database.data.lite.LitePoint.POINT_UOFM_USAGE)
	{
		return "Usage Points";
	}

	return "Device";
}
/**
 * This method was created in VisualAge.
 */
public void update() {

	com.cannontech.database.cache.DefaultDatabaseCache cache =
					com.cannontech.database.cache.DefaultDatabaseCache.getInstance();

	synchronized(cache)
	{
		java.util.List devices = cache.getAllDevices();
		java.util.Collections.sort( devices, com.cannontech.database.data.lite.LiteComparators.liteStringComparator );
		java.util.List points = null;
		
		if( showPoints )
		{
			points = cache.getAllGraphTaggedPoints();
			java.util.Collections.sort( points, com.cannontech.database.data.lite.LiteComparators.litePointDeviceIDComparator );
		}

		DBTreeNode rootNode = (DBTreeNode) getRoot();
		rootNode.removeAllChildren();
		
		int deviceDevID;
		int deviceClass;
		for( int i = 0; i < devices.size(); i++ )
		{
			deviceClass = ((com.cannontech.database.data.lite.LiteYukonPAObject)devices.get(i)).getPaoClass();
			
			if( com.cannontech.database.data.pao.DeviceClasses.isCoreDeviceClass(deviceClass) )
			{
				DBTreeNode deviceNode = new DBTreeNode( devices.get(i));	
				rootNode.add( deviceNode );

				if( showPoints )
				{
					deviceDevID = ((com.cannontech.database.data.lite.LiteYukonPAObject)devices.get(i)).getYukonID();
					boolean pointsFound = false;
					for( int j = 0; j < points.size(); j++ )
					{
						if( ((com.cannontech.database.data.lite.LitePoint)points.get(j)).getPaobjectID() == deviceDevID 
							&& ( (com.cannontech.database.data.lite.LitePoint)points.get(j)).getTags() == includePoints)
						{
							pointsFound = true;
							deviceNode.add( new DBTreeNode( points.get(j)) );
						}
						else if( pointsFound )  // used to optimize the iterations
						{						
							j = points.size();
							break;
						}					
					}
					if( !pointsFound)
					{
//						System.out.println( " REMOVE MY DEVICE");
						rootNode.remove(deviceNode);
					}
				}
				
			}
		}
	}

	reload();
}
/**
 * This method was created in VisualAge.
 */
/*
public void update(long includedPoints)
{
	com.cannontech.database.cache.DefaultDatabaseCache cache =
					com.cannontech.database.cache.DefaultDatabaseCache.getInstance();

	synchronized(cache)
	{
		java.util.List devices = cache.getAllDevices();
		java.util.Collections.sort( devices, com.cannontech.database.data.lite.LiteComparators.liteStringComparator );
		java.util.List points = null;
		
		if( showPoints )
		{
			points = cache.getAllGraphTaggedPoints();
			java.util.Collections.sort( points, com.cannontech.database.data.lite.LiteComparators.litePointDeviceIDComparator );
		}

		DBTreeNode rootNode = (DBTreeNode) getRoot();
		rootNode.removeAllChildren();
		
		int deviceDevID;
		int deviceClass;
		for( int i = 0; i < devices.size(); i++ )
		{
			deviceClass = ((com.cannontech.database.data.lite.LiteYukonPAObject)devices.get(i)).getPaoClass();
			
			if( com.cannontech.database.data.pao.DeviceClasses.isCoreDeviceClass(deviceClass) )
			{
				DBTreeNode deviceNode = new DBTreeNode( devices.get(i));	
				//rootNode.add( deviceNode );

				if( showPoints )
				{
					deviceDevID = ((com.cannontech.database.data.lite.LiteYukonPAObject)devices.get(i)).getYukonID();
					boolean pointsFound = false;
					for( int j = 0; j < points.size(); j++ )
					{
						if( ((com.cannontech.database.data.lite.LitePoint)points.get(j)).getPaobjectID() == deviceDevID 
							&& ( (com.cannontech.database.data.lite.LitePoint)points.get(j)).getTags() == includedPoints)
						{
							//pointsFound = true;
							rootNode.add( deviceNode );							
							deviceNode.add( new DBTreeNode( points.get(j)) );
						}
						//else if( pointsFound )  // used to optimize the iterations
						//{						
							//pointsFound = false;
							//break;
						//}					
					}
				}
				
			}
		}
	}

	reload();
}*/
}
