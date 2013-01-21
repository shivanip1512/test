package com.cannontech.core.dao.impl;

import java.sql.SQLException;
import java.util.List;

import org.apache.commons.lang.RandomStringUtils;
import org.joda.time.Instant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.transaction.annotation.Transactional;

import com.cannontech.common.events.loggers.SystemEventLogService;
import com.cannontech.common.exception.NotAuthorizedException;
import com.cannontech.common.search.SearchResult;
import com.cannontech.common.user.UserAuthenticationInfo;
import com.cannontech.common.util.SimpleCallback;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.common.util.SqlStringStatementBuilder;
import com.cannontech.core.authentication.model.AuthType;
import com.cannontech.core.authorization.dao.PaoPermissionDao;
import com.cannontech.core.dao.DBPersistentDao;
import com.cannontech.core.dao.YukonUserDao;
import com.cannontech.core.users.model.LiteUserGroup;
import com.cannontech.database.PagingResultSetExtractor;
import com.cannontech.database.SimpleTableAccessTemplate;
import com.cannontech.database.TransactionType;
import com.cannontech.database.YNBoolean;
import com.cannontech.database.YukonJdbcTemplate;
import com.cannontech.database.YukonMappingRowCallbackHandler;
import com.cannontech.database.YukonResultSet;
import com.cannontech.database.YukonRowMapper;
import com.cannontech.database.data.lite.LiteContact;
import com.cannontech.database.data.lite.LiteYukonGroup;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.database.data.user.YukonUser;
import com.cannontech.database.db.user.UserGroup;
import com.cannontech.database.incrementer.NextValueHelper;
import com.cannontech.message.DbChangeManager;
import com.cannontech.message.dispatch.message.DBChangeMsg;
import com.cannontech.message.dispatch.message.DbChangeType;
import com.cannontech.user.UserUtils;
import com.cannontech.yukon.IDatabaseCache;

public class YukonUserDaoImpl implements YukonUserDao {

    @Autowired private DBPersistentDao dbPersistantDao;
    @Autowired private DbChangeManager dbChangeManager;
    @Autowired private NextValueHelper nextValueHelper;
    @Autowired private SystemEventLogService systemEventLogService;
    @Autowired private YukonJdbcTemplate yukonJdbcTemplate;
    @Autowired private PaoPermissionDao<LiteYukonUser> userPaoPermissionDao;
    @Autowired private IDatabaseCache databaseCache;

    public static final int numberOfRandomChars = 5;
    
	@Override
	public void changeUsername(LiteYukonUser changingUser, int modifiedUserId, String newUsername) 
	throws NotAuthorizedException {
	    
	    LiteYukonUser existingUser = findUserByUsername(newUsername); 
	    if (existingUser != null) {
	        throw new NotAuthorizedException("Username with " + newUsername + " already exists");
	    }
	    
	    final LiteYukonUser modifiedUser = getLiteYukonUser(modifiedUserId);
	    modifiedUser.setUsername(newUsername);
	    
	    final String sql = "UPDATE YukonUser SET UserName = ? WHERE UserID = ?";
	    yukonJdbcTemplate.update(sql, newUsername, modifiedUserId);

	    systemEventLogService.usernameChanged(changingUser, modifiedUser.getUsername(), newUsername);
	    
	    dbChangeManager.processDbChange(modifiedUserId,
                                        DBChangeMsg.CHANGE_YUKON_USER_DB,
                                        DBChangeMsg.CAT_YUKON_USER,
                                        DBChangeMsg.CAT_YUKON_USER,
                                        DbChangeType.UPDATE);
	}
	
	@Override
	@Transactional
    public void save(LiteYukonUser user) {
	    boolean update = user.getUserID() != 0;

	    if (update) {
	        update(user);
	    } else {
	        int nextUserId = nextValueHelper.getNextValue("YukonUser");
	        user.setUserID(nextUserId);
	        
	        SqlStatementBuilder sql = new SqlStatementBuilder();
	        sql.append("INSERT INTO YukonUser (UserId, Username, Password, Status, AuthType, LastChangedDate, " +
	        		"ForceReset, UserGroupId)");
	        sql.values(user.getUserID(), user.getUsername(), " ", user.getLoginStatus(), AuthType.NONE, 
	                new Instant(), YNBoolean.valueOf(user.isForceReset()), user.getUserGroupId());
	        yukonJdbcTemplate.update(sql);
	    }

	    DbChangeType changeType = update ? DbChangeType.UPDATE : DbChangeType.ADD;
	    dbChangeManager.processDbChange(user.getUserID(),
                                        DBChangeMsg.CHANGE_YUKON_USER_DB,
                                        DBChangeMsg.CAT_YUKON_USER,
                                        DBChangeMsg.CAT_YUKON_USER,
                                        changeType);
    }
	
	@Override
    @Transactional
    public void update(LiteYukonUser user) {
	    SqlStatementBuilder sql = new SqlStatementBuilder();
	    sql.append("UPDATE YukonUser");
        sql.append("SET Username").eq(user.getUsername()).append(",");
        sql.append("    Status").eq_k(user.getLoginStatus()).append(",");
        sql.append("    ForceReset").eq(YNBoolean.valueOf(user.isForceReset())).append(",");
	    sql.append("    UserGroupId").eq(user.getUserGroupId());
	    sql.append("WHERE UserId").eq(user.getUserID());
	    
        yukonJdbcTemplate.update(sql);
    }
	
    @Override
    public void setUserStatus(LiteYukonUser user, LoginStatusEnum loginStatusEnum) {

        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("UPDATE YukonUser ");
        sql.append("SET Status").eq(loginStatusEnum);
        sql.append("WHERE UserId").eq(user.getUserID());
        
        yukonJdbcTemplate.update(sql);
        
    }    
	
    @Override
	public LiteYukonUser getLiteYukonUser(int userId) {
	    try {
	        SqlStatementBuilder sql = new SqlStatementBuilder();
	        sql.append("SELECT YU.*");
	        sql.append("FROM YukonUser YU");
	        sql.append("WHERE UserId").eq(userId);
	        
	        LiteYukonUser user = yukonJdbcTemplate.queryForObject(sql, new LiteYukonUserMapper());
	        return user;
	    } catch (EmptyResultDataAccessException e) {
            return null;
        }
	}

    private final static YukonRowMapper<UserAuthenticationInfo> userAuthenticationInfoMapper = new YukonRowMapper<UserAuthenticationInfo>() {
        @Override
        public UserAuthenticationInfo mapRow(YukonResultSet rs) throws SQLException {
            int userId = rs.getInt("UserId");
            AuthType authType = rs.getEnum("AuthType", AuthType.class);
            Instant lastChangedDate = rs.getInstant("LastChangedDate");
            return new UserAuthenticationInfo(userId, authType, lastChangedDate);
        }
    };

    @Override
    public UserAuthenticationInfo getUserAuthenticationInfo(int userId) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT UserId, AuthType, LastChangedDate FROM YukonUser WHERE UserId").eq(userId);

        UserAuthenticationInfo userAuthenticationInfo =
                yukonJdbcTemplate.queryForObject(sql, userAuthenticationInfoMapper);
        return userAuthenticationInfo;
    }

    @Override
	public LiteYukonUser findUserByUsername(String userName) {
		try {
            SqlStatementBuilder sql = new SqlStatementBuilder();
            sql.append("SELECT YU.*");
            sql.append("FROM YukonUser YU");
            sql.append("WHERE LOWER( YU.UserName ) = LOWER(").appendArgument(userName).append(")");
            
            LiteYukonUser user = yukonJdbcTemplate.queryForObject(sql, new LiteYukonUserMapper());
		    return user;
		} catch (EmptyResultDataAccessException e) {
		    return null;
		}
	}

	@Override
    public LiteContact getLiteContact(final int userId) {
		return databaseCache.getAContactByUserID(userId);
	}
	
	/**
	 * Generates a username from a first name and last name. This will attempt to return the
	 * first initial and last name. If the username exceeds the 64 character database limit 
	 * it will truncate the last name. If the username exists it will add the time stamp and 
	 * some extra numbers to attempt to find a unique username. 
	 * 
	 * @param String firstName
	 * @param String lastName
	 */
	@Override
    public String generateUsername(String firstName, String lastName) {
        String newUsername = null;

        String firstInitial = firstName.toLowerCase().substring(0, 1);
        
        // In the event of having a name more than the database limit of 64 characters. 
        // We will truncate the last name to 63 characters.
        if(lastName.length() > 63) {
        	newUsername = firstInitial + lastName.substring(0,63).toLowerCase();
        } else {
        	newUsername = firstInitial + lastName.toLowerCase();
        }
        
        //If the username is not unique, we will try once to generate a unique one.
        if (findUserByUsername(newUsername) != null) {
            String extraDigits = RandomStringUtils.randomAlphanumeric(numberOfRandomChars);
            String uniqueUsername;
            
            //If the extra digits will push this over 64, 
            //we will truncate part of the username to make room for it.
            if(newUsername.length() + numberOfRandomChars > 64  ) {
            	uniqueUsername = newUsername.substring(0, newUsername.length() - numberOfRandomChars);
            } else {
            	uniqueUsername = newUsername;
            }
            
            uniqueUsername += extraDigits;

            if (findUserByUsername(uniqueUsername) != null) {
                throw new RuntimeException("Failed to generate unique username, please retry");
            } else {
            	newUsername = uniqueUsername;
            }
        }

        return newUsername;
    }
	
	@Override
    public void removeUserFromEventBase(int userId) {
        SqlStatementBuilder zeroOutEventBaseUserIdsSql = new SqlStatementBuilder();
        zeroOutEventBaseUserIdsSql.append("UPDATE EventBase");
        zeroOutEventBaseUserIdsSql.append("SET UserId").eq(UserUtils.USER_DEFAULT_ID);
        zeroOutEventBaseUserIdsSql.append("WHERE UserId").eq(userId);
        yukonJdbcTemplate.update(zeroOutEventBaseUserIdsSql);
    }
	
	@Override
	@Transactional
    public void deleteUser(Integer userId) {
	    
	    // Remove existing contacts before removing username
	    SqlStatementBuilder detachContactFromLogin = new SqlStatementBuilder();
	    detachContactFromLogin.append("UPDATE Contact");
        detachContactFromLogin.append("SET LoginId").eq(UserUtils.USER_DEFAULT_ID);
        detachContactFromLogin.append("WHERE LoginId").eq(userId);
        yukonJdbcTemplate.update(detachContactFromLogin);
        
        String deleteEnergyCompanyOperatorLoginList = "DELETE FROM EnergyCompanyOperatorLoginList WHERE OperatorLoginId = ?";
        yukonJdbcTemplate.update(deleteEnergyCompanyOperatorLoginList, userId);
        userPaoPermissionDao.removeAllPermissions(userId);
        
        removeUserFromEventBase(userId);
        
        String deleteYukonUser = "DELETE FROM YukonUser WHERE UserId = ?";
        yukonJdbcTemplate.update(deleteYukonUser, userId);
        
        dbChangeManager.processDbChange(userId,
                                        DBChangeMsg.CHANGE_YUKON_USER_DB,
                                        DBChangeMsg.CAT_YUKON_USER,
                                        DBChangeMsg.CAT_YUKON_USER,
                                        DbChangeType.DELETE);       
	}

	@Override
	public int getAllYukonUserCount() {
	    String sql = "select count(*) from YukonUser";
	    int count = yukonJdbcTemplate.queryForInt(sql);
	    return count;
	}
	
	@Override
	public void callbackWithAllYukonUsers(SimpleCallback<LiteYukonUser> callback) {
	    SqlStatementBuilder sql = new SqlStatementBuilder();
	    sql.append("SELECT *");
	    sql.append("FROM YukonUser");
	    sql.append("ORDER BY Username");

	    yukonJdbcTemplate.query(sql, new YukonMappingRowCallbackHandler<LiteYukonUser>(new LiteYukonUserMapper(), callback));
	}
	
	@Override
	public void callbackWithYukonUsersInGroup(LiteYukonGroup group, SimpleCallback<LiteYukonUser> callback) {

	    SqlStatementBuilder sql = new SqlStatementBuilder();
	    sql.append("SELECT YU.*");
	    sql.append("FROM YukonUser YU");
	    sql.append("  JOIN UserGroupToYukonGroupMapping UGTYGM ON UGTYGM.UserGroupId = YU.UserGroupId");
	    sql.append("WHERE UGTYGM.GroupId").eq(group.getGroupID());
	    sql.append("ORDER BY YU.Username");

	    yukonJdbcTemplate.query(sql, new YukonMappingRowCallbackHandler<LiteYukonUser>(new LiteYukonUserMapper(), callback));
	}

	@Override
	public void callbackWithYukonUsersInUserGroup(UserGroup userGroup, SimpleCallback<LiteYukonUser> callback) {
	    
	    SqlStatementBuilder sql = new SqlStatementBuilder();
	    sql.append("SELECT YU.*");
	    sql.append("FROM YukonUser YU");
	    sql.append("WHERE YU.UserGroupId").eq(userGroup.getUserGroupId());
	    sql.append("ORDER BY YU.Username");
	    
	    yukonJdbcTemplate.query(sql, new YukonMappingRowCallbackHandler<LiteYukonUser>(new LiteYukonUserMapper(), callback));
	}

	@Override
    public void removeUserFromUserGroup(int userId) {
	    SqlStatementBuilder sql = new SqlStatementBuilder();
	    sql.append("UPDATE YukonUser");
	    sql.set("UserGroupId", null);
	    sql.append("WHERE UserId").eq(userId);
	    yukonJdbcTemplate.update(sql);
	    
	    dbChangeManager.processDbChange(userId,
                                        DBChangeMsg.CHANGE_YUKON_USER_DB,
                                        DBChangeMsg.CAT_YUKON_USER,
                                        DBChangeMsg.CAT_YUKON_USER,
                                        DbChangeType.UPDATE);
    }

    @Override
    public void addUserToGroup(int userId, Integer... groupIds) {
        
        for (int groupId : groupIds) {

            SqlStringStatementBuilder sql = new SqlStringStatementBuilder();
            sql.append("INSERT INTO YukonUserGroup (UserId, GroupId)");
            sql.append("VALUES (? , ?)");
            
            yukonJdbcTemplate.update(sql.toString(), userId, groupId);
        }

        dbChangeManager.processDbChange(userId,
                                        DBChangeMsg.CHANGE_YUKON_USER_DB,
                                        DBChangeMsg.CAT_YUKON_USER,
                                        DBChangeMsg.CAT_YUKON_USER,
                                        DbChangeType.ADD);
    }
    
    @Override
    public SearchResult<LiteYukonUser> getUsersForUserGroup(int userGroupId, final int start, final int count) {
        /* Get Total Count */
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT COUNT(*)");
        sql.append("FROM YukonUser");
        sql.append("WHERE UserGroupId").eq(userGroupId);
        
        int totalCount = yukonJdbcTemplate.queryForInt(sql);
        
        PagingResultSetExtractor<LiteYukonUser> pagingExtractor = new PagingResultSetExtractor<LiteYukonUser>(start, count, new LiteYukonUserMapper());
        
        sql = new SqlStatementBuilder();
        sql.append("SELECT *");
        sql.append("FROM YukonUser");
        sql.append("WHERE UserGroupid").eq(userGroupId);
        
        yukonJdbcTemplate.query(sql, pagingExtractor);
        
        List<LiteYukonUser> users = pagingExtractor.getResultList();
        SearchResult<LiteYukonUser> searchResult = new SearchResult<LiteYukonUser>();
        searchResult.setResultList(users);
        searchResult.setBounds(start, count, totalCount);
        
        return searchResult;
    }
	
    @Override
    public LiteYukonUser createLoginForAdditionalContact(String firstName, String lastName, LiteUserGroup userGroup) {
        YukonUser login = new YukonUser();
        String newUserName = generateUsername(firstName, lastName);
        com.cannontech.database.db.user.YukonUser user = login.getYukonUser();
        user.setUsername(newUserName);
        user.setLoginStatus(LoginStatusEnum.ENABLED);
        user.setForceReset(true);
        user.setUserGroupId(userGroup.getUserGroupId());

        dbPersistantDao.performDBChange(login, TransactionType.INSERT);

        return new LiteYukonUser(login.getUserID(), user.getUsername(), user.getLoginStatus(), user.isForceReset(),
                user.getUserGroupId());
    }

    @Override
    public List<LiteYukonUser> getOperatorLoginsByEnergyCompanyIds(Iterable<Integer> energyCompanyIds) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT YU.*");
        sql.append("FROM YukonUser YU");
        sql.append("  JOIN EnergyCompanyOperatorLoginList ECOLL ON ECOLL.OperatorLoginId = YU.UserId");
        sql.append("WHERE ECOLL.EnergyCompanyId").in(energyCompanyIds);
        sql.append("ORDER BY YU.UserName ASC");
        
        List<LiteYukonUser> operators = yukonJdbcTemplate.query(sql, new LiteYukonUserMapper());
        return operators;
    }

    @Override
    public List<LiteYukonUser> getUsersByUserGroupId(int userGroupId) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT YU.*");
        sql.append("FROM YukonUser YU");
        sql.append("WHERE YU.UserGroupId").eq(userGroupId);
        
        List<LiteYukonUser> users = yukonJdbcTemplate.query(sql,  new LiteYukonUserMapper());
        return users;
    };

    @Override
    public void updateUserGroupId(int userId, Integer userGroupId) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("UPDATE YukonUser").set("UserGroupId", userGroupId);
        sql.append("WHERE UserId").eq(userId);
        
        yukonJdbcTemplate.update(sql);
    }

    @Override
    public void updateForceResetByGroupId(int groupId, boolean forceReset) {
        SqlStatementBuilder userIdsByGroupIdSql = new SqlStatementBuilder();
        userIdsByGroupIdSql.append("SELECT UserId") ;
        userIdsByGroupIdSql.append("FROM UserGroupToYukonGroupMapping");
        userIdsByGroupIdSql.append("WHERE GroupId").eq(groupId);
        
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("UPDATE YukonUser");
        sql.append("SET ForceReset").eq(YNBoolean.valueOf(forceReset));
        sql.append("WHERE UserId").in(userIdsByGroupIdSql);
        
        yukonJdbcTemplate.update(sql);
    }
}