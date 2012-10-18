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
import com.cannontech.system.BadSettingTypeException;
import com.cannontech.system.GlobalSettingType;
import com.cannontech.system.dao.GlobalSettingDao;
import com.cannontech.system.model.GlobalSetting;

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
public class GlobalSettingDaoImpl implements GlobalSettingDao {
    
    private final Logger log = YukonLogManager.getLogger(GlobalSettingDaoImpl.class);
    private final LeastRecentlyUsedCacheMap<GlobalSettingType, GlobalSetting> cache = new LeastRecentlyUsedCacheMap<GlobalSettingType, GlobalSetting>(10000);
    
    @Autowired private YukonJdbcTemplate yukonJdbcTemplate;

    @Override
    public String getString(GlobalSettingType type) {
        Object convertedValue = getConvertedValue(type, Object.class);
        if (convertedValue == null) {
            return "";
        } else {
            return convertedValue.toString();
        }
    }

    @Override
    public Boolean getBoolean(GlobalSettingType type) {
        return getConvertedValue(type, Boolean.class);
    }

    @Override
    public boolean checkSetting(GlobalSettingType type) {
        Boolean value = getBoolean(type);
        if (value == null) {
            return false;
        }
        return value;
    }

    @Override
    public void verifySetting(GlobalSettingType type) throws NotAuthorizedException {
        if (!checkSetting(type)) throw new NotAuthorizedException("User not authorized to view this page.");
    }

    @Override
    public Integer getInteger(GlobalSettingType type) {
        return getConvertedValue(type, Integer.class);
    }

    @Override
    public <E extends Enum<E>> E getEnum(GlobalSettingType type, Class<E> enumClass) {
        return getConvertedValue(type, enumClass);
    }

    @Override
    public GlobalSetting getSetting(GlobalSettingType type) {
        GlobalSetting setting = cache.get(type);
        
        if (setting == null) {
            // Not in cache, Look in Db
            setting = findSetting(type);
            if (setting == null) {
                // Not in Db. Need to create one for cache
                setting = new GlobalSetting();
                setting.setType(type);
                setting.setValue(type.getDefaultValue());
            } 
            cache.put(type, setting);
            log.debug("Updating cache for " + type);
        } else {
            log.debug("Cache hit for " + type);
        }
        
        return setting;
    }

    /**
     * @param <T>
     * @param setting
     * @param returnType the type to convert to, can be Object, otherwise must be compatible with setting
     * @return the converted value or the default, will only return null if the default was null
     */
    private <T> T getConvertedValue(GlobalSettingType setting, Class<T> returnType) {
        return returnType.cast(getSetting(setting).getValue());
    }

    @Override
    public Object convertSettingValue(GlobalSettingType type, String value) {
        Object convertedValue;
        try {
            convertedValue = InputTypeFactory.convertPropertyValue(type.getType(), value);
        } catch (Exception e) {
            throw new BadSettingTypeException(type, value, e);
        }
        if (convertedValue == null) {
            log.debug("ConvertedValue was null for "+ type.name() +", using default.");
            convertedValue = type.getDefaultValue();
        }
        return convertedValue;
    }

    private GlobalSetting findSetting(GlobalSettingType setting) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT GlobalSettingId, Value, Name, Comments, LastChangedDate");
        sql.append("FROM GlobalSetting");
        sql.append("WHERE Name").eq(setting.name());
        
        GlobalSetting settingDb = null;
        try {
            settingDb = yukonJdbcTemplate.queryForObject(sql, settingMapper);
        } catch (EmptyResultDataAccessException e) {
            log.debug("Setting missing from the database: " + setting.name());
            return null;
        }

        log.debug("Found one setting value for " + setting.name());
        return settingDb;
    }
    
    @Override
    public void clearCache() {
        log.debug("Removing " +  cache.size() + " values from the Global Settings Cache");
        cache.clear();
    }
  
    private final YukonRowMapper<GlobalSetting> settingMapper = new YukonRowMapper<GlobalSetting>() {
        @Override
        public GlobalSetting mapRow(YukonResultSet rs) throws SQLException {
            GlobalSetting setting = new GlobalSetting();
            setting.setId(rs.getInt("GlobalSettingId"));
            GlobalSettingType type = rs.getEnum(("Name"), GlobalSettingType.class);
            setting.setType(type);
            String value = rs.getString("Value");
            if (value != null) {
                setting.setValue(InputTypeFactory.convertPropertyValue(type.getType(), value));
            }
            setting.setComments(rs.getString("Comments"));
            setting.setLastChanged(rs.getInstant("LastChangedDate"));
            
            return setting;
        }
    };
    
}