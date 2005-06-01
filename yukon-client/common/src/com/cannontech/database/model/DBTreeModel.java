package com.cannontech.database.model;

/**
 * This type was created in VisualAge.
 */
import javax.swing.tree.TreePath;

import com.cannontech.database.data.lite.LiteBase;

public abstract class DBTreeModel extends javax.swing.tree.DefaultTreeModel implements LiteBaseTreeModel
{
	public static final int SORT_POINT_NAME = 0;
	public static final int SORT_POINT_OFFSET = 1;
	

/**
 * DBEditorTreeModel constructor comment.
 * @param root javax.swing.tree.TreeNode
 */
public DBTreeModel(javax.swing.tree.TreeNode root) {
	super(root);
}


/**
 * Insert the method's description here.
 * Creation date: (4/18/2002 2:20:48 PM)
 * @return DBTreeNode
 * @param lb com.cannontech.database.data.lite.LiteBase
 */
protected DBTreeNode findLiteObject( TreePath path, LiteBase lb)
{
	if( path == null )
		path = new TreePath( getRoot() );
		
	DBTreeNode node = (DBTreeNode) path.getLastPathComponent();
	
	if( lb == null || !isLiteTypeSupported(lb.getLiteType()) )
	{
		return null;
	}
	if( node.getUserObject().equals(lb) )
	{
		return node;
	}
	else
	if( node.getChildCount() == 0 )
	{
		return null;
	}
	else
	{
		for( int i = 0; i < node.getChildCount(); i++ )
		{
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
		
	DBTreeNode node = new DBTreeNode(lb);

	//add all new tree nodes to the top, for now
	int[] ind = { 0 };
	
	rootNode.insert( node, ind[0] );
	
	nodesWereInserted(
		rootNode,
		ind );

	return true;	
}
/**
 * Insert the method's description here.
 * Creation date: (4/17/2002 1:58:45 PM)
 * @param lite com.cannontech.database.data.lite.LiteBase
 */
public boolean removeTreeObject(LiteBase lb) 
{
	if( lb == null || !isLiteTypeSupported(lb.getLiteType()) )
		return false;

java.util.Date s = new java.util.Date();

	DBTreeNode node = findLiteObject( null, lb );
	
com.cannontech.clientutils.CTILogger.info("*** REMOVE Took " + 
	(new java.util.Date().getTime() - s.getTime()) * .001 + 
	" seconds to find node in DBtreeModel, node = " + node );

	if( node != null )
	{
	
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
/**
 * Insert the method's description here.
 * Creation date: (4/22/2002 12:43:37 PM)
 * @param parentNode DBTreeNode
 * @param sortType int
 */
public void sortChildNodes(DBTreeNode parentNode, int sortType) 
{
	//this method is not implemented so sub classes will not do anything.
	//If a sub class wants to do sorting, override this method
}
/**
 * Insert the method's description here.
 * Creation date: (4/25/2002 12:35:32 PM)
 * @param path javax.swing.tree.TreePath
 */
public void treePathWillExpand(javax.swing.tree.TreePath path)
{
	//do nothing for our default implementation
}
/**
 * Insert the method's description here.
 * Creation date: (4/17/2002 1:58:45 PM)
 * @param DefaultMutableTreeNode
 */
protected void updateTreeNodeStructure(javax.swing.tree.TreeNode node) 
{
	if( node == null )
		return;

	nodeStructureChanged( node );
}
/**
 * Insert the method's description here.
 * Creation date: (4/17/2002 1:58:45 PM)
 * @param lite com.cannontech.database.data.lite.LiteBase
 */
public boolean updateTreeObject(LiteBase lb) 
{
	if( lb == null || !isLiteTypeSupported(lb.getLiteType()) )
		return false;

java.util.Date s = new java.util.Date();
	DBTreeNode node = findLiteObject( null, lb );

com.cannontech.clientutils.CTILogger.info("*** UPDATE Took " + 
	(new java.util.Date().getTime() - s.getTime()) * .001 + 
	" seconds to find node in DBtreeModel, node = " + node);


	if( node != null )
	{
		nodeChanged( node );
		return true;			
	}

	return false;
}

/**
 * This method was created in VisualAge.
 * @return java.lang.String
 */
public String toString() {
    return ModelFactory.getModelString(this.getClass());
}
}
