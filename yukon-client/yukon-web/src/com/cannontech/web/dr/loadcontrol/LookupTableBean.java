package com.cannontech.web.dr.loadcontrol;

import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.joda.time.LocalTime;

import com.cannontech.common.util.LazyList;
import com.cannontech.dr.estimatedload.FormulaInput;
import com.cannontech.dr.estimatedload.FormulaInput.InputType;
import com.cannontech.dr.estimatedload.FormulaLookupTable;
import com.cannontech.user.YukonUserContext;
import com.google.common.base.Function;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Ordering;

public class LookupTableBean {
    private Integer lookupTableId;
    private String name;
    private FormulaInput.InputType inputType;
    private Integer inputPointId;

    private double inputMax = 0;
    private List<TableEntryBean> entries = LazyList.ofInstance(TableEntryBean.class);

    private LocalTime timeInputMax = LocalTime.MIDNIGHT;
    private List<TimeTableEntryBean> timeEntries = LazyList.ofInstance(TimeTableEntryBean.class);

    private final static Function<LookupTableBean, String> nameFunction = new Function<LookupTableBean, String>() {
        @Override
        public String apply(LookupTableBean bean) {
            return bean.getName();
        }
    };

    public LookupTableBean() {}
    public LookupTableBean(final FormulaLookupTable<?> table) {
        this.name = table.getName();
        this.inputType = table.getInput().getInputType();

        if (inputType == InputType.TIME_LOOKUP) {
            this.timeInputMax = (LocalTime) table.getInput().getMax();
            setTimeEntries(TimeTableEntryBean.toBeanMap(table.getEntries()));
        } else {
            this.inputMax = (Double) table.getInput().getMax();
            setEntries(TableEntryBean.toBeanMap(table.getEntries()));
        }

        this.inputPointId = table.getInput().getPointId();
    }

    public int getNumberOfEntries() {
        if (inputType == InputType.TIME_LOOKUP) {
            return timeEntries.size();
        }
        return entries.size();
    }

    public static List<LookupTableBean> toBeanMap(ImmutableList<FormulaLookupTable<Double>> tables,
            ImmutableList<FormulaLookupTable<LocalTime>> timeTables, YukonUserContext userContext) {
        List<LookupTableBean> beans = new ArrayList<>();
        if (tables != null) {
            for (FormulaLookupTable<Double> table : tables) {
                beans.add(new LookupTableBean(table));
            }
        }
        if (timeTables != null) {
            for (FormulaLookupTable<LocalTime> table : timeTables) {
                beans.add(new LookupTableBean(table));
            }
        }
        Collections.sort(beans, Ordering.from(Collator.getInstance(userContext.getLocale())).onResultOf(nameFunction));
        return beans;
    }

    public static ImmutableList<FormulaLookupTable<Double>> toLookupTables(List<LookupTableBean> beans) {
        List<FormulaLookupTable<Double>> tables = new ArrayList<>();
        for (LookupTableBean bean : beans) {
            if (bean.inputType != InputType.TIME_LOOKUP) {
                tables.add(bean.toFormulaLookupTable());
            }
        }
        return ImmutableList.copyOf(tables);
    }

    public static ImmutableList<FormulaLookupTable<LocalTime>> toTimeLookupTables(List<LookupTableBean> beans) {
        List<FormulaLookupTable<LocalTime>> tables = new ArrayList<>();
        for (LookupTableBean bean : beans) {
            if (bean.inputType == InputType.TIME_LOOKUP) {
                tables.add(bean.toFormulaTimeLookupTable());
            }
        }
        return ImmutableList.copyOf(tables);
    }

    public FormulaLookupTable<Double> toFormulaLookupTable() {
        return new FormulaLookupTable<>(lookupTableId, null, name,
                      new FormulaInput<>(inputType, getInputMin(), inputMax, inputPointId),
                      TableEntryBean.toLookupTableMap(entries));
    }

    public FormulaLookupTable<LocalTime> toFormulaTimeLookupTable() {
            return new FormulaLookupTable<>(lookupTableId, null, name,
                    new FormulaInput<>(inputType, getTimeInputMin(), timeInputMax, inputPointId),
                    TimeTableEntryBean.toLookupTableMap(timeEntries));
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
        if (entries == null || entries.isEmpty()) {
            return 0;
        }
        return Collections.min(entries).getKey();
    }

    public List<TableEntryBean> getEntries() {
        return entries;
    }

    public void setEntries(List<TableEntryBean> entries) {
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
        return Collections.min(timeEntries).getKey();
    }

    public List<TimeTableEntryBean> getTimeEntries() {
        return timeEntries;
    }

    public void setTimeEntries(List<TimeTableEntryBean> timeEntries) {
        this.timeEntries = timeEntries;
    }

    public boolean isPointType() {
        return inputType == InputType.POINT;
    }

    public boolean isTimeInput() {
        return inputType == InputType.TIME_LOOKUP;
    }

    public boolean isHumidityType() {
        return inputType == InputType.HUMIDITY;
    }

    public boolean isTempType() {
        return inputType == InputType.TEMP_C
                || inputType == InputType.TEMP_F;
    }
}
