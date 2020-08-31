package com.eaton.builders.tools.trends;

import java.util.Random;

public class TrendTypes {

    public enum Type {
        BASIC_TYPE("BASIC_TYPE"),
        USAGE_TYPE("USAGE_TYPE"),
        PEAK_TYPE("PEAK_TYPE"),
        YESTERDAY_TYPE("YESTERDAY_TYPE"),
        DATE_TYPE("DATE_TYPE");

        private final String type;

        Type(String type) {
            this.type = type;
        }

        public String getType() {
            return this.type;
        }

        public static Type getRandomType() {
            Random random = new Random();
            return values()[random.nextInt(values().length)];
        }
    }
    
    public enum Color {
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

        Color(String color) {
            this.color = color;
        }

        public String getColor() {
            return this.color;
        }

        public static Color getRandomColor() {
            Random random = new Random();
            return values()[random.nextInt(values().length)];
        }
    }

    public enum Axis {
        LEFT("LEFT"),
        RIGHT("RIGHT");

        private final String axis;

        Axis(String axis) {
            this.axis = axis;
        }

        public String getAxis() {
            return this.axis;
        }
        
        public static Axis getRandomAxis() {
            Random random = new Random();
            return values()[random.nextInt(values().length)];
        }
    }

    public enum Style {
        LINE("LINE"),
        STEP("STEP"),
        BAR("BAR");

        private final String style;

        Style(String style) {
            this.style = style;
        }

        public String getStyle() {
            return this.style;
        }
        
        public static Style getRandomStyle() {
            Random random = new Random();
            return values()[random.nextInt(values().length)];
        }
    }
}
