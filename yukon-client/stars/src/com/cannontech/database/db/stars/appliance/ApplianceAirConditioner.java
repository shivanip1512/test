package com.cannontech.database.db.stars.appliance;

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

public class ApplianceAirConditioner extends DBPersistent {

    private Integer applianceID = null;
    private Integer tonnageID = new Integer( CtiUtilities.NONE_ZERO_ID );
    private Integer typeID = new Integer( CtiUtilities.NONE_ZERO_ID );

    public static final String[] SETTER_COLUMNS = {
        "TonnageID", "TypeID"
    };

    public static final String[] CONSTRAINT_COLUMNS = { "ApplianceID" };

    public static final String TABLE_NAME = "ApplianceAirConditioner";

    public ApplianceAirConditioner() {
        super();
    }

    public void delete() throws java.sql.SQLException {
        Object[] constraintValues = { getApplianceID() };

        delete( TABLE_NAME, CONSTRAINT_COLUMNS, constraintValues );
    }

    public void add() throws java.sql.SQLException {
        Object[] addValues = {
            getApplianceID(), getTonnageID(), getTypeID()
        };

        add( TABLE_NAME, addValues );
    }

    public void update() throws java.sql.SQLException {
        Object[] setValues = {
            getTonnageID(), getTypeID()
        };

        Object[] constraintValues = { getApplianceID() };

        update( TABLE_NAME, SETTER_COLUMNS, setValues, CONSTRAINT_COLUMNS, constraintValues );
    }

    public void retrieve() throws java.sql.SQLException {
        Object[] constraintValues = { getApplianceID() };

        Object[] results = retrieve( SETTER_COLUMNS, TABLE_NAME, CONSTRAINT_COLUMNS, constraintValues );

        if (results.length == SETTER_COLUMNS.length) {
            setTonnageID( (Integer) results[0] );
            setTypeID( (Integer) results[1] );
        }
        else
            throw new Error(getClass() + " - Incorrect number of results retrieved");
    }
    
    public static ApplianceAirConditioner getApplianceAirConditioner(Integer appID) {
    	String sql = "SELECT ApplianceID, TonnageID, TypeID " +
    			"FROM " + TABLE_NAME + " WHERE ApplianceID = " + appID;
    	com.cannontech.database.SqlStatement stmt = new com.cannontech.database.SqlStatement(
    			sql, com.cannontech.common.util.CtiUtilities.getDatabaseAlias() );
    	
    	try {
    		stmt.execute();
    		if (stmt.getRowCount() == 0) return null;
    		Object[] row = stmt.getRow(0);
    		
    		ApplianceAirConditioner app = new ApplianceAirConditioner();
    		app.setApplianceID( new Integer(((java.math.BigDecimal) row[0]).intValue()) );
    		app.setTonnageID( new Integer(((java.math.BigDecimal) row[1]).intValue()) );
    		app.setTypeID( new Integer(((java.math.BigDecimal) row[2]).intValue()) );
    		
    		return app;
    	}
    	catch (Exception e) {
    		com.cannontech.clientutils.CTILogger.error( e.getMessage(), e );
    	}
    	
    	return null;
    }

    public Integer getApplianceID() {
        return applianceID;
    }

    public void setApplianceID(Integer newApplianceID) {
        applianceID = newApplianceID;
    }
	/**
	 * Returns the tonageID.
	 * @return Integer
	 */
	public Integer getTonnageID() {
		return tonnageID;
	}

	/**
	 * Returns the typeID.
	 * @return Integer
	 */
	public Integer getTypeID() {
		return typeID;
	}

	/**
	 * Sets the tonageID.
	 * @param tonageID The tonageID to set
	 */
	public void setTonnageID(Integer tonnageID) {
		this.tonnageID = tonnageID;
	}

	/**
	 * Sets the typeID.
	 * @param typeID The typeID to set
	 */
	public void setTypeID(Integer typeID) {
		this.typeID = typeID;
	}

}