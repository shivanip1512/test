package com.cannontech.dbeditor;

/**
 * Insert the type's description here.
 * Creation date: (3/14/2001 1:59:23 PM)
 * @author: 
 */
public class JTreeEditorFrame extends javax.swing.JInternalFrame 
{
	javax.swing.tree.DefaultMutableTreeNode ownerNode = null;
/**
 * JTreeEditorFrames constructor comment.
 */
public JTreeEditorFrame() {
	super();
}
/**
 * JTreeEditorFrames constructor comment.
 * @param title java.lang.String
 */
public JTreeEditorFrame(String title) {
	super(title);
}
/**
 * JTreeEditorFrames constructor comment.
 * @param title java.lang.String
 * @param resizable boolean
 */
public JTreeEditorFrame(String title, boolean resizable) {
	super(title, resizable);
}
/**
 * JTreeEditorFrames constructor comment.
 * @param title java.lang.String
 * @param resizable boolean
 * @param closable boolean
 */
public JTreeEditorFrame(String title, boolean resizable, boolean closable) {
	super(title, resizable, closable);
}
/**
 * JTreeEditorFrames constructor comment.
 * @param title java.lang.String
 * @param resizable boolean
 * @param closable boolean
 * @param maximizable boolean
 */
public JTreeEditorFrame(String title, boolean resizable, boolean closable, boolean maximizable) {
	super(title, resizable, closable, maximizable);
}
/**
 * JTreeEditorFrames constructor comment.
 * @param title java.lang.String
 * @param resizable boolean
 * @param closable boolean
 * @param maximizable boolean
 * @param iconifiable boolean
 */
public JTreeEditorFrame(String title, boolean resizable, boolean closable, boolean maximizable, boolean iconifiable) {
	super(title, resizable, closable, maximizable, iconifiable);
}
/**
 * Insert the method's description here.
 * Creation date: (3/14/2001 1:59:50 PM)
 * @return javax.swing.tree.DefaultMutableTreeNode
 */
public javax.swing.tree.DefaultMutableTreeNode getOwnerNode() {
	return ownerNode;
}
/**
 * Insert the method's description here.
 * Creation date: (3/14/2001 1:59:50 PM)
 * @param newOwnerNode javax.swing.tree.DefaultMutableTreeNode
 */
public void setOwnerNode(javax.swing.tree.DefaultMutableTreeNode newOwnerNode) {
	ownerNode = newOwnerNode;
}
/**
 * Insert the method's description here.
 * Creation date: (3/14/2001 2:06:22 PM)
 * @param value boolean
 */
public void setVisible(boolean value) 
{
	try
	{
		if( value == false )
		{
			setOwnerNode(null); // get rid of our reference to the owner node			
			//we must set the closed attribute so the frame "appears" to be in a normal state
			super.setClosed(false);
		}
		
		super.setVisible(value);
	}
	catch(java.beans.PropertyVetoException e )
	{
		com.cannontech.clientutils.CTILogger.info("*** PropertyVetoException() occured in setVisible(boolean) of " + getClass().getName() + " : " + e.getMessage() );
	}
	
}
}
