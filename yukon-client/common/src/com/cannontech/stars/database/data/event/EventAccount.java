package com.cannontech.stars.database.data.event;

import java.util.ArrayList;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.constants.YukonListEntry;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.database.SqlStatement;
import com.cannontech.database.db.user.YukonUser;


/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: </p>
 * @author unascribed
 * @version 1.0
 */

public class EventAccount extends EventBase {

    private com.cannontech.stars.database.db.event.EventAccount eventAccount = null;
    
    public EventAccount() {
        super();
    }

    public void setEventID(Integer newID) {
    	super.setEventID(newID);
        getEventAccount().setEventID(newID);
    }

    public void setDbConnection(java.sql.Connection conn) {
        super.setDbConnection(conn);
        getEventAccount().setDbConnection(conn);
    }

    public void delete() throws java.sql.SQLException {
        getEventAccount().delete();
        super.delete();
    }

    public void add() throws java.sql.SQLException {
    	super.add();
    	
    	getEventAccount().setEventID( getEventBase().getEventID() );
        getEventAccount().add();
    }

    public void update() throws java.sql.SQLException {
    	super.update();
        getEventAccount().update();
    }

    public void retrieve() throws java.sql.SQLException {
    	super.retrieve();
        getEventAccount().retrieve();
    }
    
 	/**
	 * Returns the eventAccount.
	 * @return com.cannontech.stars.database.db.event.EventAccount
	 */
	public com.cannontech.stars.database.db.event.EventAccount getEventAccount() {
		if (eventAccount == null)
			eventAccount = new com.cannontech.stars.database.db.event.EventAccount();
		return eventAccount;
	}

	/**
	 * Sets the eventAccount.
	 * @param eventAccount The eventAccount to set
	 */
	public void setEventAccount(
		com.cannontech.stars.database.db.event.EventAccount eventAccount) {
		this.eventAccount = eventAccount;
	}

    public static ArrayList<EventAccount> retrieveEventAccounts(int accountID)
    {
        ArrayList<EventAccount> eventAccounts = new ArrayList<EventAccount>();
        String sql = "SELECT EB.EVENTID, EB.USERID, SYSTEMCATEGORYID, ACTIONID, EVENTTIMESTAMP, YLE.ENTRYTEXT, YU.USERNAME " +
                    " FROM " + com.cannontech.stars.database.db.event.EventBase.TABLE_NAME + " EB, " +
                    com.cannontech.stars.database.db.event.EventAccount.TABLE_NAME + " EA, " +
                    YukonListEntry.TABLE_NAME + " YLE, " +
                    YukonUser.TABLE_NAME + " YU " +
                    " WHERE EB.EVENTID = EA.EVENTID " +
                    " AND ACCOUNTID = " + accountID +
                    " AND YLE.ENTRYID = EB.ACTIONID" +
                    " AND YU.USERID = EB.USERID" +
                    " ORDER BY EVENTTIMESTAMP DESC ";
        SqlStatement stmt = new SqlStatement( sql, CtiUtilities.getDatabaseAlias() );
        
        try {
            stmt.execute();
            for (int i = 0; i < stmt.getRowCount(); i++)
            {
                EventAccount eventAccount = new EventAccount();
                eventAccount.setEventID(new Integer( ((java.math.BigDecimal)stmt.getRow(i)[0]).intValue() ));
                eventAccount.getEventBase().setUserID(new Integer( ((java.math.BigDecimal)stmt.getRow(i)[1]).intValue() ));
                eventAccount.getEventBase().setSystemCategoryID(new Integer( ((java.math.BigDecimal)stmt.getRow(i)[2]).intValue() ));
                eventAccount.getEventBase().setActionID(new Integer( ((java.math.BigDecimal)stmt.getRow(i)[3]).intValue() ));
                eventAccount.getEventBase().setEventTimestamp((java.util.Date) stmt.getRow(i)[4] );
                eventAccount.getEventAccount().setAccountID(new Integer( accountID));
                eventAccount.setActionText(stmt.getRow(i)[5].toString());
                eventAccount.setUserName(stmt.getRow(i)[6].toString());
                eventAccounts.add(eventAccount);
            }
        }
        catch( Exception e ) {
            CTILogger.error( e.getMessage(), e );
        }
        
        return eventAccounts;
    }
    
}