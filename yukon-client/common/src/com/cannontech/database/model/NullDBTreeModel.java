package com.cannontech.database.model;

/**
 * Insert the type's description here.
 * Creation date: (4/24/2002 2:45:44 PM)
 * @author: 
 */
public class NullDBTreeModel extends DBTreeModel {
/**
 * NullDBTreeModel constructor comment.
 * @param root javax.swing.tree.TreeNode
 */
public NullDBTreeModel()
{
	super( new DBTreeNode("(none)") );
}
/**
 * Insert the method's description here.
 * Creation date: (4/24/2002 2:45:44 PM)
 * @param lb com.cannontech.database.data.lite.LiteBase
 */
public boolean isLiteTypeSupported(int liteType) {
	return false;
}
/**
 * This method was created in VisualAge.
 */
public void update() {}
}
