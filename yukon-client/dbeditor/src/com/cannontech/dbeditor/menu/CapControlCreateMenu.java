package com.cannontech.dbeditor.menu;

/**
 * This type was created in VisualAge.
 */
import com.cannontech.common.gui.util.CommandableMenuItem;

public class CapControlCreateMenu extends javax.swing.JMenu {

	//cap control wizards
	public CommandableMenuItem capBankMenuItem;
	public CommandableMenuItem capBankControllerMenuItem;
	public CommandableMenuItem capControlFeederMenuItem;
	public CommandableMenuItem capControlSubBusMenuItem;
	public CommandableMenuItem pointMenuItem;
/**
 * CreateMenu constructor comment.
 */
public CapControlCreateMenu() {
	super();
	initialize();
}
/**
 * This method was created in VisualAge.
 */
private void initialize() {

	java.awt.Font font = new java.awt.Font("dialog", 0, 14);

	capBankMenuItem = new CommandableMenuItem("Cap Bank...");
	capBankMenuItem.setFont(font);
	capBankMenuItem.setMnemonic('b');

	capBankControllerMenuItem = new CommandableMenuItem("Cap Bank Controller...");
	capBankControllerMenuItem.setFont( font );
	capBankControllerMenuItem.setMnemonic('c');

	capControlFeederMenuItem = new CommandableMenuItem("Feeder...");
	capControlFeederMenuItem.setFont( font );
	capControlFeederMenuItem.setMnemonic('f');
	
	capControlSubBusMenuItem = new CommandableMenuItem("Substation Bus...");
	capControlSubBusMenuItem.setFont( font );
	capControlSubBusMenuItem.setMnemonic('s');

	pointMenuItem = new CommandableMenuItem("Point...");
	pointMenuItem.setFont( font );
	pointMenuItem.setMnemonic('p');

	setText("Create");
	setFont( font );
	setMnemonic('c');

	add( capBankMenuItem );
	add( capBankControllerMenuItem );
	add( capControlFeederMenuItem );
	add( capControlSubBusMenuItem );
	add( pointMenuItem );
}
}
