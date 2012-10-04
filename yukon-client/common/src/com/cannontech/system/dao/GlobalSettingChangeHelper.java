package com.cannontech.system.dao;

import javax.annotation.PostConstruct;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.core.dynamic.AsyncDynamicDataSource;
import com.cannontech.database.cache.DBChangeListener;
import com.cannontech.message.dispatch.message.DBChangeMsg;
import com.cannontech.system.dao.impl.GlobalSettingsDaoImpl;

public class GlobalSettingChangeHelper {
    
    @Autowired private AsyncDynamicDataSource asyncDynamicDataSource;
    @Autowired private GlobalSettingsDaoImpl globalSettingsDaoImpl;
    private Logger log = YukonLogManager.getLogger(GlobalSettingChangeHelper.class);
    
    @PostConstruct
    public void setup() {
        asyncDynamicDataSource.addDBChangeListener(new DBChangeListener() {

            @Override
            public void dbChangeReceived(DBChangeMsg dbChange) {
                 if (dbChange.getDatabase() == DBChangeMsg.CHANGE_YUKON_SETTING_DB) {
                     if (log.isDebugEnabled()) {
                         log.debug("Sending clearCache to GlobalSettingDao because: " + dbChange);
                     }
                     globalSettingsDaoImpl.clearCache();
                 }
            }
        });

        // now that we're registered, clear out the cache of anything accumulated until now
        globalSettingsDaoImpl.clearCache();
    }
}
