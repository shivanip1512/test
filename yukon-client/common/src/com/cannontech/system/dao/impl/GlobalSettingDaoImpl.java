package com.cannontech.system.dao.impl;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Optional;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.Logger;
import org.jdom2.JDOMException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.exception.NotAuthorizedException;
import com.cannontech.common.util.LeastRecentlyUsedCacheMap;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.database.YukonJdbcTemplate;
import com.cannontech.database.YukonResultSet;
import com.cannontech.database.YukonRowMapper;
import com.cannontech.encryption.CryptoException;
import com.cannontech.message.dispatch.message.DatabaseChangeEvent;
import com.cannontech.message.dispatch.message.DbChangeCategory;
import com.cannontech.system.GlobalSettingCryptoUtils;
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
    private final LeastRecentlyUsedCacheMap<GlobalSettingType, GlobalSetting> cache = new LeastRecentlyUsedCacheMap<>(10000);

    @Autowired private YukonJdbcTemplate yukonJdbcTemplate;

    @Override
    public String getString(GlobalSettingType type) {
        Object convertedValue = getConvertedValue(type, Object.class);
        if (convertedValue == null) {
            log.debug("Null setting found for  " + type + " using empty string.");
            return "";
        } else {
            return convertedValue.toString();
        }
    }
    
    @Override
    public Optional<String> getOptionalString(GlobalSettingType type) {
        return Optional.ofNullable(getConvertedValue(type, Object.class))
                       .map(Object::toString);
    }
    
    @Override
    public String getString(GlobalSettingType type, String defaultValue) {
        String value = getString(type);
        return StringUtils.isEmpty(value) ? defaultValue : value;
    }

    @Override
    public boolean getBoolean(GlobalSettingType type) {
        Boolean value = getNullableBoolean(type);
        if (value == null) {
            log.debug("Null or empty setting found for  " + type + " using false.");
            return false;
        }
        return value;
    }

    private Boolean getNullableBoolean(GlobalSettingType setting) {
        return getConvertedValue(setting, Boolean.class);
    }

    @Override
    public void verifySetting(GlobalSettingType type) throws NotAuthorizedException {
        if (!getBoolean(type)) {
            throw new NotAuthorizedException("User not authorized to view this page.");
        }
    }

    @Override
    public Integer getNullableInteger(GlobalSettingType type) {
        return getConvertedValue(type, Integer.class);
    }

    @Override
    public int getInteger(GlobalSettingType type) {
        Integer convertedValue = getNullableInteger(type);
        if (convertedValue == null) {
            log.debug("Null or empty setting found for  " + type + " using 0.");
            return 0;
        } else {
            return convertedValue;
        }
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
                setting = new GlobalSetting(type, type.getDefaultValue());
            }
            cache.put(type, setting);
            if (log.isDebugEnabled()) {
                log.debug("Updating cache for " + type);
            }
        } else if(log.isDebugEnabled()) {
            log.debug("Cache hit for " + type);
        }

        return new GlobalSetting(setting);
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
    public boolean hasDatabaseEntry(GlobalSettingType setting) {
        return findSetting(setting) != null;
    }
    
    @Override
    public void valueChanged() {
        log.debug("Removing " +  cache.size() + " values from the Global Settings Cache");
        cache.clear();
    }

    @Override
    public boolean isDbChangeForSetting(DatabaseChangeEvent event, GlobalSettingType globalSettingType) {
        return (event.getChangeCategory() == DbChangeCategory.GLOBAL_SETTING && 
                Integer.valueOf(event.getPrimaryKey()).equals(getSetting(globalSettingType).getId()));
    }
    
    private final YukonRowMapper<GlobalSetting> settingMapper = new YukonRowMapper<GlobalSetting>() {
        @Override
        public GlobalSetting mapRow(YukonResultSet rs) throws SQLException {
            GlobalSettingType type = rs.getEnum(("Name"), GlobalSettingType.class);

            Object value = rs.getObjectOfInputType("Value", type.getType());
            if (value != null && type.isSensitiveInformation()) {
                try {
                    if (GlobalSettingCryptoUtils.isEncrypted((String) value)) {
                        value = GlobalSettingCryptoUtils.decryptValue((String) value);
                    }

                } catch (CryptoException | IOException | JDOMException | DecoderException e) {
                    value = type.getDefaultValue();
                    log.error("Unable to decrypt value for setting " + type + ". Using the default value");
                }
            }

            GlobalSetting setting = new GlobalSetting(type, value);
            setting.setId(rs.getInt("GlobalSettingId"));
            setting.setComments(rs.getString("Comments"));
            setting.setLastChanged(rs.getInstant("LastChangedDate"));

            return setting;
        }
    };
}