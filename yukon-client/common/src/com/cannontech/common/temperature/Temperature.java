package com.cannontech.common.temperature;


/**
 * An abstract representation of temperature.
 */
public abstract class Temperature implements Comparable<Temperature> {
    
    /**
     * Create a Temperature object from a long for a given unit.
     * @param temperature
     * @param unit
     * @return
     */
    public static Temperature from(long temperature, TemperatureUnit unit) {
        return from((double)temperature, unit);
    }

    /**
     * Create a Temperature object from a long representation of a Fahrenheit temperature.
     * @param temperatureFahrenheit
     * @return
     */
    public static Temperature fromFahrenheit(long temperatureFahrenheit) {
        return from(temperatureFahrenheit, TemperatureUnit.FAHRENHEIT);
    }
    
    /**
     * Create a Temperature object from a long representation of Celsius temperature. 
     * @param temperatureCelsius
     * @return
     */
    public static Temperature fromCelsius(long temperatureCelsius) {
        return from(temperatureCelsius, TemperatureUnit.CELSIUS);
    }
    
    /**
     * Create a Temperature object form a double representation of a Fahrenheit temperature.
     * @param temperatureFahrenheit
     * @return
     */
    public static Temperature fromFahrenheit(double temperatureFahrenheit) {
        return from(temperatureFahrenheit, TemperatureUnit.FAHRENHEIT);
    }
    
    /**
     * Create a Temperature object from a double representation of a Celsius temperature.
     * @param temperatureCelsius
     * @return
     */
    public static Temperature fromCelsius(double temperatureCelsius) {
        return from(temperatureCelsius, TemperatureUnit.CELSIUS);
    }
    
    /**
     * Create a Temperature object from a double for a given unit.
     * @param temperature
     * @param unit
     * @return
     */
    public static Temperature from(double temperature, TemperatureUnit unit) {
        switch (unit) {
        case FAHRENHEIT:
            return new FahrenheitTemperature(temperature);
        case CELSIUS:
            return new CelsiusTemperature(temperature);
        }
        throw new IllegalArgumentException("temperature unit isn't supported: " + unit);
    }

    /**
     * Produces a FahrenheitTemperature object which can be queried for the temperature value
     * in Fahrenheit.
     * @return
     */
    public abstract FahrenheitTemperature toFahrenheit();
    
    /**
     * Produces a CelsiusTemperature object which can be queried for the temperature value
     * in Celsius.
     * @return
     */
    public abstract CelsiusTemperature toCelsius();
    
    @Override
    public int compareTo(Temperature o) {
        double leftF = this.toFahrenheit().getValue();
        double rightF = o.toFahrenheit().getValue();
        return Double.compare(leftF, rightF);
    }
    
}
