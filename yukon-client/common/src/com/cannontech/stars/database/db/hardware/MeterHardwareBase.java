package com.cannontech.stars.database.db.hardware;

import com.cannontech.common.util.CtiUtilities;
import com.cannontech.database.SqlStatement;
import com.cannontech.database.db.DBPersistent;


/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: </p>
 * @author unascribed
 * @version 1.0
 */

public class MeterHardwareBase extends DBPersistent {

    private Integer inventoryID = null;
    private String meterNumber = "";
    private Integer meterTypeID = new Integer( CtiUtilities.NONE_ZERO_ID );

    public static final String[] SETTER_COLUMNS = {
        "MeterNumber", "MeterTypeID"
    };

    public static final String[] CONSTRAINT_COLUMNS = { "InventoryID" };

    public static final String TABLE_NAME = "MeterHardwareBase";

    public MeterHardwareBase() {
        super();
    }

    public void delete() throws java.sql.SQLException {
        Object[] constraintValues = { getInventoryID() };

        delete( TABLE_NAME, CONSTRAINT_COLUMNS, constraintValues );
    }

    public void add() throws java.sql.SQLException {
        Object[] addValues = {
            getInventoryID(), getMeterNumber(), getMeterTypeID()
        };

        add( TABLE_NAME, addValues );
    }

    public void update() throws java.sql.SQLException {
        Object[] setValues = {
            getMeterNumber(), getMeterTypeID()
        };

        Object[] constraintValues = { getInventoryID() };

        update( TABLE_NAME, SETTER_COLUMNS, setValues, CONSTRAINT_COLUMNS, constraintValues );
    }

    public void retrieve() throws java.sql.SQLException {
        Object[] constraintValues = { getInventoryID() };

        Object[] results = retrieve( SETTER_COLUMNS, TABLE_NAME, CONSTRAINT_COLUMNS, constraintValues );

        if (results.length == SETTER_COLUMNS.length) {
            setMeterNumber( (String) results[0] );
            setMeterTypeID( (Integer) results[1] );
        }
        else
            throw new Error(getClass() + " - Incorrect number of results retrieved");
    }

    /*public static MeterHardwareBase[] searchByMeterNumber(String meterNo, int energyCompanyID) {
    	String sql = "SELECT hw.InventoryID, hw.ManufacturerSerialNumber, hw.LMHardwareTypeID, hw.RouteID, hw.ConfigurationID " +
    			"FROM " + TABLE_NAME + " hw, ECToInventoryMapping map " +
    			"WHERE UPPER(hw.ManufacturerSerialNumber) = UPPER(?) AND hw.InventoryID >= 0 " +
    			"AND hw.InventoryID = map.InventoryID AND map.EnergyCompanyID = ?";
    	
    	java.sql.Connection conn = null;
    	java.sql.PreparedStatement stmt = null;
    	java.sql.ResultSet rset = null;
    	
    	try {
    		conn = PoolManager.getInstance().getConnection( CtiUtilities.getDatabaseAlias() );
    		
    		stmt = conn.prepareStatement(sql);
			stmt.setString(1, serialNo);
			stmt.setInt(2, energyCompanyID);
			
			rset = stmt.executeQuery();
    		
			java.util.ArrayList hwList = new java.util.ArrayList();
			while (rset.next()) {
				MeterHardwareBase hw = new MeterHardwareBase();
				hw.setInventoryID( new Integer(rset.getInt(1)) );
				hw.setManufacturerSerialNumber( rset.getString(2) );
				hw.setLMHardwareTypeID( new Integer(rset.getInt(3)) );
				hw.setRouteID( new Integer(rset.getInt(4)) );
				hw.setConfigurationID( new Integer(rset.getInt(5)) );
				
				hwList.add( hw );
			}
    		
			MeterHardwareBase[] hardwares = new MeterHardwareBase[ hwList.size() ];
			hwList.toArray( hardwares );
			
			return hardwares;
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
    }*/

    public Integer getInventoryID() {
        return inventoryID;
    }

    public void setInventoryID(Integer newInventoryID) {
        inventoryID = newInventoryID;
    }

	public String getMeterNumber() {
		return meterNumber;
	}

	public void setMeterNumber(String meterNumber) {
		this.meterNumber = meterNumber;
	}

	public Integer getMeterTypeID() {
		return meterTypeID;
	}

	public void setMeterTypeID(Integer meterTypeID) {
		this.meterTypeID = meterTypeID;
	}
    
    public static String getMeterNumberFromInventoryID(int invenID)
    {
        String meterNumber = "";
        
        SqlStatement stmt = new SqlStatement("SELECT METERNUMBER FROM " + TABLE_NAME + " WHERE INVENTORYID =" + invenID, CtiUtilities.getDatabaseAlias());
        
        try
        {
            stmt.execute();
            
            if( stmt.getRowCount() > 0 )
            {
                for( int i = 0; i < stmt.getRowCount(); i++ )
                {
                    meterNumber = stmt.getRow(i)[0].toString();
                }
            }
        }
        catch( Exception e )
        {
            e.printStackTrace();
        }
        
        return meterNumber;
    }
}