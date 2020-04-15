package com.cannontech.web.api.commChannel;

import org.springframework.stereotype.Service;
import org.springframework.validation.Errors;

import com.cannontech.common.device.port.TcpPortDetail;

@Service
public class TcpPortValidator extends PortValidator<TcpPortDetail> {

    public TcpPortValidator() {
        super(TcpPortDetail.class);
    }

    @Override
    public boolean supports(Class clazz) {
        return TcpPortDetail.class.isAssignableFrom(clazz);
    }

    @Override
    protected void doValidation(TcpPortDetail tcpPort, Errors errors) {
        // Validate PortTiming if not null.
        if (tcpPort.getTiming() != null) {
            portValidatorHelper.validatePortTimingFields(errors, tcpPort.getTiming());
        }
    }
}
