package com.cannontech.amr.deviceread.service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.cannontech.amr.deviceread.dao.DeviceAttributeReadError;
import com.cannontech.core.dynamic.PointValueHolder;

public class DeviceReadResult {
	
	private Set<DeviceAttributeReadError> errors = new HashSet<DeviceAttributeReadError>();
    private List<PointValueHolder> pointValues = new ArrayList<PointValueHolder>();
    private String lastResultString;
    private boolean timeout = true;
    
	public Set<DeviceAttributeReadError> getErrors() {
		return errors;
	}
	
	public void addError(DeviceAttributeReadError error) {
		errors.add(error);
	}

	public void addPointValue(PointValueHolder pointValue) {
		pointValues.add(pointValue);
	}

	public String getLastResultString() {
		return lastResultString;
	}

	public void setLastResultString(String lastResultString) {
		this.lastResultString = lastResultString;
	}
	
	public List<PointValueHolder> getPointValues(){
        return pointValues;
    }
           
    public boolean isSuccess() {
        return errors.isEmpty();
    }

	public boolean isTimeout() {
		return timeout;
	}

	public void setTimeout(boolean timeout) {
		this.timeout = timeout;
	}
}
