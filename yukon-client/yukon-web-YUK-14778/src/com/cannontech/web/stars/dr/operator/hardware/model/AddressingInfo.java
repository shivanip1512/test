package com.cannontech.web.stars.dr.operator.hardware.model;

import org.springframework.validation.Errors;

import com.cannontech.stars.xml.serialize.StarsLMConfiguration;

public interface AddressingInfo {
    
    public void updateFromStarsConfiguration(StarsLMConfiguration config);
    
    public void updateStarsConfiguration(StarsLMConfiguration config);
    
    /**
     * Validate the addressing info.
     * @param errors The spring binding result object.
     */
    public void validate(Errors errors);
    
    /**
     * Validate the addressing info.
     * @param errors The spring binding result object.
     * @param path An optional prefix for the path lookup for fields.
     */
    public void validate(Errors errors, String path);
    
}