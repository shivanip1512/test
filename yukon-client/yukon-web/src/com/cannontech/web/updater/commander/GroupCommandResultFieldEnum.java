package com.cannontech.web.updater.commander;

import com.cannontech.common.device.commands.GroupCommandResult;
import com.cannontech.common.util.ResolvableTemplate;
import com.cannontech.web.updater.ResultAccessor;



public enum GroupCommandResultFieldEnum {

    SUCCESS_COUNT(new ResultAccessor<GroupCommandResult>() {
        public Object getValue(GroupCommandResult groupCommandResult) {
            return groupCommandResult.getResultHolder().getSuccessfulDevices().size();
        }
    }),
    
    FAILURE_COUNT(new ResultAccessor<GroupCommandResult>() {
        public Object getValue(GroupCommandResult groupCommandResult) {
            return groupCommandResult.getResultHolder().getFailedDevices().size();
        }
    }),
    
    COMPLETED_ITEMS(new ResultAccessor<GroupCommandResult>() {
        public Object getValue(GroupCommandResult groupCommandResult) {
            
            return (groupCommandResult.getResultHolder().getSuccessfulDevices().size() + groupCommandResult.getResultHolder().getFailedDevices().size());
        }
    }),
    
    IS_COMPLETE(new ResultAccessor<GroupCommandResult>() {
        public Object getValue(GroupCommandResult groupCommandResult) {
            return groupCommandResult.isComplete();
        }
    }),
    
    IS_COMPLETE_TEXT(new ResultAccessor<GroupCommandResult>() {
        public Object getValue(GroupCommandResult groupCommandResult) {
            
            ResolvableTemplate resolvableTemplate = new ResolvableTemplate("yukon.common.device.commander.groupCommandExecutor.IS_COMPLETE_TEXT");
            resolvableTemplate.addData("isComplete", groupCommandResult.isComplete());
            
            return resolvableTemplate;
        }
    }),
    
    IS_CANCELED(new ResultAccessor<GroupCommandResult>() {
        public Object getValue(GroupCommandResult groupCommandResult) {
            return groupCommandResult.isCanceled();
        }
    }),
    
    IS_CANCELED_TEXT(new ResultAccessor<GroupCommandResult>() {
        public Object getValue(GroupCommandResult groupCommandResult) {
            
            ResolvableTemplate resolvableTemplate = new ResolvableTemplate("yukon.common.device.commander.groupCommandExecutor.IS_CANCELED_TEXT");
            resolvableTemplate.addData("isCanceled", groupCommandResult.isCanceled());
            
            return resolvableTemplate;
        }
    }),
    
    ;
    
    private ResultAccessor<GroupCommandResult> groupCommandResultAccessor;
    
    GroupCommandResultFieldEnum(ResultAccessor<GroupCommandResult> groupCommandResultAccessor) {
        this.groupCommandResultAccessor = groupCommandResultAccessor;
    }
    
    public Object getValue(GroupCommandResult groupCommandResult) {
        return this.groupCommandResultAccessor.getValue(groupCommandResult);
    }
}
