package com.cannontech.common.gui.dnd;

/**
 * Insert the type's description here.
 * Creation date: (2/23/00 12:55:23 PM)
 * @author: 
 */
import java.awt.datatransfer.DataFlavor;

public class TransferableObjects implements java.awt.datatransfer.Transferable
{
	public final static DataFlavor OBJECT_FLAVOR = 
			new DataFlavor( Object.class, "ObjObject" ) ;

	public final static int JLIST_OBJECT = 0;
	public final static int STRING_OBJECT = 1;

	public static DataFlavor[] flavors = 
	{ 
		OBJECT_FLAVOR,
		DataFlavor.stringFlavor
	};
	
	private Object data = null;
/**
 * TransferableTreeNode constructor comment.
 */
protected TransferableObjects() {
	super();
}
/**
 * TransferableTreeNode constructor comment.
 */
public TransferableObjects( Object o ) 
{
	super();
	data = o;
}
/**
 * getTransferData method comment.
 */
public Object getTransferData(java.awt.datatransfer.DataFlavor flavor) throws java.io.IOException, java.awt.datatransfer.UnsupportedFlavorException
{

	if( flavor.equals(flavors[JLIST_OBJECT])
		 || flavor.equals(flavors[STRING_OBJECT]) )
	{
		return data;
	}
	else
		throw new java.awt.datatransfer.UnsupportedFlavorException( flavor );


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
