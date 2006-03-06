package com.cannontech.database.model;

/**
 * This type was created in VisualAge.
 */
import java.util.*;

import com.cannontech.common.gui.tree.CheckNode;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.database.cache.DefaultDatabaseCache;
import com.cannontech.database.data.lite.*;

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
       
    protected synchronized List getCacheList(DefaultDatabaseCache cache ) 
    {
        return cache.getAllAlarmCategories();
    }
    
    /**
     * Insert the method's description here.
     * Creation date: (4/16/2002 5:16:19 PM)
     */
    // Override me if you want a sub class to do something different.
    protected synchronized void runUpdate() 
    {
        DefaultDatabaseCache cache = DefaultDatabaseCache.getInstance();
        
        synchronized (cache)
        {
            List alarmCats = getCacheList(cache);
            Collections.sort(alarmCats, LiteComparators.liteStringComparator);
            ListIterator alarmCatsIter = alarmCats.listIterator();
            
            alarmCategoryMap = new HashMap();
            
            DBTreeNode rootNode = (DBTreeNode) getRoot();
            rootNode.removeAllChildren();
            
            int deviceDevID;
            int deviceClass;
            
            while ( alarmCatsIter.hasNext())
            {
                LiteAlarmCategory alarmCategory = (LiteAlarmCategory)alarmCatsIter.next();
                // exclude the "(none)" entry
                if( !alarmCategory.getCategoryName().equalsIgnoreCase(CtiUtilities.STRING_NONE))
                {
                    DBTreeNode deviceNode = getNewNode(alarmCategory);
                    rootNode.add(deviceNode);
                    alarmCategoryMap.put( alarmCategory.getLiteID(), deviceNode );
                }
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
