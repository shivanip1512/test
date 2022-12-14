package com.cannontech.amr.deviceread;

import java.util.Set;

import com.cannontech.amr.errors.model.SpecificDeviceErrorDescription;
import com.cannontech.core.dynamic.PointValueHolder;

public class CalculatedPointResults {
    PointValueHolder calculatedPVH;
    PointValueHolder differencePVH;
    PointValueHolder currentPVH;
    Set<SpecificDeviceErrorDescription> errors;
    String deviceError;

    public PointValueHolder getCalculatedPVH() {
        return calculatedPVH;
    }

    public void setCalculatedPVH(PointValueHolder calculatedPVH) {
        this.calculatedPVH = calculatedPVH;
    }

    public PointValueHolder getCurrentPVH() {
        return currentPVH;
    }

    public void setCurrentPVH(PointValueHolder currentPVH) {
        this.currentPVH = currentPVH;
    }

    public PointValueHolder getDifferencePVH() {
        return differencePVH;
    }

    public void setDifferencePVH(PointValueHolder differencePVH) {
        this.differencePVH = differencePVH;
    }

    public Set<SpecificDeviceErrorDescription> getErrors() {
        return errors;
    }

    public void setErrors(Set<SpecificDeviceErrorDescription> errors) {
        this.errors = errors;
    }
    
    public String getDeviceError() {
		return deviceError;
	}
    
    public void setDeviceError(String deviceError) {
		this.deviceError = deviceError;
	}
}
