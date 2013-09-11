package com.cannontech.dr.estimatedload;

import java.util.Map;

import org.joda.time.LocalTime;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;

public final class FormulaInputHolder {
    private final ImmutableMap<Integer, FormulaInput<Double>> functionInputs;
    private final ImmutableMap<Integer, FormulaInput<Double>> tableInputs;
    private final ImmutableMap<Integer, FormulaInput<LocalTime>> timeTableInputs;

    private final ImmutableMap<Integer, Double> functionInputValues;
    private final ImmutableMap<Integer, Double> tableInputValues;
    private final ImmutableMap<Integer, LocalTime> timeTableInputValues;

    public FormulaInputHolder(ImmutableMap<Integer, FormulaInput<Double>> functionInputs, ImmutableMap<Integer, Double> functionInputValues,
            ImmutableMap<Integer, FormulaInput<Double>> tableInputs, ImmutableMap<Integer, Double> tableInputValues,
            ImmutableMap<Integer, FormulaInput<LocalTime>> timeTableInputs, ImmutableMap<Integer, LocalTime> timeTableInputValues) {
        this.functionInputs = functionInputs;
        this.functionInputValues = functionInputValues;
        this.tableInputs = tableInputs;
        this.tableInputValues = tableInputValues;
        this.timeTableInputs = timeTableInputs;
        this.timeTableInputValues = timeTableInputValues;
    }

    public FormulaInputHolder(Builder b) {
        this.functionInputs = b.functionInputs.size() == 0 ? null : ImmutableMap.copyOf(b.functionInputs);
        this.functionInputValues = b.functionInputValues.size() == 0 ? null : ImmutableMap.copyOf(b.functionInputValues);
        this.tableInputs = b.tableInputs.size() == 0 ? null : ImmutableMap.copyOf(b.tableInputs);
        this.tableInputValues = b.tableInputValues.size() == 0 ? null : ImmutableMap.copyOf(b.tableInputValues);
        this.timeTableInputs = b.timeTableInputs.size() == 0 ? null : ImmutableMap.copyOf(b.timeTableInputs);
        this.timeTableInputValues = b.timeTableInputValues.size() == 0 ? null : ImmutableMap.copyOf(b.timeTableInputValues);
    }

    public Map<Integer, Double> getFunctionInputValues() {
        return functionInputValues;
    }

    public Map<Integer, Double> getTableInputValues() {
        return tableInputValues;
    }

    public Map<Integer, LocalTime> getTimeTableInputValues() {
        return timeTableInputValues;
    }

    public Map<Integer, FormulaInput<Double>> getFunctionInputs() {
        return functionInputs;
    }

    public Map<Integer, FormulaInput<Double>> getTableInputs() {
        return tableInputs;
    }

    public Map<Integer, FormulaInput<LocalTime>> getTimeTableInputs() {
        return timeTableInputs;
    }

    public static class Builder {
        private Map<Integer, FormulaInput<Double>> functionInputs = Maps.newHashMap();
        private Map<Integer, Double> functionInputValues = Maps.newHashMap();
        private Map<Integer, FormulaInput<Double>> tableInputs = Maps.newHashMap();
        private Map<Integer, Double> tableInputValues = Maps.newHashMap();
        private Map<Integer, FormulaInput<LocalTime>> timeTableInputs = Maps.newHashMap();
        private Map<Integer, LocalTime> timeTableInputValues = Maps.newHashMap();

        public Builder() {
        }

        public void addFunctionInputValue(Integer functionId, FormulaInput<Double> input, Double value) {
            functionInputs.put(functionId, input);
            functionInputValues.put(functionId, value);
        }

        public void addTableInputValue(Integer tableId, FormulaInput<Double> input, Double value) {
            tableInputs.put(tableId, input);
            tableInputValues.put(tableId, value);
        }

        public void addTimeTableInputValue(Integer timeTableId, FormulaInput<LocalTime> input, LocalTime value) {
            timeTableInputs.put(timeTableId, input);
            timeTableInputValues.put(timeTableId, value);
        }

        public FormulaInputHolder build() {
            return new FormulaInputHolder(this);
        }
    }
}
