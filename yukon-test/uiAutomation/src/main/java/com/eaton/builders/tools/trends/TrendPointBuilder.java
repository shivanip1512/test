package com.eaton.builders.tools.trends;

import java.text.SimpleDateFormat;
import java.util.Optional;

import org.json.JSONObject;

import com.github.javafaker.Faker;

public class TrendPointBuilder {

    public static class Builder {
        private Faker faker = new Faker();

        private String type;
        private String color;
        private String axis;
        private String style;
        private String date;
        private Integer pointId;
        private String label;
        private Double multiplier;

        public Builder withType(Optional<TrendTypes.Type> type) {
            TrendTypes.Type trendType = type.orElse(TrendTypes.Type.getRandomType());

            this.type = trendType.getType();
            
            return this;
        }

        public Builder withpointId(int pointId) {
            this.pointId = pointId;

            return this;
        }

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

        public Builder withStyle(Optional<TrendTypes.Style> style) {
            TrendTypes.Style randomStyle = style.orElse(TrendTypes.Style.getRandomStyle());

            this.style = randomStyle.getStyle();

            return this;
        }

        public Builder withDate(Optional<String> date) {
            if (this.type.equals(TrendTypes.Type.DATE_TYPE.getType())) {
                this.date = date.orElse(new SimpleDateFormat("MM/dd/yyyy").format(System.currentTimeMillis()));
            }

            return this;
        }

        public JSONObject build() {
            JSONObject j = new JSONObject();
            j.put("type", this.type);
            j.put("pointId", this.pointId);
            j.put("label", this.label);
            j.put("color", this.color);
            j.put("axis", this.axis);
            j.put("multiplier", this.multiplier);
            j.put("style", this.style);
            j.put("date", this.date);

            return j;
        }
    }
}
