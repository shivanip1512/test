package com.cannontech.yc.gui.menu;

/**
 * This type was created in VisualAge.
 */
import javax.swing.JMenuItem;
import javax.swing.JSeparator;

public class YCViewMenu extends javax.swing.JMenu
{
	public JSeparator separator1;

	public javax.swing.JMenuItem findMenuItem;
	public JMenuItem reloadMenuItem;
	public JMenuItem clearMenuItem;
	public JMenuItem deleteSerialNumberMenuItem;
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

	findMenuItem = new JMenuItem();
	findMenuItem.setFont(f);
	findMenuItem.setText("Find...");
	findMenuItem.setMnemonic('f');
	findMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F, java.awt.Event.CTRL_MASK));

	
	reloadMenuItem = new JMenuItem();
	reloadMenuItem.setFont(f);
	reloadMenuItem.setText("Reload Devices");
	reloadMenuItem.setMnemonic('r');
	reloadMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F5, 0));

	clearMenuItem = new JMenuItem();
	clearMenuItem.setFont(f);
	clearMenuItem.setText("Clear Screen");
	clearMenuItem.setMnemonic('c');
	clearMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke( java.awt.event.KeyEvent.VK_C, java.awt.Event.CTRL_MASK));
	
	deleteSerialNumberMenuItem = new JMenuItem();
	deleteSerialNumberMenuItem.setFont(f);
	deleteSerialNumberMenuItem.setText("Delete Serial #...");
	deleteSerialNumberMenuItem.setMnemonic('d');
	deleteSerialNumberMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_DELETE, 0));

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

	add( findMenuItem );
	add( reloadMenuItem );
	add( clearMenuItem );
	add( deleteSerialNumberMenuItem );
	//add( separator1 );
	//add( showCommandLogButton );
}
}
