package com.cannontech.tdc.utils;

import java.awt.Component;
import java.awt.Frame;
import java.util.Date;

import com.cannontech.common.gui.panel.ManualChangeJPanel;

/**
 * @author rneuharth
 *
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates.
 * To enable and disable the creation of type comments go to
 * Window>Preferences>Java>Code Generation.
 */
public class DateTimeUserQuery {
	
	private Component compRelativeTo = null;
	private Frame parentFrame = null;
	private ManualChangeJPanel panel = null;

	/**
	 * Constructor for DateTimeUserQuery.
	 */
	public DateTimeUserQuery( Component relTo_, Frame parent_ ) {
		super();
		compRelativeTo = relTo_;
		parentFrame = parent_;
	}


	public boolean queryUser( Date startingDate_, Date stoppingDate_ ) {

		final javax.swing.JDialog d = new javax.swing.JDialog( parentFrame );
		boolean retValue = false;
		
		d.setTitle("Date Range");
		panel = new ManualChangeJPanel( ManualChangeJPanel.MODE_DATE_ONLY )
		{
			public void exit()
			{
				d.dispose();
			}			
		};

		panel.setStartLabel("Min. Time:");
		panel.setStopLabel("Max. Time:");
		
		panel.setInitialDates(
				 startingDate_,
				 stoppingDate_ );
		
		d.setModal(true);
		d.setContentPane(panel);
		d.pack();
		d.setLocationRelativeTo( compRelativeTo );
		d.show();

		if( panel.getChoice() == ManualChangeJPanel.OK_CHOICE )
		{
			retValue = true; 
		}
		else
			retValue = false; 


		//destroy the JDialog
		d.dispose();

		return retValue;
	}



	public Date getStartDate() {
		return panel.getStartTime();
	}

	public Date getStopDate() {
		return panel.getStopTime();
	}


}
