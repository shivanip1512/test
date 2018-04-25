package com.cannontech.web.tools.device.config;

import java.util.List;
import java.util.stream.Collectors;

import org.apache.logging.log4j.Logger;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.device.config.service.DeviceConfigService;
import com.cannontech.common.device.model.SimpleDevice;
import com.cannontech.jobs.support.YukonTaskBase;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.tools.device.config.dao.DeviceConfigSummaryDao;

public class DeviceConfigVerificationTask extends YukonTaskBase {

    private static final Logger log = YukonLogManager.getLogger(DeviceConfigVerificationTask.class);
    
    @Autowired private DeviceConfigSummaryDao deviceConfigSummaryDao;
    @Autowired private DeviceConfigService deviceConfigService;

    @Override
    public void start() {
        log.info("Running DeviceConfigVerificationTask at "+ new DateTime().toString("MM-dd-yyyy HH:mm:ss"));
        List<SimpleDevice> devicesToVerify = deviceConfigSummaryDao.getDevicesToVerify();
        if (!devicesToVerify.isEmpty()) {
            log.info("DeviceConfigVerificationTask is verifing " + devicesToVerify.size() + " devices");
            deviceConfigService.verifyConfigs(devicesToVerify, YukonUserContext.system.getYukonUser());
            if (log.isDebugEnabled()) {
                log.debug("DeviceConfigVerificationTask is verified devices ids= "
                    + devicesToVerify.stream().map(d -> d.getDeviceId()).collect(Collectors.toList()));
            }
        }
    }
}
