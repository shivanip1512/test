package com.cannontech.database.model;

import com.cannontech.database.data.device.DeviceTypesFuncs;
import com.cannontech.database.data.pao.PAOGroups;

/**
 * This type was created in VisualAge.
 */
public class DeviceTree_CustomPointsModel extends DeviceTreeModel
{
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
	super( showPointNodes, new DBTreeNode("Devices") );
}

public boolean isDeviceValid( int category_, int class_, int type_ )
{
   return( com.cannontech.database.data.pao.DeviceClasses.isCoreDeviceClass( class_ )
            && category_ == PAOGroups.CAT_DEVICE );
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

public void setIncludeUOFMType(long pointUOFMMask)
{
	includePoints = pointUOFMMask;
}

/**
 * This method was created in VisualAge.
 * @return java.lang.String
 */
public String toString() 
{
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

protected boolean isPointValid( com.cannontech.database.data.lite.LitePoint lp )
{
   if( lp == null )
      return false;
   else
      return ( lp.getTags() == includePoints );
}

/**
 * This method was created in VisualAge.
 */
/*public void update() {

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
*/

}
