package com.cannontech.web.updater.commander;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.common.device.commands.GroupCommandResult;
import com.cannontech.common.device.commands.service.GroupCommandExecutionService;
import com.cannontech.common.util.ResultExpiredException;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.updater.RecentResultUpdateBackingService;

public class GroupCommandResultBackingService extends RecentResultUpdateBackingService {

    @Autowired private GroupCommandExecutionService groupCommandExecutionService;
    
    @Override
    public Object getResultValue(String resultId, String resultTypeStr) {

        GroupCommandResult groupCommandResult = null;
        try {
        	groupCommandResult = groupCommandExecutionService.getResult(resultId);
        } catch (ResultExpiredException e) {
        	return "";
        }
       
       GroupCommandResultFieldEnum groupCommandResultFieldEnum = GroupCommandResultFieldEnum.valueOf(resultTypeStr);
       return groupCommandResultFieldEnum.getValue(groupCommandResult);
    }
    
    @Override
    public boolean isValueAvailableImmediately(String fullIdentifier,
    		long afterDate, YukonUserContext userContext) {
    	return true;
    }
}
