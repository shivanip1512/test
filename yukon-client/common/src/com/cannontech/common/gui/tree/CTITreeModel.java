package com.cannontech.common.gui.tree;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

/**
 * @author rneuharth
 *
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates.
 * To enable and disable the creation of type comments go to
 * Window>Preferences>Java>Code Generation.
 */
public class CTITreeModel extends DefaultTreeModel {

	/**
	 * Constructor for CTITreeModel.
	 * @param root
	 */
	public CTITreeModel() {
		this( new DefaultMutableTreeNode("CTITreeModel") );
	}
	
	/**
	 * Constructor for CTITreeModel.
	 * @param root
	 */
	public CTITreeModel(TreeNode root) {
		super(root);
	}

	/**
	 * Constructor for CTITreeModel.
	 * @param root
	 * @param asksAllowsChildren
	 */
	public CTITreeModel(TreeNode root, boolean asksAllowsChildren) {
		super(root, asksAllowsChildren);
	}


	/**
	 * Insert the method's description here.
	 * Creation date: (4/18/2002 2:20:48 PM)
	 * @return DefaultMutableTreeNode
	 * @param DefaultMutableTreeNode
	 */
	public DefaultMutableTreeNode findNode( TreePath path, Object nodeToFind )
	{
		if( path == null )
			path = new TreePath( getRoot() );
			
		DefaultMutableTreeNode node = 
				(DefaultMutableTreeNode) path.getLastPathComponent();
		
		if( nodeToFind == null )
		{
			return null;
		}
		if( node.getUserObject().equals(nodeToFind) )
		{
			return node;
		}
		else
		if( node.getChildCount() == 0 )
		{
			return null;
		}
		else
		{
			for( int i = 0; i < node.getChildCount(); i++ )
			{
				Object nextPathObjs[] = new Object[path.getPath().length +1];
	
				System.arraycopy( path.getPath(), 0, nextPathObjs, 0, path.getPath().length );
	
				nextPathObjs[path.getPath().length] = node.getChildAt(i);
				
				TreePath nextPath = new TreePath(nextPathObjs);
	
				DefaultMutableTreeNode tempNode = findNode( nextPath, nodeToFind );
				if( tempNode != null )
					return tempNode;
			}
	
			//unable to find the node!!
			return null;
		}
		
			
	}
	
	
	/**
	 * Insert the method's description here.
	 * Creation date: (4/18/2002 2:20:48 PM)
	 * @return DefaultMutableTreeNode
	 * @param DefaultMutableTreeNode
	 */
	public List getAllLeafNodes( TreePath path )
	{
		ArrayList retValues = new ArrayList(20);
		DefaultMutableTreeNode root = 
				(DefaultMutableTreeNode)path.getLastPathComponent();
				
		if( root == null )
			return retValues;

		Enumeration allNodes = root.depthFirstEnumeration();
		while( allNodes.hasMoreElements() )
		{
			DefaultMutableTreeNode node = 
				(DefaultMutableTreeNode)allNodes.nextElement();
				 
			if( node.isLeaf() )
				retValues.add( node );
		}
		
		return retValues;
	}

	
}