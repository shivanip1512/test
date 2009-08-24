package com.cannontech.web.group;

import java.util.Date;

import com.cannontech.common.alert.model.AlertType;
import com.cannontech.common.alert.model.BaseAlert;
import com.cannontech.common.device.commands.GroupCommandResult;
import com.cannontech.common.util.ResolvableTemplate;

public class GroupCommandCompletionAlert extends BaseAlert {

    public GroupCommandCompletionAlert(Date date, GroupCommandResult result) {
        
        super(date, null);
        
        this.setMessage(makeMessage(date, result));
    }
    
    private ResolvableTemplate makeMessage(Date date, GroupCommandResult result) {
        
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

    @Override
    public AlertType getType() {
        return AlertType.GROUP_COMMAND_COMPLETION;
    }
}
