package com.cannontech.common.validation;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.joda.time.DateTime;
import org.junit.jupiter.api.Test;

import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.definition.model.PaoPointIdentifier;
import com.cannontech.common.pao.definition.model.PointIdentifier;
import com.cannontech.common.validation.RawPointHistoryValidationService.AnalysisResult;
import com.cannontech.common.validation.RawPointHistoryValidationService.RawPointHistoryWorkUnit;
import com.cannontech.common.validation.RawPointHistoryValidationService.RawPointHistoryWrapper;
import com.cannontech.common.validation.model.RphTag;
import com.cannontech.common.validation.model.ValidationMonitor;
import com.cannontech.database.data.point.PointType;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableListMultimap;
import com.google.common.collect.Multimap;


public class RawPointHistoryValidationServiceTest {
    
    
    @Test
    public void test_calculateHeight_peakUp1() {
        
        // This is a simple case with three readings exactly one day apart.
        // Because the average between the first and last is 200, the height is 400 - 200 = 200.
        
        RawPointHistoryWrapper base1 = createPoint(1, "2009-5-1T6:00:00Z", 100.0);
        RawPointHistoryWrapper peak = createPoint(2, "2009-5-2T6:00:00Z", 400.0);
        RawPointHistoryWrapper base2 = createPoint(3, "2009-5-3T6:00:00Z", 300.0);
        
        double calculateHeight = RawPointHistoryValidationService.calculateHeight(base1, peak, base2);
        assertEquals(200.0, calculateHeight, .01);
    }

    @Test
    public void test_calculateHeight_peakUp2() {
        
        // This case has a reading on day 1, the peak on day 2, and the next reading on day 5.
        // Because the average between the first and last on the day of the peak is 150,
        // the height is 400 - 150 = 250.
        
        RawPointHistoryWrapper base1 = createPoint(1, "2009-6-1T6:00:00Z", 100.0);
        RawPointHistoryWrapper peak = createPoint(2, "2009-6-2T6:00:00Z", 400.0);
        RawPointHistoryWrapper base2 = createPoint(3, "2009-6-5T6:00:00Z", 300.0);
        
        double calculateHeight = RawPointHistoryValidationService.calculateHeight(base1, peak, base2);
        assertEquals(250.0, calculateHeight, .01);
    }
    
    @Test
    public void test_calculateHeight_peakDown() {
        
        // This is a simple case with three readings exactly one day apart.
        // Because the average between the first and last is 400, the height is 200 - 400 = -200.
        
        RawPointHistoryWrapper base1 = createPoint(1, "2009-5-1T6:00:00Z", 300.0);
        RawPointHistoryWrapper peak = createPoint(2, "2009-5-2T6:00:00Z", 200.0);
        RawPointHistoryWrapper base2 = createPoint(3, "2009-5-3T6:00:00Z", 500.0);
        
        double calculateHeight = RawPointHistoryValidationService.calculateHeight(base1, peak, base2);
        assertEquals(-200.0, calculateHeight, .01);
    }
    
    @Test
    public void test_calculateLowerAvgKwhPerDay_simplePositive() {
        // 1000 on day 1 and 2000 on day 2 should be a slope of 1000/day
        RawPointHistoryWrapper reading1 = createPoint(1, "2009-5-1T6:00:00Z", 1000);
        RawPointHistoryWrapper reading2 = createPoint(1, "2009-5-2T6:00:00Z", 2000);
        
        double kwhPerDay = RawPointHistoryValidationService.calculateLowerAvgKwhPerDay(reading1, reading2, 0);
        assertEquals(1000, kwhPerDay, .01);
    }
    
    @Test
    public void test_calculateLowerAvgKwhPerDay_simpleNegative() {
        // 2000 on day 1 and 1000 on day 2 should be a slope of -1000/day
        RawPointHistoryWrapper reading1 = createPoint(1, "2009-5-1T6:00:00Z", 2000);
        RawPointHistoryWrapper reading2 = createPoint(1, "2009-5-2T6:00:00Z", 1000);
        
        double kwhPerDay = RawPointHistoryValidationService.calculateLowerAvgKwhPerDay(reading1, reading2, 0);
        assertEquals(-1000, kwhPerDay, .01);
    }
    
    // Because the exact nature of the slope error formula isn't all that important,
    // the following will look at the general relationship between similar results.
    
    @Test
    public void test_calculateLowerAvgKwhPerDay_slopeErrorGeneral1() {
        
        // it is expected that the kwhPerDay decreases as the slopeError increase
        
        RawPointHistoryWrapper reading1 = createPoint(1, "2009-5-1T6:00:00Z", 1000);
        RawPointHistoryWrapper reading2 = createPoint(1, "2009-5-1T7:00:00Z", 1100);
        
        double kwhPerDay_0 = RawPointHistoryValidationService.calculateLowerAvgKwhPerDay(reading1, reading2, 0);
        double kwhPerDay_2 = RawPointHistoryValidationService.calculateLowerAvgKwhPerDay(reading1, reading2, 2);
        
        assertTrue(kwhPerDay_2 < kwhPerDay_0);
    }
    
    @Test
    public void test_calculateLowerAvgKwhPerDay_slopeErrorGeneral2() {
        
        // it is expected that the kwhPerDay decreases as the time delta decreases
        
        // the following has a consistent slope: 50 kwh/30 minutes
        RawPointHistoryWrapper reading1 = createPoint(1, "2009-5-1T6:00:00Z", 1000);
        RawPointHistoryWrapper reading2 = createPoint(1, "2009-5-1T6:30:00Z", 1050);
        RawPointHistoryWrapper reading3 = createPoint(1, "2009-5-1T7:00:00Z", 1100);
        
        double kwhPerDay_1_2 = RawPointHistoryValidationService.calculateLowerAvgKwhPerDay(reading1, reading2, 4);
        double kwhPerDay_1_3 = RawPointHistoryValidationService.calculateLowerAvgKwhPerDay(reading1, reading3, 4);
        
        assertTrue(kwhPerDay_1_2 < kwhPerDay_1_3);
    }
    
    @Test
    public void test_calculateLowerAvgKwhPerDay_slopeErrorGeneral3() {
        
        // the result should never be negative for increasing usage
        
        // the following has a consistent slope: 50 kwh/30 minutes
        RawPointHistoryWrapper reading1 = createPoint(1, "2009-5-1T6:00:00Z", 1000);
        RawPointHistoryWrapper reading2 = createPoint(1, "2009-5-1T6:05:00Z", 1001);
        
        double kwhPerDay = RawPointHistoryValidationService.calculateLowerAvgKwhPerDay(reading1, reading2, 4);
        
        assertTrue(kwhPerDay >= 0);
    }
    
    // For tracking purposes, the following will verify several values as the formula
    // returns them currently. These values aren't necessarily "right" or important.
    
    @Test
    public void test_calculateLowerAvgKwhPerDay_exact1() {
        // 288 kwh/day (12 kwh/hour) for 5 minutes
        RawPointHistoryWrapper reading1 = createPoint(1, "2009-5-1T6:00:00Z", 1000);
        RawPointHistoryWrapper reading2 = createPoint(1, "2009-5-1T6:05:00Z", 1001);
        
        double kwhPerDay = RawPointHistoryValidationService.calculateLowerAvgKwhPerDay(reading1, reading2, 4);
        
        assertEquals(0, kwhPerDay, 0);
    }
    
    @Test
    public void test_calculateLowerAvgKwhPerDay_exact2() {
        // 120 kwh/day (5 kwh/hour) for 2 hours
        RawPointHistoryWrapper reading1 = createPoint(1, "2009-5-1T6:00:00Z", 1000);
        RawPointHistoryWrapper reading2 = createPoint(1, "2009-5-1T8:00:00Z", 1010);
        
        double kwhPerDay = RawPointHistoryValidationService.calculateLowerAvgKwhPerDay(reading1, reading2, 2);
        
        assertEquals(96, kwhPerDay, .001);
    }
    
    @Test
    public void test_calculateLowerAvgKwhPerDay_exact3() {
        // 100 kwh/day for 15 days
        RawPointHistoryWrapper reading1 = createPoint(1, "2009-5-1T6:00:00Z", 1000);
        RawPointHistoryWrapper reading2 = createPoint(1, "2009-5-16T6:00:00Z", 2500);
        
        double kwhPerDay = RawPointHistoryValidationService.calculateLowerAvgKwhPerDay(reading1, reading2, 4);
        
        assertEquals(99.7333, kwhPerDay, .001);
    }
    
    @Test
    public void test_calculateLowerAvgKwhPerDay_exact4() {
        // -100 kwh/day for 1 day
        RawPointHistoryWrapper reading1 = createPoint(1, "2009-5-1T6:00:00Z", 1000);
        RawPointHistoryWrapper reading2 = createPoint(1, "2009-5-2T6:00:00Z", 900);
        
        double kwhPerDay = RawPointHistoryValidationService.calculateLowerAvgKwhPerDay(reading1, reading2, 4);
        
        assertEquals(-96, kwhPerDay, .001);
    }
    
    @Test
    public void test_calculateLowerAvgKwhPerDayFromZero_exact1() {
        // 1000 will be used as 0 for a net of 100 kwh/day for 1 day
        RawPointHistoryWrapper reading1 = createPoint(1, "2009-5-1T6:00:00Z", 1000);
        RawPointHistoryWrapper reading2 = createPoint(1, "2009-5-2T6:00:00Z", 100);
        
        double kwhPerDay = RawPointHistoryValidationService.calculateLowerAvgKwhPerDayFromZero(reading1, reading2);
        
        assertEquals(100, kwhPerDay, .001);
    }
    
    @Test
    public void test_calculateLowerAvgKwhPerDayFromZero_exact2() {
        // 1000 will be used as 0 for a net of 100 kwh/day for 1 day
        // (this doesn't represent a reasonable value for calling this method)
        RawPointHistoryWrapper reading1 = createPoint(1, "2009-5-1T6:00:00Z", 1000);
        RawPointHistoryWrapper reading2 = createPoint(1, "2009-5-2T6:00:00Z", 2000);
        
        double kwhPerDay = RawPointHistoryValidationService.calculateLowerAvgKwhPerDayFromZero(reading1, reading2);
        
        assertEquals(2000, kwhPerDay, .001);
    }
    
    @Test
    public void test_calculateLowerAvgKwhPerDayFromZero_exact3() {
        // 9999 will be used as 0 for a net of 100 kwh/day for 4 day
        RawPointHistoryWrapper reading1 = createPoint(1, "2009-5-1T6:00:00Z", 9999);
        RawPointHistoryWrapper reading2 = createPoint(1, "2009-5-5T6:00:00Z", 400);
        
        double kwhPerDay = RawPointHistoryValidationService.calculateLowerAvgKwhPerDayFromZero(reading1, reading2);
        
        assertEquals(100, kwhPerDay, .001);
    }
    
    @Test
    public void test_calculateLowerAvgKwhPerDayFromZero_exact4() {
        // 1000 will be used as 0 for a net of 100 kwh/day for .5 days
        RawPointHistoryWrapper reading1 = createPoint(1, "2009-5-1T6:00:00Z", 9999);
        RawPointHistoryWrapper reading2 = createPoint(1, "2009-5-1T18:00:00Z", 50);
        
        double kwhPerDay = RawPointHistoryValidationService.calculateLowerAvgKwhPerDayFromZero(reading1, reading2);
        
        assertEquals(100, kwhPerDay, .001);
    }
    
    @Test
    public void test_analyzeThreeHistoryRows_basicPeakUp() {
        ValidationMonitor validationMonitor = createStandardSettings();
        
        RawPointHistoryWrapper reading1 = createPoint(1, "2009-5-1T6:00:00Z", 1000);
        RawPointHistoryWrapper reading2 = createPoint(1, "2009-5-2T6:00:00Z", 32154); // clearly too high
        RawPointHistoryWrapper reading3 = createPoint(1, "2009-5-3T6:00:00Z", 1200);

        RawPointHistoryWorkUnit workUnit = createWorkUnit(1, reading3);
        List<RawPointHistoryWrapper> values = ImmutableList.of(reading3, reading2, reading1);
        Multimap<RawPointHistoryWrapper, RphTag> tags = ArrayListMultimap.create();
        AnalysisResult analysisResult = RawPointHistoryValidationService.analyzeThreeHistoryRows(workUnit, validationMonitor, values, tags);
        
        assertEquals(ImmutableListMultimap.of(reading2, RphTag.PEAKUP), tags);
        assertEquals(true, analysisResult.peakInTheMiddle);
        assertEquals(false, analysisResult.considerReRead);
    }

    @Test
    public void test_analyzeThreeHistoryRows_basicPeakDown() {
        ValidationMonitor validationMonitor = createStandardSettings();
        
        RawPointHistoryWrapper reading1 = createPoint(1, "2009-5-1T6:00:00Z", 30000);
        RawPointHistoryWrapper reading2 = createPoint(1, "2009-5-2T6:00:00Z", 1523); // clearly too low
        RawPointHistoryWrapper reading3 = createPoint(1, "2009-5-3T6:00:00Z", 30200);
        
        RawPointHistoryWorkUnit workUnit = createWorkUnit(1, reading3);
        List<RawPointHistoryWrapper> values = ImmutableList.of(reading3, reading2, reading1);
        Multimap<RawPointHistoryWrapper, RphTag> tags = ArrayListMultimap.create();
        AnalysisResult analysisResult = RawPointHistoryValidationService.analyzeThreeHistoryRows(workUnit, validationMonitor, values, tags);
        
        assertEquals(ImmutableListMultimap.of(reading2, RphTag.PEAKDOWN), tags);
        assertEquals(true, analysisResult.peakInTheMiddle);
        assertEquals(false, analysisResult.considerReRead);
    }
    
    @Test
    public void test_analyzeThreeHistoryRows_basicUnreasonableUp() {
        ValidationMonitor validationMonitor = createStandardSettings();
        
        RawPointHistoryWrapper reading1 = createPoint(1, "2009-5-1T6:00:00Z", 30000); // won't matter
        RawPointHistoryWrapper reading2 = createPoint(1, "2009-5-2T6:00:00Z", 30100);
        RawPointHistoryWrapper reading3 = createPoint(1, "2009-5-3T6:00:00Z", 30900); // 800 kwh/day is unreasonable
        
        RawPointHistoryWorkUnit workUnit = createWorkUnit(1, reading3);
        List<RawPointHistoryWrapper> values = ImmutableList.of(reading3, reading2, reading1);
        Multimap<RawPointHistoryWrapper, RphTag> tags = ArrayListMultimap.create();
        AnalysisResult analysisResult = RawPointHistoryValidationService.analyzeThreeHistoryRows(workUnit, validationMonitor, values, tags);
        
        assertEquals(ImmutableListMultimap.of(reading3, RphTag.UNREASONABLEUP), tags);
        assertEquals(false, analysisResult.peakInTheMiddle);
        assertEquals(true, analysisResult.considerReRead);
    }
    
    @Test
    public void test_analyzeThreeHistoryRows_basicUnreasonableDown() {
        ValidationMonitor validationMonitor = createStandardSettings();
        
        RawPointHistoryWrapper reading1 = createPoint(1, "2009-5-1T6:00:00Z", 30000); //won't matter
        RawPointHistoryWrapper reading2 = createPoint(1, "2009-5-2T6:00:00Z", 30100);
        RawPointHistoryWrapper reading3 = createPoint(1, "2009-5-3T6:00:00Z", 15000); // negative slope
        
        RawPointHistoryWorkUnit workUnit = createWorkUnit(1, reading3);
        List<RawPointHistoryWrapper> values = ImmutableList.of(reading3, reading2, reading1);
        Multimap<RawPointHistoryWrapper, RphTag> tags = ArrayListMultimap.create();
        AnalysisResult analysisResult = RawPointHistoryValidationService.analyzeThreeHistoryRows(workUnit, validationMonitor, values, tags);
        
        assertEquals(ImmutableListMultimap.of(reading3, RphTag.UNREASONABLEDOWN), tags);
        assertEquals(false, analysisResult.peakInTheMiddle);
        assertEquals(true, analysisResult.considerReRead);
    }
    
    @Test
    public void test_analyzeThreeHistoryRows_basicUnreasonableDownChangeout() {
        ValidationMonitor validationMonitor = createStandardSettings();
        
        RawPointHistoryWrapper reading1 = createPoint(1, "2009-5-1T6:00:00Z", 30000); //won't matter
        RawPointHistoryWrapper reading2 = createPoint(1, "2009-5-2T6:00:00Z", 30100);
        RawPointHistoryWrapper reading3 = createPoint(1, "2009-5-3T6:00:00Z", 300); // negative slope, but close to zero
        
        RawPointHistoryWorkUnit workUnit = createWorkUnit(1, reading3);
        List<RawPointHistoryWrapper> values = ImmutableList.of(reading3, reading2, reading1);
        Multimap<RawPointHistoryWrapper, RphTag> tags = ArrayListMultimap.create();
        AnalysisResult analysisResult = RawPointHistoryValidationService.analyzeThreeHistoryRows(workUnit, validationMonitor, values, tags);
        
        assertEquals(ImmutableListMultimap.of(reading3, RphTag.CHANGEOUT), tags);
        assertEquals(false, analysisResult.peakInTheMiddle);
        assertEquals(false, analysisResult.considerReRead);
    }
    
    @Test
    public void test_analyzeThreeHistoryRows_lowPeakUp() {
        ValidationMonitor validationMonitor = createStandardSettings();
        
        RawPointHistoryWrapper reading1 = createPoint(1, "2009-5-1T6:00:00Z", 1000);
        RawPointHistoryWrapper reading2 = createPoint(1, "2009-5-2T6:00:00Z", 1014); // expected is 105, so height = 9
        RawPointHistoryWrapper reading3 = createPoint(1, "2009-5-3T6:00:00Z", 1010);
        
        RawPointHistoryWorkUnit workUnit = createWorkUnit(1, reading3);
        List<RawPointHistoryWrapper> values = ImmutableList.of(reading3, reading2, reading1);
        Multimap<RawPointHistoryWrapper, RphTag> tags = ArrayListMultimap.create();
        AnalysisResult analysisResult = RawPointHistoryValidationService.analyzeThreeHistoryRows(workUnit, validationMonitor, values, tags);
        
        assertEquals(ImmutableListMultimap.of(reading3, RphTag.UNREASONABLEDOWN), tags); // UnreasonableDown, but no PeakUp!
        assertEquals(false, analysisResult.peakInTheMiddle);
        assertEquals(true, analysisResult.considerReRead);
    }
    
    @Test
    public void test_analyzeThreeHistoryRows_flat() {
        ValidationMonitor validationMonitor = createStandardSettings();
        
        RawPointHistoryWrapper reading1 = createPoint(1, "2009-5-1T6:00:00Z", 1000);
        RawPointHistoryWrapper reading2 = createPoint(1, "2009-5-2T6:00:00Z", 1000);
        RawPointHistoryWrapper reading3 = createPoint(1, "2009-5-3T6:00:00Z", 1000);
        
        RawPointHistoryWorkUnit workUnit = createWorkUnit(1, reading3);
        List<RawPointHistoryWrapper> values = ImmutableList.of(reading3, reading2, reading1);
        Multimap<RawPointHistoryWrapper, RphTag> tags = ArrayListMultimap.create();
        AnalysisResult analysisResult = RawPointHistoryValidationService.analyzeThreeHistoryRows(workUnit, validationMonitor, values, tags);
        
        assertEquals(ImmutableListMultimap.of(), tags); 
        assertEquals(false, analysisResult.peakInTheMiddle);
        assertEquals(false, analysisResult.considerReRead);
    }
    
    @Test
    public void test_analyzeThreeHistoryRows_normal() {
        ValidationMonitor validationMonitor = createStandardSettings();
        
        RawPointHistoryWrapper reading1 = createPoint(1, "2009-5-1T6:00:00Z", 1100);
        RawPointHistoryWrapper reading2 = createPoint(1, "2009-5-2T6:00:00Z", 1200); 
        RawPointHistoryWrapper reading3 = createPoint(1, "2009-5-3T6:00:00Z", 1300);
        
        RawPointHistoryWorkUnit workUnit = createWorkUnit(1, reading3);
        List<RawPointHistoryWrapper> values = ImmutableList.of(reading3, reading2, reading1);
        Multimap<RawPointHistoryWrapper, RphTag> tags = ArrayListMultimap.create();
        AnalysisResult analysisResult = RawPointHistoryValidationService.analyzeThreeHistoryRows(workUnit, validationMonitor, values, tags);
        
        assertEquals(ImmutableListMultimap.of(), tags); 
        assertEquals(false, analysisResult.peakInTheMiddle);
        assertEquals(false, analysisResult.considerReRead);
    }
    
    private ValidationMonitor createStandardSettings() {
        ValidationMonitor validationMonitor = new ValidationMonitor();
        validationMonitor.setKwhReadingError(.1000001);
        validationMonitor.setKwhSlopeError(4);
        validationMonitor.setPeakHeightMinimum(10);
        validationMonitor.setReasonableMaxKwhPerDay(400);
        return validationMonitor;
    }

    private RawPointHistoryWorkUnit createWorkUnit(int paoNumber, RawPointHistoryWrapper reading) {
        RawPointHistoryWorkUnit workUnit = new RawPointHistoryWorkUnit();
        workUnit.thisValue = reading;
        workUnit.pointId = paoNumber * 10;
        PaoIdentifier paoIdentifier = new PaoIdentifier(paoNumber, PaoType.MCT410IL);
        PointIdentifier pointIdentifier = new PointIdentifier(PointType.PulseAccumulator, 1);
        workUnit.paoPointIdentifier = new PaoPointIdentifier(paoIdentifier, pointIdentifier);
        return workUnit;
    }
    
    private RawPointHistoryWrapper createPoint(long changeId, String dateTimeStr, double value) {
        RawPointHistoryWrapper wrapper = new RawPointHistoryWrapper();
        wrapper.changeId = changeId;
        wrapper.timestamp = new DateTime(dateTimeStr);
        wrapper.value = value;
        return wrapper;
    }
}
