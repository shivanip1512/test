package com.cannontech.database.model;

/**
 * This type was created in VisualAge.
 */
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Vector;

import com.cannontech.common.gui.tree.CheckNode;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.core.dao.AlarmCatDao;
import com.cannontech.database.data.lite.LiteAlarmCategory;
import com.cannontech.database.data.lite.LiteComparators;
import com.cannontech.spring.YukonSpringHook;

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
    
    // Override me if you want a sub class to do something different.
    protected synchronized void runUpdate() 
    {
        List<LiteAlarmCategory> alarmCats = YukonSpringHook.getBean(AlarmCatDao.class).getAlarmCategories();
        Collections.sort(alarmCats, LiteComparators.liteBaseIDComparator);
        ListIterator alarmCatsIter = alarmCats.listIterator();
        
        alarmCategoryMap = new HashMap();
        
        DBTreeNode rootNode = (DBTreeNode) getRoot();
        rootNode.removeAllChildren();
        
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
        reload();   
    }
    
    //  get a node from the HashTable by alarm category id
    public CheckNode getAlarmCategorybyID(int id)
    {
        CheckNode node = (CheckNode)alarmCategoryMap.get(id);
        
        return node;
    }
    
}
