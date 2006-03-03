package com.cannontech.database.model;

/**
 * This type was created in VisualAge.
 */
import java.util.HashMap;
import java.util.Vector;

import com.cannontech.common.gui.tree.CheckNode;
import com.cannontech.database.cache.DefaultDatabaseCache;
import com.cannontech.database.data.lite.LiteAlarmCategory;
import com.cannontech.database.data.lite.LiteYukonPAObject;

public class AlarmCategoryCheckBoxTreeModel extends DBTreeModel implements Checkable
{
    //Contains CheckNodes (hopefully) values.  DOES NOT CONTAIN THE PARENT!!!
    private Vector checkedNodes = null;
    private HashMap alarmCategoryMap = null;
    
    /**
     * DeviceTreeModel constructor comment.
     * @param root javax.swing.tree.TreeNode
     */   
    public AlarmCategoryCheckBoxTreeModel(  CheckNode rootNode_ )
    {
        super( rootNode_ );
    }
    
    public AlarmCategoryCheckBoxTreeModel()
    {
        super( new CheckNode("Categories") );
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
     * @param ac com.cannontech.database.data.lite.LitePoint
     * @param dTreeNode com.cannontech.database.model.DummyTreeNode
     */
    protected DBTreeNode addDummyTreeNode(LiteAlarmCategory ac, 
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
            
    
        node.add( getNewNode(ac) );
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
    public boolean isLiteTypeSupported(int liteType) {
        return false;
    }
    public void update() {
        runUpdate();
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
        return cache.getAllAlarmCategories();
    }
    
    /**
     * Insert the method's description here.
     * Creation date: (4/16/2002 5:16:19 PM)
     */
//     Override me if you want a sub class to do something different.
    protected synchronized void runUpdate() 
    {
        DefaultDatabaseCache cache = DefaultDatabaseCache.getInstance();

        synchronized (cache)
        {
            java.util.List alarmCats = getCacheList(cache);
            java.util.Collections.sort(alarmCats, com.cannontech.database.data.lite.LiteComparators.liteStringComparator);
            
            // build our hash map of alarm categories on creation
            alarmCategoryMap = new HashMap();
            
            DBTreeNode rootNode = (DBTreeNode) getRoot();
            rootNode.removeAllChildren();

            int deviceDevID;
            int deviceClass;
            for (int i = 0; i < alarmCats.size(); i++)
            {
                DBTreeNode deviceNode = getNewNode(alarmCats.get(i));
                rootNode.add(deviceNode);
                LiteAlarmCategory alarmCategory = (LiteAlarmCategory) alarmCats.get(i);
                alarmCategoryMap.put( alarmCategory.getLiteID(), deviceNode );
            } //for loop

        } //synch
        reload();   
    }
    
    //  get a node from the HashTable by alarm category id
    public CheckNode getAlarmCategorybyID(int id)
    {
        CheckNode node = (CheckNode)alarmCategoryMap.get(id);
        
        return node;
    }
    
}
