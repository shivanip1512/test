package com.cannontech.web.api.commChannel;

import org.apache.commons.lang3.BooleanUtils;
import org.springframework.stereotype.Service;
import org.springframework.validation.Errors;

import com.cannontech.common.device.port.TerminalServerPortDetailBase;
import com.cannontech.common.device.port.UdpPortDetail;
import com.cannontech.util.Validator;

@Service
public class UdpPortValidator extends TerminalServerPortValidator <UdpPortDetail> {
    private final static String key = "yukon.web.api.error.";

    public UdpPortValidator() {
        super(UdpPortDetail.class);
    }
    
    @Override
    public boolean supports(Class clazz) {
        return UdpPortDetail.class.isAssignableFrom(clazz);
    }

    @Override
    protected void doValidation(TerminalServerPortDetailBase<?> target, Errors errors) {
        super.doValidation(target, errors);
        UdpPortDetail udpPortDetail = (UdpPortDetail) target;

        if (BooleanUtils.isTrue(udpPortDetail.getEnableEncryption())) {
            if (udpPortDetail.getKeyInHex() == null || !Validator.isHex(udpPortDetail.getKeyInHex())) {
                errors.rejectValue("keyInHex", key + "udpPort.invalidHexFormat");
            }
            if (!errors.hasFieldErrors("keyInHex")) {
                if (udpPortDetail.getKeyInHex().length() != 32) {
                    errors.rejectValue("keyInHex", key + "udpPort.invalidHexLength");
                }
            }
        }
    }

}
