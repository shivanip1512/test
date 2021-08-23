package com.cannontech.web.stars.rtu.validator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.Errors;

import com.cannontech.common.pao.PaoType;
import com.cannontech.common.rtu.model.RtuDnp;
import com.cannontech.common.validator.SimpleValidator;
import com.cannontech.common.validator.YukonValidationHelper;
import com.cannontech.common.validator.YukonValidationUtils;
import com.cannontech.yukon.IDatabaseCache;

@Service
public class RtuDnpValidator extends SimpleValidator<RtuDnp> {

    @Autowired private IDatabaseCache dbCache;
    @Autowired private RtuDnpValidationUtil rtuDnpValidationUtil; 
    @Autowired private YukonValidationHelper yukonValidationHelper;
   
    private static final String basekey = "yukon.web.modules.operator.rtuDetail.error";

    public RtuDnpValidator() {
        super(RtuDnp.class);
    }

    @Override
    protected void doValidation(RtuDnp rtuDnp, Errors errors) {
        rtuDnpValidationUtil.validateName(rtuDnp, errors, false);
        rtuDnpValidationUtil.validateAddressing(
                rtuDnp.getDeviceDirectCommSettings(), 
                rtuDnp.getDeviceAddress(), 
                rtuDnp.getIpAddress(),
                rtuDnp.getPort(),
                errors, basekey + ".masterSlave");
        validateScanIntervals(rtuDnp, errors);
        YukonValidationUtils.checkIsBlank(errors, "port", rtuDnp.getPort(), yukonValidationHelper.getMessage("yukon.web.modules.operator.rtuDetail.port"), false);
        if (!errors.hasFieldErrors("deviceDirectCommSettings.portID") && !errors.hasFieldErrors("port")) {
            if (dbCache.getAllPaosMap().get(rtuDnp.getDeviceDirectCommSettings().getPortID()).getPaoType() == PaoType.TCPPORT) {
                YukonValidationUtils.ipHostNameValidator(errors, "ipAddress", rtuDnp.getIpAddress());
                YukonValidationUtils.validatePort(errors, "port",
                        yukonValidationHelper.getMessage("yukon.web.modules.operator.rtuDetail.port"), rtuDnp.getPort());
            }
        }
    }

    private void validateScanIntervals(RtuDnp rtuDnp, Errors errors) {
        rtuDnp.getDeviceScanRateMap().forEach((key, deviceScanRate) -> {
            if (deviceScanRate.getAlternateRate().equals(deviceScanRate.getIntervalRate())) {
                errors.rejectValue("deviceScanRateMap['" + key + "'].alternateRate",
                    "yukon.web.error.valuesCannotBeSame");
                errors.rejectValue("deviceScanRateMap['" + key + "'].intervalRate",
                    "yukon.web.error.valuesCannotBeSame");
            }
        });
    }
}