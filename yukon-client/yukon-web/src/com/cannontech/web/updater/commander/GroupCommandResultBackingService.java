package com.cannontech.web.updater.commander;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.common.device.commands.GroupCommandExecutor;
import com.cannontech.common.device.commands.GroupCommandResult;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.updater.UpdateBackingService;

public class GroupCommandResultBackingService implements UpdateBackingService {

    private GroupCommandExecutor groupCommandExecutor = null;
    
    @Override
    public String getLatestValue(String identifier, long afterDate, YukonUserContext userContext) {

        // split identifier
        String[] idParts = StringUtils.split(identifier, "/", 2);
        String resultKey = idParts[0];
        String resultTypeStr = idParts[1];
        
        // get result
        GroupCommandResult result = groupCommandExecutor.getResult(resultKey);
        
        // get count
        GroupCommandResultFieldEnum fieldEnum = GroupCommandResultFieldEnum.valueOf(resultTypeStr);
        Object fieldValue = fieldEnum.getField(result);
        
        return fieldValue.toString();
    }

    @Autowired
    public void setGroupCommandExecutor(GroupCommandExecutor groupCommandExecutor) {
        this.groupCommandExecutor = groupCommandExecutor;
    }
}
