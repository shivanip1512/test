package com.cannontech.common.gui.tree;

import java.util.Enumeration;
import java.util.Vector;

import com.cannontech.database.model.DBTreeNode;

/**
 * @author rneuharth
 *
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates.
 * To enable and disable the creation of type comments go to
 * Window>Preferences>Java>Code Generation.
 */
public class CheckNode extends DBTreeNode
{
	//  public final static int SINGLE_SELECTION = 0;
	//  public final static int DIG_IN_SELECTION = 4;
	//  protected int selectionMode;

	protected boolean isSelected;

	public CheckNode()
	{
		this(null);
	}

	public CheckNode(Object userObject)
	{
		this(userObject, true, false);
	}

	public CheckNode( Object userObject, boolean allowsChildren, boolean isSelected)
	{
		super(userObject);
		this.allowsChildren = allowsChildren;
		this.isSelected = isSelected;
		//    setSelectionMode(DIG_IN_SELECTION);
	}

	/*
	  public void setSelectionMode(int mode) {
	    selectionMode = mode;
	  }
	
	  public int getSelectionMode() {
	    return selectionMode;
	  }
	*/

	public void setSelected(boolean isSelected)
	{
		//A null vector will not keep track of checked objects.
		setSelected(isSelected, null);
	}

	public void setSelected(boolean isSelected, Vector checkedNodes)
	{
		this.isSelected = isSelected;
		
		//NULL checkNodes does not load the vector with the checked objects.
		if( checkedNodes != null)
			collectCheckedObjects(checkedNodes);


		if (children != null)
		{
			Enumeration enum = children.elements();
			while (enum.hasMoreElements())
			{
				DBTreeNode node = (DBTreeNode) enum.nextElement();
				if (node instanceof CheckNode)
					 ((CheckNode) node).setSelected(isSelected, checkedNodes);
			}
		}

	}

	private void collectCheckedObjects(Vector checkedNodes)
	{
		if (isSelected)
		{
			if (!checkedNodes.contains(this.getUserObject()))
				checkedNodes.add(this.getUserObject());
		}
		else
			checkedNodes.remove(this.getUserObject());
	}
	
	public boolean isSelected()
	{
		return isSelected;
	}

	// If you want to change "isSelected" by CellEditor,
	/*
	public void setUserObject(Object obj) {
	  if (obj instanceof Boolean) {
	    setSelected(((Boolean)obj).booleanValue());
	  } else {
	    super.setUserObject(obj);
	  }
	}
	*/
}
