package com.cannontech.database.db.stars;

import java.sql.SQLException;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.util.CommandExecutionException;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.database.SqlStatement;
import com.cannontech.database.db.DBPersistent;

/**
 * <p>Title: LMProgramWebPublishing.java</p>
 * <p>Description: </p>
 * <p>Generation Time: Aug 13, 2002 12:31:35 PM</p>
 * <p>Company: Cannon Technologies Inc.</p>
 * @author yao
 * @version 1.0
 */
public class LMProgramWebPublishing extends DBPersistent {

	private Integer programID = null;
	private Integer applianceCategoryID = new Integer(CtiUtilities.NONE_ID);
	private Integer deviceID = new Integer(CtiUtilities.NONE_ID);
	private Integer webSettingsID = new Integer(CtiUtilities.NONE_ID);
	private Integer chanceOfControlID = new Integer(CtiUtilities.NONE_ID);
	private Integer programOrder = new Integer(0);

	public static final String[] SETTER_COLUMNS = {
		"ApplianceCategoryID", "DeviceID", "WebSettingsID", "ChanceOfControlID", "ProgramOrder"
	};

	public static final String[] CONSTRAINT_COLUMNS = { "ProgramID" };

	public static final String TABLE_NAME = "LMProgramWebPublishing";
	
	public static final String GET_NEXT_PROGRAM_ID_SQL =
		"SELECT MAX(ProgramID) FROM " + TABLE_NAME;

	/**
	 * @see com.cannontech.database.db.DBPersistent#add()
	 */
	public void add() throws SQLException {
		if (getProgramID() == null)
			setProgramID( getNextProgramID() );
		
		Object[] addValues = {
			getApplianceCategoryID(), getDeviceID(), getWebSettingsID(),
			getChanceOfControlID(), getProgramOrder(), getProgramID()
		};
		
		add(TABLE_NAME, addValues);
	}

	/**
	 * @see com.cannontech.database.db.DBPersistent#delete()
	 */
	public void delete() throws SQLException {
		Object[] constraintValues = { getProgramID() };
		
		delete(TABLE_NAME, CONSTRAINT_COLUMNS, constraintValues);
	}

	/**
	 * @see com.cannontech.database.db.DBPersistent#retrieve()
	 */
	public void retrieve() throws SQLException {
		Object[] constraintValues = { getProgramID() };
		
		Object[] results = retrieve(SETTER_COLUMNS, TABLE_NAME, CONSTRAINT_COLUMNS, constraintValues);
		
		if (results.length == SETTER_COLUMNS.length) {
			setApplianceCategoryID( (Integer) results[0] );
			setDeviceID( (Integer) results[1] );
			setWebSettingsID( (Integer) results[2] );
			setChanceOfControlID( (Integer) results[3] );
			setProgramOrder( (Integer) results[4] );
		}
		else
			throw new Error(getClass() + " - Incorrect number of results retrieved");
	}

	/**
	 * @see com.cannontech.database.db.DBPersistent#update()
	 */
	public void update() throws SQLException {
		Object[] setValues = {
			getApplianceCategoryID(), getDeviceID(), getWebSettingsID(),
			getChanceOfControlID(), getProgramOrder()
		};
		Object[] constraintValues = { getProgramID() };
		
		update(TABLE_NAME, SETTER_COLUMNS, setValues, CONSTRAINT_COLUMNS, constraintValues);
	}

	public final Integer getNextProgramID() {
		java.sql.PreparedStatement pstmt = null;
		java.sql.ResultSet rset = null;

		int nextProgramID = 1;

		try {
			pstmt = getDbConnection().prepareStatement( GET_NEXT_PROGRAM_ID_SQL );
			rset = pstmt.executeQuery();

			if (rset.next())
				nextProgramID = rset.getInt(1) + 1;
		}
		catch (java.sql.SQLException e) {
			CTILogger.error( e.getMessage(), e );
		}
		finally {
			try {
				if( rset != null ) rset.close();
				if (pstmt != null) pstmt.close();
			}
			catch (java.sql.SQLException e2) {}
		}

		return new Integer( nextProgramID );
	}
	
	public static LMProgramWebPublishing getLMProgramWebPublishing(int programID) {
		String sql = "SELECT ApplianceCategoryID, DeviceID, WebSettingsID, ChanceOfControlID, ProgramOrder, ProgramID "
				   + "FROM " + TABLE_NAME + " WHERE ProgramID = " + programID;
        
		com.cannontech.database.SqlStatement stmt = new com.cannontech.database.SqlStatement(
				sql, com.cannontech.common.util.CtiUtilities.getDatabaseAlias() );

		try
		{
			stmt.execute();
        	
			if (stmt.getRowCount() > 0) {
				Object[] row = stmt.getRow(0);
				LMProgramWebPublishing webPub = new LMProgramWebPublishing();

				webPub.setApplianceCategoryID( new Integer(((java.math.BigDecimal) row[0]).intValue()) );
				webPub.setDeviceID( new Integer(((java.math.BigDecimal) row[1]).intValue()) );
				webPub.setWebSettingsID( new Integer(((java.math.BigDecimal) row[2]).intValue()) );
				webPub.setChanceOfControlID( new Integer(((java.math.BigDecimal) row[3]).intValue()) );
				webPub.setProgramOrder( new Integer(((java.math.BigDecimal) row[4]).intValue()) );
				webPub.setProgramID( new Integer(((java.math.BigDecimal) row[5]).intValue()) );

				return webPub;
			}
		}
		catch(Exception e)
		{
			CTILogger.error( e.getMessage(), e );
		}

		return null;
	}

	public static LMProgramWebPublishing[] getAllLMProgramWebPublishing(int appCatID) {
		String sql = "SELECT ApplianceCategoryID, DeviceID, WebSettingsID, ChanceOfControlID, ProgramOrder, ProgramID "
				   + "FROM " + TABLE_NAME + " WHERE ApplianceCategoryID = " + appCatID
				   + " ORDER BY ProgramOrder";
        
		com.cannontech.database.SqlStatement stmt = new com.cannontech.database.SqlStatement(
				sql, com.cannontech.common.util.CtiUtilities.getDatabaseAlias() );

		try
		{
			stmt.execute();
			LMProgramWebPublishing[] webPubs = new LMProgramWebPublishing[ stmt.getRowCount() ];
			
			for (int i = 0; i < webPubs.length; i++) {
				Object[] row = stmt.getRow(i);
				webPubs[i] = new LMProgramWebPublishing();

				webPubs[i].setApplianceCategoryID( new Integer(((java.math.BigDecimal) row[0]).intValue()) );
				webPubs[i].setDeviceID( new Integer(((java.math.BigDecimal) row[1]).intValue()) );
				webPubs[i].setWebSettingsID( new Integer(((java.math.BigDecimal) row[2]).intValue()) );
				webPubs[i].setChanceOfControlID( new Integer(((java.math.BigDecimal) row[3]).intValue()) );
				webPubs[i].setProgramOrder( new Integer(((java.math.BigDecimal) row[4]).intValue()) );
				webPubs[i].setProgramID( new Integer(((java.math.BigDecimal) row[5]).intValue()) );
			}
            
			return webPubs;
		}
		catch(Exception e)
		{
			CTILogger.error( e.getMessage(), e );
		}
        
		return null;
	}

	public static void deleteAllLMProgramWebPublishing(int appCatID) {
		String sql = "DELETE FROM " + TABLE_NAME + " WHERE ApplianceCategoryID = " + appCatID;
		SqlStatement stmt = new SqlStatement( sql, CtiUtilities.getDatabaseAlias() );
        
		try {
			stmt.execute();
		}
		catch (CommandExecutionException e) {
			CTILogger.error( e.getMessage(), e );
		}
	}

	/**
	 * Returns the applianceCategoryID.
	 * @return Integer
	 */
	public Integer getApplianceCategoryID() {
		return applianceCategoryID;
	}

	/**
	 * Returns the deviceID.
	 * @return Integer
	 */
	public Integer getDeviceID() {
		return deviceID;
	}

	/**
	 * Returns the webSettingsID.
	 * @return Integer
	 */
	public Integer getWebSettingsID() {
		return webSettingsID;
	}

	/**
	 * Sets the applianceCategoryID.
	 * @param applianceCategoryID The applianceCategoryID to set
	 */
	public void setApplianceCategoryID(Integer applianceCategoryID) {
		this.applianceCategoryID = applianceCategoryID;
	}

	/**
	 * Sets the deviceID.
	 * @param deviceID The deviceID to set
	 */
	public void setDeviceID(Integer deviceID) {
		this.deviceID = deviceID;
	}

	/**
	 * Sets the webSettingsID.
	 * @param webSettingsID The webSettingsID to set
	 */
	public void setWebSettingsID(Integer webSettingsID) {
		this.webSettingsID = webSettingsID;
	}

	/**
	 * Returns the chanceOfControlID.
	 * @return Integer
	 */
	public Integer getChanceOfControlID() {
		return chanceOfControlID;
	}

	/**
	 * Sets the chanceOfControlID.
	 * @param chanceOfControlID The chanceOfControlID to set
	 */
	public void setChanceOfControlID(Integer chanceOfControlID) {
		this.chanceOfControlID = chanceOfControlID;
	}

	/**
	 * @return
	 */
	public Integer getProgramOrder() {
		return programOrder;
	}

	/**
	 * @param integer
	 */
	public void setProgramOrder(Integer integer) {
		programOrder = integer;
	}

	/**
	 * @return
	 */
	public Integer getProgramID() {
		return programID;
	}

	/**
	 * @param integer
	 */
	public void setProgramID(Integer integer) {
		programID = integer;
	}

}
