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
    
    UNSUPPORTED_COUNT(new ResultAccessor<GroupCommandResult>() {
        public Object getValue(GroupCommandResult groupCommandResult) {
            return groupCommandResult.getUnsupportedCollection().getDeviceCount();
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
    
    IS_CANCELED(new ResultAccessor<GroupCommandResult>() {
        public Object getValue(GroupCommandResult groupCommandResult) {
            return groupCommandResult.isCanceled();
        }
    }),
    
    IS_ABORTED(new ResultAccessor<GroupCommandResult>() {
        public Object getValue(GroupCommandResult groupCommandResult) {
            return groupCommandResult.isAborted();
        }
    }),
    
    IS_EXCEPTION_OCCURED(new ResultAccessor<GroupCommandResult>() {
        public Object getValue(GroupCommandResult groupCommandResult) {
            return groupCommandResult.isExceptionOccured();
        }
    }),
    
    STATUS_TEXT(new ResultAccessor<GroupCommandResult>() {
        public Object getValue(GroupCommandResult groupCommandResult) {
        	
        	ResolvableTemplate resolvableTemplate = null;
        	
        	if (groupCommandResult.isSuccessfullyComplete()) {
        		resolvableTemplate = new ResolvableTemplate("yukon.common.device.commander.groupCommandExecutor.IS_SUCCESSFULLY_COMPLETE_TEXT");
        	} else if (groupCommandResult.isCanceled()) {
        		resolvableTemplate = new ResolvableTemplate("yukon.common.device.commander.groupCommandExecutor.IS_CANCELED_TEXT");
        	} else if (groupCommandResult.isExceptionOccured()) {
        		resolvableTemplate = new ResolvableTemplate("yukon.common.device.commander.groupCommandExecutor.IS_EXCEPTION_OCCURED_TEXT");
        		resolvableTemplate.addData("exceptionReason", groupCommandResult.getExceptionReason());
        	} else {
        		resolvableTemplate = new ResolvableTemplate("yukon.common.device.commander.groupCommandExecutor.IS_IN_PROGRESS_TEXT");
        	}
        	
            return resolvableTemplate;
        }
    }),
    
    STATUS_CLASS(new ResultAccessor<GroupCommandResult>() {
        public Object getValue(GroupCommandResult groupCommandResult) {
        	
        	String className = "";
        	if (groupCommandResult.isCanceled() || groupCommandResult.isExceptionOccured()) {
        		className = "error";
        	} else {
        		className = "success";
        	}
        	
        	return className;
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
