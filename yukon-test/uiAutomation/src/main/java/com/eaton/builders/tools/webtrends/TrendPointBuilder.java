package com.eaton.builders.tools.webtrends;

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

        public Builder withType(Optional<TrendTypes.PointType> type) {
            TrendTypes.PointType trendType = type.orElse(TrendTypes.PointType.getRandomType());

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

        public Builder withColor(Optional<TrendTypes.PointColor> color) {
            TrendTypes.PointColor randomColor = color.orElse(TrendTypes.PointColor.getRandomColor());

            this.color = randomColor.getColor();  
            
            return this;
        }

        public Builder withAxis(Optional<TrendTypes.PointAxis> axis) {
            TrendTypes.PointAxis randomAxis = axis.orElse(TrendTypes.PointAxis.getRandomAxis());

            this.axis = randomAxis.getAxis();

            return this;
        }

        public Builder withMultiplier(Optional<Double> multiplier) {
            this.multiplier = multiplier.orElse(faker.number().randomDouble(1, 1, 100));

            return this;
        }

        public Builder withStyle(Optional<TrendTypes.PointStyle> style) {
            TrendTypes.PointStyle randomStyle = style.orElse(TrendTypes.PointStyle.getRandomStyle());

            this.style = randomStyle.getStyle();

            return this;
        }

        public Builder withDate(Optional<String> date) {
            if (this.type == TrendTypes.PointType.DATE_TYPE.getType()) {
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
