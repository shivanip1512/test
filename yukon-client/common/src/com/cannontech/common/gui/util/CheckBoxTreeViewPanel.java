package com.cannontech.common.gui.util;

import java.util.Vector;

import com.cannontech.common.gui.tree.CheckNodeSelectionListener;

/**
 * This type was created in VisualAge.
 */

public class CheckBoxTreeViewPanel extends TreeViewPanel
{
	private CheckNodeSelectionListener nodeListener = null;
	private Vector checkedNodes = null;

	/**
	 * TreeViewPanel constructor comment.
	 */
	public CheckBoxTreeViewPanel(){
		super();
	//	getTree().getSelectionModel().setSelectionMode(javax.swing.tree.TreeSelectionModel.SINGLE_TREE_SELECTION );
		getTree().setCellRenderer( new com.cannontech.common.gui.tree.CheckRenderer() );
		getTree().addMouseListener(getNodeListener());
	}	

	private CheckNodeSelectionListener getNodeListener()
	{
		if( nodeListener == null )
			nodeListener = new CheckNodeSelectionListener( getTree(), getCheckedNodes() );
			
		return nodeListener;
	}
	
	/**
	 * @return
	 */
	public Vector getCheckedNodes()
	{
		if(checkedNodes == null)
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