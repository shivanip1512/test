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
    
    public static TemperatureUnit fromAbbreviation(String abbreviation) {
        if (FAHRENHEIT.getLetter().equals(abbreviation)) {
            return FAHRENHEIT;
        } else if (CELSIUS.getLetter().equals(abbreviation)) {
            return CELSIUS;
        } else {
            throw new IllegalArgumentException("'" + abbreviation + "' is not a valid temperature abbreviation");
        }
    }

    public String getFormatKey() {
        return "yukon.common.temperatureUnit." + name();
    }

    public Object getDatabaseRepresentation() {
        return getLetter();
    }
}
