package com.cannontech.system.dao.impl;

import javax.annotation.PostConstruct;

import org.joda.time.Instant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.transaction.annotation.Transactional;

import com.cannontech.core.dao.DBPersistentDao;
import com.cannontech.database.FieldMapper;
import com.cannontech.database.SimpleTableAccessTemplate;
import com.cannontech.database.YukonJdbcTemplate;
import com.cannontech.database.incrementer.NextValueHelper;
import com.cannontech.message.dispatch.message.DbChangeCategory;
import com.cannontech.message.dispatch.message.DbChangeType;
import com.cannontech.system.GlobalSettingType;
import com.cannontech.system.dao.GlobalSettingDao;
import com.cannontech.system.dao.GlobalSettingUpdateDao;
import com.cannontech.system.model.GlobalSetting;

public class GlobalSettingUpdateDaoImpl implements GlobalSettingUpdateDao {
    
    @Autowired private YukonJdbcTemplate yukonJdbcTemplate;
    @Autowired private DBPersistentDao dbPersistentDao;
    @Autowired private NextValueHelper nextValueHelper;
    @Autowired private GlobalSettingDao globalSettingDao;
    private SimpleTableAccessTemplate<GlobalSetting> insertTemplate;
    
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
            parameterHolder.addValue("Value", setting.getValue());
            parameterHolder.addValue("Comments", setting.getComments());
            parameterHolder.addValue("LastChangedDate", setting.getLastChanged());
        }
    };
    
    @Transactional
    @Override
    public void updateSettings(Iterable<GlobalSetting> settings) {
        for (GlobalSetting setting : settings) {
            updateSetting(setting);
        }
    }
    
    @Override
    public void updateSetting(GlobalSetting setting) {
        setting.setLastChanged(Instant.now());
        
        if (insertTemplate.saveWillUpdate(setting)) {
            insertTemplate.save(setting);
            dbPersistentDao.processDatabaseChange(DbChangeType.UPDATE, DbChangeCategory.GLOBAL_SETTING, setting.getId());
        } else {
            insertTemplate.save(setting);
            dbPersistentDao.processDatabaseChange(DbChangeType.ADD, DbChangeCategory.GLOBAL_SETTING, setting.getId());
        }
    }
    
    @Override
    public void updateSettingValue(GlobalSettingType type, Object newVal) {
        GlobalSetting setting = globalSettingDao.getSetting(type);
        setting.setValue(newVal);
        updateSetting(setting);
    }

    @PostConstruct
    public void init() {
        insertTemplate = new SimpleTableAccessTemplate<GlobalSetting>(yukonJdbcTemplate, nextValueHelper);
        insertTemplate.setTableName("GlobalSetting");
        insertTemplate.setFieldMapper(fieldMapper);
        insertTemplate.setPrimaryKeyField("GlobalSettingId");
        insertTemplate.setPrimaryKeyValidOver(0);
    }
    
}