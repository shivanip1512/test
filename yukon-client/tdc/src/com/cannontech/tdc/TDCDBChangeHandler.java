package com.cannontech.tdc;

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
class TDCDBChangeHandler
{
	public TDCDBChangeHandler()
	{
		super();
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
	public void processDBChangeMsg( DBChangeMsg msg, TDCMainPanel tdcPan )
	{	
		TDCMainFrame.messageLog.addMessage(
				"Received a Database Change Message from : " +  msg.getUserName() + 
				" at " + msg.getSource(), 
				MessageBoxFrame.INFORMATION_MSG );
				
		//be sure the display we are looking at has data that changed 
		if( !hasChangedItem(msg, tdcPan) )
			return;

		
		if( !tdcPan.isClientDisplay() 
			 && !Display.isReadOnlyDisplay(tdcPan.getTableDataModel().getCurrentDisplayNumber()) )
		{
         if( msg.getDatabase() == DBChangeMsg.CHANGE_ALARM_CATEGORY_DB )
         {
            synchronized( tdcPan.getTableDataModel() )
            {
               //just a try, work in nearly all cases!
               int i = tdcPan.getJComboCurrentDisplay().getSelectedIndex();
               tdcPan.initComboCurrentDisplay();
               tdcPan.getJComboCurrentDisplay().setSelectedIndex( i );
            }   
         }
         else
         {
            // fire the refresh button
            tdcPan.executeRefresh_Pressed();
         }
	         
	
	
		}
	
	}


}

