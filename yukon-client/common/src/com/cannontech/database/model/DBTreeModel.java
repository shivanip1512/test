package com.cannontech.database.model;

/**
 * This type was created in VisualAge.
 */
import javax.swing.tree.TreePath;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.database.data.lite.LiteBase;

public abstract class DBTreeModel extends javax.swing.tree.DefaultTreeModel implements LiteBaseTreeModel
{
	public static final int SORT_POINT_NAME = 0;
	public static final int SORT_POINT_OFFSET = 1;
	

    public DBTreeModel(javax.swing.tree.TreeNode root) {
    	super(root);
    }

    protected DBTreeNode findLiteObject( TreePath path, LiteBase lb)
    {
    	if( path == null )
    		path = new TreePath( getRoot() );
    		
    	DBTreeNode node = (DBTreeNode) path.getLastPathComponent();
    	
    	if( lb == null || !isLiteTypeSupported(lb.getLiteType()) ) {
    		return null;
    	}
    	if( node.getUserObject().equals(lb) ) {
    		return node;
    	}
    	else
    	if( node.getChildCount() == 0 ) {
    		return null;
    	}
    	else {
    		for( int i = 0; i < node.getChildCount(); i++ ) {
    			Object nextPathObjs[] = new Object[path.getPath().length +1];
    
    			System.arraycopy( path.getPath(), 0, nextPathObjs, 0, path.getPath().length );
    
    			nextPathObjs[path.getPath().length] = node.getChildAt(i);
    			
    			TreePath nextPath = new TreePath(nextPathObjs);
    
    			DBTreeNode tempNode = findLiteObject(nextPath,lb);
    			if( tempNode != null )
    				return tempNode;
    		}
    
    		//unable to find the node!!
    		return null;
    	}
    }
    
    public boolean insertTreeObject( LiteBase lb ) {
    	if( lb == null || !isLiteTypeSupported(lb.getLiteType()) )
    		return false;
    
    	DBTreeNode rootNode = (DBTreeNode) getRoot();
    	LiteBase liteBase = convertLiteBase(lb);
    	DBTreeNode node = new DBTreeNode(liteBase);
    
    	//add all new tree nodes to the top, for now
    	int[] ind = { 0 };
    	
    	rootNode.insert( node, ind[0] );
    	
    	nodesWereInserted(
    		rootNode,
    		ind );
    
    	return true;	
    }
    
    public boolean removeTreeObject(LiteBase lb) {
    	if( lb == null || !isLiteTypeSupported(lb.getLiteType()) )
    		return false;
    
    	java.util.Date s = new java.util.Date();
    
    	//Don't call convertLiteBase, it may return null if the device is already deleted.
        //Just remove the liteBase object as it exists.
    	DBTreeNode node = findLiteObject( null, lb);
    	
    	CTILogger.info("*** REMOVE Took " +
    	               (new java.util.Date().getTime() - s.getTime()) * .001 +
    	               " seconds to find node in DBtreeModel, node = " + node );
    
    	if( node != null ) {
    	
    		int[] ind = { node.getParent().getIndex(node) };
    		Object[] delArr = { node };
    			
    		nodesWereRemoved(
    			node.getParent(),
    			ind,
    			delArr );
    
    		//remove the nodes data
    		((DBTreeNode)node.getParent()).remove( node );
    
    		return true;
    	}
    
    	return false;
    }
    
    public void sortChildNodes(DBTreeNode parentNode, int sortType) {
    	//this method is not implemented so sub classes will not do anything.
    	//If a sub class wants to do sorting, override this method
    }
    
    public void treePathWillExpand(javax.swing.tree.TreePath path) {
    	//do nothing for our default implementation
    }
    
    protected void updateTreeNodeStructure(javax.swing.tree.TreeNode node) {
    	if( node == null )
    		return;
    
    	nodeStructureChanged( node );
    }
    
    public boolean updateTreeObject(LiteBase lb) {
    	
        if( lb == null || !isLiteTypeSupported(lb.getLiteType()) )
    		return false;
    
    	LiteBase liteBase = convertLiteBase(lb);
    	java.util.Date s = new java.util.Date();
    	DBTreeNode node = findLiteObject( null, liteBase);
    
    	CTILogger.info("*** UPDATE Took " +
    	               (new java.util.Date().getTime() - s.getTime()) * .001 +
    	               " seconds to find node in DBtreeModel, node = " + node);
    
    	if( node != null ) {
            node.setUserObject(liteBase);
    		nodeChanged( node );
    		return true;			
    	}
    
    	return false;
    }
    
    /**
     * Returns the converted liteBase object.  Override this method if the tree model
     * is not based on LiteYukonPaobjects (such as the DeviceMeterGroupModel which uses
     * LiteDeviceMeterNumber objects instead).
     * @param lb
     * @return
     */
    protected LiteBase convertLiteBase(LiteBase lb){
        return lb;
    }
    
    public String toString() {
        return ModelFactory.getModelString(this.getClass());
    }
}
