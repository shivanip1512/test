package com.cannontech.common.gui.util;

/**
 * This type was created in VisualAge.
 */
import javax.swing.JCheckBox;
import javax.swing.JToggleButton;
public class SingleLine16BitTogglePanel extends javax.swing.JPanel implements java.awt.event.ActionListener {

	//The CheckBoxes
	JCheckBox[] checkBoxes;
	JToggleButton[] toggleButtons;

	//List of item listeners
	java.util.Vector actionListeners = new java.util.Vector();
/**
 * SingleLineBitTogglePanel constructor comment.
 */
public SingleLine16BitTogglePanel() {
	super();
	initialize();
}
/**
 * This method was created in VisualAge.
 * @param event java.awt.event.ActionEvent
 */
public void actionPerformed(java.awt.event.ActionEvent event) {
	fireActionEvent( new java.awt.event.ActionEvent(this, 0, "null") );
}
/**
 * This method was created in VisualAge.
 * @param l java.awt.event.ItemListener
 */
public void addActionListener(java.awt.event.ActionListener l) {
	actionListeners.addElement(l);
}
/**
 * This method was created in VisualAge.
 * @param e java.awt.event.ItemEvent
 */
protected void fireActionEvent(java.awt.event.ActionEvent e) {
	for( int i = actionListeners.size() - 1; i >= 0; i-- )
		((java.awt.event.ActionListener) actionListeners.elementAt(i)).actionPerformed(e);
} 
/**
 * This method was created in VisualAge.
 * @return int
 */
public int getValue() {

	int val = 0;

	for( int i = toggleButtons.length - 1 ; i >= 0 ; i-- )
	{
		val <<= 1;
		
		if( toggleButtons[i].isSelected() )
			val += 1;
	}
	
	return val;
}
/**
 * This method was created in VisualAge.
 */
public void initialize() {
	
	toggleButtons = new JToggleButton[16];
	java.awt.GridLayout gl = new java.awt.GridLayout( 1, 16 );
	gl.setHgap( 3 );
	setLayout( gl );

	for( int i = 0; i <= 15; i++ )
	{

		javax.swing.JPanel j = new javax.swing.JPanel( new java.awt.GridLayout( 2, 1 ) );

		javax.swing.JLabel num = new javax.swing.JLabel((new Integer(i+1)).toString());
		num.setHorizontalAlignment( javax.swing.SwingConstants.CENTER );
		j.add(num);
		

		JToggleButton t = new JToggleButton("");
		t.setPreferredSize( new java.awt.Dimension( 15, 12 ) );
		
		j.add(t);
		
		add(j);

		t.addActionListener(this);
		toggleButtons[i] = t;				
	}

	
}
/**
 * This method was created in VisualAge.
 * @param value int
 */
public void setValue(int value) {

	double dval = (double) value;

	for( int i = 0; i < 16; i++ )
	{
		if( (value & 1) == 0 )
			toggleButtons[i].setSelected(false);
		else
			toggleButtons[i].setSelected(true);
		
		value >>= 1;
	}
}
}
