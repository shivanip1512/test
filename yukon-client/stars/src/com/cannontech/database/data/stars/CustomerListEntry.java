package com.cannontech.database.data.stars;

import java.sql.SQLException;

import com.cannontech.database.db.DBPersistent;

/**
 * <p>Title: CustomerListEntry.java</p>
 * <p>Description: </p>
 * <p>Generation Time: Aug 13, 2002 4:00:06 PM</p>
 * <p>Company: Cannon Technologies Inc.</p>
 * @author yao
 * @version 1.0
 */
public class CustomerListEntry extends DBPersistent {
	
	private com.cannontech.database.db.stars.CustomerListEntry customerListEntry = null;
	
	public void setEntryID(Integer newID) {
		getCustomerListEntry().setEntryID( newID );
	}
	
	public void setDbConnection(java.sql.Connection conn) {
		super.setDbConnection(conn);
		getCustomerListEntry().setDbConnection(conn);
	}

	/**
	 * @see com.cannontech.database.db.DBPersistent#add()
	 */
	public void add() throws SQLException {
		getCustomerListEntry().add();
	}

	/**
	 * @see com.cannontech.database.db.DBPersistent#delete()
	 */
	public void delete() throws SQLException {
		getCustomerListEntry().delete();
	}

	/**
	 * @see com.cannontech.database.db.DBPersistent#retrieve()
	 */
	public void retrieve() throws SQLException {
		getCustomerListEntry().retrieve();
	}

	/**
	 * @see com.cannontech.database.db.DBPersistent#update()
	 */
	public void update() throws SQLException {
		getCustomerListEntry().update();
	}
	
	public static com.cannontech.database.db.stars.CustomerListEntry[] getAllListEntries(Integer listID) {
    	java.sql.Connection conn = null;

    	try {
            conn = com.cannontech.database.PoolManager.getInstance().getConnection(
                        com.cannontech.common.util.CtiUtilities.getDatabaseAlias() );
            if (conn == null) return null;
            
            com.cannontech.database.db.stars.CustomerSelectionList list =
            		new com.cannontech.database.db.stars.CustomerSelectionList();
            list.setListID( listID );
            list.setDbConnection(conn);
            list.retrieve();
            
            return com.cannontech.database.db.stars.CustomerListEntry.getAllListEntries(list, conn);
    	}
    	catch (Exception e) {
    		e.printStackTrace();
    	}
    	finally {
    		try {
    			if (conn != null) conn.close();
    		}
    		catch (Exception e) {
    			e.printStackTrace();
    		}
    	}
    	
    	return null;
	}
	
	public static Integer getListEntryID(Integer listID, String yukonDef) {
    	java.sql.Connection conn = null;

    	try {
            conn = com.cannontech.database.PoolManager.getInstance().getConnection(
                        com.cannontech.common.util.CtiUtilities.getDatabaseAlias() );
            if (conn == null) return null;
            
            return com.cannontech.database.db.stars.CustomerListEntry.getListEntryID(listID, yukonDef, conn);
    	}
    	catch (Exception e) {
    		e.printStackTrace();
    	}
    	finally {
    		try {
    			if (conn != null) conn.close();
    		}
    		catch (Exception e) {
    			e.printStackTrace();
    		}
    	}
    	
    	return null;
	}

	/**
	 * Returns the customerListEntry.
	 * @return com.cannontech.database.db.stars.CustomerListEntry
	 */
	public com.cannontech.database.db.stars.CustomerListEntry getCustomerListEntry() {
		if (customerListEntry == null)
			customerListEntry = new com.cannontech.database.db.stars.CustomerListEntry();
		return customerListEntry;
	}

	/**
	 * Sets the customerListEntry.
	 * @param customerListEntry The customerListEntry to set
	 */
	public void setCustomerListEntry(
		com.cannontech.database.db.stars.CustomerListEntry customerListEntry) {
		this.customerListEntry = customerListEntry;
	}

}
