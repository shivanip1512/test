package com.cannontech.amr.macsscheduler.model;

import java.util.Date;

import com.cannontech.amr.macsscheduler.model.MacsSchedule.State;
import com.cannontech.amr.macsscheduler.model.MacsSchedule.Type;
import com.cannontech.amr.macsscheduler.model.MacsStartPolicy.StartPolicy;
import com.cannontech.amr.macsscheduler.model.MacsStopPolicy.StopPolicy;
import com.cannontech.message.macs.message.Schedule;
import com.google.common.base.Strings;

public class MacsScheduleHelper {
    
    public static Schedule convert(MacsSchedule macsSchedule){
        Schedule schedule = new Schedule();
        schedule.setId(macsSchedule.getId());
        schedule.setScheduleName(macsSchedule.getScheduleName());
        schedule.setCategoryName(macsSchedule.getCategoryName());
        schedule.setType(macsSchedule.getType().getTypeString());
        schedule.setCurrentState(macsSchedule.getState().getStateString());
        if (macsSchedule.getLastRunTime() != null) {
            schedule.setLastRunTime(macsSchedule.getLastRunTime());
        }
        schedule.setLastRunStatus(macsSchedule.getStatus().getStatusString());
        schedule.setCurrentState(macsSchedule.getState().getStateString());
  
        //start policy
        MacsStartPolicy start = macsSchedule.getStartPolicy();
        schedule.setStartPolicy(start.getPolicy().getPolicyString());
        schedule.setStartDay(start.getStartDay());
        schedule.setStartMonth(start.getStartMonth());
        schedule.setStartYear(start.getStartYear());
        if (!Strings.isNullOrEmpty(start.getStartTime())) {
            schedule.setStartTime(start.getStartTime());
        }
        schedule.setHolidayScheduleId(start.getHolidayScheduleId());
   
        //stop policy
        MacsStopPolicy stop = macsSchedule.getStopPolicy();
        schedule.setDuration(stop.getDuration());
        schedule.setStopPolicy(stop.getPolicy().getPolicyString());
        if (!Strings.isNullOrEmpty(stop.getStopTime())) {
            schedule.setStopTime(stop.getStopTime());
        }
        
        if (macsSchedule.isScript()) {
            MacsScriptOptions options = macsSchedule.getScriptOptions();
            schedule.setScriptFileName(options.getFileName());
        } else if (macsSchedule.isSimple()) {
            MacsSimpleOptions options = macsSchedule.getSimpleOptions();
            if (!Strings.isNullOrEmpty(options.getStartCommand())) {
                schedule.setStartCommand(options.getStartCommand());
            }
            if (!Strings.isNullOrEmpty(options.getStopCommand())) {
                schedule.setStartCommand(options.getStopCommand());
            }
            schedule.setRepeatInterval(options.getRepeatInterval());
            schedule.setTargetPAObjectId(options.getTargetPAObjectId());
        }
        return schedule;
    }
    
    public static MacsSchedule convert(Schedule schedule){
        MacsSchedule macsSchedule  = new MacsSchedule();
        macsSchedule.setId(schedule.getId());
        macsSchedule.setScheduleName(schedule.getScheduleName());
        macsSchedule.setCategoryName(schedule.getCategoryName());
        macsSchedule.setType(Type.getType(schedule.getType()));
        
        //start policy
        MacsStartPolicy start = macsSchedule.getStartPolicy();
        start.setPolicy(StartPolicy.getPolicy(schedule.getStartPolicy()));
        //set day, month, year
        //start.setManualStartTime(schedule.getStartTime());
        start.setHolidayScheduleId(schedule.getHolidayScheduleId());
        
        //stop policy
        MacsStopPolicy stop = macsSchedule.getStopPolicy();
        stop.setPolicy(StopPolicy.getPolicy(schedule.getStopPolicy()));
        stop.setDuration(schedule.getDuration());
        // stop.setManualStopTime(manualStopTime);
        
        if (macsSchedule.isScript()) {
            MacsScriptOptions options = new MacsScriptOptions();
            options.setFileName(schedule.getScriptFileName());
            macsSchedule.setScriptOptions(options);
        } else if (macsSchedule.isSimple()) {
            MacsSimpleOptions options = new MacsSimpleOptions();
            if (!Strings.isNullOrEmpty(schedule.getStartCommand())) {
                options.setStartCommand(schedule.getStartCommand());
            }
            if (!Strings.isNullOrEmpty(schedule.getStopCommand())) {
                options.setStopCommand(schedule.getStopCommand());
            }
            options.setRepeatInterval(schedule.getRepeatInterval());
            options.setTargetPAObjectId(schedule.getTargetPAObjectId());
            macsSchedule.setSimpleOptions(options);
        }
        
        macsSchedule.setState(State.getState(schedule.getCurrentState()));
        macsSchedule.setNextStopTime(getValidDate(schedule.getNextStopTime()));
        macsSchedule.setNextRunTime(getValidDate(schedule.getNextRunTime()));
        return macsSchedule;
    }
    
    private static Date getValidDate(Date date){
        Date invalid = new Date(Schedule.INVALID_DATE);
        if (date == null || invalid.equals(date) || date.before(invalid)) {
            return null;
        }
        return date;
    }
}
