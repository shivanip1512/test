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
public abstract class CheckBoxDBTreeModel extends DBTreeModel implements Checkable
{
	//Contains CheckNodes (hopefully) values.  DOES NOT CONTAIN THE PARENT!!!
	private Vector checkedNodes = null;
		
	/**
	 * @param root
	 */
	public CheckBoxDBTreeModel(javax.swing.tree.TreeNode root) {
		super(root);
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
}
