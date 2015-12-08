package com.cannontech.web.capcontrol.validators;

import org.joda.time.Instant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.Errors;

import com.cannontech.common.validator.SimpleValidator;
import com.cannontech.common.validator.YukonValidationUtils;
import com.cannontech.core.schedule.dao.PaoScheduleDao;
import com.cannontech.core.schedule.model.PaoSchedule;

@Service
public class PaoScheduleValidator extends SimpleValidator<PaoSchedule> {
    
    @Autowired PaoScheduleDao paoScheduleDao;
    private String key="yukon.web.modules.capcontrol.schedules.error.";
    
    public PaoScheduleValidator() {
        super(PaoSchedule.class);
    }

    @Override
    public void doValidation(PaoSchedule schedule, Errors errors) {
        Instant nowTime = Instant.now();
        YukonValidationUtils.rejectIfEmptyOrWhitespace(
            errors, "name", "yukon.web.error.isBlank");

        //For create, we cannot take an existing name
        if (schedule.getId() == null) {
            boolean nameTaken = paoScheduleDao.doesNameExist(schedule.getName());
            if (nameTaken) {
                errors.rejectValue("name", key + "nameConflict");
            }
        //For edit, we cannot take a different schedules name
        } else {
            PaoSchedule existingWithName = paoScheduleDao.findForName(schedule.getName());
            if (existingWithName != null && ! existingWithName.getId().equals(schedule.getId())) {
                errors.rejectValue("name", key + "nameConflict");
            }
        }
        if (schedule.isLater()) {
            if (schedule.getNextRunTime() == null) {
                errors.rejectValue("nextRunTime", key + "date");
            } else if (nowTime.isAfter(schedule.getNextRunTime())) {
                errors.rejectValue("nextRunTime", key + "date.past");
            }
        }
    }
}