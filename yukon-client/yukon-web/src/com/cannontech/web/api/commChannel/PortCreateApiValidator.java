package com.cannontech.web.api.commChannel;

import org.springframework.stereotype.Service;
import org.springframework.validation.Errors;

import com.cannontech.common.device.port.PortBase;
import com.cannontech.common.device.port.TcpSharedPortDetail;
import com.cannontech.common.device.port.TerminalServerPortDetailBase;
import com.cannontech.common.validator.SimpleValidator;
import com.cannontech.common.validator.YukonValidationUtils;

@Service
public class PortCreateApiValidator <T extends PortBase<?>> extends SimpleValidator<T> {

    @SuppressWarnings("unchecked")
    public PortCreateApiValidator() {
        super((Class<T>) PortBase.class);
    }

    public PortCreateApiValidator(Class<T> objectType) {
        super(objectType);
    }

    @Override
    protected void doValidation(T port, Errors errors) {
        // Check if type is NULL
        YukonValidationUtils.checkIfFieldRequired("type", errors, port.getType(), "Type");
        // Check if name is NULL
        YukonValidationUtils.checkIfFieldRequired("name", errors, port.getName(), "Name");
        // Check if baudRate is NULL
        YukonValidationUtils.checkIfFieldRequired("baudRate", errors, port.getBaudRate(), "Baud Rate");

        if (port instanceof TerminalServerPortDetailBase) {
            TerminalServerPortDetailBase<?> serverPortDetailBase = (TerminalServerPortDetailBase<?>) port;
            YukonValidationUtils.validatePort(errors, "portNumber", String.valueOf(serverPortDetailBase.getPortNumber()), "Port Number");
            if (port instanceof TcpSharedPortDetail) {
                TcpSharedPortDetail tcpSharedPortDetail = (TcpSharedPortDetail) port;
                YukonValidationUtils.checkIfFieldRequired("ipAddress", errors, tcpSharedPortDetail.getIpAddress(), "IP Address");
            }
        }

    }
}
