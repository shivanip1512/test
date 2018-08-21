package com.cannontech.web.amr.monitor.validators;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.Errors;

import com.cannontech.amr.tamperFlagProcessing.TamperFlagMonitor;
import com.cannontech.amr.tamperFlagProcessing.dao.TamperFlagMonitorDao;

@Service
public class TamperFlagMonitorValidator extends PointMonitorValidator<TamperFlagMonitor> {
    @Autowired private TamperFlagMonitorDao tamperFlagMonitorDao;

    public TamperFlagMonitorValidator() {
        super(TamperFlagMonitor.class);
    }

    @Override
    void isNameAvailable(TamperFlagMonitor monitor, Errors errors) {
        boolean idSpecified = monitor.getTamperFlagMonitorId() != null;

        boolean nameAvailable =
            !tamperFlagMonitorDao.processorExistsWithName(monitor.getName());

        if (!nameAvailable) {
            if (!idSpecified) {
                // For create, we must have an available name
                errors.rejectValue("name", "yukon.web.error.nameConflict");
            } else {
                // For edit, we can use our own existing name
                TamperFlagMonitor existingTamperFlagMonitor =
                    tamperFlagMonitorDao.getById(monitor.getTamperFlagMonitorId());
                if (!existingTamperFlagMonitor.getName().equals(
                    monitor.getName())) {
                    errors.rejectValue("name", "yukon.web.error.nameConflict");
                }
            }
        }
    }
}
