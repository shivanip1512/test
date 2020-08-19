package com.eaton.builders.tools.webtrends;

import java.util.Random;

public class TrendTypes {

    public enum PointType {
        BASIC_TYPE("BASIC_TYPE"),
        USAGE_TYPE("USAGE_TYPE"),
        PEAK_TYPE("PEAK_TYPE"),
        YESTERDAY_TYPE("YESTERDAY_TYPE"),
        DATE_TYPE("DATE_TYPE");

        private final String type;

        PointType(String type) {
            this.type = type;
        }

        public String getType() {
            return this.type;
        }

        public static PointType getRandomType() {
            Random random = new Random();
            return values()[random.nextInt(values().length)];
        }
    }
    
    public enum PointColor {
        BLACK("BLACK"),
        BLUE("BLUE"),
        TEAL("TEAL"),
        GRAY("GRAY"),        
        GREEN("GREEN"),
        SAGE("SAGE"),
        ORANGE("ORANGE"),
        PURPLE("PURPLE"),
        WINE("WINE"),
        YELLOW("YELLOW");

        private final String color;

        PointColor(String color) {
            this.color = color;
        }

        public String getColor() {
            return this.color;
        }

        public static PointColor getRandomColor() {
            Random random = new Random();
            return values()[random.nextInt(values().length)];
        }
    }

    enum PointAxis {
        LEFT("LEFT"),
        RIGHT("RIGHT");

        private final String axis;

        PointAxis(String axis) {
            this.axis = axis;
        }

        public String getAxis() {
            return this.axis;
        }
        
        public static PointAxis getRandomAxis() {
            Random random = new Random();
            return values()[random.nextInt(values().length)];
        }
    }

    enum PointStyle {
        LINE("LINE"),
        STEP("STEP"),
        BAR("BAR");

        private final String style;

        PointStyle(String style) {
            this.style = style;
        }

        public String getStyle() {
            return this.style;
        }
        
        public static PointStyle getRandomStyle() {
            Random random = new Random();
            return values()[random.nextInt(values().length)];
        }
    }
}
