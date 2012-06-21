package com.cannontech.web.support.development.database.service.impl;

import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.config.ConfigurationSource;
import com.cannontech.common.device.creation.DeviceCreationService;
import com.cannontech.common.device.model.SimpleDevice;
import com.cannontech.common.pao.definition.service.PaoDefinitionService;
import com.cannontech.core.dao.DBPersistentDao;
import com.cannontech.core.dao.DeviceDao;
import com.cannontech.core.dao.PaoDao;
import com.cannontech.core.dao.RoleDao;
import com.cannontech.core.roleproperties.GroupRolePropertyValueCollection;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.core.roleproperties.dao.RolePropertyEditorDao;
import com.cannontech.database.data.device.DeviceBase;
import com.cannontech.database.data.lite.LiteYukonGroup;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.database.data.multi.SmartMultiDBPersistent;
import com.cannontech.database.data.point.PointBase;
import com.cannontech.web.support.development.DevDbSetupTask;
import com.cannontech.web.support.development.database.objects.DevObject;
import com.cannontech.web.support.development.database.service.DevObjectCreationInterface;

public abstract class DevObjectCreationBase implements DevObjectCreationInterface {
    protected static final Logger log = YukonLogManager.getLogger(DevObjectCreationBase.class);
    protected DevDbSetupTask devDbSetupTask;
    @Autowired protected ConfigurationSource configurationSource;
    @Autowired protected PaoDao paoDao;
    @Autowired protected DeviceDao deviceDao;
    @Autowired protected DeviceCreationService deviceCreationService;
    @Autowired protected DBPersistentDao dbPersistentDao;
    @Autowired private PaoDefinitionService paoDefinitionService;
    @Autowired private RolePropertyEditorDao rolePropertyEditorDao;
    @Autowired private RoleDao roleDao;

    @Override
    public void createAll(DevDbSetupTask devDbSetupTask) {
        this.devDbSetupTask = devDbSetupTask;
        createAll();
    }
    
    protected abstract void createAll();
    
    @Override
    public void logFinalExecutionDetails(DevObject devObj) {
        logFinalExecutionDetails();
        log.info(" success: " + devObj.getSuccessCount());
        log.info(" failure: " + devObj.getFailureCount());
        log.info(" total:   " + devObj.getTotal());
    }
    
    protected abstract void logFinalExecutionDetails();

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
        if (deviceBase == null) {
            return null;
        }

        SmartMultiDBPersistent smartDB = new SmartMultiDBPersistent();
        smartDB.addOwnerDBPersistent(deviceBase);

        SimpleDevice yukonDevice = deviceDao.getYukonDeviceForDevice(deviceBase);
        List<PointBase> defaultPoints = paoDefinitionService.createDefaultPointsForPao(yukonDevice);

        for (PointBase point : defaultPoints) {
            smartDB.addDBPersistent(point);
        }

        return smartDB;
    }
    

    protected void setRoleProperty(LiteYukonGroup group, YukonRoleProperty yukonRoleProperty, String newVal) {
        roleDao.updateGroupRoleProperty(group,yukonRoleProperty.getRole().getRoleId(),yukonRoleProperty.getPropertyId(),newVal);
        log.info("Group " + group.getGroupName() + " YukonRole " + yukonRoleProperty.getRole().name() + " and YukonRoleProperty " + yukonRoleProperty.name() + " set to " + newVal);
    }

    protected void setRoleProperty(LiteYukonGroup group, YukonRoleProperty yukonRoleProperty, boolean newVal) {
        GroupRolePropertyValueCollection propertyValues = rolePropertyEditorDao.getForGroupAndRole(group, yukonRoleProperty.getRole(), true);
        Map<YukonRoleProperty, Object> valueMap = propertyValues.getValueMap();
        valueMap.put(yukonRoleProperty, newVal);
        propertyValues.putAll(valueMap);
        rolePropertyEditorDao.save(propertyValues);
        log.info("Group " + group.getGroupName() + " YukonRole " + yukonRoleProperty.getRole().name() + " and YukonRoleProperty " + yukonRoleProperty.name() + " set to " + newVal);
    }
    
}
