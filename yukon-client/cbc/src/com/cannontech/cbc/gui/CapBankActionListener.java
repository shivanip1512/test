package com.cannontech.cbc.gui;

/**
 * Insert the type's description here.
 * Creation date: (2/2/2001 12:04:22 PM)
 * @author: 
 */

public class CapBankActionListener implements java.awt.event.ActionListener
{
	private SubBusTableModel subBusTableModel = null;
/**
 * CapBankActionListener constructor comment.
 */
public CapBankActionListener() {
	super();
}
	/**
	 * Invoked when an action occurs.
	 */
public void actionPerformed(java.awt.event.ActionEvent e) 
{
	if( e.getSource() instanceof javax.swing.JComboBox )
	{
		jComboBox_ActionPerformed( e );		
	}
	
}
/**
 * Insert the method's description here.
 * Creation date: (2/5/2001 1:29:37 PM)
 * @return com.cannontech.cbc.gui.SubBusTableModel
 */
public SubBusTableModel getSubBusTableModel() {
	return subBusTableModel;
}
/**
 * Insert the method's description here.
 * Creation date: (2/2/2001 3:06:02 PM)
 * @param e java.awt.event.ActionEvent
 */
private void jComboBox_ActionPerformed(java.awt.event.ActionEvent e) 
{
	javax.swing.JComboBox comboBox = (javax.swing.JComboBox)e.getSource();

	if( comboBox.getSelectedItem() != null && getSubBusTableModel() != null )
	{
		getSubBusTableModel().setFilter( comboBox.getSelectedItem().toString() );
	}
		
}
/**
 * Insert the method's description here.
 * Creation date: (2/5/2001 1:29:37 PM)
 * @param newStrategyTableModel com.cannontech.cbc.gui.SubBusTableModel
 */
public void setSubBusTableModel(SubBusTableModel newSubBusTableModel) 
{
	subBusTableModel= newSubBusTableModel;
}
}
