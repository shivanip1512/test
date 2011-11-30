package com.cannontech.capcontrol.service;

import com.cannontech.capcontrol.message.IvvcAnalysisMessage;
import com.cannontech.user.YukonUserContext;

public interface IvvcAnalysisScenarioMsgFormatter {

    /**
     * Refers to int IvvcAnalysisScenarioType.formatType
     */
    public int getFormatTypeId();
    public String format(IvvcAnalysisMessage message, YukonUserContext userContext);

}
