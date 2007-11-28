package com.cannontech.core.dao.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.TimeZone;

import org.springframework.dao.DataRetrievalFailureException;
import org.springframework.jdbc.core.simple.ParameterizedRowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;

import com.cannontech.core.authentication.service.AuthType;
import com.cannontech.core.dao.EnergyCompanyDao;
import com.cannontech.core.dao.YukonUserDao;
import com.cannontech.database.data.lite.LiteContact;
import com.cannontech.database.data.lite.LiteEnergyCompany;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.yukon.IDatabaseCache;

/**
 * @author rneuharth
 * Oct 16, 2002 at 2:12:21 PM
 * 
 */
public final class YukonUserDaoImpl implements YukonUserDao {
    private static final String selectSql;
    private static final String selectByIdSql;
    private static final String selectByUsernameSql;
    private static final ParameterizedRowMapper<LiteYukonUser> rowMapper;
    private SimpleJdbcTemplate simpleJdbcTemplate;
    private IDatabaseCache databaseCache;
    private EnergyCompanyDao energyCompanyDao = null;
    
    static {
        
        selectSql = "SELECT UserID,UserName,Status,AuthType FROM YukonUser";
        
        selectByIdSql = selectSql + " WHERE UserID = ?";
        
        selectByUsernameSql = selectSql + " WHERE UserName = ?";
        
        rowMapper = createRowMapper();
    }
    
	public YukonUserDaoImpl() {
	
	}

	public LiteYukonUser getLiteYukonUser(final int userId) {
	    try {
	        LiteYukonUser user = simpleJdbcTemplate.queryForObject(selectByIdSql, rowMapper, userId);
	        return user;
	    } catch (DataRetrievalFailureException e) {
            return null;
        }
	}

	public LiteYukonUser getLiteYukonUser(final String userName) {
		try {
		    LiteYukonUser user = simpleJdbcTemplate.queryForObject(selectByUsernameSql, rowMapper, userName);
		    return user;
		} catch (DataRetrievalFailureException e) {
		    return null;
		}
	}
		
	public LiteContact getLiteContact(final int userId) {
	    synchronized(databaseCache) {
	        return databaseCache.getAContactByUserID(userId);
	    }
	}
    
    public void setDatabaseCache(IDatabaseCache databaseCache) {
        this.databaseCache = databaseCache;
    }

    public void setEnergyCompanyDao(EnergyCompanyDao energyCompanyDao) {
        this.energyCompanyDao = energyCompanyDao;
    }

    public TimeZone getUserTimeZone(LiteYukonUser user) {
        LiteEnergyCompany energyCompany = energyCompanyDao.getEnergyCompany(user);
        TimeZone timeZone = energyCompanyDao.getEnergyCompanyTimeZone(energyCompany);
        return timeZone;
    }

    private static ParameterizedRowMapper<LiteYukonUser> createRowMapper() {
        final ParameterizedRowMapper<LiteYukonUser> mapper = new ParameterizedRowMapper<LiteYukonUser>() {
            @Override
            public LiteYukonUser mapRow(ResultSet rs, int rowNum) throws SQLException {
                LiteYukonUser user = new LiteYukonUser();
                user.setUserID(rs.getInt("UserID"));
                user.setUsername(rs.getString("UserName"));
                user.setAuthType(AuthType.valueOf(rs.getString("AuthType")));
                user.setStatus(rs.getString("Status"));
                return user;
            }
        };
        return mapper;
    }

    public void setSimpleJdbcTemplate(SimpleJdbcTemplate simpleJdbcTemplate) {
        this.simpleJdbcTemplate = simpleJdbcTemplate;
    }

}
