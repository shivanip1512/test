package com.cannontech.stars.database.db.hardware;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Vector;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.database.PoolManager;
import com.cannontech.database.SqlStatement;
import com.cannontech.database.db.DBPersistent;
import com.cannontech.spring.YukonSpringHook;


/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: </p>
 * @author unascribed
 * @version 1.0
 */

public class InventoryBase extends DBPersistent {

	private Integer inventoryID = null;
	private Integer accountID = new Integer( CtiUtilities.NONE_ZERO_ID );
	private Integer installationCompanyID = new Integer( CtiUtilities.NONE_ZERO_ID );
	private Integer categoryID = new Integer( CtiUtilities.NONE_ZERO_ID );
	private Date receiveDate = new Date(0);
	private Date installDate = new Date(0);
	private Date removeDate = new Date(0);
	private String alternateTrackingNumber = "";
	private Integer voltageID = new Integer( CtiUtilities.NONE_ZERO_ID );
	private String notes = "";
	private Integer deviceID = new Integer( CtiUtilities.NONE_ZERO_ID );
	private String deviceLabel = "";
    private Integer currentStateID = new Integer( CtiUtilities.NONE_ZERO_ID );

	public static final String[] SETTER_COLUMNS = {
		"AccountID", "InstallationCompanyID", "CategoryID", "ReceiveDate", "InstallDate",
		"RemoveDate", "AlternateTrackingNumber", "VoltageID", "Notes", "DeviceID", "DeviceLabel",
        "CurrentStateID"
	};

	public static final String[] CONSTRAINT_COLUMNS = { "InventoryID" };

	public static final String TABLE_NAME = "InventoryBase";

	public InventoryBase() {
		super();
	}

	public static Vector<Integer> getInventoryIDs(Integer accountID, java.sql.Connection conn)
	throws java.sql.SQLException {
		String sql = "SELECT InventoryID FROM " + TABLE_NAME + " WHERE AccountID = ?";
		java.sql.PreparedStatement pstmt = null;
		java.sql.ResultSet rset = null;
        
		try {
			pstmt = conn.prepareStatement( sql );
			pstmt.setInt( 1, accountID.intValue() );
			rset = pstmt.executeQuery();
        	
			Vector<Integer> hwIDVct = new Vector<Integer>();
			while (rset.next())
				hwIDVct.add( new Integer(rset.getInt(1)) );
			return hwIDVct;
		}
		finally {
			if (rset != null) rset.close();
			if (pstmt != null) pstmt.close();
		}
	}

	@Override
    public void delete() throws java.sql.SQLException {
		Object[] constraintValues = { getInventoryID() };

		delete( TABLE_NAME, CONSTRAINT_COLUMNS, constraintValues );
	}

	@Override
    public void add() throws java.sql.SQLException {
		if (getInventoryID() == null)
			setInventoryID( getNextInventoryID() );
    		
		Object[] addValues = {
			getInventoryID(), getAccountID(), getInstallationCompanyID(), getCategoryID(),
			getReceiveDate(), getInstallDate(), getRemoveDate(), getAlternateTrackingNumber(),
			getVoltageID(), getNotes(), getDeviceID(), getDeviceLabel(), getCurrentStateID()
		};

		add( TABLE_NAME, addValues );
	}

	@Override
    public void update() throws java.sql.SQLException {
		Object[] setValues = {
			getAccountID(), getInstallationCompanyID(), getCategoryID(),
			getReceiveDate(), getInstallDate(), getRemoveDate(), getAlternateTrackingNumber(),
			getVoltageID(), getNotes(), getDeviceID(), getDeviceLabel(), getCurrentStateID()
		};

		Object[] constraintValues = { getInventoryID() };

		update( TABLE_NAME, SETTER_COLUMNS, setValues, CONSTRAINT_COLUMNS, constraintValues );
	}

	@Override
    public void retrieve() throws java.sql.SQLException {
		Object[] constraintValues = { getInventoryID() };

		Object[] results = retrieve( SETTER_COLUMNS, TABLE_NAME, CONSTRAINT_COLUMNS, constraintValues );

		if (results.length == SETTER_COLUMNS.length) {
			setAccountID( (Integer) results[0] );
			setInstallationCompanyID( (Integer) results[1] );
			setCategoryID( (Integer) results[2] );
			if(results[3] == null)
                setReceiveDate( new Date(0));
            else
                setReceiveDate( new Date(((java.sql.Timestamp) results[3]).getTime()) );
			if(results[4] == null)
                setInstallDate( new Date(0));
            else
                setInstallDate( new Date(((java.sql.Timestamp) results[4]).getTime()) );
			if(results[5] == null)
                setRemoveDate( new Date(0));
            else
                setRemoveDate( new Date(((java.sql.Timestamp) results[5]).getTime()) );
			setAlternateTrackingNumber( (String) results[6] );
			setVoltageID( (Integer) results[7] );
			setNotes( (String) results[8] );
			setDeviceID( (Integer) results[9] );
			setDeviceLabel( (String) results[10] );
            setCurrentStateID( (Integer) results[11]);
		}
		else
			throw new Error(getClass() + " - Incorrect number of results retrieved");
	}

    /* --As of 05/24/2006, getNextInventoryID will use the shared maxID methods from
     * com.cannontech.database.incrementer so as to avoid id collisions during
     * heavy STARS use by multiple users.
     * --Chucks a an uncaught exception; we want this to make some noise if it isn't working.
     * Also, this means we don't need to worry about checking for null, etc.
     */
    public final Integer getNextInventoryID()
    {
        return new Integer(YukonSpringHook.getNextValueHelper().getNextValue("InventoryBase"));
    }
    
	/*public final Integer getNextInventoryID() {
		java.sql.PreparedStatement pstmt = null;
		java.sql.ResultSet rset = null;

		int nextInventoryID = 1;

		try {
			pstmt = getDbConnection().prepareStatement( GET_NEXT_INVENTORY_ID_SQL );
			rset = pstmt.executeQuery();

			if (rset.next())
				nextInventoryID = rset.getInt(1) + 1;
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

		return new Integer( nextInventoryID );
	}*/
    
	public static int[] searchByAccountID(int accountID) {
		String sql = "SELECT InventoryID FROM " + TABLE_NAME + " WHERE AccountID = " + accountID;
		SqlStatement stmt = new SqlStatement( sql, CtiUtilities.getDatabaseAlias() );
    	
		try {
			stmt.execute();
			
    		int[] invIDs = new int[ stmt.getRowCount() ];
    		for (int i = 0; i < stmt.getRowCount(); i++)
    			invIDs[i] = ((java.math.BigDecimal)stmt.getRow(i)[0]).intValue();
			
			return invIDs;
		}
		catch (Exception e) {
			CTILogger.error( e.getMessage(), e );
		}
    	
		return null;
	}
	
	/**
	 * @return list of InventoryBase
	 */
	public static InventoryBase[] searchForDevice(String deviceName, int energyCompanyID) {
		String sql = "SELECT inv.InventoryID, AccountID, InstallationCompanyID, CategoryID, ReceiveDate, " +
			"InstallDate, RemoveDate, AlternateTrackingNumber, VoltageID, Notes, DeviceID, DeviceLabel, CurrentStateID " +
			"FROM " + TABLE_NAME + " inv, ECToInventoryMapping map, YukonPAObject pao " +
			"WHERE inv.DeviceID > 0 AND inv.DeviceID = pao.PAObjectID AND UPPER(pao.PAOName) LIKE UPPER(?) " +
			"AND inv.InventoryID = map.InventoryID AND map.EnergyCompanyID = ?";
		
		java.sql.Connection conn = null;
		java.sql.PreparedStatement stmt = null;
		java.sql.ResultSet rset = null;
    	
		try {
			conn = PoolManager.getInstance().getConnection( CtiUtilities.getDatabaseAlias() );
    		
			stmt = conn.prepareStatement( sql );
			stmt.setString( 1, deviceName );
			stmt.setInt( 2, energyCompanyID );
			rset = stmt.executeQuery();
			
			List<InventoryBase> resList = new ArrayList<InventoryBase>();
			while (rset.next()) {
				InventoryBase inv = new InventoryBase();
				inv.setInventoryID( new Integer(rset.getInt(1)) );
				inv.setAccountID( new Integer(rset.getInt(2)) );
				inv.setInstallationCompanyID( new Integer(rset.getInt(3)) );
				inv.setCategoryID( new Integer(rset.getInt(4)) );
				inv.setReceiveDate( rset.getTimestamp(5) );
				inv.setInstallDate( rset.getTimestamp(6) );
				inv.setRemoveDate( rset.getTimestamp(7) );
				inv.setAlternateTrackingNumber( rset.getString(8) );
				inv.setVoltageID( new Integer(rset.getInt(9)) );
				inv.setNotes( rset.getString(10) );
				inv.setDeviceID( new Integer(rset.getInt(11)) );
				inv.setDeviceLabel( rset.getString(12) );
                inv.setCurrentStateID( new Integer(rset.getInt(13)) );
				resList.add( inv );
			}
			
			InventoryBase[] invList = new InventoryBase[ resList.size() ];
			resList.toArray( invList );
			return invList;
		}
		catch (java.sql.SQLException e) {
			CTILogger.error( e.getMessage(), e );
		}
		finally {
			try {
				if (rset != null) rset.close();
				if (stmt != null) stmt.close();
				if (conn != null) conn.close();
			}
			catch (java.sql.SQLException e) {}
		}
    	
		return null;
	}
	
	/**
	 * @return (InventoryID, EnergyCompanyID)
	 */
	public static int[] searchByDeviceID(int deviceID) {
		String sql = "SELECT inv.InventoryID, map.EnergyCompanyID " +
				"FROM " + TABLE_NAME + " inv, ECToInventoryMapping map " +
				"WHERE inv.DeviceID = " + deviceID + " AND inv.InventoryID = map.InventoryID";
		SqlStatement stmt = new SqlStatement( sql, CtiUtilities.getDatabaseAlias() );
		
		try {
			stmt.execute();
			
			if (stmt.getRowCount() > 0) {
				int[] retVal = new int[2];
				retVal[0] = ((java.math.BigDecimal)stmt.getRow(0)[0]).intValue();
				retVal[1] = ((java.math.BigDecimal)stmt.getRow(0)[1]).intValue();
				
				return retVal; 
			}
		}
		catch (Exception e) {
			CTILogger.error( e.getMessage(), e );
		}
		
		return null;
	}
    
	public static int[] searchByAltTrackingNo(String altTrackNo, int energyCompanyID) {
		String sql = "SELECT inv.InventoryID FROM " + TABLE_NAME + " inv, ECToInventoryMapping map " +
				"WHERE UPPER(inv.AlternateTrackingNumber) = UPPER(?) AND inv.InventoryID >= 0 " +
				"AND inv.InventoryID = map.InventoryID AND map.EnergyCompanyID = ?";
    	
		java.sql.Connection conn = null;
		java.sql.PreparedStatement stmt = null;
		java.sql.ResultSet rset = null;
    	
		try {
			conn = PoolManager.getInstance().getConnection( CtiUtilities.getDatabaseAlias() );
    		
			stmt = conn.prepareStatement(sql);
			stmt.setString(1, altTrackNo);
			stmt.setInt(2, energyCompanyID);
			
			rset = stmt.executeQuery();
    		
			List<Integer> rstList = new ArrayList<Integer>();
			while (rset.next())
				rstList.add(rset.getInt(1));
    		
			int[] invIDs = new int[ rstList.size() ];
			for (int i = 0; i < rstList.size(); i++)
				invIDs[i] = rstList.get(i).intValue();
			
			return invIDs;
		}
		catch (java.sql.SQLException e) {
			CTILogger.error( e.getMessage(), e );
		}
		finally {
			try {
				if (rset != null) rset.close();
				if (stmt != null) stmt.close();
				if (conn != null) conn.close();
			}
			catch (java.sql.SQLException e) {}
		}
    	
		return null;
	}
	
	public static void resetInstallationCompany(int companyID) {
		String sql = "UPDATE " + TABLE_NAME + " SET InstallationCompanyID = 0 WHERE InstallationCompanyID = " + companyID;
		SqlStatement stmt = new SqlStatement( sql, CtiUtilities.getDatabaseAlias() );
		
		try {
			stmt.execute();
		}
		catch (Exception e) {
			CTILogger.error( e.getMessage(), e );
		}
	}

	public Integer getInventoryID() {
		return inventoryID;
	}

	public void setInventoryID(Integer newInventoryID) {
		inventoryID = newInventoryID;
	}

	public Integer getAccountID() {
		return accountID;
	}

	public void setAccountID(Integer newAccountID) {
		accountID = newAccountID;
	}

	public Integer getInstallationCompanyID() {
		return installationCompanyID;
	}

	public void setInstallationCompanyID(Integer newInstallationCompanyID) {
		installationCompanyID = newInstallationCompanyID;
	}

	public java.util.Date getReceiveDate() {
		return receiveDate;
	}

	public void setReceiveDate(java.util.Date newReceiveDate) {
		receiveDate = newReceiveDate;
	}

	public java.util.Date getInstallDate() {
		return installDate;
	}

	public void setInstallDate(java.util.Date newInstallDate) {
		installDate = newInstallDate;
	}

	public java.util.Date getRemoveDate() {
		return removeDate;
	}

	public void setRemoveDate(java.util.Date newRemoveDate) {
		removeDate = newRemoveDate;
	}

	public String getAlternateTrackingNumber() {
		return alternateTrackingNumber;
	}

	public void setAlternateTrackingNumber(String newAlternateTrackingNumber) {
		alternateTrackingNumber = newAlternateTrackingNumber;
	}

	public String getNotes() {
		return notes;
	}

	public void setNotes(String newNotes) {
		notes = newNotes;
	}
	/**
	 * Returns the categoryID.
	 * @return Integer
	 */
	public Integer getCategoryID() {
		return categoryID;
	}

	/**
	 * Returns the deviceID.
	 * @return Integer
	 */
	public Integer getDeviceID() {
		return deviceID;
	}

	/**
	 * Returns the voltageID.
	 * @return Integer
	 */
	public Integer getVoltageID() {
		return voltageID;
	}

	/**
	 * Sets the categoryID.
	 * @param categoryID The categoryID to set
	 */
	public void setCategoryID(Integer categoryID) {
		this.categoryID = categoryID;
	}

	/**
	 * Sets the deviceID.
	 * @param deviceID The deviceID to set
	 */
	public void setDeviceID(Integer deviceID) {
		this.deviceID = deviceID;
	}

	/**
	 * Sets the voltageID.
	 * @param voltageID The voltageID to set
	 */
	public void setVoltageID(Integer voltageID) {
		this.voltageID = voltageID;
	}

	/**
	 * Returns the deviceLabel.
	 * @return String
	 */
	public String getDeviceLabel() {
		if(deviceLabel == null)
		    deviceLabel = "";
        return deviceLabel;
	}

	/**
	 * Sets the deviceLabel.
	 * @param deviceLabel The deviceLabel to set
	 */
	public void setDeviceLabel(String deviceLabel) {
		this.deviceLabel = deviceLabel;
	}

    public Integer getCurrentStateID() {
        return currentStateID;
    }

    public void setCurrentStateID(Integer currentStateID) {
        this.currentStateID = currentStateID;
    }

}