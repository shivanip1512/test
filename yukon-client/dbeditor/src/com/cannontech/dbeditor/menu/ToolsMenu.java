package com.cannontech.dbeditor.menu;

/**
 * Insert the type's description here.
 * Creation date: (5/30/2002 9:38:23 AM)
 * @author: 
 */

import com.cannontech.common.gui.util.CommandableMenuItem;

public class ToolsMenu extends javax.swing.JMenu
{

	public CommandableMenuItem regenerateMenuItem;
	public CommandableMenuItem defaultMenuItem;

	/**
	 * ToolsMenu constructor comment.
	 */
	public ToolsMenu()
	{
		super();
		initialize();
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (5/30/2002 9:39:49 AM)
	 */
	public void initialize()
	{

		java.awt.Font font = new java.awt.Font("dialog", 0, 14);

		regenerateMenuItem = new CommandableMenuItem("Regenerate Route Roles");
		regenerateMenuItem.setFont(font);
		regenerateMenuItem.setMnemonic('r');
		//regenerateMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke( 
		//java.awt.event.KeyEvent.VK_R,
		//java.awt.Event.CTRL_MASK));

		defaultMenuItem = new CommandableMenuItem("Default Routes");
	   defaultMenuItem.setFont( font );
		defaultMenuItem.setMnemonic('d');

		setText("Tools");
		setFont(font);
		setMnemonic('t');
		add(regenerateMenuItem);
		add(defaultMenuItem);
	}
}