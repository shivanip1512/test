package com.cannontech.system.dao.impl;

import javax.annotation.PostConstruct;

import org.joda.time.Instant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;

import com.cannontech.core.dao.DBPersistentDao;
import com.cannontech.database.FieldMapper;
import com.cannontech.database.SimpleTableAccessTemplate;
import com.cannontech.database.YukonJdbcTemplate;
import com.cannontech.database.db.setting.GlobalSettingDb;
import com.cannontech.database.incrementer.NextValueHelper;
import com.cannontech.message.dispatch.message.DbChangeCategory;
import com.cannontech.message.dispatch.message.DbChangeType;
import com.cannontech.system.GlobalSetting;
import com.cannontech.system.dao.GlobalSettingsDao;
import com.cannontech.system.dao.GlobalSettingsUpdateDao;

public class GlobalSettingsUpdateDaoImpl implements GlobalSettingsUpdateDao {
    
    @Autowired private YukonJdbcTemplate yukonJdbcTemplate;
    @Autowired private DBPersistentDao dbPersistentDao;
    @Autowired private NextValueHelper nextValueHelper;
    @Autowired private GlobalSettingsDao globalSettingsDao;
    private SimpleTableAccessTemplate<GlobalSettingDb> insertTemplate;
    
    private final static FieldMapper<GlobalSettingDb> fieldMapper = new FieldMapper<GlobalSettingDb>() {
        @Override
        public Number getPrimaryKey(GlobalSettingDb setting) {
            return setting.getYukonSettingId();
        }

        @Override
        public void setPrimaryKey(GlobalSettingDb setting, int yukonSettingId) {
            setting.setYukonSettingId(yukonSettingId);
        }

        @Override
        public void extractValues(MapSqlParameterSource parameterHolder, GlobalSettingDb setting) {
            parameterHolder.addValue("Name", setting.getName());
            parameterHolder.addValue("Value", setting.getValue());
            parameterHolder.addValue("Comment", setting.getComment());
            parameterHolder.addValue("LastChangedDate", setting.getLastChangedDate());
        }
    };
    
    @Override
    public void updateSetting(GlobalSetting setting, String newVal) {
        GlobalSettingDb newSetting = globalSettingsDao.getSettingDb(setting);
        newSetting.setValue(newVal);
        newSetting.setLastChangedDate(Instant.now());
        
        if (insertTemplate.saveWillUpdate(newSetting)){
            insertTemplate.save(newSetting);
            dbPersistentDao.processDatabaseChange(DbChangeType.UPDATE, DbChangeCategory.GLOBAL_SETTING, newSetting.getYukonSettingId());
        } else {
            insertTemplate.save(newSetting);
            dbPersistentDao.processDatabaseChange(DbChangeType.ADD, DbChangeCategory.GLOBAL_SETTING, newSetting.getYukonSettingId());
        }
    }
    

    @PostConstruct
    public void init() {
        insertTemplate = new SimpleTableAccessTemplate<GlobalSettingDb>(yukonJdbcTemplate, nextValueHelper);
        insertTemplate.setTableName("YukonSetting");
        insertTemplate.setFieldMapper(fieldMapper);
        insertTemplate.setPrimaryKeyField("YukonSettingId");
        insertTemplate.setPrimaryKeyValidOver(0);
    }
}
