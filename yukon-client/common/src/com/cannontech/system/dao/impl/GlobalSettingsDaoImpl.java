package com.cannontech.system.dao.impl;

import java.sql.SQLException;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.exception.NotAuthorizedException;
import com.cannontech.common.util.LeastRecentlyUsedCacheMap;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.core.roleproperties.InputTypeFactory;
import com.cannontech.database.YukonJdbcTemplate;
import com.cannontech.database.YukonResultSet;
import com.cannontech.database.YukonRowMapper;
import com.cannontech.database.db.setting.GlobalSettingDb;
import com.cannontech.system.BadSettingTypeException;
import com.cannontech.system.GlobalSetting;
import com.cannontech.system.dao.GlobalSettingsDao;

/**
 * The class handles Yukon System wide settings. 
 * 
 * These were previously associated with Yukon Grp role properties.
 * 
 * This is primarily intended for reading the settings and not for updating settings.
 * To update Yukon settings there is GlobalSettingsUpdateDao to avoid circular dependencies
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
 *    DispatchConnection --> GlobalSettingsDaoImpl --> DataSource
 *             ^                 ^
 *             |                 |
 *    GlobalSettingChangeHelper --|
 * </pre>
 * 
 */
public class GlobalSettingsDaoImpl implements GlobalSettingsDao {
    
    private Logger log = YukonLogManager.getLogger(GlobalSettingsDaoImpl.class);
    private LeastRecentlyUsedCacheMap<GlobalSetting, GlobalSettingDb> cache = new LeastRecentlyUsedCacheMap<GlobalSetting, GlobalSettingDb>(10000);
    
    @Autowired private YukonJdbcTemplate yukonJdbcTemplate;

    @Override
    public String getString(GlobalSetting setting) {
        Object convertedValue = getConvertedValue(setting, Object.class);
        if (convertedValue == null) {
            return "";
        } else {
            return convertedValue.toString();
        }
    }

    @Override
    public Boolean getBoolean(GlobalSetting setting) {
        return getConvertedValue(setting, Boolean.class);
    }

    @Override
    public boolean checkSetting(GlobalSetting setting) {
        Boolean value = getBoolean(setting);
        if (value == null) {
            return false;
        }
        return value;
    }

    @Override
    public void verifySetting(GlobalSetting setting) throws NotAuthorizedException {
        if (!checkSetting(setting)) throw new NotAuthorizedException("User not authorized to view this page.");
    }

    @Override
    public Integer getInteger(GlobalSetting setting) {
        return getConvertedValue(setting, Integer.class);
    }

    @Override
    public <E extends Enum<E>> E getEnum(GlobalSetting setting, Class<E> enumClass) {
        return getConvertedValue(setting, enumClass);
    }

    @Override
    public GlobalSettingDb getSettingDb(GlobalSetting setting) {
        GlobalSettingDb settingDb = cache.get(setting);
        
        if (settingDb == null) {
            // Not in cache, Look in Db
            settingDb = findSetting(setting);
            if (settingDb == null) {
                // Not in Db. Need to create one for cache
                settingDb = new GlobalSettingDb(setting.name(),setting.getDefaultValue());
            } 
            cache.put(setting, settingDb);
            log.debug("Updating cache for " + setting);
        } else {
            log.debug("Cache hit for " + setting);
        }
        
        return settingDb;
    }

    /**
     * @param <T>
     * @param setting
     * @param returnType the type to convert to, can be Object, otherwise must be compatible with setting
     * @return the converted value or the default, will only return null if the default was null
     */
    private <T> T getConvertedValue(GlobalSetting setting, Class<T> returnType) {
        return returnType.cast(getSettingDb(setting).getValue());
    }

    private Object convertSettingValue(GlobalSetting setting, String value) {
        Object convertedValue;
        try {
            convertedValue = InputTypeFactory.convertPropertyValue(setting.getType(), value);
        } catch (Exception e) {
            throw new BadSettingTypeException(setting, value, e);
        }
        if (convertedValue == null) {
            log.debug("ConvertedValue was null for "+ setting.name() +", using default");
            convertedValue = setting.getDefaultValue();
        }
        return convertedValue;
    }

    private GlobalSettingDb findSetting(GlobalSetting setting) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT GlobalSettingId, Value, Name, LastChangedDate");
        sql.append("FROM GlobalSetting");
        sql.append("WHERE Name").eq(setting.name());
        
        GlobalSettingDb settingDb = null;
        try {
            settingDb = yukonJdbcTemplate.queryForObject(sql,settingMapper);
        } catch (EmptyResultDataAccessException e) {
            log.debug("Setting missing from the database: " + setting.name());
            return null;
        }

        if (settingDb == null) {
            log.debug("Setting missing from the database: " + setting.name());
            return null;
        } else {
            if (log.isDebugEnabled()) {
                log.debug("Found one setting value for " + setting.name());
            }
            return settingDb;
        } 
    }
    
    public void clearCache() {
        log.debug("Removing " +  cache.size() + " values from the Global Settings Cache");
        cache.clear();
    }
  
    private final YukonRowMapper<GlobalSettingDb> settingMapper = new YukonRowMapper<GlobalSettingDb>() {
        @Override
        public GlobalSettingDb mapRow(YukonResultSet rs) throws SQLException {
            
            GlobalSetting setting = rs.getEnum("Name", GlobalSetting.class);
            String valueStr = rs.getString("Value");
            
            Object value = convertSettingValue(setting,valueStr);
            
            GlobalSettingDb globalSettingDb = new GlobalSettingDb(setting.name(),value);
            globalSettingDb.setLastChangedDate(rs.getInstant("LastChangedDate"));
            globalSettingDb.setGlobalSettingId(rs.getInt("GlobalSettingId"));
            
            return globalSettingDb;
        }
    };
}
