package com.cannontech.watchdog.service.impl;

import java.util.Set;

import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.amr.rfn.dao.RfnDeviceDao;
import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.pao.PaoClass;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.rfn.message.RfnIdentifier;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.core.dao.PaoDao;
import com.cannontech.watchdog.base.YukonServices;
import com.cannontech.watchdog.dao.WatchdogWatcherDao;
import com.cannontech.watchdog.service.WatchdogWatcherService;
import com.google.common.collect.Sets;

public class WatchdogWatcherServiceImpl implements WatchdogWatcherService {

    private static final Logger log = YukonLogManager.getLogger(WatchdogWatcherServiceImpl.class);
    @Autowired WatchdogWatcherDao dao;
    @Autowired private PaoDao paoDao;
    @Autowired RfnDeviceDao rfnDeviceDao;

    @Override
    public boolean isServiceRequired(YukonServices serviceName) {
        if (serviceName == YukonServices.CAPCONTROL) {
            return doPaoWithPaoClassExists(PaoClass.CAPCONTROL);
        } else if (serviceName == YukonServices.LOADMANAGEMENT) {
            return doPaoWithPaoTypeExists(Sets.newHashSet(PaoType.LM_CONTROL_AREA));
        } else if (serviceName == YukonServices.NETWORKMANAGER) {
            return doPaoWithPaoClassExists(PaoClass.RFMESH);
        } else if (serviceName == YukonServices.ITRON) {
            return doPaoWithPaoClassExists(PaoClass.ITRON);
        } else {
            log.info("Incorrect service name " + serviceName);
            return true;
        }
    }

    /**
     * Check if pao exists for the paoClass
     */
    private boolean doPaoWithPaoClassExists(PaoClass paoClass) {
        return dao.paoClassPaoExists(paoClass);
    }

    /**
     * Check if any pao exists for the paoTypes
     */
    private boolean doPaoWithPaoTypeExists(Set<PaoType> paoTypes) {
        return paoDao.getPaoCount(paoTypes) > 1;
    }

    @Override
    public RfnIdentifier getGatewayRfnIdentifier() throws NotFoundException {
        return dao.getIdForLatestGateway();
    }
}
