package com.cannontech.database.model;

/**
 * This type was created in VisualAge.
 */
import java.util.Vector;

import com.cannontech.common.gui.tree.CheckNode;

public class DeviceCheckBoxTreeModel extends DeviceTreeModel implements Checkable
{
	//Contains CheckNodes (hopefully) values.  DOES NOT CONTAIN THE PARENT!!!
	private Vector checkedNodes = null;
	
		
	/**
	 * DeviceTreeModel constructor comment.
	 * @param root javax.swing.tree.TreeNode
	 */
	public DeviceCheckBoxTreeModel() {
		this( false );
	}
	/**
	 * DeviceTreeModel constructor comment.
	 * @param rootNode_ DBTreeNode
	 */
	public DeviceCheckBoxTreeModel( CheckNode rootNode_ ) 
	{
		this( false, rootNode_ );
	}
	/**
	 * DeviceTreeModel constructor comment.
	 * @param root javax.swing.tree.TreeNode
	 */
	public DeviceCheckBoxTreeModel( boolean showPointNodes )
	{
		this( showPointNodes, new CheckNode("Devices") );
	}
	
	/**
	 * DeviceTreeModel constructor comment.
	 * @param root javax.swing.tree.TreeNode
	 */
	public DeviceCheckBoxTreeModel( boolean showPointNodes, CheckNode rootNode_ )
	{
		super(showPointNodes, rootNode_ );
	}

	/* (non-Javadoc)
	 * @see com.cannontech.database.model.DeviceTreeModel#getNewNode(java.lang.Object)
	 */
	protected DBTreeNode getNewNode(Object obj)
	{
		return new CheckNode(obj);
	}
	
	/**
	 * Insert the method's description here.
	 * Creation date: (2/27/2002 10:17:05 AM)
	 * @param lp com.cannontech.database.data.lite.LitePoint
	 * @param dTreeNode com.cannontech.database.model.DummyTreeNode
	 */
	protected DBTreeNode addDummyTreeNode(com.cannontech.database.data.lite.LitePoint lp, 
						DBTreeNode node, String text, DBTreeNode deviceNode ) 
	{
		if( node == null)
		{
			DBTreeNode retNode = getNewNode(text);
	
			int indx = -1;
			for( int i = 0 ; i < deviceNode.getChildCount(); i++ )
				if( deviceNode.getChildAt(i).equals(retNode) )
				{
					indx = i;
					break;
				}
					
			if( indx >= 0 )
				node = (CheckNode)deviceNode.getChildAt(indx);
			else
				node = retNode;
		}
			
	
		node.add( getNewNode(lp) );
		//updateTreeNodeStructure( node );
	
		return node;
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
