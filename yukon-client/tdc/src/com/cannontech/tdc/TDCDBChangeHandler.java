package com.cannontech.tdc;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Timer;

import com.cannontech.message.dispatch.message.DBChangeMsg;
import com.cannontech.tdc.data.Display;
import com.cannontech.tdc.logbox.MessageBoxFrame;

/**
 * @author rneuharth
 *
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates.
 * To enable and disable the creation of type comments go to
 * Window>Preferences>Java>Code Generation.
 */
class TDCDBChangeHandler implements ActionListener
{
	public static final int DB_REFRESH = 5000; //millis to batch DB changes
	private Timer dbTimer = null;
	
	private TDCMainPanel tdcPan = null;
	
	//a flag to say if we need to refresh from the DB or not
	private boolean needRefresh = false;

	
	public TDCDBChangeHandler( TDCMainPanel tdcPanel_ )
	{
		super();
		
		tdcPan = tdcPanel_;
		dbTimer = new Timer( DB_REFRESH, this );
		dbTimer.start();
	}
	
	public void actionPerformed(ActionEvent e)
	{
		if( e.getSource() == dbTimer
			 && needRefresh )
		{
			synchronized( tdcPan.getTableDataModel() )
			{
				try
				{
					tdcPan.executeRefresh_Pressed();
				}
				finally
				{
					needRefresh = false;
				}
			} //end synch
		}

	}

	private boolean hasChangedItem( DBChangeMsg msg, TDCMainPanel tdcPan )
	{
		if( (msg.getDatabase() == DBChangeMsg.CHANGE_ALARM_CATEGORY_DB ||
			  msg.getDatabase() == DBChangeMsg.CHANGE_POINT_DB ||
			  msg.getDatabase() == DBChangeMsg.CHANGE_PAO_DB ||
			  msg.getDatabase() == DBChangeMsg.CHANGE_STATE_GROUP_DB) && 
			 (msg.getTypeOfChange() == DBChangeMsg.CHANGE_TYPE_DELETE ||
			  msg.getTypeOfChange() == DBChangeMsg.CHANGE_TYPE_UPDATE) )
		{
	
			//search for specific IDs here
			if( msg.getDatabase() == DBChangeMsg.CHANGE_PAO_DB 
	          || msg.getDatabase() == DBChangeMsg.CHANGE_POINT_DB )
			{
				boolean found = false;
				for( int i = 0; i < tdcPan.getTableDataModel().getRowCount(); i++ )
				{
					if( msg.getDatabase() == DBChangeMsg.CHANGE_POINT_DB )
						found |= (tdcPan.getTableDataModel().getPointValue(i).getPointID() 
									 == msg.getId());
	
					if( msg.getDatabase() == DBChangeMsg.CHANGE_PAO_DB )
						found |= (tdcPan.getTableDataModel().getPointValue(i).getDeviceID() 
									 == msg.getId());
									 
					if( found )
						break;
				}
	
				return found;
			}
		
		}		
	
		return false;
	}
	
	/**
	 * Insert the method's description here.
	 * Creation date: (1/21/00 11:46:00 AM)
	 */
	public void processDBChangeMsg( DBChangeMsg msg )
	{	
		TDCMainFrame.messageLog.addMessage(
				"Received a Database Change Message from : " +  msg.getUserName() + 
				" at " + msg.getSource(), 
				MessageBoxFrame.INFORMATION_MSG );
		
		//be sure the display we are looking at has data that changed and
		// it is not a TDC specific change 
		if( !hasChangedItem(msg, tdcPan) 
			&& msg.getDatabase() != DBChangeMsg.CHANGE_TDC_DB )
		{
			return;
		}

		
		if( !tdcPan.isClientDisplay() 
			 && !Display.isReadOnlyDisplay(tdcPan.getTableDataModel().getCurrentDisplay().getDisplayNumber()) )
		{
			synchronized( tdcPan.getTableDataModel() )
			{
	         if( msg.getDatabase() == DBChangeMsg.CHANGE_ALARM_CATEGORY_DB
	         	 || msg.getDatabase() == DBChangeMsg.CHANGE_TDC_DB )
	         {
	            //just a try, work in nearly all cases!
	            Object o = tdcPan.getJComboCurrentDisplay().getSelectedItem();
	            tdcPan.initComboCurrentDisplay();
	            tdcPan.getJComboCurrentDisplay().setSelectedItem( o );
	         }
	         else
	         {
	            // fire the refresh button
	            //tdcPan.executeRefresh_Pressed();
	            needRefresh = true;
	         }
			}	         
	
	
		}
	
	}


}

