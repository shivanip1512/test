package com.cannontech.common.gui.util;

import com.cannontech.common.gui.tree.CheckNodeSelectionListener;
import com.cannontech.common.gui.tree.CheckRenderer;

/**
 * This type was created in VisualAge.
 */

public class CheckBoxTreeViewPanel extends TreeViewPanel
{
	private CheckNodeSelectionListener nodeListener = null;
	
	/**
	 * TreeViewPanel constructor comment.
	 */
	public CheckBoxTreeViewPanel(){
		this(false);
	}	

	/**
	 * TreeViewPanel constructor comment.
	 * boolean storeCheckedNodes - true turns the listener on to record the currently checked nodes.
	 */
	public CheckBoxTreeViewPanel(boolean storeCheckNodes_)
	{
		super();
//		getTree().getSelectionModel().setSelectionMode(TreeSelectionModel.DISCONTIGUOUS_TREE_SELECTION);
		getTree().setCellRenderer( new CheckRenderer() );
		getTree().addMouseListener(getNodeListener());
		getNodeListener().setStoreCheckedNodes(storeCheckNodes_);
	}	
	
	/**
	 * @return
	 */
	private CheckNodeSelectionListener getNodeListener()
	{
		if( nodeListener == null )
			nodeListener = new CheckNodeSelectionListener( getTree());
			
		return nodeListener;
	}

	/**
	 * @param b
	 */
	public void setStoreCheckedNodes(boolean b)
	{
		getNodeListener().setStoreCheckedNodes(b);
	}

}