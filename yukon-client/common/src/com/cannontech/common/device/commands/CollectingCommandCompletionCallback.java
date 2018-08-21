package com.cannontech.common.device.commands;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.cannontech.amr.errors.model.SpecificDeviceErrorDescription;
import com.cannontech.common.device.commands.dao.model.CommandRequestExecutionIdentifier;
import com.cannontech.common.util.Cancelable;
import com.cannontech.common.util.Completable;
import com.cannontech.core.dynamic.PointValueHolder;

public class CollectingCommandCompletionCallback extends
        CommandCompletionCallback<CommandRequestBase> implements CommandResultHolder, Completable, Cancelable {
    
	private CommandRequestExecutionIdentifier commandRequestExecutionIdentifier = null;
    private final Set<SpecificDeviceErrorDescription> errors = new HashSet<>();
    private final List<PointValueHolder> values = new ArrayList<>();
    private final List<String> resultStrings = new ArrayList<>();
    private boolean complete = false;
    private boolean canceled = false;
    private boolean processingErrorOccurred = false;
    private String processingErrorReason = "";

    @Override
    public void receivedLastError(CommandRequestBase command, SpecificDeviceErrorDescription error) {
        errors.add(error);
    }

    @Override
    public void receivedLastResultString(CommandRequestBase command, String value) {
        resultStrings.add(value);
    }

    @Override
    public void receivedValue(CommandRequestBase command, PointValueHolder value) {
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
    	
    	return isExceptionOccurred() || isErrorsExist();
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
    final public void processingExceptionOccurred(String reason) {
    	processingErrorReason = reason;
    	processingErrorOccurred = true;
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
    public boolean isExceptionOccurred() {
    	return processingErrorOccurred;
    }
    
    @Override
    public String getExceptionReason() {
    	return processingErrorReason;
    }
    
}
