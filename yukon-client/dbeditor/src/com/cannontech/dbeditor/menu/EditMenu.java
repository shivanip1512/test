package com.cannontech.dbeditor.menu;

/**
 * This type was created in VisualAge.
 */
import java.awt.KeyboardFocusManager;
import java.awt.event.KeyEvent;

import javax.swing.JMenuItem;
import javax.swing.JSeparator;

import com.cannontech.common.gui.util.CTIKeyEventDispatcher;

public class EditMenu extends javax.swing.JMenu 
{
	public JMenuItem editMenuItem;
	public JMenuItem copyMenuItem;
	public JMenuItem deleteMenuItem;
	public JMenuItem searchMenuItem;

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


	editMenuItem = new JMenuItem("Edit Item");
	editMenuItem.setFont( font );
	editMenuItem.setMnemonic('E');
	editMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke( 
													java.awt.event.KeyEvent.VK_E,
													java.awt.Event.CTRL_MASK));


	copyMenuItem = new JMenuItem("Copy");
	copyMenuItem.setFont( font );
	copyMenuItem.setMnemonic('C');
	copyMenuItem.setEnabled(true);
	copyMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke( 
													java.awt.event.KeyEvent.VK_C,
													java.awt.Event.CTRL_MASK));



	separator1 = new JSeparator();
	
	deleteMenuItem = new JMenuItem("Delete");
	deleteMenuItem.setFont( font );
	deleteMenuItem.setMnemonic('d');
	deleteMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke( 
													java.awt.event.KeyEvent.VK_DELETE,
													java.awt.Event.CTRL_MASK));

	searchMenuItem = new JMenuItem("Find...");
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
				if( e.getKeyCode() == KeyEvent.VK_C && e.isControlDown() )
				{
					copyMenuItem.doClick();
					return true;
				}
				else if( e.getKeyCode() == KeyEvent.VK_E && e.isControlDown() )
				{
					editMenuItem.doClick();
					return true;
				}
				else if( e.getKeyCode() == KeyEvent.VK_F && e.isControlDown() )
				{
					searchMenuItem.doClick();
					return true;
				}
				else if( e.getKeyCode() == KeyEvent.VK_DELETE && e.isControlDown() )
				{
					deleteMenuItem.doClick();
					return true;
				}
				
				//its this the last handling of the KeyEvent in this KeyboardFocusManager?
				return false;
			}
		});	
}
}
