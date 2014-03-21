package com.cannontech.web.updater.validationProcessing.handler;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.common.validation.dao.RphTagUiDao;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.updater.validationProcessing.ValidationMonitorUpdaterTypeEnum;

public class TotalViolationsProcessor implements ValidationProcessingUpdaterHandler {

    @Autowired private RphTagUiDao rphTagUiDao;

    @Override
    public String handle(int validationMonitorId, YukonUserContext userContext) {
        Integer totalViolations = rphTagUiDao.getTotalValidationTagCounts();
        return totalViolations.toString();
    }

    @Override
    public ValidationMonitorUpdaterTypeEnum getUpdaterType() {
        return ValidationMonitorUpdaterTypeEnum.TOTAL_VIOLATIONS;
    }
}