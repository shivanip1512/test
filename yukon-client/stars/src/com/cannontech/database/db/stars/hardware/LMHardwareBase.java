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
    	String sql = "SELECT inv.InventoryID FROM " + TABLE_NAME + " inv, ECToInventoryMapping map " +
    			"WHERE LMHardwareTypeID = " + deviceType + " AND UPPER(ManufacturerSerialNumber) LIKE UPPER('" + serialNo + "')" +
    			" AND inv.InventoryID >= 0 AND inv.InventoryID = map.InventoryID AND map.EnergyCompanyID = " + energyCompanyID;
    	java.sql.Statement stmt = conn.createStatement();
    	java.sql.ResultSet rset = stmt.executeQuery( sql );
    	
		java.util.ArrayList invIDList = new java.util.ArrayList();
    	while (rset.next())
    		invIDList.add( new Integer(rset.getInt(1)) );
    	
    	int[] invIDs = new int[ invIDList.size() ];
    	for (int i = 0; i < invIDList.size(); i++)
    		invIDs[i] = ((Integer) invIDList.get(i)).intValue();
		
		return invIDs;
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
		
		java.sql.Statement stmt = conn.createStatement();
		java.sql.ResultSet rset = stmt.executeQuery( sql );
		
		java.util.TreeMap snTable = new java.util.TreeMap();
		while (rset.next()) {
			int invID = rset.getInt(1);
			String serialNo = rset.getString(2);
			snTable.put(serialNo, new Integer(invID));
		}
		
		return snTable;
    }
    
    public static int[] searchBySerialNumber(String serialNo, int energyCompanyID, java.sql.Connection conn)
    throws java.sql.SQLException {
    	String sql = "SELECT inv.InventoryID FROM " + TABLE_NAME + " inv, ECToInventoryMapping map " +
    			"WHERE UPPER(ManufacturerSerialNumber) = UPPER(?) AND inv.InventoryID >= 0 " +
    			"AND inv.InventoryID = map.InventoryID AND map.EnergyCompanyID = ?";
    	
    	java.sql.PreparedStatement stmt = conn.prepareStatement(sql);
    	stmt.setString(1, serialNo);
    	stmt.setInt(2, energyCompanyID);
    	java.sql.ResultSet rset = stmt.executeQuery();
    	
		java.util.ArrayList invIDList = new java.util.ArrayList();
		while (rset.next())
			invIDList.add( new Integer(rset.getInt(1)) );
    	
		int[] invIDs = new int[ invIDList.size() ];
		for (int i = 0; i < invIDList.size(); i++)
			invIDs[i] = ((Integer) invIDList.get(i)).intValue();
		
		return invIDs;
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