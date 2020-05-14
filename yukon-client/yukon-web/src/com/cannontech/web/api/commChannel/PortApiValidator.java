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
import com.cannontech.common.validator.PortValidatorHelper;
import com.cannontech.common.validator.SimpleValidator;
import com.cannontech.common.validator.YukonValidationHelper;
import com.cannontech.common.validator.YukonValidationUtils;
import com.cannontech.stars.util.ServletUtils;

public class PortApiValidator<T extends PortBase<?>> extends SimpleValidator<T> {

    @Autowired private PortDao portDao;
    @Autowired private YukonValidationHelper yukonValidationHelper;

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
        if (paoId != null) {
            yukonValidationHelper.checkIfPaoTypeChanged(errors, port.getType(), Integer.valueOf(paoId));
        }

        // Validate Name if present.
        if (port.getName() != null) {
            yukonValidationHelper.validatePaoName(port.getName(), port.getType(), errors, "Name", paoId);
        }

        if (port instanceof TcpPortDetail) {
            // Validate PortTiming if not null.
            TcpPortDetail tcpPort = (TcpPortDetail) port;
            if (tcpPort.getTiming() != null) {
                PortValidatorHelper.validatePortTimingFields(errors, tcpPort.getTiming());
            }
        }

        if (port instanceof TerminalServerPortDetailBase) {
            TerminalServerPortDetailBase<?> detailBase = (TerminalServerPortDetailBase<?>) port;
            if (detailBase.getTiming() != null) {
                PortValidatorHelper.validatePortTimingFields(errors, detailBase.getTiming());
            }

            if (detailBase.getSharing() != null && detailBase.getSharing().getSharedSocketNumber() != null) {
                PortValidatorHelper.validatePortSharingFields(errors, detailBase.getSharing());
            }

            PortValidatorHelper.validateCarrierDetectWait(errors, detailBase.getCarrierDetectWaitInMilliseconds());

            if (detailBase.getPortNumber() != null) {
                YukonValidationUtils.validatePort(errors, "portNumber", String.valueOf(detailBase.getPortNumber()));
            }
        }

        if (port instanceof LocalSharedPortDetail) {
            LocalSharedPortDetail localSharedPortDetail = (LocalSharedPortDetail) port;
            if (localSharedPortDetail.getTiming() != null) {
                PortValidatorHelper.validatePortTimingFields(errors, localSharedPortDetail.getTiming());
            }

            if (localSharedPortDetail.getSharing() != null && localSharedPortDetail.getSharing().getSharedSocketNumber() != null) {
                PortValidatorHelper.validatePortSharingFields(errors, localSharedPortDetail.getSharing());
            }

            PortValidatorHelper.validateCarrierDetectWait(errors, localSharedPortDetail.getCarrierDetectWaitInMilliseconds());

            if (localSharedPortDetail.getPhysicalPort() != null) {
                PortValidatorHelper.validatePhysicalPort(errors, localSharedPortDetail.getPhysicalPort());
            }
        }

        if (port instanceof UdpPortDetail) {
            UdpPortDetail udpPortDetail = (UdpPortDetail) port;

            if (udpPortDetail.getKeyInHex() != null) {
                PortValidatorHelper.validateEncryptionKey(errors, udpPortDetail.getKeyInHex());
            }
            if (udpPortDetail.getPortNumber() != null && !errors.hasFieldErrors("portNumber")) {
                // Checks for unique IP Address and Port number
                Integer existingPortId = portDao.findUniquePortTerminalServer(udpPortDetail.getIpAddress(), udpPortDetail.getPortNumber());
                PortValidatorHelper.validateUniquePortAndIpAddress(errors, udpPortDetail.getPortNumber(), udpPortDetail.getIpAddress(), existingPortId, paoId);
            }
        }

        if (port instanceof TcpSharedPortDetail) {
            TcpSharedPortDetail tcpSharedPortDetail = (TcpSharedPortDetail) port;
            if (tcpSharedPortDetail.getIpAddress() != null) {
                PortValidatorHelper.validateIPAddress(errors, tcpSharedPortDetail.getIpAddress(), false);
                Integer existingPortId = portDao.findUniquePortTerminalServer(tcpSharedPortDetail.getIpAddress(), tcpSharedPortDetail.getPortNumber());
                PortValidatorHelper.validateUniquePortAndIpAddress(errors, tcpSharedPortDetail.getPortNumber(), tcpSharedPortDetail.getIpAddress(), existingPortId, paoId);
            }
        }
    }
}
