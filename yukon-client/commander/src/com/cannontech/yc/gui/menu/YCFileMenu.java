package com.cannontech.yc.gui.menu;

/**
 * This type was created in VisualAge.
 */
import javax.swing.JMenuItem;
import javax.swing.JSeparator;

public class YCFileMenu extends javax.swing.JMenu {
	public JSeparator separator1;
	public JSeparator separator2;

	public JMenuItem saveMenuItem;
	public JMenuItem printMenuItem;
	public JMenuItem exitMenuItem;
	public JMenuItem commandSpecificControl;	//CHEATER MODE
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

	saveMenuItem = new JMenuItem();
	saveMenuItem.setFont(f);
	saveMenuItem.setText("Save Output...");
	saveMenuItem.setMnemonic('s');
	saveMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke( java.awt.event.KeyEvent.VK_S, java.awt.Event.CTRL_MASK));

	printMenuItem = new JMenuItem();
	printMenuItem.setFont(f);
	printMenuItem.setText("Print...");
	printMenuItem.setMnemonic('p');
	printMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke( java.awt.event.KeyEvent.VK_P, java.awt.Event.CTRL_MASK));
	
	exitMenuItem = new JMenuItem();
	exitMenuItem.setFont( f );
	exitMenuItem.setText("Exit");
	exitMenuItem.setMnemonic('x');
	exitMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke( java.awt.event.KeyEvent.VK_X, java.awt.Event.CTRL_MASK));

	commandSpecificControl = new JMenuItem();
	commandSpecificControl.setAccelerator(javax.swing.KeyStroke.getKeyStroke( java.awt.event.KeyEvent.VK_F5, java.awt.Event.CTRL_MASK));
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
}
}
