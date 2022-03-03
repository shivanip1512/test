package com.cannontech.web.capcontrol.validators;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.Errors;

import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.PaoUtils;
import com.cannontech.common.validator.SimpleValidator;
import com.cannontech.common.validator.YukonValidationHelper;
import com.cannontech.common.validator.YukonValidationUtils;
import com.cannontech.core.dao.PaoDao;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.database.db.capcontrol.DeviceCBC;
import com.cannontech.web.editor.CapControlCBC;
import com.cannontech.web.stars.rtu.validator.RtuDnpValidationUtil;
import com.cannontech.yukon.IDatabaseCache;

@Service
public class CbcValidator extends SimpleValidator<CapControlCBC> {

    @Autowired private IDatabaseCache dbCache;
    @Autowired private PaoDao paoDao;
    @Autowired private YukonValidationHelper yukonValidationHelper;
    @Autowired private RtuDnpValidationUtil rtuDnpValidationUtil;

    private static final String basekey = "yukon.web.modules.capcontrol.cbc.error";

    public CbcValidator() {
        super(CapControlCBC.class);
    }

    @Override
    public void doValidation(CapControlCBC cbc, Errors errors) {
        validateName(cbc, errors);
        validateSerialNumber(cbc, errors);
        if (cbc.isTwoWay()) {
            //  Validate the IP and port before validating the addressing
            if (cbc.getPaoType().isTcpPortEligible() &&
                    dbCache.getAllPaosMap().get(cbc.getDeviceDirectCommSettings().getPortID()).getPaoType() == PaoType.TCPPORT) {
                YukonValidationUtils.ipHostNameValidator(errors, "ipAddress", cbc.getIpAddress());
                YukonValidationUtils.validatePort(errors, "port",
                        yukonValidationHelper.getMessage("yukon.web.modules.capcontrol.cbc.port"), cbc.getPort());
            }
            rtuDnpValidationUtil.validateAddressing(
                    cbc.getId(),
                    cbc.getDeviceDirectCommSettings(), 
                    cbc.getDeviceAddress(),
                    cbc.getIpAddress(),
                    cbc.getPort(),
                    errors, basekey + ".masterSlave");
            if (!cbc.getDeviceScanRateMap().isEmpty()) {
                validateScanIntervals(cbc, errors);
            }
        }
        if (cbc.isLogical()) {
            YukonValidationUtils.rejectIfEmptyOrWhitespace(errors, "parentRtuId", basekey + ".parentRTURequired");
        }
   }

    private void validateName(CapControlCBC cbc, Errors errors) {
        YukonValidationUtils.rejectIfEmptyOrWhitespace(errors, "name", "yukon.web.error.isBlank");
        
        if(!errors.hasFieldErrors("name")){
            if(!PaoUtils.isValidPaoName(cbc.getName())){
                errors.rejectValue("name", "yukon.web.error.paoName.containsIllegalChars");
            }
        }

        YukonValidationUtils.checkExceedsMaxLength(errors, "name", cbc.getName(), 60);
        boolean idSpecified = cbc.getId() != null;

        boolean nameAvailable = paoDao.isNameAvailable(cbc.getName(), cbc.getPaoType());
        
        if (!nameAvailable) {
            if (!idSpecified) {
                //For create, we must have an available name
                errors.rejectValue("name", "yukon.web.error.nameConflict");
            } else {
                //For edit, we can use our own existing name
                LiteYukonPAObject existingPao = dbCache.getAllPaosMap().get(cbc.getId());
                if (!existingPao.getPaoName().equals(cbc.getName())) {
                    errors.rejectValue("name", "yukon.web.error.nameConflict");
                }
            }
        }
    }

    private void validateScanIntervals(CapControlCBC cbc, Errors errors) {
        cbc.getDeviceScanRateMap().forEach((key, deviceScanRate) -> {
            if (deviceScanRate.getAlternateRate().equals(deviceScanRate.getIntervalRate())) {
                errors.rejectValue("deviceScanRateMap['" + key + "'].alternateRate",
                    "yukon.web.error.valuesCannotBeSame");
                errors.rejectValue("deviceScanRateMap['" + key + "'].intervalRate",
                    "yukon.web.error.valuesCannotBeSame");
            }
        });
    }

    private void validateSerialNumber(CapControlCBC cbc, Errors errors) {
        Integer serialNumber = cbc.getDeviceCBC().getSerialNumber();
        YukonValidationUtils.checkRange(errors, "deviceCBC.serialNumber", serialNumber,
            0, Integer.MAX_VALUE, true);
        if (serialNumber != null) {
            String[] paos = DeviceCBC.isSerialNumberUnique(serialNumber, cbc.getId());
            if (paos != null && paos.length > 0) {
                errors.rejectValue("deviceCBC.serialNumber", basekey + ".duplicateSerial", paos, "Serial Number in use");
            }
        }
    }
}