package com.cannontech.web.updater.commander;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.common.device.commands.GroupCommandExecutor;
import com.cannontech.common.device.commands.GroupCommandResult;
import com.cannontech.web.updater.RecentResultUpdateBackingService;

public class GroupCommandResultBackingService extends RecentResultUpdateBackingService {

    private GroupCommandExecutor groupCommandExecutor = null;
    
    @Override
    public Object getResultValue(String resultId, String resultTypeStr) {

        GroupCommandResult groupCommandResult = groupCommandExecutor.getResult(resultId);
       
       if (groupCommandResult == null) {
           return "";
       }
       GroupCommandResultFieldEnum groupCommandResultFieldEnum = GroupCommandResultFieldEnum.valueOf(resultTypeStr);
       return groupCommandResultFieldEnum.getValue(groupCommandResult);
    }
    
    @Autowired
    public void setGroupCommandExecutor(GroupCommandExecutor groupCommandExecutor) {
        this.groupCommandExecutor = groupCommandExecutor;
    }
}
