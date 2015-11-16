package com.cannontech.web.updater.groupMeterRead;

import java.util.HashSet;
import java.util.Set;

import com.cannontech.amr.deviceread.service.GroupMeterReadResult;
import com.cannontech.common.device.model.SimpleDevice;
import com.cannontech.common.util.ResolvableTemplate;
import com.cannontech.web.updater.ResultAccessor;

public enum GroupMeterReadResultFieldEnum {

    SUCCESS_COUNT(new ResultAccessor<GroupMeterReadResult>() {
        @Override
        public Object getValue(GroupMeterReadResult groupMeterReadResult) {
            return groupMeterReadResult.getResultHolder().getSuccessfulDevices().size();
        }
    }),

    FAILURE_COUNT(new ResultAccessor<GroupMeterReadResult>() {
        @Override
        public Object getValue(GroupMeterReadResult groupMeterReadResult) {
            return groupMeterReadResult.getResultHolder().getFailedDevices().size();
        }
    }),

    UNSUPPORTED_COUNT(new ResultAccessor<GroupMeterReadResult>() {
        @Override
        public Object getValue(GroupMeterReadResult groupMeterReadResult) {
            return groupMeterReadResult.getUnsupportedCollection().getDeviceCount();
        }
    }),

    COMPLETED_ITEMS(new ResultAccessor<GroupMeterReadResult>() {
        @Override
        public Object getValue(GroupMeterReadResult groupMeterReadResult) {

            Set<SimpleDevice> completedDevices = new HashSet<>();
            completedDevices.addAll(groupMeterReadResult.getResultHolder().getSuccessfulDevices());
            completedDevices.addAll(groupMeterReadResult.getResultHolder().getFailedDevices());
            if(groupMeterReadResult.isComplete()){
                completedDevices.addAll(groupMeterReadResult.getUnsupportedCollection().getDeviceList());
            }      
            return completedDevices.size();
        }
    }),

    IS_COMPLETE(new ResultAccessor<GroupMeterReadResult>() {
        @Override
        public Object getValue(GroupMeterReadResult groupMeterReadResult) {
            return groupMeterReadResult.isComplete();
        }
    }),

    IS_EXCEPTION_OCCURED(new ResultAccessor<GroupMeterReadResult>() {
        @Override
        public Object getValue(GroupMeterReadResult groupMeterReadResult) {
            return groupMeterReadResult.isExceptionOccured();
        }
    }),

    STATUS_TEXT(new ResultAccessor<GroupMeterReadResult>() {
        @Override
        public Object getValue(GroupMeterReadResult groupCommandResult) {

            ResolvableTemplate resolvableTemplate = null;

            if (groupCommandResult.isSuccessfullyComplete()) {
                resolvableTemplate = new ResolvableTemplate("yukon.common.device.groupMeterRead.groupMeterReadResult.IS_SUCCESSFULLY_COMPLETE_TEXT");
            } else if (groupCommandResult.isExceptionOccured()) {
                resolvableTemplate = new ResolvableTemplate("yukon.common.device.groupMeterRead.groupMeterReadResult.IS_EXCEPTION_OCCURED_TEXT");
                resolvableTemplate.addData("exceptionReason", groupCommandResult.getExceptionReason());
            } else {
                resolvableTemplate = new ResolvableTemplate("yukon.common.device.groupMeterRead.groupMeterReadResult.IS_IN_PROGRESS_TEXT");
            }

            return resolvableTemplate;
        }
    }),

    STATUS_CLASS(new ResultAccessor<GroupMeterReadResult>() {
        @Override
        public Object getValue(GroupMeterReadResult groupMeterReadResult) {
            return groupMeterReadResult.isExceptionOccured() ? "error" : "success";
        }
    }),

    ;

    private ResultAccessor<GroupMeterReadResult> groupMeterReadResultAccessor;

    GroupMeterReadResultFieldEnum(ResultAccessor<GroupMeterReadResult> groupMeterReadResultAccessor) {
        this.groupMeterReadResultAccessor = groupMeterReadResultAccessor;
    }

    public Object getValue(GroupMeterReadResult groupMeterReadResult) {
        return this.groupMeterReadResultAccessor.getValue(groupMeterReadResult);
    }
}
