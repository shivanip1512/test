package com.cannontech.web.amr.monitor.validators;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.Errors;

import com.cannontech.amr.tamperFlagProcessing.TamperFlagMonitor;
import com.cannontech.amr.tamperFlagProcessing.dao.TamperFlagMonitorDao;
import com.cannontech.common.device.groups.service.DeviceGroupService;
import com.cannontech.common.device.groups.util.DeviceGroupUtil;
import com.cannontech.common.validator.SimpleValidator;
import com.cannontech.common.validator.YukonValidationUtils;

@Service
public class TamperFlagMonitorValidator extends SimpleValidator<TamperFlagMonitor> {
    @Autowired private TamperFlagMonitorDao tamperFlagMonitorDao;
    @Autowired private DeviceGroupService deviceGroupService;

    public TamperFlagMonitorValidator() {
        super(TamperFlagMonitor.class);
    }

    @Override
    public void doValidation(TamperFlagMonitor tamperFlagMonitor, Errors errors) {

        validateName(tamperFlagMonitor, errors);
        if (!errors.hasErrors() && (tamperFlagMonitor.getGroupName() == null
            || deviceGroupService.findGroupName(tamperFlagMonitor.getGroupName()) == null)) {
            errors.reject("yukon.web.modules.amr.invalidGroupName");
        }
    }

    private void validateName(TamperFlagMonitor tamperFlagMonitor, Errors errors) {

        YukonValidationUtils.rejectIfEmptyOrWhitespace(errors, "tamperFlagMonitorName", "yukon.web.error.isBlank");
        if (!errors.hasFieldErrors("tamperFlagMonitorName")) {
            YukonValidationUtils.checkExceedsMaxLength(errors, "tamperFlagMonitorName",
                tamperFlagMonitor.getTamperFlagMonitorName(), 60);
            if (!DeviceGroupUtil.isValidName(tamperFlagMonitor.getTamperFlagMonitorName())) {
                errors.rejectValue("tamperFlagMonitorName", "yukon.web.error.deviceGroupName.containsIllegalChars");
            }
        }
        boolean idSpecified = tamperFlagMonitor.getTamperFlagMonitorId() != null;

        boolean nameAvailable =
            !tamperFlagMonitorDao.processorExistsWithName(tamperFlagMonitor.getTamperFlagMonitorName());

        if (!nameAvailable) {
            if (!idSpecified) {
                // For create, we must have an available name
                errors.rejectValue("tamperFlagMonitorName", "yukon.web.error.nameConflict");
            } else {
                // For edit, we can use our own existing name
                TamperFlagMonitor existingTamperFlagMonitor =
                    tamperFlagMonitorDao.getById(tamperFlagMonitor.getTamperFlagMonitorId());
                if (!existingTamperFlagMonitor.getTamperFlagMonitorName().equals(
                    tamperFlagMonitor.getTamperFlagMonitorName())) {
                    errors.rejectValue("tamperFlagMonitorName", "yukon.web.error.nameConflict");
                }
            }
        }
    }
}
