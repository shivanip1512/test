package com.cannontech.web.dev.database.service.impl;

import java.util.List;
import java.util.Map;

import javax.naming.ConfigurationException;

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
import com.cannontech.core.roleproperties.YukonRole;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.core.roleproperties.dao.RolePropertyEditorDao;
import com.cannontech.database.data.device.DeviceBase;
import com.cannontech.database.data.lite.LiteYukonGroup;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.database.data.multi.SmartMultiDBPersistent;
import com.cannontech.database.data.point.PointBase;
import com.cannontech.stars.dr.account.dao.CustomerAccountDao;

public abstract class DevObjectCreationBase {
    protected static final Logger log = YukonLogManager.getLogger(DevObjectCreationBase.class);
    @Autowired protected ConfigurationSource configurationSource;
    @Autowired protected PaoDao paoDao;
    @Autowired protected DeviceDao deviceDao;
    @Autowired protected DeviceCreationService deviceCreationService;
    @Autowired protected DBPersistentDao dbPersistentDao;
    @Autowired protected CustomerAccountDao customerAccountDao;

    @Autowired private PaoDefinitionService paoDefinitionService;
    @Autowired private RolePropertyEditorDao rolePropertyEditorDao;
    @Autowired private RoleDao roleDao;

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

    protected boolean canAddRole(LiteYukonGroup group, YukonRole yukonRole) {
        if (!roleDao.getRolesForGroup(group.getGroupID()).contains(yukonRole)) {
            try {
                rolePropertyEditorDao.addRoleToGroup(group, yukonRole);
            } catch (ConfigurationException e) {
                return false;
            }
        }
        return true;
    }

    protected void setRoleProperty(LiteYukonGroup group, YukonRoleProperty yukonRoleProperty, String newVal) {
        roleDao.updateGroupRoleProperty(group, yukonRoleProperty.getRole().getRoleId(),
            yukonRoleProperty.getPropertyId(), newVal);
        log.debug("Group " + group.getGroupName() + " YukonRole " + yukonRoleProperty.getRole().name()
            + " and YukonRoleProperty " + yukonRoleProperty.name() + " set to " + newVal);
    }

    protected void setRoleProperty(LiteYukonGroup group, YukonRoleProperty yukonRoleProperty, boolean newVal) {
        GroupRolePropertyValueCollection propertyValues =
            rolePropertyEditorDao.getForGroupAndRole(group, yukonRoleProperty.getRole(), true);
        Map<YukonRoleProperty, Object> valueMap = propertyValues.getValueMap();
        valueMap.put(yukonRoleProperty, newVal);
        propertyValues.putAll(valueMap);
        rolePropertyEditorDao.save(propertyValues);
        log.debug("Group " + group.getGroupName() + " YukonRole " + yukonRoleProperty.getRole().name()
            + " and YukonRoleProperty " + yukonRoleProperty.name() + " set to " + newVal);
    }

}
