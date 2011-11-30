package com.cannontech.web.updater.capcontrol.handler;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.capcontrol.service.IvvcAnalysisMessageService;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.updater.capcontrol.CapControlUpdaterTypeEnum;

public class IvvcAnalysisMessageUpdaterHandler implements CapControlUpdaterHandler {

    private IvvcAnalysisMessageService ivvcAnalysisMessageService;

    @Override
    public String handle(int subBusId, YukonUserContext userContext) {
        String msg = ivvcAnalysisMessageService.getMessageForSubBusId(subBusId, userContext);
        return msg != null ? msg : StringUtils.EMPTY;
    }

    @Override
    public CapControlUpdaterTypeEnum getUpdaterType() {
        return CapControlUpdaterTypeEnum.IVVC_ANALYSIS_MESSAGE;
    }

    @Autowired
    public void setIvvcAnalysisMessageService(IvvcAnalysisMessageService ivvcAnalysisMessageService) {
        this.ivvcAnalysisMessageService = ivvcAnalysisMessageService;
    }
}
