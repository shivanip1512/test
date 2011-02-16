package com.cannontech.database.db.stars.customer;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.database.SqlStatement;
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

public class SiteInformation extends DBPersistent {

    public static final int NONE_INT = 0;

    private Integer siteID = null;
    private String feeder = "";
    private String pole = "";
    private String transformerSize = "";
    private String serviceVoltage = "";
    private Integer substationID = new Integer( com.cannontech.database.db.stars.Substation.NONE_INT );

    public static final String[] SETTER_COLUMNS = {
        "Feeder", "Pole", "TransformerSize", "ServiceVoltage", "SubstationID"
    };

    public static final String[] CONSTRAINT_COLUMNS = { "SiteID" };

    public static final String TABLE_NAME = "SiteInformation";

    public SiteInformation() {
        super();
    }

    @Override
    public void delete() throws java.sql.SQLException {
        Object[] constraintValues = { getSiteID() };

        delete( TABLE_NAME, CONSTRAINT_COLUMNS, constraintValues );
    }

    @Override
    public void add() throws java.sql.SQLException {
    	if (getSiteID() == null)
    		setSiteID( getNextSiteID() );
    		
        Object[] addValues = {
            getSiteID(), getFeeder(), getPole(), getTransformerSize(),
            getServiceVoltage(), getSubstationID()
        };

        add( TABLE_NAME, addValues );
    }

    @Override
    public void update() throws java.sql.SQLException {
        Object[] setValues = {
            getFeeder(), getPole(), getTransformerSize(),
            getServiceVoltage(), getSubstationID()
        };

        Object[] constraintValues = { getSiteID() };

        update( TABLE_NAME, SETTER_COLUMNS, setValues, CONSTRAINT_COLUMNS, constraintValues );
    }

    @Override
    public void retrieve() throws java.sql.SQLException {
        Object[] constraintValues = { getSiteID() };

        Object[] results = retrieve( SETTER_COLUMNS, TABLE_NAME, CONSTRAINT_COLUMNS, constraintValues );

        if (results.length == SETTER_COLUMNS.length) {
            setFeeder( (String) results[0] );
            setPole( (String) results[1] );
            setTransformerSize( (String) results[2] );
            setServiceVoltage( (String) results[3] );
            setSubstationID( (Integer) results[4] );
        }
        else
            throw new Error(getClass() + " - Incorrect number of results retrieved");
    }

    private Integer getNextSiteID() {
        Integer nextValueId = YukonSpringHook.getNextValueHelper().getNextValue(TABLE_NAME);
        return nextValueId;
    }
    
    /**
     * @deprecated Use the siteInformationDao.resetSubstation method instead of this method.
     */
    @Deprecated
    public static void resetSubstation(int substationID) {
    	String sql = "UPDATE " + TABLE_NAME + " SET SubstationID = 0 WHERE SubstationID = " + substationID;
    	SqlStatement stmt = new SqlStatement( sql, CtiUtilities.getDatabaseAlias() );
    	
    	try {
    		stmt.execute();
    	}
    	catch (Exception e) {
    		CTILogger.error( e.getMessage(), e );
    	}
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