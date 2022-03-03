package com.cannontech.web.stars.rfn1200;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.Errors;

import com.cannontech.common.rfn.model.Rfn1200Detail;
import com.cannontech.common.util.Range;
import com.cannontech.common.validator.PortValidatorHelper;
import com.cannontech.common.validator.SimpleValidator;
import com.cannontech.common.validator.YukonValidationHelper;
import com.cannontech.common.validator.YukonValidationUtils;

public class Rfn1200Validator extends SimpleValidator<Rfn1200Detail> {
    @Autowired private YukonValidationHelper yukonValidationHelper;

    private static final String baseKey = "yukon.web.modules.operator.commChannelInfoWidget.";

    public Rfn1200Validator() {
        super(Rfn1200Detail.class);
    }

    @Override
    protected void doValidation(Rfn1200Detail rfn1200, Errors errors) {
        String paoId = rfn1200.getId() != null ? rfn1200.getId().toString() : null;

        yukonValidationHelper.validatePaoName(rfn1200.getName(), rfn1200.getPaoType(), 
                                              errors, yukonValidationHelper.getMessage("yukon.common.name"), paoId);
        
        PortValidatorHelper.validateRfnAddressField(errors, "rfnAddress.serialNumber", rfn1200.getRfnAddress().getSerialNumber(), 
                                                 yukonValidationHelper.getMessage("yukon.common.serialNumber"), true, 30);
        PortValidatorHelper.validateRfnAddressField(errors, "rfnAddress.manufacturer", rfn1200.getRfnAddress().getManufacturer(), 
                                                    yukonValidationHelper.getMessage("yukon.common.manufacturer"), true, 60);
        PortValidatorHelper.validateRfnAddressField(errors, "rfnAddress.model", rfn1200.getRfnAddress().getModel(), 
                                                    yukonValidationHelper.getMessage("yukon.common.model"), true, 60);
        
        if (!errors.hasFieldErrors("postCommWait")) {
            Range<Integer> range = Range.inclusive(0, 100_000);
            YukonValidationUtils.checkRange(errors, "postCommWait",
                    yukonValidationHelper.getMessage(baseKey + "postCommWait"), rfn1200.getPostCommWait(), range, true);
        }
                
    }

}