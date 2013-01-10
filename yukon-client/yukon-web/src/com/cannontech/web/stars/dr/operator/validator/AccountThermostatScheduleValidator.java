package com.cannontech.web.stars.dr.operator.validator;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.springframework.validation.Errors;

import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.common.validator.SimpleValidator;
import com.cannontech.stars.dr.thermostat.dao.AccountThermostatScheduleDao;
import com.cannontech.stars.dr.thermostat.model.AccountThermostatSchedule;
import com.cannontech.stars.dr.thermostat.model.AccountThermostatScheduleEntry;
import com.cannontech.stars.dr.thermostat.model.TimeOfWeek;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.Sets;

public class AccountThermostatScheduleValidator extends SimpleValidator<AccountThermostatSchedule>{
    
    private AccountThermostatScheduleDao accountThermostatScheduleDao;
    private MessageSourceAccessor messageSourceAccessor;
    
    private static final int SCHEDULE_NAME_LENGTH = 60;

    public AccountThermostatScheduleValidator(AccountThermostatScheduleDao accountThermostatScheduleDao, MessageSourceAccessor messageSourceAccessor) {
        super(AccountThermostatSchedule.class);
        
        this.accountThermostatScheduleDao = accountThermostatScheduleDao;
        this.messageSourceAccessor = messageSourceAccessor;
    }

    @Override
    protected void doValidation(AccountThermostatSchedule target, Errors errors) {
        
        if (StringUtils.isBlank(target.getScheduleName())) {
            errors.rejectValue("scheduleName", "yukon.web.components.thermostat.schedule.error.blankName");
        }

        if (target.getScheduleName().length() > SCHEDULE_NAME_LENGTH) {
            errors.rejectValue("scheduleName", "yukon.web.components.thermostat.schedule.error.scheduleNameTooLong",
                             new Object[] { SCHEDULE_NAME_LENGTH },
                             null);
        }
        
        //we only care about the schedule name on real accounts.  per com.cannontech.stars.dr.thermostat.dao.impl.DefaultAccountThermostatScheduleHelper
        //the default schedules have an accountId of 0
        if(target.getAccountId() != 0){
            List <AccountThermostatSchedule> duplicateNames = accountThermostatScheduleDao.getSchedulesForAccountByScheduleName(target.getAccountId(), target.getScheduleName(), target.getAccountThermostatScheduleId());
            //check that the schedule name is unique among schedules for this account
            if(duplicateNames.size() > 0){
                errors.rejectValue("scheduleName", "yukon.web.components.thermostat.schedule.error.duplicateName");
            }
        }
        
        //check for extra days
        Set<TimeOfWeek> excludedValues = Sets.newHashSet(Arrays.asList(TimeOfWeek.values()));
        
        //verify the values and quantity of schedule entries
        //remove the allowable values
        excludedValues.removeAll(target.getThermostatScheduleMode().getAssociatedTimeOfWeeks());
        ListMultimap<TimeOfWeek, AccountThermostatScheduleEntry> entriesByTimeOfWeekMultimap = target.getEntriesByTimeOfWeekMultimap();
        
        //check if the entries contains any unallowed days of the week
        String invalidDays = "";
        for(TimeOfWeek timeOfWeek : excludedValues){
            if(entriesByTimeOfWeekMultimap.containsKey(timeOfWeek)){
                invalidDays += messageSourceAccessor.getMessage("yukon.web.components.thermostat.schedule." + timeOfWeek) + "  ";
            }
        }
        
        if(invalidDays.length() > 0){
            errors.rejectValue("scheduleEntries", "yukon.web.components.thermostat.schedule.error.invalidScheduleEntryTimeOfWeek", 
                               new Object[]{invalidDays}, 
                               null);
        }
        
        //make sure we have the correct number of days in the week given the schedule mode
        if(entriesByTimeOfWeekMultimap.keySet().size() != target.getThermostatScheduleMode().getAssociatedTimeOfWeeks().size()){
        	errors.rejectValue("scheduleEntries", "yukon.web.components.thermostat.schedule.error.invalidTimeOfWeekCount");
        }
        
        //invalid number of periods for a given day
        String invalidPeriodCounts = "";
        for(TimeOfWeek timeOfWeek : entriesByTimeOfWeekMultimap.keySet()){
            if(entriesByTimeOfWeekMultimap.get(timeOfWeek).size() != target.getThermostatType().getPeriodStyle().getAllPeriods().size()){
                invalidPeriodCounts += messageSourceAccessor.getMessage("yukon.web.components.thermostat.schedule." + timeOfWeek) + " ";
            }

            //check that each day has valid period temperatures
            int considered_entries = 0;
            for(int i=0; i<entriesByTimeOfWeekMultimap.get(timeOfWeek).size(); i++){
            	AccountThermostatScheduleEntry entry = entriesByTimeOfWeekMultimap.get(timeOfWeek).get(i);
            	if((entry.getCoolTemp().toFahrenheit().toIntValue() != -1) ||
        			(entry.getHeatTemp().toFahrenheit().toIntValue() != -1)){
            		considered_entries++;
            		//lower cool limit
            		if(entry.getCoolTemp().compareTo(target.getThermostatType().getLowerLimitCool()) < 0){
            			errors.rejectValue("scheduleEntries", "yukon.web.components.thermostat.schedule.error.invalidScheduleEntryTemperature.cool.tooCold", entry.getCoolTemp().toString());
            		}
            		//upper cool limit
            		if(entry.getCoolTemp().compareTo(target.getThermostatType().getUpperLimitCool()) > 0){
            			errors.rejectValue("scheduleEntries", "yukon.web.components.thermostat.schedule.error.invalidScheduleEntryTemperature.cool.tooWarm", entry.getCoolTemp().toString());
            		}
            		//lower heat limit
            		if(entry.getHeatTemp().compareTo(target.getThermostatType().getLowerLimitHeat()) < 0){
            			errors.rejectValue("scheduleEntries", "yukon.web.components.thermostat.schedule.error.invalidScheduleEntryTemperature.heat.tooCold", entry.getHeatTemp().toString());
            		}
            		//upper heat limit
            		if(entry.getHeatTemp().compareTo(target.getThermostatType().getUpperLimitHeat()) > 0){
            			errors.rejectValue("scheduleEntries", "yukon.web.components.thermostat.schedule.error.invalidScheduleEntryTemperature.heat.tooWarm", entry.getHeatTemp().toString());
            		}
            	}
            }
            
            if(considered_entries != target.getThermostatType().getPeriodStyle().getRealPeriods().size()){
            	invalidPeriodCounts += messageSourceAccessor.getMessage("yukon.web.components.thermostat.schedule." + timeOfWeek);
            }
            
        }
        
        if(invalidPeriodCounts.length() > 0){
            errors.rejectValue("scheduleEntries", "yukon.web.components.thermostat.schedule.error.invalidScheduleEntryCount", 
                               new Object[]{invalidPeriodCounts}, 
                               null);
        }
        
    }

}
