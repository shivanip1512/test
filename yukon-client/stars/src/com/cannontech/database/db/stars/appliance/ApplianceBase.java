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
    private String notes = "";

    public static final String[] SETTER_COLUMNS = {
        "AccountID", "ApplianceCategoryID", "LMProgramID", "Notes"
    };

    public static final String[] CONSTRAINT_COLUMNS = { "ApplianceID" };

    public static final String TABLE_NAME = "ApplianceBase";

    public static final String GET_NEXT_APPLIANCE_ID_SQL =
        "SELECT MAX(ApplianceID) FROM " + TABLE_NAME;

    public ApplianceBase() {
        super();
    }

    public static ApplianceBase[] getAllAppliances(Integer accountID, java.sql.Connection conn) {
        String sql = "SELECT * FROM " + TABLE_NAME + " WHERE AccountID = ?";

        java.sql.PreparedStatement pstmt = null;
        java.sql.ResultSet rset = null;
        java.util.ArrayList loadList = new java.util.ArrayList();

        try
        {
            if( conn == null )
            {
                throw new IllegalStateException("Database connection should not be null.");
            }
            else
            {
                pstmt = conn.prepareStatement(sql);
                pstmt.setInt( 1, accountID.intValue() );
                rset = pstmt.executeQuery();

                while (rset.next()) {
                    ApplianceBase load = new ApplianceBase();

                    load.setApplianceID( new Integer(rset.getInt("ApplianceID")) );
                    load.setAccountID( new Integer(rset.getInt("AccountID")) );
                    load.setApplianceCategoryID( new Integer(rset.getInt("ApplianceCategoryID")) );
                    load.setLMProgramID( new Integer(rset.getInt("LMProgramID")) );
                    load.setNotes( rset.getString("Notes") );

                    loadList.add(load);
                }
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
                if (rset != null) rset.close();
            }
            catch( java.sql.SQLException e2 )
            {
                e2.printStackTrace();
            }
        }

        ApplianceBase[] loads = new ApplianceBase[ loadList.size() ];
        loadList.toArray( loads );
        return loads;
    }

    public void delete() throws java.sql.SQLException {
        Object[] constraintValues = { getApplianceID() };

        delete( TABLE_NAME, CONSTRAINT_COLUMNS, constraintValues );
    }

    public void add() throws java.sql.SQLException {
    	if (getApplianceID() == null)
    		setApplianceID( getNextApplianceID() );
    		
        Object[] addValues = {
            getApplianceID(), getAccountID(), getApplianceCategoryID(),
            getLMProgramID(), getNotes()
        };

        add( TABLE_NAME, addValues );
    }

    public void update() throws java.sql.SQLException {
        Object[] setValues = {
            getAccountID(), getApplianceCategoryID(), getLMProgramID(), getNotes()
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
            setNotes( (String) results[3] );
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

}