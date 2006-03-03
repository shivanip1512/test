package com.cannontech.database.model;

/**
 * This type was created in VisualAge.
 */
import java.util.*;

import javax.swing.tree.TreePath;

import com.cannontech.common.gui.tree.CheckNode;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.database.cache.DefaultDatabaseCache;
import com.cannontech.database.data.lite.*;
import com.cannontech.database.data.point.PointTypes;

public class DeviceCheckBoxTreeModel extends DeviceTreeModel implements Checkable
{
	//Contains CheckNodes (hopefully) values.  DOES NOT CONTAIN THE PARENT!!!
	private Vector checkedNodes = null;
    private boolean showPoints = true;

    //a Vector only needed to store temporary things
    private java.util.List pointTempList = new java.util.Vector(20);

    //a mutable lite point used for comparisons
    private static final LitePoint DUMMY_LITE_POINT = 
                    new LitePoint(Integer.MIN_VALUE, "**DUMMY**", 0, 0, 0, 0 );
    
    private HashMap deviceMap = null;
    private HashMap pointMap = null;
		
	/**
	 * DeviceTreeModel constructor comment.
	 * @param root javax.swing.tree.TreeNode
	 */
	public DeviceCheckBoxTreeModel() {
		this( false );
	}
    
	/**
	 * DeviceTreeModel constructor comment.
	 * @param rootNode_ DBTreeNode
	 */
	public DeviceCheckBoxTreeModel( CheckNode rootNode_ ) 
	{
		this( false, rootNode_ );
	}
    
	/**
	 * DeviceTreeModel constructor comment.
	 * @param root javax.swing.tree.TreeNode
	 */
	public DeviceCheckBoxTreeModel( boolean showPointNodes )
	{
		this( showPointNodes, new CheckNode("Devices") );
	}
	
	/**
	 * DeviceTreeModel constructor comment.
	 * @param root javax.swing.tree.TreeNode
	 */
	public DeviceCheckBoxTreeModel( boolean showPointNodes, CheckNode rootNode_ )
	{
		super(showPointNodes, rootNode_ );
	}

	/* (non-Javadoc)
	 * @see com.cannontech.database.model.DeviceTreeModel#getNewNode(java.lang.Object)
	 */
	protected DBTreeNode getNewNode(Object obj)
	{
		return new CheckNode(obj);
	}
	
	/**
	 * Insert the method's description here.
	 * Creation date: (2/27/2002 10:17:05 AM)
	 * @param lp com.cannontech.database.data.lite.LitePoint
	 * @param dTreeNode com.cannontech.database.model.DummyTreeNode
	 */
	protected DBTreeNode addDummyTreeNode(LitePoint lp, 
						DBTreeNode node, String text, DBTreeNode deviceNode ) 
	{
		if( node == null)
		{
			DBTreeNode retNode = getNewNode(text);
	
			int indx = -1;
			for( int i = 0 ; i < deviceNode.getChildCount(); i++ )
				if( deviceNode.getChildAt(i).equals(retNode) )
				{
					indx = i;
					break;
				}
					
			if( indx >= 0 )
				node = (CheckNode)deviceNode.getChildAt(indx);
			else
				node = retNode;
		}
		
        CheckNode newNode = (CheckNode) getNewNode(lp);
        LitePoint point = (LitePoint) newNode.getUserObject();
		pointMap.put(point.getLiteID(), newNode);
        node.add(newNode);
        System.out.println("Point added to map: "+ point);
		//node.add( getNewNode(lp) );
		//updateTreeNodeStructure( node );
	
		return node;
	}
	
	/**
	 * @return
	 */
	public Vector getCheckedNodes()
	{
		if( checkedNodes == null)
			checkedNodes = new Vector();
		return checkedNodes;
	}
		
	/**
	 * @param vector
	 */
	public void setCheckedNodes(Vector vector)
	{
		checkedNodes = vector;
	}
    
    /**
     * This method was created in VisualAge.
     */
    public synchronized void update()
    {
        runUpdate();
    }
    
    /**
     * Insert the method's description here.
     * Creation date: (4/16/2002 5:16:19 PM)
     */
    //   Override me if you want a sub class to do something different.
    protected synchronized void runUpdate() 
    {
        com.cannontech.database.cache.DefaultDatabaseCache cache =
            com.cannontech.database.cache.DefaultDatabaseCache.getInstance();

        synchronized (cache)
        {
            java.util.List devices = getCacheList(cache);
            java.util.Collections.sort(devices, com.cannontech.database.data.lite.LiteComparators.liteStringComparator);
            java.util.List points = null;
            
            // build our device map 
            
            deviceMap = new HashMap();
            pointMap = new HashMap();
            
            if (showPoints)
            {
                points = cache.getAllPoints();
            }
            
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
                    
                    LiteYukonPAObject device = (LiteYukonPAObject) devices.get(i);
                    deviceMap.put( device.getLiteID(), deviceNode );
                    
                    rootNode.add(deviceNode);
                    
                    if (showPoints)
                    {
                        deviceDevID = ((com.cannontech.database.data.lite.LiteYukonPAObject) devices.get(i)).getYukonID();
                        
                        //change our dummy points device ID to the current DeviceID
                        DUMMY_LITE_POINT.setPaobjectID(deviceDevID);
                        
                        //for(int i=0;i<points.size();i++)
                        
                        java.util.Collections.sort(points, LiteComparators.litePointDeviceIDComparator);

                        
                        
                        int res = java.util.Collections.binarySearch( points, DUMMY_LITE_POINT, LiteComparators.litePointDeviceIDComparator );

                        if( res >= 0 )

                        deviceNode.setWillHaveChildren(true);
                        TreePath path = new TreePath(deviceNode.getPath());
                        treePathWillExpand(path);
                        //sortChildNodes( deviceNode, SORT_POINT_OFFSET );

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

        if( node.willHaveChildren() && node.getUserObject() instanceof LiteYukonPAObject )
        {
            DefaultDatabaseCache cache = DefaultDatabaseCache.getInstance();

            synchronized (cache)
            {
                int deviceDevID = ((LiteYukonPAObject)node.getUserObject()).getYukonID();
                List points = cache.getAllPoints();

                //change our dummy point's device ID to the current DeviceID
                DUMMY_LITE_POINT.setPaobjectID(deviceDevID);
                
                //lock our point list down
                synchronized( pointTempList )
                {
                    node.removeAllChildren();
                    pointTempList.clear();
                    
                    //makes a list of points associated with the current deviceNode
                    createDevicePointList( points, pointTempList, deviceDevID );

                    //sorts the pointList according to name or offset, (default is set to sort by name)
                    Collections.sort(pointTempList, LiteComparators.litePointPointOffsetComparator);

                    //add all points and point types to the deviceNode
                    addPoints( node );
                }
            }
        }

        node.setWillHaveChildren(false);
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

            if( lp.getPointType() == PointTypes.ANALOG_POINT)
            {
                anNode = addDummyTreeNode( lp, anNode, "Analog", deviceNode );
            }
            else if( lp.getPointType() == PointTypes.STATUS_POINT)
            {
                stNode = addDummyTreeNode( lp, stNode, "Status", deviceNode );
            }
            else if ( lp.getPointType() == PointTypes.PULSE_ACCUMULATOR_POINT)
            {
                accPulsNode = addDummyTreeNode( lp, accPulsNode, "Pulse Accumulator", deviceNode );
            }
            else if ( lp.getPointType() == PointTypes.DEMAND_ACCUMULATOR_POINT)
            {
                accDmndNode = addDummyTreeNode( lp, accDmndNode, "Demand Accumulator", deviceNode );
            }
            else if ( lp.getPointType() == PointTypes.CALCULATED_POINT
                    || lp.getPointType() == PointTypes.CALCULATED_STATUS_POINT )
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
    
    // get a node from the HashTable by device id
    public CheckNode getDevicebyID(int id)
    {
        CheckNode node = (CheckNode)deviceMap.get(id);
        
        return node;
    }
    
    // get a node from the HashTable by point id
    public CheckNode getPointbyID(int id)
    {
        CheckNode node = (CheckNode)pointMap.get(id);
        
        return node;
    }
    
}