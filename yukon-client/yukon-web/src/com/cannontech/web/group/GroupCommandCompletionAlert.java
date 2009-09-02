package com.cannontech.web.group;

import java.util.Date;

import com.cannontech.common.alert.model.AlertType;
import com.cannontech.common.alert.model.SimpleAlert;
import com.cannontech.common.device.commands.GroupCommandResult;
import com.cannontech.common.util.ResolvableTemplate;

public class GroupCommandCompletionAlert extends SimpleAlert {

    public GroupCommandCompletionAlert(Date date, GroupCommandResult result) {
        
        super(AlertType.GROUP_COMMAND_COMPLETION, date, makeMessage(date, result));
    }
    
    private static ResolvableTemplate makeMessage(Date date, GroupCommandResult result) {
        
        ResolvableTemplate resolvableTemplate = new ResolvableTemplate("yukon.common.alerts.commandCompletion");
        int successCount = result.getResultHolder().getResultStrings().size();
        resolvableTemplate.addData("successCount", successCount);
        int failureCount = result.getResultHolder().getErrors().size();
        resolvableTemplate.addData("failureCount", failureCount);
        int total = failureCount + successCount;
        resolvableTemplate.addData("percentSuccess", (float)successCount *100 / total);
        resolvableTemplate.addData("command", result.getCommand());
        resolvableTemplate.addData("resultKey", result.getKey());
        
        return resolvableTemplate;
    }
}
