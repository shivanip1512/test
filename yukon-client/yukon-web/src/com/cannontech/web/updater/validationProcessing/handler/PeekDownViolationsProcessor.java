package com.cannontech.web.updater.validationProcessing.handler;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.common.validation.dao.RphTagUiDao;
import com.cannontech.common.validation.model.RphTag;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.updater.validationProcessing.ValidationMonitorUpdaterTypeEnum;

public class PeekDownViolationsProcessor implements ValidationProcessingUpdaterHandler {

    @Autowired private RphTagUiDao rphTagUiDao;

    @Override
    public String handle(int validationMonitorId, YukonUserContext userContext) {
        String countStr = "(0)";
        Map<RphTag, Integer> tagCounts = rphTagUiDao.getAllValidationTagCounts();
        if (tagCounts.get(RphTag.PD) != null) {
            countStr = tagCounts.get(RphTag.PD).toString();
        }
        return countStr;
    }

    @Override
    public ValidationMonitorUpdaterTypeEnum getUpdaterType() {
        return ValidationMonitorUpdaterTypeEnum.PD_VIOLATIONS;
    }
}