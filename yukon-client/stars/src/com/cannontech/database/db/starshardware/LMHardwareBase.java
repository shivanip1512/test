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

public class LMHardwareBase extends DBPersistent {

    public static final String DEVICETYPE_LCR1000 = "LCR-1000";
    public static final String DEVICETYPE_LCR2000 = "LCR-2000";
    public static final String DEVICETYPE_LCR3000 = "LCR-3000";
    public static final String DEVICETYPE_LCR4000 = "LCR-4000";
    public static final String DEVICETYPE_LCR5000 = "LCR-5000";

    private Integer inventoryID = null;
    private String manufacturerSerialNumber = null;
    private String _LMDeviceType = null;

    public static final String[] SETTER_COLUMNS = {
        "ManufacturerSerialNumber", "LMDeviceType"
    };

    public static final String[] CONSTRAINT_COLUMNS = { "InventoryID" };

    public static final String TABLE_NAME = "LMHardwareBase";

    public LMHardwareBase() {
        super();
    }

    public static boolean isLMHardwareBase(Integer inventoryID, java.sql.Connection conn) {
        String sql = "SELECT COUNT(*) FROM " + TABLE_NAME + " WHERE InventoryID = ?";

        java.sql.PreparedStatement pstmt = null;
        java.sql.ResultSet rset = null;
        boolean isLMHardware = false;

        try
        {
            if( conn == null )
            {
                throw new IllegalStateException("Database connection should not be null.");
            }
            else
            {
                pstmt = conn.prepareStatement(sql);
                pstmt.setInt( 1, inventoryID.intValue() );
                rset = pstmt.executeQuery();

                if (rset.next()) isLMHardware = true;
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

        return isLMHardware;
    }

    public void delete() throws java.sql.SQLException {
        Object[] constraintValues = { getInventoryID() };

        delete( TABLE_NAME, CONSTRAINT_COLUMNS, constraintValues );
    }

    public void add() throws java.sql.SQLException {
        Object[] addValues = {
            getInventoryID(), getManufacturerSerialNumber(), getLMDeviceType()
        };

        add( TABLE_NAME, addValues );
    }

    public void update() throws java.sql.SQLException {
        Object[] setValues = {
            getManufacturerSerialNumber(), getLMDeviceType()
        };

        Object[] constraintValues = { getInventoryID() };

        update( TABLE_NAME, SETTER_COLUMNS, setValues, CONSTRAINT_COLUMNS, constraintValues );
    }

    public void retrieve() throws java.sql.SQLException {
        Object[] constraintValues = { getInventoryID() };

        Object[] results = retrieve( SETTER_COLUMNS, TABLE_NAME, CONSTRAINT_COLUMNS, constraintValues );

        if (results.length == SETTER_COLUMNS.length) {
            setManufacturerSerialNumber( (String) results[0] );
            setLMDeviceType( (String) results[1] );
        }
        else
            throw new Error(getClass() + " - Incorrect number of results retrieved");
    }

    public Integer getInventoryID() {
        return inventoryID;
    }

    public void setInventoryID(Integer newInventoryID) {
        inventoryID = newInventoryID;
    }

    public String getManufacturerSerialNumber() {
        return manufacturerSerialNumber;
    }

    public void setManufacturerSerialNumber(String newManufacturerSerialNumber) {
        manufacturerSerialNumber = newManufacturerSerialNumber;
    }

    public String getLMDeviceType() {
        return _LMDeviceType;
    }

    public void setLMDeviceType(String newLMDeviceType) {
        _LMDeviceType = newLMDeviceType;
    }
}