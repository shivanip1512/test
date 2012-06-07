package com.cannontech.stars.dr.thermostat.model;

/**
 * Data transfer object for thermostat schedule
 */
public class ScheduleDropDownItem {

    private Integer id;
    private String name;

    public ScheduleDropDownItem(Integer id, String name) {
        super();
        this.id = id;
        this.name = name;
    }

    public Integer getId() {
        return id;
    }

    public String getName() {
        return name;
    }

}
