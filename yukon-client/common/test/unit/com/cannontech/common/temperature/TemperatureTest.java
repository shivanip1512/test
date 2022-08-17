package com.cannontech.common.temperature;

import static org.junit.Assert.*;

import java.math.BigDecimal;

import org.junit.Test;

public class TemperatureTest {

    private static final double CONVERSION_DELTA = .0000001;

    @Test
    public void test_F_to_F() {
        double valueF = Temperature.from(100.0, TemperatureUnit.FAHRENHEIT).toFahrenheit().getValue();
        assertEquals(100.0, valueF, CONVERSION_DELTA);
    }

    @Test
    public void test_C_to_F() {
        double valueF = Temperature.from(100.0, TemperatureUnit.CELSIUS).toFahrenheit().getValue();
        assertEquals(212.0, valueF, CONVERSION_DELTA);
    }
    
    @Test
    public void test_F_to_C() {
        double valueC = Temperature.from(100.0, TemperatureUnit.FAHRENHEIT).toCelsius().getValue();
        assertEquals(37.7777777777777777777, valueC, CONVERSION_DELTA);
    }
    
    @Test
    public void test_C_to_C() {
        double valueC = Temperature.from(100.0, TemperatureUnit.CELSIUS).toCelsius().getValue();
        assertEquals(100.0, valueC, CONVERSION_DELTA);
    }
    
    @Test
    public void test_toBigDecimal_C() {
        BigDecimal bigDecimal = Temperature.from(100.0, TemperatureUnit.FAHRENHEIT).toCelsius().toBigDecimal(2);
        assertEquals("37.78", bigDecimal.toPlainString());
    }
    
    @Test
    public void test_toBigDecimal_F() {
        BigDecimal bigDecimal = Temperature.from(49.1, TemperatureUnit.CELSIUS).toFahrenheit().toBigDecimal(2);
        assertEquals("120.38", bigDecimal.toPlainString());
    }
    
    @Test
    public void test_toString_F() {
        String result = Temperature.fromFahrenheit(56.78).toString();
        assertEquals("56.8\u00B0F", result);
    }
    
    @Test
    public void test_toString_C() {
        String result = Temperature.fromCelsius(56.78).toString();
        assertEquals("56.8\u00B0C", result);
    }
    
    @Test
    public void test_compare_F_F() {
        assertTrue(Temperature.fromFahrenheit(31.2).compareTo(Temperature.fromFahrenheit(32)) < 0);
        assertTrue(Temperature.fromFahrenheit(32).compareTo(Temperature.fromFahrenheit(32)) == 0);
        assertTrue(Temperature.fromFahrenheit(32).compareTo(Temperature.fromFahrenheit(31.8)) > 0);
    }
    
    @Test
    public void test_compare_C_C() {
        assertTrue(Temperature.fromCelsius(31.2).compareTo(Temperature.fromCelsius(32)) < 0);
        assertTrue(Temperature.fromCelsius(32).compareTo(Temperature.fromCelsius(32)) == 0);
        assertTrue(Temperature.fromCelsius(32).compareTo(Temperature.fromCelsius(31.8)) > 0);
    }
    
    @Test
    public void test_compare_C_F() {
        assertTrue(Temperature.fromCelsius(-1).compareTo(Temperature.fromFahrenheit(32)) < 0);
        assertTrue(Temperature.fromCelsius(0).compareTo(Temperature.fromFahrenheit(32)) == 0);
        assertTrue(Temperature.fromCelsius(0).compareTo(Temperature.fromFahrenheit(31.8)) > 0);
    }
    
    @Test
    public void test_compare_F_C() {
        assertTrue(Temperature.fromFahrenheit(31.2).compareTo(Temperature.fromCelsius(0)) < 0);
        assertTrue(Temperature.fromFahrenheit(32).compareTo(Temperature.fromCelsius(0)) == 0);
        assertTrue(Temperature.fromFahrenheit(32).compareTo(Temperature.fromCelsius(-1)) > 0);
    }
    
}
