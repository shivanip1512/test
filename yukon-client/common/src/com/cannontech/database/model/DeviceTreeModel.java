package com.cannontech.database.model;

/**
 * This type was created in VisualAge.
 */
import javax.swing.tree.TreePath;

import com.cannontech.common.util.CtiUtilities;
import com.cannontech.database.data.lite.LiteBase;
import com.cannontech.database.data.lite.LitePoint;

public class DeviceTreeModel extends DBTreeModel 
{
	//the number of device we will put in the tree before painting the tree
	//protected static final int REFRESH_ITEM_COUNT = 1000;
	
	private boolean showPoints = true;

	//a Vector only needed to store temporary things
	private java.util.List pointTempList = new java.util.Vector(20);


	//a mutable lite point used for comparisons
	private static final LitePoint DUMMY_LITE_POINT = 
					new LitePoint(Integer.MIN_VALUE, "**DUMMY**", 0, 0, 0, 0 );
/**
 * DeviceTreeModel constructor comment.
 * @param root javax.swing.tree.TreeNode
 */
public DeviceTreeModel() {
	this( true );
}
/**
 * DeviceTreeModel constructor comment.
 * @param rootNode_ DBTreeNode
 */
public DeviceTreeModel( DBTreeNode rootNode_ ) 
{
	this( true, rootNode_ );
}
/**
 * DeviceTreeModel constructor comment.
 * @param root javax.swing.tree.TreeNode
 */
public DeviceTreeModel( boolean showPointNodes )
{
	this( showPointNodes, new DBTreeNode("Devices") );
}

/**
 * DeviceTreeModel constructor comment.
 * @param root javax.swing.tree.TreeNode
 */
public DeviceTreeModel( boolean showPointNodes, DBTreeNode rootNode_ )
{
	super( rootNode_ );
	showPoints = showPointNodes;
}

/**
 * Allows ease of overriding type of node in tree model.
 * Override this method when entending DeviceTreeModel
 */
protected DBTreeNode getNewNode(Object obj)
{
	return new DBTreeNode(obj);
}
/**
 * Insert the method's description here.
 * Creation date: (2/27/2002 10:17:05 AM)
 * @param lp com.cannontech.database.data.lite.LitePoint
 * @param dTreeNode com.cannontech.database.model.DummyTreeNode
 */
protected DBTreeNode addDummyTreeNode(com.cannontech.database.data.lite.LitePoint lp, 
					DBTreeNode node, String text, DBTreeNode deviceNode ) 
{
	if( node == null)
	{
		DummyTreeNode retNode = new DummyTreeNode(text);

		int indx = -1;
		for( int i = 0 ; i < deviceNode.getChildCount(); i++ )
			if( deviceNode.getChildAt(i).equals(retNode) )
			{
				indx = i;
				break;
			}
				
		if( indx >= 0 )
			node = (DummyTreeNode)deviceNode.getChildAt(indx);
		else
			node = retNode;
	}
		

	node.add( getNewNode(lp) );
	//updateTreeNodeStructure( node );

	return node;
}
/**
 * Insert the method's description here.
 * Creation date: (4/19/2002 1:35:08 PM)
 * @param deviceNode DBTreeNode
 */
private void addPoints(DBTreeNode deviceNode )
{
	//type nodes of point types
	DBTreeNode anNode = null, stNode = null;
	DBTreeNode accDmndNode = null, accPulsNode = null;
	DBTreeNode calcNode = null;

	
	//the points in the pointList are added to the device node
	//pseudo points are added to the end of the list if sorting by point offset
	//if sorting by name, all points are added in alphabetical order, regardless if pseudo points
	for (int j = 0; j < pointTempList.size(); j++)
	{
		LitePoint lp = (LitePoint) pointTempList.get(j);

		if( lp.getPointType() == com.cannontech.database.data.point.PointTypes.ANALOG_POINT)
		{
			anNode = addDummyTreeNode( lp, anNode, "Analog", deviceNode );
		}
		else if( lp.getPointType() == com.cannontech.database.data.point.PointTypes.STATUS_POINT)
		{
			stNode = addDummyTreeNode( lp, stNode, "Status", deviceNode );
		}
		else if ( lp.getPointType() == com.cannontech.database.data.point.PointTypes.PULSE_ACCUMULATOR_POINT)
		{
			accPulsNode = addDummyTreeNode( lp, accPulsNode, "Pulse Accumulator", deviceNode );
		}
		else if ( lp.getPointType() == com.cannontech.database.data.point.PointTypes.DEMAND_ACCUMULATOR_POINT)
		{
			accDmndNode = addDummyTreeNode( lp, accDmndNode, "Demand Accumulator", deviceNode );
		}
		else if ( lp.getPointType() == com.cannontech.database.data.point.PointTypes.CALCULATED_POINT
				|| lp.getPointType() == com.cannontech.database.data.point.PointTypes.CALCULATED_STATUS_POINT )
		{
			calcNode = addDummyTreeNode( lp, calcNode, "Calculated", deviceNode );
		}
	}

	//finally, add typeNodes to the device -- added here to ensure they are 
	//  added in the same order every time
	//if a type node is null, it means there are no points of that 
	//  type and the type node will not be added
	if (anNode != null)
		deviceNode.add(anNode);
	if (stNode != null)
		deviceNode.add(stNode);
	if (accPulsNode != null)
		deviceNode.add(accPulsNode);
	if(accDmndNode != null)
		deviceNode.add(accDmndNode);	
	if (calcNode != null)
		deviceNode.add(calcNode);

	//pointList is cleared - only points associated with the current device are held in here
	pointTempList.clear();

}
/**
 * Insert the method's description here.
 * Creation date: (2/27/2002 10:37:56 AM)
 * @param points java.util.List
 * @param destList java.util.Vector
 */
private boolean createDevicePointList(java.util.List points, java.util.List destList, int deviceDevID )
{
	//searches and sorts the list!
	CtiUtilities.binarySearchRepetition( 
					points,
					DUMMY_LITE_POINT, //must have the needed DeviceID set!!
					com.cannontech.database.data.lite.LiteComparators.litePointDeviceIDComparator,
					destList );
						
	for( int i = destList.size()-1; i >= 0; i-- )
	{
		com.cannontech.database.data.lite.LitePoint lp = (LitePoint)destList.get(i);
		if( !isPointValid(lp) )
			destList.remove(i);
	}

	return destList.size() > 0;
}
/**
 * Insert the method's description here.
 * Creation date: (4/24/2002 9:24:01 AM)
 * @return java.util.List
 */
/* This method will return what List of LiteYukonPAObjects we 
   want to use.  Example, a device tree model will use :
	   cache.getAllDevices()
   and a LoadManagement tree model will use:
	   cache.getAllLoadManagement()

	Override this method when using a differnt List
	*/
   
protected synchronized java.util.List getCacheList(
		com.cannontech.database.cache.DefaultDatabaseCache cache ) 
{
	return cache.getAllDevices();
}

protected boolean isPointValid( LitePoint lp )
{
   return true; //all points are valid by default
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
		
	if( lb instanceof LitePoint )
	{
		int devID = ((LitePoint)lb).getPaobjectID();

		com.cannontech.database.data.lite.LiteYukonPAObject pao =
				com.cannontech.database.cache.functions.PAOFuncs.getLiteYukonPAO( devID );

		rootNode = findLiteObject( null, pao );

		if( rootNode != null )
		{

			//this will force us to reload ALL the points for this PAOBject
			rootNode.setWillHaveChildren(true);
			TreePath rootPath = new TreePath( rootNode );
			treePathWillExpand( rootPath );

			updateTreeNodeStructure( rootNode );
			
/*       //lock our point list down
         synchronized( pointTempList )
         {
            pointTempList.clear();
            pointTempList.add( lb );

            //add this point and maybe its point type to the deviceNode
            addPoints( rootNode );
         }

         updateTreeNodeStructure( rootNode );
*/
			return true;
		}

	}
	else if ( lb instanceof com.cannontech.database.data.lite.LiteYukonPAObject )
	{
		com.cannontech.database.data.lite.LiteYukonPAObject liteYuk =
				(com.cannontech.database.data.lite.LiteYukonPAObject)lb;

		if( isDeviceValid(liteYuk.getCategory(), liteYuk.getPaoClass(), liteYuk.getType() ) )
		{
			DBTreeNode node = getNewNode(lb);

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
/* Other models should override this method to return the condition
	they want to use */
public boolean isDeviceValid( int category_, int class_, int type_ )
{
	return com.cannontech.database.data.pao.DeviceClasses.isCoreDeviceClass(class_)
				&& category_ == com.cannontech.database.data.pao.PAOGroups.CAT_DEVICE 
				&& type_ != com.cannontech.database.data.pao.PAOGroups.MCTBROADCAST;
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
/**
 * Insert the method's description here.
 * Creation date: (4/16/2002 5:16:19 PM)
 */
// Override me if you want a sub class to do something different.
protected synchronized void runUpdate() 
{
	com.cannontech.database.cache.DefaultDatabaseCache cache =
		com.cannontech.database.cache.DefaultDatabaseCache.getInstance();

	synchronized (cache)
	{
		java.util.List devices = getCacheList(cache);
		java.util.Collections.sort(devices, com.cannontech.database.data.lite.LiteComparators.liteStringComparator);
		java.util.List points = null;

		if (showPoints)
			points = cache.getAllPoints();

		DBTreeNode rootNode = (DBTreeNode) getRoot();
		rootNode.removeAllChildren();

		int deviceDevID;
		int deviceClass;
		for (int i = 0; i < devices.size(); i++)
		{
			if( isDeviceValid(
					((com.cannontech.database.data.lite.LiteYukonPAObject)devices.get(i)).getCategory(),
					((com.cannontech.database.data.lite.LiteYukonPAObject)devices.get(i)).getPaoClass(),
					((com.cannontech.database.data.lite.LiteYukonPAObject)devices.get(i)).getType() ) )
			{
				DBTreeNode deviceNode = getNewNode(devices.get(i));
				rootNode.add(deviceNode);
				
				if (showPoints)
				{
/*					deviceDevID = ((com.cannontech.database.data.lite.LiteYukonPAObject) devices.get(i)).getYukonID();
					
					//change our dummy points device ID to the current DeviceID
					DUMMY_LITE_POINT.setPaobjectID(deviceDevID);
					
					java.util.Collections.sort(points, com.cannontech.database.data.lite.LiteComparators.litePointDeviceIDComparator);

					int res = java.util.Collections.binarySearch( points, 
						DUMMY_LITE_POINT, 
						com.cannontech.database.data.lite.LiteComparators.litePointDeviceIDComparator );

					if( res >= 0 )
*/
					deviceNode.setWillHaveChildren(true);

				}
				
			}

		} //for loop

	} //synch

	reload();	
}
/**
 * Insert the method's description here.
 * Creation date: (4/22/2002 12:43:37 PM)
 * @param parentNode DBTreeNode
 * @param sortType int
 */
public synchronized void sortChildNodes(DBTreeNode parentNode, int sortType) 
{
	if( parentNode == null )
		return;

	//we only sort points, for now............

	//default the sorting to the POINT_NAME comparator
	java.util.Comparator comp = com.cannontech.database.data.lite.LiteComparators.liteStringComparator;
	if( sortType == SORT_POINT_OFFSET )
		comp = com.cannontech.database.data.lite.LiteComparators.litePointPointOffsetComparator;

	java.util.Vector liteObjects = new java.util.Vector( parentNode.getChildCount() );
	for( int i = 0 ; i < parentNode.getChildCount(); i++ )
		liteObjects.add( ((DBTreeNode)parentNode.getChildAt(i)).getUserObject() );

	java.util.Collections.sort( liteObjects, comp );

	parentNode.removeAllChildren();
	for( int i = 0 ; i < liteObjects.size(); i++ )
		parentNode.add( getNewNode(liteObjects.get(i) ) );

	updateTreeNodeStructure( parentNode );
}
/**
 * This method was created in VisualAge.
 * @return java.lang.String
 */
public String toString() {
	return "Device";
}
/**
 * Insert the method's description here.
 * Creation date: (4/25/2002 12:35:32 PM)
 * @param path javax.swing.tree.TreePath
 */
public synchronized void treePathWillExpand(javax.swing.tree.TreePath path)
{
	if( !showPoints )
		return;
	
	//Watch out, this reloads the points every TIME!!!
	DBTreeNode node = (DBTreeNode)path.getLastPathComponent();

	if( node.willHaveChildren() &&
		 node.getUserObject() instanceof com.cannontech.database.data.lite.LiteYukonPAObject )
	{
		com.cannontech.database.cache.DefaultDatabaseCache cache =
			com.cannontech.database.cache.DefaultDatabaseCache.getInstance();

		synchronized (cache)
		{
			int deviceDevID = ((com.cannontech.database.data.lite.LiteYukonPAObject)node.getUserObject()).getYukonID();
			java.util.List points = cache.getAllPoints();

			//change our dummy points device ID to the current DeviceID
			DUMMY_LITE_POINT.setPaobjectID(deviceDevID);
			
			//lock our point list down
			synchronized( pointTempList )
			{
				node.removeAllChildren();
				pointTempList.clear();
				
				//makes a list of points associated with the current deviceNode
				createDevicePointList( points, pointTempList, deviceDevID );

				//sorts the pointList according to name or offset, (default is set to sort by name)
				java.util.Collections.sort(pointTempList, com.cannontech.database.data.lite.LiteComparators.liteStringComparator);

				//add all points and point types to the deviceNode
				addPoints( node );
			}
		}
	}

	node.setWillHaveChildren(false);
}
/**
 * This method was created in VisualAge.
 */
public synchronized void update()
{
	runUpdate();
}

public void setShowPoints(boolean revealPts)
{
	showPoints = revealPts;
}
}
