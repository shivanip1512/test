package com.cannontech.database.data.stars.event;


/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: </p>
 * @author unascribed
 * @version 1.0
 */

public class EventAccount extends EventBase {

    private com.cannontech.database.db.stars.event.EventAccount eventAccount = null;

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
	 * @return com.cannontech.database.db.stars.event.EventAccount
	 */
	public com.cannontech.database.db.stars.event.EventAccount getEventAccount() {
		if (eventAccount == null)
			eventAccount = new com.cannontech.database.db.stars.event.EventAccount();
		return eventAccount;
	}

	/**
	 * Sets the eventAccount.
	 * @param eventAccount The eventAccount to set
	 */
	public void setEventAccount(
		com.cannontech.database.db.stars.event.EventAccount eventAccount) {
		this.eventAccount = eventAccount;
	}

}