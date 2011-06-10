package com.cannontech.common.temperature;



/**
 * A Temperature representation based on the Fahrenheit scale. Can represent values
 * corresponding to the range of a double.
 */
public final class FahrenheitTemperature extends DoubleTemperatureBase {

    /**
     * Constructs a FahrenheitTemperature for the given temperature value in Fahrenheit.
     * @param temperature
     */
    public FahrenheitTemperature(double temperature) {
        super(temperature, TemperatureUnit.FAHRENHEIT);
    }

    @Override
    public FahrenheitTemperature toFahrenheit() {
        return this;
    }

    @Override
    public CelsiusTemperature toCelsius() {
        double celsius = (getValue() - 32) * 5 / 9;
        return new CelsiusTemperature(celsius);
    }
    
    

}
