package com.cannontech.common.gui.tree;

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Vector;

import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.JTree;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;

/**
 * @author rneuharth
 * Added vector to hold all checked (NON PARENT) nodes. SN 6-03-04
 */
public class CheckNodeSelectionListener extends MouseAdapter
{
	private JTree tree = null;

	//Contains LiteBase (hopefully) values.  DOES NOT CONTAIN THE PARENT!!!
	private Vector checkedNodes = null;

	public CheckNodeSelectionListener(JTree tree, Vector checkedNodes)
	{
		super();
		this.tree = tree;
		this.checkedNodes = checkedNodes;
	}

	/* (non-Javadoc)
	 * @see java.awt.event.MouseListener#mouseClicked(java.awt.event.MouseEvent)
	 */
	public void mouseClicked(MouseEvent e)
	{
		int x = e.getX();
		int y = e.getY();
		int row = tree.getRowForLocation(x, y);
		TreePath path = tree.getPathForRow(row);

		//only do events if the actual check box is selected
		if (!isCheckBoxSelected(e.getPoint(), tree.getRowBounds(row)))
			return;

		if (path != null && path.getLastPathComponent() instanceof CheckNode)
		{
			CheckNode node = (CheckNode) path.getLastPathComponent();

			boolean doSelect = !node.isSelected();

			//be sure we are an editable node
			if (!node.isSystemReserved())
				selectNode(node, doSelect, row);

			// I need revalidate if node is root.  but why?
			if (row == 0)
			{
				tree.revalidate();
				tree.repaint();
			}
		}

		super.mouseClicked(e);
	}

	/**
	 * takes a mouse clicks point and the rows visible rectangle attribute to know
	 */
	private boolean isCheckBoxSelected(Point pt, Rectangle visRowRect_)
	{
		if (visRowRect_ == null || pt == null)
			return false;

		JPanel rend = (JPanel) tree.getCellRenderer();
		int checkW = 0;
		for (int i = 0; i < rend.getComponentCount(); i++)
			if (rend.getComponent(i) instanceof JCheckBox)
			{
				checkW = rend.getComponent(i).getWidth();
				break;
			}

		return pt.getX() >= visRowRect_.getLocation().getX()
			&& pt.getX() <= (checkW + visRowRect_.getLocation().getX());
	}

	/**
	 * Puts a check mark in the box for the selected item and all of it's children 
	 * using the node.setSelected(...) function.
	 * @param node
	 * @param selected
	 * @param row
	 */
	private void selectNode(CheckNode node, boolean selected, int row)
	{
		node.setSelected(selected, checkedNodes);

		((DefaultTreeModel) tree.getModel()).nodeChanged(node);
	}
}
