package com.eaton.builders.tools.trends;

import java.util.Optional;

import org.json.JSONObject;

import com.github.javafaker.Faker;

public class TrendMarkerBuilder {

    public static class Builder {

        private Faker faker = new Faker();

        private static final String TYPE = "MARKER_TYPE";
        private String color;
        private String axis;
        private final Integer pointId = -110;
        private String label;
        private Double multiplier;

        public Builder withLabel(Optional<String> label) {
            this.label = label.orElse(faker.lorem().word());

            return this;
        }

        public Builder withColor(Optional<TrendTypes.Color> color) {
            TrendTypes.Color randomColor = color.orElse(TrendTypes.Color.getRandomColor());

            this.color = randomColor.getColor();

            return this;
        }

        public Builder withAxis(Optional<TrendTypes.Axis> axis) {
            TrendTypes.Axis randomAxis = axis.orElse(TrendTypes.Axis.getRandomAxis());

            this.axis = randomAxis.getAxis();

            return this;
        }

        public Builder withMultiplier(Optional<Double> multiplier) {
            this.multiplier = multiplier.orElse(faker.number().randomDouble(1, 1, 100));

            return this;
        }

        public JSONObject build() {
            JSONObject j = new JSONObject();
            j.put("type", TYPE);
            j.put("pointId", pointId);
            j.put("label", label);
            j.put("color", color);
            j.put("axis", axis);
            j.put("multiplier", multiplier);

            return j;
        }
    }
}
