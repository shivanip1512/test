package com.cannontech.database.db.starsappliance;

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
    public static final String CATEGORY_AC = "AC";
    public static final String CATEGORY_WH = "WH";

    private Integer applianceCategoryID = null;
    private String description = null;
    private String category = null;

    public static final String[] SETTER_COLUMNS = {
        "Description", "Category"
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
            getApplianceCategoryID(), getDescription(), getCategory()
        };

        add( TABLE_NAME, addValues );
    }

    public void update() throws java.sql.SQLException {
        Object[] setValues = {
            getDescription(), getCategory()
        };

        Object[] constraintValues = { getApplianceCategoryID() };

        update( TABLE_NAME, SETTER_COLUMNS, setValues, CONSTRAINT_COLUMNS, constraintValues );
    }

    public void retrieve() throws java.sql.SQLException {
        Object[] constraintValues = { getApplianceCategoryID() };

        Object[] results = retrieve( SETTER_COLUMNS, TABLE_NAME, CONSTRAINT_COLUMNS, constraintValues );

        if (results.length == SETTER_COLUMNS.length) {
            setDescription( (String) results[0] );
            setCategory( (String) results[1] );
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

    public String getCategory() {
        return category;
    }

    public void setCategory(String newCategory) {
        category = newCategory;
    }
}