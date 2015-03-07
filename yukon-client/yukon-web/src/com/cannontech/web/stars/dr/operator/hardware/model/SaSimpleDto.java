package com.cannontech.web.stars.dr.operator.hardware.model;

import org.springframework.validation.Errors;

import com.cannontech.common.validator.YukonValidationUtils;
import com.cannontech.stars.xml.serialize.SASimple;
import com.cannontech.stars.xml.serialize.StarsLMConfiguration;

public class SaSimpleDto implements AddressingInfo {
    
    private String operationalAddress = "";
    
    public String getOperationalAddress() {
        return operationalAddress;
    }
    
    public void setOperationalAddress(String operationalAddress) {
        this.operationalAddress = operationalAddress;
    }
    
    @Override
    public void updateFromStarsConfiguration(StarsLMConfiguration config) {
        operationalAddress = config.getSASimple().getOperationalAddress();
    }
    
    @Override
    public void updateStarsConfiguration(StarsLMConfiguration config) {
        SASimple saSimple = new SASimple();
        saSimple.setOperationalAddress(operationalAddress);
        config.setSASimple(saSimple);
    }
    
    @Override
    public void validate(Errors errors) {
        validate(errors, "");
    }
    
    @Override
    public void validate(Errors errors, String path) {
        YukonValidationUtils.checkExceedsMaxLength(errors, path + "addressingInfo.operationalAddress",
                                                   operationalAddress, 8);
    }
    
}