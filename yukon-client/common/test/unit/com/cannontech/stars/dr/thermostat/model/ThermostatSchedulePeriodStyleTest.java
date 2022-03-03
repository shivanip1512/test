package com.cannontech.stars.dr.thermostat.model;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import org.junit.jupiter.api.Test;

public class ThermostatSchedulePeriodStyleTest {
    
    @Test
    public void testThermostatSchedulePeriodStyle(){
        ThermostatSchedulePeriod period0;
        ThermostatSchedulePeriod period1;
        ThermostatSchedulePeriod period2;
        ThermostatSchedulePeriod period3;
        
        //Test "real" periods in two-time style
        List<ThermostatSchedulePeriod> realPeriodList = ThermostatSchedulePeriodStyle.TWO_TIMES.getRealPeriods();
        assertEquals(2, realPeriodList.size());
        
        period0 = realPeriodList.get(0);
        assertEquals(ThermostatSchedulePeriod.OCCUPIED, period0);
        
        period1 = realPeriodList.get(1);
        assertEquals(ThermostatSchedulePeriod.UNOCCUPIED, period1);
        
        //Test all (real and pseudo) periods in two-time style
        List<ThermostatSchedulePeriod> allPeriodList = ThermostatSchedulePeriodStyle.TWO_TIMES.getAllPeriods();
        assertEquals(4, allPeriodList.size());
        
        period0 = allPeriodList.get(0);
        assertEquals(ThermostatSchedulePeriod.FAKE_1, period0);
        
        period1 = allPeriodList.get(1);
        assertEquals(ThermostatSchedulePeriod.FAKE_2, period1);
        
        period2 = allPeriodList.get(2);
        assertEquals(ThermostatSchedulePeriod.OCCUPIED, period2);
        
        period3 = allPeriodList.get(3);
        assertEquals(ThermostatSchedulePeriod.UNOCCUPIED, period3);
        
        //Test "real" periods in four-time style
        realPeriodList = ThermostatSchedulePeriodStyle.FOUR_TIMES.getRealPeriods();
        assertEquals(4, realPeriodList.size());
        
        period0 = realPeriodList.get(0);
        assertEquals(ThermostatSchedulePeriod.WAKE, period0);
        
        period1 = realPeriodList.get(1);
        assertEquals(ThermostatSchedulePeriod.LEAVE, period1);
        
        period2 = realPeriodList.get(2);
        assertEquals(ThermostatSchedulePeriod.RETURN, period2);
        
        period3 = realPeriodList.get(3);
        assertEquals(ThermostatSchedulePeriod.SLEEP, period3);
        
        //Test all (real and pseudo) periods in four-time style
        allPeriodList = ThermostatSchedulePeriodStyle.FOUR_TIMES.getAllPeriods();
        assertEquals(4, allPeriodList.size());
        
        period0 = allPeriodList.get(0);
        assertEquals(ThermostatSchedulePeriod.WAKE, period0);
        
        period1 = allPeriodList.get(1);
        assertEquals(ThermostatSchedulePeriod.LEAVE, period1);
        
        period2 = allPeriodList.get(2);
        assertEquals(ThermostatSchedulePeriod.RETURN, period2);
        
        period3 = allPeriodList.get(3);
        assertEquals(ThermostatSchedulePeriod.SLEEP, period3);
    }
    
}
