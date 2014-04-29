package com.cannontech.dr.ecobee.service.impl;

import com.cannontech.dr.ecobee.service.EcobeeStatusCode;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Contains all the messaging classes used by EcobeeCommunicationService for serializing and de-serializing JSON
 * messages.
 */
public final class EcobeeMessages {
    private EcobeeMessages() {} //class cannot be instantiated
    
    /**
     * This status object is included in most responses from the Ecobee API.
     * @param code A code of 0 indicates a successfuly operation. Any other code is an error code.
     * @param message Additional error details.
     * @see EcobeeStatusCode
     */
    @JsonIgnoreProperties(ignoreUnknown=true)
    public static final class Status {
        private Integer code;
        private String message;
        
        @JsonCreator
        public Status(@JsonProperty("code") Integer code, @JsonProperty("message") String message) {
            this.code = code;
            this.message = message;
        }

        public Integer getCode() {
            return code;
        }

        public String getMessage() {
            return message;
        }
    }
    
    /**
     * Base class for all Ecobee responses that contain an EcobeeStatus.
     */
    @JsonIgnoreProperties(ignoreUnknown=true)
    public abstract static class ResponseWithStatus {
        protected Status status;
        
        public Status getStatus() {
            return status;
        }
    }
    
    public static final class AuthenticationRequest {
        private String userName;
        private String password;
        
        @JsonCreator
        public AuthenticationRequest(String userName, String password) {
            this.userName = userName;
            this.password = password;
        }

        public String getUserName() {
            return userName;
        }

        public String getPassword() {
            return password;
        }
    }
    
    @JsonIgnoreProperties(ignoreUnknown=true)
    public static final class AuthenticationResponse extends ResponseWithStatus {
        private String token;
        
        @JsonCreator
        public AuthenticationResponse(@JsonProperty("token") String token, @JsonProperty("status") Status status) {
            this.token = token;
            this.status = status;
        }
        
        public String getToken() {
            return token;
        }
    }
    
    public static final class CreateSetRequest {
        private String operation = "add";
        private String setName;
        private String parentPath = "/";
        
        public CreateSetRequest(String setName) {
            this.setName = setName;
        }
        
        public String getOperation() {
            return operation;
        }
        
        public String getSetName() {
            return setName;
        }
        
        public String getParentPath() {
            return parentPath;
        }
    }
    
    @JsonIgnoreProperties(ignoreUnknown=true)
    public static final class CreateSetResponse extends ResponseWithStatus {
        private Boolean success;
        
        @JsonCreator
        public CreateSetResponse(@JsonProperty("success") Boolean success, @JsonProperty("status") Status status) {
            this.success = success;
            this.status = status;
        }
        
        public Boolean getSuccess() {
            return success;
        }
    }
    
    public static final class DeleteSetRequest {
        private String operation = "remove";
        private String setPath;
        
        public DeleteSetRequest(String setName) {
            this.setPath = "/" + setName;
        }
        
        public String getSetPath() {
            return setPath;
        }
        
        public String getOperation() {
            return operation;
        }
    }
    
    @JsonIgnoreProperties(ignoreUnknown=true)
    public static final class DeleteSetResponse extends ResponseWithStatus {
        private Boolean success;
        
        @JsonCreator
        public DeleteSetResponse(@JsonProperty("success") Boolean success, @JsonProperty("status") Status status) {
            this.success = success;
            this.status = status;
        }
        
        public Boolean getSuccess() {
            return success;
        }
    }
    
    public static final class MoveSetRequest {
        private String operation = "move";
        private String setPath;
        private String toPath;
        
        public MoveSetRequest(String currentPath, String newPath) {
            setPath = currentPath;
            toPath = newPath;
        }
        
        public String getOperation() {
            return operation;
        }
        
        public String getSetPath() {
            return setPath;
        }
        
        public String getToPath() {
            return toPath;
        }
    }
    
    @JsonIgnoreProperties(ignoreUnknown=true)
    public static final class MoveSetResponse extends ResponseWithStatus {
        private Boolean success;
        
        @JsonCreator
        public MoveSetResponse(@JsonProperty("success") Boolean success, @JsonProperty("status") Status status) {
            this.success = success;
            this.status = status;
        }
        
        public Boolean getSuccess() {
            return success;
        }
    }
    
    public static final class MoveDeviceRequest {
        private String operation = "assign";
        private String setPath;
        private String thermostats;
        
        public MoveDeviceRequest(String serialNumber, String setPath) {
            thermostats = serialNumber;
            this.setPath = setPath;
        }
        
        public String getOperation() {
            return operation;
        }

        
        public String getSetPath() {
            return setPath;
        }
        
        public String getThermostats() {
            return thermostats;
        }
    }
    
    @JsonIgnoreProperties(ignoreUnknown=true)
    public static final class MoveDeviceResponse extends ResponseWithStatus {
        private Boolean success;
        
        @JsonCreator
        public MoveDeviceResponse(@JsonProperty("success") Boolean success, @JsonProperty("status") Status status) {
            this.success = success;
            this.status = status;
        }
        
        public Boolean getSuccess() {
            return success;
        }
    }
    
    public static final class RegisterDeviceRequest {
        private String operation = "register";
        private String thermostats;
        
        public RegisterDeviceRequest(String serialNumber) {
            thermostats = serialNumber;
        }
        
        public String getThermostats() {
            return thermostats;
        }
        
        public String getOperation() {
            return operation;
        }
    }
    
    @JsonIgnoreProperties(ignoreUnknown=true)
    public static final class RegisterDeviceResponse extends ResponseWithStatus {
        private Boolean success;
        
        @JsonCreator
        public RegisterDeviceResponse(@JsonProperty("success") Boolean success, @JsonProperty("status") Status status) {
            this.success = success;
            this.status = status;
        }
        
        public Boolean getSuccess() {
            return success;
        }
    }
}
