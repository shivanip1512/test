package com.cannontech.web.updater.demandreset;

import java.util.HashSet;
import java.util.Set;

import com.cannontech.amr.demandreset.model.DemandResetResult;
import com.cannontech.common.device.commands.dao.model.CommandRequestExecution;
import com.cannontech.common.device.model.SimpleDevice;
import com.cannontech.common.util.ResolvableTemplate;
import com.cannontech.web.updater.ResultAccessor;

public enum DemandResetResultField {

    CONFIRMED_COUNT(new ResultAccessor<DemandResetResult>() {
        @Override
        public Object getValue(DemandResetResult result) {
            return result.getConfirmedCollection().getDeviceCount();
        }
    }),

    UNCONFIRMED_COUNT(new ResultAccessor<DemandResetResult>() {
        @Override
        public Object getValue(DemandResetResult result) {
            return result.getUnconfirmedCollection().getDeviceCount();
        }
    }),
    
    UNSUPPORTED_COUNT(new ResultAccessor<DemandResetResult>() {
        @Override
        public Object getValue(DemandResetResult result) {
            return result.getUnsupportedCollection().getDeviceCount();
        }
    }),

    FAILED_COUNT(new ResultAccessor<DemandResetResult>() {
        @Override
        public Object getValue(DemandResetResult result) {
            return result.getFailedCollection().getDeviceCount();
        }
    }),
    
    CANCELED_COUNT(new ResultAccessor<DemandResetResult>() {
        @Override
        public Object getValue(DemandResetResult result) {
            return result.getCanceledCollection().getDeviceCount();
        }
    }),

    COMPLETED_ITEMS(new ResultAccessor<DemandResetResult>() {
        @Override
        public Object getValue(DemandResetResult result) {
            Set<SimpleDevice> devices = new HashSet<>();
            devices.addAll(result.getConfirmedCollection().getDeviceList());
            devices.addAll(result.getUnconfirmedCollection().getDeviceList());
            devices.addAll(result.getUnsupportedCollection().getDeviceList());
            devices.addAll(result.getFailedCollection().getDeviceList());
            devices.addAll(result.getCanceledCollection().getDeviceList());
            return devices.size();
        }
    }),
    
    SUCCESS_COUNT(new ResultAccessor<DemandResetResult>() {
        @Override
        public Object getValue(DemandResetResult result) {
            return result.getSuccessCount();
        }
    }),
    
    NOT_ATTEMPTED_COUNT(new ResultAccessor<DemandResetResult>() {
        @Override
        public Object getValue(DemandResetResult result) {
            return result.getNotAttemptedCount();
        }
    }),

    IS_COMPLETE(new ResultAccessor<DemandResetResult>() {
        @Override
        public Object getValue(DemandResetResult result) {
            return result.isComplete();
        }
    }),

    IS_EXCEPTION_OCCURED(new ResultAccessor<DemandResetResult>() {
        @Override
        public Object getValue(DemandResetResult result) {
            return result.isExceptionOccured();
        }
    }),
    
    CANCELLABLE(new ResultAccessor<DemandResetResult>() {
        @Override
        public Object getValue(DemandResetResult result) {
            return result.cancellable();
        }
    }),


    STATUS_TEXT(new ResultAccessor<DemandResetResult>() {
        @Override
        public Object getValue(DemandResetResult result) {

            ResolvableTemplate resolvableTemplate = null;

            if (result.isExceptionOccured()) {
                resolvableTemplate =
                    new ResolvableTemplate("yukon.web.modules.tools.bulk.demandReset.results.error");
                resolvableTemplate.addData("exceptionReason", result.getExceptionReason());
            } else {
                CommandRequestExecution execution  = result.getVerificationExecution();
                if(execution == null){
                    execution  = result.getInitiatedExecution();
                }
                resolvableTemplate = new ResolvableTemplate(execution.getCommandRequestExecutionStatus().getFormatKey());
            }
            return resolvableTemplate;
        }
    }),
    
    STATUS_CLASS(new ResultAccessor<DemandResetResult>() {
        @Override
        public Object getValue(DemandResetResult result) {
            return result.isExceptionOccured() ? "error" : "success";
        }
    }),
    ;

    private ResultAccessor<DemandResetResult> resultAccessor;

    DemandResetResultField(ResultAccessor<DemandResetResult> resultAccessor) {
        this.resultAccessor = resultAccessor;
    }

    public Object getValue(DemandResetResult result) {
        return this.resultAccessor.getValue(result);
    }
}

