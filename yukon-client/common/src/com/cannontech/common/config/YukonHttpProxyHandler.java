package com.cannontech.common.config;

import javax.annotation.PostConstruct;

import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.util.YukonHttpProxy;
import com.cannontech.core.dynamic.AsyncDynamicDataSource;
import com.cannontech.core.dynamic.DatabaseChangeEventListener;
import com.cannontech.message.dispatch.message.DatabaseChangeEvent;
import com.cannontech.system.GlobalSettingType;
import com.cannontech.system.dao.GlobalSettingDao;

public class YukonHttpProxyHandler {
    private final static Logger log = YukonLogManager.getLogger(YukonHttpProxyHandler.class);
    @Autowired private AsyncDynamicDataSource asyncDynamicDataSource;
    @Autowired private GlobalSettingDao settingDao;

    @PostConstruct
    public void init() {
        log.debug("Setting system proxy.");
        YukonHttpProxy.fromGlobalSetting(settingDao).ifPresent(proxy -> proxy.setAsSystemProxy());

        asyncDynamicDataSource.addDatabaseChangeEventListener(new DatabaseChangeEventListener() {
            @Override
            public void eventReceived(DatabaseChangeEvent event) {
                if (settingDao.isDbChangeForSetting(event, GlobalSettingType.HTTP_PROXY)) {
                    YukonHttpProxy.fromGlobalSetting(settingDao).ifPresent(proxy -> proxy.setAsSystemProxy());
                }
            }
        });
    }
}
