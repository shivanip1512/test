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

public class ApplianceCategory extends DBPersistent {

    public static final int NONE_INT = 0;

    private Integer applianceCategoryID = null;
    private String description = "";
    private Integer categoryID = new Integer( com.cannontech.database.db.stars.CustomerListEntry.NONE_INT );
    private Integer webConfigurationID = new Integer( com.cannontech.database.db.stars.CustomerWebConfiguration.NONE_INT );

    public static final String[] SETTER_COLUMNS = {
        "Description", "CategoryID", "WebConfigurationID"
    };

    public static final String[] CONSTRAINT_COLUMNS = { "ApplianceCategoryID" };

    public static final String TABLE_NAME = "ApplianceCategory";

    public static final String GET_NEXT_CATEGORY_ID_SQL =
        "SELECT MAX(ApplianceCategoryID) FROM " + TABLE_NAME;

    public ApplianceCategory() {
        super();
    }

    public void delete() throws java.sql.SQLException {
        Object[] constraintValues = { getApplianceCategoryID() };

        delete( TABLE_NAME, CONSTRAINT_COLUMNS, constraintValues );
    }

    public void add() throws java.sql.SQLException {
    	if (getApplianceCategoryID() == null)
    		setApplianceCategoryID( getNextCategoryID() );
    		
        Object[] addValues = {
            getApplianceCategoryID(), getDescription(), getCategoryID(), getWebConfigurationID()
        };

        add( TABLE_NAME, addValues );
    }

    public void update() throws java.sql.SQLException {
        Object[] setValues = {
            getDescription(), getCategoryID(), getWebConfigurationID()
        };

        Object[] constraintValues = { getApplianceCategoryID() };

        update( TABLE_NAME, SETTER_COLUMNS, setValues, CONSTRAINT_COLUMNS, constraintValues );
    }

    public void retrieve() throws java.sql.SQLException {
        Object[] constraintValues = { getApplianceCategoryID() };

        Object[] results = retrieve( SETTER_COLUMNS, TABLE_NAME, CONSTRAINT_COLUMNS, constraintValues );

        if (results.length == SETTER_COLUMNS.length) {
            setDescription( (String) results[0] );
            setCategoryID( (Integer) results[1] );
            setWebConfigurationID( (Integer) results[2] );
        }
        else
            throw new Error(getClass() + " - Incorrect number of results retrieved");
    }

    public final Integer getNextCategoryID() {
        java.sql.PreparedStatement pstmt = null;
        java.sql.ResultSet rset = null;

        int nextCategoryID = 1;

        try {
            pstmt = getDbConnection().prepareStatement( GET_NEXT_CATEGORY_ID_SQL );
            rset = pstmt.executeQuery();

            if (rset.next())
                nextCategoryID = rset.getInt(1) + 1;
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

        return new Integer( nextCategoryID );
    }
    
    public static ApplianceCategory[] getAllApplianceCategories(Integer categoryID, java.sql.Connection conn) {
    	String sql = "SELECT * FROM " + TABLE_NAME + " WHERE CategoryID = ?";
    	
        java.sql.PreparedStatement pstmt = null;
        java.sql.ResultSet rset = null;
        java.util.ArrayList appCatList = new java.util.ArrayList();

        try {
            pstmt = conn.prepareStatement( sql );
            pstmt.setInt( 1, categoryID.intValue() );
            rset = pstmt.executeQuery();

            while (rset.next()) {
            	ApplianceCategory appCat = new ApplianceCategory();
            	
            	appCat.setApplianceCategoryID( new Integer(rset.getInt("ApplianceCategoryID")) );
            	appCat.setDescription( rset.getString("Description") );
            	appCat.setCategoryID( new Integer(rset.getInt("CategoryID")) );
            	appCat.setWebConfigurationID( new Integer(rset.getInt("WebConfigurationID")) );
            	
            	appCatList.add( appCat );
            }
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
        
        ApplianceCategory[] appCats = new ApplianceCategory[ appCatList.size() ];
        appCatList.toArray( appCats );
        return appCats;
    }

    public Integer getApplianceCategoryID() {
        return applianceCategoryID;
    }

    public void setApplianceCategoryID(Integer newApplianceCategoryID) {
        applianceCategoryID = newApplianceCategoryID;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String newDescription) {
        description = newDescription;
    }
	/**
	 * Returns the categoryID.
	 * @return Integer
	 */
	public Integer getCategoryID() {
		return categoryID;
	}

	/**
	 * Returns the webConfigurationID.
	 * @return Integer
	 */
	public Integer getWebConfigurationID() {
		return webConfigurationID;
	}

	/**
	 * Sets the categoryID.
	 * @param categoryID The categoryID to set
	 */
	public void setCategoryID(Integer categoryID) {
		this.categoryID = categoryID;
	}

	/**
	 * Sets the webConfigurationID.
	 * @param webConfigurationID The webConfigurationID to set
	 */
	public void setWebConfigurationID(Integer webConfigurationID) {
		this.webConfigurationID = webConfigurationID;
	}

}