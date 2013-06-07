package com.cannontech.dbeditor;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.editor.PropertyPanel;
import com.cannontech.database.data.lite.LiteBase;
import com.cannontech.database.data.lite.LiteFactory;
import com.cannontech.database.db.DBPersistent;
import com.cannontech.dispatch.DbChangeType;
import com.cannontech.messaging.message.dispatch.DBChangeMessage;

/**
 * @author rneuharth
 *
 * A simple class to handle DB changes for a PropertyPanel
 */
public class DBChangeGUIHandler {
	private PropertyPanel thePanel = null;
	private StringBuffer txtMsg = null;

	public static final int[] HANDLED_DBS =	{ 
		DBChangeMessage.CHANGE_PAO_DB,
		DBChangeMessage.CHANGE_POINT_DB,
		DBChangeMessage.CHANGE_STATE_GROUP_DB,
		DBChangeMessage.CHANGE_GRAPH_DB,
		DBChangeMessage.CHANGE_NOTIFICATION_GROUP_DB,
		DBChangeMessage.CHANGE_ALARM_CATEGORY_DB,
		DBChangeMessage.CHANGE_CONTACT_DB
	};
		
	/**
	 * Constructor for DBChangeGUIHandler.
	 */
	public DBChangeGUIHandler( PropertyPanel panel_, StringBuffer txtMsg_ ) {
		super();
		thePanel = panel_;
		txtMsg = txtMsg_;
	}
	
	private static boolean isHandled( int dbType_ ) {
		for( int i = 0; i < HANDLED_DBS.length; i++ ) {
			if( HANDLED_DBS[i] == dbType_ ) {
				return true;
			}
		}
		return false;
	}
	
	public void handleGUIChange( DBChangeMessage msg ) {

	    //Check change type first, only create lite object from panel if change type is one we care about.
        if( (msg.getDbChangeType() == DbChangeType.DELETE || 
                msg.getDbChangeType() == DbChangeType.UPDATE) ) {

            LiteBase liteObject = null;
    		if( thePanel.getOriginalObjectToEdit() instanceof DBPersistent ) {
    			liteObject = LiteFactory.createLite( (DBPersistent)thePanel.getOriginalObjectToEdit() );
    		} else {
    			throw new IllegalArgumentException("Trying to handle a non DBPersitent class for a DBChange in the GUI"); 
    		}

    		if( liteObject.getLiteID() == msg.getId()) {
    			if( !isHandled(msg.getDatabase()) ) {
    				CTILogger.info("**** Unable to find matching object for the DBChangeMsg = " + msg.getDatabase() 
                        + " and the lite object type is = " + liteObject.getLiteType() );
    				return;
    			}
    			txtMsg.append(". Editing of '" + liteObject.toString() + "' was canceled.");
    			thePanel.fireCancelButtonPressed();			
    		}
        }
	}
}
