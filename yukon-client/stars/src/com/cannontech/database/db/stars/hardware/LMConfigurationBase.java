/*
 * Created on Jun 23, 2004
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package com.cannontech.database.db.stars.hardware;

import java.sql.SQLException;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.database.db.DBPersistent;

/**
 * @author yao
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class LMConfigurationBase extends DBPersistent {

	private Integer configurationID = null;
	private String coldLoadPickup = CtiUtilities.STRING_NONE;
	private String tamperDetect = CtiUtilities.STRING_NONE;
	
	public static final String[] SETTER_COLUMNS = {
		"ColdLoadPickup", "TamperDetect"
	};
	
	public static final String[] CONSTRAINT_COLUMNS = { "ConfigurationID" };
	
	public static final String TABLE_NAME = "LMConfigurationBase";
	
	public static final String GET_NEXT_CONFIG_ID_SQL =
			"SELECT MAX(ConfigurationID) FROM " + TABLE_NAME;
	
	public LMConfigurationBase() {
		super();
	}
	
	public final Integer getNextConfigurationID() {
		java.sql.PreparedStatement pstmt = null;
		java.sql.ResultSet rset = null;

		int nextConfigID = 1;

		try {
			pstmt = getDbConnection().prepareStatement( GET_NEXT_CONFIG_ID_SQL );
			rset = pstmt.executeQuery();

			if (rset.next())
			nextConfigID = rset.getInt(1) + 1;
		}
		catch (java.sql.SQLException e) {
			CTILogger.error( e.getMessage(), e );
		}
		finally {
			try {
				if (rset != null) rset.close();
				if (pstmt != null) pstmt.close();
			}
			catch (java.sql.SQLException e2) {}
		}

		return new Integer( nextConfigID );
	}
	
	/* (non-Javadoc)
	 * @see com.cannontech.database.db.DBPersistent#add()
	 */
	public void add() throws SQLException {
		if (getConfigurationID() == null)
			setConfigurationID( getNextConfigurationID() );
		
		Object[] addValues = {
			getConfigurationID(), getColdLoadPickup(), getTamperDetect()
		};
		add( TABLE_NAME, addValues );
	}

	/* (non-Javadoc)
	 * @see com.cannontech.database.db.DBPersistent#delete()
	 */
	public void delete() throws SQLException {
		Object[] constraintValues = { getConfigurationID() };
		delete( TABLE_NAME, CONSTRAINT_COLUMNS, constraintValues );
	}

	/* (non-Javadoc)
	 * @see com.cannontech.database.db.DBPersistent#retrieve()
	 */
	public void retrieve() throws SQLException {
		Object[] constraintValues = { getConfigurationID() };
		Object[] results = retrieve( SETTER_COLUMNS, TABLE_NAME, CONSTRAINT_COLUMNS, constraintValues );
		
		if (results.length == SETTER_COLUMNS.length) {
			setConfigurationID( (Integer) results[0] );
			setColdLoadPickup( (String) results[1] );
			setTamperDetect( (String) results[2] );
		}
		else
			throw new Error(getClass() + " - Incorrect number of results retrieved");
	}

	/* (non-Javadoc)
	 * @see com.cannontech.database.db.DBPersistent#update()
	 */
	public void update() throws SQLException {
		Object[] setValues = {
			getColdLoadPickup(), getTamperDetect()
		};
		Object[] constraintValues = { getConfigurationID() };
		
		update( TABLE_NAME, SETTER_COLUMNS, setValues, CONSTRAINT_COLUMNS, constraintValues );
	}

	/**
	 * @return
	 */
	public String getColdLoadPickup() {
		return coldLoadPickup;
	}

	/**
	 * @return
	 */
	public Integer getConfigurationID() {
		return configurationID;
	}

	/**
	 * @return
	 */
	public String getTamperDetect() {
		return tamperDetect;
	}

	/**
	 * @param string
	 */
	public void setColdLoadPickup(String string) {
		coldLoadPickup = string;
	}

	/**
	 * @param integer
	 */
	public void setConfigurationID(Integer integer) {
		configurationID = integer;
	}

	/**
	 * @param string
	 */
	public void setTamperDetect(String string) {
		tamperDetect = string;
	}

}
