package com.cannontech.common.gui.util;

import com.cannontech.database.model.DBTreeNode;

/**
 * This type was created in VisualAge.
 */
public class CtiTreeCellRenderer implements javax.swing.tree.TreeCellRenderer {
	private static SimpleLabel selectedLabel = new com.cannontech.common.gui.util.SimpleLabel("I am the simple Label", new java.awt.Font("dialog", 0, 12), java.awt.Color.blue, java.awt.Color.white);
	private static SimpleLabel unselectedLabel = new com.cannontech.common.gui.util.SimpleLabel("I am the simple Label", new java.awt.Font("dialog", 0, 12), java.awt.Color.white, java.awt.Color.black);
	private static SimpleLabel selectedBoldLabel = new com.cannontech.common.gui.util.SimpleLabel("I am the simple Label", new java.awt.Font("dialog", java.awt.Font.BOLD, 12), java.awt.Color.blue, java.awt.Color.white);
	private static SimpleLabel unselectedBoldLabel = new com.cannontech.common.gui.util.SimpleLabel("I am the simple Label", new java.awt.Font("dialog", java.awt.Font.BOLD, 12), java.awt.Color.white, java.awt.Color.black);	

	//private static SimpleLabel selectedRdOnlyLabel = new com.cannontech.common.gui.util.SimpleLabel("I am the simple Label", new java.awt.Font("dialog", 0, 12), java.awt.Color.blue, java.awt.Color.white);
	private static SimpleLabel unselectedRdOnlyLabel = new com.cannontech.common.gui.util.SimpleLabel("I am the simple Label", new java.awt.Font("dialog", 0, 12), java.awt.Color.white, java.awt.Color.lightGray.darker());	

/**
 * CtiTreeCellRenderer constructor comment.
 */
public CtiTreeCellRenderer() {
	super();

	unselectedRdOnlyLabel.setToolTipText("System reserved tree node");
}

/**
 * getTreeCellRendererComponent method comment.
 */
public java.awt.Component getTreeCellRendererComponent(
	javax.swing.JTree tree,
	Object value,
	boolean selected,
	boolean expanded,
	boolean leaf,
	int row,
	boolean hasFocus)
{

	if (value instanceof com.cannontech.database.model.DummyTreeNode)
	{

		if (selected)
		{
			selectedBoldLabel.setText(value.toString());
			return selectedBoldLabel;
		}
		else
		{
			unselectedBoldLabel.setText(value.toString());
			return unselectedBoldLabel;
		}
	}


	if (selected)
	{
		selectedLabel.setText(value.toString());
		return selectedLabel;
	}
	else
	{
		if (value instanceof DBTreeNode )
		{
			if( ((DBTreeNode)value).isSystemReserved() )
			{ 
				unselectedRdOnlyLabel.setText( value.toString() );
				return unselectedRdOnlyLabel;
			}
		}

		unselectedLabel.setText(value.toString());
		return unselectedLabel;
	}
}
}
