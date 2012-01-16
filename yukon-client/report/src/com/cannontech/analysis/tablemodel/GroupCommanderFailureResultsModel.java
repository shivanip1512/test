package com.cannontech.analysis.tablemodel;

import java.util.LinkedHashMap;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.common.device.commands.GroupCommandExecutor;
import com.cannontech.common.device.commands.GroupCommandResult;
import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.user.YukonUserContext;


public class GroupCommanderFailureResultsModel extends GroupFailureResultsModelBase {
    
    private GroupCommandExecutor groupCommandExecutor;
    private YukonUserContextMessageSourceResolver messageSourceResolver;
    private static String title;
    private String command;
    
    public void doLoadData() {
        GroupCommandResult result = groupCommandExecutor.getResult(resultKey);
        
        // command
        this.command = result.getCommand();
        doLoadData(result);
    }
    
    @Override
    public LinkedHashMap<String, String> getMetaInfo(YukonUserContext context) {
        MessageSourceAccessor accessor = messageSourceResolver.getMessageSourceAccessor(context);
        LinkedHashMap<String, String> info = new LinkedHashMap<String, String>();
        info.put(accessor.getMessage(baseKey + "command"), this.command);
        title = accessor.getMessage(baseKey + "groupCommandFailure.title");
        return info;
    }

    public String getTitle() {
        return title;
    }
    
    @Autowired
    public void setGroupCommandExecutor(GroupCommandExecutor groupCommandExecutor) {
        this.groupCommandExecutor = groupCommandExecutor;
    }
    
    @Autowired
    public void setMessageSourceResolver(YukonUserContextMessageSourceResolver messageSourceResolver) {
        this.messageSourceResolver = messageSourceResolver;
    }
}