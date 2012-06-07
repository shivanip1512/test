package com.cannontech.stars.database.db.appliance;

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

public class ApplianceDualStageAirCond extends DBPersistent {

    private Integer applianceID = null;
    private Integer stageOneTonnageID = new Integer( CtiUtilities.NONE_ZERO_ID );
    private Integer stageTwoTonnageID = new Integer( CtiUtilities.NONE_ZERO_ID );
    private Integer typeID = new Integer( CtiUtilities.NONE_ZERO_ID );

    public static final String[] SETTER_COLUMNS = {
        "StageOneTonnageID", "StageTwoTonnageID", "TypeID"
    };

    public static final String[] CONSTRAINT_COLUMNS = { "ApplianceID" };

    public static final String TABLE_NAME = "ApplianceDualStageAirCond";

    public ApplianceDualStageAirCond() {
        super();
    }

    public void delete() throws java.sql.SQLException {
        Object[] constraintValues = { getApplianceID() };

        delete( TABLE_NAME, CONSTRAINT_COLUMNS, constraintValues );
    }

    public void add() throws java.sql.SQLException {
        Object[] addValues = {
            getApplianceID(), getStageOneTonnageID(), getStageTwoTonnageID(), getTypeID()
        };

        add( TABLE_NAME, addValues );
    }

    public void update() throws java.sql.SQLException {
        Object[] setValues = {
            getStageOneTonnageID(), getStageTwoTonnageID(), getTypeID()
        };

        Object[] constraintValues = { getApplianceID() };

        update( TABLE_NAME, SETTER_COLUMNS, setValues, CONSTRAINT_COLUMNS, constraintValues );
    }

    public void retrieve() throws java.sql.SQLException {
        Object[] constraintValues = { getApplianceID() };

        Object[] results = retrieve( SETTER_COLUMNS, TABLE_NAME, CONSTRAINT_COLUMNS, constraintValues );

        if (results.length == SETTER_COLUMNS.length) {
            setStageOneTonnageID( (Integer) results[0] );
            setStageTwoTonnageID( (Integer) results[1] );
            setTypeID( (Integer) results[2] );
        }
        else
            throw new Error(getClass() + " - Incorrect number of results retrieved");
    }
    
    public static ApplianceDualStageAirCond getApplianceDualStageAirCond(Integer appID) {
    	String sql = "SELECT ApplianceID, StageOneTonnageID, StageTwoTonnageID, TypeID " +
    			"FROM " + TABLE_NAME + " WHERE ApplianceID = " + appID;
    	com.cannontech.database.SqlStatement stmt = new com.cannontech.database.SqlStatement(
    			sql, com.cannontech.common.util.CtiUtilities.getDatabaseAlias() );
    	
    	try {
    		stmt.execute();
    		if (stmt.getRowCount() == 0) return null;
    		Object[] row = stmt.getRow(0);
    		
    		ApplianceDualStageAirCond app = new ApplianceDualStageAirCond();
    		app.setApplianceID( new Integer(((java.math.BigDecimal) row[0]).intValue()) );
    		app.setStageOneTonnageID( new Integer(((java.math.BigDecimal) row[1]).intValue()) );
            app.setStageTwoTonnageID( new Integer(((java.math.BigDecimal) row[2]).intValue()) );
            app.setTypeID( new Integer(((java.math.BigDecimal) row[3]).intValue()) );
    		
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
	public Integer getStageOneTonnageID() {
		return stageOneTonnageID;
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
	public void setStageOneTonnageID(Integer stageOneTonnageID) {
		this.stageOneTonnageID = stageOneTonnageID;
	}

	/**
	 * Sets the typeID.
	 * @param typeID The typeID to set
	 */
	public void setTypeID(Integer typeID) {
		this.typeID = typeID;
	}

    public Integer getStageTwoTonnageID() {
        return stageTwoTonnageID;
    }

    public void setStageTwoTonnageID(Integer stageTwoTonnageID) {
        this.stageTwoTonnageID = stageTwoTonnageID;
    }

}