package com.cannontech.database.model;

/**
 * This type was created in VisualAge.
 */
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Vector;

import com.cannontech.common.gui.tree.CheckNode;
import com.cannontech.core.dao.PointDao;
import com.cannontech.database.cache.DefaultDatabaseCache;
import com.cannontech.database.data.lite.LiteComparators;
import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.database.data.point.PointType;
import com.cannontech.spring.YukonSpringHook;
import com.cannontech.yukon.IDatabaseCache;

public class DeviceCheckBoxTreeModel extends AbstractDeviceTreeModel implements Checkable
{
	//Contains CheckNodes (hopefully) values.  DOES NOT CONTAIN THE PARENT!!!
	private Vector checkedNodes = null;
    private boolean showPoints = true;
    
    //a Vector only needed to store temporary things
    private java.util.List<LitePoint> pointTempList = new java.util.Vector<LitePoint>(20);
    
    //a mutable lite point used for comparisons
    private static final LitePoint DUMMY_LITE_POINT = new LitePoint(Integer.MIN_VALUE, "**DUMMY**", 0, 0, 0, 0 );
    
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
	 * @param pointNode LitePoint
	 * @param typeNode DBTreeNode
     * @param text String
     * @param deviceNode DBTreeNode
	 */
	protected DBTreeNode addDummyTreeNode(LitePoint pointNode, DBTreeNode typeNode, String text, DBTreeNode deviceNode ) 
	{
		if( typeNode == null)
		{
			DBTreeNode retNode = getNewNode(text);
			
			int indx = -1;
			for( int i = 0 ; i < deviceNode.getChildCount(); i++ )
            {
				if( deviceNode.getChildAt(i).equals(retNode) )
				{
					indx = i;
					break;
				}
            }
			if( indx >= 0 )
            {
                typeNode = (CheckNode)deviceNode.getChildAt(indx);
            }
			else
            {
                typeNode = retNode;
            }
		}
		
        CheckNode newNode = (CheckNode) getNewNode(pointNode);
        LitePoint point = (LitePoint) newNode.getUserObject();
		pointMap.put(point.getLiteID(), newNode);
        typeNode.add(newNode);
        
		return typeNode;
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
        IDatabaseCache cache = DefaultDatabaseCache.getInstance();

        synchronized (cache)
        {
            List devices = getCacheList(cache);
            
            ListIterator deviceIter = devices.listIterator();
            Collections.sort(devices, LiteComparators.liteStringComparator);
            
            deviceMap = new HashMap();
            pointMap = new HashMap();
                                    
            DBTreeNode rootNode = (DBTreeNode) getRoot();
            rootNode.removeAllChildren();
            
            int deviceDevID;
            int deviceClass;
            
            while(deviceIter.hasNext())
            {
                LiteYukonPAObject currentDevice = (LiteYukonPAObject) deviceIter.next();
                
                if( isDeviceValid(currentDevice.getPaoType().getPaoCategory(), currentDevice.getPaoType().getPaoClass(), currentDevice.getPaoType()) )
                {
                    DBTreeNode deviceNode = getNewNode(currentDevice);
                    
                    
                    deviceMap.put( currentDevice.getLiteID(), deviceNode );
                    
                    rootNode.add(deviceNode);
                    
                    deviceNode.setWillHaveChildren(showPoints);
                    
                }

            } //while loop

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
        {
            return;
        }
        //we only sort points, for now............
        //default the sorting to the POINT_NAME comparator
        
        Comparator comp = LiteComparators.liteStringComparator;
        
        if( sortType == SORT_POINT_OFFSET )
        {
            comp = LiteComparators.litePointPointOffsetComparator;
        }
        
        Vector liteObjects = new Vector( parentNode.getChildCount() );
        
        for( int i = 0 ; i < parentNode.getChildCount(); i++ )
        {
            liteObjects.add( ((DBTreeNode)parentNode.getChildAt(i)).getUserObject() );
        }
        
        Collections.sort( liteObjects, comp );
        parentNode.removeAllChildren();

        for( int i = 0 ; i < liteObjects.size(); i++ )
        {
            parentNode.add( getNewNode(liteObjects.get(i) ) );
        }
        
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
            int deviceDevID = ((LiteYukonPAObject)node.getUserObject()).getYukonID();

            //lock our point list down
            synchronized( pointTempList )
            {
                node.removeAllChildren();
                pointTempList.clear();
                pointTempList = YukonSpringHook.getBean(PointDao.class).getLitePointsByPaObjectId(deviceDevID);
                ListIterator<LitePoint> iter = pointTempList.listIterator();
                while(iter.hasPrevious()) {
                    LitePoint p = iter.previous();
                    if(!isPointValid(p)) {
                        iter.remove();
                    }
                }
                
                //sorts the pointList according to name or offset, (default is set to sort by name)
                Collections.sort(pointTempList, LiteComparators.litePointPointOffsetComparator);

                //add all points and point types to the deviceNode
                addPoints( node );
            }   
            // Select the new child nodes if the node is selected
            if (node instanceof CheckNode) {
                ((CheckNode) node).setSelected(((CheckNode) node).isSelected());
            }
        }

        node.setWillHaveChildren(false);
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
            
            if( lp.getPointTypeEnum() == PointType.Analog)
            {
                anNode = addDummyTreeNode( lp, anNode, "Analog", deviceNode );
            }
            else if( lp.getPointTypeEnum() == PointType.Status)
            {
                stNode = addDummyTreeNode( lp, stNode, "Status", deviceNode );
            }
            else if ( lp.getPointTypeEnum() == PointType.PulseAccumulator)
            {
                accPulsNode = addDummyTreeNode( lp, accPulsNode, "Pulse Accumulator", deviceNode );
            }
            else if ( lp.getPointTypeEnum() == PointType.DemandAccumulator)
            {
                accDmndNode = addDummyTreeNode( lp, accDmndNode, "Demand Accumulator", deviceNode );
            }
            else if ( lp.getPointTypeEnum() == PointType.CalcAnalog
                    || lp.getPointTypeEnum() == PointType.CalcStatus)
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