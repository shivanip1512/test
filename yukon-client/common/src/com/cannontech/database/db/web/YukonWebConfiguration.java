package com.cannontech.database.db.web;

import com.cannontech.database.db.DBPersistent;

/**
 * <p>Title: CustomerWebConfiguration.java</p>
 * <p>Description: </p>
 * <p>Generation Time: Aug 12, 2002 1:00:39 PM</p>
 * <p>Company: Cannon Technologies Inc.</p>
 * @author yao
 * @version 1.0
 */
public class YukonWebConfiguration extends DBPersistent {

    public static final int NONE_INT = 0;

    private Integer configurationID = null;
    private String logoLocation = "";
    private String description = "";
    private String alternateDisplayName = "";
    private String url = "";

    public static final String[] CONSTRAINT_COLUMNS = { "ConfigurationID" };

    public static final String[] SETTER_COLUMNS = {
    	"LogoLocation", "Description", "AlternateDisplayName", "URL"
    };

    public static final String TABLE_NAME = "CustomerWebConfiguration";

    public static final String GET_NEXT_CONFIG_ID_SQL =
        "SELECT MAX(ConfigurationID) FROM " + TABLE_NAME;

    public void delete() throws java.sql.SQLException {
        Object[] constraintValues = { getConfigurationID() };

        delete( TABLE_NAME, CONSTRAINT_COLUMNS, constraintValues );
    }

    public void add() throws java.sql.SQLException {
    	if (getConfigurationID() == null)
    		setConfigurationID( getNextConfigID() );
    		
        Object[] addValues = {
            getConfigurationID(), getLogoLocation(), getDescription(), getAlternateDisplayName(), getURL()
        };

        add( TABLE_NAME, addValues );
    }

    public void update() throws java.sql.SQLException {
        Object[] setValues = {
            getLogoLocation(), getDescription(), getAlternateDisplayName(), getURL()
        };

        Object[] constraintValues = { getConfigurationID() };

        update( TABLE_NAME, SETTER_COLUMNS, setValues, CONSTRAINT_COLUMNS, constraintValues );
    }

    public void retrieve() throws java.sql.SQLException {
        Object[] constraintValues = { getConfigurationID() };

        Object[] results = retrieve( SETTER_COLUMNS, TABLE_NAME, CONSTRAINT_COLUMNS, constraintValues );

        if (results.length == SETTER_COLUMNS.length) {
        	setLogoLocation( (String) results[0] );
        	setDescription( (String) results[1] );
        	setAlternateDisplayName( (String) results[2] );
        	setURL( (String) results[3] );
        }
        else
            throw new Error(getClass() + " - Incorrect number of results retrieved");
    }

    public final Integer getNextConfigID() {
        java.sql.PreparedStatement pstmt = null;
        java.sql.ResultSet rset = null;

        int nextConfigID = 1;

        try {
            pstmt = getDbConnection().prepareStatement( GET_NEXT_CONFIG_ID_SQL );
            rset = pstmt.executeQuery();

            if (rset.next())
                nextConfigID = rset.getInt(1) + 1;
        }
        catch (java.sql.SQLException e) {
            e.printStackTrace();
        }
        finally {
            try {
                if (rset != null) rset.close();
                if (pstmt != null) pstmt.close();
            }
            catch (java.sql.SQLException e2) {
                e2.printStackTrace();
            }
        }

        return new Integer( nextConfigID );
    }
    
    public static YukonWebConfiguration[] getAllCustomerWebConfigurations() {
    	String sql = "SELECT * FROM " + TABLE_NAME;
    	
    	com.cannontech.database.SqlStatement stmt = new com.cannontech.database.SqlStatement(
    			sql, com.cannontech.common.util.CtiUtilities.getDatabaseAlias() );
    			
    	try {
    		stmt.execute();
    		YukonWebConfiguration[] webConfigs = new YukonWebConfiguration[ stmt.getRowCount() ];
    		
    		for (int i = 0; i < stmt.getRowCount(); i++) {
    			Object[] row = stmt.getRow(i);
    			webConfigs[i] = new YukonWebConfiguration();
    			
    			webConfigs[i].setConfigurationID( new Integer(((java.math.BigDecimal) row[0]).intValue()) );
    			webConfigs[i].setLogoLocation( (String) row[1] );
    			webConfigs[i].setDescription( (String) row[2] );
    			webConfigs[i].setAlternateDisplayName( (String) row[3] );
    			webConfigs[i].setURL( (String) row[4] );
    		}
    		
    		return webConfigs;
    	}
    	catch (Exception e) {
    		e.printStackTrace();
    	}
    	
    	return null;
    }

	/**
	 * Returns the alternateDisplayName.
	 * @return String
	 */
	public String getAlternateDisplayName() {
		return alternateDisplayName;
	}

	/**
	 * Returns the configurationID.
	 * @return Integer
	 */
	public Integer getConfigurationID() {
		return configurationID;
	}

	/**
	 * Returns the description.
	 * @return String
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * Returns the logoLocation.
	 * @return String
	 */
	public String getLogoLocation() {
		return logoLocation;
	}

	/**
	 * Returns the url.
	 * @return String
	 */
	public String getURL() {
		return url;
	}

	/**
	 * Sets the alternateDisplayName.
	 * @param alternateDisplayName The alternateDisplayName to set
	 */
	public void setAlternateDisplayName(String alternateDisplayName) {
		this.alternateDisplayName = alternateDisplayName;
	}

	/**
	 * Sets the configurationID.
	 * @param configurationID The configurationID to set
	 */
	public void setConfigurationID(Integer configurationID) {
		this.configurationID = configurationID;
	}

	/**
	 * Sets the description.
	 * @param description The description to set
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * Sets the logoLocation.
	 * @param logoLocation The logoLocation to set
	 */
	public void setLogoLocation(String logoLocation) {
		this.logoLocation = logoLocation;
	}

	/**
	 * Sets the url.
	 * @param url The url to set
	 */
	public void setURL(String url) {
		this.url = url;
	}

}
