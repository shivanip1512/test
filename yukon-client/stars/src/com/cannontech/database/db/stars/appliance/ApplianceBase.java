package com.cannontech.database.db.stars.appliance;

import com.cannontech.database.db.DBPersistent;


/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: </p>
 * @author unascribed
 * @version 1.0
 */

public class ApplianceBase extends DBPersistent {

    public static final int NONE_INT = 0;

    private Integer applianceID = null;
    private Integer accountID = new Integer( com.cannontech.database.db.stars.customer.CustomerAccount.NONE_INT );
    private Integer applianceCategoryID = new Integer( ApplianceCategory.NONE_INT );
    private Integer lmProgramID = new Integer( NONE_INT );
    private Integer yearManufactured = new Integer(0);
    private Integer manufacturerID = new Integer( com.cannontech.common.util.CtiUtilities.NONE_ID );
    private Integer locationID = new Integer( com.cannontech.common.util.CtiUtilities.NONE_ID );
    private Integer kwCapacity = new Integer(0);
    private Integer efficiencyRating = new Integer(0);
    private String notes = "";

    public static final String[] SETTER_COLUMNS = {
        "AccountID", "ApplianceCategoryID", "LMProgramID", "YearManufactured",
        "ManufacturerID", "LocationID", "KWCapacity", "EfficiencyRating", "Notes"
    };

    public static final String[] CONSTRAINT_COLUMNS = { "ApplianceID" };

    public static final String TABLE_NAME = "ApplianceBase";

    public static final String GET_NEXT_APPLIANCE_ID_SQL =
        "SELECT MAX(ApplianceID) FROM " + TABLE_NAME;

    public ApplianceBase() {
        super();
    }

    public static ApplianceBase[] getAllAppliances(Integer accountID) {
        String sql = "SELECT * FROM " + TABLE_NAME + " WHERE AccountID = " + accountID.toString();

		try {
			com.cannontech.database.SqlStatement stmt = new com.cannontech.database.SqlStatement( sql, com.cannontech.common.util.CtiUtilities.getDatabaseAlias() );
			stmt.execute();

	        ApplianceBase[] apps = new ApplianceBase[ stmt.getRowCount() ];
            for (int i = 0; i < stmt.getRowCount(); i++) {
            	Object[] row = stmt.getRow(i);
                apps[i] = new ApplianceBase();

                apps[i].setApplianceID( new Integer(((java.math.BigDecimal) row[0]).intValue()) );
                apps[i].setAccountID( new Integer(((java.math.BigDecimal) row[1]).intValue()) );
                apps[i].setApplianceCategoryID( new Integer(((java.math.BigDecimal) row[2]).intValue()) );
                apps[i].setLMProgramID( new Integer(((java.math.BigDecimal) row[3]).intValue()) );
                apps[i].setYearManufactured( new Integer(((java.math.BigDecimal) row[4]).intValue()) );
                apps[i].setManufacturerID( new Integer(((java.math.BigDecimal) row[5]).intValue()) );
                apps[i].setLocationID( new Integer(((java.math.BigDecimal) row[6]).intValue()) );
                apps[i].setKWCapacity( new Integer(((java.math.BigDecimal) row[7]).intValue()) );
                apps[i].setEfficiencyRating( new Integer(((java.math.BigDecimal) row[8]).intValue()) );
                apps[i].setNotes( (String) row[9] );
            }
            
            return apps;
        }
        catch( Exception e )
        {
            e.printStackTrace();
        }

        return null;
    }

    public void delete() throws java.sql.SQLException {
        Object[] constraintValues = { getApplianceID() };

        delete( TABLE_NAME, CONSTRAINT_COLUMNS, constraintValues );
    }

    public void add() throws java.sql.SQLException {
    	if (getApplianceID() == null)
    		setApplianceID( getNextApplianceID() );
    		
        Object[] addValues = {
            getApplianceID(), getAccountID(), getApplianceCategoryID(), getLMProgramID(), getYearManufactured(),
            getManufacturerID(), getLocationID(), getKWCapacity(), getEfficiencyRating(), getNotes()
        };

        add( TABLE_NAME, addValues );
    }

    public void update() throws java.sql.SQLException {
        Object[] setValues = {
            getAccountID(), getApplianceCategoryID(), getLMProgramID(), getYearManufactured(),
            getManufacturerID(), getLocationID(), getKWCapacity(), getEfficiencyRating(), getNotes()
        };

        Object[] constraintValues = { getApplianceID() };

        update( TABLE_NAME, SETTER_COLUMNS, setValues, CONSTRAINT_COLUMNS, constraintValues );
    }

    public void retrieve() throws java.sql.SQLException {
        Object[] constraintValues = { getApplianceID() };

        Object[] results = retrieve( SETTER_COLUMNS, TABLE_NAME, CONSTRAINT_COLUMNS, constraintValues );

        if (results.length == SETTER_COLUMNS.length) {
            setAccountID( (Integer) results[0] );
            setApplianceCategoryID( (Integer) results[1] );
            setLMProgramID( (Integer) results[2] );
            setYearManufactured( (Integer) results[3] );
            setManufacturerID( (Integer) results[4] );
            setLocationID( (Integer) results[5] );
            setKWCapacity( (Integer) results[6] );
            setEfficiencyRating( (Integer) results[7] );
            setNotes( (String) results[8] );
        }
        else
            throw new Error(getClass() + " - Incorrect number of results retrieved");
    }

    public final Integer getNextApplianceID() {
        java.sql.PreparedStatement pstmt = null;
        java.sql.ResultSet rset = null;

        int nextApplianceID = 1;

        try {
            pstmt = getDbConnection().prepareStatement( GET_NEXT_APPLIANCE_ID_SQL );
            rset = pstmt.executeQuery();

            if (rset.next())
                nextApplianceID = rset.getInt(1) + 1;
        }
        catch (java.sql.SQLException e) {
            e.printStackTrace();
        }
        finally {
            try {
				if( rset != null ) rset.close();
                if (pstmt != null) pstmt.close();
            }
            catch (java.sql.SQLException e2) {
                e2.printStackTrace();
            }
        }

        return new Integer( nextApplianceID );
    }

    public Integer getApplianceID() {
        return applianceID;
    }

    public void setApplianceID(Integer newApplianceID) {
        applianceID = newApplianceID;
    }

    public Integer getAccountID() {
        return accountID;
    }

    public void setAccountID(Integer newAccountID) {
        accountID = newAccountID;
    }

    public Integer getApplianceCategoryID() {
        return applianceCategoryID;
    }

    public void setApplianceCategoryID(Integer newApplianceCategoryID) {
        applianceCategoryID = newApplianceCategoryID;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String newNotes) {
        notes = newNotes;
    }
	/**
	 * Returns the lmProgramID.
	 * @return Integer
	 */
	public Integer getLMProgramID() {
		return lmProgramID;
	}

	/**
	 * Sets the lmProgramID.
	 * @param lmProgramID The lmProgramID to set
	 */
	public void setLMProgramID(Integer lmProgramID) {
		this.lmProgramID = lmProgramID;
	}

	/**
	 * Returns the efficiencyRating.
	 * @return Integer
	 */
	public Integer getEfficiencyRating() {
		return efficiencyRating;
	}

	/**
	 * Returns the kwCapacity.
	 * @return Double
	 */
	public Integer getKWCapacity() {
		return kwCapacity;
	}

	/**
	 * Returns the locationID.
	 * @return Integer
	 */
	public Integer getLocationID() {
		return locationID;
	}

	/**
	 * Returns the menufacturerID.
	 * @return Integer
	 */
	public Integer getManufacturerID() {
		return manufacturerID;
	}

	/**
	 * Sets the efficiencyRating.
	 * @param efficiencyRating The efficiencyRating to set
	 */
	public void setEfficiencyRating(Integer efficiencyRating) {
		this.efficiencyRating = efficiencyRating;
	}

	/**
	 * Sets the kwCapacity.
	 * @param kwCapacity The kwCapacity to set
	 */
	public void setKWCapacity(Integer kwCapacity) {
		this.kwCapacity = kwCapacity;
	}

	/**
	 * Sets the locationID.
	 * @param locationID The locationID to set
	 */
	public void setLocationID(Integer locationID) {
		this.locationID = locationID;
	}

	/**
	 * Sets the menufacturerID.
	 * @param menufacturerID The menufacturerID to set
	 */
	public void setManufacturerID(Integer menufacturerID) {
		this.manufacturerID = menufacturerID;
	}

	/**
	 * Returns the yearManufactured.
	 * @return Integer
	 */
	public Integer getYearManufactured() {
		return yearManufactured;
	}

	/**
	 * Sets the yearManufactured.
	 * @param yearManufactured The yearManufactured to set
	 */
	public void setYearManufactured(Integer yearManufactured) {
		this.yearManufactured = yearManufactured;
	}

}