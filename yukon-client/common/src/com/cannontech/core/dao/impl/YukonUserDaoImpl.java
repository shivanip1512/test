package com.cannontech.core.dao.impl;

import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;

import org.apache.commons.lang.RandomStringUtils;
import org.joda.time.Instant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.transaction.annotation.Transactional;

import com.cannontech.common.events.loggers.SystemEventLogService;
import com.cannontech.common.exception.NotAuthorizedException;
import com.cannontech.common.search.SearchResult;
import com.cannontech.common.util.SimpleCallback;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.common.util.SqlStringStatementBuilder;
import com.cannontech.core.authentication.model.AuthType;
import com.cannontech.core.authorization.dao.PaoPermissionDao;
import com.cannontech.core.dao.DBPersistentDao;
import com.cannontech.core.dao.YukonUserDao;
import com.cannontech.core.users.model.LiteUserGroup;
import com.cannontech.database.FieldMapper;
import com.cannontech.database.PagingResultSetExtractor;
import com.cannontech.database.SimpleTableAccessTemplate;
import com.cannontech.database.TransactionType;
import com.cannontech.database.YNBoolean;
import com.cannontech.database.YukonJdbcTemplate;
import com.cannontech.database.YukonMappingRowCallbackHandler;
import com.cannontech.database.data.lite.LiteContact;
import com.cannontech.database.data.lite.LiteYukonGroup;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.database.data.user.YukonUser;
import com.cannontech.database.db.user.UserGroup;
import com.cannontech.database.incrementer.NextValueHelper;
import com.cannontech.message.dispatch.message.DBChangeMsg;
import com.cannontech.message.dispatch.message.DbChangeType;
import com.cannontech.user.UserUtils;
import com.cannontech.yukon.IDatabaseCache;

public class YukonUserDaoImpl implements YukonUserDao {

    @Autowired private DBPersistentDao dbPersistantDao;
    @Autowired private NextValueHelper nextValueHelper;
    @Autowired private SystemEventLogService systemEventLogService;
    @Autowired private YukonJdbcTemplate yukonJdbcTemplate;
    
    private PaoPermissionDao<LiteYukonUser> userPaoPermissionDao;
    private IDatabaseCache databaseCache;
    private SimpleTableAccessTemplate<LiteYukonUser> simpleTableTemplate;
    
    private final static FieldMapper<LiteYukonUser> fieldMapper = new FieldMapper<LiteYukonUser>() {
        @Override
        public Number getPrimaryKey(LiteYukonUser user) {
            return user.getUserID();
        }

        @Override
        public void setPrimaryKey(LiteYukonUser user, int userId) {
            user.setUserID(userId);
        }

        @Override
        public void extractValues(MapSqlParameterSource parameterHolder, LiteYukonUser user) {
            parameterHolder.addValue("Username", user.getUsername());
            parameterHolder.addValue("Status", user.getLoginStatus());
            parameterHolder.addValue("AuthType", user.getAuthType());
            parameterHolder.addValue("LastChangedDate", user.getLastChangedDate());
            parameterHolder.addValue("ForceReset", YNBoolean.valueOf(user.isForceReset()));
            parameterHolder.addValue("UserGroupId", user.getUserGroupId());
        }
    };
    
    @PostConstruct
    public void init() {
        simpleTableTemplate = new SimpleTableAccessTemplate<LiteYukonUser>(yukonJdbcTemplate, nextValueHelper);
        simpleTableTemplate.setTableName("YukonUser");
        simpleTableTemplate.setFieldMapper(fieldMapper);
        simpleTableTemplate.setPrimaryKeyField("UserId");
        simpleTableTemplate.setPrimaryKeyValidNotEqualTo(0);
    }
    
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
	    sendUserDbChangeMsg(modifiedUserId, DbChangeType.UPDATE);
	}
	
	@Override
	@Transactional
    public void save(LiteYukonUser user) {
	    boolean update = simpleTableTemplate.saveWillUpdate(user);
	    
	    if (update) {
	        simpleTableTemplate.save(user);
	    } else {
	        int nextUserId = nextValueHelper.getNextValue("YukonUser");
	        user.setUserID(nextUserId);
	        
	        SqlStatementBuilder sql = new SqlStatementBuilder();
	        sql.append("INSERT INTO YukonUser (UserId, Username, Password, Status, AuthType, LastChangedDate, ForceReset, UserGroupId)");
	        sql.values(user.getUserID(), user.getUsername(), " ", user.getLoginStatus(), user.getAuthType(), 
	                   user.getLastChangedDate(), YNBoolean.valueOf(user.isForceReset()), user.getUserGroupId());
	        yukonJdbcTemplate.update(sql);
	    }

	    sendUserDbChangeMsg(user.getUserID(), update ? DbChangeType.UPDATE : DbChangeType.ADD);
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

        
        sendUserDbChangeMsg(userId, DbChangeType.DELETE);        
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
	    
        sendUserDbChangeMsg(userId, DbChangeType.UPDATE);
    }

    @Override
    public void addUserToGroup(int userId, Integer... groupIds) {
        
        for (int groupId : groupIds) {

            SqlStringStatementBuilder sql = new SqlStringStatementBuilder();
            sql.append("INSERT INTO YukonUserGroup (UserId, GroupId)");
            sql.append("VALUES (? , ?)");
            
            yukonJdbcTemplate.update(sql.toString(), userId, groupId);
        }

        sendUserDbChangeMsg(userId, DbChangeType.ADD);
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

    /**
     * @param userId
     */
    private void sendUserDbChangeMsg(Integer userId, DbChangeType dbChangeType) {
        DBChangeMsg changeMsg = new DBChangeMsg(userId,
                                                DBChangeMsg.CHANGE_YUKON_USER_DB,
                                                DBChangeMsg.CAT_YUKON_USER,
                                                DBChangeMsg.CAT_YUKON_USER,
                                                dbChangeType);
        dbPersistantDao.processDBChange(changeMsg);
    }
	
    @Override
    public LiteYukonUser createLoginForAdditionalContact(String firstName, String lastName, LiteUserGroup userGroup) {
        YukonUser login = new YukonUser();
        String newUserName = generateUsername(firstName, lastName);
        login.getYukonUser().setUsername(newUserName);
        login.getYukonUser().setAuthType(AuthType.NONE);
        login.getYukonUser().setLoginStatus(LoginStatusEnum.ENABLED);
        login.getYukonUser().setLastChangedDate(new Date());
        login.getYukonUser().setForceReset(true);
        login.getYukonUser().setUserGroupId(userGroup.getUserGroupId());
        
        dbPersistantDao.performDBChange(login, TransactionType.INSERT);
        
        return new LiteYukonUser(login.getUserID(), login.getYukonUser().getUsername(), login.getYukonUser().getLoginStatus(), login.getYukonUser().getAuthType(),
                                 new Instant(login.getYukonUser().getLastChangedDate()), login.getYukonUser().isForceReset(), login.getYukonUser().getUserGroupId());
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

    /* Dependency Injections */
    public void setDatabaseCache(IDatabaseCache databaseCache) {
        this.databaseCache = databaseCache;
    }
    
    @Autowired
    public void setUserPaoPermissionDao(PaoPermissionDao<LiteYukonUser> userPaoPermissionDao) {
        this.userPaoPermissionDao = userPaoPermissionDao;
    }
}