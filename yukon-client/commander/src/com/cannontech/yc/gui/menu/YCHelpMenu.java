package com.cannontech.yc.gui.menu;

/**
 * This type was created in VisualAge.
 */
import javax.swing.JMenuItem;
import javax.swing.JSeparator;

public class YCHelpMenu extends javax.swing.JMenu
{
	public JSeparator separator1;

	public JMenuItem aboutMenuItem;
	public JMenuItem helpTopicMenuItem;
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


	helpTopicMenuItem = new JMenuItem();
	helpTopicMenuItem.setFont(f);
	helpTopicMenuItem.setText("Help Topics");
	helpTopicMenuItem.setMnemonic('t');
	helpTopicMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F1, 0));

	aboutMenuItem = new JMenuItem();
	aboutMenuItem.setFont(f);
	aboutMenuItem.setText("About");
	aboutMenuItem.setMnemonic('a');
	aboutMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke( java.awt.event.KeyEvent.VK_A, java.awt.Event.CTRL_MASK));
	
	setFont( f );
	setText("Help");
	setMnemonic('h');


	add( helpTopicMenuItem );
	add( aboutMenuItem );
}
}
