package com.cannontech.common.temperature;


/**
 * A Temperature representation based on the Celsius scale. Can represent values
 * corresponding to the range of a double.
 */
public final class CelsiusTemperature extends DoubleTemperatureBase {

    /**
     * Constructs a CelsiusTemperature for the given temperature value in Celsius.
     * @param temperature
     */
    public CelsiusTemperature(double temperature) {
        super(temperature, TemperatureUnit.CELSIUS);
    }

    @Override
    public FahrenheitTemperature toFahrenheit() {
        double fahrenheit = getValue() * 9 / 5 + 32;
        return new FahrenheitTemperature(fahrenheit);
    }

    @Override
    public CelsiusTemperature toCelsius() {
        return this;
    }
    
}
