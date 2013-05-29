package com.cannontech.common.temperature;

import com.cannontech.common.i18n.DisplayableEnum;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.common.util.DatabaseRepresentationSource;
import com.cannontech.common.util.xml.XmlRepresentation;


public enum TemperatureUnit implements DatabaseRepresentationSource, DisplayableEnum {

    @XmlRepresentation("F")
    FAHRENHEIT("F"),
    @XmlRepresentation("C")
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
    
    public static TemperatureUnit fromAbbreviation(String letter) {
        if (CtiUtilities.FAHRENHEIT_CHARACTER.equals(letter)) {
            return FAHRENHEIT;
        } else if (CtiUtilities.CELSIUS_CHARACTER.equals(letter)) {
            return CELSIUS;
        } else {
            throw new IllegalArgumentException("'" + letter + "' is not a valid temperature abbreviation");
        }
    }

    public String getFormatKey() {
        return "yukon.web.modules.consumer.thermostat." + this.name();
    }

    public Object getDatabaseRepresentation() {
        return this.name();
    }
}
