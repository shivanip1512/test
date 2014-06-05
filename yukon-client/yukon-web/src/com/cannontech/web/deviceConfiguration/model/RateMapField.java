package com.cannontech.web.deviceConfiguration.model;

import java.util.List;

import com.cannontech.web.input.type.InputType;

public class RateMapField extends Field<RateInput> {
    private List<DisplayableRate> inputTypes;

    public static final class DisplayableRate {
        private final InputType<?> timeType;
        private final String field;
        private final InputType<?> rateType;

        public DisplayableRate(InputType<?> timeType, InputType<?> rateType, String field) {
            this.timeType = timeType;
            this.rateType = rateType;
            this.field = field;
        }

        public InputType<?> getRateType() {
            return rateType;
        }

        public InputType<?> getTimeType() {
            return timeType;
        }

        public String getField() {
            return field;
        }
    }

    public RateMapField(String displayName, String fieldName, String description, InputType<RateInput> rateInput, List<DisplayableRate> inputTypes) {
        super(displayName, fieldName, description, rateInput, "", null);
        this.inputTypes = inputTypes;
    }

    public List<DisplayableRate> getInputTypes() {
        return inputTypes;
    }
}
