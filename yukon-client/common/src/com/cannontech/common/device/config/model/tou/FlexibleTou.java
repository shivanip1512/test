package com.cannontech.common.device.config.model.tou;

import java.util.ArrayList;
import java.util.List;

public class FlexibleTou {

    public static final int DEFAULT_NUMBER_OF_TIMERATE = 5;

    private List<Schedule> scheduleList = new ArrayList<Schedule>();

    private TouDays days = new TouDays();

    public FlexibleTou() {
        this(DEFAULT_NUMBER_OF_TIMERATE);
    }

    public FlexibleTou(final int numberOfTimeRate) {
        for (int i = 0; i < 4; i++) {
            scheduleList.add(new Schedule(numberOfTimeRate));
        }
    }

    public List<Schedule> getScheduleList() {
        return scheduleList;
    }

    public void setScheduleList(List<Schedule> scheduleList) {
        this.scheduleList = scheduleList;
    }

    public TouDays getDays() {
        return days;
    }

    public void setDays(TouDays days) {
        this.days = days;
    }
}
