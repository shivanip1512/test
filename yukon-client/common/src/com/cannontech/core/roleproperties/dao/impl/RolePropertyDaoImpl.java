package com.cannontech.core.roleproperties.dao.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;

import javax.annotation.PostConstruct;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.Validate;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.jdbc.core.simple.ParameterizedRowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;
import org.springframework.jmx.export.annotation.ManagedAttribute;
import org.springframework.jmx.export.annotation.ManagedResource;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.config.ConfigurationSource;
import com.cannontech.common.exception.BadConfigurationException;
import com.cannontech.common.exception.NotAuthorizedException;
import com.cannontech.common.util.LeastRecentlyUsedCacheMap;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.core.roleproperties.BadPropertyTypeException;
import com.cannontech.core.roleproperties.InputTypeFactory;
import com.cannontech.core.roleproperties.UserNotInRoleException;
import com.cannontech.core.roleproperties.YukonRole;
import com.cannontech.core.roleproperties.YukonRoleCategory;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.core.roleproperties.dao.RolePropertyDao;
import com.cannontech.database.IntegerRowMapper;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.roles.YukonGroupRoleDefs;
import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Iterables;
import com.google.common.collect.ImmutableMap.Builder;

/**
 * The class handles all aspects of Yukon's Role/Property system.
 * 
 * This class is designed to have a single dependency on a JdbcTemplate. Any other
 * dependency could cause issues because this class is needed early in the boot process.
 * This presents a problem because this class cache property values and thus must
 * know when to clear its cache. Normally this would be implemented by creating
 * a dependency on the AsyncDynamicDataSource. Instead, this class has a partner object
 * that will notify this class of any changes.
 * This creates the following dependency graph (some details glossed over):

 * <pre>
 *    DispatchConnection --> RolePropertyDaoImpl --> DataSource
 *             ^                 ^
 *             |                 |
 *    RolePropertyChangeHelper --|
 * </pre>
 */
@ManagedResource
public class RolePropertyDaoImpl implements RolePropertyDao {
    private static class UserGroupPropertyValue {
        public boolean isUser;
        public String value;
    }
    private static class UserPropertyTuple {
        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + ((property == null) ? 0
                    : property.hashCode());
            result = prime * result + ((userId == null) ? 0 : userId.hashCode());
            return result;
        }
        @Override
        public boolean equals(Object obj) {
            if (this == obj)
                return true;
            if (obj == null)
                return false;
            if (getClass() != obj.getClass())
                return false;
            UserPropertyTuple other = (UserPropertyTuple) obj;
            if (!property.equals(other.property))
                return false;
            if (userId == null) {
                if (other.userId != null)
                    return false;
            } else if (!userId.equals(other.userId))
                return false;
            return true;
        }
        public UserPropertyTuple(LiteYukonUser user, YukonRoleProperty property) {
            if (user == null) {
                this.userId = null;
            } else {
                this.userId = user.getUserID();
            }
            this.property = property;
        }
        public Integer userId;
        public YukonRoleProperty property;
    }
    private SimpleJdbcTemplate simpleJdbcTemplate;
    private ConfigurationSource configurationSource;
    private Logger log = YukonLogManager.getLogger(RolePropertyDaoImpl.class);
    
    private ImmutableMap<YukonRoleProperty, Object> defaultValueLookup;
    private LeastRecentlyUsedCacheMap<UserPropertyTuple, Object> convertedValueCache = new LeastRecentlyUsedCacheMap<UserPropertyTuple, Object>(10000);
    private final Object NULL_CACHE_VALUE = new Object();
    private LeastRecentlyUsedCacheMap<Integer, Set<YukonRole>> userRoleCache = new LeastRecentlyUsedCacheMap<Integer, Set<YukonRole>>(1000);
    private Set<YukonRoleProperty> propertyExceptions = EnumSet.noneOf(YukonRoleProperty.class);
    private boolean allowRoleConflicts = false;
    
    // statistics
    private AtomicLong totalAccesses = new AtomicLong(0);
    private AtomicLong totalCacheHits = new AtomicLong(0);
    private AtomicLong totalDbHits = new AtomicLong(0);
    
    private static final ParameterizedRowMapper<UserGroupPropertyValue> userGroupPropertyValue = new ParameterizedRowMapper<UserGroupPropertyValue>() {
        public UserGroupPropertyValue mapRow(ResultSet rs, int rowNum) throws SQLException {
            int isUser = rs.getInt("isUser");
            String value = rs.getString("value");
            UserGroupPropertyValue propertyValue = new UserGroupPropertyValue();
            propertyValue.isUser = isUser == 1;
            propertyValue.value = value;
            return propertyValue;
        };
    };
    
    private class IsCheckPropertyCompatiblePredicate implements Predicate<YukonRoleProperty> {
        @Override
        public boolean apply(YukonRoleProperty property) {
            return isCheckPropertyCompatible(property);
        }
    }

    
    @PostConstruct
    public void initialize() {
        final Builder<YukonRoleProperty, Object> builder = ImmutableMap.builder();
        
        final EnumSet<YukonRoleProperty> unseenProperties = EnumSet.allOf(YukonRoleProperty.class);
        
        String sql = "select rolepropertyid, roleid, defaultvalue from yukonroleproperty";
        simpleJdbcTemplate.getJdbcOperations().query(sql, new RowCallbackHandler() {
            public void processRow(ResultSet rs) throws SQLException {
                int rolePropertyId = rs.getInt("RolePropertyId");
                int roleId = rs.getInt("RoleId");
                String defaultValue = rs.getString("DefaultValue");
                
                try {
                    YukonRoleProperty roleProperty = YukonRoleProperty.getForId(rolePropertyId);
                    unseenProperties.remove(roleProperty);
                    try {
                        YukonRole role = YukonRole.getForId(roleId);
                        if (role != roleProperty.getRole()) {
                            log.warn("Property " + roleProperty + " is incorrectly mapped to " + role + " (should be " + roleProperty.getRole() + ")");
                        }
                    } catch (IllegalArgumentException e1) {
                        log.warn("Database contains an unknown role: " + roleId);
                    }
                    try {
                        Object convertPropertyValue = convertPropertyValue(roleProperty, defaultValue);
                        if (convertPropertyValue != null) {
                            builder.put(roleProperty, convertPropertyValue);
                        }
                    } catch (BadPropertyTypeException e) {
                        log.error("Database contains an illegal default value for " + roleProperty + " (will be treated as null): " + defaultValue);
                    }
                } catch (IllegalArgumentException e) {
                    log.warn("Database contains an unknown role property: " + rolePropertyId);
                }
            }
            
        });
        
        defaultValueLookup = builder.build();
        
        // let's see what we missed
        for (YukonRoleProperty property : unseenProperties) {
            log.error("Database is missing a role property: " + property);
        }
        
        // build up exception list
        String exceptionList = configurationSource.getString("ROLE_PROPERTY_EXCEPTION_LIST", "");
        if (exceptionList.contains("*")) {
            propertyExceptions.addAll(EnumSet.allOf(YukonRoleProperty.class));
            log.info("propertyException list configured for all role properties");
        } else {
        	
        	// Add known issues to this list for 4.2
        	propertyExceptions.add(YukonRoleProperty.TEMPLATE_ROOT);	//YUK-6977, YUK-7073
        	
            String[] exceptionArray = exceptionList.split("\\s*,\\s*");
            for (String propertyStr : exceptionArray) {
            	if (StringUtils.isNotBlank(propertyStr)) {
	                try {
	                    YukonRoleProperty property = YukonRoleProperty.valueOf(propertyStr);
	                    propertyExceptions.add(property);
	                } catch (IllegalArgumentException e) {
	                    log.warn("master.cfg contains an unknown role property: " + propertyStr);
	                }
            	}
            }
            log.info("propertyException list configured for: " + propertyExceptions);
        }
        
        // determine of role conflicts are allowed
        String allowedStr = configurationSource.getString("ROLE_PROPERTY_CONFLICTS_ALLOWED", Boolean.FALSE.toString());
        allowRoleConflicts = Boolean.parseBoolean(allowedStr.trim());
    }
    
    public void clearCache() {
        log.debug("Removing about " +  convertedValueCache.size() + " values from convertedValueCache");
        convertedValueCache.clear();
        log.debug("Removing about " +  userRoleCache.size() + " values from userRoleCache");
        userRoleCache.clear();
    }

    @Override
    public boolean checkCategory(YukonRoleCategory category, LiteYukonUser user) {
        Set<YukonRoleCategory> roleCategoriesForUser = getRoleCategoriesForUser(user);
        boolean result = roleCategoriesForUser.contains(category);
        return result;
    }

    @Override
    public boolean checkProperty(YukonRoleProperty property, LiteYukonUser user) {
        boolean value;
        try {
            value = getPropertyBooleanValue(property, user);
            return value;
        } catch (UserNotInRoleException e) {
            return false;
        }
    }
    
    @Override
    public boolean isCheckPropertyCompatible(YukonRoleProperty property) {
        boolean assignableFrom = Boolean.class.isAssignableFrom(property.getType().getTypeClass());
        return assignableFrom;
    }
    
    @Override
    public boolean checkAllProperties(LiteYukonUser user,
            YukonRoleProperty firstProperty,
            YukonRoleProperty... otherProperties) {
        Iterable<YukonRoleProperty> properties = Iterables.concat(ImmutableList.of(firstProperty), ImmutableList.of(otherProperties));
        boolean allAreCompatible = Iterables.all(properties, new IsCheckPropertyCompatiblePredicate());
        Validate.isTrue(allAreCompatible, "at least one of " + properties + " is not compatible with a check method");
        
        for (YukonRoleProperty property : properties) {
            if (!checkProperty(property, user)) return false;
        }
        
        return true;
    }
    
    @Override
    public boolean checkAnyProperties(LiteYukonUser user, YukonRoleProperty firstProperty, YukonRoleProperty... otherProperties) {
        Iterable<YukonRoleProperty> properties = Iterables.concat(ImmutableList.of(firstProperty), ImmutableList.of(otherProperties));
        boolean allAreCompatible = Iterables.all(properties, new IsCheckPropertyCompatiblePredicate());
        Validate.isTrue(allAreCompatible, "at least one of " + properties + " is not compatible with a check method");
        
        for (YukonRoleProperty property : properties) {
            if (checkProperty(property, user)) return true;
        }
        
        return false;
    }

    @Override
    public boolean checkFalseProperty(YukonRoleProperty property, LiteYukonUser user) {
        try {
            Boolean booleanValue = getConvertedValue(property, user, Boolean.class);
            if (booleanValue == null) {
                return false;
            } else {
                return !booleanValue.booleanValue();
            }
        } catch (UserNotInRoleException e) {
            return false;
        }
    }
    
    @Override
    public boolean checkRole(YukonRole role, LiteYukonUser user) {
        Set<YukonRole> rolesForUser = getRolesForUser(user);
        boolean result = rolesForUser.contains(role);
        return result;
    }
    
    public Object convertPropertyValue(YukonRoleProperty roleProperty, String value) throws BadPropertyTypeException {
        try {
            return InputTypeFactory.convertPropertyValue(roleProperty.getType(), value);
        } catch (Exception e) {
            throw new BadPropertyTypeException(roleProperty, value, e);
        }
    }

    /**
     * @param <T>
     * @param property
     * @param user can be null if a System property is specified
     * @param returnType the type to convert to, can be Object, otherwise must be compatible with property
     * @return the converted value or the default, will only return null if the default was null
     */
    public <T> T getConvertedValue(YukonRoleProperty property,
            LiteYukonUser user, Class<T> returnType) throws BadPropertyTypeException {
    	totalAccesses.incrementAndGet();
        if (log.isDebugEnabled()) {
            log.debug("getting converted value of " + property + " for " + user + " as " + returnType.getSimpleName());
        }
        Validate.isTrue(returnType.isAssignableFrom(property.getType().getTypeClass()), "can't convert " + property + " to " + returnType);
        // create user/property key
        UserPropertyTuple key = new UserPropertyTuple(user, property);
        // check cache (using a special value to allow get to be used to check containsValue)
        Object cachedValue = convertedValueCache.get(key);
        if (cachedValue != null) {
            if (cachedValue == NULL_CACHE_VALUE) {
                cachedValue = null;
            }
            log.debug("cache hit, returning: " + cachedValue);
            totalCacheHits.incrementAndGet();
            return returnType.cast(cachedValue);
        }
        String stringValue = getPropertyValue(property, user);
        Object convertedValue = convertPropertyValue(property, stringValue);
        if (convertedValue == null) {
            log.debug("convertedValue was null, using default");
            convertedValue = defaultValueLookup.get(property);
        }
        if (log.isDebugEnabled()) {
            log.debug("returning: " + convertedValue);
        }
        T result = returnType.cast(convertedValue);
        if (convertedValue == null) {
            convertedValue = NULL_CACHE_VALUE;
        }
        convertedValueCache.put(key, convertedValue);
        return result;
    }

    public String getGlobalPropertyValue(YukonRoleProperty property) {
        Validate.isTrue(property.getRole().getCategory().isSystem(), "can't get global property for non-System property");
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("select value");
        sql.append("from YukonGroupRole ygr");
        sql.append("where GroupId = ").appendArgument(YukonGroupRoleDefs.GRP_YUKON);
        sql.append("  and RolePropertyId = ").appendArgument(property.getPropertyId());
        
        String value = null;
        try {
            value = simpleJdbcTemplate.queryForObject(sql.getSql(), String.class, sql.getArguments());
        } catch (EmptyResultDataAccessException e) {
            value = null;
            log.debug("got zero rows for global property");
        }
        if (log.isDebugEnabled()) {
            log.debug("got global property value of " + property + ": " + value);
        }
        return value;
    }

    @Override
    public boolean getPropertyBooleanValue(YukonRoleProperty property,
            LiteYukonUser user) throws UserNotInRoleException {
        Boolean booleanValue = getConvertedValue(property, user, Boolean.class);
        if (booleanValue == null) {
            return false;
        } else {
            return booleanValue.booleanValue();
        }
    }

    @Override
    public <E extends Enum<E>> E getPropertyEnumValue(
            YukonRoleProperty property, Class<E> enumClass, LiteYukonUser user)
            throws UserNotInRoleException {
        E convertedValue = getConvertedValue(property, user, enumClass);
        return convertedValue;
    }

    @Override
    public float getPropertyFloatValue(YukonRoleProperty property,
            LiteYukonUser user) throws UserNotInRoleException {
        Number propertyNumberValue = getPropertyNumberValue(property, user);
        
        return propertyNumberValue.floatValue();
    }
    
    @Override
    public int getPropertyIntegerValue(YukonRoleProperty property,
            LiteYukonUser user) throws UserNotInRoleException {
        Number propertyNumberValue = getPropertyNumberValue(property, user);
        
        return propertyNumberValue.intValue();
    }
    
    @Override
    public double getPropertyDoubleValue(YukonRoleProperty property,
            LiteYukonUser user) throws UserNotInRoleException {
        Number propertyNumberValue = getPropertyNumberValue(property, user);
        
        return propertyNumberValue.doubleValue();
    }
    
    @Override
    public long getPropertyLongValue(YukonRoleProperty property,
            LiteYukonUser user) throws UserNotInRoleException {
        Number propertyNumberValue = getPropertyNumberValue(property, user);
        
        return propertyNumberValue.longValue();
    }

    public Number getPropertyNumberValue(YukonRoleProperty property,
            LiteYukonUser user) throws UserNotInRoleException {
        Number convertedValue = getConvertedValue(property, user, Number.class);
        if (convertedValue == null) {
            return 0;
        } else {
            return convertedValue;
        }
    }

    @Override
    public String getPropertyStringValue(YukonRoleProperty property,
            LiteYukonUser user) throws UserNotInRoleException {
        Object convertedValue = getPropertyObjectValue(property, user);
        if (convertedValue == null) {
            return "";
        } else {
            return convertedValue.toString();
        }
    }
    
    public Object getPropertyObjectValue(YukonRoleProperty property,
            LiteYukonUser user) throws UserNotInRoleException {
        Object convertedValue = getConvertedValue(property, user, Object.class);
        return convertedValue;
    }

    public String getPropertyValue(YukonRoleProperty property,
            LiteYukonUser user) throws UserNotInRoleException {
    	totalDbHits.incrementAndGet();
        if (property.getRole().getCategory().isSystem()) {
            String result = getGlobalPropertyValue(property);
            return result;
        }
        Validate.notNull(user, "user can only be null when requesting System properties");
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("select 1 isUser, value, -99999999 + userroleid"); // 3rd column is a number smaller than any groupid
        sql.append("from YukonUserRole");
        sql.append("where UserId = ").appendArgument(user.getUserID());
        sql.append("  and rolepropertyid = ").appendArgument(property.getPropertyId());
        sql.append("union");
        sql.append("select 0 isUser, value, ygr.groupid");
        sql.append("from YukonGroupRole ygr");
        sql.append("  join YukonUserGroup yug on ygr.GroupId = yug.GroupId");
        sql.append("where yug.UserId = ").appendArgument(user.getUserID());
        sql.append("  and ygr.RolePropertyId = ").appendArgument(property.getPropertyId());
        sql.append("order by 3"); // this is to mimic the old behavior
        
        List<UserGroupPropertyValue> values = simpleJdbcTemplate.query(sql.getSql(), userGroupPropertyValue, sql.getArguments());
        
        return processReturnedValues(property, values, user);
    }

    public String processReturnedValues(YukonRoleProperty property,
            List<UserGroupPropertyValue> values, LiteYukonUser user) {
        if (values.isEmpty()) {
            if (!checkRole(property.getRole(), user)) {
                UserNotInRoleException userNotInRoleException = new UserNotInRoleException(property, user);
                // the following exception should be removed before 4.3 is released
                // let's see if this is in the exception list
                if (propertyExceptions.contains(property)) {
                    // must be a special case
                    log.warn("handling UserNotInRoleException for property on exception list", userNotInRoleException);
                    return null;
                } else {
                    throw userNotInRoleException;
                }
            }
            // if we got here, we know the user has this property's role
            // but nothing has ever inserted a row, this usually happens
            // after new properties are added to an existing system
            // returning null here will cause the default to be looked up
            if (log.isDebugEnabled()) {
                log.debug("got no role property values of " + property + " for " + user);
            }
            return null;
        } else if (values.size() == 1) {
            // this is the best case scenario, we don't even need to worry about whether
            // it was a group or user property that was returned
            // if the value happens to be "(none)" or null, it will be handled in the getConvertedValue method
            String value = values.get(0).value;
            if (log.isDebugEnabled()) {
                String type = values.get(0).isUser ? "user" : "group";
                log.debug("got one " + type + " role property value of " + property + " for " + user + ": " + value);
            }
            return value;
        } else {
            // the only way this can happen is if the user has exactly one user property
            // and zero or one group property, any other combination is invalid (and the 0 group case is somewhat sketchy)
            int userCount = 0;
            int groupCount = 0;
            UserGroupPropertyValue lastUserValue = null;
            UserGroupPropertyValue firstValue = null;
            for (UserGroupPropertyValue userGroupPropertyValue : values) {
                if (userGroupPropertyValue.isUser) {
                    lastUserValue = userGroupPropertyValue;
                    userCount++;
                } else {
                    groupCount++;
                }
                if (firstValue == null) {
                    firstValue = userGroupPropertyValue;
                }
            }
            if (userCount == 1 && groupCount <= 1) {
                // if the value happens to be "(none)" or null, it will be handled in the getConvertedValue method
                if (log.isDebugEnabled()) {
                    log.debug("got two role property values (using user) of " + property + " for " + user + ": " + lastUserValue.value);
                }
                return lastUserValue.value;
            } else {
                BadConfigurationException configurationException = new BadConfigurationException("Invalid role property combination found of " + property + " for " + user + " (userCount=" + userCount + ", groupCount=" + groupCount + ")");
                // the following exception should be removed before 4.3 is released
                if (allowRoleConflicts) {
                    log.warn("handling role conflict exception because ROLE_PROPERTY_CONFLICTS_ALLOWED is set", configurationException);
                    if (log.isDebugEnabled()) {
                        log.debug("got multiple role property values (using user) of " + property + " for " + user + ", returning first value (which happens to be a " + (firstValue.isUser ? "user" : "group") + " value): " + firstValue.value);
                    }
                    return firstValue.value;
                }
                throw configurationException;
            }
        }
    }

    @Override
    public Set<YukonRoleCategory> getRoleCategoriesForUser(LiteYukonUser user) {
        EnumSet<YukonRoleCategory> result = EnumSet.noneOf(YukonRoleCategory.class);
        Set<YukonRole> rolesForUser = getRolesForUser(user);
        for (YukonRole yukonRole : rolesForUser) {
            result.add(yukonRole.getCategory());
        }
        return Collections.unmodifiableSet(result);
    }
    
    @Override
    public Set<YukonRole> getRolesForUser(LiteYukonUser user) {
        Set<YukonRole> cachedRoleSet = userRoleCache.get(user.getUserID());
        if (cachedRoleSet != null) {
            if (log.isDebugEnabled()) {
                log.debug("found roles in cache for " + user + ": " + StringUtils.join(cachedRoleSet, ", "));
            }
            return cachedRoleSet;
        }
        Set<YukonRole> rolesForUserFromDatabase = getRolesForUserFromDatabase(user);
        userRoleCache.put(user.getUserID(), rolesForUserFromDatabase);
        if (log.isDebugEnabled()) {
            log.debug("found roles for " + user + ": " + StringUtils.join(rolesForUserFromDatabase, ", "));
        }
        return rolesForUserFromDatabase;
    }

    public Set<YukonRole> getRolesForUserFromDatabase(LiteYukonUser user) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("select roleid");
        sql.append("from YukonUserRole");
        sql.append("where UserId = ").appendArgument(user.getUserID());
        sql.append("union");
        sql.append("select roleid");
        sql.append("from YukonGroupRole ygr");
        sql.append("  join YukonUserGroup yug on ygr.GroupId = yug.GroupId");
        sql.append("where yug.UserId = ").appendArgument(user.getUserID());
        
        List<Integer> roleIdList = simpleJdbcTemplate.query(sql.getSql(), new IntegerRowMapper(), sql.getArguments());
        
        EnumSet<YukonRole> result = EnumSet.noneOf(YukonRole.class);
        
        for (Integer roleId : roleIdList) {
            try {
                YukonRole role = YukonRole.getForId(roleId);
                result.add(role);
            } catch (IllegalArgumentException e) {
                log.warn("Database contains an unknown role: " + roleId);
            }
        }
        
        return Collections.unmodifiableSet(result);
    }

    @Override
    public void verifyCategory(YukonRoleCategory category, LiteYukonUser user)
            throws NotAuthorizedException {
        if (!checkCategory(category, user)) throw NotAuthorizedException.category(user, category);
    }

    @Override
    public void verifyAnyProperties(LiteYukonUser user, YukonRoleProperty firstProperty,
    		YukonRoleProperty... otherProperties) throws NotAuthorizedException {
    	
        if (!checkAnyProperties(user, firstProperty, otherProperties)) {
        	throw NotAuthorizedException.atLeastOneTrueProperty(user, 
        			ImmutableList.of(firstProperty, otherProperties).toArray(
        																new YukonRoleProperty[]{}));
        }
    }
    
    @Override
    public void verifyProperty(YukonRoleProperty property, LiteYukonUser user)
            throws NotAuthorizedException {
        if (!checkProperty(property, user)) throw NotAuthorizedException.trueProperty(user, property);
    }
    
    @Override
    public void verifyFalseProperty(YukonRoleProperty property, LiteYukonUser user)
    throws NotAuthorizedException {
        if (!checkFalseProperty(property, user)) throw NotAuthorizedException.trueProperty(user, property);
    }
    
    @Override
    public void verifyRole(YukonRole role, LiteYukonUser user)
            throws NotAuthorizedException {
        if (!checkRole(role, user)) throw NotAuthorizedException.role(user, role);
    }
    
    /**
     * This is public but not in the interface. Designed to be used by other 
     * role property DAO implementations.
     */
    public ImmutableMap<YukonRoleProperty, Object> getDefaultValueLookup() {
        return defaultValueLookup;
    }
    
    @Autowired
    public void setSimpleJdbcTemplate(SimpleJdbcTemplate simpleJdbcTemplate) {
        this.simpleJdbcTemplate = simpleJdbcTemplate;
    }

    @Autowired
    public void setConfigurationSource(ConfigurationSource configurationSource) {
        this.configurationSource = configurationSource;
    }
    
    @ManagedAttribute
    public long getTotalAccesses() {
		return totalAccesses.get();
	}
    
    @ManagedAttribute
    public long getTotalCacheHits() {
		return totalCacheHits.get();
	}
    
    @ManagedAttribute
    public long getTotalDbHits() {
		return totalDbHits.get();
	}
    
    @ManagedAttribute
    public long getPropertyCacheSize() {
    	return convertedValueCache.size();
    }
    
    @ManagedAttribute
    public long getRoleCacheSize() {
    	return userRoleCache.size();
    }
}
