package com.cannontech.dbeditor.menu;

/**
 * This type was created by Cannon Technologies Inc.
 */
import java.awt.KeyboardFocusManager;
import java.awt.event.KeyEvent;

import javax.swing.JMenuItem;

import com.cannontech.common.gui.util.CTIKeyEventDispatcher;

import com.cannontech.common.gui.util.CommandableMenuItem;

public class HelpMenu extends javax.swing.JMenu 
{
	public JMenuItem helpTopicMenuItem;
	public JMenuItem aboutMenuItem;
	public CommandableMenuItem ptOffsetLegendMenuItem;

	/**
	 * HelpMenu constructor comment.
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
	
		helpTopicMenuItem = new JMenuItem("Help Topics");
		helpTopicMenuItem.setFont( font );
		helpTopicMenuItem.setMnemonic('t');
		helpTopicMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke( 
														java.awt.event.KeyEvent.VK_F1,
														0));
	
		aboutMenuItem = new JMenuItem("About");
		aboutMenuItem.setFont( font );
		aboutMenuItem.setMnemonic('b');
		aboutMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke( 
														java.awt.event.KeyEvent.VK_B,
														java.awt.Event.CTRL_MASK));
														
		ptOffsetLegendMenuItem = new CommandableMenuItem("Point Offset Legend");
		ptOffsetLegendMenuItem.setFont( font );
		ptOffsetLegendMenuItem.setMnemonic('o');
	
		setText("Help");
		setFont( font );
		setMnemonic('h');
	
		add( helpTopicMenuItem );
		add(ptOffsetLegendMenuItem);	
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
				public boolean handleKeyEvent(KeyEvent e)
				{
					//do the checks of the keystrokes here
					if( e.getKeyCode() == KeyEvent.VK_B && e.isControlDown() )
					{
						aboutMenuItem.doClick();
						return true;
					}
					else if( e.getKeyCode() == KeyEvent.VK_F1 )
					{
						helpTopicMenuItem.doClick();
						return true;
					}
					
					//its this the last handling of the KeyEvent in this KeyboardFocusManager?
					return false;
				}
			});	

	}
}
