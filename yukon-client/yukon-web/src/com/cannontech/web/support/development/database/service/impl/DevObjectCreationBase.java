package com.cannontech.web.support.development.database.service.impl;

import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.device.creation.DeviceCreationService;
import com.cannontech.common.device.model.SimpleDevice;
import com.cannontech.common.pao.definition.service.PaoDefinitionService;
import com.cannontech.core.dao.DBPersistentDao;
import com.cannontech.core.dao.DeviceDao;
import com.cannontech.core.dao.PaoDao;
import com.cannontech.core.dao.PaoScheduleDao;
import com.cannontech.core.dao.YukonUserDao;
import com.cannontech.database.data.device.DeviceBase;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.database.data.multi.SmartMultiDBPersistent;
import com.cannontech.database.data.point.PointBase;
import com.cannontech.web.support.development.DevDbSetupTask;
import com.cannontech.web.support.development.database.service.DevObjectCreationInterface;

public abstract class DevObjectCreationBase implements DevObjectCreationInterface {
    protected static final Logger log = YukonLogManager.getLogger(DevObjectCreationBase.class);
    protected PaoDefinitionService paoDefinitionService;
    protected PaoDao paoDao;
    protected DeviceDao deviceDao;
    protected PaoScheduleDao paoScheduleDao;
    protected YukonUserDao yukonUserDao;
    protected DevDbSetupTask devDbSetupTask;
    protected DeviceCreationService deviceCreationService;
    protected DBPersistentDao dbPersistentDao;

    @Override
    public void createAll(DevDbSetupTask devDbSetupTask, boolean create) {
        if (!create) {
            return;
        }
        this.devDbSetupTask = devDbSetupTask;
        createAll();
    }
    
    @Override
    public abstract void createAll();

    // This is a pretty terrible way of accomplishing this... but good enough for now
    protected void checkIsCancelled() {
        if (devDbSetupTask.isCancelled()) {
            devDbSetupTask.setCancelled(false);
            log.info("Development database setup cancelled.");
            throw new RuntimeException("Execution cancelled.");
        }
    }
    
    protected LiteYukonPAObject getPaoByName(String paoName) {
        List<LiteYukonPAObject> paos = paoDao.getLiteYukonPaoByName(paoName, false);
        if (paos.size() != 1) {
            return null;
        }
        LiteYukonPAObject litePao = paos.get(0);
        return litePao;
    }

    protected int getPaoIdByName(String paoName) {
        LiteYukonPAObject litePao = getPaoByName(paoName);
        if (litePao == null) {
            return -1;
        }
        return litePao.getYukonID();
    }

    protected SmartMultiDBPersistent createSmartDBPersistent(DeviceBase deviceBase) {
        if (deviceBase == null)
            return null;

        SmartMultiDBPersistent smartDB = new SmartMultiDBPersistent();
        smartDB.addOwnerDBPersistent(deviceBase);

        SimpleDevice yukonDevice = deviceDao.getYukonDeviceForDevice(deviceBase);
        List<PointBase> defaultPoints = paoDefinitionService.createDefaultPointsForPao(yukonDevice);

        for (PointBase point : defaultPoints) {
            smartDB.addDBPersistent(point);
        }

        return smartDB;
    }
    
    @Autowired
    public void setDeviceDao(DeviceDao deviceDao) {
        this.deviceDao = deviceDao;
    }
    @Autowired
    public void setPaoDefinitionService(PaoDefinitionService paoDefinitionService) {
        this.paoDefinitionService = paoDefinitionService;
    }
    @Autowired
    public void setPaoDao(PaoDao paoDao) {
        this.paoDao = paoDao;
    }
    @Autowired
    public void setPaoScheduleDao(PaoScheduleDao paoScheduleDao) {
        this.paoScheduleDao = paoScheduleDao;
    }
    @Autowired
    public void setYukonUserDao(YukonUserDao yukonUserDao) {
        this.yukonUserDao = yukonUserDao;
    }
    @Autowired
    public void setDeviceCreationService(DeviceCreationService deviceCreationService) {
        this.deviceCreationService = deviceCreationService;
    }
    @Autowired
    public void setDbPersistentDao(DBPersistentDao dbPersistentDao) {
        this.dbPersistentDao = dbPersistentDao;
    }
}
