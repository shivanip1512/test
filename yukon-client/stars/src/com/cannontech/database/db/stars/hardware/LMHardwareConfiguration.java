package com.cannontech.database.db.stars.hardware;

import com.cannontech.database.db.DBPersistent;


/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: </p>
 * @author unascribed
 * @version 1.0
 */

public class LMHardwareConfiguration extends DBPersistent {

    private Integer inventoryID = null;
    private Integer applianceID = null;
    private Integer addressingGroupID = new Integer(0);

    public static final String[] SETTER_COLUMNS = {
        "AddressingGroupID"
    };

    public static final String[] CONSTRAINT_COLUMNS = { "InventoryID", "ApplianceID" };

    public static final String TABLE_NAME = "LMHardwareConfiguration";

    public LMHardwareConfiguration() {
        super();
    }

    public static LMHardwareConfiguration getLMHardwareConfiguration(Integer applianceID, java.sql.Connection conn) {
        String sql = "SELECT * FROM " + TABLE_NAME + " WHERE ApplianceID = ?";

        java.sql.PreparedStatement pstmt = null;
        java.sql.ResultSet rset = null;
        LMHardwareConfiguration config = null;

        try
        {
            if( conn == null )
            {
                throw new IllegalStateException("Database connection should not be null.");
            }
            else
            {
                pstmt = conn.prepareStatement(sql);
                pstmt.setInt( 1, applianceID.intValue() );
                rset = pstmt.executeQuery();

                if (rset.next()) {
                    config = new LMHardwareConfiguration();

                    config.setInventoryID( new Integer(rset.getInt("InventoryID")) );
                    config.setApplianceID( new Integer(rset.getInt("ApplianceID")) );
                    config.setAddressingGroupID( new Integer(rset.getInt("AddressingGroupID")) );
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

        return config;
    }

    public static LMHardwareConfiguration[] getALLHardwareConfigs(Integer inventoryID, java.sql.Connection conn) {
        String sql = "SELECT * FROM " + TABLE_NAME + " WHERE InventoryID = ?";

        java.sql.PreparedStatement pstmt = null;
        java.sql.ResultSet rset = null;
        java.util.ArrayList configList = new java.util.ArrayList();

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

                while (rset.next()) {
                    LMHardwareConfiguration config = new LMHardwareConfiguration();

                    config.setInventoryID( new Integer(rset.getInt("InventoryID")) );
                    config.setApplianceID( new Integer(rset.getInt("ApplianceID")) );
                    config.setAddressingGroupID( new Integer(rset.getInt("AddressingGroupID")) );

                    configList.add(config);
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

        LMHardwareConfiguration[] configs = new LMHardwareConfiguration[ configList.size() ];
        configList.toArray( configs );
        return configs;
    }

    public static void deleteLMHardwareConfiguration(Integer applianceID, java.sql.Connection conn) {
        String sql = "DELETE FROM " + TABLE_NAME + " WHERE ApplianceID = ?";

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
                pstmt.setInt( 1, applianceID.intValue() );
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

    public static void deleteAllLMHardwareConfiguration(Integer inventoryID, java.sql.Connection conn) {
        String sql = "DELETE FROM " + TABLE_NAME + " WHERE InventoryID = ?";

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
                pstmt.setInt( 1, inventoryID.intValue() );
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
        Object[] constraintValues = { getInventoryID(), getApplianceID() };

        delete( TABLE_NAME, CONSTRAINT_COLUMNS, constraintValues );
    }

    public void add() throws java.sql.SQLException {
        Object[] addValues = {
            getInventoryID(), getApplianceID(), getAddressingGroupID()
        };

        add( TABLE_NAME, addValues );
    }

    public void update() throws java.sql.SQLException {
        Object[] setValues = {
            getAddressingGroupID()
        };

        Object[] constraintValues = { getInventoryID(), getApplianceID() };

        update( TABLE_NAME, SETTER_COLUMNS, setValues, CONSTRAINT_COLUMNS, constraintValues );
    }

    public void retrieve() throws java.sql.SQLException {
        Object[] constraintValues = { getInventoryID(), getApplianceID() };

        Object[] results = retrieve( SETTER_COLUMNS, TABLE_NAME, CONSTRAINT_COLUMNS, constraintValues );

        if (results.length == SETTER_COLUMNS.length) {
            setAddressingGroupID( (Integer) results[0] );
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

    public Integer getApplianceID() {
        return applianceID;
    }

    public void setApplianceID(Integer newApplianceID) {
        applianceID = newApplianceID;
    }

    public Integer getAddressingGroupID() {
        return addressingGroupID;
    }

    public void setAddressingGroupID(Integer newAddressingGroupID) {
        addressingGroupID = newAddressingGroupID;
    }
}