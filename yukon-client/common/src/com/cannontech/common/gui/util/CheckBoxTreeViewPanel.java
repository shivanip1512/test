package com.cannontech.common.gui.util;

import com.cannontech.common.gui.tree.CheckNodeSelectionListener;

/**
 * This type was created in VisualAge.
 */

public class CheckBoxTreeViewPanel extends TreeViewPanel
{
	private CheckNodeSelectionListener nodeListener = null;
	
	/**
	 * TreeViewPanel constructor comment.
	 */
	public CheckBoxTreeViewPanel() {
		super();
	//	getTree().getSelectionModel().setSelectionMode(javax.swing.tree.TreeSelectionModel.SINGLE_TREE_SELECTION );
		getTree().setCellRenderer( new com.cannontech.common.gui.tree.CheckRenderer() );
		getTree().addMouseListener(getNodeListener());
	
	}
	
	
	private CheckNodeSelectionListener getNodeListener()
	{
		if( nodeListener == null )
			nodeListener = new CheckNodeSelectionListener( getTree() );
			
		return nodeListener;
	}
}