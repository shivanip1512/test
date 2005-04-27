package com.cannontech.dbeditor.menu;

import java.awt.KeyboardFocusManager;
import java.awt.event.KeyEvent;

import javax.swing.JMenuItem;

import com.cannontech.common.gui.util.CTIKeyEventDispatcher;

/**
 * This type was created in VisualAge.
 */
public class FileMenu extends javax.swing.JMenu 
{

	public JMenuItem exitMenuItem;
	public JMenuItem endSessionMenuItem;

	/**
	 * FileMenu constructor comment.
	 */
	public FileMenu() {
		super();
		initialize();
	}
	/**
	 * This method was created in VisualAge.
	 */
	private void initialize() {
	
		java.awt.Font font = new java.awt.Font("dialog", 0, 14);
	
		exitMenuItem = new JMenuItem("Exit");
		exitMenuItem.setFont( font );
		exitMenuItem.setMnemonic('x');
		exitMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke( 
														java.awt.event.KeyEvent.VK_X,
														java.awt.Event.CTRL_MASK));
														
		endSessionMenuItem = new JMenuItem("End Session");
		endSessionMenuItem.setFont( font );
		endSessionMenuItem.setMnemonic('z');
		endSessionMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke( 
														java.awt.event.KeyEvent.VK_Z,
														java.awt.Event.CTRL_MASK));
	
		setText("File");
		setFont( font );
		setMnemonic('f');
		add(exitMenuItem);
		add(endSessionMenuItem);
		



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
					if( e.getKeyCode() == KeyEvent.VK_X && e.isControlDown() )
					{
						exitMenuItem.doClick();
						return true;
					}
					if( e.getKeyCode() == KeyEvent.VK_Z && e.isControlDown() )
					{
						endSessionMenuItem.doClick();
						return true;
					}

					//its this the last handling of the KeyEvent in this KeyboardFocusManager?
					return false;
				}
			});	
		
	}
}
