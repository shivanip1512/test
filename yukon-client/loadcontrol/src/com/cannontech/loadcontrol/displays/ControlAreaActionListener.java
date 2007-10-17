package com.cannontech.loadcontrol.displays;

import javax.swing.event.EventListenerList;


/**
 * Insert the type's description here.
 * Creation date: (2/2/2001 12:04:22 PM)
 * @author: 
 */

public class ControlAreaActionListener implements java.awt.event.ActionListener
{
	//contains IControlAreaListener
	private EventListenerList listeners = new EventListenerList();

	public static final String SEL_ALL_CONTROL_AREAS	= "All Control Areas";
	public static final String SEL_ACTIVE_AREAS			= "Active Areas";
	public static final String SEL_INACTIVE_AREAS		= "Inactive Areas";
	public static final String SEL_SCHEDULED_AREAS		= "Scheduled Areas";
	public static final String SEL_CNTRL_PT_HISTORY		= "Control Point History";


	/**
	 * ControlAreaActionListener constructor comment.
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
	 * Creation date: (2/2/2001 3:06:02 PM)
	 * @param e java.awt.event.ActionEvent
	 */
	private void jComboBox_ActionPerformed(java.awt.event.ActionEvent e) 
	{
		javax.swing.JComboBox comboBox = (javax.swing.JComboBox)e.getSource();
	
		if( comboBox.getSelectedItem() != null )
		{
			if( comboBox.getSelectedItem() instanceof LCDisplayItem )
			{
				LCDisplayItem dispItem = (LCDisplayItem)comboBox.getSelectedItem();
				
				for( int i = 0; i < listeners.getListenerCount(IControlAreaListener.class); i++ )
				{
					IControlAreaListener lst = listeners.getListeners(IControlAreaListener.class)[i];
						
					lst.setCurrentDisplay( dispItem );
				}
			}		
			else
				com.cannontech.clientutils.CTILogger.info(
					"*** An object was not recognized in the LoadControl's " + 
					"JComboBox, the class found was : " + comboBox.getSelectedItem().getClass() );
		}
			
	}
	
	/**
	 * Insert the method's description here.
	 * Creation date: (7/31/2001 12:08:25 PM)
	 * @param newModelListener com.cannontech.loadcontrol.gui.ControlAreaListener[]
	 */
	public void addControlAreaListener(IControlAreaListener newModelListener) 
	{
		listeners.add( IControlAreaListener.class, newModelListener );
	}
}
