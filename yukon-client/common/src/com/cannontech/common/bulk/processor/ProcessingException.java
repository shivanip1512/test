package com.cannontech.common.bulk.processor;

import com.cannontech.common.csvImport.ImportAction;
import com.cannontech.common.csvImport.ValidationResultType;
import com.cannontech.common.exception.DisplayableRuntimeException;
import com.cannontech.common.fdr.FdrDirection;
import com.cannontech.common.fdr.FdrInterfaceType;
import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.attribute.model.BuiltInAttribute;
import com.cannontech.common.rfn.message.RfnIdentifier;

/**
 * Exception thrown when there is a problem when trying to process an object
 */
public class ProcessingException extends DisplayableRuntimeException implements
        ProcessorCallbackException {

    private static final String keyBase = "yukon.exception.processingException.";

    public ProcessingException(String message, String key, Throwable cause, PaoIdentifier paoId) {
        super(message, cause, keyBase + key, paoId);
    }

    public ProcessingException(String message, String key, String minValue, String maxValue,
            PaoIdentifier paoId) {
        super(message, keyBase + key, minValue, maxValue, paoId);
    }

    public ProcessingException(String message, String key) {
        super(message, keyBase + key);
    }

    public ProcessingException(String message, String key, Throwable cause) {
        super(message, cause, keyBase + key);
    }

    public ProcessingException(String message, String key, Throwable cause, int deviceId, String deviceName,
            String route) {
        super(message, cause, keyBase + key, deviceId, deviceName, route);
    }

    public ProcessingException(String message, String key, int address) {
        super(message, keyBase + key, address);
    }

    public ProcessingException(String message, String key, RfnIdentifier rfnId) {
        super(message, keyBase + key, rfnId);
    }

    public ProcessingException(String message, String key, String pointName, String paoName) {
        super(message, keyBase + key, pointName, paoName);
    }

    public ProcessingException(String message, String key, Throwable cause, int deviceId) {
        super(message, cause, keyBase + key, deviceId);
    }
    
    public ProcessingException(String message, String key, Throwable cause, int deviceId, String deviceName) {
        super(message, cause, keyBase + key, deviceId, deviceName);
    }

    /**
     * objectName can be columnValue or RouteName
     */
    public ProcessingException(String message, String key, Throwable cause, String objectName) {
        super(message, cause, keyBase + key, objectName);
    }

    public ProcessingException(String message, String key, Throwable cause, String objectName, String deviceName) {
        super(message, cause, keyBase + key, objectName, deviceName);
    }

    /**
     * objectName can be MissingColumn or DeviceName
     */
    public ProcessingException(String message, String key, String objectName) {
        super(message, keyBase + key, objectName);
    }

    public ProcessingException(String message, String key, Throwable cause, String paoName,
            int deviceId) {
        super(message, cause, keyBase + key, paoName, deviceId);
    }

    public ProcessingException(String message, String key, int pointOffset, String deviceName) {
        super(message, keyBase + key, pointOffset, deviceName);
    }

    public ProcessingException(String message, String key, ImportAction action) {
        super(message, keyBase + key, action);
    }

    public ProcessingException(String message, String key, BuiltInAttribute attribute, int paoId) {
        super(message, keyBase + key, attribute, paoId);
    }

    public ProcessingException(String message, String key, String displayName, String paoName,
            PaoType paoType) {
        super(message, keyBase + key, displayName, paoName, paoType);
    }

    public ProcessingException(String message, String key, Throwable cause, PaoType paoType) {
        super(message, cause, keyBase + key, paoType);
    }

    public ProcessingException(String message, String key, Throwable cause, PaoType paoType, String paoName) {
        super(message, cause, keyBase + key, paoType, paoName);
    }

    public ProcessingException(String message, String key, Throwable cause, PaoType paoType, String paoName, int paoId) {
        super(message, cause, keyBase + key, paoType, paoName, paoId);
    }

    public ProcessingException(String message, String key, String deviceName, PaoType paoType) {
        super(message, keyBase + key, deviceName, paoType);
    }

    public ProcessingException(String message, String key, FdrDirection fdrDirection,
            FdrInterfaceType fdrInterfaceType) {
        super(message, keyBase + key, fdrDirection, fdrInterfaceType);
    }

    public ProcessingException(String message, String key, ValidationResultType validation) {
        super(message, keyBase + key, validation);
    }

}
