package com.cannontech.common.gui.dnd;

/**
 * Insert the type's description here.
 * Creation date: (2/23/00 12:55:23 PM)
 * @author: 
 */
import java.awt.dnd.*;
import java.awt.datatransfer.DataFlavor;
import javax.swing.tree.DefaultMutableTreeNode;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.database.data.lite.LitePoint;

public class TransferableTreeNode extends javax.swing.tree.DefaultMutableTreeNode implements java.awt.datatransfer.Transferable 
{
	public final static int TREE_NODE = 0;

	public final static DataFlavor DEFAULT_MUTABLE_TREENODE_FLAVOR = 
			new DataFlavor( DefaultMutableTreeNode.class, "Default Mutable Tree Node" ) ;

	public static DataFlavor[] flavors = 
	{ 
		DEFAULT_MUTABLE_TREENODE_FLAVOR
	};
	
	private DefaultMutableTreeNode data;	
/**
 * TransferableTreeNode constructor comment.
 */
public TransferableTreeNode() {
	super();
}
/**
 * TransferableTreeNode constructor comment.
 * @param userObject java.lang.Object
 */
public TransferableTreeNode(Object userObject) {
	super(userObject);
}
/**
 * TransferableTreeNode constructor comment.
 * @param userObject java.lang.Object
 * @param allowsChildren boolean
 */
public TransferableTreeNode(Object userObject, boolean allowsChildren) {
	super(userObject, allowsChildren);
}
/**
 * TransferableTreeNode constructor comment.
 */
public TransferableTreeNode( DefaultMutableTreeNode node ) 
{
	data = node;
}
/**
 * getTransferData method comment.
 */
public Object getTransferData(java.awt.datatransfer.DataFlavor flavor) throws java.io.IOException, java.awt.datatransfer.UnsupportedFlavorException
{
	Object returnObject;

	if( flavor.equals( flavors[TREE_NODE] ) )
	{
		Object userObject = data.getUserObject();
		
		if( userObject == null )
			returnObject = data;
		else
			returnObject = userObject;			
	}
	else
		throw new java.awt.datatransfer.UnsupportedFlavorException( flavor );

			
	return returnObject;
}
/**
 * getTransferDataFlavors method comment.
 */
public java.awt.datatransfer.DataFlavor[] getTransferDataFlavors() 
{
	return flavors;
}
/**
 * isDataFlavorSupported method comment.
 */
public boolean isDataFlavorSupported(java.awt.datatransfer.DataFlavor flavor) 
{
	boolean returnValue = false;

	for( int i = 0, n = flavors.length; i < n; i++ )
	{
		if( flavors.equals( flavors[i] ) )
		{
			returnValue = true;
			break;
		}
	}
	
	return returnValue;
}
}
