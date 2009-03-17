package com.cannontech.core.dao.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataRetrievalFailureException;
import org.springframework.jdbc.core.simple.ParameterizedRowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;
import org.springframework.transaction.annotation.Transactional;

import com.cannontech.common.exception.NotAuthorizedException;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.core.authentication.service.AuthType;
import com.cannontech.core.authorization.dao.PaoPermissionDao;
import com.cannontech.core.dao.DBPersistentDao;
import com.cannontech.core.dao.YukonUserDao;
import com.cannontech.database.SqlUtils;
import com.cannontech.database.data.lite.LiteContact;
import com.cannontech.database.data.lite.LiteYukonGroup;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.database.db.user.YukonUser;
import com.cannontech.database.incrementer.NextValueHelper;
import com.cannontech.message.dispatch.message.DBChangeMsg;
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
    private NextValueHelper nextValueHelper;
    private PaoPermissionDao<LiteYukonUser> userPaoPermissionDao = null;
    private DBPersistentDao dbPersistantDao;    

    static {
        
        selectSql = "SELECT UserID,UserName,Status,AuthType FROM YukonUser";
        
        selectByIdSql = selectSql + " WHERE UserID = ?";
        
        selectByUsernameSql = selectSql + " WHERE UserName = ?";
        
        rowMapper = createRowMapper();
    }
    
	public YukonUserDaoImpl() {
	
	}
	
	@Override
	public void changeUsername(final int userId, final String username) throws NotAuthorizedException {
	    LiteYukonUser existingUser = getLiteYukonUser(username); 
	    if (existingUser != null) {
	        throw new NotAuthorizedException("Username with " + username + " already exists");
	    }
	    
	    final LiteYukonUser user = getLiteYukonUser(userId);
	    user.setUsername(username);
	    
	    final String sql = "UPDATE YukonUser SET UserName = ? WHERE UserID = ?";
	    simpleJdbcTemplate.update(sql, username, userId);
	    
	    synchronized (databaseCache) {
	        databaseCache.getAllUsersMap().put(userId, user);
        }
	}
	
	@Override
	@Transactional
	public void addLiteYukonUserWithPassword(LiteYukonUser user, String password, int energyCompanyId, List<LiteYukonGroup> groups) throws DataAccessException {
	    int userId = nextValueHelper.getNextValue(YukonUser.TABLE_NAME);
	    user.setUserID(userId);
	    SqlStatementBuilder sql = new SqlStatementBuilder();
	    sql.append("INSERT INTO YukonUser VALUES (?,?,?,?,?)");
	    simpleJdbcTemplate.update(sql.toString(), user.getUserID(), user.getUsername(), SqlUtils.convertStringToDbValue(password), user.getStatus(), user.getAuthType().name());
	    
	    for(LiteYukonGroup group : groups) {
	        sql = new SqlStatementBuilder();
    	    sql.append("INSERT INTO YukonUserGroup VALUES (");
    	    sql.append(user.getUserID()); 
    	    sql.append(",");
    	    sql.append(group.getGroupID());
    	    sql.append(")");
    	    
    	    simpleJdbcTemplate.update(sql.toString());
	    }
	    
	    if( energyCompanyId > -2 ) { // default energy company starts at -1 
	        sql = new SqlStatementBuilder();
	        sql.append("INSERT INTO EnergyCompanyOperatorLoginList VALUES (");
	        sql.append(energyCompanyId);
	        sql.append(", ");
	        sql.append(user.getUserID());
	        sql.append(")");
	        simpleJdbcTemplate.update(sql.toString());
	    }
	}
	
	@Override
    @Transactional
	public void update(LiteYukonUser user) {
	    final String sql = "update yukonuser set username = ?, status = ?, AuthType = ? where userid = ?";
        simpleJdbcTemplate.update(sql, user.getUsername(), user.getStatus(), user.getAuthType().name(), user.getUserID());
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
	
	public String generateUsername(String firstName, String lastName) {
        String newUsername = null;

        String firstInitial = "";
        if (firstName != null) {
            firstInitial = firstName.toLowerCase().substring(0, 1);
        }
        newUsername = firstInitial + lastName.toLowerCase();
        if (getLiteYukonUser(newUsername) != null) {
            String timeStamp = Long.toString(new Date().getTime());
            StringBuilder username = new StringBuilder(lastName.toLowerCase()).append(timeStamp);
            boolean uniqueUsername = false;
            for (int i = 0; i < 100; i++) {
                newUsername = username.append(i).toString();
                if (getLiteYukonUser(newUsername) == null) {
                    uniqueUsername = true;
                    break;
                }
            }
            if (!uniqueUsername) {
                throw new RuntimeException("Failed to generate unique username, please retry");
            }
        }

        return newUsername;
    }
	
	@Override
	@Transactional
    public void deleteUser(Integer userId) {
        String deleteUserRole = "DELETE FROM YukonUserRole WHERE UserId = ?";
        String deleteYukonUserGroup = "DELETE FROM YukonUserGroup WHERE UserId = ?";
        String deleteEnergyCompanyOperatorLoginList = "DELETE FROM EnergyCompanyOperatorLoginList WHERE OperatorLoginId = ?";
        String deleteYukonUser = "DELETE FROM YukonUser WHERE UserId = ?";
        simpleJdbcTemplate.update(deleteUserRole, userId);
        simpleJdbcTemplate.update(deleteYukonUserGroup, userId);
        simpleJdbcTemplate.update(deleteEnergyCompanyOperatorLoginList, userId);
        userPaoPermissionDao.removeAllPermissions(userId);
        simpleJdbcTemplate.update(deleteYukonUser, userId);
        
        DBChangeMsg changeMsg = new DBChangeMsg(userId,
                                                DBChangeMsg.CHANGE_YUKON_USER_DB,
                                                DBChangeMsg.CAT_YUKON_USER,
                                                DBChangeMsg.CAT_YUKON_USER,
                                                DBChangeMsg.CHANGE_TYPE_DELETE);
        dbPersistantDao.processDBChange(changeMsg);        
	}
    
    public void setDatabaseCache(IDatabaseCache databaseCache) {
        this.databaseCache = databaseCache;
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
    
    @Autowired
    public void setNextValueHelper(NextValueHelper nextValueHelper) {
        this.nextValueHelper = nextValueHelper;
    }

    public void setUserPaoPermissionDao(PaoPermissionDao<LiteYukonUser> userPaoPermissionDao) {
        this.userPaoPermissionDao = userPaoPermissionDao;
    }
    
    @Autowired
    public void setDbPersistantDao(DBPersistentDao dbPersistantDao) {
        this.dbPersistantDao = dbPersistantDao;
    }    
}
