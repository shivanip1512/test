package com.cannontech.web.api.commChannel;

import org.springframework.stereotype.Service;
import org.springframework.validation.Errors;

import com.cannontech.common.device.port.LocalSharedPortDetail;
import com.cannontech.common.device.port.PortBase;
import com.cannontech.common.device.port.TcpSharedPortDetail;
import com.cannontech.common.device.port.TerminalServerPortDetailBase;
import com.cannontech.common.validator.SimpleValidator;
import com.cannontech.common.validator.YukonApiValidationUtils;

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
        YukonApiValidationUtils.checkIfFieldRequired("deviceType", errors, port.getDeviceType(), "Type");
        // Check if name is NULL
        YukonApiValidationUtils.checkIfFieldRequired("deviceName", errors, port.getDeviceName(), "Name");
        // Check if baudRate is NULL
        YukonApiValidationUtils.checkIfFieldRequired("baudRate", errors, port.getBaudRate(), "Baud Rate");

        if (port instanceof TerminalServerPortDetailBase) {
            TerminalServerPortDetailBase<?> serverPortDetailBase = (TerminalServerPortDetailBase<?>) port;
            YukonApiValidationUtils.validatePort(errors, "portNumber", "Port Number",
                    String.valueOf(serverPortDetailBase.getPortNumber()));

            if (port instanceof TcpSharedPortDetail) {
                TcpSharedPortDetail tcpSharedPortDetail = (TcpSharedPortDetail) port;
                YukonApiValidationUtils.checkIfFieldRequired("ipAddress", errors, tcpSharedPortDetail.getIpAddress(), "IP Address");
            }
        }

        if (port instanceof LocalSharedPortDetail) {
            YukonApiValidationUtils.checkIfFieldRequired("physicalPort", errors, ((LocalSharedPortDetail) port).getPhysicalPort(),
                    "Physical Port");
        }
    }
}