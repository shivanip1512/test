package com.cannontech.web.updater.scheduledGroupRequestExecution.handler;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.amr.scheduledGroupRequestExecution.dao.ScheduledGroupRequestExecutionStatus;
import com.cannontech.amr.scheduledGroupRequestExecution.dao.model.ScheduledGroupRequestExecutionBundle;
import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.updater.scheduledGroupRequestExecution.ScheduledGroupCommandRequestExecutionUpdaterTypeEnum;

public class LastTooltipHandler implements ScheduledGroupRequestExecutionUpdaterHandler {
    private YukonUserContextMessageSourceResolver messageSourceResolver;

    @Override
    public String handle(ScheduledGroupRequestExecutionBundle execution, YukonUserContext userContext) {
        MessageSourceAccessor messageSourceAccessor = messageSourceResolver.getMessageSourceAccessor(userContext);
        ScheduledGroupRequestExecutionStatus status = execution.getStatus();
        int successCount = execution.getExecutionCounts().getSuccessCount(); 
        int failureCount = execution.getExecutionCounts().getFailureCount();
        int totalCount = execution.getExecutionCounts().getTotalCount();

        String tooltipString;
        if (status == ScheduledGroupRequestExecutionStatus.RUNNING) {
            tooltipString = messageSourceAccessor.getMessage("yukon.web.widgets.schedules.tooltip.running",
                                successCount,
                                failureCount,
                                totalCount);
        } else {
            tooltipString = messageSourceAccessor.getMessage("yukon.web.widgets.schedules.tooltip.notRunning",
                                successCount,
                                failureCount,
                                totalCount);
        }

        return tooltipString;
    }

    @Override
    public ScheduledGroupCommandRequestExecutionUpdaterTypeEnum getUpdaterType() {
        return ScheduledGroupCommandRequestExecutionUpdaterTypeEnum.LAST_TOOLTIP_TEXT_FOR_JOB;
    }

    @Autowired
    public void setMessageSourceResolver(YukonUserContextMessageSourceResolver messageSourceResolver) {
        this.messageSourceResolver = messageSourceResolver;
    }
}
