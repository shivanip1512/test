package com.cannontech.web.updater.scheduledGroupRequestExecution.handler;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.amr.scheduledGroupRequestExecution.dao.ScheduledGroupRequestExecutionDao;
import com.cannontech.amr.scheduledGroupRequestExecution.dao.ScheduledGroupRequestExecutionStatus;
import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.updater.scheduledGroupRequestExecution.ScheduledGroupCommandRequestExecutionUpdaterTypeEnum;

public class LastTooltipForJobUpdateHandler implements ScheduledGroupRequestExecutionUpdaterHandler {

    private ScheduledGroupRequestExecutionDao scheduledGroupRequestExecutionDao;
    private YukonUserContextMessageSourceResolver messageSourceResolver;

    @Override
    public String handle(int id, YukonUserContext userContext) {
        MessageSourceAccessor messageSourceAccessor = messageSourceResolver.getMessageSourceAccessor(userContext);
        ScheduledGroupRequestExecutionStatus status = scheduledGroupRequestExecutionDao.getStatusByJobId(id);
        int successCount = scheduledGroupRequestExecutionDao.getLatestSuccessCountByJobId(id);
        int failureCount = scheduledGroupRequestExecutionDao.getLatestFailCountByJobId(id);
        int totalCount = scheduledGroupRequestExecutionDao.getLatestRequestCountByJobId(id);

        String tooltipString;
        if (status == ScheduledGroupRequestExecutionStatus.RUNNING) {
            tooltipString = messageSourceAccessor.getMessage("yukon.web.widgets.scheduledGroupRequstExecutionWidget.tooltip.running",
                                successCount,
                                failureCount,
                                totalCount);
        } else {
            tooltipString = messageSourceAccessor.getMessage("yukon.web.widgets.scheduledGroupRequstExecutionWidget.tooltip.notRunning",
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
    public void setScheduledGroupRequestExecutionDao(ScheduledGroupRequestExecutionDao scheduledGroupRequestExecutionDao) {
        this.scheduledGroupRequestExecutionDao = scheduledGroupRequestExecutionDao;
    }

    @Autowired
    public void setMessageSourceResolver(YukonUserContextMessageSourceResolver messageSourceResolver) {
        this.messageSourceResolver = messageSourceResolver;
    }
}
