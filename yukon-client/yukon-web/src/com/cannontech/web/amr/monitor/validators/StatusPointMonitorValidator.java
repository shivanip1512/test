package com.cannontech.web.amr.monitor.validators;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.Errors;

import com.cannontech.amr.statusPointMonitoring.dao.StatusPointMonitorDao;
import com.cannontech.amr.statusPointMonitoring.model.StatusPointMonitor;

@Service
public class StatusPointMonitorValidator extends PointMonitorValidator<StatusPointMonitor> {

    @Autowired private StatusPointMonitorDao statusPointMonitorDao;
    
    public StatusPointMonitorValidator() {
        super(StatusPointMonitor.class);
    }

    @Override
    void isNameAvailable(StatusPointMonitor monitor, Errors errors) {
        
        boolean idSpecified = monitor.getStatusPointMonitorId() != null;
        
        boolean nameAvailable = !statusPointMonitorDao.monitorExistsWithName(monitor.getName());
        
        if (!nameAvailable) {
            if (!idSpecified) {
                // For create, we must have an available name
                errors.rejectValue("name", "yukon.web.error.nameConflict");
            } else {
                // For edit, we can use our own existing name
                StatusPointMonitor existingDeviceDataMonitor = statusPointMonitorDao.getStatusPointMonitorById(monitor.getStatusPointMonitorId());
                if (!existingDeviceDataMonitor.getName().equals(monitor.getName())) {
                    errors.rejectValue("name", "yukon.web.error.nameConflict");
                }
            }
        }
    }

}
