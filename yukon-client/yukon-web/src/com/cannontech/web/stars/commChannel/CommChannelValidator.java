package com.cannontech.web.stars.commChannel;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.Errors;

import com.cannontech.common.device.port.LocalSharedPortDetail;
import com.cannontech.common.device.port.PortBase;
import com.cannontech.common.device.port.PortSharing;
import com.cannontech.common.device.port.PortTiming;
import com.cannontech.common.device.port.SharedPortType;
import com.cannontech.common.device.port.TcpPortDetail;
import com.cannontech.common.device.port.TcpSharedPortDetail;
import com.cannontech.common.device.port.TerminalServerPortDetailBase;
import com.cannontech.common.pao.PaoUtils;
import com.cannontech.common.validator.SimpleValidator;
import com.cannontech.common.validator.YukonValidationHelper;

public class CommChannelValidator<T extends PortBase<?>> extends SimpleValidator<T> {

    @Autowired private YukonValidationHelper yukonValidationHelper;

    @SuppressWarnings("unchecked")
    public CommChannelValidator() {
        super((Class<T>) PortBase.class);
    }

    public CommChannelValidator(Class<T> objectType) {
        super(objectType);
    }

    @Override
    protected void doValidation(PortBase commChannel, Errors errors) {
        yukonValidationHelper.checkIfFieldRequired("name", errors, commChannel.getName(), "Name");
        if (!errors.hasFieldErrors("name")) {
            if (!PaoUtils.isValidPaoName(commChannel.getName())) {
                errors.rejectValue("name", "yukon.web.error.paoName.containsIllegalChars");
            }
        }
        if (commChannel instanceof TcpPortDetail) {
            validateTimingField(errors, ((TcpPortDetail) commChannel).getTiming());
        }

        if (commChannel instanceof TerminalServerPortDetailBase) {
            validateTimingField(errors, ((TerminalServerPortDetailBase) commChannel).getTiming());

            validateSharingField(errors, ((TerminalServerPortDetailBase) commChannel).getSharing());

            yukonValidationHelper.checkIfFieldRequired("portNumber", errors,
                    ((TerminalServerPortDetailBase) commChannel).getPortNumber(), "Port Number");

            if (commChannel instanceof TcpSharedPortDetail) {
                yukonValidationHelper.checkIfFieldRequired("ipAddress", errors,
                        ((TcpSharedPortDetail) commChannel).getIpAddress(), "IP Address");
            }
        }

        if (commChannel instanceof LocalSharedPortDetail) {
            validateTimingField(errors, ((LocalSharedPortDetail) commChannel).getTiming());
            validateSharingField(errors, ((LocalSharedPortDetail) commChannel).getSharing());
        }
    }

    private void validateTimingField(Errors errors, PortTiming timing) {
        yukonValidationHelper.checkIfFieldRequired("timing.preTxWait", errors, timing.getPreTxWait(), "Pre Tx Wait");
        yukonValidationHelper.checkIfFieldRequired("timing.rtsToTxWait", errors, timing.getRtsToTxWait(), "RTS To Tx Wait");
        yukonValidationHelper.checkIfFieldRequired("timing.postTxWait", errors, timing.getPostTxWait(), "Post Tx Wait");
        yukonValidationHelper.checkIfFieldRequired("timing.receiveDataWait", errors, timing.getReceiveDataWait(), "Receive Data Wait");
        yukonValidationHelper.checkIfFieldRequired("timing.extraTimeOut", errors, timing.getExtraTimeOut(), "Extra Time Out");
    }

    private void validateSharingField(Errors errors, PortSharing sharing) {
        if (sharing.getSharedPortType() != SharedPortType.NONE) {
            yukonValidationHelper.checkIfFieldRequired("sharing.sharedSocketNumber", errors, sharing.getSharedSocketNumber(),
                    "Socket Number");
        }
    }
}
