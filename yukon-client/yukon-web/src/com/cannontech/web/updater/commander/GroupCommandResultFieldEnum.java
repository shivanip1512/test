package com.cannontech.web.updater.commander;

import com.cannontech.common.device.commands.GroupCommandResult;



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
    
    ;
    
    private GroupCommandResultAccessor groupCommandResultAccessor;
    
    GroupCommandResultFieldEnum(GroupCommandResultAccessor groupCommandResultAccessor) {
        this.groupCommandResultAccessor = groupCommandResultAccessor;
    }
    
    public Object getField(GroupCommandResult groupCommandResult) {
        return this.groupCommandResultAccessor.getField(groupCommandResult);
    }
}
