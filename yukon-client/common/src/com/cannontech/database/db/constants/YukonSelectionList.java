package com.cannontech.database.db.constants;

import java.sql.SQLException;

import com.cannontech.common.util.CtiUtilities;
import com.cannontech.database.db.DBPersistent;

/**
 * @author yao
 *
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates.
 * To enable and disable the creation of type comments go to
 * Window>Preferences>Java>Code Generation.
 */
public class YukonSelectionList extends DBPersistent {
	
	private Integer listID = null;
	private String ordering = "";
	private String selectionLabel = "";
	private String whereIsList = "";
	private String listName = "";
	private String userUpdateAvailable = "";
	
	public static final String TABLE_NAME = "YukonSelectionList";
	
	public static final String[] CONSTRAINT_COLUMNS = { "ListID" };
	
	public static final String[] SETTER_COLUMNS = {
		"Ordering", "SelectionLabel", "WhereIsList",
		"ListName", "UserUpdateAvailable"
	};
	
	public static final String GET_NEXT_LIST_ID_SQL = 
			"SELECT MAX(ListID)+1 FROM " + TABLE_NAME;
	
	public YukonSelectionList() {
		super();
	}

	/**
	 * @see com.cannontech.database.db.DBPersistent#add()
	 */
	public void add() throws SQLException {
		if (getListID() == null)
			setListID( getNextListID(getDbConnection()) );
			
		Object[] addValues = {
			getListID(), getOrdering(), getSelectionLabel(), getWhereIsList(),
			getListName(), getUserUpdateAvailable()
		};
		
		add( TABLE_NAME, addValues );
	}

	/**
	 * @see com.cannontech.database.db.DBPersistent#delete()
	 */
	public void delete() throws SQLException {
		Object[] constraintValues = { getListID() };
		
		delete( TABLE_NAME, CONSTRAINT_COLUMNS, constraintValues );
	}

	/**
	 * @see com.cannontech.database.db.DBPersistent#retrieve()
	 */
	public void retrieve() throws SQLException {
		Object[] constraintValues = { getListID() };
		
		Object[] results = retrieve( SETTER_COLUMNS, TABLE_NAME, CONSTRAINT_COLUMNS, constraintValues );
		
		if( results.length == SETTER_COLUMNS.length )
		{
			setOrdering( (String) results[0] );
			setSelectionLabel( (String) results[1] );
			setWhereIsList( (String) results[2] );
			setListName( (String) results[3] );
			setUserUpdateAvailable( (String) results[4] );
		}
		else
			throw new Error(getClass() + " - Incorrect Number of results retrieved");
	}

	/**
	 * @see com.cannontech.database.db.DBPersistent#update()
	 */
	public void update() throws SQLException {
		Object[] constraintValues = { getListID() };
		Object[] setValues = {
			getOrdering(), getSelectionLabel(), getWhereIsList(),
			getListName(), getUserUpdateAvailable()
		};
		
		update(TABLE_NAME, SETTER_COLUMNS, setValues, CONSTRAINT_COLUMNS, constraintValues);
	}
	
	public synchronized static Integer getNextListID(java.sql.Connection conn) {
		if( conn == null )
			throw new IllegalStateException("Database connection should not be null.");
		
		java.sql.Statement stmt = null;
		java.sql.ResultSet rset = null;
		
		try {
			stmt = conn.createStatement();
			rset = stmt.executeQuery( GET_NEXT_LIST_ID_SQL );	
				
			//get the first returned result
			rset.next();
		    return new Integer( rset.getInt(1) );
		}
		catch (java.sql.SQLException e) {
		    e.printStackTrace();
		}
		finally {
		    try {
				if ( stmt != null) stmt.close();
		    }
		    catch (java.sql.SQLException e2) {
				e2.printStackTrace();
		    }
		}
		
		//strange, should not get here
		return new Integer(CtiUtilities.NONE_ZERO_ID);
	}

	/**
	 * Returns the listID.
	 * @return Integer
	 */
	public Integer getListID() {
		return listID;
	}

	/**
	 * Returns the listName.
	 * @return String
	 */
	public String getListName() {
		return listName;
	}

	/**
	 * Returns the ordering.
	 * @return String
	 */
	public String getOrdering() {
		return ordering;
	}

	/**
	 * Returns the selectionLabel.
	 * @return String
	 */
	public String getSelectionLabel() {
		return selectionLabel;
	}

	/**
	 * Returns the userUpdateAvailable.
	 * @return String
	 */
	public String getUserUpdateAvailable() {
		return userUpdateAvailable;
	}

	/**
	 * Returns the whereIsList.
	 * @return String
	 */
	public String getWhereIsList() {
		return whereIsList;
	}

	/**
	 * Sets the listID.
	 * @param listID The listID to set
	 */
	public void setListID(Integer listID) {
		this.listID = listID;
	}

	/**
	 * Sets the listName.
	 * @param listName The listName to set
	 */
	public void setListName(String listName) {
		this.listName = listName;
	}

	/**
	 * Sets the ordering.
	 * @param ordering The ordering to set
	 */
	public void setOrdering(String ordering) {
		this.ordering = ordering;
	}

	/**
	 * Sets the selectionLabel.
	 * @param selectionLabel The selectionLabel to set
	 */
	public void setSelectionLabel(String selectionLabel) {
		this.selectionLabel = selectionLabel;
	}

	/**
	 * Sets the userUpdateAvailable.
	 * @param userUpdateAvailable The userUpdateAvailable to set
	 */
	public void setUserUpdateAvailable(String userUpdateAvailable) {
		this.userUpdateAvailable = userUpdateAvailable;
	}

	/**
	 * Sets the whereIsList.
	 * @param whereIsList The whereIsList to set
	 */
	public void setWhereIsList(String whereIsList) {
		this.whereIsList = whereIsList;
	}

}
