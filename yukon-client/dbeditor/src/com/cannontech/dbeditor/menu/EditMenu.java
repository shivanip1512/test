package com.cannontech.dbeditor.menu;

/**
 * This type was created in VisualAge.
 */
import javax.swing.JSeparator;

import com.cannontech.common.gui.util.CommandableMenuItem;

public class EditMenu extends javax.swing.JMenu 
{
	public CommandableMenuItem editMenuItem;
	public CommandableMenuItem copyMenuItem;
	public CommandableMenuItem deleteMenuItem;
	public CommandableMenuItem searchMenuItem;

	private JSeparator separator1;
/**
 * EditMenu constructor comment.
 */
public EditMenu() {
	super();
	initialize();
}
/**
 * This method was created in VisualAge.
 */
private void initialize() 
{
	java.awt.Font font = new java.awt.Font("dialog", 0, 14);


	editMenuItem = new CommandableMenuItem("Edit Item");
	editMenuItem.setFont( font );
	editMenuItem.setMnemonic('e');
	editMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke( 
													java.awt.event.KeyEvent.VK_E,
													java.awt.Event.CTRL_MASK));

	
	copyMenuItem = new CommandableMenuItem("Copy");
	copyMenuItem.setFont( font );
	copyMenuItem.setMnemonic('c');
	copyMenuItem.setEnabled(true);
	copyMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke( 
													java.awt.event.KeyEvent.VK_C,
													java.awt.Event.CTRL_MASK));

	separator1 = new JSeparator();
	
	deleteMenuItem = new CommandableMenuItem("Delete");
	deleteMenuItem.setFont( font );
	deleteMenuItem.setMnemonic('d');
	deleteMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke( 
													java.awt.event.KeyEvent.VK_DELETE,
													0));

	searchMenuItem = new CommandableMenuItem("Find...");
	searchMenuItem.setFont( font );
	searchMenuItem.setMnemonic('f');
	searchMenuItem.setEnabled(true);
	searchMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke( 
													java.awt.event.KeyEvent.VK_F,
													java.awt.Event.CTRL_MASK));
		
	setText("Edit");
	setFont( font );
	setMnemonic('e');

	add( editMenuItem );
	add( copyMenuItem );
	add( separator1 );
	add( searchMenuItem );
	add( deleteMenuItem );
}
}
