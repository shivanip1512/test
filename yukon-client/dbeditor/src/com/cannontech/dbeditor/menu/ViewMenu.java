package com.cannontech.dbeditor.menu;

/**
 * This type was created in VisualAge.
 */
import java.awt.KeyboardFocusManager;
import java.awt.event.KeyEvent;

import javax.swing.ButtonGroup;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JSeparator;
import javax.swing.JMenuItem;

import com.cannontech.common.gui.util.CTIKeyEventDispatcher;
;


public class ViewMenu extends javax.swing.JMenu 
{
	public JRadioButtonMenuItem coreRadioButtonMenuItem;
	public JRadioButtonMenuItem lmRadioButtonMenuItem;
	public JRadioButtonMenuItem capControlRadioButtonMenuItem;
	public JRadioButtonMenuItem systemRadioButtonMenuItem;
	private ButtonGroup radioButtonGroup;

	private JSeparator separator;
	
	public JMenuItem refreshMenuItem;
	public javax.swing.JCheckBoxMenuItem showMessageLogButton;


	/**
	 * ViewMenu constructor comment.
	 */
	public ViewMenu() {
		super();
		initialize();
	}
	/**
	 * This method was created in VisualAge.
	 */
	private void initialize() {
	
		java.awt.Font font = new java.awt.Font("dialog", 0, 14);
	
		coreRadioButtonMenuItem = new JRadioButtonMenuItem("Core");
		coreRadioButtonMenuItem.setFont(font);
		coreRadioButtonMenuItem.setMnemonic('o');
		coreRadioButtonMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke( 
														java.awt.event.KeyEvent.VK_O,
														java.awt.Event.CTRL_MASK));
		
		lmRadioButtonMenuItem = new JRadioButtonMenuItem("Load Management");
		lmRadioButtonMenuItem.setFont(font);
		lmRadioButtonMenuItem.setMnemonic('l');
		lmRadioButtonMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke( 
														java.awt.event.KeyEvent.VK_L,
														java.awt.Event.CTRL_MASK));
		
		capControlRadioButtonMenuItem = new JRadioButtonMenuItem("Cap Control");
		capControlRadioButtonMenuItem.setFont(font);
		capControlRadioButtonMenuItem.setMnemonic('t');
		capControlRadioButtonMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke( 
														java.awt.event.KeyEvent.VK_T,
														java.awt.Event.CTRL_MASK));
	
		systemRadioButtonMenuItem = new JRadioButtonMenuItem("System");
		systemRadioButtonMenuItem.setFont(font);
		systemRadioButtonMenuItem.setMnemonic('y');	
		systemRadioButtonMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke( 
														java.awt.event.KeyEvent.VK_Y,
														java.awt.Event.CTRL_MASK));
		
		radioButtonGroup = new ButtonGroup();
		radioButtonGroup.add(coreRadioButtonMenuItem);
		radioButtonGroup.add(lmRadioButtonMenuItem);
		radioButtonGroup.add(capControlRadioButtonMenuItem); // ADDED THIS LATE??!!
		radioButtonGroup.add(systemRadioButtonMenuItem);
		
		separator = new JSeparator();
			
		refreshMenuItem = new JMenuItem("Refresh");
		refreshMenuItem.setFont( font );
		refreshMenuItem.setMnemonic('r');
		refreshMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke( 
														java.awt.event.KeyEvent.VK_R,
														java.awt.Event.CTRL_MASK));
	
		showMessageLogButton = new javax.swing.JCheckBoxMenuItem("Message Log");
		showMessageLogButton.setFont(font);
		showMessageLogButton.setMnemonic('s');
		showMessageLogButton.setSelected(true);
		showMessageLogButton.setAccelerator(javax.swing.KeyStroke.getKeyStroke( 
														java.awt.event.KeyEvent.VK_S,
														java.awt.Event.CTRL_MASK));
	
		setText("View");
		setFont( font );
		setMnemonic('v');
	
	
		add( coreRadioButtonMenuItem );
		add( lmRadioButtonMenuItem );
		add( capControlRadioButtonMenuItem );
		add( systemRadioButtonMenuItem );
		
		add( separator );
		add( showMessageLogButton );
		add( refreshMenuItem );




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
					if( e.getKeyCode() == KeyEvent.VK_O && e.isControlDown() )
					{
						coreRadioButtonMenuItem.doClick();
						return true;
					}
					else if( e.getKeyCode() == KeyEvent.VK_L && e.isControlDown() )
					{
						lmRadioButtonMenuItem.doClick();
						return true;
					}
					else if( e.getKeyCode() == KeyEvent.VK_T && e.isControlDown() )
					{
						capControlRadioButtonMenuItem.doClick();
						return true;
					}
					else if( e.getKeyCode() == KeyEvent.VK_Y && e.isControlDown() )
					{
						systemRadioButtonMenuItem.doClick();
						return true;
					}
					else if( e.getKeyCode() == KeyEvent.VK_R && e.isControlDown() )
					{
						refreshMenuItem.doClick();
						return true;
					}
					else if( e.getKeyCode() == KeyEvent.VK_S && e.isControlDown() )
					{
						showMessageLogButton.doClick();
						return true;
					}

					//its this the last handling of the KeyEvent in this KeyboardFocusManager?
					return false;
				}
			});	
	}
}
