package com.cannontech.yc.gui.menu;

/**
 * This type was created in VisualAge.
 */
import java.awt.KeyboardFocusManager;
import java.awt.event.KeyEvent;

import javax.swing.JSeparator;

import com.cannontech.common.gui.util.CTIKeyEventDispatcher;
import com.cannontech.common.gui.util.CommandableMenuItem;

public class YCFileMenu extends javax.swing.JMenu {
	public JSeparator separator1;
	public JSeparator separator2;

	public CommandableMenuItem saveMenuItem;
	public CommandableMenuItem printMenuItem;
	public CommandableMenuItem exitMenuItem;
	public CommandableMenuItem commandSpecificControl;	//CHEATER MODE
/**
 * YukonCommanderFileMenu constructor comment.
 */
public YCFileMenu() {
	super();
	initialize();
}
/**
 * This method was created in VisualAge.
 */
private void initialize() {
	java.awt.Font f = new java.awt.Font("dialog", 0, 14 );

	separator1 = new JSeparator();
	separator2 = new JSeparator();

	saveMenuItem = new CommandableMenuItem();
	saveMenuItem.setFont(f);
	saveMenuItem.setText("Save Output...");
	saveMenuItem.setMnemonic('s');
	saveMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(
								java.awt.event.KeyEvent.VK_S,
								java.awt.Event.CTRL_MASK));

	printMenuItem = new CommandableMenuItem();
	printMenuItem.setFont(f);
	printMenuItem.setText("Print...");
	printMenuItem.setMnemonic('p');
	printMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(
	                             java.awt.event.KeyEvent.VK_P,
	                             java.awt.Event.CTRL_MASK));
	
	exitMenuItem = new CommandableMenuItem();
	exitMenuItem.setFont( f );
	exitMenuItem.setText("Exit");
	exitMenuItem.setMnemonic('x');
	exitMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(
	                            java.awt.event.KeyEvent.VK_X,
	                            java.awt.Event.CTRL_MASK));

	commandSpecificControl = new CommandableMenuItem();
	commandSpecificControl.setAccelerator(javax.swing.KeyStroke.getKeyStroke(
	                                      java.awt.event.KeyEvent.VK_F5,
	                                      java.awt.Event.CTRL_MASK));
	commandSpecificControl.setVisible(false);
		
	setFont( f );
	setText("File");
	setMnemonic('f');

	add( saveMenuItem );
	add( separator1 );
	add( printMenuItem );
	add( separator2 );
	add( exitMenuItem );
	add( commandSpecificControl );	//THIS WILL NEVER BE VISIBLE
	
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
				if( e.getKeyCode() == KeyEvent.VK_S && e.isControlDown() )
				{
					saveMenuItem.doClick();
					return true;
				}
				else if( e.getKeyCode() == KeyEvent.VK_P && e.isControlDown() )
				{
					printMenuItem.doClick();
					return true;
				}
				else if( e.getKeyCode() == KeyEvent.VK_X && e.isControlDown() )
				{
					exitMenuItem.doClick();
					return true;
				}
				else if( e.getKeyCode() == KeyEvent.VK_F5 && e.isControlDown() )
				{
					commandSpecificControl.doClick();
					return true;
				}
				//its this the last handling of the KeyEvent in this KeyboardFocusManager?
				return false;
			}
		});	
	}
}
