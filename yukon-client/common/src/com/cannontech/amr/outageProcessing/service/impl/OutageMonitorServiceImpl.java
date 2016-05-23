package com.cannontech.amr.outageProcessing.service.impl;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.amr.MonitorEvaluatorStatus;
import com.cannontech.amr.outageProcessing.OutageMonitor;
import com.cannontech.amr.outageProcessing.dao.OutageMonitorDao;
import com.cannontech.amr.outageProcessing.service.OutageMonitorService;
import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.device.groups.editor.dao.DeviceGroupEditorDao;
import com.cannontech.common.device.groups.editor.dao.SystemGroupEnum;
import com.cannontech.common.device.groups.editor.model.StoredDeviceGroup;
import com.cannontech.common.userpage.dao.UserSubscriptionDao;
import com.cannontech.common.userpage.model.UserSubscription.SubscriptionType;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.core.dao.OutageMonitorNotFoundException;
import com.cannontech.message.DbChangeManager;
import com.cannontech.message.dispatch.message.DbChangeCategory;
import com.cannontech.message.dispatch.message.DbChangeType;

public class OutageMonitorServiceImpl implements OutageMonitorService {

    @Autowired private DbChangeManager dbChangeManager;
    @Autowired private OutageMonitorDao outageMonitorDao;
    @Autowired private DeviceGroupEditorDao deviceGroupEditorDao;
    @Autowired private UserSubscriptionDao userSubscriptionDao;
    private final Logger log = YukonLogManager.getLogger(OutageMonitorServiceImpl.class);

    @Override
    public StoredDeviceGroup getOutageGroup(String name) {
        StoredDeviceGroup outageGroup = deviceGroupEditorDao.getStoredGroup(SystemGroupEnum.OUTAGE, name, true);
        return outageGroup;
    }

    @Override
    public void create(OutageMonitor outageMonitor) {
        outageMonitorDao.saveOrUpdate(outageMonitor);
        dbChangeManager.processDbChange(DbChangeType.ADD, DbChangeCategory.MONITOR, outageMonitor.getOutageMonitorId());
    }
    
    @Override
    public void update(OutageMonitor outageMonitor) {
        outageMonitorDao.saveOrUpdate(outageMonitor);
        dbChangeManager.processDbChange(DbChangeType.UPDATE, DbChangeCategory.MONITOR, outageMonitor.getOutageMonitorId());
    }
    
    @Override
    public boolean delete(int outageMonitorId) throws OutageMonitorNotFoundException {

        OutageMonitor outageMonitor = outageMonitorDao.getById(outageMonitorId);

        // delete outage group
        try {
            StoredDeviceGroup outageGroup = getOutageGroup(outageMonitor.getOutageMonitorName());
            deviceGroupEditorDao.removeGroup(outageGroup);
        } catch (NotFoundException e) {
            // may have been deleted? who cares
        }

        userSubscriptionDao.deleteSubscriptionsForItem(SubscriptionType.OUTAGE_MONITOR, outageMonitorId);
        // delete processor
        boolean deleted = outageMonitorDao.delete(outageMonitorId);
        dbChangeManager.processDbChange(DbChangeType.DELETE, DbChangeCategory.MONITOR, outageMonitorId);
        return deleted;
    }

    @Override
    public MonitorEvaluatorStatus toggleEnabled(int outageMonitorId) throws OutageMonitorNotFoundException {

        OutageMonitor outageMonitor = outageMonitorDao.getById(outageMonitorId);

        // set status
        MonitorEvaluatorStatus newEvaluatorStatus;
        if (outageMonitor.getEvaluatorStatus().equals(MonitorEvaluatorStatus.DISABLED)) {
            newEvaluatorStatus = MonitorEvaluatorStatus.ENABLED;
        } else {
            newEvaluatorStatus = MonitorEvaluatorStatus.DISABLED;
        }
        outageMonitor.setEvaluatorStatus(newEvaluatorStatus);

        // update
        outageMonitorDao.saveOrUpdate(outageMonitor);
        dbChangeManager.processDbChange(DbChangeType.UPDATE, DbChangeCategory.MONITOR, outageMonitorId);
        log.debug("Updated outageMonitor evaluator status: status=" + newEvaluatorStatus + ", outageMonitor=" + outageMonitor.toString());

        return newEvaluatorStatus;
    }
}
