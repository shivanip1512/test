package com.cannontech.capcontrol.service;

import com.cannontech.capcontrol.message.IvvcAnalysisMessage;
import com.cannontech.user.YukonUserContext;

public interface IvvcAnalysisScenarioMsgFormatter {

    public IvvcAnalysisFormatType getFormatType();

    /**
     * Returns an html-injected string ready to be displayed on the web
     */
    public String format(IvvcAnalysisMessage message, YukonUserContext userContext);

}
