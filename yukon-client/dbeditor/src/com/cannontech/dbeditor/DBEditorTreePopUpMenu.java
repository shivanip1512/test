package com.cannontech.dbeditor;

/**
 * Insert the type's description here.
 * Creation date: (2/13/2001 2:49:45 PM)
 * @author: 
 */
public class DBEditorTreePopUpMenu
	extends com.cannontech.clientutils.popup.JPopUpMenuEventBase
	implements java.awt.event.ActionListener 
{
	//private int commandToExecute = -1;
	private javax.swing.JMenuItem jMenuItemEnableDisable = null;
	private javax.swing.JMenuItem jMenuItemCopy = null;
	private javax.swing.JMenuItem jMenuItemEdit = null;
	private javax.swing.JMenuItem jMenuItemDelete = null;
	private javax.swing.JMenuItem jMenuItemUpdate = null;
	private javax.swing.JMenuItem jMenuItemChangeType = null;
	private javax.swing.JMenu jMenuSortAllPointsBy = null;

	private javax.swing.JMenuItem jMenuItemSortbyName = null;
	private javax.swing.JMenuItem jMenuItemSortbyOffset = null;

	public static final int DELETE_TREENODE = 0;
	public static final int COPY_TREENODE = 1;
	public static final int ENABLEDISABLE_TREENODE = 2;
	public static final int EDIT_TREENODE = 3;
	public static final int UPDATE_TREENODE = 4;
	public static final int CHANGE_TYPE_TREENODE = 5;
	public static final int SORT_BY_NAME = 6;
	public static final int SORT_BY_OFFSET = 7;

/**
 * SchedulerPopUpMenu constructor comment.
 */
public DBEditorTreePopUpMenu() {
	super();
	initialize();
}
/**
 * SchedulerPopUpMenu constructor comment.
 * @param label java.lang.String
 */
public DBEditorTreePopUpMenu(String label) {
	super(label);
	initialize();
}
/**
 * This method was created in VisualAge.
 * @param event java.awt.event.ActionEvent
 */
public void actionPerformed(java.awt.event.ActionEvent event)
{
	if (event.getSource() == DBEditorTreePopUpMenu.this.getJMenuItemCopy())
		firePopUpEvent(new com.cannontech.clientutils.commonutils.GenericEvent(this, "copyTreeNode", COPY_TREENODE));

	if (event.getSource() == DBEditorTreePopUpMenu.this.getJMenuItemEdit())
		firePopUpEvent(new com.cannontech.clientutils.commonutils.GenericEvent(this, "editTreeNode", EDIT_TREENODE));

	if (event.getSource() == DBEditorTreePopUpMenu.this.getJMenuItemEnableDisable())
		firePopUpEvent(new com.cannontech.clientutils.commonutils.GenericEvent(this, "enableDisableSchedule", ENABLEDISABLE_TREENODE));

	if (event.getSource() == DBEditorTreePopUpMenu.this.getJMenuItemDelete())
		firePopUpEvent(new com.cannontech.clientutils.commonutils.GenericEvent(this, "deleteTreeNode", DELETE_TREENODE));

	if (event.getSource() == DBEditorTreePopUpMenu.this.getJMenuItemUpdate())
		firePopUpEvent(new com.cannontech.clientutils.commonutils.GenericEvent(this, "updateSchedule", UPDATE_TREENODE));

	if (event.getSource() == DBEditorTreePopUpMenu.this.getJMenuItemChangeType())
		firePopUpEvent(new com.cannontech.clientutils.commonutils.GenericEvent(this, "ChangeType", CHANGE_TYPE_TREENODE));
	
	if (event.getSource() == DBEditorTreePopUpMenu.this.getJMenuItemSortbyName())
		firePopUpEvent(new com.cannontech.clientutils.commonutils.GenericEvent(this, "Sort by Name", SORT_BY_NAME));
	
	if (event.getSource() == DBEditorTreePopUpMenu.this.getJMenuItemSortbyOffset())
		firePopUpEvent(new com.cannontech.clientutils.commonutils.GenericEvent(this, "Sort by Offset", SORT_BY_OFFSET));
}
/**
 * Insert the method's description here.
 * Creation date: (6/11/2001 8:54:09 AM)
 * @return javax.swing.JMenuItem
 */
public javax.swing.JMenuItem getJMenuItemChangeType() {
	if( jMenuItemChangeType == null )
	{
		jMenuItemChangeType = new javax.swing.JMenuItem();
		jMenuItemChangeType.setName("JMenuItemChangeType");
		jMenuItemChangeType.setMnemonic('g');
		jMenuItemChangeType.setText("Change Type...");
		
		
	}
	
	return jMenuItemChangeType;
}
/**
 * Insert the method's description here.
 * Creation date: (2/22/2001 10:13:30 AM)
 * @return javax.swing.JMenuItem
 */
protected javax.swing.JMenuItem getJMenuItemCopy() 
{
	if( jMenuItemCopy == null )
	{
		jMenuItemCopy = new javax.swing.JMenuItem();
		jMenuItemCopy.setName("JMenuItemCopy");
		jMenuItemCopy.setMnemonic('c');
		jMenuItemCopy.setText("Copy...");
	}
	
	return jMenuItemCopy;
}
/**
 * Insert the method's description here.
 * Creation date: (2/22/2001 10:13:30 AM)
 * @return javax.swing.JMenuItem
 */
protected javax.swing.JMenuItem getJMenuItemDelete()
{
	if( jMenuItemDelete == null )
	{
		jMenuItemDelete = new javax.swing.JMenuItem();
		jMenuItemDelete.setName("JMenuItemDelete");
		jMenuItemDelete.setMnemonic('t');
		jMenuItemDelete.setText("Delete...");
	}
	
	return jMenuItemDelete;
}
/**
 * Insert the method's description here.
 * Creation date: (2/22/2001 10:13:30 AM)
 * @return javax.swing.JMenuItem
 */
protected javax.swing.JMenuItem getJMenuItemEdit() 
{
	if( jMenuItemEdit == null )
	{
		jMenuItemEdit = new javax.swing.JMenuItem();
		jMenuItemEdit.setName("JMenuItemEdit");
		jMenuItemEdit.setMnemonic('d');
		jMenuItemEdit.setText("Edit...");
	}
	
	return jMenuItemEdit;
}
/**
 * Insert the method's description here.
 * Creation date: (2/22/2001 10:13:30 AM)
 * @return javax.swing.JMenuItem
 */
private javax.swing.JMenuItem getJMenuItemEnableDisable() 
{
	if( jMenuItemEnableDisable == null )
	{
		jMenuItemEnableDisable = new javax.swing.JMenuItem();
		jMenuItemEnableDisable.setName("JMenuItemEnableDisable");
		jMenuItemEnableDisable.setMnemonic('b');
		jMenuItemEnableDisable.setText("Enable");
	}
	
	return jMenuItemEnableDisable;
}
/**
 * Insert the method's description here.
 * Creation date: (7/6/2001 11:48:26 AM)
 * @return javax.swing.JMenuItem
 */
public javax.swing.JMenuItem getJMenuItemSortbyName() 
{
	if( jMenuItemSortbyName == null )
	{
		jMenuItemSortbyName = new javax.swing.JMenuItem();
		jMenuItemSortbyName.setName("JMenuItemSortbyName");
		//jMenuItemSortbyName.setMnemonic('d');
		jMenuItemSortbyName.setText("Point Name");
		jMenuItemSortbyName.setSelected( true );
	}
	
	return jMenuItemSortbyName;
}
/**
 * Insert the method's description here.
 * Creation date: (7/6/2001 11:48:26 AM)
 * @return javax.swing.JMenuItem
 */
public javax.swing.JMenuItem getJMenuItemSortbyOffset() 
{
	if( jMenuItemSortbyOffset == null )
	{
		jMenuItemSortbyOffset = new javax.swing.JMenuItem();
		jMenuItemSortbyOffset.setName("JMenuItemSortbyOffset");
		//jMenuItemSortbyOffset.setMnemonic('d');
		jMenuItemSortbyOffset.setText("Point Offset");

	}
	
	return jMenuItemSortbyOffset;
}
/**
 * Insert the method's description here.
 * Creation date: (2/22/2001 10:13:30 AM)
 * @return javax.swing.JMenuItem
 */
private javax.swing.JMenuItem getJMenuItemUpdate()
{
	if( jMenuItemUpdate == null )
	{
		jMenuItemUpdate = new javax.swing.JMenuItem();
		jMenuItemUpdate.setName("JMenuItemUpdate");
		jMenuItemUpdate.setMnemonic('u');
		jMenuItemUpdate.setText("Update");
	}
	
	return jMenuItemUpdate;
}
/**
 * Insert the method's description here.
 * Creation date: (7/6/2001 10:36:46 AM)
 * @return javax.swing.JMenuItem
 */
protected javax.swing.JMenu getJMenuSortAllPointsBy() {
	
	if( jMenuSortAllPointsBy == null )
	{
		jMenuSortAllPointsBy = new javax.swing.JMenu();
		jMenuSortAllPointsBy.setName("JMenuItemSortBy");
		jMenuSortAllPointsBy.setMnemonic('S');
		jMenuSortAllPointsBy.setText("Sort By");
	}
	
	return jMenuSortAllPointsBy;
	
	
	
}
/**
 * Insert the method's description here.
 * Creation date: (2/22/2001 10:09:35 AM)
 */
private void initConnections() 
{
	getJMenuItemDelete().addActionListener( this );
	getJMenuItemEdit().addActionListener( this );
	getJMenuItemEnableDisable().addActionListener( this );
	getJMenuItemCopy().addActionListener( this );
	getJMenuItemUpdate().addActionListener( this );
	getJMenuItemChangeType().addActionListener(this);
	getJMenuItemSortbyName().addActionListener(this);
	getJMenuItemSortbyOffset().addActionListener(this);
	
}
/**
 * Insert the method's description here.
 * Creation date: (2/22/2001 10:08:06 AM)
 */
private void initialize() 
{
	setName("DBEditorTreePopUp");
	
/*	if( getJMenuItemEnableDisable().isVisible() )
		add(getJMenuItemEnableDisable(), getJMenuItemEnableDisable().getName());
		
	if( getJMenuItemUpdate().isVisible() )
		add(getJMenuItemUpdate(), getJMenuItemUpdate().getName());		
*/
	add(getJMenuItemEdit(), getJMenuItemEdit().getName());
	add(getJMenuItemCopy(), getJMenuItemCopy().getName());
	add(getJMenuItemChangeType(), getJMenuItemChangeType().getName());
	add(getJMenuItemDelete(), getJMenuItemDelete().getName());
	
	// a good idea to put this one at the buttom!!!
	add(getJMenuItemDelete(), getJMenuItemDelete().getName());
	
	addSeparator();
	getJMenuSortAllPointsBy().add(getJMenuItemSortbyOffset());
	getJMenuSortAllPointsBy().add(getJMenuItemSortbyName());
	add(getJMenuSortAllPointsBy(), getJMenuSortAllPointsBy().getName());
	initConnections();
	
}
}
