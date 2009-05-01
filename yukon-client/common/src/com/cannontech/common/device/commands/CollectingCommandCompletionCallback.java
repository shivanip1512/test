package com.cannontech.common.device.commands;

import java.util.ArrayList;
import java.util.List;

import com.cannontech.amr.errors.model.DeviceErrorDescription;
import com.cannontech.common.util.CancelStatus;
import com.cannontech.common.util.Completable;
import com.cannontech.common.util.ExceptionStatus;
import com.cannontech.core.dynamic.PointValueHolder;

public class CollectingCommandCompletionCallback implements
        CommandCompletionCallback<Object>, CommandResultHolder, Completable, CancelStatus, ExceptionStatus {
    
    private List<DeviceErrorDescription> errors = new ArrayList<DeviceErrorDescription>();
    private List<PointValueHolder> values = new ArrayList<PointValueHolder>();
    private List<String> resultStrings = new ArrayList<String>();
    private boolean complete = false;
    private boolean canceled = false;
    private boolean processingErrorOccured = false;
    private String processingErrorReason = "";

    @Override
    public void receivedIntermediateError(Object command, DeviceErrorDescription error) {
        // ignore
    }

    @Override
    public void receivedIntermediateResultString(Object command, String value) {
        // ignore
    }

    @Override
    public void receivedLastError(Object command, DeviceErrorDescription error) {
        errors.add(error);
    }

    @Override
    public void receivedLastResultString(Object command, String value) {
        resultStrings.add(value);
    }

    @Override
    public void receivedValue(Object command, PointValueHolder value) {
        values.add(value);
    }

    public boolean isErrorsExist() {
        return !errors.isEmpty();
    }

    public List<DeviceErrorDescription> getErrors() {
        return errors;
    }

    public List<PointValueHolder> getValues() {
        return values;
    }
    
    public List<String> getResultStrings() {
        return resultStrings;
    }
    
    public String getLastResultString() {
        
        if(resultStrings.size() > 0){
            return resultStrings.get(resultStrings.size() - 1);
        }
        
        return "";
    }
    
    @Override
    final public void complete() {
        complete = true;
        doComplete();
    }
    
    @Override
    final public void cancel() {
        canceled = true;
        complete();
    }
    
    @Override
    final public void processingExceptionOccured(String reason) {
    	processingErrorReason = reason;
    	processingErrorOccured = true;
    }
    
    @Override
    public boolean isComplete() {
        return complete;
    }
    
    @Override
    public boolean isCanceled() {
        return canceled;
    }

    protected void doComplete() {
        // noop
    }
    
    @Override
    public boolean hasException() {
    	return processingErrorOccured;
    }
    
    @Override
    public String getExceptionReason() {
    	return processingErrorReason;
    }
    
}
