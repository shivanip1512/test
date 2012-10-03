package com.cannontech.system.dao.impl;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.core.dao.DBPersistentDao;
import com.cannontech.database.SqlUtils;
import com.cannontech.database.YukonJdbcTemplate;
import com.cannontech.message.dispatch.message.DBChangeMsg;
import com.cannontech.message.dispatch.message.DbChangeType;
import com.cannontech.system.YukonSetting;
import com.cannontech.system.dao.YukonSettingsUpdater;

public class YukonSettingsUpdaterImpl implements YukonSettingsUpdater {
    
    @Autowired private YukonJdbcTemplate yukonJdbcTemplate;
    @Autowired private DBPersistentDao dbPersistentDao;

    @Override
    public void updateSetting(YukonSetting setting, String newVal) {
        newVal = SqlUtils.convertStringToDbValue(newVal);
        
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("UPDATE YukonSetting");
        sql.append("SET Value").eq(newVal);
        sql.append("WHERE Name").eq(setting.name());

        yukonJdbcTemplate.update(sql);
        
        dbPersistentDao.processDBChange(new DBChangeMsg(0,
                       DBChangeMsg.CHANGE_YUKON_SETTING_DB,
                       DBChangeMsg.CAT_YUKON_SETTING_DB,
                       DbChangeType.UPDATE));
    }
}
