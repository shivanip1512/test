package com.eaton.builders.tools.webtrends;

public class WebTrendEnums {

    public enum Type {
        BASIC_TYPE("BASIC_TYPE"),
        USAGE_TYPE("USAGE_TYPE"),
        PEAK_TYPE("PEAK_TYPE"),
        YESTERDAY_TYPE("YESTERDAY_TYPE"),
        MARKER_TYPE("MARKER_TYPE"),
        DATE_TYPE("DATE_TYPE");

        private final String type;

        Type(String type) {
            this.type = type;
        }

        public String getType() {
            return this.type;
        }

    }

    public enum Color {
        BLACK("BLACK"),
        BLUE("BLUE"),
        CYAN("CYAN"),
        GREY("GREY"),
        GREEN("GREEN"),
        MAGENTA("MAGENTA"),
        ORANGE("ORANGE"),
        PINK("PINK"),
        RED("RED"),
        YELLOW("YELLOW");

        private final String color;

        Color(String color) {
            this.color = color;
        }

        public String getColor() {
            return this.color;
        }

    }

    enum Axis {
        LEFT("LEFT"),
        RIGHT("RIGHT");

        private final String axis;

        Axis(String axis) {
            this.axis = axis;
        }

        public String getAxis() {
            return this.axis;
        }
    }

    enum Style {
        LINE("LINE"),
        STEP("LINE"),
        BAR("LINE");

        private final String style;

        Style(String style) {
            this.style = style;
        }

        public String getStyle() {
            return this.style;
        }
    }
}
