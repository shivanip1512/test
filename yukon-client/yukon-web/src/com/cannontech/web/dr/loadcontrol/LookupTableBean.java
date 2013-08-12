package com.cannontech.web.dr.loadcontrol;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.joda.time.LocalTime;

import com.cannontech.common.util.LazyList;
import com.cannontech.dr.estimatedload.FormulaInput;
import com.cannontech.dr.estimatedload.FormulaInput.InputType;
import com.cannontech.dr.estimatedload.FormulaLookupTable;
import com.google.common.collect.ImmutableList;

public class LookupTableBean {
    private Integer lookupTableId;
    private String name;
    private Integer inputPointId;
    private FormulaInput.InputType inputType;

    private double inputMax;
    private List<TableEntryBean> entries = LazyList.ofInstance(TableEntryBean.class);

    private LocalTime timeInputMax;
    private List<TimeTableEntryBean> timeEntries = LazyList.ofInstance(TimeTableEntryBean.class);


    public LookupTableBean() {}
    public LookupTableBean(final FormulaLookupTable<?> table) {
        this.name = table.getName();
        this.inputType = table.getInput().getInputType();

        if (inputType == InputType.TIME) {
            this.timeInputMax = (LocalTime) table.getInput().getMax();
            setTimeEntries(TimeTableEntryBean.toBeanMap(table.getEntries()));
        } else {
            this.inputMax = (Double) table.getInput().getMax();
            setEntries(TableEntryBean.toBeanMap(table.getEntries()));
        }

        this.inputPointId = table.getInput().getPointId();
    }

    public int getNumberOfEntries() {
        if (inputType == InputType.TIME) {
            return timeEntries.size();
        }
        return entries.size();
    }

    public static List<LookupTableBean> toBeanMap(ImmutableList<FormulaLookupTable<Object>> tables) {
        List<LookupTableBean> beans = new ArrayList<>();
        if (tables == null) {
            return beans;
        }
        for (FormulaLookupTable<?> table : tables) {
            beans.add(new LookupTableBean(table));
        }
        return beans;
    }

    public static ImmutableList<FormulaLookupTable<Object>> toLookupTables(List<LookupTableBean> beans) {
        List<FormulaLookupTable<Object>> tables = new ArrayList<>();
        for (LookupTableBean bean : beans) {
            tables.add(bean.toFormulaLookupTable());
        }
        return ImmutableList.copyOf(tables);
    }

    public FormulaLookupTable<Object> toFormulaLookupTable() {
        if (inputType == InputType.TIME) {
            return new FormulaLookupTable<>(lookupTableId, null, name,
                    new FormulaInput<Object>(inputType, getTimeInputMin(), timeInputMax, inputPointId),
                    TimeTableEntryBean.toLookupTableMap(timeEntries));
        }
        return new FormulaLookupTable<>(lookupTableId, null, name,
                      new FormulaInput<Object>(inputType, getInputMin(), inputMax, inputPointId),
                      TableEntryBean.toLookupTableMap(entries));
    }

    public Integer getLookupTableId() {
        return lookupTableId;
    }

    public void setLookupTableId(Integer lookupTableId) {
        this.lookupTableId = lookupTableId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getInputPointId() {
        return inputPointId;
    }

    public void setInputPointId(Integer inputPointId) {
        this.inputPointId = inputPointId;
    }

    public FormulaInput.InputType getInputType() {
        return inputType;
    }

    public void setInputType(FormulaInput.InputType inputType) {
        this.inputType = inputType;
    }

    public double getInputMax() {
        return inputMax;
    }

    public void setInputMax(double inputMax) {
        this.inputMax = inputMax;
    }

    public double getInputMin() {
        if (timeEntries == null || timeEntries.isEmpty()) {
            return 0;
        }
        return 0;//Collections.min(TableEntryBean.toLookupTableMap(entries).keySet());
    }

    public List<TableEntryBean> getEntries() {
        return entries;
    }

    public void setEntries(List<TableEntryBean> entries) {
        Collections.sort(entries);
        this.entries = entries;
    }

    public LocalTime getTimeInputMax() {
        return timeInputMax;
    }

    public void setTimeInputMax(LocalTime timeInputMax) {
        this.timeInputMax = timeInputMax;
    }

    public LocalTime getTimeInputMin() {
        if (timeEntries == null || timeEntries.isEmpty()) {
            return null;
        }
        return null;//Collections.min(TimeTableEntryBean.toLookupTableMap(timeEntries).keySet());
    }

    public List<TimeTableEntryBean> getTimeEntries() {
        return timeEntries;
    }

    public void setTimeEntries(List<TimeTableEntryBean> timeEntries) {
        Collections.sort(timeEntries);
        this.timeEntries = timeEntries;
    }
}
