package com.cannontech.dbeditor.menu;

/**
 * This type was created in VisualAge.
 */
import javax.swing.ButtonGroup;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JSeparator;

import com.cannontech.common.gui.util.CommandableMenuItem;


public class ViewMenu extends javax.swing.JMenu 
{
	public JRadioButtonMenuItem coreRadioButtonMenuItem;
	public JRadioButtonMenuItem lmRadioButtonMenuItem;
	public JRadioButtonMenuItem capControlRadioButtonMenuItem;
	public JRadioButtonMenuItem systemRadioButtonMenuItem;
	private ButtonGroup radioButtonGroup;

	private JSeparator separator;
	
	public CommandableMenuItem refreshMenuItem;
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
		
	refreshMenuItem = new CommandableMenuItem("Refresh");
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
}
}
