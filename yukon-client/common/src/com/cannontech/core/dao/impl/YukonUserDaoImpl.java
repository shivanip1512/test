package com.cannontech.core.dao.impl;

import java.util.Arrays;
import java.util.List;

import javax.annotation.PostConstruct;

import org.apache.commons.lang.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataRetrievalFailureException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.simple.ParameterizedRowMapper;
import org.springframework.transaction.annotation.Transactional;

import com.cannontech.common.events.loggers.SystemEventLogService;
import com.cannontech.common.exception.NotAuthorizedException;
import com.cannontech.common.search.SearchResult;
import com.cannontech.common.util.SimpleCallback;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.common.util.SqlStringStatementBuilder;
import com.cannontech.core.authentication.service.AuthType;
import com.cannontech.core.authorization.dao.PaoPermissionDao;
import com.cannontech.core.dao.DBPersistentDao;
import com.cannontech.core.dao.YukonUserDao;
import com.cannontech.database.FieldMapper;
import com.cannontech.database.MappingRowCallbackHandler;
import com.cannontech.database.PagingResultSetExtractor;
import com.cannontech.database.SimpleTableAccessTemplate;
import com.cannontech.database.SqlUtils;
import com.cannontech.database.TransactionType;
import com.cannontech.database.YukonJdbcTemplate;
import com.cannontech.database.data.lite.LiteContact;
import com.cannontech.database.data.lite.LiteFactory;
import com.cannontech.database.data.lite.LiteYukonGroup;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.database.data.user.YukonGroup;
import com.cannontech.database.data.user.YukonUser;
import com.cannontech.database.incrementer.NextValueHelper;
import com.cannontech.message.dispatch.message.DBChangeMsg;
import com.cannontech.message.dispatch.message.DbChangeType;
import com.cannontech.user.UserUtils;
import com.cannontech.yukon.IDatabaseCache;

/**
 * @author rneuharth
 * Oct 16, 2002 at 2:12:21 PM
 * 
 */
public class YukonUserDaoImpl implements YukonUserDao {
    private static final String selectSql;
    private static final String selectByIdSql;
    private static final String selectByUsernameSql;
    public static final ParameterizedRowMapper<LiteYukonUser> rowMapper;
    
    private DBPersistentDao dbPersistantDao;
    private IDatabaseCache databaseCache;
    private NextValueHelper nextValueHelper;
    private PaoPermissionDao<LiteYukonUser> userPaoPermissionDao = null;
    private SystemEventLogService systemEventLogService;
    private YukonJdbcTemplate yukonJdbcTemplate;
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
    
    static {
        
        selectSql = "SELECT UserID,UserName,Status,AuthType FROM YukonUser YU";
        
        selectByIdSql = selectSql + " WHERE UserID = ?";
        
        selectByUsernameSql = selectSql + " WHERE LOWER( UserName ) = LOWER( ? )";
        
        rowMapper = createRowMapper();
    }
    
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

	    systemEventLogService.usernameChanged(changingUser,
	                                          modifiedUser.getUsername(),
	                                          newUsername);
	}
	
	@Override
	@Transactional
    public void save(LiteYukonUser user) {
	    boolean create = simpleTableTemplate.saveWillUpdate(user);
	    simpleTableTemplate.save(user);
	    sendUserDbChangeMsg(user.getUserID(), create ? DbChangeType.ADD : DbChangeType.UPDATE);
    }
	
	@Override
    @Transactional
    public void addLiteYukonUserWithPassword(LiteYukonUser user, String password, int energyCompanyId, List<LiteYukonGroup> groups) throws DataAccessException {
        addLiteYukonUserWithPassword(user, password, groups);
    }

    @Override
	@Transactional
	public void addLiteYukonUserWithPassword(LiteYukonUser user, String password, List<LiteYukonGroup> groups) throws DataAccessException {
	    int userId = nextValueHelper.getNextValue("YukonUser");
	    user.setUserID(userId);
	    SqlStringStatementBuilder sql = new SqlStringStatementBuilder();
	    sql.append("INSERT INTO YukonUser VALUES (?,?,?,?,?)");
	    yukonJdbcTemplate.update(sql.toString(), user.getUserID(), user.getUsername(), SqlUtils.convertStringToDbValue(password), user.getLoginStatus().getDatabaseRepresentation(), user.getAuthType().name());
	    
	    for(LiteYukonGroup group : groups) {
	        sql = new SqlStringStatementBuilder();
    	    sql.append("INSERT INTO YukonUserGroup VALUES (");
    	    sql.append(user.getUserID()); 
    	    sql.append(",");
    	    sql.append(group.getGroupID());
    	    sql.append(")");
    	    
    	    yukonJdbcTemplate.update(sql.toString());
	    }
	}
	
	@Override
    @Transactional
    public void update(LiteYukonUser user) {
        final String sql = "update yukonuser set username = ?, status = ?, AuthType = ? where userid = ?";
        yukonJdbcTemplate.update(sql, user.getUsername(),
                                   user.getLoginStatus().getDatabaseRepresentation(),
                                   user.getAuthType().name(), user.getUserID());
    }

	
    @Override
    public void setUserStatus(LiteYukonUser user, LoginStatusEnum loginStatusEnum) {

        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("UPDATE YukonUser ");
        sql.append("SET Status").eq(loginStatusEnum);
        sql.append("WHERE UserId").eq(user.getUserID());
        
        yukonJdbcTemplate.update(sql);
        
    }    
	
	public LiteYukonUser getLiteYukonUser(final int userId) {
	    try {
	        LiteYukonUser user = yukonJdbcTemplate.queryForObject(selectByIdSql, rowMapper, userId);
	        return user;
	    } catch (DataRetrievalFailureException e) {
            return null;
        }
	}

	public LiteYukonUser findUserByUsername(final String userName) {
		try {
		    LiteYukonUser user = yukonJdbcTemplate.queryForObject(selectByUsernameSql, rowMapper, userName);
		    return user;
		} catch (DataRetrievalFailureException e) {
		    return null;
		}
	}
		
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
        
        String deleteUserRole = "DELETE FROM YukonUserRole WHERE UserId = ?";
        yukonJdbcTemplate.update(deleteUserRole, userId);

        String deleteYukonUserGroup = "DELETE FROM YukonUserGroup WHERE UserId = ?";
        yukonJdbcTemplate.update(deleteYukonUserGroup, userId);

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
	    String sql = "select * from yukonuser order by username";
	    yukonJdbcTemplate.getJdbcOperations().query(sql, new MappingRowCallbackHandler<LiteYukonUser>(new LiteYukonUserMapper(), callback));
	}
	
	@Override
	public void callbackWithYukonUsersInGroup(LiteYukonGroup group, SimpleCallback<LiteYukonUser> callback) {

	    SqlStatementBuilder sql = new SqlStatementBuilder();
	    sql.append("select yu.*");
	    sql.append("from yukonusergroup yug");
	    sql.append("  join yukonuser yu on yug.userid = yu.userid");
	    sql.append("where groupid = ").appendArgument(group.getGroupID());
	    sql.append("order by yu.username");
	    yukonJdbcTemplate.query(sql, new MappingRowCallbackHandler<LiteYukonUser>(new LiteYukonUserMapper(), callback));
	}

    public void removeUserFromGroup(int userId, Integer... groupIds) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("DELETE FROM YukonUserGroup");
        sql.append("WHERE UserId").eq(userId);
        sql.append("AND GroupId").in(Arrays.asList(groupIds));
        yukonJdbcTemplate.update(sql);

        sendUserDbChangeMsg(userId, DbChangeType.DELETE);
    }
	
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
    public SearchResult<LiteYukonUser> getUsersForGroup(int groupId, final int start, final int count) {
        /* Get Total Count */
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT COUNT(*)");
        sql.append("FROM YukonUserGroup yug");
        sql.append("  JOIN YukonUser yu ON yug.UserId = yu.UserId");
        sql.append("WHERE GroupId").eq(groupId);
        
        int totalCount = yukonJdbcTemplate.queryForInt(sql);
        
        PagingResultSetExtractor<LiteYukonUser> pagingExtractor = new PagingResultSetExtractor<LiteYukonUser>(start, count, createRowMapper());
        
        sql = new SqlStatementBuilder();
        sql.append("SELECT yu.*");
        sql.append("FROM yukonusergroup yug");
        sql.append("  JOIN yukonuser yu ON yug.userid = yu.userid");
        sql.append("WHERE groupid").eq(groupId);
        
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
	
    public void setDatabaseCache(IDatabaseCache databaseCache) {
        this.databaseCache = databaseCache;
    }

    private static ParameterizedRowMapper<LiteYukonUser> createRowMapper() {
        final ParameterizedRowMapper<LiteYukonUser> mapper = new LiteYukonUserMapper();
        return mapper;
    }
    
    @Override
    public LiteYukonUser createLoginForAdditionalContact(String firstName, String lastName, LiteYukonGroup group) {
        YukonUser login = new YukonUser();
        String newUserName = generateUsername(firstName, lastName);
        login.getYukonUser().setUsername(newUserName);
        login.getYukonUser().setAuthType(AuthType.NONE);
        login.getYukonGroups().addElement(((YukonGroup)LiteFactory.convertLiteToDBPers(group)).getYukonGroup());
        login.getYukonUser().setLoginStatus(LoginStatusEnum.ENABLED);
        
        dbPersistantDao.performDBChange(login, TransactionType.INSERT);
        
        return new LiteYukonUser(login.getUserID(), login.getYukonUser().getUsername(), login.getYukonUser().getLoginStatus(), login.getYukonUser().getAuthType());
    }

    @Override
    public List<LiteYukonUser> getOperatorLoginsByEnergyCompanyIds(Iterable<Integer> energyCompanyIds) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append(selectSql);
        sql.append("JOIN EnergyCompanyOperatorLoginList ECOLL ON ECOLL.OperatorLoginId = YU.UserId");
        sql.append("WHERE ECOLL.EnergyCompanyId").in(energyCompanyIds);
        
        List<LiteYukonUser> operators = yukonJdbcTemplate.query(sql, rowMapper);
        
        return operators;
    }
    
    // DI Setters
    @Autowired
    public void setDbPersistantDao(DBPersistentDao dbPersistantDao) {
        this.dbPersistantDao = dbPersistantDao;
    }
    
    @Autowired
    public void setNextValueHelper(NextValueHelper nextValueHelper) {
        this.nextValueHelper = nextValueHelper;
    }
    
    @Autowired
    public void setSystemEventLogService(SystemEventLogService systemEventLogService) {
        this.systemEventLogService = systemEventLogService;
    }

    @Autowired
    public void setUserPaoPermissionDao(PaoPermissionDao<LiteYukonUser> userPaoPermissionDao) {
        this.userPaoPermissionDao = userPaoPermissionDao;
    }

    @Autowired
    public void setYukonJdbcTemplate(YukonJdbcTemplate yukonJdbcTemplate) {
        this.yukonJdbcTemplate = yukonJdbcTemplate;
    }

}