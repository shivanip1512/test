package com.cannontech.dr.estimatedload;

import java.beans.PropertyEditor;
import java.beans.PropertyEditorSupport;

import org.joda.time.LocalTime;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;
import org.springframework.beans.propertyeditors.CustomNumberEditor;

import com.cannontech.common.i18n.DisplayableEnum;

public final class FormulaInput<T> {

    public enum InputType implements DisplayableEnum {
        TEMP,
        HUMIDITY,
        TIME {
            // Use a ReadableInstant property editor when converting Time values
            @Override
            public PropertyEditorSupport makeTypeConverter() {
                return new LocalTimePropertyEditor();
            };
        },
        CONTROL_PERCENT,
        RAMP_RATE,
        POINT;

        @Override
        public String getFormatKey() {
            return "yukon.dr.estimatedLoad.inputType." + name();
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
        };
    }

    private final InputType inputType;
    private final T min;
    private final T max;
    private final Integer pointId;
    

    public FormulaInput(InputType inputType, T min, T max, Integer pointId) {
        this.inputType = inputType;
        this.min = min;
        this.max = max;
        this.pointId = pointId;
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
    
    public FormulaInput<Double> castAsDouble() {
        return new FormulaInput<Double>(inputType, (Double) min, (Double) max, pointId);
    }
    
    public FormulaInput<LocalTime> castAsLocalTime() {
        return new FormulaInput<LocalTime>(inputType, (LocalTime) min, (LocalTime) max, pointId);
    }
}
