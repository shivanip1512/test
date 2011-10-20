package com.cannontech.web.stars.dr.operator.validator;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.Errors;

import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.common.validator.SimpleValidator;
import com.cannontech.stars.dr.thermostat.dao.AccountThermostatScheduleDao;
import com.cannontech.stars.dr.thermostat.model.AccountThermostatSchedule;

public class AccountThermostatScheduleValidator extends SimpleValidator<AccountThermostatSchedule>{
    
    private AccountThermostatScheduleDao accountThermostatScheduleDao;
    

    public AccountThermostatScheduleValidator() {
        super(AccountThermostatSchedule.class);
    }

    @Override
    protected void doValidation(AccountThermostatSchedule target, Errors errors) {
        
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("AND AcctThermostatScheduleId").neq(target.getAccountThermostatScheduleId());
        
        List <AccountThermostatSchedule> duplicateNames = accountThermostatScheduleDao.getSchedulesForAccountByScheduleName(target.getAccountId(), target.getScheduleName(), sql);
        //check that the schedule name is unique among schedules for this account
        if(duplicateNames.size() > 0){
            errors.rejectValue("scheduleName", "yukon.web.components.thermostat.schedule.error.duplicateName");
        }
    }
    
    @Autowired
    public void setAccountThermostatScheduleDao(AccountThermostatScheduleDao accountThermostatScheduleDao) {
        this.accountThermostatScheduleDao = accountThermostatScheduleDao;
    }

}
