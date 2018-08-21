package com.cannontech.web.amr.monitor.validators;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.Errors;

import com.cannontech.amr.deviceDataMonitor.dao.DeviceDataMonitorDao;
import com.cannontech.amr.deviceDataMonitor.model.DeviceDataMonitor;
import com.cannontech.amr.deviceDataMonitor.model.DeviceDataMonitorProcessor;
import com.cannontech.amr.deviceDataMonitor.model.ProcessorType;
import com.cannontech.common.validator.YukonValidationUtils;

@Service
public class DeviceDataMonitorValidator extends PointMonitorValidator<DeviceDataMonitor> {
    @Autowired private DeviceDataMonitorDao deviceDataMonitorDao;

    public DeviceDataMonitorValidator() {
        super(DeviceDataMonitor.class);
    }

    @Override
    public void doValidation(DeviceDataMonitor monitor, Errors errors) {
        super.doValidation(monitor, errors);
        validateProcessors(monitor.getProcessors(), errors);
    }

    @Override
    void isNameAvailable(DeviceDataMonitor monitor, Errors errors) {
        boolean idSpecified = monitor.getId() != null;

        boolean nameAvailable = !deviceDataMonitorDao.processorExistsWithName(monitor.getName());

        if (!nameAvailable) {
            if (!idSpecified) {
                // For create, we must have an available name
                errors.rejectValue("name", "yukon.web.error.nameConflict");
            } else {
                // For edit, we can use our own existing name
                DeviceDataMonitor existingDeviceDataMonitor = deviceDataMonitorDao.getMonitorById(monitor.getId());
                if (!existingDeviceDataMonitor.getName().equals(monitor.getName())) {
                    errors.rejectValue("name", "yukon.web.error.nameConflict");
                }
            }
        }
    }
    
    private void validateProcessors(List<DeviceDataMonitorProcessor> processors, Errors errors) {
        int index = 0;
        for (DeviceDataMonitorProcessor processor : processors) {
            YukonValidationUtils.rejectIfEmptyOrWhitespace(errors, "processors[" + index + "].attribute", "yukon.web.error.isBlank");
            if (processor.getType() != ProcessorType.STATE) {
                if (processor.getType() == ProcessorType.RANGE || processor.getType() == ProcessorType.OUTSIDE) {
                    String rangeMinField = "processors[" + index + "].rangeMin";
                    String rangeMaxField = "processors[" + index + "].rangeMax";
                    YukonValidationUtils.rejectIfEmptyOrWhitespace(errors, rangeMinField, "yukon.web.error.isBlank");
                    YukonValidationUtils.rejectIfEmptyOrWhitespace(errors, rangeMaxField, "yukon.web.error.isBlank");
                    if (!errors.hasFieldErrors(rangeMinField) && !errors.hasFieldErrors(rangeMaxField)) {
                        if (processor.getRangeMin() >= processor.getRangeMax()) {
                            errors.rejectValue(rangeMinField, "yukon.web.modules.amr.deviceDataMonitor.validationError.minGreaterThanMax");
                        }
                    }
                } else {
                    YukonValidationUtils.rejectIfEmptyOrWhitespace(errors, "processors[" + index + "].processorValue", "yukon.web.error.isBlank");
                }
            }
            index++;
        }
    }
}