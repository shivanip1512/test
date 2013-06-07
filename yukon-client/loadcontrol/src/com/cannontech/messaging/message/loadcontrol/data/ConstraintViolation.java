package com.cannontech.messaging.message.loadcontrol.data;

import java.util.Date;
import java.util.List;
import java.util.Map;


public class ConstraintViolation {

    private ConstraintError errorCode;

    private List<Double> doubleParams;
    private List<Integer> integerParams;
    private List<String> stringParams;
    private List<Date> datetimeParams;

    public ConstraintError getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(ConstraintError errorCode) {
        this.errorCode = errorCode;
    }

    public void setDoubleParams(List<Double> doubleParams) {
        this.doubleParams = doubleParams;
    }

    public void setIntegerParams(List<Integer> integerParams) {
        this.integerParams = integerParams;
    }

    public void setStringParams(List<String> stringParams) {
        this.stringParams = stringParams;
    }

    public void setDateTimeParams(List<Date> dateTimeParams) {
        this.datetimeParams = dateTimeParams;
    }

    public List<Double> getDoubleParams() {
        return doubleParams;
    }

    public List<Integer> getIntegerParams() {
        return integerParams;
    }

    public List<String> getStringParams() {
        return stringParams;
    }

    public List<Date> getDatetimeParams() {
        return datetimeParams;
    }

    public Map<String, Object> getViolationParameters() {
        return errorCode.getViolationParameters(this);
    }
}
