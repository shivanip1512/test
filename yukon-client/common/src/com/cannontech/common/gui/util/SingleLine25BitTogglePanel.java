package com.cannontech.common.gui.util;

/**
 * This type was created in VisualAge.
 */
import javax.swing.JCheckBox;
import javax.swing.JToggleButton;
public class SingleLine25BitTogglePanel extends javax.swing.JPanel implements java.awt.event.ActionListener {

	//The CheckBoxes
	JCheckBox[] checkBoxes;
	JToggleButton[] toggleButtons;
	javax.swing.JLabel[] labels;

	//List of item listeners
	java.util.Vector actionListeners = new java.util.Vector();
	
/**
 * SingleLineBitTogglePanel constructor comment.
 */
public SingleLine25BitTogglePanel() {
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
 * Insert the method's description here.
 * Creation date: (7/17/2001 2:21:33 PM)
 * @return javax.swing.JLabel[]
 */
public javax.swing.JLabel[] getLabels() {
	return labels;
}
/**
 * Insert the method's description here.
 * Creation date: (7/16/2001 4:32:40 PM)
 * @return javax.swing.JToggleButton
 */
public JToggleButton[] getToggleButtons() {
	return toggleButtons;
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
public void initialize()
{

	toggleButtons = new JToggleButton[25];
	labels = new javax.swing.JLabel[25];
	java.awt.GridLayout gl = new java.awt.GridLayout(1, 25);
	gl.setHgap(3);
	setLayout(gl);

	for (int i = 0; i <= 24; i++)
	{

		javax.swing.JPanel j = new javax.swing.JPanel(new java.awt.GridLayout(2, 1));
		javax.swing.JLabel num = null;

		//sets labels above numbers to empty string
		
		num = new javax.swing.JLabel(" ");
		
		num.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
		num.setFont(new java.awt.Font("dialog", 0, 9));
		j.add(num);

		JToggleButton t = new JToggleButton("");
		t.setPreferredSize(new java.awt.Dimension(1, 1));

		j.add(t);

		add(j);

		t.addActionListener(this);
		toggleButtons[i] = t;
		//keeps track of labels
		labels[i] = num;
		
		}

}
/**
 * This method was created in VisualAge.
 * @param value int
 */
public void setValue(int value) {

	double dval = (double) value;

	for( int i = 0; i < 25; i++ )
	{
		if( (value & 1) == 0 )
			toggleButtons[i].setSelected(false);
		else
			toggleButtons[i].setSelected(true);
		
		value >>= 1;
	}
}
}
