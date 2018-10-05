package com.cannontech.web.ami.trend;

import static org.junit.Assert.assertTrue;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.springframework.test.util.ReflectionTestUtils;

import com.cannontech.common.chart.model.ChartInterval;
import com.cannontech.common.chart.model.ChartValue;
import com.cannontech.web.common.chart.service.impl.ChartServiceImpl;

public class ChartServiceImplTest {
    
    private ChartServiceImpl chartServiceImpl;
    @Before
    public void setUp() throws NoSuchMethodException, SecurityException {
        chartServiceImpl = new ChartServiceImpl();
    }
    
    @Test
    public void test_isValueRepeated_true() {
        DateTime dt = new DateTime(2018, 8, 5, 7, 10, 5, 0);
        ChartValue<Double> oldValue = buildChartValue(dt.getMillis(), 12.32, dt.getMillis());
        ChartValue<Double> currentValue = buildChartValue(dt.plusHours(1).getMillis(), 12.32, dt.plusHours(1).getMillis());
        boolean result = ReflectionTestUtils.invokeMethod(chartServiceImpl, "isValueRepeated", oldValue, currentValue);
        assertTrue(result);
    }
    
    @Test
    public void test_isValueRepeated_false() {
        DateTime dt = new DateTime(2018, 8, 5, 7, 10, 5, 0);
        ChartValue<Double> oldValue = buildChartValue(dt.getMillis(), 12.32, dt.getMillis());
        ChartValue<Double> currentValue = buildChartValue(dt.plusHours(1).getMillis(), 22.32, dt.plusHours(1).getMillis());
        boolean result = ReflectionTestUtils.invokeMethod(chartServiceImpl, "isValueRepeated", oldValue, currentValue);
        assertTrue(!result);
    }
    
    @Test
    public void test_addMinMaxValuesinOrder_minRequied_1() {
        List<ChartValue<Double>> list = new ArrayList<>();
        DateTime dt = new DateTime(2018, 8, 5, 7, 10, 5, 0);
        ChartValue<Double> minValue = buildChartValue(dt.getMillis(), 32.32, dt.getMillis());
        ChartValue<Double> maxValue= buildChartValue(dt.plusHours(1).getMillis(), 82.32, dt.plusHours(1).getMillis());
        List<ChartValue<Double>> result = ReflectionTestUtils.invokeMethod(chartServiceImpl,"addMinMaxValuesinOrder",list, minValue, maxValue, true);
        // min value is coming first.
        assertTrue(result.get(0).getValue() == 32.32 && result.get(1).getValue() == 82.32);
    }
    
    @Test
    public void test_addMinMaxValuesinOrder_minRequied_2() {
        List<ChartValue<Double>> list = new ArrayList<>();
        DateTime dt = new DateTime(2018, 8, 5, 7, 10, 5, 0);
        ChartValue<Double> minValue = buildChartValue(dt.plusHours(2).getMillis(), 32.32, dt.plusHours(2).getMillis());
        ChartValue<Double> maxValue= buildChartValue(dt.plusHours(1).getMillis(), 82.32, dt.plusHours(1).getMillis());
        List<ChartValue<Double>> result = ReflectionTestUtils.invokeMethod(chartServiceImpl,"addMinMaxValuesinOrder",list, minValue, maxValue, true);
        // min value is required and max value is coming first hence it will be added first.
        assertTrue(result.get(0).getValue() == 82.32 && result.get(1).getValue() == 32.32);
    }
    
    @Test
    public void test_addMinMaxValuesinOrder_minNotRequied() {
        List<ChartValue<Double>> list = new ArrayList<>();
        DateTime dt = new DateTime(2018, 8, 5, 7, 10, 5, 0);
        ChartValue<Double> minValue = buildChartValue(dt.getMillis(), 32.32, dt.getMillis());
        ChartValue<Double> maxValue= buildChartValue(dt.plusHours(1).getMillis(), 82.32, dt.plusHours(1).getMillis());
        List<ChartValue<Double>> result = ReflectionTestUtils.invokeMethod(chartServiceImpl,"addMinMaxValuesinOrder",list, minValue, maxValue, false);
        // min value is not required hence only max value will be returned
        assertTrue(result.get(0).getValue() == 82.32);
    }

    @Test
    public void test_getXAxisMinMaxValues_FIVEMINUTE_max() throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        List<ChartValue<Double>> list = new ArrayList<>();
        DateTime dt = new DateTime(2018, 8, 5, 7, 11, 0, 0);
        // 1st 5 minutes (7:11 -> 7:14)
        list.add(buildChartValue(dt.plusMinutes(1).getMillis(),42.32,dt.plusMinutes(1).getMillis()));
        list.add(buildChartValue(dt.plusMinutes(2).getMillis(),162.32,dt.plusMinutes(2).getMillis()));
        list.add(buildChartValue(dt.plusMinutes(3).getMillis(),128.32,dt.plusMinutes(3).getMillis()));
        //2nd 5 minutes (7:16 -> 7:19)
        list.add(buildChartValue(dt.plusMinutes(5).getMillis(),123.32,dt.plusMinutes(6).getMillis()));
        list.add(buildChartValue(dt.plusMinutes(6).getMillis(),362.32,dt.plusMinutes(6).getMillis()));
        list.add(buildChartValue(dt.plusMinutes(7).getMillis(),265.32,dt.plusMinutes(6).getMillis()));
        
        List<ChartValue<Double>> result = ReflectionTestUtils.invokeMethod(chartServiceImpl,"getXAxisMinMaxValues",ChartInterval.FIVEMINUTE, list, false);
        // This will get max values for each five minute interval
        assertTrue(result.get(0).getValue() == 162.32 && result.get(1).getValue() == 362.32);
    }

    @Test
    public void test_getXAxisMinMaxValues_MINUTE_min_max() throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        List<ChartValue<Double>> list = new ArrayList<>();
        DateTime dt = new DateTime(2018, 8, 5, 7, 11, 0, 0);
        // 1st 5 minutes (7:11 -> 7:14)
        list.add(buildChartValue(dt.plusMinutes(1).getMillis(),42.32,dt.plusMinutes(1).getMillis()));
        list.add(buildChartValue(dt.plusMinutes(2).getMillis(),162.32,dt.plusMinutes(2).getMillis()));
        list.add(buildChartValue(dt.plusMinutes(3).getMillis(),128.32,dt.plusMinutes(3).getMillis()));
        //2nd 5 minutes (7:16 -> 7:19)
        list.add(buildChartValue(dt.plusMinutes(5).getMillis(),123.32,dt.plusMinutes(6).getMillis()));
        list.add(buildChartValue(dt.plusMinutes(6).getMillis(),362.32,dt.plusMinutes(6).getMillis()));
        list.add(buildChartValue(dt.plusMinutes(7).getMillis(),265.32,dt.plusMinutes(6).getMillis()));
        // This will get min-max values for each five minute
        List<ChartValue<Double>> result = ReflectionTestUtils.invokeMethod(chartServiceImpl,"getXAxisMinMaxValues",ChartInterval.FIVEMINUTE, list, true);
        // 1st 5 minutes [min-max]
        assertTrue(result.get(0).getValue() == 42.32 && result.get(1).getValue() == 162.32);
        // 2nd 5 minutes [min-max]
        assertTrue(result.get(2).getValue() == 123.32 && result.get(3).getValue() == 362.32);
    }

    @Test
    public void test_getXAxisMinMaxValues_FIFTEENMINUTE_max() throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        List<ChartValue<Double>> list = new ArrayList<>();
        DateTime dt = new DateTime(2018, 8, 5, 7, 0, 0, 0);
        // 1st 15 minutes (7:00 -> 7:14)
        list.add(buildChartValue(dt.plusMinutes(1).getMillis(),42.32,dt.plusMinutes(1).getMillis()));
        list.add(buildChartValue(dt.plusMinutes(6).getMillis(),162.32,dt.plusMinutes(2).getMillis()));
        list.add(buildChartValue(dt.plusMinutes(12).getMillis(),128.32,dt.plusMinutes(3).getMillis()));
        //2nd 15 minutes (7:16 -> 7:27)
        list.add(buildChartValue(dt.plusMinutes(16).getMillis(),123.32,dt.plusMinutes(6).getMillis()));
        list.add(buildChartValue(dt.plusMinutes(22).getMillis(),362.32,dt.plusMinutes(6).getMillis()));
        list.add(buildChartValue(dt.plusMinutes(27).getMillis(),265.32,dt.plusMinutes(6).getMillis()));
        
        List<ChartValue<Double>> result = ReflectionTestUtils.invokeMethod(chartServiceImpl,"getXAxisMinMaxValues",ChartInterval.FIFTEENMINUTE, list, false);
        // This will max values for each fifteen minute interval
        assertTrue(result.get(0).getValue() == 162.32 && result.get(1).getValue() == 362.32);
    }

    @Test
    public void test_getXAxisMinMaxValues_FIFTEENMINUTE_min_max() throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        List<ChartValue<Double>> list = new ArrayList<>();
        DateTime dt = new DateTime(2018, 8, 5, 7, 11, 0, 0);
        // 1st 15 minutes (7:11 -> 7:14)
        list.add(buildChartValue(dt.plusMinutes(1).getMillis(),42.32,dt.plusMinutes(1).getMillis()));
        list.add(buildChartValue(dt.plusMinutes(2).getMillis(),162.32,dt.plusMinutes(2).getMillis()));
        list.add(buildChartValue(dt.plusMinutes(3).getMillis(),128.32,dt.plusMinutes(3).getMillis()));
        //2nd 15 minutes (7:16 -> 7:19)
        list.add(buildChartValue(dt.plusMinutes(5).getMillis(),123.32,dt.plusMinutes(6).getMillis()));
        list.add(buildChartValue(dt.plusMinutes(6).getMillis(),362.32,dt.plusMinutes(6).getMillis()));
        list.add(buildChartValue(dt.plusMinutes(7).getMillis(),265.32,dt.plusMinutes(6).getMillis()));
        // This will get min-max values for each fifteen minute
        List<ChartValue<Double>> result = ReflectionTestUtils.invokeMethod(chartServiceImpl,"getXAxisMinMaxValues",ChartInterval.FIFTEENMINUTE, list, true);
        // 1st 15 minutes [min-max]
        assertTrue(result.get(0).getValue() == 42.32 && result.get(1).getValue() == 162.32);
        // 2nd 15 minutes [min-max]
        assertTrue(result.get(2).getValue() == 123.32 && result.get(3).getValue() == 362.32);
    }

    @Test
    public void test_getXAxisMinMaxValues_HOUR_max() throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        List<ChartValue<Double>> list = new ArrayList<>();
        DateTime dt = new DateTime(2018, 8, 5, 7, 10, 5, 0);
        // Hour-1
        list.add(buildChartValue(dt.plusHours(1).plusMinutes(5).getMillis(),42.32,dt.plusHours(1).plusMinutes(5).getMillis()));
        list.add(buildChartValue(dt.plusHours(1).plusMinutes(6).getMillis(),162.32,dt.plusHours(1).plusMinutes(6).getMillis()));
        list.add(buildChartValue(dt.plusHours(1).plusMinutes(7).getMillis(),128.32,dt.plusHours(1).plusMinutes(7).getMillis()));
        list.add(buildChartValue(dt.plusHours(1).plusMinutes(14).getMillis(),82.32,dt.plusHours(1).plusMinutes(14).getMillis()));
        //Hour-2
        list.add(buildChartValue(dt.plusHours(2).plusMinutes(5).getMillis(),123.32,dt.plusHours(2).plusMinutes(5).getMillis()));
        list.add(buildChartValue(dt.plusHours(2).plusMinutes(6).getMillis(),62.32,dt.plusHours(2).plusMinutes(6).getMillis()));
        list.add(buildChartValue(dt.plusHours(2).plusMinutes(7).getMillis(),265.32,dt.plusHours(2).plusMinutes(7).getMillis()));
        list.add(buildChartValue(dt.plusHours(2).plusMinutes(14).getMillis(),82.32,dt.plusHours(2).plusMinutes(14).getMillis()));
        // This will get max values for each hour
        List<ChartValue<Double>> result = ReflectionTestUtils.invokeMethod(chartServiceImpl,"getXAxisMinMaxValues",ChartInterval.HOUR, list, false);
        assertTrue(result.get(0).getValue() == 162.32 && result.get(1).getValue() == 265.32);
    }

    @Test
    public void test_getXAxisMinMaxValues_HOUR_min_max() throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        List<ChartValue<Double>> list = new ArrayList<>();
        DateTime dt = new DateTime(2018, 8, 5, 7, 10, 5, 0);
        // Hour-1
        list.add(buildChartValue(dt.plusHours(1).plusMinutes(5).getMillis(),42.32,dt.plusHours(1).plusMinutes(5).getMillis()));
        list.add(buildChartValue(dt.plusHours(1).plusMinutes(6).getMillis(),162.32,dt.plusHours(1).plusMinutes(6).getMillis()));
        list.add(buildChartValue(dt.plusHours(1).plusMinutes(7).getMillis(),128.32,dt.plusHours(1).plusMinutes(7).getMillis()));
        list.add(buildChartValue(dt.plusHours(1).plusMinutes(14).getMillis(),82.32,dt.plusHours(1).plusMinutes(14).getMillis()));
        //Hour-2
        list.add(buildChartValue(dt.plusHours(2).plusMinutes(5).getMillis(),123.32,dt.plusHours(2).plusMinutes(5).getMillis()));
        list.add(buildChartValue(dt.plusHours(2).plusMinutes(6).getMillis(),362.32,dt.plusHours(2).plusMinutes(6).getMillis()));
        list.add(buildChartValue(dt.plusHours(2).plusMinutes(7).getMillis(),265.32,dt.plusHours(2).plusMinutes(7).getMillis()));
        list.add(buildChartValue(dt.plusHours(2).plusMinutes(14).getMillis(),82.32,dt.plusHours(2).plusMinutes(14).getMillis()));
        // This will get max values for each hour
        List<ChartValue<Double>> result = ReflectionTestUtils.invokeMethod(chartServiceImpl,"getXAxisMinMaxValues",ChartInterval.HOUR, list, true);
        // Hour-1 [min -max]
        assertTrue(result.get(0).getValue() == 42.32 && result.get(1).getValue() == 162.32);
        // Hour-1 [max- min]
        assertTrue(result.get(2).getValue() == 362.32 && result.get(3).getValue() == 82.32);
    }

    @Test
    public void test_getXAxisMinMaxValues_DAY_max() throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        List<ChartValue<Double>> list = new ArrayList<>();
        DateTime dt = new DateTime(2018, 8, 5, 7, 10, 5, 0);
        // Day-1
        list.add(buildChartValue(dt.getMillis(),12.32,dt.getMillis()));
        list.add(buildChartValue(dt.plusHours(1).getMillis(),42.32,dt.plusHours(1).getMillis()));
        list.add(buildChartValue(dt.plusHours(2).getMillis(),62.32,dt.plusHours(2).getMillis()));
        list.add(buildChartValue(dt.plusHours(3).getMillis(),128.32,dt.plusHours(3).getMillis()));
        list.add(buildChartValue(dt.plusHours(4).getMillis(),82.32,dt.plusHours(4).getMillis()));
        //Day-2
        list.add(buildChartValue(dt.plusDays(1).getMillis(),82.32,dt.plusDays(1).getMillis()));
        list.add(buildChartValue(dt.plusDays(1).getMillis(),22.32,dt.plusDays(1).getMillis()));
        list.add(buildChartValue(dt.plusDays(1).getMillis(),2.32,dt.plusDays(1).getMillis()));
        list.add(buildChartValue(dt.plusDays(1).getMillis(),92.32,dt.plusDays(1).getMillis()));
        // This will get max values for each day
        List<ChartValue<Double>> result = ReflectionTestUtils.invokeMethod(chartServiceImpl,"getXAxisMinMaxValues",ChartInterval.DAY, list, false);
        assertTrue(result.get(0).getValue() == 128.32 && result.get(1).getValue() == 92.32);
    }

    @Test
    public void test_getXAxisMinMaxValues_DAY_min_max() throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        List<ChartValue<Double>> list = new ArrayList<>();
        DateTime dt = new DateTime(2018, 8, 5, 7, 10, 5, 0);
        // Day-1
        list.add(buildChartValue(dt.getMillis(),12.32,dt.getMillis()));
        list.add(buildChartValue(dt.plusHours(1).getMillis(),42.32,dt.plusHours(1).getMillis()));
        list.add(buildChartValue(dt.plusHours(2).getMillis(),62.32,dt.plusHours(2).getMillis()));
        list.add(buildChartValue(dt.plusHours(3).getMillis(),128.32,dt.plusHours(3).getMillis()));
        list.add(buildChartValue(dt.plusHours(4).getMillis(),82.32,dt.plusHours(4).getMillis()));
        //Day-2
        list.add(buildChartValue(dt.plusDays(1).getMillis(),92.32,dt.plusDays(1).getMillis()));
        list.add(buildChartValue(dt.plusDays(1).getMillis(),22.32,dt.plusDays(1).getMillis()));
        list.add(buildChartValue(dt.plusDays(1).plusMinutes(5).getMillis(),2.32,dt.plusDays(1).plusMinutes(5).getMillis()));
        list.add(buildChartValue(dt.plusDays(1).getMillis(),52.32,dt.plusDays(1).getMillis()));
        List<ChartValue<Double>> result = ReflectionTestUtils.invokeMethod(chartServiceImpl,"getXAxisMinMaxValues",ChartInterval.DAY, list, true);
        //Day-1 [min max]
        assertTrue(result.get(0).getValue() == 12.32 && result.get(1).getValue() == 128.32);
        // Day-2  [max-min]
        assertTrue(result.get(2).getValue() == 92.32 && result.get(3).getValue() == 2.32);
    }

    @Test
    public void test_getXAxisMinMaxValues_WEEK_max() throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        List<ChartValue<Double>> list = new ArrayList<>();
        DateTime dt = new DateTime(2018, 8, 5, 7, 11, 0, 0);
        // 1st Week
        list.add(buildChartValue(dt.plusMinutes(1).getMillis(),42.32,dt.plusMinutes(1).getMillis()));
        list.add(buildChartValue(dt.plusMinutes(2).getMillis(),162.32,dt.plusMinutes(2).getMillis()));
        list.add(buildChartValue(dt.plusMinutes(3).getMillis(),128.32,dt.plusMinutes(3).getMillis()));
        // 2nd Week
        list.add(buildChartValue(dt.plusWeeks(1).getMillis(),123.32,dt.plusWeeks(1).getMillis()));
        list.add(buildChartValue(dt.plusWeeks(1).getMillis(),362.32,dt.plusWeeks(1).getMillis()));
        list.add(buildChartValue(dt.plusWeeks(1).getMillis(),265.32,dt.plusWeeks(1).getMillis()));
        
        List<ChartValue<Double>> result = ReflectionTestUtils.invokeMethod(chartServiceImpl,"getXAxisMinMaxValues",ChartInterval.WEEK, list, false);
        // This will get max values for each week
        assertTrue(result.get(0).getValue() == 162.32 && result.get(1).getValue() == 362.32);
    }

    @Test
    public void test_getXAxisMinMaxValues_WEEK_min_max() throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        List<ChartValue<Double>> list = new ArrayList<>();
        DateTime dt = new DateTime(2018, 8, 5, 7, 11, 0, 0);
        // 1st Week
        list.add(buildChartValue(dt.plusMinutes(1).getMillis(),42.32,dt.plusMinutes(1).getMillis()));
        list.add(buildChartValue(dt.plusMinutes(2).getMillis(),162.32,dt.plusMinutes(2).getMillis()));
        list.add(buildChartValue(dt.plusMinutes(3).getMillis(),128.32,dt.plusMinutes(3).getMillis()));
        // 2nd Week
        list.add(buildChartValue(dt.plusWeeks(1).getMillis(),123.32,dt.plusWeeks(1).getMillis()));
        list.add(buildChartValue(dt.plusWeeks(1).getMillis(),362.32,dt.plusWeeks(1).getMillis()));
        list.add(buildChartValue(dt.plusWeeks(1).getMillis(),265.32,dt.plusWeeks(1).getMillis()));
        // This will get min-max values for each week
        List<ChartValue<Double>> result = ReflectionTestUtils.invokeMethod(chartServiceImpl,"getXAxisMinMaxValues",ChartInterval.WEEK, list, true);
        //  1st Week [min-max]
        assertTrue(result.get(0).getValue() == 42.32 && result.get(1).getValue() == 162.32);
        // 2nd Week [min-max]
        assertTrue(result.get(2).getValue() == 123.32 && result.get(3).getValue() == 362.32);
    }

    @Test
    public void test_getXAxisMinMaxValues_MONTH_max() throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        List<ChartValue<Double>> list = new ArrayList<>();
        DateTime dt = new DateTime(2018, 8, 5, 7, 11, 0, 0);
        // 1st month
        list.add(buildChartValue(dt.plusMinutes(1).getMillis(),42.32,dt.plusMinutes(1).getMillis()));
        list.add(buildChartValue(dt.plusMinutes(2).getMillis(),162.32,dt.plusMinutes(2).getMillis()));
        list.add(buildChartValue(dt.plusMinutes(3).getMillis(),128.32,dt.plusMinutes(3).getMillis()));
        // 2nd month
        list.add(buildChartValue(dt.plusMonths(1).getMillis(),123.32,dt.plusMonths(1).getMillis()));
        list.add(buildChartValue(dt.plusMonths(1).getMillis(),362.32,dt.plusMonths(1).getMillis()));
        list.add(buildChartValue(dt.plusMonths(1).getMillis(),265.32,dt.plusMonths(1).getMillis()));
        
        List<ChartValue<Double>> result = ReflectionTestUtils.invokeMethod(chartServiceImpl,"getXAxisMinMaxValues",ChartInterval.MONTH, list, false);
        // This will get max values for each month
        assertTrue(result.get(0).getValue() == 162.32 && result.get(1).getValue() == 362.32);
    }

    @Test
    public void test_getXAxisMinMaxValues_MONTH_min_max() throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        List<ChartValue<Double>> list = new ArrayList<>();
        DateTime dt = new DateTime(2018, 8, 5, 7, 11, 0, 0);
        // 1st month
        list.add(buildChartValue(dt.plusMinutes(1).getMillis(),42.32,dt.plusMinutes(1).getMillis()));
        list.add(buildChartValue(dt.plusMinutes(2).getMillis(),162.32,dt.plusMinutes(2).getMillis()));
        list.add(buildChartValue(dt.plusMinutes(3).getMillis(),128.32,dt.plusMinutes(3).getMillis()));
        // 2nd month
        list.add(buildChartValue(dt.plusMonths(1).getMillis(),123.32,dt.plusMonths(1).getMillis()));
        list.add(buildChartValue(dt.plusMonths(1).getMillis(),362.32,dt.plusMonths(1).getMillis()));
        list.add(buildChartValue(dt.plusMonths(1).getMillis(),265.32,dt.plusMonths(1).getMillis()));
        // This will get min-max values for each month
        List<ChartValue<Double>> result = ReflectionTestUtils.invokeMethod(chartServiceImpl,"getXAxisMinMaxValues",ChartInterval.MONTH, list, true);
        // 1st month [min-max]
        assertTrue(result.get(0).getValue() == 42.32 && result.get(1).getValue() == 162.32);
        // 2nd month [min-max]
        assertTrue(result.get(2).getValue() == 123.32 && result.get(3).getValue() == 362.32);
    }

    private ChartValue<Double> buildChartValue(long id, Double value, long time){
        ChartValue<Double> chartValue = new ChartValue<Double>(id, value);
        chartValue.setTime(time);
        return chartValue;
    }

}
