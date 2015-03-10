package com.cannontech.dr.estimatedload;

import java.beans.PropertyEditor;
import java.beans.PropertyEditorSupport;
import java.util.EnumSet;
import java.util.Set;

import org.joda.time.LocalTime;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;
import org.springframework.beans.propertyeditors.CustomNumberEditor;

import com.cannontech.common.i18n.DisplayableEnum;

public final class FormulaInput<T extends Comparable<? super T>> {

    public enum InputType implements DisplayableEnum {
        TEMP_C,
        TEMP_F,
        HUMIDITY,
        TIME_LOOKUP {
            // Use a ReadableInstant property editor when converting Time values
            @Override
            public PropertyEditorSupport makeTypeConverter() {
                return new LocalTimePropertyEditor();
            }
        },
        CONTROL_PERCENT,
        RAMP_RATE,
        POINT,
        TIME_FUNCTION;  // This input type operates on Double values, not ReadableInstant values used by lookup tables.

        @Override
        public String getFormatKey() {
            return "yukon.dr.estimatedLoad.inputType." + name();
        }

        public boolean isWeatherData() {
            return this == TEMP_F || this == HUMIDITY || this == TEMP_C;
        }
        
        public static Set<InputType> getApplianceCategoryFunctionInputs() {
            return EnumSet.of(TEMP_C, TEMP_F, HUMIDITY, TIME_FUNCTION, POINT);
        }

        public static Set<InputType> getApplianceCategoryTableInputs() {
            return EnumSet.of(TEMP_C, TEMP_F, HUMIDITY, TIME_LOOKUP, POINT);
        }

        public static Set<InputType> getGearInputs() {
            return EnumSet.of(CONTROL_PERCENT, RAMP_RATE, TIME_FUNCTION, POINT);
        }

        public PropertyEditor makeTypeConverter() {
            return new CustomNumberEditor(Double.class, false);
        }

        private static class LocalTimePropertyEditor extends PropertyEditorSupport {
            private final static DateTimeFormatter dtFormatter = ISODateTimeFormat.hourMinute();

            @Override
            public String getAsText() {
                return ((LocalTime) getValue()).toString(dtFormatter);
            }
            @Override
            public void setAsText(String text) {
                LocalTime t = LocalTime.parse(text, dtFormatter);
                setValue(t);
            }
        }
    }

    private final InputType inputType;
    private final T min;
    private final T max;
    private final Integer pointId;
    private final Integer timeOfDayInterval;

    /**
     *  Overloaded to provide a default timeOfDayInterval size of 60 minutes for code calling the original constructor.
     *  At some future time, timeOfDayInterval is going to be user-supplied and this constructor may not be needed.
     * @param inputType What kind of formula input this is: point, temperature, humidity, time, etc.
     * @param min The minimum allowable value.
     * @param max The maximum allowable value.
     * @param pointId The PointId of an input point, if the POINT InputType is used.
     */
    public FormulaInput(InputType inputType, T min, T max, Integer pointId) {
        this(inputType, min, max, pointId, 60);  // For now, the only valid interval size is 60 minutes.
    }

    /**
     * Allows a user-supplied timeOfDayInterval size to be used when creating the FormulaInput.
     * @param inputType What kind of formula input this is: point, temperature, humidity, time, etc.
     * @param min The minimum allowable value.
     * @param max The maximum allowable value.
     * @param pointId The PointId of an input point, if the POINT InputType is used.
     * @param timeOfDayInterval Interval size in minutes for time-of-day calculation, if the TIME_FUNCTION InputType is used.
     */
    public FormulaInput(InputType inputType, T min, T max, Integer pointId, Integer timeOfDayInterval) {
        this.inputType = inputType;
        this.min = min;
        this.max = max;
        this.pointId = pointId;
        this.timeOfDayInterval = timeOfDayInterval;
    }

    public InputType getInputType() {
        return inputType;
    }

    public T getMin() {
        return min;
    }

    public T getMax() {
        return max;
    }

    public Integer getPointId() {
        return pointId;
    }

    public Integer getTimeOfDayInterval() {
        return timeOfDayInterval;
    }
}
