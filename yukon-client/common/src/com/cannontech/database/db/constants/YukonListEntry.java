package com.cannontech.database.db.constants;

import java.sql.SQLException;

import com.cannontech.common.util.CtiUtilities;
import com.cannontech.database.db.CTIDbChange;
import com.cannontech.database.db.DBPersistent;
import com.cannontech.database.incrementer.NextValueHelper;
import com.cannontech.dispatch.DbChangeCategory;
import com.cannontech.dispatch.DbChangeHelper;
import com.cannontech.dispatch.DbChangeType;
import com.cannontech.messaging.message.dispatch.DBChangeMessage;
import com.cannontech.spring.YukonSpringHook;

public class YukonListEntry extends DBPersistent implements CTIDbChange {
    
	private Integer entryID = null;
	private Integer listID = new Integer(CtiUtilities.NONE_ZERO_ID);
	private Integer entryOrder = new Integer(0);
	private String entryText = "";
	private Integer yukonDefID = new Integer(CtiUtilities.NONE_ZERO_ID);
	
	public static final String TABLE_NAME = "YukonListEntry";
	
	public static final String[] CONSTRAINT_COLUMNS = { "EntryID" };
	
	public static final String[] SETTER_COLUMNS = {
		"ListID", "EntryOrder", "EntryText", "YukonDefinitionID"
	};
	
	/**
	 * Constructor for YukonListEntry.
	 */
	public YukonListEntry() {
		super();
	}

	/**
	 * @see com.cannontech.database.db.DBPersistent#add()
	 */
	public void add() throws SQLException {
		if (getEntryID() == null)
			setEntryID(getNextEntryID());
		
		Object[] addValues = {
			getEntryID(), getListID(), getEntryOrder(), getEntryText(), getYukonDefID()
		};
		
		add( TABLE_NAME, addValues );
	}

	/**
	 * @see com.cannontech.database.db.DBPersistent#delete()
	 */
	public void delete() throws SQLException {
		Object[] constraintValues = { getEntryID() };
		
		delete( TABLE_NAME, CONSTRAINT_COLUMNS, constraintValues );
	}

	/**
	 * @see com.cannontech.database.db.DBPersistent#retrieve()
	 */
	public void retrieve() throws SQLException {
		Object[] constraintValues = { getEntryID() };
		
		Object[] results = retrieve( SETTER_COLUMNS, TABLE_NAME, CONSTRAINT_COLUMNS, constraintValues );
		
		if( results.length == SETTER_COLUMNS.length )
		{
			setListID( (Integer) results[0] );
			setEntryOrder( (Integer) results[1] );
			setEntryText( (String) results[2] );
			setYukonDefID( (Integer) results[3] );
		}
		else
			throw new Error(getClass() + " - Incorrect Number of results retrieved");
	}

	/**
	 * @see com.cannontech.database.db.DBPersistent#update()
	 */
	public void update() throws SQLException {
		Object[] constraintValues = { getEntryID() };
		
		Object[] setValues = {
			getListID(), getEntryOrder(), getEntryText(), getYukonDefID()
		};
		
		update( TABLE_NAME, SETTER_COLUMNS, setValues, CONSTRAINT_COLUMNS, constraintValues );
	}
	
	public static Integer getNextEntryID() {
        NextValueHelper nextValueHelper = YukonSpringHook.getNextValueHelper();
        return nextValueHelper.getNextValue("YukonListEntry");
	}
	
	public static void deleteAllListEntries(Integer listID, java.sql.Connection conn) {
		java.sql.PreparedStatement stmt = null;
		String sql = "DELETE FROM " + TABLE_NAME + " WHERE ListID = ?";
		
		try {
			stmt = conn.prepareStatement( sql );
			stmt.setInt(1, listID.intValue());
			stmt.execute();
		}
		catch (SQLException e) {
			e.printStackTrace();
		}
		finally {
			try {
				if (stmt != null) stmt.close();
			}
			catch (SQLException e) {}
		}
	}

	/**
	 * Returns the entryID.
	 * @return Integer
	 */
	public Integer getEntryID() {
		return entryID;
	}

	/**
	 * Returns the entryOrder.
	 * @return Integer
	 */
	public Integer getEntryOrder() {
		return entryOrder;
	}

	/**
	 * Returns the entryText.
	 * @return String
	 */
	public String getEntryText() {
		return entryText;
	}

	/**
	 * Returns the listID.
	 * @return Integer
	 */
	public Integer getListID() {
		return listID;
	}

	/**
	 * Returns the yukonDefID.
	 * @return Integer
	 */
	public Integer getYukonDefID() {
		return yukonDefID;
	}

	/**
	 * Sets the entryID.
	 * @param entryID The entryID to set
	 */
	public void setEntryID(Integer entryID) {
		this.entryID = entryID;
	}

	/**
	 * Sets the entryOrder.
	 * @param entryOrder The entryOrder to set
	 */
	public void setEntryOrder(Integer entryOrder) {
		this.entryOrder = entryOrder;
	}

	/**
	 * Sets the entryText.
	 * @param entryText The entryText to set
	 */
	public void setEntryText(String entryText) {
		this.entryText = entryText;
	}

	/**
	 * Sets the listID.
	 * @param listID The listID to set
	 */
	public void setListID(Integer listID) {
		this.listID = listID;
	}

	/**
	 * Sets the yukonDefID.
	 * @param yukonDefID The yukonDefID to set
	 */
	public void setYukonDefID(Integer yukonDefID) {
		this.yukonDefID = yukonDefID;
	}

    @Override
    public DBChangeMessage[] getDBChangeMsgs(DbChangeType dbChangeType) {
        
        DBChangeMessage[] msgs = {
            DbChangeHelper.newDbChange(dbChangeType, DbChangeCategory.YUKON_LIST_ENTRY, entryID)
        };

        return msgs;
    }

}
