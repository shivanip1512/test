package com.cannontech.database.db.stars.hardware;

import com.cannontech.common.util.CtiUtilities;
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
    private java.util.Date receiveDate = new java.util.Date(0);
    private java.util.Date installDate = new java.util.Date(0);
    private java.util.Date removeDate = new java.util.Date(0);
    private String alternateTrackingNumber = "";
    private Integer voltageID = new Integer( CtiUtilities.NONE_ID );
    private String notes = "";
    private Integer deviceID = new Integer( CtiUtilities.NONE_ID );

    public static final String[] SETTER_COLUMNS = {
        "AccountID", "InstallationCompanyID", "CategoryID", "ReceiveDate", "InstallDate",
        "RemoveDate", "AlternateTrackingNumber", "VoltageID", "Notes", "DeviceID"
    };

    public static final String[] CONSTRAINT_COLUMNS = { "InventoryID" };

    public static final String TABLE_NAME = "InventoryBase";

    public static final String GET_NEXT_INVENTORY_ID_SQL =
        "SELECT MAX(InventoryID) FROM " + TABLE_NAME;

    public InventoryBase() {
        super();
    }

    public static InventoryBase[] getAllInventories(Integer accountID, java.sql.Connection conn) {
        String sql = "SELECT * FROM " + TABLE_NAME + " WHERE AccountID = ?";

        java.sql.PreparedStatement pstmt = null;
        java.sql.ResultSet rset = null;
        java.util.ArrayList hwList = new java.util.ArrayList();

        try
        {
            if( conn == null )
            {
                throw new IllegalStateException("Database connection should not be null.");
            }
            else
            {
                pstmt = conn.prepareStatement(sql);
                pstmt.setInt( 1, accountID.intValue() );
                rset = pstmt.executeQuery();

                while (rset.next()) {
                    InventoryBase hw = new InventoryBase();

                    hw.setDbConnection(conn);
                    hw.setInventoryID( new Integer(rset.getInt("InventoryID")) );
                    hw.setAccountID( new Integer(rset.getInt("AccountID")) );
                    hw.setInstallationCompanyID( new Integer(rset.getInt("InstallationCompanyID")) );
                    hw.setCategoryID( new Integer(rset.getInt("CategoryID")) );
                    java.util.Date date = new java.util.Date(rset.getTimestamp("ReceiveDate").getTime());
                    if (date.getTime() > 0) hw.setReceiveDate(date);
                    date = new java.util.Date(rset.getTimestamp("InstallDate").getTime());
                    if (date.getTime() > 0) hw.setInstallDate(date);
                    date = new java.util.Date(rset.getTimestamp("RemoveDate").getTime());
                    if (date.getTime() > 0) hw.setRemoveDate(date);
                    hw.setAlternateTrackingNumber( rset.getString("AlternateTrackingNumber") );
                    hw.setVoltageID( new Integer(rset.getInt("VoltageID")) );
                    hw.setNotes( rset.getString("Notes") );
                    hw.setDeviceID( new Integer(rset.getInt("DeviceID")) );

                    hwList.add(hw);
                }
            }
        }
        catch( java.sql.SQLException e )
        {
            e.printStackTrace();
        }
        finally
        {
            try
            {
                if (rset != null) rset.close();
                if( pstmt != null ) pstmt.close();
            }
            catch( java.sql.SQLException e2 )
            {
                e2.printStackTrace();
            }
        }

        InventoryBase[] hws = new InventoryBase[ hwList.size() ];
        hwList.toArray( hws );
        return hws;
    }

    public void delete() throws java.sql.SQLException {
        Object[] constraintValues = { getInventoryID() };

        delete( TABLE_NAME, CONSTRAINT_COLUMNS, constraintValues );
    }

    public void add() throws java.sql.SQLException {
    	if (getInventoryID() == null)
    		setInventoryID( getNextInventoryID() );
    		
        Object[] addValues = {
            getInventoryID(), getAccountID(), getInstallationCompanyID(),
            getCategoryID(), getReceiveDate(), getInstallDate(), getRemoveDate(),
            getAlternateTrackingNumber(), getVoltageID(), getNotes(), getDeviceID()
        };

        add( TABLE_NAME, addValues );
    }

    public void update() throws java.sql.SQLException {
        Object[] setValues = {
            getAccountID(), getInstallationCompanyID(), getCategoryID(),
            getReceiveDate(), getInstallDate(), getRemoveDate(),
            getAlternateTrackingNumber(), getVoltageID(), getNotes(), getDeviceID()
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
            setReceiveDate( new java.util.Date(((java.sql.Timestamp) results[3]).getTime()) );
            setInstallDate( new java.util.Date(((java.sql.Timestamp) results[4]).getTime()) );
            setRemoveDate( new java.util.Date(((java.sql.Timestamp) results[5]).getTime()) );
            setAlternateTrackingNumber( (String) results[6] );
            setVoltageID( (Integer) results[7] );
            setNotes( (String) results[8] );
            setDeviceID( (Integer) results[9] );
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

}