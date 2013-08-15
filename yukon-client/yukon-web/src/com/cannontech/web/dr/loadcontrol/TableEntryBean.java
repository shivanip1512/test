package com.cannontech.web.dr.loadcontrol;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.google.common.collect.ImmutableMap;

public class TableEntryBean implements Comparable<TableEntryBean> {
    
    private double key;
    private double value;

    public TableEntryBean() {}
    public TableEntryBean(double key, double value) {
        this.key = key;
        this.value = value;
    }

    public double getKey() {
        return key;
    }

    public void setKey(double key) {
        this.key = key;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    public static List<TableEntryBean> toBeanMap(ImmutableMap<?, Double> entries) {
        List<TableEntryBean> beans = new ArrayList<>();
        for (Entry<?, Double> k : entries.entrySet()) {
            beans.add(new TableEntryBean((Double)k.getKey(), k.getValue()));
        }
        Collections.sort(beans);
        return beans;
    }

    public static ImmutableMap<Double, Double> toLookupTableMap(List<TableEntryBean> beans) {
        Map<Double, Double> lookupMap = new HashMap<>();
        for (TableEntryBean bean : beans) {
            lookupMap.put(bean.getKey(), bean.getValue());
        }
        return ImmutableMap.copyOf(lookupMap);
    }

    @Override
    public int compareTo(TableEntryBean otherBean) {
        return Double.compare(key, otherBean.getKey());
    }
}
