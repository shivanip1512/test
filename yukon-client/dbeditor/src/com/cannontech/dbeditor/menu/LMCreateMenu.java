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
	public CommandableMenuItem lmProgramConstraintMenuItem;
	public CommandableMenuItem lmControlScenarioMenuItem;
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
	
	lmControlScenarioMenuItem = new CommandableMenuItem("Control Scenario...");
	lmControlScenarioMenuItem.setFont( font );
	lmControlScenarioMenuItem.setMnemonic('w');
	
	pointMenuItem = new CommandableMenuItem("Point...");
	pointMenuItem.setFont( font );
	pointMenuItem.setMnemonic('p');
	
	lmProgramConstraintMenuItem = new CommandableMenuItem("Program Constraint...");
	lmProgramConstraintMenuItem.setFont( font );
	lmProgramConstraintMenuItem.setMnemonic('q');
	
	setText("Create");
	setFont( font );
	setMnemonic('c');

	//keep and add to these in alphabetical order
	add( lmControlAreaMenuItem );
	add( lmControlScenarioMenuItem );
	add( lmGroupMenuItem );
	add( lmProgramMenuItem );
	add( pointMenuItem );
	add( lmProgramConstraintMenuItem );

}
}
