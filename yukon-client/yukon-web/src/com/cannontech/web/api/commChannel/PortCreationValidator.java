package com.cannontech.web.api.commChannel;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.Errors;

import com.cannontech.common.device.port.PortBase;
import com.cannontech.common.device.port.TcpSharedPortDetail;
import com.cannontech.common.device.port.TerminalServerPortDetailBase;
import com.cannontech.common.validator.SimpleValidator;
import com.cannontech.common.validator.YukonValidationUtils;

@Service
public class PortCreationValidator <T extends PortBase<?>> extends SimpleValidator<T> {

    @Autowired PortValidatorHelper portValidatorHelper;

    @SuppressWarnings("unchecked")
    public PortCreationValidator() {
        super((Class<T>) PortBase.class);
    }

    public PortCreationValidator(Class<T> objectType) {
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
            YukonValidationUtils.checkIfFieldRequired("portNumber", errors, serverPortDetailBase.getPortNumber(), "Port");

            if (port instanceof TcpSharedPortDetail) {
                TcpSharedPortDetail tcpSharedPortDetail = (TcpSharedPortDetail) port;
                YukonValidationUtils.checkIfFieldRequired("ipAddress", errors, tcpSharedPortDetail.getIpAddress(), "IP Address");
            }
        }

    }
}
