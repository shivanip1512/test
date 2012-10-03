package com.cannontech.system.dao.impl;

import org.apache.commons.lang.Validate;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.exception.NotAuthorizedException;
import com.cannontech.common.util.LeastRecentlyUsedCacheMap;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.core.roleproperties.InputTypeFactory;
import com.cannontech.database.YukonJdbcTemplate;
import com.cannontech.system.BadSettingTypeException;
import com.cannontech.system.YukonSetting;
import com.cannontech.system.dao.YukonSettingsDao;

/**
 * The class handles Yukon System wide settings. 
 * 
 * These were previously associated with Yukon Grp role properties.
 * 
 * 
 * This is primarily intended for reading the settings and not for updating settings.
 * To update Yukon settings there is YukonSettingsUpdater to avoid circular dependencies
 * 
 * Taken From RolePropertyDaoImpl.java:
 * This class is designed to have a single dependency on a JdbcTemplate. Any other
 * dependency could cause issues because this class is needed early in the boot process.
 * This presents a problem because this class cache property values and thus must
 * know when to clear its cache. Normally this would be implemented by creating
 * a dependency on the AsyncDynamicDataSource. Instead, this class has a partner object
 * that will notify this class of any changes.
 * This creates the following dependency graph:

 * <pre>
 *    DispatchConnection --> YukonSettingsDaoImpl --> DataSource
 *             ^                 ^
 *             |                 |
 *    YukonSettingChangeHelper --|
 * </pre>
 * 
 */
public class YukonSettingsDaoImpl implements YukonSettingsDao {
    
    private Logger log = YukonLogManager.getLogger(YukonSettingsDaoImpl.class);
    private LeastRecentlyUsedCacheMap<YukonSetting, Object> cache = new LeastRecentlyUsedCacheMap<YukonSetting, Object>(10000);
    private final Object NULL_CACHE_VALUE = new Object();
    
    @Autowired private YukonJdbcTemplate yukonJdbcTemplate;

    @Override
    public String getSettingStringValue(YukonSetting setting) {
        Object convertedValue = getConvertedValue(setting, Object.class);
        if (convertedValue == null) {
            return "";
        } else {
            return convertedValue.toString();
        }
    }

    @Override
    public Boolean getSettingBooleanValue(YukonSetting setting) {
        return getConvertedValue(setting, Boolean.class);
    }

    @Override
    public boolean checkSetting(YukonSetting setting) {
        Boolean value = getSettingBooleanValue(setting);
        if (value == null) {
            return false;
        }
        return value;
    }

    @Override
    public void verifySetting(YukonSetting setting) throws NotAuthorizedException {
        if (!checkSetting(setting)) throw new NotAuthorizedException("User not authorized to view this page.");
    }

    @Override
    public Integer getSettingIntegerValue(YukonSetting setting) {
        return getConvertedValue(setting, Integer.class);
    }

    @Override
    public <E extends Enum<E>> E getSettingEnumValue(YukonSetting setting, Class<E> enumClass) {
        return getConvertedValue(setting, enumClass);
    }

    /**
     * @param <T>
     * @param setting
     * @param returnType the type to convert to, can be Object, otherwise must be compatible with setting
     * @return the converted value or the default, will only return null if the default was null
     */
    private <T> T getConvertedValue(YukonSetting setting, Class<T> returnType) {

        if (log.isDebugEnabled()) {
            log.debug("Getting converted value of " + setting + " as " + returnType.getSimpleName());
        }
        Validate.isTrue(returnType.isAssignableFrom(setting.getType().getTypeClass()), "can't convert " + setting + " to " + returnType);

        // check cache (using a special value to allow get to be used to check containsValue)
        Object cachedValue = cache.get(setting);
        if (cachedValue != null) {
            if (cachedValue == NULL_CACHE_VALUE) {
                cachedValue = null;
            }
            log.debug("Cache hit for " + setting);
            return returnType.cast(cachedValue);
        }

        // We didn't find the entry in the cache, so we'll need to retrieve it from the database.
        String stringValue = findSettingValue(setting);
        Object convertedValue = convertSettingValue(setting, stringValue);
        if (convertedValue == null) {
            log.debug("ConvertedValue was null for "+ setting.name() +", using default");
            convertedValue = setting.getDefaultValue();
        }
        if (log.isDebugEnabled()) {
            log.debug("Returning: " + convertedValue);
        }
        T result = returnType.cast(convertedValue);
        if (convertedValue == null) {
            convertedValue = NULL_CACHE_VALUE;
        }
        log.debug("Updating cache for " + setting);
        cache.put(setting, convertedValue);
        return result;
    }

    private Object convertSettingValue(YukonSetting setting, String value) {
        try {
            return InputTypeFactory.convertPropertyValue(setting.getType(), value);
        } catch (Exception e) {
            throw new BadSettingTypeException(setting, value, e);
        }
    }

    private String findSettingValue(YukonSetting setting) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT Value");
        sql.append("FROM YukonSetting");
        sql.append("WHERE Name").eq(setting.name());
        
        String value = null;
        try {
            value = yukonJdbcTemplate.queryForString(sql);
        } catch (EmptyResultDataAccessException e) {
            log.warn("Setting missing from the database: " + setting.name());
        }
        
        if (log.isDebugEnabled()) {
            log.debug("Got one setting value of " + setting);
        }

        return value;
    }

    public void clearCache() {
        log.debug("Removing about " +  cache.size() + " values from the Yukon Settings Cache");
        cache.clear();
    }
}
