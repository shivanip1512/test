package com.cannontech.system.dao.impl;

import javax.annotation.PostConstruct;

import org.joda.time.Instant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;

import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.core.dao.DBPersistentDao;
import com.cannontech.database.FieldMapper;
import com.cannontech.database.SimpleTableAccessTemplate;
import com.cannontech.database.YukonJdbcTemplate;
import com.cannontech.database.incrementer.NextValueHelper;
import com.cannontech.message.dispatch.message.DBChangeMsg;
import com.cannontech.message.dispatch.message.DbChangeCategory;
import com.cannontech.message.dispatch.message.DbChangeType;
import com.cannontech.system.GlobalSetting;
import com.cannontech.system.dao.GlobalSettingsUpdateDao;

public class GlobalSettingsUpdateDaoImpl implements GlobalSettingsUpdateDao {
    
    @Autowired private YukonJdbcTemplate yukonJdbcTemplate;
    @Autowired private DBPersistentDao dbPersistentDao;
    @Autowired private NextValueHelper nextValueHelper;
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
    
    private static class GlobalSettingDb {
        private int yukonSettingId;
        private String name;
        private String value;
        private String comment;
        private Instant lastChangedDate;
        public Number getYukonSettingId() { return yukonSettingId; }
        public void setYukonSettingId(int yukonSettingId) { this.yukonSettingId = yukonSettingId; }
        public String getName() {return name;}
        public void setName(String name) {this.name = name;}
        public String getValue() {return value;}
        public void setValue(String value) {this.value = value;}
        public String getComment() {return comment;}
        public void setComment(String comment) {this.comment = comment;}
        public Instant getLastChangedDate() {return lastChangedDate;}
        public void setLastChangedDate(Instant lastChangedDate) {this.lastChangedDate = lastChangedDate;}
    }
    
    @Override
    public void updateSetting(GlobalSetting setting, String newVal) {
        
        GlobalSettingDb newSetting = new GlobalSettingDb();
        newSetting.setLastChangedDate(Instant.now());
        newSetting.setName(setting.name());
        newSetting.setValue(newVal);
        
        if (getCount(setting) > 0) {
            dbPersistentDao.processDatabaseChange(DbChangeType.UPDATE, DbChangeCategory.GLOBAL_SETTING, 0);
            update(newSetting);
        } else {
            dbPersistentDao.processDatabaseChange(DbChangeType.ADD, DbChangeCategory.GLOBAL_SETTING, 0);
            insertTemplate.insert(newSetting);
        }
    }
    
    private void update(GlobalSettingDb setting) {
      
      SqlStatementBuilder sql = new SqlStatementBuilder();
      sql.append("UPDATE YukonSetting");
      sql.append("SET Value").eq(setting.getValue());
      sql.append(", LastChangedDate").eq(setting.getLastChangedDate());
      sql.append("WHERE Name").eq(setting.getName());

      yukonJdbcTemplate.update(sql);
    }
    
    private int getCount(GlobalSetting setting) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT COUNT(*)");// as 'count'
        sql.append("FROM YukonSetting");
        sql.append("WHERE name").eq(setting.name());
        return yukonJdbcTemplate.queryForInt(sql);
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
