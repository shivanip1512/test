package com.cannontech.dbeditor.menu;

/**
 * This type was created by Cannon Technologies Inc.
 */
import javax.swing.JSeparator;

import com.cannontech.common.gui.util.CommandableMenuItem;

public class HelpMenu extends javax.swing.JMenu 
{
	public CommandableMenuItem helpTopicMenuItem;
	public CommandableMenuItem aboutMenuItem;
/**
 * HelpMeno constructor comment.
 */
public HelpMenu() {
	super();
	initialize();
}
/**
 * This method was created in VisualAge.
 */
private void initialize() {

	java.awt.Font font = new java.awt.Font("dialog", 0, 14);

	helpTopicMenuItem = new CommandableMenuItem("Help Topics");
	helpTopicMenuItem.setFont( font );
	helpTopicMenuItem.setMnemonic('t');
	helpTopicMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke( 
													java.awt.event.KeyEvent.VK_F1,
													0));

	aboutMenuItem = new CommandableMenuItem("About");
	aboutMenuItem.setFont( font );
	aboutMenuItem.setMnemonic('a');
	aboutMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke( 
													java.awt.event.KeyEvent.VK_A,
													java.awt.Event.CTRL_MASK));

	setText("Help");
	setFont( font );
	setMnemonic('h');

	add( helpTopicMenuItem );
	add( aboutMenuItem );
}
}
