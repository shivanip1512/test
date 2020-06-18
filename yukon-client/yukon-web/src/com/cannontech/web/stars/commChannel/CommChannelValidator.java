package com.cannontech.web.stars.commChannel;

import org.apache.commons.lang3.StringUtils;
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
import com.cannontech.common.util.Range;
import com.cannontech.common.validator.PortValidatorHelper;
import com.cannontech.common.validator.SimpleValidator;
import com.cannontech.common.validator.YukonValidationHelper;
import com.cannontech.common.validator.YukonValidationUtils;

public class CommChannelValidator<T extends PortBase<?>> extends SimpleValidator<T> {
    @Autowired private YukonValidationHelper yukonValidationHelper;
    @Autowired private PortDao portDao;

    private static final String baseKey = "yukon.web.modules.operator.commChannelInfoWidget.";

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
            if (StringUtils.isNotEmpty(paoId)) {
                validateTimingField(errors, ((TcpPortDetail) commChannel).getTiming());
            }
        }

        if (commChannel instanceof TerminalServerPortDetailBase) {
            TerminalServerPortDetailBase<?> terminalServerPortDetail = (TerminalServerPortDetailBase<?>) commChannel;

            if (StringUtils.isNotEmpty(paoId)) {

                PortValidatorHelper.validateCarrierDetectWait(errors, terminalServerPortDetail.getCarrierDetectWaitInMilliseconds(),
                        yukonValidationHelper.getMessage(baseKey + "carrierDetectWait"));
                validateTimingField(errors, terminalServerPortDetail.getTiming());
                PortValidatorHelper.validatePortSharingFields(errors, terminalServerPortDetail.getSharing(),
                        yukonValidationHelper.getMessage(baseKey + "socketNumber"));
            }

            if (commChannel instanceof TcpSharedPortDetail) {
                PortValidatorHelper.validateIPAddress(errors, ((TcpSharedPortDetail) commChannel).getIpAddress(),
                        yukonValidationHelper.getMessage(baseKey + "ipAddress"), true);
                validatePort(errors, terminalServerPortDetail.getPortNumber(), ((TcpSharedPortDetail) commChannel).getIpAddress(),
                        paoId, commChannel.getType());
            }

            if (commChannel instanceof UdpPortDetail) {
                validatePort(errors, terminalServerPortDetail.getPortNumber(), ((UdpPortDetail) commChannel).getIpAddress(),
                        paoId, commChannel.getType());
                PortValidatorHelper.validateEncryptionKey(errors, ((UdpPortDetail) commChannel).getKeyInHex());
            }
        }

        if (commChannel instanceof LocalSharedPortDetail) {

            PortValidatorHelper.validatePhysicalPort(errors, ((LocalSharedPortDetail) commChannel).getPhysicalPort(),
                    yukonValidationHelper.getMessage(baseKey + "physicalPort"));
            if (StringUtils.isNotEmpty(paoId)) {
                PortValidatorHelper.validateCarrierDetectWait(errors,
                        ((LocalSharedPortDetail) commChannel).getCarrierDetectWaitInMilliseconds(),
                        yukonValidationHelper.getMessage(baseKey + "carrierDetectWait"));
                validateTimingField(errors, ((LocalSharedPortDetail) commChannel).getTiming());
                PortValidatorHelper.validatePortSharingFields(errors, ((LocalSharedPortDetail) commChannel).getSharing(),
                        yukonValidationHelper.getMessage(baseKey + "socketNumber"));
            }
        }
    }

    private void validateTimingField(Errors errors, PortTiming timing) {
        if (!errors.hasFieldErrors("timing.preTxWait")) {
            Range<Integer> range = Range.inclusive(0, 10000000);
            YukonValidationUtils.checkRange(errors, "timing.preTxWait", yukonValidationHelper.getMessage(baseKey + "preTxWait"),
                    timing.getPreTxWait(), range, true);
        }
        if (!errors.hasFieldErrors("timing.rtsToTxWait")) {
            Range<Integer> range = Range.inclusive(0, 10000000);
            YukonValidationUtils.checkRange(errors, "timing.rtsToTxWait",
                    yukonValidationHelper.getMessage(baseKey + "rtsToTxWait"), timing.getRtsToTxWait(), range, true);
        }
        if (!errors.hasFieldErrors("timing.postTxWait")) {
            Range<Integer> range = Range.inclusive(0, 10000000);
            YukonValidationUtils.checkRange(errors, "timing.postTxWait", yukonValidationHelper.getMessage(baseKey + "postTxWait"),
                    timing.getPostTxWait(), range, true);
        }
        if (!errors.hasFieldErrors("timing.receiveDataWait")) {
            Range<Integer> range = Range.inclusive(0, 1000);
            YukonValidationUtils.checkRange(errors, "timing.receiveDataWait",
                    yukonValidationHelper.getMessage(baseKey + "receiveDataWait"), timing.getReceiveDataWait(), range, true);
        }
        if (!errors.hasFieldErrors("timing.extraTimeOut")) {
            Range<Integer> range = Range.inclusive(0, 999);
            YukonValidationUtils.checkRange(errors, "timing.extraTimeOut",
                    yukonValidationHelper.getMessage(baseKey + "additionalTimeOut"), timing.getExtraTimeOut(), range, true);
        }
    }

    private void validatePort(Errors errors, Integer portNumber, String ipAddress, String portIdString, PaoType portType) {
        YukonValidationUtils.validatePort(errors, "portNumber", yukonValidationHelper.getMessage(baseKey + "portNumber"),
                String.valueOf(portNumber));
        Integer existingPortId = portDao.findUniquePortTerminalServer(ipAddress, portNumber);
        PortValidatorHelper.validateUniquePortAndIpAddress(errors, portNumber, ipAddress, existingPortId, portIdString, portType);
    }
}