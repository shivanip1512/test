package com.cannontech.dbeditor.menu;

/**
 * This type was created in VisualAge.
 */
import com.cannontech.common.gui.util.CommandableMenuItem;

public class CoreCreateMenu extends javax.swing.JMenu {

	//core wizards
	public CommandableMenuItem portMenuItem;
	public CommandableMenuItem deviceMenuItem;
	public CommandableMenuItem routeMenuItem;
	public CommandableMenuItem pointMenuItem;	
	public CommandableMenuItem stateGroupMenuItem;
	public CommandableMenuItem billingGroupMenuItem;
	public CommandableMenuItem config2WayMenuItem;
/**
 * CreateMenu constructor comment.
 */
public CoreCreateMenu() {
	super();
	initialize();
}
/**
 * This method was created in VisualAge.
 */
private void initialize() {

	java.awt.Font font = new java.awt.Font("dialog", 0, 14);
	
	portMenuItem = new CommandableMenuItem("Comm Channel...");
	portMenuItem.setFont( font );
	portMenuItem.setMnemonic('o');

	deviceMenuItem = new CommandableMenuItem("Device...");
	deviceMenuItem.setFont( font );
	deviceMenuItem.setMnemonic('d');

	routeMenuItem = new CommandableMenuItem("Route...");
	routeMenuItem.setFont( font );
	routeMenuItem.setMnemonic('r');

	pointMenuItem = new CommandableMenuItem("Point...");
	pointMenuItem.setFont( font );
	pointMenuItem.setMnemonic('p');

	stateGroupMenuItem = new CommandableMenuItem("State Group...");
	stateGroupMenuItem.setFont( font );
	stateGroupMenuItem.setMnemonic('s');

	billingGroupMenuItem = new CommandableMenuItem("Billing File...");
	billingGroupMenuItem.setFont( font );
	billingGroupMenuItem.setMnemonic('b');
	
	config2WayMenuItem = new CommandableMenuItem("Two-way Config...");
	config2WayMenuItem.setFont( font );
	config2WayMenuItem.setMnemonic('m');

	setText("Create");
	setFont( font );
	setMnemonic('c');

	add( portMenuItem );
	add( deviceMenuItem );
	add( routeMenuItem );
	add( pointMenuItem );
	add( stateGroupMenuItem );
	add( billingGroupMenuItem );
	add( config2WayMenuItem );
}
}
