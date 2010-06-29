package com.cannontech.web.stars.dr.operator.hardware.model;

import org.springframework.validation.Errors;

import com.cannontech.stars.xml.serialize.StarsLMConfiguration;

public interface AddressingInfo {
    public void updateFromStarsConfiguration(StarsLMConfiguration config);
    public void updateStarsConfiguration(StarsLMConfiguration config);

    public void validate(Errors errors);
}
