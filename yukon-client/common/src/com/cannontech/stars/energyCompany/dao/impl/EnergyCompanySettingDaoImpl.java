package com.cannontech.stars.energyCompany.dao.impl;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.joda.time.Instant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.events.loggers.StarsEventLogService;
import com.cannontech.common.exception.NotAuthorizedException;
import com.cannontech.common.util.LeastRecentlyUsedCacheMap;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.core.dynamic.AsyncDynamicDataSource;
import com.cannontech.core.dynamic.DatabaseChangeEventListener;
import com.cannontech.core.roleproperties.InputTypeFactory;
import com.cannontech.database.FieldMapper;
import com.cannontech.database.SimpleTableAccessTemplate;
import com.cannontech.database.YNBoolean;
import com.cannontech.database.YukonJdbcTemplate;
import com.cannontech.database.YukonResultSet;
import com.cannontech.database.YukonRowMapper;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.database.incrementer.NextValueHelper;
import com.cannontech.message.DbChangeManager;
import com.cannontech.message.dispatch.message.DatabaseChangeEvent;
import com.cannontech.message.dispatch.message.DbChangeCategory;
import com.cannontech.message.dispatch.message.DbChangeType;
import com.cannontech.stars.energyCompany.ECSettingCacheKey;
import com.cannontech.stars.energyCompany.EnergyCompanySettingType;
import com.cannontech.stars.energyCompany.dao.EnergyCompanySettingDao;
import com.cannontech.stars.energyCompany.model.EnergyCompanySetting;
import com.cannontech.user.UserUtils;

public class EnergyCompanySettingDaoImpl implements EnergyCompanySettingDao {

    private final Logger log = YukonLogManager.getLogger(EnergyCompanySettingDaoImpl.class);
    private SimpleTableAccessTemplate<EnergyCompanySetting> insertTemplate;
    private final LeastRecentlyUsedCacheMap<ECSettingCacheKey, EnergyCompanySetting> cache = new LeastRecentlyUsedCacheMap<>(100);
    
    @Autowired private DbChangeManager dbChangeManager;
    @Autowired private NextValueHelper nextValueHelper;
    @Autowired private YukonJdbcTemplate yukonJdbcTemplate;
    @Autowired private StarsEventLogService starsEventLogService;
    @Autowired private AsyncDynamicDataSource asyncDynamicDataSource;

    private final static FieldMapper<EnergyCompanySetting> fieldMapper = new FieldMapper<EnergyCompanySetting>() {
        @Override
        public void extractValues(MapSqlParameterSource parameterHolder, EnergyCompanySetting setting) {
            parameterHolder.addValue("Name", setting.getType());
            parameterHolder.addValue("EnergyCompanyId", setting.getEnergyCompanyId());
            parameterHolder.addValue("Name", setting.getType());
            parameterHolder.addValue("Enabled", YNBoolean.valueOf(setting.isEnabled()));
            parameterHolder.addValue("Value", setting.getValue());
            parameterHolder.addValue("Comments", setting.getComments());
            parameterHolder.addValue("LastChangedDate", setting.getLastChanged());
        }

        @Override
        public Number getPrimaryKey(EnergyCompanySetting object) {
            return object.getId();
        }

        @Override
        public void setPrimaryKey(EnergyCompanySetting object, int value) {
            object.setId(value);
        }
    };

    @Override
    public EnergyCompanySetting getSetting(EnergyCompanySettingType type, int ecId) {
        EnergyCompanySetting setting = cache.get(ECSettingCacheKey.of(type, ecId));

        if (setting == null) {
            // Not in cache, Look in Db
            setting = findSetting(type, ecId);
            if (setting == null) {
                // Not in Db. Need to create one for cache
                setting = EnergyCompanySetting.getDefault(type, ecId);
            }
            cache.put(ECSettingCacheKey.of(type, ecId),setting);
            if (log.isDebugEnabled()) {
                log.debug("Updating cache for " + type);
            }
        } else if(log.isDebugEnabled()) {
            log.debug("Cache hit for " + type);
        }

        return new EnergyCompanySetting(setting);
    }

    @Override
    public String getString(EnergyCompanySettingType setting, int ecId) {
        Object convertedValue = getConvertedValue(setting, ecId, Object.class);
        if (convertedValue == null) {
            log.debug("Null setting found for  " + setting + " using empty string");
            return "";
        } else {
            return convertedValue.toString();
        }
    }

    @Override
    public int getInteger(EnergyCompanySettingType setting, int ecId) {
        Integer convertedValue = getConvertedValue(setting, ecId, Integer.class);
        if (convertedValue == null) {
            log.debug("Null or empty setting found for  " + setting + " using 0");
            return 0;
        } else {
            return convertedValue;
        }
    }

    @Override
    public <E extends Enum<E>> E getEnum(EnergyCompanySettingType setting, Class<E> enumClass, int ecId) {
        return getConvertedValue(setting, ecId, enumClass);
    }

    @Override
    public boolean getBoolean(EnergyCompanySettingType setting, int ecId) {
        Boolean value = getNullableBoolean(setting, ecId);
        if (value == null) {
            log.debug("Null or empty setting found for  " + setting + " using false");
            return false;
        } else {
            return value;
        }
    }

    @Override
    public boolean getFalseBoolean(EnergyCompanySettingType setting, int ecId) {
        Boolean value = getNullableBoolean(setting, ecId);
        if (value == null) {
            log.debug("Null or empty setting found for  " + setting + " using false");
            return false;
        }
        return !value.booleanValue();
    }

    private Boolean getNullableBoolean(EnergyCompanySettingType setting, int ecId) {
        return getConvertedValue(setting, ecId, Boolean.class);
    }

    @Override
    public void verifySetting(EnergyCompanySettingType setting, int ecId)
            throws NotAuthorizedException {
        if (!getBoolean(setting, ecId)) {
            throw new NotAuthorizedException("User not authorized to view this page.");
        }
    }

    @Override
    public void valueChanged() {
        log.debug("Removing values from the Energy Company Settings Cache");
        cache.clear();
    }

    private EnergyCompanySetting findSetting(EnergyCompanySettingType setting, int ecId) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT EnergyCompanySettingId, EnergyCompanyId, Name, Value, Enabled, Comments, LastChangedDate");
        sql.append("FROM EnergyCompanySetting");
        sql.append("WHERE Name").eq(setting.name());
        sql.append("AND EnergyCompanyId").eq(ecId);

        EnergyCompanySetting settingDb = null;
        try {
            settingDb = yukonJdbcTemplate.queryForObject(sql, settingMapper);
        } catch (EmptyResultDataAccessException e) {
            log.debug("Setting missing from the database: " + setting.name());
            return null;
        }

        log.debug("Found one setting value for " + setting.name());
        return settingDb;
    }

    private final YukonRowMapper<EnergyCompanySetting> settingMapper = new YukonRowMapper<EnergyCompanySetting>() {
        @Override
        public EnergyCompanySetting mapRow(YukonResultSet rs) throws SQLException {

            EnergyCompanySettingType type = rs.getEnum(("Name"), EnergyCompanySettingType.class);
            Object value = null;
            String valueStr = rs.getString("Value");
            if (valueStr != null) {
                value = InputTypeFactory.convertPropertyValue(type.getType(), valueStr);
            }

            EnergyCompanySetting setting = new EnergyCompanySetting();
            setting.setType(type);
            setting.setValue(value);
            setting.setEnergyCompanyId(rs.getInt("EnergyCompanyId"));
            setting.setId(rs.getInt("EnergyCompanySettingId"));
            setting.setEnabled(rs.getBooleanYN("Enabled"));
            setting.setComments(rs.getString("Comments"));
            setting.setLastChanged(rs.getInstant("LastChangedDate"));

            return setting;
        }
    };
    
    /**
     * @param <T>
     * @param returnType the type to convert to, can be Object, otherwise must be compatible with setting
     * @return the converted value or the default, will only return null if the default was null
     */
    private <T> T getConvertedValue(EnergyCompanySettingType setting, int ecId, Class<T> returnType) {
        return returnType.cast(getSetting(setting, ecId).getValue());
    }

    @Override
    public void updateSetting(EnergyCompanySetting setting, LiteYukonUser user, int ecId) {

        EnergyCompanySetting currentSetting = getSetting(setting.getType(), setting.getEnergyCompanyId());
        
        if (setting.isChanged(currentSetting)) {
            // Only update last changed date on value update
            if (setting.isValueChanged(currentSetting)) {
                setting.setLastChanged(Instant.now());
            }

            if (StringUtils.isBlank(setting.getComments())) {
                setting.setComments(null);
            }

            if (insertTemplate.saveWillUpdate(setting)) {
                insertTemplate.save(setting);
                dbChangeManager.processDbChange(DbChangeType.UPDATE,
                                                DbChangeCategory.ENERGY_COMPANY_SETTING,
                                                setting.getEnergyCompanyId());
            } else {
                insertTemplate.save(setting);
                dbChangeManager.processDbChange(DbChangeType.ADD,
                                                DbChangeCategory.ENERGY_COMPANY_SETTING,
                                                setting.getEnergyCompanyId());
            }
            
            Object value = setting.getValue();
            Object currentValue = currentSetting.getValue();
            String comments = setting.getComments();
            log.debug(setting.getType() + " updated. Previous value (" + currentValue + ") current value (" + value + "). Comments: (" + comments + ")");
            if (user == null) {
                user = UserUtils.getYukonUser();
            }
            starsEventLogService.energyCompanySettingUpdated(user, setting.getType(), ecId, value == null ? "" : value.toString());
            valueChanged();
        }
    }

    @Override
    public void updateSettings(Iterable<EnergyCompanySetting> settings, LiteYukonUser user, int ecId) {
        for (EnergyCompanySetting setting : settings) {
            updateSetting(setting, user, ecId);
        }
    }

    @Override
    public void updateSettingValue(EnergyCompanySettingType settingType, Object value,
                                   LiteYukonUser user, int ecId) {
        EnergyCompanySetting setting = getSetting(settingType, ecId);
        setting.setValue(value);
        updateSetting(setting, user, ecId);
    }

    @Override
    public List<EnergyCompanySetting> getAllSettings(int ecId) {
        List<EnergyCompanySetting> settings = new ArrayList<>();
        
        for (EnergyCompanySettingType type : EnergyCompanySettingType.values()) {
            settings.add(getSetting(type, ecId));
        }
        
        return settings;
    }

    @Override
    public boolean isEnabled(EnergyCompanySettingType setting, int ecId) {
        return getSetting(setting, ecId).isEnabled();
    }
    
    @PostConstruct
    public void init() {
        insertTemplate = new SimpleTableAccessTemplate<EnergyCompanySetting>(yukonJdbcTemplate, nextValueHelper);
        insertTemplate.setTableName("EnergyCompanySetting");
        insertTemplate.setFieldMapper(fieldMapper);
        insertTemplate.setPrimaryKeyField("EnergyCompanySettingId");
        insertTemplate.setPrimaryKeyValidOver(0);
        
        asyncDynamicDataSource.addDatabaseChangeEventListener(DbChangeCategory.ENERGY_COMPANY_SETTING, new DatabaseChangeEventListener() {
            @Override
            public void eventReceived(DatabaseChangeEvent event) {
                if (log.isDebugEnabled()) {
                    log.debug("Clearing EnergyCompanySettingCache because database change.");
                }
                valueChanged();
            }
        });

        // now that we're registered, clear out the cache of anything accumulated until now
        valueChanged();
    }
}
