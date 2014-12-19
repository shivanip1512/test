package com.cannontech.common.device.commands;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.cannontech.amr.errors.model.SpecificDeviceErrorDescription;
import com.cannontech.common.device.commands.dao.model.CommandRequestExecutionIdentifier;
import com.cannontech.common.util.CancelStatus;
import com.cannontech.common.util.Completable;
import com.cannontech.core.dynamic.PointValueHolder;

public class CollectingCommandCompletionCallback implements
        CommandCompletionCallback<Object>, CommandResultHolder, Completable, CancelStatus {
    
	private CommandRequestExecutionIdentifier commandRequestExecutionIdentifier = null;
    private final Set<SpecificDeviceErrorDescription> errors = new HashSet<>();
    private final List<PointValueHolder> values = new ArrayList<PointValueHolder>();
    private final List<String> resultStrings = new ArrayList<String>();
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
    
    @Override
    public boolean isErrorsExist() {
        return !errors.isEmpty();
    }

    @Override
    public Set<SpecificDeviceErrorDescription> getErrors() {
        return errors;
    }

    @Override
    public List<PointValueHolder> getValues() {
        return values;
    }
    
    @Override
    public List<String> getResultStrings() {
        return resultStrings;
    }
    
    @Override
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
