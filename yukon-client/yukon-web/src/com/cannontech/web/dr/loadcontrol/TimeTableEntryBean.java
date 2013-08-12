package com.cannontech.web.dr.loadcontrol;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.joda.time.LocalTime;

import com.google.common.collect.ImmutableMap;

public class TimeTableEntryBean implements Comparable<TimeTableEntryBean>{
    private LocalTime key = LocalTime.MIDNIGHT;
    private double value;

    public TimeTableEntryBean() {}
    public TimeTableEntryBean(LocalTime key, double value) {
        this.key = key;
        this.value = value;
    }

    public LocalTime getKey() {
        return key;
    }

    public void setKey(LocalTime key) {
        this.key = key;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    public static List<TimeTableEntryBean> toBeanMap(ImmutableMap<?, Double> entries) {
        List<TimeTableEntryBean> beans = new ArrayList<>();
        for (Entry<?, Double> k : entries.entrySet()) {
            beans.add(new TimeTableEntryBean((LocalTime)k.getKey(), k.getValue()));
        }
        return beans;
    }

    public static ImmutableMap<Object, Double> toLookupTableMap(List<TimeTableEntryBean> beans) {
        Map<Object, Double> lookupMap = new HashMap<>();
        for (TimeTableEntryBean bean : beans) {
            lookupMap.put(bean.getKey(), bean.getValue());
        }
        return ImmutableMap.copyOf(lookupMap);
    }

    @Override
    public int compareTo(TimeTableEntryBean otherBean) {
        return key.compareTo(otherBean.getKey());
    }
}
