package com.cannontech.web.group;

import java.util.Date;

import org.apache.commons.lang3.StringEscapeUtils;

import com.cannontech.common.alert.model.AlertType;
import com.cannontech.common.alert.model.SimpleAlert;
import com.cannontech.common.device.commands.GroupCommandResult;
import com.cannontech.common.util.ResolvableTemplate;

public class GroupCommandCompletionAlert extends SimpleAlert {

    public GroupCommandCompletionAlert(Date date, GroupCommandResult result) {
        
        super(AlertType.GROUP_COMMAND_COMPLETION, date, makeMessage(result));
    }
    
    private static ResolvableTemplate makeMessage(GroupCommandResult result) {

        int deviceCount = (int) result.getDeviceCollection().getDeviceCount();
        int successCount = result.getResultHolder().getResultStrings().size();
        int failureCount = result.getResultHolder().getErrors().size();
        int completedCount = failureCount + successCount;
        int notCompletedCount = deviceCount - completedCount;

        ResolvableTemplate resolvableTemplate;

        if (result.isAborted() && !result.isCanceled()) {
            if (completedCount == 0) {
                resolvableTemplate = new ResolvableTemplate("yukon.common.alerts.commandCompletion.failed.noneSucceeded");
            } else {
                resolvableTemplate = new ResolvableTemplate("yukon.common.alerts.commandCompletion.failed");
            }
        } else if (result.isAborted() && result.isCanceled()) {
            if (completedCount == 0) {
                resolvableTemplate = new ResolvableTemplate("yukon.common.alerts.commandCompletion.canceled.noneSucceeded");
            } else {
                resolvableTemplate = new ResolvableTemplate("yukon.common.alerts.commandCompletion.canceled");
            }
            resolvableTemplate.addData("notCompletedCount", notCompletedCount);
        } else {
            resolvableTemplate = new ResolvableTemplate("yukon.common.alerts.commandCompletion");
        }

        resolvableTemplate.addData("command", StringEscapeUtils.escapeHtml4(result.getCommand()));
        resolvableTemplate.addData("exceptionReason", result.getExceptionReason());
        resolvableTemplate.addData("notCompletedCount", notCompletedCount);
        resolvableTemplate.addData("percentSuccess", completedCount > 0 ? ((float) successCount * 100 / completedCount) : 0);
        resolvableTemplate.addData("completedCount", completedCount);
        resolvableTemplate.addData("resultKey", result.getKey());

        return resolvableTemplate;
    }
}
