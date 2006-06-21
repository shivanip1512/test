package com.cannontech.database.model;
import java.util.Collections;
import java.util.List;

import com.cannontech.database.cache.DefaultDatabaseCache;
import com.cannontech.database.data.lite.LiteBase;
import com.cannontech.database.data.lite.LiteComparators;
import com.cannontech.database.data.lite.LiteTypes;
import com.cannontech.yukon.IDatabaseCache;

public class SystemDeviceModel extends DBTreeModel 
{
    
    /**
     * DeviceTreeModel constructor comment.
     * @param root javax.swing.tree.TreeNode
     */
    public SystemDeviceModel()
    {
        super( new DBTreeNode("System Device") );
    }
    
    /**
     * Insert the method's description here.
     * Creation date: (4/25/2002 12:35:32 PM)
     * @param path javax.swing.tree.TreePath
     */
    public synchronized void treePathWillExpand(javax.swing.tree.TreePath path)
    {
        
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
     * Creation date: (4/22/2002 4:11:23 PM)
     * @param deviceType int
     */
    public boolean isDeviceValid()
    {
         return true;
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
        
//        if( lb instanceof LitePoint )
//        {
//
//            if( rootNode != null )
//            {
//
//                //this will force us to reload ALL the points for this PAOBject
//                rootNode.setWillHaveChildren(true);
//                TreePath rootPath = new TreePath( rootNode );
//                treePathWillExpand( rootPath );
//
//                updateTreeNodeStructure( rootNode );
//
//                return true;
//            }
//
//        }
//        else if ( lb instanceof com.cannontech.database.data.lite.LiteYukonPAObject )
//        {
//            LiteYukonPAObject liteYuk = (LiteYukonPAObject)lb;

                DBTreeNode node = getNewNode(lb);

                //add all new tree nodes to the top, for now
                int[] ind = { 0 };
                
                rootNode.insert( node, ind[0] );
                
                nodesWereInserted( rootNode, ind );

                return true;
//            }
//        return false;
    }
    
    /**
     * Insert the method's description here.
     * Creation date: (4/24/2002 9:24:01 AM)
     * @return java.util.List
     */
    protected synchronized List getCacheList(IDatabaseCache cache ) 
    {
        return cache.getAllSystemPoints();
    }
    
    /**
     * Allows ease of overriding type of node in tree model.
     * Override this method when entending DeviceTreeModel
     */
    protected DBTreeNode getNewNode(Object obj)
    {
        return new DBTreeNode(obj);
    }
    
    public boolean isLiteTypeSupported(int liteType) {
        return ( liteType == LiteTypes.YUKON_PAOBJECT || liteType == LiteTypes.POINT );
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
    protected synchronized void runUpdate() 
    {
        IDatabaseCache cache = DefaultDatabaseCache.getInstance();

        synchronized (cache)
        {
            List points = getCacheList(cache);
            Collections.sort(points, LiteComparators.liteStringComparator);
            
            DBTreeNode rootNode = (DBTreeNode) getRoot();
            rootNode.removeAllChildren();
            
            int deviceDevID;
            int deviceClass;
            for (int i = 0; i < points.size(); i++)
            {
                DBTreeNode pointNode = getNewNode(points.get(i));
                rootNode.add(pointNode);    
            }

        }

        reload();   
    }

}
