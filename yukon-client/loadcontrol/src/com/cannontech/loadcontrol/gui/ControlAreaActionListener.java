package com.cannontech.loadcontrol.gui;

/**
 * Insert the type's description here.
 * Creation date: (2/2/2001 12:04:22 PM)
 * @author: 
 */

public class ControlAreaActionListener implements java.awt.event.ActionListener
{
	private ControlAreaListener modelListener[] = new ControlAreaListener[0];
	public static final String ALL_CONTROL_AREAS = "All Control Areas";
	public static final String ENERGY_EXCHANGE = "Energy Exchange";
/**
 * CapBankActionListener constructor comment.
 */
public ControlAreaActionListener() {
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
 * Creation date: (7/31/2001 12:08:25 PM)
 * @return com.cannontech.loadcontrol.gui.ControlAreaListener[]
 */
public ControlAreaListener[] getModelListener() {
	return modelListener;
}
/**
 * Insert the method's description here.
 * Creation date: (2/2/2001 3:06:02 PM)
 * @param e java.awt.event.ActionEvent
 */
private void jComboBox_ActionPerformed(java.awt.event.ActionEvent e) 
{
	javax.swing.JComboBox comboBox = (javax.swing.JComboBox)e.getSource();

	if( comboBox.getSelectedItem() != null && getModelListener() != null )
	{
		if( comboBox.getSelectedItem() instanceof String )
		{
			for( int i = 0; i < getModelListener().length; i++ )
			{
				getModelListener()[i].setCurrentView(comboBox.getSelectedItem().toString());
			}

		}		
		else
			com.cannontech.clientutils.CTILogger.info("*** A String object was not found in the LoadControl's JComboBox, the class found was : " + comboBox.getSelectedItem().getClass() );
	}
		
}
/**
 * Insert the method's description here.
 * Creation date: (7/31/2001 12:08:25 PM)
 * @param newModelListener com.cannontech.loadcontrol.gui.ControlAreaListener[]
 */
public void setModelListener(ControlAreaListener[] newModelListener) {
	modelListener = newModelListener;
}
}
