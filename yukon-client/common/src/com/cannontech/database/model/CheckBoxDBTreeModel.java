/*
 * Created on Jun 15, 2004
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package com.cannontech.database.model;

import java.util.Vector;

/**
 * @author snebben
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public abstract class CheckBoxDBTreeModel extends DBTreeModel
{

	//Contains CheckNodes (hopefully) values.  DOES NOT CONTAIN THE PARENT!!!
	private Vector checkedNodes = null;	
	/**
	 * DBEditorTreeModel constructor comment.
	 * @param root javax.swing.tree.TreeNode
	 */
	public CheckBoxDBTreeModel(javax.swing.tree.TreeNode root) {
		super(root);
	}

//	/* (non-Javadoc)
//	 * @see com.cannontech.database.model.LiteBaseTreeModel#isLiteTypeSupported(int)
//	 */
//	public boolean isLiteTypeSupported(int liteType)
//	{
//		// TODO Auto-generated method stub
//		return false;
//	}
//
//	/* (non-Javadoc)
//	 * @see com.cannontech.database.model.LiteBaseTreeModel#update()
//	 */
//	public void update()
//	{
//		// TODO Auto-generated method stub
//
//	}

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

}
