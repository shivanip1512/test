package com.cannontech.database.db.stars.hardware;

import java.util.Date;

import com.cannontech.common.util.CtiUtilities;
import com.cannontech.database.PoolManager;
import com.cannontech.database.db.DBPersistent;


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
    private Integer accountID = new Integer( CtiUtilities.NONE_ID );
    private Integer installationCompanyID = new Integer( CtiUtilities.NONE_ID );
    private Integer categoryID = new Integer( CtiUtilities.NONE_ID );
    private Date receiveDate = new Date(0);
    private Date installDate = new Date(0);
    private Date removeDate = new Date(0);
    private String alternateTrackingNumber = "";
    private Integer voltageID = new Integer( CtiUtilities.NONE_ID );
    private String notes = "";
    private Integer deviceID = new Integer( CtiUtilities.NONE_ID );
    private String deviceLabel = "";

    public static final String[] SETTER_COLUMNS = {
        "AccountID", "InstallationCompanyID", "CategoryID", "ReceiveDate", "InstallDate",
        "RemoveDate", "AlternateTrackingNumber", "VoltageID", "Notes", "DeviceID", "DeviceLabel"
    };

    public static final String[] CONSTRAINT_COLUMNS = { "InventoryID" };

    public static final String TABLE_NAME = "InventoryBase";

    public static final String GET_NEXT_INVENTORY_ID_SQL =
        "SELECT MAX(InventoryID) FROM " + TABLE_NAME;

    public InventoryBase() {
        super();
    }

    public static java.util.Vector getInventoryIDs(Integer accountID, java.sql.Connection conn)
    throws java.sql.SQLException {
        String sql = "SELECT InventoryID FROM " + TABLE_NAME + " WHERE AccountID = ?";

        java.sql.PreparedStatement pstmt = conn.prepareStatement( sql );
        pstmt.setInt( 1, accountID.intValue() );
        java.sql.ResultSet rset = pstmt.executeQuery();
        
        java.util.Vector hwIDVct = new java.util.Vector();
        while (rset.next())
        	hwIDVct.add( new Integer(rset.getInt(1)) );
        return hwIDVct;
    }

    public void delete() throws java.sql.SQLException {
        Object[] constraintValues = { getInventoryID() };

        delete( TABLE_NAME, CONSTRAINT_COLUMNS, constraintValues );
    }

    public void add() throws java.sql.SQLException {
    	if (getInventoryID() == null)
    		setInventoryID( getNextInventoryID() );
    		
        Object[] addValues = {
            getInventoryID(), getAccountID(), getInstallationCompanyID(), getCategoryID(),
            getReceiveDate(), getInstallDate(), getRemoveDate(), getAlternateTrackingNumber(),
            getVoltageID(), getNotes(), getDeviceID(), getDeviceLabel()
        };

        add( TABLE_NAME, addValues );
    }

    public void update() throws java.sql.SQLException {
        Object[] setValues = {
            getAccountID(), getInstallationCompanyID(), getCategoryID(),
            getReceiveDate(), getInstallDate(), getRemoveDate(), getAlternateTrackingNumber(),
            getVoltageID(), getNotes(), getDeviceID(), getDeviceLabel()
        };

        Object[] constraintValues = { getInventoryID() };

        update( TABLE_NAME, SETTER_COLUMNS, setValues, CONSTRAINT_COLUMNS, constraintValues );
    }

    public void retrieve() throws java.sql.SQLException {
        Object[] constraintValues = { getInventoryID() };

        Object[] results = retrieve( SETTER_COLUMNS, TABLE_NAME, CONSTRAINT_COLUMNS, constraintValues );

        if (results.length == SETTER_COLUMNS.length) {
            setAccountID( (Integer) results[0] );
            setInstallationCompanyID( (Integer) results[1] );
            setCategoryID( (Integer) results[2] );
            setReceiveDate( new Date(((java.sql.Timestamp) results[3]).getTime()) );
            setInstallDate( new Date(((java.sql.Timestamp) results[4]).getTime()) );
            setRemoveDate( new Date(((java.sql.Timestamp) results[5]).getTime()) );
            setAlternateTrackingNumber( (String) results[6] );
            setVoltageID( (Integer) results[7] );
            setNotes( (String) results[8] );
            setDeviceID( (Integer) results[9] );
            setDeviceLabel( (String) results[10] );
        }
        else
            throw new Error(getClass() + " - Incorrect number of results retrieved");
    }

    public final Integer getNextInventoryID() {
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
            e.printStackTrace();
        }
        finally {
            try {
                if (rset != null) rset.close();
                if (pstmt != null) pstmt.close();
            }
            catch (java.sql.SQLException e2) {
                e2.printStackTrace();
            }
        }

        return new Integer( nextInventoryID );
    }
    
	public static int[] searchByAccountID(int accountID) {
		String sql = "SELECT InventoryID FROM " + TABLE_NAME + " WHERE AccountID = " + accountID;
		
		java.sql.Connection conn = null;
		java.sql.Statement stmt = null;
		java.sql.ResultSet rset = null;
    	
    	try {
    		conn = PoolManager.getInstance().getConnection( CtiUtilities.getDatabaseAlias() );
    		stmt = conn.createStatement();
    		rset = stmt.executeQuery( sql );
    		
			java.util.ArrayList invIDList = new java.util.ArrayList();
			while (rset.next())
				invIDList.add( new Integer(rset.getInt(1)) );
    		
			int[] invIDs = new int[ invIDList.size() ];
			for (int i = 0; i < invIDList.size(); i++)
				invIDs[i] = ((Integer) invIDList.get(i)).intValue();
			
			return invIDs;
    	}
    	catch (java.sql.SQLException e) {
    		e.printStackTrace();
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
	
	public static int[] searchForDevice(int categoryID, String deviceName, int energyCompanyID) {
		String sql = "SELECT inv.InventoryID " +
				"FROM " + TABLE_NAME + " inv, ECToInventoryMapping map, YukonPAObject pao " +
				"WHERE inv.CategoryID = ? AND inv.DeviceID > 0 " +
				"AND inv.DeviceID = pao.PAObjectID AND UPPER(pao.PAOName) LIKE UPPER(?) " +
				"AND inv.InventoryID = map.InventoryID AND map.EnergyCompanyID = ?";
		
		java.sql.Connection conn = null;
		java.sql.PreparedStatement stmt = null;
		java.sql.ResultSet rset = null;
    	
    	try {
    		conn = PoolManager.getInstance().getConnection( CtiUtilities.getDatabaseAlias() );
    		
    		stmt = conn.prepareStatement( sql );
			stmt.setInt( 1, categoryID );
			stmt.setString( 2, deviceName );
			stmt.setInt( 3, energyCompanyID );
			
			rset = stmt.executeQuery();
			
			java.util.ArrayList invIDList = new java.util.ArrayList();
			while (rset.next())
				invIDList.add( new Integer(rset.getInt(1)) );
    		
			int[] invIDs = new int[ invIDList.size() ];
			for (int i = 0; i < invIDList.size(); i++)
				invIDs[i] = ((Integer) invIDList.get(i)).intValue();
			
			return invIDs;
    	}
    	catch (java.sql.SQLException e) {
    		e.printStackTrace();
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
	
	public static InventoryBase searchByDeviceID(int deviceID, int energyCompanyID) {
		String sql = "SELECT inv.InventoryID, AccountID, InstallationCompanyID, CategoryID, " +
				"ReceiveDate, InstallDate, RemoveDate, AlternateTrackingNumber, " +
				"VoltageID, Notes, DeviceID, DeviceLabel " +
				"FROM " + TABLE_NAME + " inv, ECToInventoryMapping map " +
				"WHERE DeviceID = ? AND inv.InventoryID = map.InventoryID " +
				"AND map.EnergyCompanyID = ?";
		
		java.sql.Connection conn = null;
		java.sql.PreparedStatement stmt = null;
		java.sql.ResultSet rset = null;
		
		try {
			conn = PoolManager.getInstance().getConnection( CtiUtilities.getDatabaseAlias() );
			
			stmt = conn.prepareStatement( sql );
			stmt.setInt( 1, deviceID );
			stmt.setInt( 2, energyCompanyID );
			
			rset = stmt.executeQuery();
			
			if (rset.next()) {
				InventoryBase inv = new InventoryBase();
				inv.setInventoryID( new Integer(rset.getInt(1)) );
				inv.setAccountID( new Integer(rset.getInt(2)) );
				inv.setInstallationCompanyID( new Integer(rset.getInt(3)) );
				inv.setCategoryID( new Integer(rset.getInt(4)) );
				inv.setReceiveDate( new Date(rset.getTimestamp(5).getTime()) );
				inv.setInstallDate( new Date(rset.getTimestamp(6).getTime()) );
				inv.setRemoveDate( new Date(rset.getTimestamp(7).getTime()) );
				inv.setAlternateTrackingNumber( rset.getString(8) );
				inv.setVoltageID( new Integer(rset.getInt(9)) );
				inv.setNotes( rset.getString(10) );
				inv.setDeviceID( new Integer(rset.getInt(11)) );
				inv.setDeviceLabel( rset.getString(12) );
				
				return inv;
			}
		}
		catch (java.sql.SQLException e) {
			e.printStackTrace();
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
		return deviceLabel;
	}

	/**
	 * Sets the deviceLabel.
	 * @param deviceLabel The deviceLabel to set
	 */
	public void setDeviceLabel(String deviceLabel) {
		this.deviceLabel = deviceLabel;
	}

}