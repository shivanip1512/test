package com.cannontech.system.dao.impl;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.jdom2.JDOMException;
import org.joda.time.Instant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.transaction.annotation.Transactional;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.events.loggers.SystemEventLogService;
import com.cannontech.database.FieldMapper;
import com.cannontech.database.SimpleTableAccessTemplate;
import com.cannontech.database.YukonJdbcTemplate;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.database.incrementer.NextValueHelper;
import com.cannontech.encryption.CryptoException;
import com.cannontech.message.DbChangeManager;
import com.cannontech.message.dispatch.message.DbChangeCategory;
import com.cannontech.message.dispatch.message.DbChangeType;
import com.cannontech.system.GlobalSettingCryptoUtils;
import com.cannontech.system.GlobalSettingType;
import com.cannontech.system.dao.GlobalSettingDao;
import com.cannontech.system.dao.GlobalSettingUpdateDao;
import com.cannontech.system.model.GlobalSetting;
import com.cannontech.user.UserUtils;

public class GlobalSettingUpdateDaoImpl implements GlobalSettingUpdateDao {

    @Autowired private YukonJdbcTemplate yukonJdbcTemplate;
    @Autowired private DbChangeManager dbChangeManager;
    @Autowired private NextValueHelper nextValueHelper;
    @Autowired private GlobalSettingDao globalSettingDao;
    @Autowired private SystemEventLogService systemEventLogService;

    private SimpleTableAccessTemplate<GlobalSetting> insertTemplate;
    private static final Logger log = YukonLogManager.getLogger(GlobalSettingUpdateDaoImpl.class);

    private final static FieldMapper<GlobalSetting> fieldMapper = new FieldMapper<GlobalSetting>() {
        @Override
        public Number getPrimaryKey(GlobalSetting setting) {
            return setting.getId();
        }

        @Override
        public void setPrimaryKey(GlobalSetting setting, int id) {
            setting.setId(id);
        }

        @Override
        public void extractValues(MapSqlParameterSource parameterHolder, GlobalSetting setting) {
            parameterHolder.addValue("Name", setting.getType());
            Object value = setting.getValue();
            if (value != null && setting.getType().isSensitiveInformation()) {
                try {
                    if (!GlobalSettingCryptoUtils.isEncrypted((String) value)) {
                        value = GlobalSettingCryptoUtils.encryptValue((String) value);
                    }
                } catch (CryptoException | IOException | JDOMException e) {
                    throw new RuntimeException("Unable to encrypt value for setting " + setting.getType(), e);
                }
            }
            parameterHolder.addValue("Value", value);
            parameterHolder.addValue("Comments", setting.getComments());
            parameterHolder.addValue("LastChangedDate", setting.getLastChanged());
        }
    };
    
    /**
     * On startup encrypt the sensitive information which is not already encrypted.
     */
    private void encryptSensitiveInformation() {
        List<GlobalSettingType> sensitiveInformation = GlobalSettingType.getSensitiveSettings();
        Map<GlobalSettingType, GlobalSetting> map = globalSettingDao.getGlobalSettingsValue(sensitiveInformation);
        if (!map.isEmpty()) {
            updateSettings(map.values(), null);
        }
    }

    @Transactional
    @Override
    public void updateSettings(Iterable<GlobalSetting> settings, LiteYukonUser user) {
        for (GlobalSetting setting : settings) {
            updateSetting(setting, user);
        }
    }

    @Override
    public void updateSetting(GlobalSetting setting, LiteYukonUser user) {
        boolean isChanged = false;
        boolean isChangedValue = false;
        boolean isChangedComments = false;
        boolean isOnlyCommentsChange = false;

        GlobalSetting currentSetting = globalSettingDao.getSetting(setting.getType());
        Object currentValue = currentSetting.getValue();
        String currentComments = currentSetting.getComments();
        
        Object value = setting.getValue();
        if (value == null) {
            if (currentValue != null) {
                isChanged = true;
                isChangedValue = true;
            }
        } else {
            if (!value.equals(currentValue)) {
                isChanged = true;
                isChangedValue = true;
            }
        }
        if (setting.getComments() == null || StringUtils.isBlank(setting.getComments())) {
            if (currentComments != null) {
                if(!isChanged) {
                    isOnlyCommentsChange = true;
                }
                isChanged = true;
                isChangedComments = true;
            }
        } else {
            if (!setting.getComments().equalsIgnoreCase(currentComments)) {
                if(!isChanged) {
                    isOnlyCommentsChange = true;
                }
                isChanged = true;
                isChangedComments = true;
            }
        }

        if (isChanged) {
            if(!isOnlyCommentsChange) {
                setting.setLastChanged(Instant.now());
            }

            if (StringUtils.isBlank(setting.getComments())) {
                setting.setComments(null);
            }

            if (insertTemplate.saveWillUpdate(setting)) {
                insertTemplate.save(setting);
                dbChangeManager.processDbChange(DbChangeType.UPDATE,
                                                DbChangeCategory.GLOBAL_SETTING,
                                                setting.getId());
            } else {
                insertTemplate.save(setting);
                dbChangeManager.processDbChange(DbChangeType.ADD,
                                                DbChangeCategory.GLOBAL_SETTING,
                                                setting.getId());
            }
            if (isChangedValue) {
                log.debug(setting.getType() + " changed from (" + currentValue + ") to (" + value + ")");
                if (user == null) {
                    user = UserUtils.getYukonUser();
                }
                if (setting.isSensitiveInformation()) {
                    systemEventLogService.sensitiveGlobalSettingChanged(user, setting.getType());
                } else {
                    systemEventLogService.globalSettingChanged(user, setting.getType(),
                        value == null ? "" : value.toString());
                }
            }
            if (isChangedComments) {
                log.debug("Comments for " + setting.getType() + " updated");
            }
        }
    }

    @Override
    public void updateSettingValue(GlobalSettingType type, Object newVal, LiteYukonUser user) {
        GlobalSetting setting = globalSettingDao.getSetting(type);
        setting.setValue(newVal);
        updateSetting(setting, user);
    }

    @PostConstruct
    public void init() {
        insertTemplate = new SimpleTableAccessTemplate<GlobalSetting>(yukonJdbcTemplate, nextValueHelper);
        insertTemplate.setTableName("GlobalSetting");
        insertTemplate.setFieldMapper(fieldMapper);
        insertTemplate.setPrimaryKeyField("GlobalSettingId");
        insertTemplate.setPrimaryKeyValidOver(0);
        
        encryptSensitiveInformation();
    }

}