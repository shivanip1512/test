package com.cannontech.core.dao.impl;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang3.RandomStringUtils;
import org.joda.time.Instant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.transaction.annotation.Transactional;

import com.cannontech.common.events.loggers.SystemEventLogService;
import com.cannontech.common.exception.NotAuthorizedException;
import com.cannontech.common.model.PagingParameters;
import com.cannontech.common.search.result.SearchResults;
import com.cannontech.common.user.NewUser;
import com.cannontech.common.user.UserAuthenticationInfo;
import com.cannontech.common.util.ChunkingMappedSqlTemplate;
import com.cannontech.common.util.SimpleCallback;
import com.cannontech.common.util.SqlFragmentGenerator;
import com.cannontech.common.util.SqlFragmentSource;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.core.authentication.model.AuthType;
import com.cannontech.core.authentication.service.AuthenticationService;
import com.cannontech.core.authorization.dao.PaoPermissionDao;
import com.cannontech.core.dao.DBPersistentDao;
import com.cannontech.core.dao.YukonUserDao;
import com.cannontech.core.roleproperties.YukonRole;
import com.cannontech.core.users.model.LiteUserGroup;
import com.cannontech.database.PagingResultSetExtractor;
import com.cannontech.database.SqlParameterSink;
import com.cannontech.database.TransactionType;
import com.cannontech.database.TypeRowMapper;
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
import com.cannontech.stars.core.dao.ECMappingDao;
import com.cannontech.user.UserUtils;
import com.cannontech.yukon.IDatabaseCache;
import com.google.common.base.Function;
import com.google.common.base.Functions;
import com.google.common.collect.Maps;

public class YukonUserDaoImpl implements YukonUserDao {

    @Autowired private AuthenticationService authService;
    @Autowired private DbChangeManager dbChangeManager;
    @Autowired private DBPersistentDao dbPersistantDao;
    @Autowired private IDatabaseCache databaseCache;
    @Autowired private NextValueHelper nextValueHelper;
    @Autowired private PaoPermissionDao<LiteYukonUser> userPaoPermissionDao;
    @Autowired private SystemEventLogService systemEventLogService;
    @Autowired private YukonJdbcTemplate jdbcTemplate;
    @Autowired private ECMappingDao ecMappingDao;
    
    public static final int numberOfRandomChars = 5;
    
    private final static YukonRowMapper<UserAuthenticationInfo> authInfoMapper =
            new YukonRowMapper<UserAuthenticationInfo>() {
        @Override
        public UserAuthenticationInfo mapRow(YukonResultSet rs) throws SQLException {
            int userId = rs.getInt("UserId");
            AuthType authType = rs.getEnum("AuthType", AuthType.class);
            Instant lastChangedDate = rs.getInstant("LastChangedDate");
            return new UserAuthenticationInfo(userId, authType, lastChangedDate);
        }
    };
    
    private final static YukonRowMapper<Map.Entry<Integer, UserAuthenticationInfo>> authInfoEntryMapper =
        new YukonRowMapper<Map.Entry<Integer, UserAuthenticationInfo>>() {
            @Override
            public Entry<Integer, UserAuthenticationInfo> mapRow(YukonResultSet rs) throws SQLException {
                UserAuthenticationInfo userAuthenticationInfo = authInfoMapper.mapRow(rs);
                return Maps.immutableEntry(userAuthenticationInfo.getUserId(), userAuthenticationInfo);
            }
        };
    
    @Override
    public void changeUsername(LiteYukonUser changingUser, int modifiedUserId, String newUsername)
    throws NotAuthorizedException {
        
        LiteYukonUser existingUser = findUserByUsername(newUsername);
        if (existingUser != null) {
            throw new NotAuthorizedException("Username with " + newUsername + " already exists");
        }
        
        final LiteYukonUser modifiedUser = getLiteYukonUser(modifiedUserId);
        modifiedUser.setUsername(newUsername);
        update(modifiedUser);
        
        systemEventLogService.usernameChanged(changingUser, modifiedUser.getUsername(), newUsername);
        
        dbChangeManager.processDbChange(modifiedUserId, DBChangeMsg.CHANGE_YUKON_USER_DB, DBChangeMsg.CAT_YUKON_USER,
            DBChangeMsg.CAT_YUKON_USER, DbChangeType.UPDATE);
    }
    
    @Override
    @Transactional
    public void save(LiteYukonUser user) {
        
        boolean update = user.getUserID() != LiteYukonUser.CREATE_NEW_USER_ID;
        if (update) {
            update(user);
        } else {
            int nextUserId = nextValueHelper.getNextValue("YukonUser");
            user.setUserID(nextUserId);

            SqlStatementBuilder sql = new SqlStatementBuilder();
            sql.append("INSERT INTO YukonUser (UserId, Username, Password, Status, AuthType, LastChangedDate, "
                + "ForceReset, UserGroupId)");
            sql.values(user.getUserID(), user.getUsername(), " ", user.getLoginStatus(), AuthType.NONE, new Instant(),
                YNBoolean.valueOf(user.isForceReset()), user.getUserGroupId());
            jdbcTemplate.update(sql);
        }
        
        DbChangeType changeType = update ? DbChangeType.UPDATE : DbChangeType.ADD;
        dbChangeManager.processDbChange(user.getUserID(), DBChangeMsg.CHANGE_YUKON_USER_DB, DBChangeMsg.CAT_YUKON_USER,
            DBChangeMsg.CAT_YUKON_USER, changeType);
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
        
        jdbcTemplate.update(sql);
    }
    
    @Override
    public void setUserStatus(LiteYukonUser user, LoginStatusEnum loginStatusEnum) {
        
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("UPDATE YukonUser ");
        sql.append("SET Status").eq(loginStatusEnum);
        sql.append("WHERE UserId").eq(user.getUserID());
        
        jdbcTemplate.update(sql);
        
    }
    
    @Override
    public LiteYukonUser getLiteYukonUser(int userId) {
        try {
            SqlStatementBuilder sql = new SqlStatementBuilder();
            sql.append("SELECT YU.*");
            sql.append("FROM YukonUser YU");
            sql.append("WHERE UserId").eq(userId);
            
            LiteYukonUser user = jdbcTemplate.queryForObject(sql, new LiteYukonUserMapper());
            return user;
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }
    
    @Override
    public UserAuthenticationInfo getUserAuthenticationInfo(int userId) {
        
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT UserId, AuthType, LastChangedDate FROM YukonUser WHERE UserId").eq(userId);
        UserAuthenticationInfo userAuthenticationInfo = jdbcTemplate.queryForObject(sql, authInfoMapper);
        
        return userAuthenticationInfo;
    }
    
    @Override
    public Map<Integer, UserAuthenticationInfo> getUserAuthenticationInfo(Iterable<Integer> userIds) {
        
        ChunkingMappedSqlTemplate template = new ChunkingMappedSqlTemplate(jdbcTemplate);
        SqlFragmentGenerator<Integer> sqlGenerator = new SqlFragmentGenerator<Integer>() {
            @Override
            public SqlFragmentSource generate(List<Integer> subList) {
                SqlStatementBuilder sql = new SqlStatementBuilder();
                sql.append("SELECT UserId, AuthType, LastChangedDate FROM YukonUser WHERE UserId").in(subList);
                return sql;
            }
        };
        Function<Integer, Integer> identityFunction = Functions.identity();
        Map<Integer, UserAuthenticationInfo> authenticationInfo =
            template.mappedQuery(sqlGenerator, userIds, authInfoEntryMapper, identityFunction);
        
        return authenticationInfo;
    }
    
    @Override
    public LiteYukonUser findUserByUsername(String userName) {
        try {
            SqlStatementBuilder sql = new SqlStatementBuilder();
            sql.append("SELECT YU.*");
            sql.append("FROM YukonUser YU");
            sql.append("WHERE LOWER( YU.UserName ) = LOWER(").appendArgument(userName).append(")");

            LiteYukonUser user = jdbcTemplate.queryForObject(sql, new LiteYukonUserMapper());
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
        if (lastName.length() > 63) {
            newUsername = firstInitial + lastName.substring(0, 63).toLowerCase();
        } else {
            newUsername = firstInitial + lastName.toLowerCase();
        }
        
        // If the username is not unique, we will try once to generate a unique one.
        if (findUserByUsername(newUsername) != null) {
            String extraDigits = RandomStringUtils.randomAlphanumeric(numberOfRandomChars);
            String uniqueUsername;
            
            // If the extra digits will push this over 64,
            // we will truncate part of the username to make room for it.
            if (newUsername.length() + numberOfRandomChars > 64) {
                uniqueUsername = newUsername.substring(0, newUsername.length() - numberOfRandomChars);
            } else {
                uniqueUsername = newUsername;
            }
            
            uniqueUsername += extraDigits;
            
            if (findUserByUsername(uniqueUsername) != null) {
                throw new RuntimeException("Failed to generate unique username, please retry");
            }
            newUsername = uniqueUsername;
        }
        
        return newUsername;
    }
    
    @Override
    public void removeUserFromEventBase(int userId) {
        
        SqlStatementBuilder zeroOutEventBaseUserIdsSql = new SqlStatementBuilder();
        zeroOutEventBaseUserIdsSql.append("UPDATE EventBase");
        zeroOutEventBaseUserIdsSql.append("SET UserId").eq(UserUtils.USER_NONE_ID);
        zeroOutEventBaseUserIdsSql.append("WHERE UserId").eq(userId);
        
        jdbcTemplate.update(zeroOutEventBaseUserIdsSql);
    }
    
    @Override
    @Transactional
    public void deleteUser(Integer userId) {
        
        // Remove existing contacts before removing username
        SqlStatementBuilder detachContactFromLogin = new SqlStatementBuilder();
        detachContactFromLogin.append("UPDATE Contact");
        detachContactFromLogin.append("SET LoginId").eq(UserUtils.USER_NONE_ID);
        detachContactFromLogin.append("WHERE LoginId").eq(userId);
        jdbcTemplate.update(detachContactFromLogin);
        
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("DELETE FROM EnergyCompanyOperatorLoginList");
        sql.append("WHERE OperatorLoginId").eq(userId);
        
        jdbcTemplate.update(sql);
        
        userPaoPermissionDao.removeAllPermissions(userId);
        
        removeUserFromEventBase(userId);
        
        sql = new SqlStatementBuilder();
        sql.append("DELETE FROM YukonUser");
        sql.append("WHERE UserId").eq(userId);
        
        jdbcTemplate.update(sql);
        
        dbChangeManager.processDbChange(userId, DBChangeMsg.CHANGE_YUKON_USER_DB, DBChangeMsg.CAT_YUKON_USER,
            DBChangeMsg.CAT_YUKON_USER, DbChangeType.DELETE);
    }
    
    @Override
    public int getAllYukonUserCount() {
        
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT COUNT(*) FROM YukonUser");
        
        int count = jdbcTemplate.queryForInt(sql);
        return count;
    }
    
    @Override
    public void callbackWithAllYukonUsers(SimpleCallback<LiteYukonUser> callback) {
        
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT *");
        sql.append("FROM YukonUser");
        sql.append("ORDER BY Username");
        
        jdbcTemplate.query(sql, new YukonMappingRowCallbackHandler<>(new LiteYukonUserMapper(), callback));
    }
    
    @Override
    public void callbackWithYukonUsersInGroup(LiteYukonGroup group, SimpleCallback<LiteYukonUser> callback) {
        
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT YU.*");
        sql.append("FROM YukonUser YU");
        sql.append("  JOIN UserGroupToYukonGroupMapping UGTYGM ON UGTYGM.UserGroupId = YU.UserGroupId");
        sql.append("WHERE UGTYGM.GroupId").eq(group.getGroupID());
        sql.append("ORDER BY YU.Username");
        
        jdbcTemplate.query(sql, new YukonMappingRowCallbackHandler<>(new LiteYukonUserMapper(), callback));
    }
    
    @Override
    public void callbackWithYukonUsersInUserGroup(UserGroup userGroup, SimpleCallback<LiteYukonUser> callback) {
        
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT YU.*");
        sql.append("FROM YukonUser YU");
        sql.append("WHERE YU.UserGroupId").eq(userGroup.getUserGroupId());
        sql.append("ORDER BY YU.Username");
        
        jdbcTemplate.query(sql, new YukonMappingRowCallbackHandler<>(new LiteYukonUserMapper(), callback));
    }
    
    @Override
    public void removeUserFromUserGroup(int userId) {
        
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("UPDATE YukonUser");
        sql.set("UserGroupId", null);
        sql.append("WHERE UserId").eq(userId);
        jdbcTemplate.update(sql);
        
        dbChangeManager.processDbChange(userId, DBChangeMsg.CHANGE_YUKON_USER_DB, DBChangeMsg.CAT_YUKON_USER,
            DBChangeMsg.CAT_YUKON_USER, DbChangeType.UPDATE);
    }
    
    @Override
    public void addUserToGroup(int userId, Integer... groupIds) {
        
        for (int groupId : groupIds) {
            
            SqlStatementBuilder sql = new SqlStatementBuilder();
            sql.append("INSERT INTO YukonUserGroup (UserId, GroupId)");
            sql.values(userId, groupId);
            jdbcTemplate.update(sql);
        }
        
        dbChangeManager.processDbChange(userId, DBChangeMsg.CHANGE_YUKON_USER_DB, DBChangeMsg.CAT_YUKON_USER,
            DBChangeMsg.CAT_YUKON_USER, DbChangeType.ADD);
    }
    
    @Override
    public SearchResults<LiteYukonUser> getUsersForUserGroup(int userGroupId, PagingParameters paging) {
        
        int start = paging.getStartIndex();
        int count = paging.getItemsPerPage();
        
        /* Get Total Count */
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT COUNT(*)");
        sql.append("FROM YukonUser");
        sql.append("WHERE UserGroupId").eq(userGroupId);
        
        int totalCount = jdbcTemplate.queryForInt(sql);
        
        PagingResultSetExtractor<LiteYukonUser> pagingExtractor =
            new PagingResultSetExtractor<>(start, count, new LiteYukonUserMapper());
        
        sql = new SqlStatementBuilder();
        sql.append("SELECT *");
        sql.append("FROM YukonUser");
        sql.append("WHERE UserGroupid").eq(userGroupId);
        
        jdbcTemplate.query(sql, pagingExtractor);
        
        List<LiteYukonUser> users = pagingExtractor.getResultList();
        SearchResults<LiteYukonUser> searchResult = new SearchResults<>();
        searchResult.setResultList(users);
        searchResult.setBounds(start, count, totalCount);
        
        return searchResult;
    }
    
    @Override
    public List<Integer> getUserIdsForUserGroup(int userGroupId) {
        
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT UserId");
        sql.append("FROM YukonUser");
        sql.append("WHERE UserGroupid").eq(userGroupId);
        
        List<Integer> userIds = jdbcTemplate.query(sql, TypeRowMapper.INTEGER);
        
        return userIds;
    }
    
    @Override
    @Transactional
    public LiteYukonUser create(NewUser user, boolean forceReset, LiteYukonUser createdBy) {
        
        int userId = nextValueHelper.getNextValue("YukonUser");
        
        SqlStatementBuilder sql = new SqlStatementBuilder();
        SqlParameterSink values = sql.insertInto("YukonUser");
        values.addValue("UserId", userId);
        values.addValue("Username", user.getUsername());
        values.addValueSafe("Password", user.getPassword().getPassword());
        LoginStatusEnum status = user.isEnabled() ? LoginStatusEnum.ENABLED : LoginStatusEnum.DISABLED;
        values.addValue("Status", status);
        values.addValue("AuthType", user.getAuthCategory().getSupportingAuthType());
        values.addValue("LastChangedDate", Instant.now());
        values.addValue("ForceReset", YNBoolean.valueOf(forceReset));
        values.addValue("UserGroupId", user.getUserGroupId());
        jdbcTemplate.update(sql);
        
        // Set encrypted password and expire if necessary.
        LiteYukonUser lyu = new LiteYukonUser(userId, user.getUsername(), status, forceReset, user.getUserGroupId());
        if (authService.supportsPasswordSet(user.getAuthCategory())) {
            authService.setPassword(lyu, user.getPassword().getPassword(), createdBy);
            // Setting password unexpires it.
            if (forceReset) {
                sql = new SqlStatementBuilder();
                sql.append("UPDATE YukonUser SET ForceReset").eq_k(YNBoolean.YES);
                sql.append("WHERE UserId").eq(userId);
                jdbcTemplate.update(sql);
            }
        }
        
        if (user.getEnergyCompanyId() != null) {
            ecMappingDao.addEnergyCompanyOperatorLoginListMapping(userId, user.getEnergyCompanyId());
        }
        
        dbChangeManager.processDbChange(userId, DBChangeMsg.CHANGE_YUKON_USER_DB, DBChangeMsg.CAT_YUKON_USER, 
                DBChangeMsg.CAT_YUKON_USER, DbChangeType.ADD);
        
        return lyu;
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
        
        List<LiteYukonUser> operators = jdbcTemplate.query(sql, new LiteYukonUserMapper());
        return operators;
    }
    
    @Override
    public void updateUserGroupId(int userId, Integer userGroupId) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("UPDATE YukonUser").set("UserGroupId", userGroupId);
        sql.append("WHERE UserId").eq(userId);
        
        jdbcTemplate.update(sql);
        
        dbChangeManager.processDbChange(userId, DBChangeMsg.CHANGE_YUKON_USER_DB, DBChangeMsg.CAT_YUKON_USER,
            DBChangeMsg.CAT_YUKON_USER, DbChangeType.UPDATE);
    }
    
    @Override
    public void updateForceResetByRoleGroupId(int roleGroupId, boolean forceReset) {
        
        SqlStatementBuilder userGroupsByRoleGroupIdSql = new SqlStatementBuilder();
        userGroupsByRoleGroupIdSql.append("SELECT UserGroupId");
        userGroupsByRoleGroupIdSql.append("FROM UserGroupToYukonGroupMapping");
        userGroupsByRoleGroupIdSql.append("WHERE GroupId").eq(roleGroupId);
        
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("UPDATE YukonUser");
        sql.append("SET ForceReset").eq(YNBoolean.valueOf(forceReset));
        sql.append("WHERE UserGroupId").in(userGroupsByRoleGroupIdSql);
        
        jdbcTemplate.update(sql);
    }
    
    @Override
    public void removeUsersFromUserGroup(int userGroupId) {
        
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("UPDATE YukonUser");
        sql.set("UserGroupId", null);
        sql.append("WHERE UserGroupId").eq(userGroupId);
        
        jdbcTemplate.update(sql);
    }
    
    @Override
    public int getNonResidentialUserCount() {
        SqlStatementBuilder userGroupsWithResidentialRole = new SqlStatementBuilder();
        userGroupsWithResidentialRole.append("SELECT DISTINCT UserGroupId");
        userGroupsWithResidentialRole.append("FROM UserGroupToYukonGroupMapping map");
        userGroupsWithResidentialRole.append("JOIN YukonGroupRole ygr ON map.GroupId = ygr.GroupId");
        userGroupsWithResidentialRole.append("WHERE RoleId").eq(YukonRole.RESIDENTIAL_CUSTOMER);

        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT COUNT(DISTINCT UserId)");
        sql.append("FROM YukonUser yu");
        sql.append("JOIN UserGroup ug ON yu.UserGroupId = ug.UserGroupId");
        sql.append("AND ug.UserGroupId").notIn(userGroupsWithResidentialRole);

        int count = jdbcTemplate.queryForInt(sql);
        return count;
    }

    @Override
    public List<String> getNonResidentialUserGroups() {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT Name");
        sql.append("FROM UserGroup yg");

        SqlStatementBuilder userGroupsWithResidentialRole = new SqlStatementBuilder();
        userGroupsWithResidentialRole.append("SELECT DISTINCT UserGroupId");
        userGroupsWithResidentialRole.append("FROM UserGroupToYukonGroupMapping map");
        userGroupsWithResidentialRole.append("JOIN YukonGroupRole ygr ON map.GroupId = ygr.GroupId");
        userGroupsWithResidentialRole.append("WHERE RoleId").eq(YukonRole.RESIDENTIAL_CUSTOMER);

        sql.append("WHERE yg.UserGroupId").notIn(userGroupsWithResidentialRole);

        List<String> userGroupNameList = jdbcTemplate.query(sql, TypeRowMapper.STRING);
        return userGroupNameList;

    }
    
    @Override
    public List<String> getDeviceActionsRoleUserGroups() {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT Name");
        sql.append("FROM UserGroup yg");

        SqlStatementBuilder userGroupsWithDeviceActionsRole = new SqlStatementBuilder();
        userGroupsWithDeviceActionsRole.append("SELECT DISTINCT UserGroupId");
        userGroupsWithDeviceActionsRole.append("FROM UserGroupToYukonGroupMapping map");
        userGroupsWithDeviceActionsRole.append("JOIN YukonGroupRole ygr ON map.GroupId = ygr.GroupId");
        userGroupsWithDeviceActionsRole.append("WHERE RoleId").eq(YukonRole.DEVICE_ACTIONS);

        sql.append("WHERE yg.UserGroupId").in(userGroupsWithDeviceActionsRole);

        List<String> userGroupNameList = jdbcTemplate.query(sql, TypeRowMapper.STRING);
        return userGroupNameList;

    }
}