package com.cannontech.dr.eatonCloud.model;

import java.util.Arrays;
import java.util.stream.Collectors;

import com.cannontech.amr.errors.dao.DeviceError;
import com.google.common.collect.ImmutableMap;


public enum EatonCloudError {
    NO_RESPONSE_FROM_DEVICE(DeviceError.NO_RESPONSE_FROM_DEVICE, 10000),
    NO_RESPONSE_DUE_TO_RESTART(DeviceError.NO_RESPONSE_DUE_TO_RESTART, 10001),
    JOB_CREATION_FAILED(DeviceError.JOB_CREATION_FAILED, 10002),
    USER_NOT_AUTHORIZED(DeviceError.USER_NOT_AUTHORIZED, 10004),
    DEVICE_NOT_CONFIGURED(DeviceError.DEVICE_NOT_CONFIGURED, 10066),
    PROFILE_NOT_FOUND(DeviceError.PROFILE_NOT_FOUND, 10068),
    CHANNELS_ARE_INVALID(DeviceError.CHANNELS_ARE_INVALID, 11311),
    CHANNEL_METHOD_NOT_AVAILABLE(DeviceError.CHANNEL_METHOD_NOT_AVAILABLE, 11617),
    NO_CHANNELS_DEFINED(DeviceError.NO_CHANNELS_DEFINED, 15516),
    CHANNEL_METHOD_PARAMS_NOT_VALID(DeviceError.CHANNEL_METHOD_PARAMS_NOT_VALID, 15517),
    CHANNEL_METHOD_PARAMS_NOT_WRITABLE(DeviceError.CHANNEL_METHOD_PARAMS_NOT_WRITABLE, 15518),
    DEVICE_NOT_FOUND(DeviceError.DEVICE_NOT_FOUND, 15703),
    COMMAND_TIMED_OUT(DeviceError.COMMAND_TIMED_OUT, 16201),
    COMMAND_FAILURE(DeviceError.COMMAND_FAILURE, 16202),
    ;
    private final int code;
    private final DeviceError error;
    
    private final static ImmutableMap<Integer, DeviceError> errors = ImmutableMap.copyOf(
            Arrays.stream(values()).collect(Collectors.toMap(v -> v.code, v -> v.error)));
    
    EatonCloudError(DeviceError error, int code) {
        this.error = error;
        this.code = code;
    }
    
    public static DeviceError getErrorByCode(int code){
        DeviceError error = errors.get(code);
        if(error == null){
            error = DeviceError.UNKNOWN;
        }
        return error;
    }
    
    public DeviceError getDeviceError() {
        return error;
    }
    
    public int getCode() {
        return code;
    }
}
