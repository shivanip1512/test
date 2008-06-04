package com.cannontech.common.device.commands;

import java.util.ArrayList;
import java.util.List;

import com.cannontech.amr.errors.model.DeviceErrorDescription;
import com.cannontech.common.util.Completable;
import com.cannontech.core.dynamic.PointValueHolder;

public class CollectingCommandCompletionCallback implements
        CommandCompletionCallback<Object>, CommandResultHolder, Completable {
    
    private List<DeviceErrorDescription> errors = new ArrayList<DeviceErrorDescription>();
    private List<PointValueHolder> values = new ArrayList<PointValueHolder>();
    private List<String> resultStrings = new ArrayList<String>();
    private boolean complete = false;

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
    public boolean isComplete() {
        return complete;
    }

    protected void doComplete() {
        // noop
    }

}
