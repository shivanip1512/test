package com.cannontech.yc.gui.menu;

/**
 * This type was created in VisualAge.
 */
import java.awt.KeyboardFocusManager;
import java.awt.event.KeyEvent;

import javax.swing.JSeparator;

import com.cannontech.common.gui.util.CTIKeyEventDispatcher;
import com.cannontech.common.gui.util.CommandableMenuItem;

public class YCViewMenu extends javax.swing.JMenu
{
	public JSeparator separator1;

	public CommandableMenuItem searchMenuItem;
	public CommandableMenuItem reloadMenuItem;
	public CommandableMenuItem clearMenuItem;
	public CommandableMenuItem deleteSerialNumberMenuItem;
	//public javax.swing.JCheckBoxMenuItem showCommandLogButton;
/**
 * YukonCommanderFileMenu constructor comment.
 */
public YCViewMenu() {
	super();
	initialize();
}
/**
 * This method was created in VisualAge.
 */
private void initialize() {
	java.awt.Font f = new java.awt.Font("dialog", 0, 14 );

	separator1 = new JSeparator();

	searchMenuItem = new CommandableMenuItem();
	searchMenuItem.setFont(f);
	searchMenuItem.setText("Find...");
	searchMenuItem.setMnemonic('f');
	searchMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(
	                            java.awt.event.KeyEvent.VK_F, 
	                            java.awt.Event.CTRL_MASK));

	
	reloadMenuItem = new CommandableMenuItem();
	reloadMenuItem.setFont(f);
	reloadMenuItem.setText("Reload Devices");
	reloadMenuItem.setMnemonic('r');
	reloadMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(
	                              java.awt.event.KeyEvent.VK_F5,
	                              0));

	clearMenuItem = new CommandableMenuItem();
	clearMenuItem.setFont(f);
	clearMenuItem.setText("Clear Screen");
	clearMenuItem.setMnemonic('c');
	
	deleteSerialNumberMenuItem = new CommandableMenuItem();
	deleteSerialNumberMenuItem.setFont(f);
	deleteSerialNumberMenuItem.setText("Delete Serial #...");
	deleteSerialNumberMenuItem.setMnemonic('d');
	deleteSerialNumberMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(
	                                          java.awt.event.KeyEvent.VK_DELETE,
	                                          0));

	/*
	showCommandLogButton = new javax.swing.JCheckBoxMenuItem();
	showCommandLogButton.setFont(f);
	showCommandLogButton.setText("Show Message Log");
	showCommandLogButton.setMnemonic('s');
	showCommandLogButton.setSelected(true);
	showCommandLogButton.setAccelerator(javax.swing.KeyStroke.getKeyStroke( java.awt.event.KeyEvent.VK_M, java.awt.Event.CTRL_MASK));
*/
	setFont( f );
	setText("View");
	setMnemonic('v');

	add( searchMenuItem );
	add( reloadMenuItem );
	add( clearMenuItem );
	add( deleteSerialNumberMenuItem );
	//add( separator1 );
	//add( showCommandLogButton );
	
	/* 
	 * This way to handle accelerators was changed to work with JRE 1.4. The accelerator
	 * event would always get consumed by the component focus was in. This ensures that
	 * accelerator fires the correct event ONLY (that is why true is returned on after
	 * each click). We keep the above accelerators set for display purposes.
	 */
	KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher(
		new CTIKeyEventDispatcher()
		{
			public boolean handleKeyEvent(KeyEvent e)
			{
				//do the checks of the keystrokes here
				if( e.getKeyCode() == KeyEvent.VK_F && e.isControlDown() )
				{
					searchMenuItem.doClick();
					return true;
				}
				else if( e.getKeyCode() == KeyEvent.VK_F5)
				{
					reloadMenuItem.doClick();
					return true;
				}
				else if( e.getKeyCode() == KeyEvent.VK_DELETE)
				{
					deleteSerialNumberMenuItem.doClick();
					return true;
				}
								
				//its this the last handling of the KeyEvent in this KeyboardFocusManager?
				return false;
			}
		});	
	}
}
