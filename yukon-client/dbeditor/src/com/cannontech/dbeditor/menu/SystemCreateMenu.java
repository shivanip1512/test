package com.cannontech.dbeditor.menu;

/**
 * This type was created in VisualAge.
 */
import com.cannontech.common.gui.util.CommandableMenuItem;

public class SystemCreateMenu extends javax.swing.JMenu {

	//System wizards
	public CommandableMenuItem customerMenuItem;
	public CommandableMenuItem contactMenuItem;
	public CommandableMenuItem userMenuItem;
	public CommandableMenuItem userGroupMenuItem;
	public CommandableMenuItem roleGroupMenuItem;
	public CommandableMenuItem notificationGroupMenuItem;
	public CommandableMenuItem holidayMenuItem;
	public CommandableMenuItem baselineMenuItem;
	public CommandableMenuItem tagMenuItem;
	public CommandableMenuItem seasonMenuItem;
	public CommandableMenuItem touMenuItem;
    public CommandableMenuItem systemPointMenuItem;

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

	contactMenuItem = new CommandableMenuItem("Contact...");
	contactMenuItem.setFont( font );
	contactMenuItem.setMnemonic('t');

	userMenuItem = new CommandableMenuItem("User...");
	userMenuItem.setFont( font );
	userMenuItem.setMnemonic('l');

	userGroupMenuItem = new CommandableMenuItem("User Group...");
	userGroupMenuItem.setFont( font );
	
	roleGroupMenuItem = new CommandableMenuItem("Role Group...");
	roleGroupMenuItem.setFont( font );
	roleGroupMenuItem.setMnemonic('g');

	baselineMenuItem = new CommandableMenuItem("Baseline...");
	baselineMenuItem.setFont( font );
	baselineMenuItem.setMnemonic('b');
	
	seasonMenuItem = new CommandableMenuItem("Season Schedule...");
	seasonMenuItem.setFont( font );
	seasonMenuItem.setMnemonic('s');
	
	tagMenuItem = new CommandableMenuItem("Tag...");
	tagMenuItem.setFont( font );
	tagMenuItem.setMnemonic('t');
	
	touMenuItem = new CommandableMenuItem("TOU Schedule...");
	touMenuItem.setFont( font );
	touMenuItem.setMnemonic('o');
    
    systemPointMenuItem = new CommandableMenuItem("System Point...");
    systemPointMenuItem.setFont( font );
    systemPointMenuItem.setMnemonic('y');
	
	setText("Create");
	setFont( font );
	setMnemonic('c');

	//keep and add to these in alphabetical order
	add( baselineMenuItem );
	add( contactMenuItem );
	add( customerMenuItem );
	add( holidayMenuItem );
	add( notificationGroupMenuItem );
	add( roleGroupMenuItem );
	add( seasonMenuItem );
	add( systemPointMenuItem );
	add( tagMenuItem );
	add( touMenuItem );
	add( userMenuItem );
	add( userGroupMenuItem );
}
}
