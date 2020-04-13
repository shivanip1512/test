package com.cannontech.web.api.commChannel;

import org.apache.commons.lang3.BooleanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.Errors;

import com.cannontech.common.device.port.TerminalServerPortDetailBase;
import com.cannontech.common.validator.YukonValidationUtils;

public class TerminalServerPortValidator <T extends TerminalServerPortDetailBase<?>> extends PortValidator<TerminalServerPortDetailBase<?>> {
    @Autowired PortValidatorHelper portValidatorHelper;

    @SuppressWarnings("unchecked")
    public TerminalServerPortValidator(Class<?> objectType) {
        super((Class<TerminalServerPortDetailBase<?>>) objectType);
    }

    @Override
    protected void doValidation(TerminalServerPortDetailBase<?> target, Errors errors) {

        if (target.getTiming() != null) {
            portValidatorHelper.validatePortTimingFields(errors, target.getTiming());
        }

        if (target.getSharing() != null) {
            portValidatorHelper.validatePortSharingFields(errors, target.getSharing());
        }

        if (BooleanUtils.isTrue(target.getCarrierDetectWait())) {
            YukonValidationUtils.checkRange(errors, "carrierDetectWaitInMilliseconds", target.getCarrierDetectWaitInMilliseconds(), 0, 9999, false);
        }

        if (target.getPortNumber() != null) {
            YukonValidationUtils.checkRange(errors, "portNumber", target.getPortNumber(), 0, 999999, false);
        }

    }

}
