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

            validateCarrierDetectWait(errors, terminalServerPortDetail.getCarrierDetectWaitInMilliseconds());
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
            validateCarrierDetectWait(errors, ((LocalSharedPortDetail) commChannel).getCarrierDetectWaitInMilliseconds());
            validateTimingField(errors, ((LocalSharedPortDetail) commChannel).getTiming());
            PortValidatorHelper.validatePortSharingFields(errors, ((LocalSharedPortDetail) commChannel).getSharing());
        }
    }

    private void validateTimingField(Errors errors, PortTiming timing) {
        if (!errors.hasFieldErrors("timing.preTxWait")) {
            YukonValidationUtils.checkIfFieldRequired("timing.preTxWait", errors, timing.getPreTxWait(), "Pre Tx Wait");
        }
        if (!errors.hasFieldErrors("timing.rtsToTxWait")) {
            YukonValidationUtils.checkIfFieldRequired("timing.rtsToTxWait", errors, timing.getRtsToTxWait(), "RTS To Tx Wait");
        }
        if (!errors.hasFieldErrors("timing.postTxWait")) {
            YukonValidationUtils.checkIfFieldRequired("timing.postTxWait", errors, timing.getPostTxWait(), "Post Tx Wait");
        }
        if (!errors.hasFieldErrors("timing.receiveDataWait")) {
            YukonValidationUtils.checkIfFieldRequired("timing.receiveDataWait", errors, timing.getReceiveDataWait(),
                    "Receive Data Wait");
        }
        if (!errors.hasFieldErrors("timing.extraTimeOut")) {
            YukonValidationUtils.checkIfFieldRequired("timing.extraTimeOut", errors, timing.getExtraTimeOut(),
                    "Additional Time Out");
        }
        PortValidatorHelper.validatePortTimingFields(errors, timing);
    }

    private void validatePort(Errors errors, Integer portNumber, String ipAddress, String portIdString, PaoType portType) {
        YukonValidationUtils.validatePort(errors, "portNumber", String.valueOf(portNumber));
        Integer existingPortId = portDao.findUniquePortTerminalServer(ipAddress, portNumber);
        PortValidatorHelper.validateUniquePortAndIpAddress(errors, portNumber, ipAddress, existingPortId, portIdString, portType);
    }

    private void validateCarrierDetectWait(Errors errors, Integer carrierDetectWaitInMilliseconds) {
        if (carrierDetectWaitInMilliseconds != null) {
            YukonValidationUtils.checkRange(errors, "carrierDetectWaitInMilliseconds", carrierDetectWaitInMilliseconds, 0, 9999, false);
        }
    }
}
