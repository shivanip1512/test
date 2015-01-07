package com.cannontech.database.db.company;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.RowMapper;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.common.util.SqlStringStatementBuilder;
import com.cannontech.database.YukonJdbcTemplate;
import com.cannontech.database.db.DBPersistent;
import com.cannontech.database.incrementer.NextValueHelper;
import com.cannontech.spring.YukonSpringHook;

public class EnergyCompany extends DBPersistent {
    
    public static final int DEFAULT_ENERGY_COMPANY_ID = -1;
    private static YukonJdbcTemplate jdbcTemplate = YukonSpringHook.getBean(YukonJdbcTemplate.class);
    private Integer energyCompanyId;
    private String name;
    private Integer primaryContactId = CtiUtilities.NONE_ZERO_ID;
    private Integer userId = CtiUtilities.NONE_ZERO_ID;
    private Integer parentEnergyCompanyId;
    
	public static final String[] SETTER_COLUMNS = { "Name", "PrimaryContactID", "UserID", "ParentEnergyCompanyId" };
	public static final String[] CONSTRAINT_COLUMNS = { "EnergyCompanyID" };
	public static final String TABLE_NAME = "EnergyCompany";

    public EnergyCompany() {
    	super();
    }
    
    @Override
    public void retrieve() throws SQLException {
        Object[] constraintValues =  { getEnergyCompanyId() };
    
        Object[] results = retrieve(SETTER_COLUMNS, TABLE_NAME, CONSTRAINT_COLUMNS, constraintValues );
    
        if( results.length == SETTER_COLUMNS.length ) {
            setName( (String) results[0] );
            setPrimaryContactId( (Integer)results[1] );
            setUserId((Integer)results[2]);
        } else {
            throw new RuntimeException("Incorrect number of columns in result");
        }
        
    }
    
    @Override
    public void add() throws SQLException {
    	Object[] addValues = { 
    		getEnergyCompanyId(),
    		getName(),
    		getPrimaryContactId(),
    		getUserId(),
    		getParentEnergyCompanyId()
    	};
    
    	//if any of the values are null, return
    	if(!isValidValues(addValues)) {
    		return;
    	}
    	
    	add(TABLE_NAME, addValues);
    }
    
    @Override
    public void update() throws SQLException {
        Object[] setValues = {
            getName(),
            getPrimaryContactId(),
            getUserId(),
            getParentEnergyCompanyId()
        };
    
        //if any of the values are null, return
        if( !isValidValues(setValues) ) {
            return;
        }
        
        Object[] constraintValues =  { getEnergyCompanyId() };
        update( TABLE_NAME, SETTER_COLUMNS, setValues, CONSTRAINT_COLUMNS, constraintValues );
    }
    
    @Override
    public void delete() throws SQLException {
    	delete(TABLE_NAME, CONSTRAINT_COLUMNS[0], getEnergyCompanyId());
    }
    
    /* Utility Methods */
    
    public static final Integer getNextEnergyCompanyID() {
        NextValueHelper nextValueHelper = YukonSpringHook.getNextValueHelper();
        return nextValueHelper.getNextValue("EnergyCompany");
    }
    
    public static long[] getAllEnergyCompanyIDs() {
        try {
            SqlStringStatementBuilder sql = new SqlStringStatementBuilder();
            sql.append("SELECT EnergyCompanyId");
            sql.append("FROM EnergyCompany");
            
            RowMapper<Long> longMapper = new RowMapper<Long>() {
                @Override
                public Long mapRow(ResultSet rs, int rowNum) throws SQLException {
                    return rs.getLong("EnergyCompanyId");
                }
            };
            final List<Long> list = jdbcTemplate.query(sql.toString(), longMapper);
            
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
    
    public static final EnergyCompany[] getEnergyCompanies() {
    	try {
    	    SqlStringStatementBuilder sql = new SqlStringStatementBuilder();
            sql.append("SELECT EnergyCompanyId, Name, PrimaryContactId, UserId, ParentEnergyCompanyId");
            sql.append("FROM EnergyCompany");
            
            RowMapper<EnergyCompany> energyCompanyRowMapper = new RowMapper<EnergyCompany>() {
                @Override
                public EnergyCompany mapRow(ResultSet rs, int rowNum) throws SQLException {
                    final EnergyCompany company = new EnergyCompany();
                    company.setEnergyCompanyId(rs.getInt("EnergyCompanyId"));
                    company.setName(rs.getString("Name"));
                    company.setPrimaryContactId(rs.getInt("PrimaryContactId"));
                    company.setUserId(rs.getInt("UserId"));
                    company.setParentEnergyCompanyId(rs.getInt("ParentEnergyCompanyId"));
                    return company;
                }
            };
    	    List<EnergyCompany> list = jdbcTemplate.query(sql.toString(), energyCompanyRowMapper);
            
            return list.toArray(new EnergyCompany[list.size()]);
    	} catch (DataAccessException e) {
    	    CTILogger.error(e.getMessage(), e);
    	}
        return new EnergyCompany[0];
    }
    
    private boolean isValidValues( Object[] values ) {
    	if( values == null ) {
            return false;
        }
    
    	for( int i = 0; i < values.length-1; i++ ) {
            if( values[i] == null ) {
                return false;
            }
        }
    
    
    	return true;
    }
    
    /* Getters and Setters */
    
    public String getName() {
        return name;
    }
    
    public void setName(String newName) {
    	name = newName;
    }
    
    /**
     * @deprecated user getEnergyCompanyId()
     */
    @Deprecated
    public Integer getEnergyCompanyID() {
        return energyCompanyId;
    }
    
    /**
     * @deprecated use setEnergyCompanyId()
     */
    @Deprecated
    public void setEnergyCompanyID(Integer newEnergyCompanyID) {
        energyCompanyId = newEnergyCompanyID;
    }
    
    public Integer getEnergyCompanyId() {
        return energyCompanyId;
    }
    
    public void setEnergyCompanyId(Integer energyCompanyId) {
        this.energyCompanyId = energyCompanyId;
    }
    
    /**
     * @deprecated use getPrimaryContactId
     */
    @Deprecated
	public Integer getPrimaryContactID() {
		return primaryContactId;
	}
    
    public Integer getPrimaryContactId() {
        return primaryContactId;
    }
    
    /**
     * @deprecated use setPrimaryContactId
     */
    @Deprecated
	public void setPrimaryContactID(Integer primaryContactID) {
		this.primaryContactId = primaryContactID;
	}
	
	public void setPrimaryContactId(Integer primaryContactId) {
	    this.primaryContactId = primaryContactId;
	}
	
	/**
	 * @deprecated use getUserId
	 */
	@Deprecated
    public Integer getUserID() {
	    return userId;
	}
	
	public Integer getUserId() {
	    return userId;
	}
	
	/**
	 * @deprecated use setUserId
	 */
	@Deprecated
    public void setUserID(Integer userId) {
	    this.userId = userId;
	}

	public void setUserId(Integer userId) {
		this.userId = userId;
	}
	
	public Integer getParentEnergyCompanyId() {
		return parentEnergyCompanyId;
	}

	public void setParentEnergyCompanyId(Integer parentEnergyCompanyId) {
		this.parentEnergyCompanyId = parentEnergyCompanyId;
	}

    @Override
    public String toString() {
        return getName();
    }
    
    @Override
    public boolean equals(Object o) {
        if( o instanceof EnergyCompany ) {
            return ((EnergyCompany)o).getEnergyCompanyId().equals( getEnergyCompanyId() )
                ? true 
                : false;
        } else {
            return false;
        }
    }

}