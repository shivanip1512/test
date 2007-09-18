package com.cannontech.database.db.company;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.simple.ParameterizedRowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.spring.YukonSpringHook;

/**
 * Creation date: (10/18/2001 1:20:37 PM)
 */
public class EnergyCompany extends com.cannontech.database.db.DBPersistent 
{
    public static final int DEFAULT_ENERGY_COMPANY_ID = -1;
    private static final String selectIdSql;
    private static final String selectEnergyCompanySql;
    private static final String nextIdSql;
    private static final SimpleJdbcTemplate simpleJdbcTemplate = YukonSpringHook.getBean("simpleJdbcTemplate", SimpleJdbcTemplate.class);
    private Integer energyCompanyID;
    private String name;
    private Integer primaryContactID = Integer.valueOf(CtiUtilities.NONE_ZERO_ID);
    private Integer userID = Integer.valueOf(CtiUtilities.NONE_ZERO_ID);
    
	public static final String[] SETTER_COLUMNS = { "Name", "PrimaryContactID", "UserID" };
	
	public static final String[] CONSTRAINT_COLUMNS = { "EnergyCompanyID" };
	
	public static final String TABLE_NAME = "EnergyCompany";

    static {
        
        selectIdSql = "SELECT EnergyCompanyID FROM EnergyCompany";

        selectEnergyCompanySql = "SELECT EnergyCompanyID,Name,PrimaryContactID,UserID FROM EnergyCompany";
        
        nextIdSql = "SELECT MAX(EnergyCompanyID)+1 FROM EnergyCompany";
    }
	
/**
 * EnergyCompany constructor comment.
 */
public EnergyCompany() {
	super();
}
/**
 * @exception java.sql.SQLException The exception description.
 */
@Override
public void add() throws java.sql.SQLException 
{
	if (getEnergyCompanyID() == null)
		setEnergyCompanyID( getNextEnergyCompanyID() );
	
	Object[] addValues = 
	{ 
		getEnergyCompanyID(),
		getName(),
		getPrimaryContactID(),
		getUserID()
	};

	//if any of the values are null, return
	if( !isValidValues(addValues) )
		return;
	
	add( TABLE_NAME, addValues );
}
/**
 */
@Override
public void delete() throws java.sql.SQLException 
{
	delete( TABLE_NAME, CONSTRAINT_COLUMNS[0], getEnergyCompanyID() );
}
/**
 * Insert the method's description here.
 * Creation date: (11/17/00 4:28:38 PM)
 * @return java.lang.String
 */
@Override
public boolean equals(Object o)
{
	if( o instanceof EnergyCompany )	
		return ((EnergyCompany)o).getEnergyCompanyID().equals( getEnergyCompanyID() )
			? true 
			: false;
	else
		return false;
}

/**
 * Creation date: (6/11/2001 3:38:14 PM)
 * @return com.cannontech.database.db.web.EnergyCompany
 * @param dbAlias java.lang.String
 */
public static long[] getAllEnergyCompanyIDs() {
    try {
        final List<Long> list = simpleJdbcTemplate.query(selectIdSql, new ParameterizedRowMapper<Long>() {
            public Long mapRow(ResultSet rs, int rowNum) throws SQLException {
                Long id = Long.valueOf(rs.getLong("EnergyCompanyID"));
                return id;
            }
        }, new Object[]{});
        
        final long[] idArray = new long[list.size()];
        for (int x = 0; x < list.size(); x++) {
            Long id = list.get(x);
            idArray[x] = id.longValue();
        }
        return idArray;
        
    } catch (DataAccessException e) {
        CTILogger.error(e.getMessage(), e);
    }
    return new long[0];
}

/**
 * Insert the method's description here.
 * Creation date: (8/24/2001 12:52:15 PM)
 * @ret int
 */
public static final EnergyCompany[] getEnergyCompanies() {
	try {
	    List<EnergyCompany> list = simpleJdbcTemplate.query(selectEnergyCompanySql, new ParameterizedRowMapper<EnergyCompany>() {
	        public EnergyCompany mapRow(ResultSet rs, int rowNum) throws SQLException {
	            final EnergyCompany company = new EnergyCompany();
	            company.setEnergyCompanyID(rs.getInt("EnergyCompanyID"));
	            company.setName(rs.getString("Name"));
	            company.setPrimaryContactID(rs.getInt("PrimaryContactID"));
	            company.setUserID(rs.getInt("UserID"));
	            return company;
	        }
	    }, new Object[]{});
        
        return list.toArray(new EnergyCompany[list.size()]);
	} catch (DataAccessException e) {
	    CTILogger.error(e.getMessage(), e);
	}
    return new EnergyCompany[0];
}

/**
 * Insert the method's description here.
 * Creation date: (2/28/2002 10:21:44 AM)
 * @return java.lang.Integer
 */
public java.lang.Integer getEnergyCompanyID() {
	return energyCompanyID;
}
/**
 * Insert the method's description here.
 * Creation date: (2/28/2002 10:21:44 AM)
 * @return java.lang.String
 */
public java.lang.String getName() {
	return name;
}
/**
 * This method was created in VisualAge.
 * @return java.lang.Integer
 */
public static final Integer getNextEnergyCompanyID() {
    Integer id = null;
    try {
        int nextId = simpleJdbcTemplate.queryForInt(nextIdSql, new Object[]{});
        return Integer.valueOf(nextId);
    } catch (DataAccessException e) {
        CTILogger.error(e.getMessage(), e);
    }
    return id;
}
/**
 * Insert the method's description here.
 * Creation date: (9/27/2001 10:30:24 AM)
 * @return boolean
 */
private boolean isValidValues( Object[] values ) 
{
	if( values == null )
		return false;

	for( int i = 0; i < values.length; i++ )
		if( values[i] == null )
			return false;


	return true;
}
/**
 */
public void retrieve() throws java.sql.SQLException 
{
	Object[] constraintValues =  { getEnergyCompanyID() };

	Object[] results = retrieve(SETTER_COLUMNS, TABLE_NAME, CONSTRAINT_COLUMNS, constraintValues );

	if( results.length == SETTER_COLUMNS.length )
	{
		setName( (String) results[0] );
		setPrimaryContactID( (Integer)results[1] );
		setUserID((Integer)results[2]);
	}
	else
		throw new RuntimeException("Incorrect number of columns in result");
	
}
/**
 * Insert the method's description here.
 * Creation date: (2/28/2002 10:21:44 AM)
 * @param newEnergyCompanyID java.lang.Integer
 */
public void setEnergyCompanyID(java.lang.Integer newEnergyCompanyID) {
	energyCompanyID = newEnergyCompanyID;
}
/**
 * Insert the method's description here.
 * Creation date: (2/28/2002 10:21:44 AM)
 * @param newName java.lang.String
 */
public void setName(java.lang.String newName) {
	name = newName;
}
/**
 * toString() override
 */
public String toString()
{
	return getName();
}
/**
 * @exception java.sql.SQLException The exception description.
 */
public void update() throws java.sql.SQLException 
{
	Object[] setValues = 
	{
		getName(),
		getPrimaryContactID(),
		getUserID()
	};

	//if any of the values are null, return
	if( !isValidValues(setValues) )
		return;

	
	Object[] constraintValues =  { getEnergyCompanyID() };

	Object[] results = retrieve(SETTER_COLUMNS, TABLE_NAME, CONSTRAINT_COLUMNS, constraintValues );

	update( TABLE_NAME, SETTER_COLUMNS, setValues, CONSTRAINT_COLUMNS, constraintValues );
}
	/**
	 * Returns the primaryContactID.
	 * @return Integer
	 */
	public Integer getPrimaryContactID() {
		return primaryContactID;
	}

	/**
	 * Sets the primaryContactID.
	 * @param primaryContactID The primaryContactID to set
	 */
	public void setPrimaryContactID(Integer primaryContactID) {
		this.primaryContactID = primaryContactID;
	}

	/**
	 * @return
	 */
	public Integer getUserID() {
		return userID;
	}

	/**
	 * @param integer
	 */
	public void setUserID(Integer integer) {
		userID = integer;
	}

}
