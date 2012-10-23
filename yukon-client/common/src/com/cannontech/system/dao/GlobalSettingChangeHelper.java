package com.cannontech.system.dao;

import javax.annotation.PostConstruct;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.core.dynamic.AsyncDynamicDataSource;
import com.cannontech.core.dynamic.DatabaseChangeEventListener;
import com.cannontech.message.dispatch.message.DatabaseChangeEvent;
import com.cannontech.message.dispatch.message.DbChangeCategory;

public class GlobalSettingChangeHelper {
    
    @Autowired private AsyncDynamicDataSource asyncDynamicDataSource;
    @Autowired private GlobalSettingDao globalSettingDao;
    private static final Logger log = YukonLogManager.getLogger(GlobalSettingChangeHelper.class);

    @PostConstruct
    public void setup() {
        asyncDynamicDataSource.addDatabaseChangeEventListener(DbChangeCategory.GLOBAL_SETTING, new DatabaseChangeEventListener() {
            @Override
            public void eventReceived(DatabaseChangeEvent event) {
                if (log.isDebugEnabled()) {
                    log.debug("Sending clearCache to globalSettingsDao because database change.");
                }
                globalSettingDao.valueChanged();
            }
        });
        
        // now that we're registered, clear out the cache of anything accumulated until now
        globalSettingDao.valueChanged();
    }
}
