package com.cannontech.web.updater.meterProgramming;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.core.service.DateFormattingService;
import com.cannontech.core.service.PorterDynamicPaoInfoService;
import com.cannontech.core.service.DateFormattingService.DateFormatEnum;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.tools.device.programming.dao.MeterProgrammingSummaryDao;
import com.cannontech.web.tools.device.programming.model.MeterProgramSummaryDetail;
import com.cannontech.web.tools.device.programming.model.MeterProgrammingSummaryFilter.DisplayableStatus;
import com.cannontech.web.updater.UpdateBackingService;

public class MeterProgrammingBackingService implements UpdateBackingService {
    
    @Autowired private PorterDynamicPaoInfoService porterDynamicPaoInfoService;
    @Autowired private MeterProgrammingSummaryDao meterProgrammingSummaryDao;
    @Autowired private DateFormattingService dateFormattingService;

    private enum RequestType {
        PROGRESS,
        IS_IN_PROGRESS,
        IS_CONFIRMING,
        LAST_UPDATED
        ;
    }
    
    @Override
    public String getLatestValue(String identifier, long afterDate, YukonUserContext userContext) {
        String[] idParts = StringUtils.split(identifier, "/");
        String deviceId = idParts[0];
        RequestType type = RequestType.valueOf(idParts[1]);
        if (type == RequestType.PROGRESS) {
            int progressValue = 0;
            var progress = porterDynamicPaoInfoService.getProgrammingProgress(Integer.valueOf(deviceId));
            if (progress != null && progress >= 0 && progress <= 100) {
                progressValue = progress.intValue();
            }
            return Integer.toString(progressValue);
        } else if (type == RequestType.IS_IN_PROGRESS) {
            MeterProgramSummaryDetail program = meterProgrammingSummaryDao.getProgramConfigurationByDeviceId(Integer.valueOf(deviceId), userContext);
            return Boolean.toString(program.getStatus() == DisplayableStatus.IN_PROGRESS);
        } else if (type == RequestType.IS_CONFIRMING) {
            MeterProgramSummaryDetail program = meterProgrammingSummaryDao.getProgramConfigurationByDeviceId(Integer.valueOf(deviceId), userContext);
            return Boolean.toString(program.getStatus() == DisplayableStatus.CONFIRMING);
        } else if (type == RequestType.LAST_UPDATED) {
            MeterProgramSummaryDetail program = meterProgrammingSummaryDao.getProgramConfigurationByDeviceId(Integer.valueOf(deviceId), userContext);
            return dateFormattingService.format(program.getLastUpdate(), DateFormatEnum.BOTH, userContext);
        }
        return null;
    }

    @Override
    public boolean isValueAvailableImmediately(String fullIdentifier, long afterDate, YukonUserContext userContext) {
        return true;
    }
}