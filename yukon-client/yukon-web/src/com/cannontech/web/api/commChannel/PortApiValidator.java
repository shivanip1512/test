package com.cannontech.web.api.commChannel;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.Errors;

import com.cannontech.common.device.port.LocalSharedPortDetail;
import com.cannontech.common.device.port.PortBase;
import com.cannontech.common.device.port.TcpPortDetail;
import com.cannontech.common.device.port.TcpSharedPortDetail;
import com.cannontech.common.device.port.TerminalServerPortDetailBase;
import com.cannontech.common.device.port.UdpPortDetail;
import com.cannontech.common.device.port.dao.PortDao;
import com.cannontech.common.validator.PortApiValidatorHelper;
import com.cannontech.common.validator.SimpleValidator;
import com.cannontech.common.validator.YukonApiValidationHelper;
import com.cannontech.common.validator.YukonApiValidationUtils;
import com.cannontech.stars.util.ServletUtils;

public class PortApiValidator<T extends PortBase<?>> extends SimpleValidator<T> {

    @Autowired private PortDao portDao;
    @Autowired private YukonApiValidationHelper yukonApiValidationHelper;
  

    @SuppressWarnings("unchecked")
    public PortApiValidator() {
        super((Class<T>) PortBase.class);
    }

    public PortApiValidator(Class<T> objectType) {
        super(objectType);
    }

    @Override
    protected void doValidation(T port, Errors errors) {
        
        // Validate if type is changed during update.
        String paoId = ServletUtils.getPathVariable("id");
        if (paoId != null && port.getDeviceType() != null) {
            yukonApiValidationHelper.checkIfPaoTypeChanged(errors, port.getDeviceType(), Integer.valueOf(paoId));
        }

        // Validate Name if present.
        if (port.getDeviceName() != null) {
            yukonApiValidationHelper.validatePaoName(port.getDeviceName(), port.getDeviceType(), errors, "Name", paoId);
        }

        if (port instanceof TcpPortDetail) {
            // Validate PortTiming if not null.
            TcpPortDetail tcpPort = (TcpPortDetail) port;
            if (tcpPort.getTiming() != null) {
                PortApiValidatorHelper.validatePortTimingFields(errors, tcpPort.getTiming());
            }
        }

        if (port instanceof TerminalServerPortDetailBase) {
            TerminalServerPortDetailBase<?> detailBase = (TerminalServerPortDetailBase<?>) port;
            if (detailBase.getTiming() != null) {
                PortApiValidatorHelper.validatePortTimingFields(errors, detailBase.getTiming());
            }

            if (detailBase.getSharing() != null && detailBase.getSharing().getSharedSocketNumber() != null) {
                PortApiValidatorHelper.validatePortSharingFields(errors, detailBase.getSharing(), "Socket Number");
            }

            PortApiValidatorHelper.validateCarrierDetectWait(errors, detailBase.getCarrierDetectWaitInMilliseconds(),
                    "Carrier Detect Wait");

            if (detailBase.getPortNumber() != null) {
                YukonApiValidationUtils.validatePort(errors, "portNumber", "Port Number",
                        String.valueOf(detailBase.getPortNumber()));
            }
        }

        if (port instanceof LocalSharedPortDetail) {
            LocalSharedPortDetail localSharedPortDetail = (LocalSharedPortDetail) port;
            if (localSharedPortDetail.getTiming() != null) {
                PortApiValidatorHelper.validatePortTimingFields(errors, localSharedPortDetail.getTiming());
            }

            if (localSharedPortDetail.getSharing() != null
                    && localSharedPortDetail.getSharing().getSharedSocketNumber() != null) {
                PortApiValidatorHelper.validatePortSharingFields(errors, localSharedPortDetail.getSharing(), "Socket Number");
            }

            PortApiValidatorHelper.validateCarrierDetectWait(errors, localSharedPortDetail.getCarrierDetectWaitInMilliseconds(),
                    "Carrier Detect Wait");

            if (localSharedPortDetail.getPhysicalPort() != null) {
                PortApiValidatorHelper.validatePhysicalPort(errors, localSharedPortDetail.getPhysicalPort(), "Physical Port");
            }
        }

        if (port instanceof UdpPortDetail) {
            UdpPortDetail udpPortDetail = (UdpPortDetail) port;

            if (udpPortDetail.getKeyInHex() != null) {
                PortApiValidatorHelper.validateEncryptionKey(errors, udpPortDetail.getKeyInHex());
            }
            if (udpPortDetail.getPortNumber() != null && !errors.hasFieldErrors("portNumber")) {
                // Checks for unique IP Address and Port number
                Integer existingPortId = portDao.findUniquePortTerminalServer(udpPortDetail.getIpAddress(),
                        udpPortDetail.getPortNumber());
                PortApiValidatorHelper.validateUniquePortAndIpAddress(errors, udpPortDetail.getPortNumber(),
                        udpPortDetail.getIpAddress(), existingPortId, paoId, udpPortDetail.getDeviceType());
            }
        }

        if (port instanceof TcpSharedPortDetail) {
            TcpSharedPortDetail tcpSharedPortDetail = (TcpSharedPortDetail) port;
            if (tcpSharedPortDetail.getIpAddress() != null) {
                PortApiValidatorHelper.validateIPAddress(errors, tcpSharedPortDetail.getIpAddress(), "IP Address", false);
                Integer existingPortId = portDao.findUniquePortTerminalServer(tcpSharedPortDetail.getIpAddress(),
                        tcpSharedPortDetail.getPortNumber());
                PortApiValidatorHelper.validateUniquePortAndIpAddress(errors, tcpSharedPortDetail.getPortNumber(),
                        tcpSharedPortDetail.getIpAddress(), existingPortId, paoId, tcpSharedPortDetail.getDeviceType());
            }
        }
    }
}