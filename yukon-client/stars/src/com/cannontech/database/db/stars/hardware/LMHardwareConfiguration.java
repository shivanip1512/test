package com.cannontech.database.db.stars.hardware;

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

public class LMHardwareConfiguration extends DBPersistent {

    private Integer inventoryID = null;
    private Integer applianceID = null;
    private Integer addressingGroupID = new Integer( CtiUtilities.NONE_ZERO_ID );
    private Integer loadNumber = new Integer(0);

    public static final String[] SETTER_COLUMNS = {
        "AddressingGroupID", "LoadNumber"
    };

    public static final String[] CONSTRAINT_COLUMNS = {
    	"InventoryID", "ApplianceID"
    };

    public static final String TABLE_NAME = "LMHardwareConfiguration";

    public LMHardwareConfiguration() {
        super();
    }

    public static LMHardwareConfiguration getLMHardwareConfiguration(Integer applianceID) {
        String sql = "SELECT InventoryID, ApplianceID, AddressingGroupID, LoadNumber FROM " +
        		TABLE_NAME + " WHERE ApplianceID=" + applianceID;
        SqlStatement stmt = new SqlStatement( sql, CtiUtilities.getDatabaseAlias() );
        
        try {
        	stmt.execute();
        	
        	if (stmt.getRowCount() > 0) {
                LMHardwareConfiguration config = new LMHardwareConfiguration();
                
                config.setInventoryID( new Integer(((java.math.BigDecimal) stmt.getRow(0)[0]).intValue()) );
                config.setApplianceID( new Integer(((java.math.BigDecimal) stmt.getRow(0)[1]).intValue()) );
                config.setAddressingGroupID( new Integer(((java.math.BigDecimal) stmt.getRow(0)[2]).intValue()) );
                config.setLoadNumber( new Integer(((java.math.BigDecimal) stmt.getRow(0)[3]).intValue()) );
                
                return config;
            }
        }
        catch( Exception e ) {
            com.cannontech.clientutils.CTILogger.error( e.getMessage(), e );
        }
        
        return null;
    }

    public static LMHardwareConfiguration[] getAllLMHardwareConfiguration(Integer inventoryID) {
        String sql = "SELECT * FROM " + TABLE_NAME + " WHERE InventoryID = " + inventoryID;
        SqlStatement stmt = new SqlStatement( sql, CtiUtilities.getDatabaseAlias() );
        
        try {
        	stmt.execute();
        	
        	LMHardwareConfiguration[] configs = new LMHardwareConfiguration[ stmt.getRowCount() ];
            for (int i = 0; i < configs.length; i++) {
            	Object[] row = stmt.getRow(i);
                configs[i] = new LMHardwareConfiguration();
                
                configs[i].setInventoryID( new Integer(((java.math.BigDecimal) row[0]).intValue()) );
                configs[i].setApplianceID( new Integer(((java.math.BigDecimal) row[1]).intValue()) );
                configs[i].setAddressingGroupID( new Integer(((java.math.BigDecimal) row[2]).intValue()) );
				configs[i].setLoadNumber( new Integer(((java.math.BigDecimal) row[3]).intValue()) );
            }
            
            return configs;
        }
        catch (Exception e) {
            com.cannontech.clientutils.CTILogger.error( e.getMessage(), e );
        }
        
        return null;
    }
    
	public static LMHardwareConfiguration[] getAllLMHardwareConfiguration(int energyCompanyID) {
		String sql = "SELECT cfg.* FROM " + TABLE_NAME + " cfg, ECToInventoryMapping map " +
				"WHERE map.EnergyCompanyID = " + energyCompanyID + " AND map.InventoryID = cfg.InventoryID";
		SqlStatement stmt = new SqlStatement( sql, CtiUtilities.getDatabaseAlias() );
        
		try {
			stmt.execute();
        	
			LMHardwareConfiguration[] configs = new LMHardwareConfiguration[ stmt.getRowCount() ];
			for (int i = 0; i < configs.length; i++) {
				Object[] row = stmt.getRow(i);
				configs[i] = new LMHardwareConfiguration();
                
				configs[i].setInventoryID( new Integer(((java.math.BigDecimal) row[0]).intValue()) );
				configs[i].setApplianceID( new Integer(((java.math.BigDecimal) row[1]).intValue()) );
				configs[i].setAddressingGroupID( new Integer(((java.math.BigDecimal) row[2]).intValue()) );
				configs[i].setLoadNumber( new Integer(((java.math.BigDecimal) row[3]).intValue()) );
			}
            
			return configs;
		}
		catch (Exception e) {
			com.cannontech.clientutils.CTILogger.error( e.getMessage(), e );
		}
        
		return null;
	}

    public static void deleteLMHardwareConfiguration(Integer applianceID) {
        String sql = "DELETE FROM " + TABLE_NAME + " WHERE ApplianceID=" + applianceID;
        SqlStatement stmt = new SqlStatement( sql, CtiUtilities.getDatabaseAlias() );
        
        try {
        	stmt.execute();
        }
        catch( Exception e ) {
            com.cannontech.clientutils.CTILogger.error( e.getMessage(), e );
        }
    }

    public static void deleteAllLMHardwareConfiguration(int inventoryID) {
        String sql = "DELETE FROM " + TABLE_NAME + " WHERE InventoryID=" + inventoryID;
        SqlStatement stmt = new SqlStatement( sql, CtiUtilities.getDatabaseAlias() );
        
        try {
        	stmt.execute();
        }
        catch( Exception e ) {
            com.cannontech.clientutils.CTILogger.error( e.getMessage(), e );
        }
    }

    public void delete() throws java.sql.SQLException {
        Object[] constraintValues = { getInventoryID(), getApplianceID() };

        delete( TABLE_NAME, CONSTRAINT_COLUMNS, constraintValues );
    }

    public void add() throws java.sql.SQLException {
        Object[] addValues = {
            getInventoryID(), getApplianceID(), getAddressingGroupID(), getLoadNumber()
        };

        add( TABLE_NAME, addValues );
    }

    public void update() throws java.sql.SQLException {
        Object[] setValues = { getAddressingGroupID(), getLoadNumber() };

        Object[] constraintValues = { getInventoryID(), getApplianceID() };

        update( TABLE_NAME, SETTER_COLUMNS, setValues, CONSTRAINT_COLUMNS, constraintValues );
    }

    public void retrieve() throws java.sql.SQLException {
        Object[] constraintValues = { getInventoryID(), getApplianceID() };

        Object[] results = retrieve( SETTER_COLUMNS, TABLE_NAME, CONSTRAINT_COLUMNS, constraintValues );

        if (results.length == SETTER_COLUMNS.length) {
            setAddressingGroupID( (Integer) results[1] );
            setLoadNumber( (Integer) results[2] );
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
	/**
	 * @return
	 */
	public Integer getLoadNumber() {
		return loadNumber;
	}

	/**
	 * @param integer
	 */
	public void setLoadNumber(Integer integer) {
		loadNumber = integer;
	}

}