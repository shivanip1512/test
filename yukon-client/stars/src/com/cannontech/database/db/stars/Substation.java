package com.cannontech.database.db.stars;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.database.db.DBPersistent;
import com.cannontech.spring.YukonSpringHook;


/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: </p>
 * @author unascribed
 * @version 1.0
 */

public class Substation extends DBPersistent {

    public static final int NONE_INT = 0;

    private Integer substationID = null;
    private String substationName = "";
    private Integer lmRouteID = new Integer(NONE_INT);

    public static final String[] SETTER_COLUMNS = {
        "SubstationName", "LMRouteID"
    };

    public static final String[] CONSTRAINT_COLUMNS = { "SubstationID" };

    public static final String TABLE_NAME = "Substation";

    public Substation() {
        super();
    }

    @Override
    public void delete() throws java.sql.SQLException {
        Object[] constraintValues = { getSubstationID() };

        delete( TABLE_NAME, CONSTRAINT_COLUMNS, constraintValues );
    }

    @Override
    public void add() throws java.sql.SQLException {
    	if (getSubstationID() == null)
    		setSubstationID( getNextSubstationID() );
    		
        Object[] addValues = {
            getSubstationID(), getSubstationName(), getRouteID()
        };

        add( TABLE_NAME, addValues );
    }

    @Override
    public void update() throws java.sql.SQLException {
        Object[] setValues = {
            getSubstationName(), getRouteID()
        };

        Object[] constraintValues = { getSubstationID() };

        update( TABLE_NAME, SETTER_COLUMNS, setValues, CONSTRAINT_COLUMNS, constraintValues );
    }

    @Override
    public void retrieve() throws java.sql.SQLException {
        Object[] constraintValues = { getSubstationID() };

        Object[] results = retrieve( SETTER_COLUMNS, TABLE_NAME, CONSTRAINT_COLUMNS, constraintValues );

        if (results.length == SETTER_COLUMNS.length) {
            setSubstationName( (String) results[0] );
            setRouteID( (Integer) results[1] );
        }
        else
            throw new Error(getClass() + " - Incorrect number of results retrieved");
    }

    public final Integer getNextSubstationID() {
        Integer nextValueId = YukonSpringHook.getNextValueHelper().getNextValue(TABLE_NAME);
        return nextValueId;
    }
    
    public static Substation[] getAllSubstations(Integer energyCompanyID) {
    	com.cannontech.database.db.stars.ECToGenericMapping[] items =
    			com.cannontech.database.db.stars.ECToGenericMapping.getAllMappingItems( energyCompanyID, TABLE_NAME );
    	if (items == null || items.length == 0)
    		return new Substation[0];
    	
    	StringBuffer sql = new StringBuffer( "SELECT * FROM " + TABLE_NAME + " WHERE SubstationID = " + items[0].getItemID().toString() );
    	for (int i = 1; i < items.length; i++)
    		sql.append( " OR SubstationID = " ).append( items[i].getItemID() );
    	
    	com.cannontech.database.SqlStatement stmt = new com.cannontech.database.SqlStatement(
    			sql.toString(), com.cannontech.common.util.CtiUtilities.getDatabaseAlias() );

        try {
        	stmt.execute();
        	Substation[] subs = new Substation[ stmt.getRowCount() ];

            for (int i = 0; i < subs.length; i++) {
            	Object[] row = stmt.getRow(i);
            	subs[i] = new Substation();
            	
            	subs[i].setSubstationID( new Integer(((java.math.BigDecimal) row[0]).intValue()) );
            	subs[i].setSubstationName( (String) row[1] );
            	subs[i].setRouteID( new Integer(((java.math.BigDecimal) row[2]).intValue()) );
            }
            
            return subs;
        }
        catch(Exception e)
        {
            CTILogger.error( e.getMessage(), e );
        }

        return null;
    }

    public Integer getSubstationID() {
        return substationID;
    }

    public void setSubstationID(Integer newSubstationID) {
        substationID = newSubstationID;
    }

    public String getSubstationName() {
        return substationName;
    }

    public void setSubstationName(String newSubstationName) {
        substationName = newSubstationName;
    }

    public Integer getRouteID() {
        return lmRouteID;
    }

    public void setRouteID(Integer newRouteID) {
        lmRouteID = newRouteID;
    }
}