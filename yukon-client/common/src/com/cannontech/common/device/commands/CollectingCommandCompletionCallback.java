package com.cannontech.common.device.commands;

import java.util.ArrayList;
import java.util.List;

import com.cannontech.common.device.commands.dao.model.CommandRequestExecutionIdentifier;
import com.cannontech.common.device.commands.impl.SpecificDeviceErrorDescription;
import com.cannontech.common.util.CancelStatus;
import com.cannontech.common.util.Completable;
import com.cannontech.core.dynamic.PointValueHolder;

public class CollectingCommandCompletionCallback implements
        CommandCompletionCallback<Object>, CommandResultHolder, Completable, CancelStatus {
    
	private CommandRequestExecutionIdentifier commandRequestExecutionIdentifier = null;
    private List<SpecificDeviceErrorDescription> errors = new ArrayList<SpecificDeviceErrorDescription>();
    private List<PointValueHolder> values = new ArrayList<PointValueHolder>();
    private List<String> resultStrings = new ArrayList<String>();
    private boolean complete = false;
    private boolean canceled = false;
    private boolean processingErrorOccured = false;
    private String processingErrorReason = "";

    @Override
    public void receivedIntermediateError(Object command, SpecificDeviceErrorDescription error) {
        // ignore
    }

    @Override
    public void receivedIntermediateResultString(Object command, String value) {
        // ignore
    }

    @Override
    public void receivedLastError(Object command, SpecificDeviceErrorDescription error) {
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

    @Override
    public CommandRequestExecutionIdentifier getCommandRequestExecutionIdentifier() {
    	return this.commandRequestExecutionIdentifier;
    }
    
    public void setCommandRequestExecutionIdentifier(
			CommandRequestExecutionIdentifier commandRequestExecutionIdentifier) {
		this.commandRequestExecutionIdentifier = commandRequestExecutionIdentifier;
	}
    
    @Override
    public boolean isAnyErrorOrException() {
    	
    	return isExceptionOccured() || isErrorsExist();
    }
    
    public boolean isErrorsExist() {
        return !errors.isEmpty();
    }

    public List<SpecificDeviceErrorDescription> getErrors() {
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
    public boolean isExceptionOccured() {
    	return processingErrorOccured;
    }
    
    @Override
    public String getExceptionReason() {
    	return processingErrorReason;
    }
    
}
