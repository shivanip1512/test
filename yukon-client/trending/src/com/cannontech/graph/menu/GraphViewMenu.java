package com.cannontech.graph.menu;

/**
 * This type was created in VisualAge.
 */
import javax.swing.JMenuItem;
import javax.swing.JSeparator;

public class GraphViewMenu extends javax.swing.JMenu
{
	public JSeparator separator1;
	public JSeparator separator2;

	public javax.swing.ButtonGroup group;
	public javax.swing.JRadioButtonMenuItem lineGraphMenuItem;
	public javax.swing.JRadioButtonMenuItem loadDurationMenuItem;
	public javax.swing.JRadioButtonMenuItem barGraphMenuItem;
	public javax.swing.JRadioButtonMenuItem multiDayLDMenuItem;;
	public JMenuItem refreshMenuItem;
/**
 * YukonCommanderFileMenu constructor comment.
 */
public GraphViewMenu() {
	super();
	initialize();
}
/**
 * This method was created in VisualAge.
 */
private void initialize()
{
	java.awt.Font f = new java.awt.Font("dialog", 0, 14 );
	separator1 = new JSeparator();

	group = new javax.swing.ButtonGroup();

	lineGraphMenuItem = new javax.swing.JRadioButtonMenuItem("Line Graph", true);
	lineGraphMenuItem.setMnemonic('l');
	lineGraphMenuItem.setAccelerator( javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_L, java.awt.event.KeyEvent.CTRL_MASK ));
	lineGraphMenuItem.setFont(f);
	
	barGraphMenuItem = new javax.swing.JRadioButtonMenuItem("Bar Graph", false);
	barGraphMenuItem.setMnemonic('b');
	barGraphMenuItem.setAccelerator( javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_B, java.awt.event.KeyEvent.CTRL_MASK ));
	barGraphMenuItem.setFont(f);

	loadDurationMenuItem = new javax.swing.JRadioButtonMenuItem("Load Duration", false);
	loadDurationMenuItem.setMnemonic('d');
	loadDurationMenuItem.setAccelerator( javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_D, java.awt.event.KeyEvent.CTRL_MASK ));
	loadDurationMenuItem.setFont(f);

	//multiDayLDMenuItem = new javax.swing.JRadioButtonMenuItem("Multi Day Load Duration", false);
	//multiDayLDMenuItem.setMnemonic('m');
	//multiDayLDMenuItem.setAccelerator( javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_M, java.awt.event.KeyEvent.CTRL_MASK ));
	//multiDayLDMenuItem.setFont(f);

	refreshMenuItem = new JMenuItem ("Refresh");
	refreshMenuItem.setMnemonic('r');
	refreshMenuItem.setAccelerator( javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_R, java.awt.event.KeyEvent.CTRL_MASK ));
	refreshMenuItem.setFont(f);
	
	setMnemonic('V');
	setText("View");
	setFont( f);
	
	group.add(lineGraphMenuItem);
	group.add(barGraphMenuItem);
	group.add(loadDurationMenuItem);
	add(lineGraphMenuItem);
	add(barGraphMenuItem);
	add(loadDurationMenuItem);

	add(separator1);
	add(refreshMenuItem);
}
}
