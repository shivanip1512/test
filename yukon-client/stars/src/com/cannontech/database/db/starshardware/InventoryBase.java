package com.cannontech.database.db.starshardware;

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

    public static final int NONE_INT = 0;

    private Integer inventoryID = null;
    private Integer accountID = new Integer( com.cannontech.database.db.starscustomer.CustomerAccount.NONE_INT );
    private Integer installationCompanyID = new Integer( ServiceCompany.NONE_INT );
    private String category = null;
    private java.util.Date receiveDate = null;
    private java.util.Date installDate = null;
    private java.util.Date removeDate = null;
    private String alternateTrackingNumber = null;
    private String voltage = null;
    private String notes = null;
    private Integer PAObjectID = new Integer( NONE_INT );

    public static final String[] SETTER_COLUMNS = {
        "AccountID", "InstallationCompanyID", "Category", "ReceiveDate",
        "InstallDate", "RemoveDate", "AlternateTrackingNumber", "Voltage",
        "Notes", "PAObjectID"
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
                    hw.setCategory( rset.getString("Category") );
                    if (rset.getTimestamp("ReceiveDate") != null)
                        hw.setReceiveDate( new java.util.Date(rset.getTimestamp("ReceiveDate").getTime()) );
                    else
                        hw.setReceiveDate( new java.util.Date(0) );
                    if (rset.getTimestamp("InstallDate") != null)
                        hw.setInstallDate( new java.util.Date(rset.getTimestamp("InstallDate").getTime()) );
                    else
                        hw.setInstallDate( new java.util.Date(0) );
                    if (rset.getTimestamp("RemoveDate") != null)
                        hw.setRemoveDate( new java.util.Date(rset.getTimestamp("RemoveDate").getTime()) );
                    else
                        hw.setRemoveDate( new java.util.Date(0) );
                    hw.setAlternateTrackingNumber( rset.getString("AlternateTrackingNumber") );
                    hw.setVoltage( rset.getString("Voltage") );
                    hw.setNotes( rset.getString("Notes") );
                    hw.setPAObjectID( new Integer(rset.getInt("PAObjectID")) );

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
                if( pstmt != null ) pstmt.close();
                if (rset != null) rset.close();
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

    public static void clearInstallationCompany(Integer companyID, java.sql.Connection conn) {
        String sql = "UPDATE " + TABLE_NAME + " SET InstallationCompanyID = " +
                     ServiceCompany.NONE_INT + " WHERE InstallationCompanyID = ?";

        java.sql.PreparedStatement pstmt = null;
        try
        {
            if( conn == null )
            {
                throw new IllegalStateException("Database connection should not be null.");
            }
            else
            {
                pstmt = conn.prepareStatement(sql);
                pstmt.setInt( 1, companyID.intValue() );
                pstmt.execute();
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
                if( pstmt != null ) pstmt.close();
            }
            catch( java.sql.SQLException e2 )
            {
                e2.printStackTrace();
            }
        }
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
            getCategory(), getReceiveDate(), getInstallDate(), getRemoveDate(),
            getAlternateTrackingNumber(), getVoltage(), getNotes(), getPAObjectID()
        };

        add( TABLE_NAME, addValues );
    }

    public void update() throws java.sql.SQLException {
        Object[] setValues = {
            getAccountID(), getInstallationCompanyID(), getCategory(),
            getReceiveDate(), getInstallDate(), getRemoveDate(),
            getAlternateTrackingNumber(), getVoltage(), getNotes(), getPAObjectID()
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
            setCategory( (String) results[2] );
            if (results[3] != null)
                setReceiveDate( new java.util.Date(((java.sql.Timestamp) results[3]).getTime()) );
            else
                setReceiveDate( new java.util.Date(0) );
            if (results[4] != null)
                setInstallDate( new java.util.Date(((java.sql.Timestamp) results[4]).getTime()) );
            else
                setInstallDate( new java.util.Date(0) );
            if (results[5] != null)
                setRemoveDate( new java.util.Date(((java.sql.Timestamp) results[5]).getTime()) );
            else
                setRemoveDate( new java.util.Date(0) );
            setAlternateTrackingNumber( (String) results[6] );
            setVoltage( (String) results[7] );
            setNotes( (String) results[8] );
            setPAObjectID( (Integer) results[9] );
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

    public String getCategory() {
        return category;
    }

    public void setCategory(String newCategory) {
        category = newCategory;
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

    public String getVoltage() {
        return voltage;
    }

    public void setVoltage(String newVoltage) {
        voltage = newVoltage;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String newNotes) {
        notes = newNotes;
    }

    public Integer getPAObjectID() {
        return PAObjectID;
    }

    public void setPAObjectID(Integer newPAObjectID) {
        PAObjectID = newPAObjectID;
    }
}