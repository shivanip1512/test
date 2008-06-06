package com.cannontech.web.updater.commander;

import com.cannontech.common.device.commands.GroupCommandResult;
import com.cannontech.common.util.ResolvableTemplate;



public enum GroupCommandResultFieldEnum {

    SUCCESS_COUNT(new GroupCommandResultAccessor() {
        public Object getField(GroupCommandResult groupCommandResult) {
            return groupCommandResult.getResultHolder().getSuccessfulDevices().size();
        }
    }),
    
    FAILURE_COUNT(new GroupCommandResultAccessor() {
        public Object getField(GroupCommandResult groupCommandResult) {
            return groupCommandResult.getResultHolder().getFailedDevices().size();
        }
    }),
    
    COMPLETED_ITEMS(new GroupCommandResultAccessor() {
        public Object getField(GroupCommandResult groupCommandResult) {
            
            return (groupCommandResult.getResultHolder().getSuccessfulDevices().size() + groupCommandResult.getResultHolder().getFailedDevices().size());
        }
    }),
    
    IS_COMPLETE(new GroupCommandResultAccessor() {
        public Object getField(GroupCommandResult groupCommandResult) {
            
            ResolvableTemplate resolvableTemplate = new ResolvableTemplate("yukon.common.device.commander.groupCommandExecutor.IS_COMPLETE");
            resolvableTemplate.addData("isComplete", groupCommandResult.isComplete());
            
            return resolvableTemplate;
        }
    }),
    
    ;
    
    private GroupCommandResultAccessor groupCommandResultAccessor;
    
    GroupCommandResultFieldEnum(GroupCommandResultAccessor groupCommandResultAccessor) {
        this.groupCommandResultAccessor = groupCommandResultAccessor;
    }
    
    public Object getField(GroupCommandResult groupCommandResult) {
        return this.groupCommandResultAccessor.getField(groupCommandResult);
    }
}
