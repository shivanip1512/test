package com.cannontech.database.db.stars;

import java.sql.SQLException;

import com.cannontech.common.util.CtiUtilities;
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

	private Integer applianceCategoryID = null;
	private Integer lmProgramID = null;
	private Integer webSettingsID = new Integer(CtiUtilities.NONE_ID);
	private Integer chanceOfControlID = new Integer(CtiUtilities.NONE_ID);
	private Integer programOrder = new Integer(0);

	public static final String[] SETTER_COLUMNS = {
		"WebSettingsID", "ChanceOfControlID", "ProgramOrder"
	};

	public static final String[] CONSTRAINT_COLUMNS = {
		"ApplianceCategoryID", "LMProgramID"
	};

	public static final String TABLE_NAME = "LMProgramWebPublishing";

	/**
	 * @see com.cannontech.database.db.DBPersistent#add()
	 */
	public void add() throws SQLException {
		Object[] addValues = {
			getApplianceCategoryID(), getLMProgramID(), getWebSettingsID(),
			getChanceOfControlID(), getProgramOrder()
		};
		
		add(TABLE_NAME, addValues);
	}

	/**
	 * @see com.cannontech.database.db.DBPersistent#delete()
	 */
	public void delete() throws SQLException {
		Object[] constraintValues = {
			getApplianceCategoryID(), getLMProgramID()
		};
		
		delete(TABLE_NAME, CONSTRAINT_COLUMNS, constraintValues);
	}

	/**
	 * @see com.cannontech.database.db.DBPersistent#retrieve()
	 */
	public void retrieve() throws SQLException {
		Object[] constraintValues = {
			getApplianceCategoryID(), getLMProgramID()
		};
		
		Object[] results = retrieve(SETTER_COLUMNS, TABLE_NAME, CONSTRAINT_COLUMNS, constraintValues);
		
		if (results.length == SETTER_COLUMNS.length) {
			setWebSettingsID( (Integer) results[0] );
			setChanceOfControlID( (Integer) results[1] );
			setProgramOrder( (Integer) results[2] );
		}
		else
			throw new Error(getClass() + " - Incorrect number of results retrieved");
	}

	/**
	 * @see com.cannontech.database.db.DBPersistent#update()
	 */
	public void update() throws SQLException {
		Object[] setValues = {
			getWebSettingsID(), getChanceOfControlID(), getProgramOrder()
		};
		Object[] constraintValues = {
			getApplianceCategoryID(), getLMProgramID()
		};
		
		update(TABLE_NAME, SETTER_COLUMNS, setValues, CONSTRAINT_COLUMNS, constraintValues);
	}
	
	public static LMProgramWebPublishing getLMProgramWebPublishing(Integer programID) {
		String sql = "SELECT ApplianceCategoryID, LMProgramID, WebSettingsID, ChanceOfControlID, ProgramOrder "
				   + "FROM " + TABLE_NAME + " WHERE LMProgramID = " + programID.toString();
        
		com.cannontech.database.SqlStatement stmt = new com.cannontech.database.SqlStatement(
				sql, com.cannontech.common.util.CtiUtilities.getDatabaseAlias() );

		try
		{
			stmt.execute();
        	
			if (stmt.getRowCount() > 0) {
				Object[] row = stmt.getRow(0);
				LMProgramWebPublishing webPub = new LMProgramWebPublishing();

				webPub.setApplianceCategoryID( new Integer(((java.math.BigDecimal) row[0]).intValue()) );
				webPub.setLMProgramID( new Integer(((java.math.BigDecimal) row[1]).intValue()) );
				webPub.setWebSettingsID( new Integer(((java.math.BigDecimal) row[2]).intValue()) );
				webPub.setChanceOfControlID( new Integer(((java.math.BigDecimal) row[3]).intValue()) );
				webPub.setProgramOrder( new Integer(((java.math.BigDecimal) row[4]).intValue()) );

				return webPub;
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

		return null;
	}

	public static LMProgramWebPublishing[] getAllLMProgramWebPublishing(Integer appCatID) {
		String sql = "SELECT ApplianceCategoryID, LMProgramID, WebSettingsID, ChanceOfControlID, ProgramOrder "
				   + "FROM " + TABLE_NAME + " WHERE ApplianceCategoryID = " + appCatID.toString()
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
				webPubs[i].setLMProgramID( new Integer(((java.math.BigDecimal) row[1]).intValue()) );
				webPubs[i].setWebSettingsID( new Integer(((java.math.BigDecimal) row[2]).intValue()) );
				webPubs[i].setChanceOfControlID( new Integer(((java.math.BigDecimal) row[3]).intValue()) );
				webPubs[i].setProgramOrder( new Integer(((java.math.BigDecimal) row[4]).intValue()) );
			}
            
			return webPubs;
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
        
		return null;
	}

	public static void deleteAllLMProgramWebPublishing(Integer appCatID) {
		java.sql.Connection conn = null;
		java.sql.PreparedStatement stmt = null;
		String sql = "DELETE FROM " + TABLE_NAME + " WHERE ApplianceCategoryID = ?";
        
		try {
			conn = com.cannontech.database.PoolManager.getInstance().getConnection( CtiUtilities.getDatabaseAlias() );
			stmt = conn.prepareStatement( sql );
			stmt.setInt( 1, appCatID.intValue() );
			stmt.execute();
		}
		catch (java.sql.SQLException e) {
			e.printStackTrace();
		}
		finally {
			try {
				if (stmt != null) stmt.close();
				if (conn != null) conn.close();
			}
			catch (java.sql.SQLException e) {}
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
	 * Returns the lmProgramID.
	 * @return Integer
	 */
	public Integer getLMProgramID() {
		return lmProgramID;
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
	 * Sets the lmProgramID.
	 * @param lmProgramID The lmProgramID to set
	 */
	public void setLMProgramID(Integer lmProgramID) {
		this.lmProgramID = lmProgramID;
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

}
