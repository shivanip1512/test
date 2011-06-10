package com.cannontech.common.temperature;

public enum TemperatureUnit {
    FAHRENHEIT("F"),
    CELSIUS("C"),
    ;
    
    private final String letter;

    private TemperatureUnit(String letter) {
        this.letter = letter;
    }
    
    public String getLetter() {
        return letter;
    }
    
    public String getSuffix() {
        return "\u00B0" + letter;
    }
}
