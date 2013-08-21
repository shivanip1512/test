package com.cannontech.messaging.serialization.thrift.test.messagevalidator.mac;

import java.util.Date;

import com.cannontech.message.macs.message.Schedule;
import com.cannontech.messaging.serialization.thrift.test.validator.AutoInitializedClassValidator;
import com.cannontech.messaging.serialization.thrift.test.validator.RandomGenerator;

public class ScheduleValidator extends AutoInitializedClassValidator<Schedule> {
    private static long DEFAULT_SEED = 100;

    private static final String[] scheduleTypeItems = { Schedule.SIMPLE_TYPE, Schedule.SCRIPT_TYPE };
    private static final String[] currentStateItems = { Schedule.STATE_WAITING, Schedule.STATE_PENDING,
                                                       Schedule.STATE_RUNNING, Schedule.STATE_DISABLED };
    private static final String[] startPolicyItems = { Schedule.DATETIME_START, Schedule.DAYOFMONTH_START,
                                                      Schedule.WEEKDAY_START, Schedule.MANUAL_START };
    private static final String[] stopPolicyItems = { Schedule.ABSOLUTETIME_STOP, Schedule.DURATION_STOP,
                                                     Schedule.UNTILCOMPLETE_STOP, Schedule.MANUAL_STOP };
    private static final String[] lastRunStatusItems = { Schedule.LAST_STATUS_NONE, Schedule.LAST_STATUS_ERROR,
                                                        Schedule.LAST_STATUS_FINISHED };

    public ScheduleValidator() {
        super(Schedule.class, DEFAULT_SEED);
    }

    @Override
    public void populateExpectedValue(Schedule ctrlObj, RandomGenerator generator) {

        ctrlObj.setId(generator.generateInt());
        ctrlObj.setScheduleName(generator.generateString());
        ctrlObj.setCategoryName(generator.generateString());
        ctrlObj.setType(generator.generateChoice(scheduleTypeItems));
        ctrlObj.setHolidayScheduleId(generator.generateInt());
        ctrlObj.setScriptFileName(generator.generateString());
        ctrlObj.setCurrentState(generator.generateChoice(currentStateItems));
        ctrlObj.setStartPolicy(generator.generateChoice(startPolicyItems));
        ctrlObj.setStopPolicy(generator.generateChoice(stopPolicyItems));
        ctrlObj.setLastRunTime(generator.generateDate());
        ctrlObj.setLastRunStatus(generator.generateChoice(lastRunStatusItems));
        ctrlObj.setStartDay(generator.generateInt(0, 31));
        ctrlObj.setStartMonth(generator.generateInt());
        ctrlObj.setStartYear(generator.generateInt());
        ctrlObj.setStartTime(generator.generateString(8));
        ctrlObj.setStopTime(generator.generateString(8));
        ctrlObj.setValidWeekDays(generator.generateString(8));
        ctrlObj.setDuration(generator.generateInt());
        ctrlObj.setTargetPAObjectId(generator.generateInt());
        ctrlObj.setStartCommand(generator.generateString());
        ctrlObj.setStopCommand(generator.generateString());
        ctrlObj.setRepeatInterval(generator.generateInt());
        ctrlObj.setNextRunTime(generator.generateDate());
        ctrlObj.setNextStopTime(generator.generateDate());
        ctrlObj.setTemplateType(generator.generateInt());

        ctrlObj.setManualStartTime(new Date(0));
        ctrlObj.setManualStopTime(new Date(0));

        ignoreField("nonPersistantData");
    }
}
