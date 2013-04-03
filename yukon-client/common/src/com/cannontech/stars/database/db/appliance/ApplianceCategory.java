package com.cannontech.stars.database.db.appliance;

import java.sql.SQLException;
import java.util.List;

import com.cannontech.common.util.CtiUtilities;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.database.YukonJdbcTemplate;
import com.cannontech.database.YukonResultSet;
import com.cannontech.database.YukonRowMapper;
import com.cannontech.database.db.DBPersistent;
import com.cannontech.spring.YukonSpringHook;
import com.cannontech.stars.energyCompany.EcMappingCategory;


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
    private Integer categoryID = new Integer( com.cannontech.common.util.CtiUtilities.NONE_ZERO_ID );
    private Integer webConfigurationID = new Integer( CtiUtilities.NONE_ZERO_ID );
    private boolean consumerSelectable = true;

    public static final String[] SETTER_COLUMNS = {
        "Description", "CategoryID", "WebConfigurationID", "ConsumerSelectable"
    };

    public static final String[] CONSTRAINT_COLUMNS = { "ApplianceCategoryID" };

    public static final String TABLE_NAME = "ApplianceCategory";

    public ApplianceCategory() {
        super();
    }

    @Override
    public void delete() throws java.sql.SQLException {
        Object[] constraintValues = { getApplianceCategoryID() };

        delete( TABLE_NAME, CONSTRAINT_COLUMNS, constraintValues );
    }

    @Override
    public void add() throws java.sql.SQLException {
    	if (getApplianceCategoryID() == null)
    		setApplianceCategoryID( getNextCategoryID() );
    		
        Object[] addValues = {
            getApplianceCategoryID(), getDescription(), getCategoryID(),
            getWebConfigurationID(), consumerSelectable ? "Y" : "N"
        };

        add( TABLE_NAME, addValues );
    }

    @Override
    public void update() throws java.sql.SQLException {
        Object[] setValues = {
            getDescription(), getCategoryID(), getWebConfigurationID(),
            consumerSelectable ? "Y" : "N"
        };

        Object[] constraintValues = { getApplianceCategoryID() };

        update( TABLE_NAME, SETTER_COLUMNS, setValues, CONSTRAINT_COLUMNS, constraintValues );
    }

    @Override
    public void retrieve() throws java.sql.SQLException {
        Object[] constraintValues = { getApplianceCategoryID() };

        Object[] results = retrieve( SETTER_COLUMNS, TABLE_NAME, CONSTRAINT_COLUMNS, constraintValues );

        if (results.length == SETTER_COLUMNS.length) {
            setDescription( (String) results[0] );
            setCategoryID( (Integer) results[1] );
            setWebConfigurationID( (Integer) results[2] );
            consumerSelectable = "y".equalsIgnoreCase((String) results[3]);
        }
        else
            throw new Error(getClass() + " - Incorrect number of results retrieved");
    }

    private Integer getNextCategoryID() {
        Integer nextValueId = YukonSpringHook.getNextValueHelper().getNextValue(TABLE_NAME);
        return nextValueId;
    }
    
    public static List<ApplianceCategory> getAllApplianceCategories(Integer energyCompanyID) {
        YukonJdbcTemplate yukonJdbcTemplate = YukonSpringHook.getBean(YukonJdbcTemplate.class);

        SqlStatementBuilder sql = new SqlStatementBuilder();
    	sql.append("SELECT * FROM ApplianceCategory");

    	SqlStatementBuilder inClause = new SqlStatementBuilder();
    	inClause.append("SELECT ectgm.ItemId FROM EcToGenericMapping ectgm");
    	inClause.append("WHERE ectgm.MappingCategory").eq_k(EcMappingCategory.APPLIANCE_CATEGORY);
    	inClause.append("AND ectgm.EnergyCompanyId").eq(energyCompanyID);
    	
    	sql.append("WHERE ApplianceCategoryId").in(inClause);
    	sql.append("ORDER BY ApplianceCategoryID");
    	
    	List<ApplianceCategory> retList = yukonJdbcTemplate.query(sql, new YukonRowMapper<ApplianceCategory>() {

            @Override
            public ApplianceCategory mapRow(YukonResultSet rs) throws SQLException {
                ApplianceCategory applianceCategory = new ApplianceCategory();
                applianceCategory.setApplianceCategoryID(rs.getInt("ApplianceCategoryId"));
                applianceCategory.setDescription(rs.getString("Description"));
                applianceCategory.setCategoryID(rs.getInt("CategoryId"));
                applianceCategory.setWebConfigurationID(rs.getInt("WebConfigurationId"));
                applianceCategory.setConsumerSelectable(rs.getYNBoolean("ConsumerSelectable").getBoolean());
                return applianceCategory;
            }
        });
    	return retList;
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

    public boolean getConsumerSelectable() {
        return consumerSelectable;
    }

    public void setConsumerSelectable(boolean consumerSelectable) {
        this.consumerSelectable = consumerSelectable;
    }
}