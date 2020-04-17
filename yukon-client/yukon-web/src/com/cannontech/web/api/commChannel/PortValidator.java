package com.cannontech.web.api.commChannel;

import org.apache.commons.lang3.BooleanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.Errors;

import com.cannontech.common.device.port.PortBase;
import com.cannontech.common.device.port.TcpPortDetail;
import com.cannontech.common.device.port.TcpSharedPortDetail;
import com.cannontech.common.device.port.TerminalServerPortDetailBase;
import com.cannontech.common.device.port.UdpPortDetail;
import com.cannontech.common.validator.SimpleValidator;
import com.cannontech.common.validator.YukonValidationUtils;
import com.cannontech.util.Validator;

public class PortValidator<T extends PortBase<?>> extends SimpleValidator<T> {

    @Autowired PortValidatorHelper portValidatorHelper;

    @SuppressWarnings("unchecked")
    public PortValidator() {
        super((Class<T>) PortBase.class);
    }

    public PortValidator(Class<T> objectType) {
        super(objectType);
    }

    @Override
    protected void doValidation(T port, Errors errors) {
        // Validate Name if present.
        if (port.getName() != null) {
            YukonValidationUtils.checkIsBlank(errors, "Name", port.getName(), false);
            if (!errors.hasFieldErrors("Name")) {
                portValidatorHelper.validatePaoName(port.getName(), port.getType(), errors, "Name");
            }
       }

        if (port instanceof TcpPortDetail) {
            // Validate PortTiming if not null.
            TcpPortDetail tcpPort = (TcpPortDetail) port;
            if (tcpPort.getTiming() != null) {
                portValidatorHelper.validatePortTimingFields(errors, tcpPort.getTiming());
            }
        }

        if (port instanceof TerminalServerPortDetailBase) {
            TerminalServerPortDetailBase<?> detailBase = (TerminalServerPortDetailBase<?>) port;
            if (detailBase.getTiming() != null) {
                portValidatorHelper.validatePortTimingFields(errors, detailBase.getTiming());
            }

            if (detailBase.getSharing() != null) {
                portValidatorHelper.validatePortSharingFields(errors, detailBase.getSharing());
            }

            if (BooleanUtils.isTrue(detailBase.getCarrierDetectWait())) {
                YukonValidationUtils.checkRange(errors, "carrierDetectWaitInMilliseconds",
                        detailBase.getCarrierDetectWaitInMilliseconds(), 0, 9999, false);
            }

            if (detailBase.getPortNumber() != null) {
                YukonValidationUtils.validatePort(errors, "portNumber", String.valueOf(detailBase.getPortNumber()));
            }
        }

        if (port instanceof UdpPortDetail) {
            UdpPortDetail udpPortDetail = (UdpPortDetail) port;

            if (BooleanUtils.isTrue(udpPortDetail.getEnableEncryption())) {
                if (udpPortDetail.getKeyInHex() == null || !Validator.isHex(udpPortDetail.getKeyInHex())) {
                    errors.rejectValue("keyInHex", "yukon.web.api.error.udpPort.invalidHexFormat");
                }
                if (!errors.hasFieldErrors("keyInHex")) {
                    if (udpPortDetail.getKeyInHex().length() != 32) {
                        errors.rejectValue("keyInHex", "yukon.web.api.error.udpPort.invalidHexLength");
                    }
                }
            }
        }

        if (port instanceof TcpSharedPortDetail) {
            TcpSharedPortDetail tcpSharedPortDetail = (TcpSharedPortDetail) port;
            if (tcpSharedPortDetail.getIpAddress() != null) {
                YukonValidationUtils.ipHostNameValidator(errors, "ipAddress", tcpSharedPortDetail.getIpAddress());
            }
        }
    }
}
