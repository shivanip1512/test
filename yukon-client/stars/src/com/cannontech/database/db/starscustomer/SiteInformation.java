package com.cannontech.database.db.starscustomer;

import com.cannontech.database.db.*;


/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: </p>
 * @author unascribed
 * @version 1.0
 */

public class SiteInformation extends DBPersistent {

    public static final int NONE_INT = 0;

    private Integer siteID = null;
    private Integer substationID = new Integer( Substation.NONE_INT );
    private String feeder = null;
    private String pole = null;
    private String transformerSize = null;
    private String serviceVoltage = null;

    public static final String[] SETTER_COLUMNS = {
        "SubstationID", "Feeder", "Pole",
        "TransformerSize", "ServiceVoltage"
    };

    public static final String[] CONSTRAINT_COLUMNS = { "SiteID" };

    public static final String TABLE_NAME = "SiteInformation";

    public static final String GET_NEXT_SITE_ID_SQL =
        "SELECT MAX(SiteID) FROM " + TABLE_NAME;

    public SiteInformation() {
        super();
    }

    public static void clearSubstation(Integer substationID, java.sql.Connection conn) {
        String sql = "UPDATE " + TABLE_NAME + " SET SubstationID = " +
                     Substation.NONE_INT + " WHERE SubstationID = ?";

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
                pstmt.setInt( 1, substationID.intValue() );
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
        Object[] constraintValues = { getSiteID() };

        delete( TABLE_NAME, CONSTRAINT_COLUMNS, constraintValues );
    }

    public void add() throws java.sql.SQLException {
    	if (getSiteID() == null)
    		setSiteID( getNextSiteID() );
    		
        Object[] addValues = {
            getSiteID(), getSubstationID(), getFeeder(), getPole(),
            getTransformerSize(), getServiceVoltage()
        };

        add( TABLE_NAME, addValues );
    }

    public void update() throws java.sql.SQLException {
        Object[] setValues = {
            getSubstationID(), getFeeder(), getPole(),
            getTransformerSize(), getServiceVoltage()
        };

        Object[] constraintValues = { getSiteID() };

        update( TABLE_NAME, SETTER_COLUMNS, setValues, CONSTRAINT_COLUMNS, constraintValues );
    }

    public void retrieve() throws java.sql.SQLException {
        Object[] constraintValues = { getSiteID() };

        Object[] results = retrieve( SETTER_COLUMNS, TABLE_NAME, CONSTRAINT_COLUMNS, constraintValues );

        if (results.length == SETTER_COLUMNS.length) {
            setSubstationID( (Integer) results[0] );
            setFeeder( (String) results[1] );
            setPole( (String) results[2] );
            setTransformerSize( (String) results[3] );
            setServiceVoltage( (String) results[4] );
        }
        else
            throw new Error(getClass() + " - Incorrect number of results retrieved");
    }

    public final Integer getNextSiteID() {
        java.sql.PreparedStatement pstmt = null;
        java.sql.ResultSet rset = null;

        int nextSiteID = 1;

        try {
            pstmt = getDbConnection().prepareStatement( GET_NEXT_SITE_ID_SQL );
            rset = pstmt.executeQuery();

            if (rset.next())
                nextSiteID = rset.getInt(1) + 1;
        }
        catch (java.sql.SQLException e) {
            e.printStackTrace();
        }
        finally {
            try {
                if (pstmt != null) pstmt.close();
            }
            catch (java.sql.SQLException e2) {
                e2.printStackTrace();
            }
        }

        return new Integer( nextSiteID );
    }

    public Integer getSiteID() {
        return siteID;
    }

    public void setSiteID(Integer newSiteID) {
        siteID = newSiteID;
    }

    public Integer getSubstationID() {
        return substationID;
    }

    public void setSubstationID(Integer newSubstationID) {
        substationID = newSubstationID;
    }

    public String getFeeder() {
        return feeder;
    }

    public void setFeeder(String newFeeder) {
        feeder = newFeeder;
    }

    public String getPole() {
        return pole;
    }

    public void setPole(String newPole) {
        pole = newPole;
    }

    public String getTransformerSize() {
        return transformerSize;
    }

    public void setTransformerSize(String newTransformerSize) {
        transformerSize = newTransformerSize;
    }

    public String getServiceVoltage() {
        return serviceVoltage;
    }

    public void setServiceVoltage(String newServiceVoltage) {
        serviceVoltage = newServiceVoltage;
    }
}