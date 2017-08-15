package com.cannontech.amr.macsscheduler.model;

import java.util.Date;

import org.joda.time.Minutes;
import org.joda.time.Seconds;

import com.cannontech.amr.macsscheduler.model.MacsSchedule.State;
import com.cannontech.amr.macsscheduler.model.MacsStartPolicy.StartPolicy;
import com.cannontech.amr.macsscheduler.model.MacsStopPolicy.StopPolicy;
import com.cannontech.common.pao.PaoType;
import com.cannontech.message.macs.message.Schedule;
import com.google.common.base.Strings;

public class MacsScheduleHelper {
    
    public static Schedule convert(MacsSchedule macsSchedule){
        Schedule schedule = new Schedule();
        schedule.setId(macsSchedule.getId());
        schedule.setScheduleName(macsSchedule.getScheduleName());
        schedule.setCategoryName(macsSchedule.getCategoryName());
        schedule.setType(macsSchedule.getType().getDbString());
        schedule.setTemplateType(macsSchedule.getTemplate().getId());
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
        schedule.setStartTime(start.getStartTime());

        schedule.setHolidayScheduleId(start.getHolidayScheduleId());
   
        //stop policy
        MacsStopPolicy stop = macsSchedule.getStopPolicy();
        schedule.setDuration(getSeconds(stop.getDuration()));
        schedule.setStopPolicy(stop.getPolicy().getPolicyString());
        schedule.setStopTime(stop.getStopTime());
        
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
            schedule.setRepeatInterval(getSeconds(options.getRepeatInterval()));
            schedule.setTargetPAObjectId(options.getTargetPAObjectId());
        }
        return schedule;
    }
    
    public static MacsSchedule convert(Schedule schedule){
        MacsSchedule macsSchedule  = new MacsSchedule();
        macsSchedule.setId(schedule.getId());
        macsSchedule.setScheduleName(schedule.getScheduleName());
        macsSchedule.setCategoryName(schedule.getCategoryName());
        macsSchedule.setType(PaoType.getForDbString(schedule.getType()));
        macsSchedule.setTemplate(MacsScriptTemplate.getTemplate(schedule.getTemplateType()));
        
        //start policy
        MacsStartPolicy start = macsSchedule.getStartPolicy();
        start.setPolicy(StartPolicy.getPolicy(schedule.getStartPolicy()));
        start.setWeekDays(schedule.getValidWeekDays());
        start.buildStartTimeDate(schedule.getStartMonth(), schedule.getStartDay(), schedule.getStartYear(),
            schedule.getStartTime());
      
        start.setHolidayScheduleId(schedule.getHolidayScheduleId());
        
        //stop policy
        MacsStopPolicy stop = macsSchedule.getStopPolicy();
        stop.setPolicy(StopPolicy.getPolicy(schedule.getStopPolicy()));
        stop.setDuration(getMinutes(stop.getDuration()));
        stop.buildStopTimeDate(schedule.getStopTime());
        
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
            options.setRepeatInterval(getMinutes(schedule.getRepeatInterval()));
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
    
    private static int getMinutes(int seconds){
        return Seconds.seconds(seconds).toStandardMinutes().getMinutes();
    }
    
    private static int getSeconds(int minutes) {
        return Minutes.minutes(minutes).toStandardSeconds().getSeconds();
    }
}
