package com.cannontech.dbeditor.menu;

/**
 * This type was created in VisualAge.
 */
import com.cannontech.common.gui.util.CommandableMenuItem;

public class LMCreateMenu extends javax.swing.JMenu {

	//lm wizards
	public CommandableMenuItem lmGroupMenuItem;	
	public CommandableMenuItem lmProgramMenuItem;
	public CommandableMenuItem pointMenuItem;
	public CommandableMenuItem lmControlAreaMenuItem;
/**
 * CreateMenu constructor comment.
 */
public LMCreateMenu() {
	super();
	initialize();
}
/**
 * This method was created in VisualAge.
 */
private void initialize() {

	java.awt.Font font = new java.awt.Font("dialog", 0, 14);

	lmGroupMenuItem = new CommandableMenuItem("Load Group...");
	lmGroupMenuItem.setFont(font);
	lmGroupMenuItem.setMnemonic('l');

	lmProgramMenuItem = new CommandableMenuItem("Load Program...");
	lmProgramMenuItem.setFont(font);
	lmProgramMenuItem.setMnemonic('r');

	lmControlAreaMenuItem = new CommandableMenuItem("Control Area...");
	lmControlAreaMenuItem.setFont(font);
	lmControlAreaMenuItem.setMnemonic('c');

	pointMenuItem = new CommandableMenuItem("Point...");
	pointMenuItem.setFont( font );
	pointMenuItem.setMnemonic('p');
	
	setText("Create");
	setFont( font );
	setMnemonic('c');

	add( lmGroupMenuItem );
	add( lmProgramMenuItem );
	add( lmControlAreaMenuItem );
	add( pointMenuItem );
}
}
