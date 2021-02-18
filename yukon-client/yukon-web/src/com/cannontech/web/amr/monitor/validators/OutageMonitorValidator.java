package com.cannontech.web.amr.monitor.validators;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.Errors;

import com.cannontech.amr.outageProcessing.OutageMonitor;
import com.cannontech.amr.outageProcessing.dao.OutageMonitorDao;
import com.cannontech.common.validator.YukonValidationUtils;

@Service
public class OutageMonitorValidator extends PointMonitorValidator<OutageMonitor> {

    @Autowired private OutageMonitorDao outageMonitorDao;
    
    public OutageMonitorValidator() {
        super(OutageMonitor.class);
    }

    @Override
    public void doValidation(OutageMonitor outageMonitor, Errors errors) {
        super.doValidation(outageMonitor, errors);
        
        YukonValidationUtils.checkIsNumberPositiveIntOrDouble(errors, "numberOfOutages", outageMonitor.getNumberOfOutages());
        if (!errors.hasFieldErrors("numberOfOutages") && outageMonitor.getNumberOfOutages() < 1) {
            errors.rejectValue("numberOfOutages", "yukon.web.modules.amr.outageMonitorConfig.invalidNumberOfOutages");
        }

        YukonValidationUtils.checkIsNumberPositiveIntOrDouble(errors, "timePeriodDays", outageMonitor.getTimePeriodDays());
        if (!errors.hasFieldErrors("timePeriodDays") && outageMonitor.getTimePeriodDays() < 0) {
            errors.rejectValue("timePeriodDays", "yukon.web.modules.amr.outageMonitorConfig.invalidTimePeriod");
        }
        if (outageMonitor.isScheduleGroupCommand() && StringUtils.isBlank(outageMonitor.getScheduleName())) {
            errors.rejectValue("scheduleName", "yukon.web.modules.amr.outageMonitorConfig.scheduleName.blank");
        }
    }

    @Override
    void isNameAvailable(OutageMonitor outageMonitor, Errors errors) {
        boolean idSpecified = outageMonitor.getOutageMonitorId() != null;

        boolean nameAvailable = !outageMonitorDao.processorExistsWithName(outageMonitor.getName());

        if (!nameAvailable) {
            if (!idSpecified) {
                // For create, we must have an available name
                errors.rejectValue("name", "yukon.web.error.nameConflict");
            } else {
                // For edit, we can use our own existing name
                OutageMonitor existingOutageMonitor = outageMonitorDao.getById(outageMonitor.getOutageMonitorId());
                if (!existingOutageMonitor.getName().equals(outageMonitor.getName())) {
                    errors.rejectValue("name", "yukon.web.error.nameConflict");
                }
            }
        }
    }
}
