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

public class LMHardwareBase extends DBPersistent {

    private Integer inventoryID = null;
    private String manufacturerSerialNumber = "";
    private Integer lmHardwareTypeID = new Integer( com.cannontech.common.util.CtiUtilities.NONE_ID );

    public static final String[] SETTER_COLUMNS = {
        "ManufacturerSerialNumber", "LMHardwareTypeID"
    };

    public static final String[] CONSTRAINT_COLUMNS = { "InventoryID" };

    public static final String TABLE_NAME = "LMHardwareBase";

    public LMHardwareBase() {
        super();
    }

    public void delete() throws java.sql.SQLException {
        Object[] constraintValues = { getInventoryID() };

        delete( TABLE_NAME, CONSTRAINT_COLUMNS, constraintValues );
    }

    public void add() throws java.sql.SQLException {
        Object[] addValues = {
            getInventoryID(), getManufacturerSerialNumber(), getLMHardwareTypeID()
        };

        add( TABLE_NAME, addValues );
    }

    public void update() throws java.sql.SQLException {
        Object[] setValues = {
            getManufacturerSerialNumber(), getLMHardwareTypeID()
        };

        Object[] constraintValues = { getInventoryID() };

        update( TABLE_NAME, SETTER_COLUMNS, setValues, CONSTRAINT_COLUMNS, constraintValues );
    }

    public void retrieve() throws java.sql.SQLException {
        Object[] constraintValues = { getInventoryID() };

        Object[] results = retrieve( SETTER_COLUMNS, TABLE_NAME, CONSTRAINT_COLUMNS, constraintValues );

        if (results.length == SETTER_COLUMNS.length) {
            setManufacturerSerialNumber( (String) results[0] );
            setLMHardwareTypeID( (Integer) results[1] );
        }
        else
            throw new Error(getClass() + " - Incorrect number of results retrieved");
    }
    
    public static int[] searchForLMHardware(int deviceType, String serialNo, int energyCompanyID, java.sql.Connection conn)
    throws java.sql.SQLException {
    	String sql = "SELECT hw.InventoryID " +
    			"FROM " + TABLE_NAME + " hw, ECToInventoryMapping map " +
    			"WHERE hw.LMHardwareTypeID = ? AND UPPER(hw.ManufacturerSerialNumber) LIKE UPPER(?) " +
    			"AND hw.InventoryID >= 0 AND hw.InventoryID = map.InventoryID AND map.EnergyCompanyID = ?";
    	
    	java.sql.PreparedStatement stmt = null;
    	java.sql.ResultSet rset = null;
    	
    	try {
    		stmt = conn.prepareStatement( sql );
			stmt.setInt( 1, deviceType );
			stmt.setString( 2, serialNo );
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
    	finally {
    		if (rset != null) rset.close();
    		if (stmt != null) stmt.close();
    	}
    }
    
    /**
     * Return map from serial number (String) to inventory id (Integer)
     */
    public static java.util.TreeMap searchBySNRange(
    	int deviceType, String serialNoLB, String serialNoUB, int energyCompanyID, java.sql.Connection conn)
    	throws java.sql.SQLException
    {
		String sql = "SELECT inv.InventoryID, ManufacturerSerialNumber FROM " + TABLE_NAME + " inv, ECToInventoryMapping map " +
				"WHERE LMHardwareTypeID = " + deviceType + " AND inv.InventoryID >= 0 AND inv.InventoryID = map.InventoryID " +
				"AND map.EnergyCompanyID = " + energyCompanyID;
		
		if (serialNoLB != null)
			sql += " AND ManufacturerSerialNumber >= " + serialNoLB;
		
		if (serialNoUB != null)
			sql += " AND ManufacturerSerialNumber <= " + serialNoUB;
		
		java.sql.Statement stmt = null;
		java.sql.ResultSet rset = null;
		
		try {
			stmt = conn.createStatement();
			rset = stmt.executeQuery( sql );
			
			java.util.TreeMap snTable = new java.util.TreeMap();
			while (rset.next()) {
				int invID = rset.getInt(1);
				String serialNo = rset.getString(2);
				snTable.put(serialNo, new Integer(invID));
			}
			
			return snTable;
		}
		finally {
			if (rset != null) rset.close();
			if (stmt != null) stmt.close();
		}
    }
    
    public static LMHardwareBase[] searchBySerialNumber(String serialNo, int energyCompanyID, java.sql.Connection conn)
    throws java.sql.SQLException {
    	String sql = "SELECT hw.InventoryID, hw.ManufacturerSerialNumber, hw.LMHardwareTypeID " +
    			"FROM " + TABLE_NAME + " hw, ECToInventoryMapping map " +
    			"WHERE UPPER(hw.ManufacturerSerialNumber) = UPPER(?) AND hw.InventoryID >= 0 " +
    			"AND hw.InventoryID = map.InventoryID AND map.EnergyCompanyID = ?";
    	
    	java.sql.PreparedStatement stmt = null;
    	java.sql.ResultSet rset = null;
    	
    	try {
    		stmt = conn.prepareStatement(sql);
			stmt.setString(1, serialNo);
			stmt.setInt(2, energyCompanyID);
			
			rset = stmt.executeQuery();
    		
			java.util.ArrayList hwList = new java.util.ArrayList();
			while (rset.next()) {
				LMHardwareBase hw = new LMHardwareBase();
				hw.setInventoryID( new Integer(rset.getInt(1)) );
				hw.setManufacturerSerialNumber( rset.getString(2) );
				hw.setLMHardwareTypeID( new Integer(rset.getInt(3)) );
				
				hwList.add( hw );
			}
    		
			LMHardwareBase[] hardwares = new LMHardwareBase[ hwList.size() ];
			hwList.toArray( hardwares );
			
			return hardwares;
    	}
    	finally {
    		if (rset != null) rset.close();
    		if (stmt != null) stmt.close();
    	}
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
	/**
	 * Returns the lmHardwareTypeID.
	 * @return Integer
	 */
	public Integer getLMHardwareTypeID() {
		return lmHardwareTypeID;
	}

	/**
	 * Sets the lmHardwareTypeID.
	 * @param lmHardwareTypeID The lmHardwareTypeID to set
	 */
	public void setLMHardwareTypeID(Integer lmHardwareTypeID) {
		this.lmHardwareTypeID = lmHardwareTypeID;
	}

}