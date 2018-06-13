package com.cannontech.watchdog.service.impl;

import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.pao.PaoClass;
import com.cannontech.watchdog.base.YukonServices;
import com.cannontech.watchdog.dao.WatchdogWatcherDao;
import com.cannontech.watchdog.service.WatchdogWatcherService;

public class WatchdogWatcherServiceImpl implements WatchdogWatcherService {

    private static final Logger log = YukonLogManager.getLogger(WatchdogWatcherServiceImpl.class);
    @Autowired WatchdogWatcherDao dao;

    @Override
    public boolean isServiceRequired(YukonServices serviceName) {
        PaoClass paoClass = null;
        if (serviceName == YukonServices.CAPCONTROL) {
            paoClass = PaoClass.CAPCONTROL;
        } else if (serviceName == YukonServices.LOADMANAGEMENT) {
            paoClass = PaoClass.LOADMANAGEMENT;
        } else {
            log.info("Incorrect service name " + serviceName);
            return true;
        }
        return dao.paoClassPaoExists(paoClass);
    }

}
