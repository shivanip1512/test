package com.cannontech.database.model;

/**
 * This type was created in VisualAge.
 */

public class DBTreeNode extends javax.swing.tree.DefaultMutableTreeNode 
{
	private boolean willHaveChildren = false;
/**
 * DBEditorTreeNode constructor comment.
 * @param userObject java.lang.Object
 */
public DBTreeNode(Object userObject) {
	super(userObject);
}
/**
 * Insert the method's description here.
 * Creation date: (4/25/2002 1:17:06 PM)
 * @param newWillHaveChildren boolean
 */
public boolean isLeaf() 
{
	if( willHaveChildren() )
		return false;  //act like we have some kids
	else
		return super.isLeaf();
}
/**
 * Insert the method's description here.
 * Creation date: (4/25/2002 1:17:06 PM)
 * @param newWillHaveChildren boolean
 */
public void setWillHaveChildren(boolean newWillHaveChildren) {
	willHaveChildren = newWillHaveChildren;
}
/**
 * Insert the method's description here.
 * Creation date: (4/25/2002 1:17:06 PM)
 * @return boolean
 */
public boolean willHaveChildren() {
	return willHaveChildren;
}
}
