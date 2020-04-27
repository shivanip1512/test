package com.cannontech.web.api.commChannel;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.Errors;

import com.cannontech.common.device.port.PortSharing;
import com.cannontech.common.device.port.PortTiming;
import com.cannontech.common.device.port.SharedPortType;
import com.cannontech.common.device.port.TcpSharedPortDetail;
import com.cannontech.common.device.port.TerminalServerPortDetailBase;
import com.cannontech.common.device.port.dao.PortDao;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.PaoUtils;
import com.cannontech.common.validator.YukonValidationUtils;
import com.cannontech.core.dao.PaoDao;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.database.db.port.CommPort;
import com.cannontech.stars.util.ServletUtils;

public class PortValidatorHelper {

    @Autowired private PaoDao paoDao;
    @Autowired private PortDao portDao;

    private final static String key = "yukon.web.api.error.";

    public void checkIfFieldRequired(String field, Errors errors, Object fieldValue, String fieldName) {
        if (fieldValue == null) {
            errors.rejectValue(field, key + "required", new Object[] { fieldName }, "");
        }
    }

    public void validatePaoName(String paoName, PaoType type, Errors errors, String fieldName) {
        YukonValidationUtils.checkExceedsMaxLength(errors, "name", paoName, 60);
        if (!PaoUtils.isValidPaoName(paoName)) {
            errors.rejectValue("name", "yukon.web.error.paoName.containsIllegalChars");
        }

        if (!errors.hasFieldErrors("name")) {
            String paoId = ServletUtils.getPathVariable("portId");
            // Check if pao name already exists
            if (type != null && (paoId == null || !(paoDao.getYukonPAOName(Integer.valueOf(paoId)).equalsIgnoreCase(paoName)))) {
                LiteYukonPAObject unique = paoDao.findUnique(paoName, type);
                if (unique != null) {
                    errors.rejectValue("name", key + "unique", new Object[] { fieldName }, "");
                }
            }
        }
    }

    public void validatePortTimingFields(Errors errors, PortTiming timing) {
        YukonValidationUtils.checkRange(errors, "timing.preTxWait", timing.getPreTxWait(), 0, 10000000, false);
        YukonValidationUtils.checkRange(errors, "timing.rtsToTxWait", timing.getRtsToTxWait(), 0, 10000000, false);
        YukonValidationUtils.checkRange(errors, "timing.postTxWait", timing.getPostTxWait(), 0, 10000000, false);
        YukonValidationUtils.checkRange(errors, "timing.receiveDataWait", timing.getReceiveDataWait(), 0, 1000, false);
        YukonValidationUtils.checkRange(errors, "timing.extraTimeOut", timing.getExtraTimeOut(), 0, 999, false);
    }
    
    public void validatePortSharingFields(Errors errors, PortSharing sharing) {
        if ((sharing.getSharedPortType() != null && sharing.getSharedPortType() != SharedPortType.NONE)
                && sharing.getSharedSocketNumber() != null) {
            YukonValidationUtils.validatePort(errors, "sharing.sharedSocketNumber",
                    String.valueOf(sharing.getSharedSocketNumber()));
        }

        if (sharing.getSharedSocketNumber() != null) {
            if ((sharing.getSharedPortType() == null || sharing.getSharedPortType() == SharedPortType.NONE)
                    && sharing.getSharedSocketNumber() != CommPort.DEFAULT_SHARED_SOCKET_NUMBER) {
                errors.rejectValue("sharing.sharedSocketNumber", key + "udpPort.invalidSocketNumber");
            }
        }
    }

    public void validateUniquePortTerminalServer(Errors errors, TerminalServerPortDetailBase<?> serverPortDetailBase, TcpSharedPortDetail tcpSharedPortDetail) {

        Integer portId = portDao.findUniquePortTerminalServer(tcpSharedPortDetail.getIpAddress(), serverPortDetailBase.getPortNumber());

        if (portId != null) {
            errors.reject(key + "invalidPortTerminalServer",
                    new Object[] { tcpSharedPortDetail.getIpAddress(), serverPortDetailBase.getPortNumber() }, "");
        }
    }

}
