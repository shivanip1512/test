package com.cannontech.amr.deviceread;

import java.util.List;

import com.cannontech.amr.errors.model.DeviceErrorDescription;
import com.cannontech.core.dynamic.PointValueHolder;

public class CalculatedPointResults {
    PointValueHolder calculatedPVH;
    PointValueHolder differencePVH;
    PointValueHolder currentPVH;
    List<DeviceErrorDescription> errors;

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

    public List<DeviceErrorDescription> getErrors() {
        return errors;
    }

    public void setErrors(List<DeviceErrorDescription> errors) {
        this.errors = errors;
    }
}
