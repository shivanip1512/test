package com.cannontech.web.stars.commChannel;

import org.springframework.validation.Errors;

import com.cannontech.common.device.port.LocalSharedPortDetail;
import com.cannontech.common.device.port.PortBase;
import com.cannontech.common.device.port.PortSharing;
import com.cannontech.common.device.port.PortTiming;
import com.cannontech.common.device.port.SharedPortType;
import com.cannontech.common.device.port.TcpPortDetail;
import com.cannontech.common.device.port.TcpSharedPortDetail;
import com.cannontech.common.device.port.TerminalServerPortDetailBase;
import com.cannontech.common.validator.SimpleValidator;
import com.cannontech.common.validator.YukonValidationUtils;

public class CommChannelValidator<T extends PortBase<?>> extends SimpleValidator<T> {

    @SuppressWarnings("unchecked")
    public CommChannelValidator() {
        super((Class<T>) PortBase.class);
    }

    public CommChannelValidator(Class<T> objectType) {
        super(objectType);
    }

    @Override
    protected void doValidation(T commChannel, Errors errors) {
        YukonValidationUtils.checkIsBlank(errors, "name", commChannel.getName(), false);
        if (commChannel instanceof TcpPortDetail) {
            validateTimingField(errors, ((TcpPortDetail) commChannel).getTiming());
        }

        if (commChannel instanceof TerminalServerPortDetailBase) {
            TerminalServerPortDetailBase<?> terminalServerPortDetail = (TerminalServerPortDetailBase<?>) commChannel;
            validateTimingField(errors, terminalServerPortDetail.getTiming());

            validateSharingField(errors, terminalServerPortDetail.getSharing());

            YukonValidationUtils.checkIfFieldRequired("portNumber", errors, terminalServerPortDetail.getPortNumber(),
                    "Port Number");

            if (commChannel instanceof TcpSharedPortDetail) {
                YukonValidationUtils.checkIsBlank(errors, "ipAddress", ((TcpSharedPortDetail) commChannel).getIpAddress(), false);
            }
        }

        if (commChannel instanceof LocalSharedPortDetail) {
            validateTimingField(errors, ((LocalSharedPortDetail) commChannel).getTiming());
            validateSharingField(errors, ((LocalSharedPortDetail) commChannel).getSharing());
            YukonValidationUtils.checkIsBlank(errors, "physicalPort", ((LocalSharedPortDetail) commChannel).getPhysicalPort(), false);
        }
    }

    private void validateTimingField(Errors errors, PortTiming timing) {
        YukonValidationUtils.checkIfFieldRequired("timing.preTxWait", errors, timing.getPreTxWait(), "Pre Tx Wait");
        YukonValidationUtils.checkIfFieldRequired("timing.rtsToTxWait", errors, timing.getRtsToTxWait(), "RTS To Tx Wait");
        YukonValidationUtils.checkIfFieldRequired("timing.postTxWait", errors, timing.getPostTxWait(), "Post Tx Wait");
        YukonValidationUtils.checkIfFieldRequired("timing.receiveDataWait", errors, timing.getReceiveDataWait(),
                "Receive Data Wait");
        YukonValidationUtils.checkIfFieldRequired("timing.extraTimeOut", errors, timing.getExtraTimeOut(), "Extra Time Out");
    }

    private void validateSharingField(Errors errors, PortSharing sharing) {
        if (sharing.getSharedPortType() != SharedPortType.NONE) {
            YukonValidationUtils.checkIfFieldRequired("sharing.sharedSocketNumber", errors, sharing.getSharedSocketNumber(),
                    "Socket Number");
        }
    }
}
