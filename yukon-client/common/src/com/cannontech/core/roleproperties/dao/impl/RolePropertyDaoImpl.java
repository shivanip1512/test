package com.cannontech.core.roleproperties.dao.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;

import javax.annotation.PostConstruct;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.jmx.export.annotation.ManagedAttribute;
import org.springframework.jmx.export.annotation.ManagedResource;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.config.ConfigurationSource;
import com.cannontech.common.exception.BadConfigurationException;
import com.cannontech.common.exception.NotAuthorizedException;
import com.cannontech.common.util.LeastRecentlyUsedCacheMap;
import com.cannontech.common.util.SqlFragmentSource;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.core.roleproperties.AccessLevel;
import com.cannontech.core.roleproperties.BadPropertyTypeException;
import com.cannontech.core.roleproperties.CapControlCommandsAccessLevel;
import com.cannontech.core.roleproperties.HierarchyPermissionLevel;
import com.cannontech.core.roleproperties.InputTypeFactory;
import com.cannontech.core.roleproperties.NotInRoleException;
import com.cannontech.core.roleproperties.RoleGroupNotInRoleException;
import com.cannontech.core.roleproperties.UserNotInRoleException;
import com.cannontech.core.roleproperties.YukonRole;
import com.cannontech.core.roleproperties.YukonRoleCategory;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.core.roleproperties.dao.RolePropertyDao;
import com.cannontech.database.TypeRowMapper;
import com.cannontech.database.YukonJdbcTemplate;
import com.cannontech.database.data.lite.LiteYukonGroup;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.google.common.base.Predicate;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

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
    private final static Logger log = YukonLogManager.getLogger(RolePropertyDaoImpl.class);

    @Autowired private YukonJdbcTemplate jdbcTemplate;
    @Autowired private ConfigurationSource configurationSource;
    
    private ImmutableMap<YukonRoleProperty, Object> defaultValueLookup;
    private LeastRecentlyUsedCacheMap<PropertyTuple, Object> convertedValueCache =
            new LeastRecentlyUsedCacheMap<PropertyTuple, Object>(10000);
    private final Object NULL_CACHE_VALUE = new Object();
    private LoadingCache<Integer, Set<YukonRole>> userRoleCache =
            CacheBuilder.newBuilder().maximumSize(1000).build(new CacheLoader<Integer, Set<YukonRole>>(){
                @Override
                public Set<YukonRole> load(Integer userId) throws Exception {
                    return getRolesForUserFromDatabase(userId);
                }});
        
    private Set<YukonRoleProperty> propertyExceptions = EnumSet.noneOf(YukonRoleProperty.class);
    private boolean allowRoleConflicts = false;
    private ImmutableSet<YukonRoleProperty> missingProperties;
    
    // statistics
    private AtomicLong totalAccesses = new AtomicLong(0);
    private AtomicLong totalCacheHits = new AtomicLong(0);
    private AtomicLong totalDbHits = new AtomicLong(0);
    
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
        
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT RolePropertyId, RoleId, DefaultValue");
        sql.append("FROM YukonRoleProperty");
        jdbcTemplate.query(sql, new RowCallbackHandler() {
            @Override
            public void processRow(ResultSet rs) throws SQLException {
                int rolePropertyId = rs.getInt("RolePropertyId");
                int roleId = rs.getInt("RoleId");
                String defaultValue = rs.getString("DefaultValue");

                YukonRoleProperty roleProperty;
                try {
                    roleProperty = YukonRoleProperty.getForId(rolePropertyId);
                } catch (IllegalArgumentException e) {
                    log.warn("Database contains an unknown role property: " + rolePropertyId);
                    return;
                }

                unseenProperties.remove(roleProperty);
                try {
                    YukonRole role = YukonRole.getForId(roleId);
                    if (role != roleProperty.getRole()) {
                        log.warn("Property " + roleProperty + " is incorrectly mapped to " + role + " (should be "
                            + roleProperty.getRole() + ")");
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
                    log.error("Database contains an illegal default value for " + roleProperty
                        + " (will be treated as null): " + defaultValue);
                }
            }
        });
        
        defaultValueLookup = builder.build();
        missingProperties = ImmutableSet.copyOf(unseenProperties);
        
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
        userRoleCache.invalidateAll();
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
    public boolean checkLevel(YukonRoleProperty property, HierarchyPermissionLevel minLevel, LiteYukonUser user) {
        try {
            HierarchyPermissionLevel level = getPropertyEnumValue(property, HierarchyPermissionLevel.class, user);
            return level.grantAccess(minLevel);
        } catch (UserNotInRoleException e) {
            return false;
        }
    }
    
    @Override
    public boolean checkLevel(YukonRoleProperty property, AccessLevel minLevel, LiteYukonUser user) {
        try {
            AccessLevel level = getPropertyEnumValue(property, AccessLevel.class, user);
            return level.grantAccess(minLevel);
        } catch (UserNotInRoleException e) {
            return false;
        }
    }
    
    @Override
    public boolean checkLevel(YukonRoleProperty property, CapControlCommandsAccessLevel level, LiteYukonUser user) {
        try {
            CapControlCommandsAccessLevel userLevel = getPropertyEnumValue(property, CapControlCommandsAccessLevel.class, user);
            return userLevel.grantAccess(level);
        } catch (UserNotInRoleException e) {
            return false;
        }
    }
    
    @Override
    public boolean checkAnyLevel(YukonRoleProperty property, CapControlCommandsAccessLevel[] levels, LiteYukonUser user) {
        try {
            CapControlCommandsAccessLevel userLevel = getPropertyEnumValue(property, CapControlCommandsAccessLevel.class, user);
            for (CapControlCommandsAccessLevel level : levels) {
                if (userLevel.grantAccess(level)) {
                    return true;
                }
            }
            return false;
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
    public boolean checkAllProperties(LiteYukonUser user, YukonRoleProperty firstProperty,
            YukonRoleProperty... otherProperties) {
        Iterable<YukonRoleProperty> properties = Iterables.concat(ImmutableList.of(firstProperty),
            ImmutableList.copyOf(otherProperties));
        return checkAllProperties(user, properties);
    }

    @Override
    public boolean checkAllProperties(LiteYukonUser user, Iterable<YukonRoleProperty> properties) {
        boolean allAreCompatible = Iterables.all(properties, new IsCheckPropertyCompatiblePredicate());
        Validate.isTrue(allAreCompatible, "at least one of " + properties +" is not compatible with a check method");

        for (YukonRoleProperty property : properties) {
            if (!checkProperty(property, user)) {
                return false;
            }
        }

        return true;
    }

    @Override
    public boolean checkAnyProperties(LiteYukonUser user, YukonRoleProperty firstProperty,
            YukonRoleProperty... otherProperties) {
        Iterable<YukonRoleProperty> properties = Iterables.concat(ImmutableList.of(firstProperty),
            ImmutableList.copyOf(otherProperties));
        return checkAnyProperties(user, properties);
    }

    @Override
    public boolean checkAnyProperties(LiteYukonUser user, Iterable<YukonRoleProperty> properties) {
        boolean allAreCompatible = Iterables.all(properties, new IsCheckPropertyCompatiblePredicate());
        Validate.isTrue(allAreCompatible, "at least one of " + properties +" is not compatible with a check method");

        for (YukonRoleProperty property : properties) {
            if (checkProperty(property, user)) {
                return true;
            }
        }

        return false;
    }

    @Override
    public boolean checkFalseProperty(YukonRoleProperty property, LiteYukonUser user) {
        try {
            UserPropertyTuple userPropertyTuple = new UserPropertyTuple(user, property);
            Boolean booleanValue = getConvertedValue(userPropertyTuple, Boolean.class);
            if (booleanValue == null) {
                return false;
            }
            return !booleanValue.booleanValue();
        } catch (UserNotInRoleException e) {
            return false;
        }
    }
    
    private boolean checkRole(PropertyTuple propertyTuple) {
        if (propertyTuple instanceof UserPropertyTuple) {
            UserPropertyTuple userPropertyTuple = (UserPropertyTuple) propertyTuple;
            Set<YukonRole> rolesForUser = userRoleCache.getUnchecked(userPropertyTuple.getUserId());
            return rolesForUser.contains(userPropertyTuple.getYukonRoleProperty().getRole());
        } else if (propertyTuple instanceof RoleGroupPropertyTuple) {
            RoleGroupPropertyTuple roleGroupPropertyTuple = (RoleGroupPropertyTuple) propertyTuple;
            return checkRoleForRoleGroupId(roleGroupPropertyTuple.getYukonRoleProperty().getRole(),
                roleGroupPropertyTuple.getRoleGroupId());
        }
        return false;
    }
    
    @Override
    public boolean checkRole(YukonRole role, LiteYukonUser user) {
        Set<YukonRole> rolesForUser = userRoleCache.getUnchecked(user.getUserID());
        boolean result = rolesForUser.contains(role);
        return result;
    }
    
    @Override
    public boolean checkRoleForRoleGroupId(YukonRole role, int roleGroupId) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT DISTINCT RoleId");
        sql.append("FROM YukonGroupRole YGR");
        sql.append("WHERE YGR.GroupId").eq(roleGroupId);
        
        List<Integer> roleIds = jdbcTemplate.query(sql, TypeRowMapper.INTEGER);
        
        return roleIds.contains(role.getRoleId());
    }
    
    private Object convertPropertyValue(YukonRoleProperty roleProperty, String value) throws BadPropertyTypeException {
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
    private <T> T getConvertedValue(PropertyTuple propertyTuple, Class<T> returnType) throws BadPropertyTypeException {
        totalAccesses.incrementAndGet();
        if (log.isDebugEnabled()) {
            log.debug("getting converted value of " + propertyTuple + " as " + returnType.getSimpleName());
        }
        Validate.isTrue(returnType.isAssignableFrom(propertyTuple.getYukonRoleProperty().getType().getTypeClass()),
            "can't convert " + propertyTuple.getYukonRoleProperty() + " to " + returnType);

        // check cache (using a special value to allow get to be used to check containsValue)
        Object cachedValue = convertedValueCache.get(propertyTuple);
        if (cachedValue != null) {
            if (cachedValue == NULL_CACHE_VALUE) {
                cachedValue = null;
            }
            log.debug("cache hit, returning: " + cachedValue);
            totalCacheHits.incrementAndGet();
            return returnType.cast(cachedValue);
        }
        
        // We didn't find the entry in the cache, so we'll need to retrieve it from the database.
        String stringValue = findPropertyValue(propertyTuple);
        Object convertedValue = convertPropertyValue(propertyTuple.getYukonRoleProperty(), stringValue);
        if (convertedValue == null) {
            log.debug("convertedValue was null, using default");
            convertedValue = defaultValueLookup.get(propertyTuple.getYukonRoleProperty());
        }
        if (log.isDebugEnabled()) {
            log.debug("returning: " + convertedValue);
        }
        T result = returnType.cast(convertedValue);
        if (convertedValue == null) {
            convertedValue = NULL_CACHE_VALUE;
        }
        convertedValueCache.put(propertyTuple, convertedValue);
        return result;
    }

    @Override
    public boolean getPropertyBooleanValue(YukonRoleProperty property, LiteYukonUser user) throws UserNotInRoleException {
        UserPropertyTuple userPropertyTuple = new UserPropertyTuple(user, property);
        Boolean booleanValue = getConvertedValue(userPropertyTuple, Boolean.class);
        if (booleanValue == null) {
            return false;
        }
        return booleanValue.booleanValue();
    }

    @Override
    public <E extends Enum<E>> E getPropertyEnumValue(YukonRoleProperty property, Class<E> enumClass, LiteYukonUser user)
    throws UserNotInRoleException {
        UserPropertyTuple userPropertyTuple = new UserPropertyTuple(user, property);
        E convertedValue = getConvertedValue(userPropertyTuple, enumClass);
        return convertedValue;
    }

    @Override
    public int getPropertyIntegerValue(YukonRoleProperty property, LiteYukonUser user) throws UserNotInRoleException {
        Number propertyNumberValue = getPropertyNumberValue(property, user);
        
        return propertyNumberValue.intValue();
    }

    @Override
    public long getPropertyLongValue(YukonRoleProperty property, LiteYukonUser user) throws UserNotInRoleException {
        Number propertyNumberValue = getPropertyNumberValue(property, user);
        
        return propertyNumberValue.longValue();
    }

    public Number getPropertyNumberValue(YukonRoleProperty property, LiteYukonUser user) throws UserNotInRoleException {
        UserPropertyTuple userPropertyTuple = new UserPropertyTuple(user, property);
        Number convertedValue = getConvertedValue(userPropertyTuple, Number.class);
        if (convertedValue == null) {
            return 0;
        }
        return convertedValue;
    }
    
    @Override
    public String getPropertyStringValue(YukonRoleProperty property, LiteYukonUser user) throws UserNotInRoleException {
        UserPropertyTuple userPropertyTuple = new UserPropertyTuple(user, property);
        Object convertedValue = getPropertyObjectValue(userPropertyTuple);
        if (convertedValue == null) {
            return "";
        }
        return convertedValue.toString();
    }

    private Object getPropertyObjectValue(PropertyTuple propertyTuple) throws UserNotInRoleException {
        Object convertedValue = getConvertedValue(propertyTuple, Object.class);
        return convertedValue;
    }

    private String findPropertyValue(PropertyTuple propertyTuple) throws UserNotInRoleException {
        totalDbHits.incrementAndGet();

        SqlFragmentSource sql = propertyTuple.getRolePropertyValueLookupQuery();
        List<String> values = jdbcTemplate.query(sql, TypeRowMapper.STRING);
        
        return processReturnedValues(propertyTuple, values);
    }

    private String processReturnedValues(PropertyTuple propertyTuple, List<String> values) {
        if (values.isEmpty()) {
            if (!checkRole(propertyTuple)) {
                NotInRoleException notInRoleException = propertyTuple.notInRoleException();

                // the following exception should be removed before 4.3 is released
                // let's see if this is in the exception list
                if (propertyExceptions.contains(propertyTuple.getYukonRoleProperty())) {
                    // must be a special case
                    log.warn("handling UserNotInRoleException for property on exception list", notInRoleException);
                    return null;
                }

                throw notInRoleException;
            }
            
            // if we got here, we know the user has this property's role
            // but nothing has ever inserted a row, this usually happens
            // after new properties are added to an existing system
            // returning null here will cause the default to be looked up
            if (log.isDebugEnabled()) {
                log.debug("got no role property values of "+propertyTuple);
            }
            return null;
            
        } else if (values.size() == 1) {
            // this is the best case scenario, we don't even need to worry about whether
            // it was a group or user property that was returned
            // if the value happens to be "(none)" or null, it will be handled in the getConvertedValue method
            String value = values.get(0);
            if (log.isDebugEnabled()) {
                log.debug("got one group role property value of "+propertyTuple);
            }
            return value;
            
        } else {
            // the only way this can happen is if the user has exactly one user property
            // and zero or one group property, any other combination is invalid (and the 0 group case is somewhat sketchy)
            String firstValue = values.get(0);
            BadConfigurationException configurationException =
                    new BadConfigurationException("Invalid role property combination found of " + propertyTuple
                        + " (groupCount=" + values.size() + ")");
            if (allowRoleConflicts) {
                log.warn("handling role conflict exception because ROLE_PROPERTY_CONFLICTS_ALLOWED is set", configurationException);
                if (log.isDebugEnabled()) {
                    log.debug("got multiple role property values (using user) of " + propertyTuple
                        + ", returning first value (which happens to be a group  value): " + firstValue);
                }
                return firstValue;
            }
            throw configurationException;
        }
    }

    @Override
    public Set<YukonRoleCategory> getRoleCategoriesForUser(LiteYukonUser user) {
        EnumSet<YukonRoleCategory> result = EnumSet.noneOf(YukonRoleCategory.class);
        Set<YukonRole> rolesForUser = userRoleCache.getUnchecked(user.getUserID());
        for (YukonRole yukonRole : rolesForUser) {
            result.add(yukonRole.getCategory());
        }
        return Collections.unmodifiableSet(result);
    }

    /**
     * This method returns all the roles for a given user.  Keep in mind this method will not show duplicated roles by different groups..
     */
    private Set<YukonRole> getRolesForUserFromDatabase(int userId) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT RoleId");
        sql.append("FROM YukonGroupRole YGR");
        sql.append("  JOIN UserGroupToYukonGroupMapping UGYGM ON UGYGM.GroupId = YGR.GroupId");
        sql.append("  JOIN YukonUser YU ON YU.UserGroupId = UGYGM.UserGroupId");
        sql.append("WHERE YU.UserId").eq(userId);
        
        List<Integer> roleIdList = jdbcTemplate.query(sql, TypeRowMapper.INTEGER);
        
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
    public void verifyAnyProperties(LiteYukonUser user, YukonRoleProperty firstProperty,
    		YukonRoleProperty... otherProperties) throws NotAuthorizedException {
        if (!checkAnyProperties(user, firstProperty, otherProperties)) {
        	throw NotAuthorizedException.atLeastOneTrueProperty(user, Lists.asList(firstProperty, otherProperties).toArray(new YukonRoleProperty[]{}));
        }
    }
    
    @Override
    public void verifyProperty(YukonRoleProperty property, LiteYukonUser user) throws NotAuthorizedException {
        if (!checkProperty(property, user)) {
            throw NotAuthorizedException.trueProperty(user, property);
        }
    }
    
    @Override
    public void verifyLevel(YukonRoleProperty property, HierarchyPermissionLevel minLevel,  LiteYukonUser user) throws NotAuthorizedException {
        if (!checkLevel(property, minLevel, user)) {
            throw NotAuthorizedException.hierarchicalProperty(user, minLevel);
        }
    }

    @Override
    public void verifyRole(YukonRole role, LiteYukonUser user) throws NotAuthorizedException {
        if (!checkRole(role, user)) {
            throw NotAuthorizedException.role(user, role);
        }
    }

    @Override
    public ImmutableSet<YukonRoleProperty> getMissingProperties() {
        return missingProperties;
    }
    
    @Override
    public Number getPropertyNumberValue(LiteYukonGroup roleGroup, YukonRoleProperty property) throws RoleGroupNotInRoleException {
        RoleGroupPropertyTuple roleGroupPropertyTuple = new RoleGroupPropertyTuple(roleGroup, property);
        Number convertedValue = getConvertedValue(roleGroupPropertyTuple, Number.class);
        if (convertedValue == null) {
            return 0;
        }
        return convertedValue;
    }

    @Override
    public int getPropertyIntegerValue(LiteYukonGroup roleGroup, YukonRoleProperty property) throws RoleGroupNotInRoleException {
        Number propertyNumberValue = getPropertyNumberValue(roleGroup, property);
        
        return propertyNumberValue.intValue();
    }

    @Override
    public String getPropertyStringValue(LiteYukonGroup roleGroup, YukonRoleProperty property) throws RoleGroupNotInRoleException {
        RoleGroupPropertyTuple roleGroupPropertyTuple = new RoleGroupPropertyTuple(roleGroup, property);
        Object convertedValue = getPropertyObjectValue(roleGroupPropertyTuple);
        if (convertedValue == null) {
            return "";
        }
        return convertedValue.toString();
    }

    @Override
    public boolean getPropertyBooleanValue(LiteYukonGroup roleGroup, YukonRoleProperty property) throws RoleGroupNotInRoleException {
        RoleGroupPropertyTuple roleGroupPropertyTuple = new RoleGroupPropertyTuple(roleGroup, property);
        Boolean booleanValue = getConvertedValue(roleGroupPropertyTuple, Boolean.class);
        if (booleanValue == null) {
            return false;
        }
        return booleanValue.booleanValue();
    }
    
    /**
     * This is public but not in the interface. Designed to be used by other 
     * role property DAO implementations.
     */
    public ImmutableMap<YukonRoleProperty, Object> getDefaultValueLookup() {
        return defaultValueLookup;
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
