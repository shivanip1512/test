package com.cannontech.dbeditor.menu;

/**
 * This type was created in VisualAge.
 */
import com.cannontech.common.gui.util.CommandableMenuItem;

public class SystemCreateMenu extends javax.swing.JMenu {

	//System wizards
	public CommandableMenuItem notificationGroupMenuItem;

	public CommandableMenuItem holidayMenuItem;
	public CommandableMenuItem customerMenuItem;


/**
 * CreateMenu constructor comment.
 */
public SystemCreateMenu() {
	super();
	initialize();
}
/**
 * This method was created in VisualAge.
 */
private void initialize() {

	java.awt.Font font = new java.awt.Font("dialog", 0, 14);

	notificationGroupMenuItem = new CommandableMenuItem("Notification Group...");
	notificationGroupMenuItem.setFont(font);
	notificationGroupMenuItem.setMnemonic('n');
	
	holidayMenuItem = new CommandableMenuItem("Holiday Schedule...");
	holidayMenuItem.setFont( font );
	holidayMenuItem.setMnemonic('p');

	customerMenuItem = new CommandableMenuItem("Customer...");
	customerMenuItem.setFont( font );
	customerMenuItem.setMnemonic('c');
	
	setText("Create");
	setFont( font );
	setMnemonic('c');

	add( notificationGroupMenuItem );
	add( holidayMenuItem );
	add( customerMenuItem );
}
}
