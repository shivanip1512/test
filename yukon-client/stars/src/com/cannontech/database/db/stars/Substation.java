package com.cannontech.database.db.stars;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.database.db.DBPersistent;


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
    
    public static final String LISTNAME_SUBSTATION = "Substation";

    private Integer substationID = null;
    private String substationName = "";
    private Integer routeID = new Integer(NONE_INT);

    public static final String[] SETTER_COLUMNS = {
        "SubstationName", "RouteID"
    };

    public static final String[] CONSTRAINT_COLUMNS = { "SubstationID" };

    public static final String TABLE_NAME = "Substation";

    public static final String GET_NEXT_SUBSTATION_ID_SQL =
        "SELECT MAX(SubstationID) FROM " + TABLE_NAME;

    public Substation() {
        super();
    }

    public void delete() throws java.sql.SQLException {
        Object[] constraintValues = { getSubstationID() };

        delete( TABLE_NAME, CONSTRAINT_COLUMNS, constraintValues );
    }

    public void add() throws java.sql.SQLException {
    	if (getSubstationID() == null)
    		setSubstationID( getNextSubstationID() );
    		
        Object[] addValues = {
            getSubstationID(), getSubstationName(), getRouteID()
        };

        add( TABLE_NAME, addValues );
    }

    public void update() throws java.sql.SQLException {
        Object[] setValues = {
            getSubstationName(), getRouteID()
        };

        Object[] constraintValues = { getSubstationID() };

        update( TABLE_NAME, SETTER_COLUMNS, setValues, CONSTRAINT_COLUMNS, constraintValues );
    }

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
        java.sql.PreparedStatement pstmt = null;
        java.sql.ResultSet rset = null;

        int nextSubstationID = 1;

        try {
            pstmt = getDbConnection().prepareStatement( GET_NEXT_SUBSTATION_ID_SQL );
            rset = pstmt.executeQuery();

            if (rset.next())
                nextSubstationID = rset.getInt(1) + 1;
        }
        catch (java.sql.SQLException e) {
            CTILogger.error( e.getMessage(), e );
        }
        finally {
            try {
				if (rset != null) rset.close();
                if (pstmt != null) pstmt.close();
            }
            catch (java.sql.SQLException e2) {}
        }

        return new Integer( nextSubstationID );
    }
    
    public static Substation[] getAllSubstations(Integer energyCompanyID) {
    	com.cannontech.database.db.stars.ECToGenericMapping[] items =
    			com.cannontech.database.db.stars.ECToGenericMapping.getAllMappingItems( energyCompanyID, "Substation" );
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
        return routeID;
    }

    public void setRouteID(Integer newRouteID) {
        routeID = newRouteID;
    }
}