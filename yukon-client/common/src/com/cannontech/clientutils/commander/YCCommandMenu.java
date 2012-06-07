package com.cannontech.clientutils.commander;

/**
 * This type was created in VisualAge.
 */
import javax.swing.JMenuItem;
import javax.swing.JSeparator;

public class YCCommandMenu extends javax.swing.JMenu {
	public JSeparator separator1;
	public JSeparator separator2;

	//public javax.swing.JCheckBoxMenuItem queueMCTCommandsMenuItem;
	//public javax.swing.JCheckBoxMenuItem areYouSureItem;
	public JMenuItem stopMenuItem;
	public JMenuItem executeMenuItem;
	public JMenuItem locateRoute;
	public JMenuItem installAddressing;
	public JMenuItem downloadSchedule;
	public JMenuItem editCustomCommandFile;
	
	public JMenuItem advancedOptionsMenuItem;	
/**
 * YukonCommanderFileMenu constructor comment.
 */
public YCCommandMenu() {
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

	executeMenuItem = new JMenuItem();
	executeMenuItem.setFont( f );
	executeMenuItem.setText("Execute");
	executeMenuItem.setMnemonic('e');

	stopMenuItem = new JMenuItem();
	stopMenuItem.setFont(f);
	stopMenuItem.setText("Stop");
	stopMenuItem.setMnemonic('s');

	locateRoute = new JMenuItem();
	locateRoute.setFont(f);
	locateRoute.setText("Locate Route");
	locateRoute.setMnemonic('l');

	downloadSchedule = new JMenuItem();
	downloadSchedule.setFont(f);
	downloadSchedule.setText("Download TOU Schedule");
	downloadSchedule.setMnemonic('d');

	installAddressing = new JMenuItem();
	installAddressing.setFont(f);
	installAddressing.setText("Install Addressing");
	installAddressing.setMnemonic('i');
	
	//queueMCTCommandsMenuItem = new javax.swing.JCheckBoxMenuItem();
	//queueMCTCommandsMenuItem.setFont( f );
	//queueMCTCommandsMenuItem.setSelected(false);
	//queueMCTCommandsMenuItem.setText("Queue MCT Commands");
	//queueMCTCommandsMenuItem.setMnemonic('q');
	////queueMCTCommandsMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke( java.awt.event.KeyEvent.VK_Q, java.awt.Event.CTRL_MASK));

	//areYouSureItem = new javax.swing.JCheckBoxMenuItem();
	//areYouSureItem.setFont( f );
	//areYouSureItem.setSelected(false);
	//areYouSureItem.setText("Confirm Execute");
	//areYouSureItem.setMnemonic('c');

	editCustomCommandFile = new JMenuItem();
	editCustomCommandFile.setFont(f);
	editCustomCommandFile.setText("Edit Custom Commands");
	editCustomCommandFile.setMnemonic('d');	

	advancedOptionsMenuItem = new JMenuItem();
	advancedOptionsMenuItem.setFont( f );
	advancedOptionsMenuItem.setText("Options...");
	advancedOptionsMenuItem.setMnemonic('o');

	setFont( f );
	setText("Command");
	setMnemonic('c');

	add( executeMenuItem );
	add( stopMenuItem );
	add( locateRoute );
	add( downloadSchedule);
	add( installAddressing );
	add( separator1 );
	add( editCustomCommandFile );
	add( advancedOptionsMenuItem );
	//add( queueMCTCommandsMenuItem );
	//add( areYouSureItem );
}
}
