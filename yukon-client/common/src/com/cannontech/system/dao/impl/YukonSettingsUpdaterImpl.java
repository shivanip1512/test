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

// Need
public class YukonSettingsUpdaterImpl implements YukonSettingsUpdater {
    
    @Autowired private YukonJdbcTemplate yukonJdbcTemplate;
    @Autowired private DBPersistentDao dbPersistentDao;

    @Override
    public void updateSetting(YukonSetting setting, String newVal) {
        // TODO verify the newVal?
        newVal = SqlUtils.convertStringToDbValue(newVal);
        //String settingValue = findSettingValue(setting);        
        
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("Update YukonSetting");
        sql.append("set Value").eq(newVal);
        sql.append("WHERE Name").eq(setting.name());

        yukonJdbcTemplate.update(sql);
        
        //if (settingValue != null) {
            dbPersistentDao.processDBChange(new DBChangeMsg(0,
                                                           DBChangeMsg.CHANGE_YUKON_SETTING_DB,
                                                           DBChangeMsg.CAT_YUKON_SETTING_DB,
                                                           DbChangeType.UPDATE));
//        } else {
//            dbPersistentDao.processDBChange(new DBChangeMsg(0,
//                                                            DBChangeMsg.CHANGE_YUKON_SETTING_DB,
//                                                            DBChangeMsg.CAT_YUKON_SETTING_DB,
//                                                            DbChangeType.ADD));
//        }
    }
}
