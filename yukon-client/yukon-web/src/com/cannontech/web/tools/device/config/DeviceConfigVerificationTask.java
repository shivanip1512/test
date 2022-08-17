package com.cannontech.web.tools.device.config;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.logging.log4j.Logger;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.device.commands.VerifyConfigCommandResult;
import com.cannontech.common.device.config.dao.DeviceConfigurationDao;
import com.cannontech.common.device.config.model.LightDeviceConfiguration;
import com.cannontech.common.device.config.service.DeviceConfigService;
import com.cannontech.common.device.model.SimpleDevice;
import com.cannontech.common.model.Direction;
import com.cannontech.common.model.PagingParameters;
import com.cannontech.common.search.result.SearchResults;
import com.cannontech.jobs.support.YukonTaskBase;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.tools.device.config.dao.DeviceConfigSummaryDao;
import com.cannontech.web.tools.device.config.dao.DeviceConfigSummaryDao.SortBy;
import com.cannontech.web.tools.device.config.model.DeviceConfigSummaryDetail;
import com.cannontech.web.tools.device.config.model.DeviceConfigSummaryFilter;
import com.cannontech.web.tools.device.config.model.DeviceConfigSummaryFilter.InSync;
import com.cannontech.web.tools.device.config.model.DeviceConfigSummaryFilter.LastAction;
import com.cannontech.web.tools.device.config.model.DeviceConfigSummaryFilter.LastActionStatus;

public class DeviceConfigVerificationTask extends YukonTaskBase {

    private static final Logger log = YukonLogManager.getLogger(DeviceConfigVerificationTask.class);
    
    @Autowired private DeviceConfigSummaryDao deviceConfigSummaryDao;
    @Autowired private DeviceConfigService deviceConfigService;
    @Autowired private DeviceConfigurationDao deviceConfigurationDao;

    @Override
    public void start() {
        log.info("Running DeviceConfigVerificationTask at "+ new DateTime().toString("MM-dd-yyyy HH:mm:ss"));
        
        List<SimpleDevice> devicesToVerify =  getDevicesToVerify();

        if (!devicesToVerify.isEmpty()) {
            log.info("DeviceConfigVerificationTask is verifying " + devicesToVerify.size() + " devices");
            VerifyConfigCommandResult result = deviceConfigService.verifyConfigs(devicesToVerify, YukonUserContext.system.getYukonUser());
            if (log.isDebugEnabled()) {
                log.debug("DeviceConfigVerificationTask is verified devices ids= " + result.getVerifyResultsMap().keySet());
            }
        }
    }
    
    private List<SimpleDevice> getDevicesToVerify() {
        DeviceConfigSummaryFilter filter = new DeviceConfigSummaryFilter();

        List<LightDeviceConfiguration> configurations = deviceConfigurationDao.getAllLightDeviceConfigurations();
        filter.setConfigurationIds(configurations.stream()
            .map(LightDeviceConfiguration::getConfigurationId)
            .collect(Collectors.toList()));
        
        filter.setActions(Arrays.asList(LastAction.values()));
        filter.setStatuses(Arrays.asList(LastActionStatus.SUCCESS, LastActionStatus.FAILURE));
        filter.setInSync(Arrays.asList(InSync.UNVERIFIED));
        
        SearchResults<DeviceConfigSummaryDetail> details =
            deviceConfigSummaryDao.getSummary(filter, PagingParameters.EVERYTHING, SortBy.DEVICE_NAME, Direction.asc);
        
        return details.getResultList().stream()
                .map(detail -> new SimpleDevice(detail.getDevice()))
                .collect(Collectors.toList());
    }
}
