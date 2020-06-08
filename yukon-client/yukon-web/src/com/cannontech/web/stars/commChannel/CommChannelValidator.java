package com.cannontech.web.stars.commChannel;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.Errors;

import com.cannontech.common.device.port.LocalSharedPortDetail;
import com.cannontech.common.device.port.PortBase;
import com.cannontech.common.device.port.PortTiming;
import com.cannontech.common.device.port.TcpPortDetail;
import com.cannontech.common.device.port.TcpSharedPortDetail;
import com.cannontech.common.device.port.TerminalServerPortDetailBase;
import com.cannontech.common.device.port.UdpPortDetail;
import com.cannontech.common.device.port.dao.PortDao;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.validator.PortValidatorHelper;
import com.cannontech.common.validator.SimpleValidator;
import com.cannontech.common.validator.YukonValidationHelper;
import com.cannontech.common.validator.YukonValidationUtils;

public class CommChannelValidator<T extends PortBase<?>> extends SimpleValidator<T> {
    @Autowired private YukonValidationHelper yukonValidationHelper;
    @Autowired private PortDao portDao;

    @SuppressWarnings("unchecked")
    public CommChannelValidator() {
        super((Class<T>) PortBase.class);
    }

    public CommChannelValidator(Class<T> objectType) {
        super(objectType);
    }

    @Override
    protected void doValidation(T commChannel, Errors errors) {
        String paoId = commChannel.getId() != null ? commChannel.getId().toString() : null;

        yukonValidationHelper.validatePaoName(commChannel.getName(), commChannel.getType(), errors, "Name", paoId);
        if (commChannel instanceof TcpPortDetail) {
            validateTimingField(errors, ((TcpPortDetail) commChannel).getTiming());
        }

        if (commChannel instanceof TerminalServerPortDetailBase) {
            TerminalServerPortDetailBase<?> terminalServerPortDetail = (TerminalServerPortDetailBase<?>) commChannel;

            PortValidatorHelper.validateCarrierDetectWait(errors, terminalServerPortDetail.getCarrierDetectWaitInMilliseconds());
            validateTimingField(errors, terminalServerPortDetail.getTiming());
            PortValidatorHelper.validatePortSharingFields(errors, terminalServerPortDetail.getSharing());

            if (commChannel instanceof TcpSharedPortDetail) {
                PortValidatorHelper.validateIPAddress(errors, ((TcpSharedPortDetail) commChannel).getIpAddress(), true);
                validatePort(errors, terminalServerPortDetail.getPortNumber(), ((TcpSharedPortDetail) commChannel).getIpAddress(), paoId, commChannel.getType());
            }

            if (commChannel instanceof UdpPortDetail) {
                validatePort(errors, terminalServerPortDetail.getPortNumber(), ((UdpPortDetail) commChannel).getIpAddress(), paoId, commChannel.getType());
                PortValidatorHelper.validateEncryptionKey(errors, ((UdpPortDetail) commChannel).getKeyInHex());
            }
        }

        if (commChannel instanceof LocalSharedPortDetail) {
            PortValidatorHelper.validatePhysicalPort(errors, ((LocalSharedPortDetail) commChannel).getPhysicalPort());
            PortValidatorHelper.validateCarrierDetectWait(errors,
                    ((LocalSharedPortDetail) commChannel).getCarrierDetectWaitInMilliseconds());
            validateTimingField(errors, ((LocalSharedPortDetail) commChannel).getTiming());
            PortValidatorHelper.validatePortSharingFields(errors, ((LocalSharedPortDetail) commChannel).getSharing());
        }
    }

    private void validateTimingField(Errors errors, PortTiming timing) {
        if (!errors.hasFieldErrors("timing.preTxWait")) {
            YukonValidationUtils.checkIfFieldRequiredAndInRange(errors, "timing.preTxWait", timing.getPreTxWait(), 0, 10000000,
                    "Pre Tx Wait", true);
        }
        if (!errors.hasFieldErrors("timing.rtsToTxWait")) {
            YukonValidationUtils.checkIfFieldRequiredAndInRange(errors, "timing.rtsToTxWait", timing.getRtsToTxWait(), 0,
                    10000000, "RTS To Tx Wait", true);
        }
        if (!errors.hasFieldErrors("timing.postTxWait")) {
            YukonValidationUtils.checkIfFieldRequiredAndInRange(errors, "timing.postTxWait", timing.getPostTxWait(), 0, 10000000,
                    "Post Tx Wait", true);
        }
        if (!errors.hasFieldErrors("timing.receiveDataWait")) {
            YukonValidationUtils.checkIfFieldRequiredAndInRange(errors, "timing.receiveDataWait", timing.getReceiveDataWait(), 0,
                    1000, "Receive Data Wait", true);
        }
        if (!errors.hasFieldErrors("timing.extraTimeOut")) {
            YukonValidationUtils.checkIfFieldRequiredAndInRange(errors, "timing.extraTimeOut", timing.getExtraTimeOut(), 0, 999,
                    "Additional Time Out", true);
        }
    }

    private void validatePort(Errors errors, Integer portNumber, String ipAddress, String portIdString, PaoType portType) {
        YukonValidationUtils.validatePort(errors, "portNumber", String.valueOf(portNumber), "Port Number");
        Integer existingPortId = portDao.findUniquePortTerminalServer(ipAddress, portNumber);
        PortValidatorHelper.validateUniquePortAndIpAddress(errors, portNumber, ipAddress, existingPortId, portIdString, portType);
    }
}
