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
    	if (result.isAborted()) {
        	resolvableTemplate = new ResolvableTemplate("yukon.common.alerts.commandCompletion.failed");
        	if (result.isCanceled()) {
        		resolvableTemplate = new ResolvableTemplate("yukon.common.alerts.commandCompletion.canceled");
        	}
    	}
        	
    	int successCount = result.getResultHolder().getResultStrings().size();
    	int failureCount = result.getResultHolder().getErrors().size();
    	int completedCount = failureCount + successCount;
    	
        resolvableTemplate.addData("completedCount", completedCount);
        resolvableTemplate.addData("percentSuccess", (float)successCount *100 / completedCount);
        resolvableTemplate.addData("command", result.getCommand());
        resolvableTemplate.addData("resultKey", result.getKey());
        
        if (result.isAborted()) {
        	
        	int deviceCount = (int)result.getDeviceCollection().getDeviceCount();
        	int notCompletedCount = deviceCount - completedCount;
        	String exceptionReason = result.getExceptionReason();
        	
        	resolvableTemplate.addData("notCompletedCount", notCompletedCount);
        	resolvableTemplate.addData("exceptionReason", exceptionReason);
        }
        
        return resolvableTemplate;
    }
}
