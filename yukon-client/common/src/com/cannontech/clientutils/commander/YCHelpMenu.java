package com.cannontech.clientutils.commander;

/**
 * This type was created in VisualAge.
 */
import java.awt.KeyboardFocusManager;
import java.awt.event.KeyEvent;

import javax.swing.JSeparator;

import com.cannontech.common.gui.util.CTIKeyEventDispatcher;
import com.cannontech.common.gui.util.CommandableMenuItem;

public class YCHelpMenu extends javax.swing.JMenu
{
	public JSeparator separator1;

	public CommandableMenuItem aboutMenuItem;
	public CommandableMenuItem helpTopicMenuItem;
/**
 * YukonCommanderFileMenu constructor comment.
 */
public YCHelpMenu() {
	super();
	initialize();
}
/**
 * This method was created in VisualAge.
 */
private void initialize() {
	java.awt.Font f = new java.awt.Font("dialog", 0, 14 );

	separator1 = new JSeparator();


	helpTopicMenuItem = new CommandableMenuItem();
	helpTopicMenuItem.setFont(f);
	helpTopicMenuItem.setText("Help Topics");
	helpTopicMenuItem.setMnemonic('t');
	helpTopicMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(
	                                 java.awt.event.KeyEvent.VK_F1,
	                                 0));

	aboutMenuItem = new CommandableMenuItem();
	aboutMenuItem.setFont(f);
	aboutMenuItem.setText("About");
	aboutMenuItem.setMnemonic('b');
	aboutMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(
	                             java.awt.event.KeyEvent.VK_B,
	                             java.awt.Event.CTRL_MASK));
	
	setFont( f );
	setText("Help");
	setMnemonic('h');


	add( helpTopicMenuItem );
	add( aboutMenuItem );

	/* 
	 * This way to handle accelerators was changed to work with JRE 1.4. The accelerator
	 * event would always get consumed by the component focus was in. This ensures that
	 * accelerator fires the correct event ONLY (that is why true is returned on after
	 * each click). We keep the above accelerators set for display purposes.
	 */
	KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher(
		new CTIKeyEventDispatcher()
		{
			public boolean handleKeyEvent(KeyEvent  e)
			{
				//do the checks of the keystrokes here
				if( e.getKeyCode() == KeyEvent.VK_F1 )
				{
					helpTopicMenuItem.doClick();
					return true;
				}
				else if( e.getKeyCode() == KeyEvent.VK_B && e.isControlDown() )
				{
					aboutMenuItem.doClick();
					return true;
				}
				
				//its this the last handling of the KeyEvent in this KeyboardFocusManager?
				return false;
			}
		});	
	}
}
