package com.cannontech.dbeditor.menu;

/**
 * This type was created in VisualAge.
 */
import com.cannontech.common.gui.util.CommandableMenuItem;

public class FileMenu extends javax.swing.JMenu {

	public CommandableMenuItem exitMenuItem;
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

	exitMenuItem = new CommandableMenuItem("Exit");
	exitMenuItem.setFont( font );
	exitMenuItem.setMnemonic('x');
	exitMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke( 
													java.awt.event.KeyEvent.VK_X,
													java.awt.Event.CTRL_MASK));

	setText("File");
	setFont( font );
	setMnemonic('f');
	add(exitMenuItem);
}
}
