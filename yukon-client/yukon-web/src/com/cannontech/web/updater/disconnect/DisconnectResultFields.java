package com.cannontech.web.updater.disconnect;

import java.util.HashSet;
import java.util.Set;

import com.cannontech.amr.disconnect.model.DisconnectResult;
import com.cannontech.common.device.model.SimpleDevice;
import com.cannontech.common.util.ResolvableTemplate;
import com.cannontech.web.updater.ResultAccessor;

public enum DisconnectResultFields {

    ARMED_COUNT(new ResultAccessor<DisconnectResult>() {
        @Override
        public Object getValue(DisconnectResult result) {
            return result.getArmedCollection().getDeviceCount();
        }
    }),

    CONNECTED_COUNT(new ResultAccessor<DisconnectResult>() {
        @Override
        public Object getValue(DisconnectResult result) {
            return result.getConnectedCollection().getDeviceCount();
        }
    }),
    
    DISCONNECTED_COUNT(new ResultAccessor<DisconnectResult>() {
        @Override
        public Object getValue(DisconnectResult result) {
            return result.getDisconnectedCollection().getDeviceCount();
        }
    }),

    UNSUPPORTED_COUNT(new ResultAccessor<DisconnectResult>() {
        @Override
        public Object getValue(DisconnectResult result) {
            return result.getUnsupportedCollection().getDeviceCount();
        }
    }),

    NOT_CONFIGURED_COUNT(new ResultAccessor<DisconnectResult>() {
        @Override
        public Object getValue(DisconnectResult result) {
            return result.getNotConfiguredCollection().getDeviceCount();
        }
    }),

    FAILED_COUNT(new ResultAccessor<DisconnectResult>() {
        @Override
        public Object getValue(DisconnectResult result) {
            return result.getFailedCollection().getDeviceCount();
        }
    }),
    
    CANCELED_COUNT(new ResultAccessor<DisconnectResult>() {
        @Override
        public Object getValue(DisconnectResult result) {
            return result.getCanceledCollection().getDeviceCount();
        }
    }),

    COMPLETED_ITEMS(new ResultAccessor<DisconnectResult>() {
        @Override
        public Object getValue(DisconnectResult result) {
            Set<SimpleDevice> devices = new HashSet<>();
            devices.addAll(result.getArmedCollection().getDeviceList());
            devices.addAll(result.getConnectedCollection().getDeviceList());
            devices.addAll(result.getDisconnectedCollection().getDeviceList());
            devices.addAll(result.getUnsupportedCollection().getDeviceList());
            devices.addAll(result.getNotConfiguredCollection().getDeviceList());
            devices.addAll(result.getFailedCollection().getDeviceList());
            devices.addAll(result.getCanceledCollection().getDeviceList());
            return devices.size();
        }
    }),
    
    SUCCESS_COUNT(new ResultAccessor<DisconnectResult>() {
        @Override
        public Object getValue(DisconnectResult result) {
            return result.getSuccessCount();
        }
    }),
    
    NOT_ATTEMPTED_COUNT(new ResultAccessor<DisconnectResult>() {
        @Override
        public Object getValue(DisconnectResult result) {
            return result.getNotAttemptedCount();
        }
    }),

    IS_COMPLETE(new ResultAccessor<DisconnectResult>() {
        @Override
        public Object getValue(DisconnectResult result) {
            return result.isComplete();
        }
    }),

    IS_EXCEPTION_OCCURED(new ResultAccessor<DisconnectResult>() {
        @Override
        public Object getValue(DisconnectResult result) {
            return result.isExceptionOccured();
        }
    }),

    STATUS_TEXT(new ResultAccessor<DisconnectResult>() {
        @Override
        public Object getValue(DisconnectResult result) {

            ResolvableTemplate resolvableTemplate = null;

            if (result.isExceptionOccured()) {
                resolvableTemplate =
                    new ResolvableTemplate("yukon.web.modules.tools.bulk.disconnect.results.error");
                resolvableTemplate.addData("exceptionReason", result.getExceptionReason());
            } else {
                resolvableTemplate =
                    new ResolvableTemplate(result.getCommandRequestExecution().getCommandRequestExecutionStatus()
                        .getFormatKey());
            }
            return resolvableTemplate;
        }
    }),
    
    STATUS_CLASS(new ResultAccessor<DisconnectResult>() {
        @Override
        public Object getValue(DisconnectResult result) {
            return result.isExceptionOccured() ? "error" : "success";
        }
    }),
    ;

    private ResultAccessor<DisconnectResult> resultAccessor;

    DisconnectResultFields(ResultAccessor<DisconnectResult> resultAccessor) {
        this.resultAccessor = resultAccessor;
    }

    public Object getValue(DisconnectResult result) {
        return this.resultAccessor.getValue(result);
    }
}
