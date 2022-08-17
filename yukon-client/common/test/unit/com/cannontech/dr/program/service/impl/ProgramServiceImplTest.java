package com.cannontech.dr.program.service.impl;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import javax.annotation.Resource;

import org.joda.time.Duration;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.test.util.ReflectionTestUtils;

import com.cannontech.core.service.DateFormattingService;
import com.cannontech.core.service.impl.DateFormattingServiceImpl;
import com.cannontech.dr.program.model.GearAdjustment;
import com.cannontech.dr.scenario.model.ScenarioProgram;
import com.cannontech.user.SimpleYukonUserContext;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

public class ProgramServiceImplTest {
    static private TimeZone timeZone = TimeZone.getTimeZone("America/Chicago");
    
    private SimpleYukonUserContext userContext;

    @Resource(name="drProgramService")
    private ProgramServiceImpl programService;

    @Before
    public void setup() {
        userContext = new SimpleYukonUserContext();
        userContext.setLocale(Locale.getDefault());
        userContext.setTimeZone(timeZone);
        programService = new ProgramServiceImpl();
        DateFormattingService dateFormattingService = new DateFormattingServiceImpl();
        ReflectionTestUtils.setField(programService, "dateFormattingService", dateFormattingService, DateFormattingService.class);
    }

    @After
    public void teardown() {
        programService = null;
        userContext = null;
    }

    @Test
    public void testDatePlusOffset() {
        Date date = makeDate("2009/11/11 05:07");
        Duration offset = new Duration(60 * 1000);
        Date expectedDatePlusOffset = makeDate("2009/11/11 05:08");
        Date datePlusOffset = programService.datePlusOffset(date, offset);
        Assert.assertEquals(expectedDatePlusOffset, datePlusOffset);

        offset = new Duration(60 * 60 * 1000);
        expectedDatePlusOffset = makeDate("2009/11/11 06:07");
        datePlusOffset = programService.datePlusOffset(date, offset);
        Assert.assertEquals(expectedDatePlusOffset, datePlusOffset);

        date = makeDate("2009/03/08 01:07");
        offset = new Duration(60 * 60 * 1000);
        expectedDatePlusOffset = makeDate("2009/03/08 03:07");
        datePlusOffset = programService.datePlusOffset(date, offset);
        Assert.assertEquals(expectedDatePlusOffset, datePlusOffset);

        date = makeDate("2009/11/01 00:07");
        offset = new Duration(60 * 60 * 1000);
        expectedDatePlusOffset = makeDateWithTZ("2009/11/01 01:07-0500");
        datePlusOffset = programService.datePlusOffset(date, offset);
        Assert.assertEquals(expectedDatePlusOffset, datePlusOffset);

        offset = new Duration(120 * 60 * 1000);
        expectedDatePlusOffset = makeDateWithTZ("2009/11/01 01:07-0600");
        datePlusOffset = programService.datePlusOffset(date, offset);
        Assert.assertEquals(expectedDatePlusOffset, datePlusOffset);
    }

    /**
     * Test the getDefaultAdjustments method with very simple arguments.
     */
    @Test
    public void simpleGearAdjustmentTest() {
        Date startDate = makeDate("2009/11/11 05:07");
        Date stopDate = makeDate("2009/11/11 07:07");;
        List<GearAdjustment> correctAdjustments =
            makeAdjustments("2009/11/11 05:00",
                            "2009/11/11 06:00",
                            "2009/11/11 07:00");
        List<GearAdjustment> adjustments =
            programService.getDefaultAdjustments(startDate, stopDate,
                                                 null, userContext);
        Assert.assertEquals(correctAdjustments, adjustments);

        // some edge cases
        startDate = makeDate("2009/11/11 18:00");
        stopDate = makeDate("2009/11/11 19:00");;
        correctAdjustments = makeAdjustments("2009/11/11 18:00");
        adjustments =
            programService.getDefaultAdjustments(startDate, stopDate,
                                                 null, userContext);
        Assert.assertEquals(correctAdjustments, adjustments);

        startDate = makeDate("2009/11/11 17:59");
        stopDate = makeDate("2009/11/11 19:01");;
        correctAdjustments =
            makeAdjustments("2009/11/11 17:0",
                            "2009/11/11 18:00",
                            "2009/11/11 19:00");
        adjustments =
            programService.getDefaultAdjustments(startDate, stopDate,
                                                 null, userContext);
        Assert.assertEquals(correctAdjustments, adjustments);

        // cross a date boundary
        startDate = makeDate("2009/11/11 22:12");
        stopDate = makeDate("2009/11/12 01:34");;
        correctAdjustments =
            makeAdjustments("2009/11/11 22:00",
                            "2009/11/11 23:00",
                            "2009/11/12 00:00",
                            "2009/11/12 01:00");
        adjustments =
            programService.getDefaultAdjustments(startDate, stopDate,
                                                 null, userContext);
        Assert.assertEquals(correctAdjustments, adjustments);
    }

    /**
     * Test getDefaultAdjustments as called with multiple programs with start
     * and stop offsets (starting a scenario).
     */
    @Test
    public void scenarioGearAdjustmentTest() {
        Date startDate = makeDate("2009/11/11 05:07");
        Date stopDate = makeDate("2009/11/11 07:07");
        Map<Integer, ScenarioProgram> scenarioPrograms = makeScenarioPrograms(
              0,   0,
             30,   0,
             45,  50,
             90,  95,
              0, 112,
              0, 113,
              0, 114,
            120, 120);

        // so, we have
        //      program 1 runs from 5:07 - 7:07
        //      program 2 runs from 5:37 - 7:07
        //      program 3 runs from 5:52 - 7:57
        //      program 4 runs from 6:37 - 8:42
        //      program 5 runs from 5:07 - 8:59
        //      program 6 runs from 5:07 - 9:00
        //      program 7 runs from 5:07 - 9:01
        //      program 8 runs from 7:07 - 9:07

        List<GearAdjustment> correctAdjustments =
            makeAdjustments("2009/11/11 05:00",
                            "2009/11/11 06:00",
                            "2009/11/11 07:00",
                            "2009/11/11 08:00",
                            "2009/11/11 09:00");

        List<GearAdjustment> adjustments =
            programService.getDefaultAdjustments(startDate, stopDate,
                                                 scenarioPrograms.values(),
                                                 userContext);
        Assert.assertEquals(correctAdjustments, adjustments);

        // change the adjustments so we have something to check for
        int adjustmentValue = 95;
        for (GearAdjustment adjustment : adjustments) {
            adjustment.setAdjustmentValue(adjustmentValue++);
        }
        Map<Integer, String> expectedAdditionalInfoByProgramId = Maps.newHashMap();
        expectedAdditionalInfoByProgramId.put(1, "adjustments 95 96 97");
        expectedAdditionalInfoByProgramId.put(2, "adjustments 95 96 97");
        expectedAdditionalInfoByProgramId.put(3, "adjustments 95 96 97");
        expectedAdditionalInfoByProgramId.put(4, "adjustments 96 97 98");
        expectedAdditionalInfoByProgramId.put(5, "adjustments 95 96 97 98");
        expectedAdditionalInfoByProgramId.put(6, "adjustments 95 96 97 98 99");
        expectedAdditionalInfoByProgramId.put(7, "adjustments 95 96 97 98 99");
        expectedAdditionalInfoByProgramId.put(8, "adjustments 97 98 99");

        // now, let's make sure we get the right adjustment by program
        for (Integer programId : scenarioPrograms.keySet()) {
            ScenarioProgram scenarioProgram = scenarioPrograms.get(programId);
            Date programStartDate =
                programService.datePlusOffset(startDate,
                                              scenarioProgram.getStartOffset());
            Date programStopDate =
                programService.datePlusOffset(stopDate,
                                              scenarioProgram.getStopOffset());
            String additionalInfo =
                programService.additionalInfoFromGearAdjustments(adjustments,
                                                                 programStartDate,
                                                                 programStopDate);
            String expectedAdditionalInfo =
                expectedAdditionalInfoByProgramId.get(programId);
            Assert.assertEquals(expectedAdditionalInfo, additionalInfo);
        }
    }

    @Test
    public void springAheadTest() {
        Date startDate = makeDate("2009/03/08 01:07");
        Date stopDate = makeDate("2009/03/08 05:07");;
        List<GearAdjustment> correctAdjustments =
            makeAdjustments("2009/03/08 01:00",
                            "2009/03/08 03:00",
                            "2009/03/08 04:00",
                            "2009/03/08 05:00");
        List<GearAdjustment> adjustments =
            programService.getDefaultAdjustments(startDate, stopDate,
                                                 null, userContext);
        Assert.assertEquals(correctAdjustments, adjustments);
    }

    @Test
    public void fallBackTest() {
        Date startDate = makeDate("2009/11/01 00:07");
        Date stopDate = makeDate("2009/11/01 02:07");;
        List<GearAdjustment> correctAdjustments =
            makeAdjustments("2009/11/01 00:00",
                            makeDateWithTZ("2009/11/01 01:00-0500"),
                            makeDateWithTZ("2009/11/01 01:00-0600"),
                            "2009/11/01 02:00");
        List<GearAdjustment> adjustments =
            programService.getDefaultAdjustments(startDate, stopDate,
                                                 null, userContext);
        Assert.assertEquals(correctAdjustments, adjustments);
    }

    private static Date makeDate(String dateStr) {
        DateFormat df = new SimpleDateFormat("yyyy/MM/dd HH:mm"); // ss.SSS
        df.setCalendar(Calendar.getInstance(timeZone));
        try {
            return df.parse(dateStr);
        } catch (ParseException parseException) {
            throw new RuntimeException("badly formatted date", parseException);
        }
    }

    private static Date makeDateWithTZ(String dateStr) {
        DateFormat df = new SimpleDateFormat("yyyy/MM/dd HH:mmZ"); // ss.SSS
        df.setCalendar(Calendar.getInstance(timeZone));
        try {
            return df.parse(dateStr);
        } catch (ParseException parseException) {
            throw new RuntimeException("badly formatted date", parseException);
        }
    }

    private List<GearAdjustment> makeAdjustments(Object...beginTimes) {
        List<GearAdjustment> retVal = Lists.newArrayList();
        for (Object beginTime : beginTimes) {
            if (beginTime instanceof String) {
                retVal.add(new GearAdjustment((makeDate((String) beginTime))));
            } else if (beginTime instanceof Date) {
                retVal.add(new GearAdjustment(((Date) beginTime)));
            }
        }
        return retVal;
    }

    private Map<Integer, ScenarioProgram> makeScenarioPrograms(
            Integer... offsets) {
        if (offsets.length % 2 != 0) {
            throw new RuntimeException("makeScenarioProgram needs start " +
            		"and stop offsets in sets");
        }
        Map<Integer, ScenarioProgram> retVal = Maps.newHashMap();
        for (int index = 0; index < offsets.length / 2; index++) {
            int programId = index + 1;
            int startOffset = 60 * offsets[index * 2];
            int stopOffset = 60 * offsets[index * 2 + 1];
            retVal.put(programId,
                       new ScenarioProgram(0, programId, startOffset, stopOffset, 1));
        }
        return retVal;
    }
}
