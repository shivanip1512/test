/*
 * Created on Nov 20, 2003
 */
package com.cannontech.cttp.db;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import com.cannontech.database.SqlUtils;
import com.cannontech.database.db.DBPersistent;

/**
 * @author aaron
 **/
public class CttpCmdGroup extends DBPersistent {

	public static final String TABLE_NAME = "CTTPCMDGROUP";

	public static final String[] SETTER_COLUMNS  = 
	{ 
		"TrackingID", "LMGroupID"
	};

	public static final String[] CONSTRAINT_COLUMNS  = { "CmdGroupID" };
	
	private Integer cmdGroupID;
	private Integer trackingID;
	private Integer lmGroupID;
			
	/* (non-Javadoc)
	 * @see com.cannontech.database.db.DBPersistent#add()
	 */
	public void add() throws SQLException {
		if(getCmdGroupID() == null) {
			setCmdGroupID(new Integer(getNextID(getDbConnection())));
		}
		Object[] values = {
			getCmdGroupID(), getTrackingID(), getLmGroupID()		
		};
		add(TABLE_NAME, values);
	}

	/* (non-Javadoc)
	 * @see com.cannontech.database.db.DBPersistent#delete()
	 */
	public void delete() throws SQLException {
		delete(TABLE_NAME, CONSTRAINT_COLUMNS[0], getCmdGroupID());
	}

	/* (non-Javadoc)
	 * @see com.cannontech.database.db.DBPersistent#retrieve()
	 */
	public void retrieve() throws SQLException {
		Object[] constraintValues = { getCmdGroupID() };		
		Object[] results = retrieve(SETTER_COLUMNS, TABLE_NAME, CONSTRAINT_COLUMNS, constraintValues);
		if(results.length == SETTER_COLUMNS.length) {
			setTrackingID((Integer) results[0]);
			setLmGroupID((Integer)results[1]);
		}
	}

	/* (non-Javadoc)
	 * @see com.cannontech.database.db.DBPersistent#update()
	 */
	public void update() throws SQLException {
		Object[] setValues = {
			getTrackingID(), getLmGroupID()
		};
		Object[] constraintValues = { getCmdGroupID() };
		update(TABLE_NAME, SETTER_COLUMNS, setValues, CONSTRAINT_COLUMNS, constraintValues);
	}

	public static final synchronized int getNextID(Connection conn) {
		int retVal = 0;
		Statement stmt = null;
		ResultSet rset = null;

		try	{
			if(conn == null ) {
						throw new IllegalStateException("Database connection cannot be (null).");
			}
			else {
				stmt = conn.createStatement();
				rset = stmt.executeQuery("select max(CmdGroupID) from CTTPCMDGROUP");

				// Just one please
				if( rset.next() )
					retVal = rset.getInt(1) + 1;
			}
		}
		catch( java.sql.SQLException e ) {
				com.cannontech.clientutils.CTILogger.error( e.getMessage(), e );
		}
		finally {
			SqlUtils.close(rset, stmt);
		}

		return retVal;
	}
	/**
	 * @return
	 */
	public Integer getLmGroupID() {
		return lmGroupID;
	}

	/**
	 * @return
	 */
	public Integer getTrackingID() {
		return trackingID;
	}

	/**
	 * @param integer
	 */
	public void setLmGroupID(Integer integer) {
		lmGroupID = integer;
	}

	/**
	 * @param integer
	 */
	public void setTrackingID(Integer integer) {
		trackingID = integer;
	}

	/**
	 * @return
	 */
	public Integer getCmdGroupID() {
		return cmdGroupID;
	}

	/**
	 * @param integer
	 */
	public void setCmdGroupID(Integer integer) {
		cmdGroupID = integer;
	}

}
