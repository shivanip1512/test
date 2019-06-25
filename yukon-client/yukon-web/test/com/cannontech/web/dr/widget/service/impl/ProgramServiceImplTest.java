package com.cannontech.web.dr.widget.service.impl;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;

import org.easymock.EasyMock;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.springframework.context.support.StaticMessageSource;
import org.springframework.test.util.ReflectionTestUtils;

import com.cannontech.common.program.widget.model.ProgramData;
import com.cannontech.core.service.DateFormattingService;
import com.cannontech.core.service.impl.DateFormattingServiceImpl;
import com.cannontech.dr.program.service.impl.ProgramServiceImpl;
import com.cannontech.i18n.YukonUserContextMessageSourceResolverMock;
import com.cannontech.loadcontrol.LoadControlClientConnection;
import com.cannontech.loadcontrol.data.LMProgramBase;
import com.cannontech.loadcontrol.data.LMProgramDirect;
import com.cannontech.loadcontrol.service.data.ProgramControlHistory;
import com.cannontech.user.SimpleYukonUserContext;
public class ProgramServiceImplTest {

    private LoadControlClientConnection loadControlClientConnection ;
    private ProgramServiceImpl programServiceImplTest;
    private SimpleYukonUserContext userContext;
    
    private Set<LMProgramBase> programs = new HashSet<>();
    private Set<LMProgramBase> emptyPrograms = new HashSet<>();
    private List<ProgramControlHistory> programControlHistory = new ArrayList<>();
    StaticMessageSource messageSource = new StaticMessageSource();
    {
        messageSource.addMessage("yukon.common.dateFormatting.DATE", Locale.US, "MM/dd/yyyy");
    }
    YukonUserContextMessageSourceResolverMock messageSourceResolver = new YukonUserContextMessageSourceResolverMock();
    {
        messageSourceResolver.setMessageSource(messageSource);
    }

    @Before
    public void setUp() throws Exception {
        programServiceImplTest = new ProgramServiceImpl();
        loadControlClientConnection = EasyMock.createMock(LoadControlClientConnection.class);
        ReflectionTestUtils.setField(programServiceImplTest, "loadControlClientConnection",
            loadControlClientConnection);
        userContext = new SimpleYukonUserContext();
        userContext.setLocale(Locale.getDefault());
        userContext.setTimeZone(TimeZone.getTimeZone("America/Chicago"));
        DateFormattingService dateFormattingService = new DateFormattingServiceImpl();
        ReflectionTestUtils.setField(dateFormattingService, "messageSourceResolver", messageSourceResolver);
        ReflectionTestUtils.setField(programServiceImplTest, "dateFormattingService", dateFormattingService, DateFormattingService.class);
        LMProgramDirect lm1 = new LMProgramDirect();
        DateTime now = DateTime.now();
        // Future scheduled program
        lm1.setDirectStartTime(buildGregorianCalendar(now.getYearOfEra() + 1));
        lm1.setYukonName("futureProgram1");
        lm1.setYukonID(1);
        programs.add(lm1);

        // Program to execute for today.
        LMProgramDirect lm2 = new LMProgramDirect();
        lm2.setDirectStartTime(new GregorianCalendar());
        lm2.setYukonName("todaysProgram");
        lm2.setYukonID(2);
        programs.add(lm2);

        // Program executed in past.
        LMProgramDirect lm3 = new LMProgramDirect();
        lm3.setDirectStartTime(buildGregorianCalendar(now.getYearOfEra() - 1));
        lm3.setYukonName("pastProgram");
        lm3.setYukonID(3);
        programs.add(lm3);

        LMProgramDirect lm4 = new LMProgramDirect();
        // Future scheduled program
        lm4.setDirectStartTime(buildGregorianCalendar(now.getYearOfEra() + 2));
        lm4.setYukonName("futureProgram2");
        lm4.setYukonID(4);
        programs.add(lm4);
        
        ProgramControlHistory pch1 = new ProgramControlHistory(90, 56);
        pch1.setGearName("Ecobee Program");
        pch1.setStartDateTime(new Date());
        pch1.setStopDateTime(new Date());
        pch1.setKnownGoodStopDateTime(true);
        ProgramControlHistory pch3 = new ProgramControlHistory(90, 56);
        pch3.setGearName("SEP Program");
        pch3.setStartDateTime(new Date());
        pch3.setStopDateTime(new Date());
        pch3.setKnownGoodStopDateTime(true);
        programControlHistory.add(pch1);
        //programControlHistory.add(pch2);
        programControlHistory.add(pch3);
    }
    
    @Test
    public void test_getTodaysProgram() {
        EasyMock.expect(loadControlClientConnection.getAllProgramsSet()).andReturn(programs);
        EasyMock.replay(loadControlClientConnection);
        List<ProgramData> todaysPrograms = ReflectionTestUtils.invokeMethod(programServiceImplTest, "getAllTodaysPrograms");
        // todaysPrograms should contain program which will execute today i.e todaysProgram
        assertTrue(todaysPrograms.size() == 1 && "todaysProgram".equals(todaysPrograms.get(0).getProgramName()));
    }
    
    @Test
    public void test_getTodaysProgram_withEmptyProgramSet() {
        EasyMock.expect(loadControlClientConnection.getAllProgramsSet()).andReturn(emptyPrograms);
        EasyMock.replay(loadControlClientConnection);
        List<ProgramData> todaysPrograms = ReflectionTestUtils.invokeMethod(programServiceImplTest, "getAllTodaysPrograms");
        // todaysPrograms will be empty
        assertTrue(todaysPrograms.size() == 0);
    }
    
    @Test
    public void test_getSchedulePrograms() {
        EasyMock.expect(loadControlClientConnection.getAllProgramsSet()).andReturn(programs);
        EasyMock.replay(loadControlClientConnection);
        List<ProgramData> futurePrograms = ReflectionTestUtils.invokeMethod(programServiceImplTest, "getProgramsScheduledForNextControlDayAfterToday");
        // futurePrograms should contain program which will execute on 1st in future i.e futureProgram1
        assertTrue(futurePrograms.size() == 1 && "futureProgram1".equals(futurePrograms.get(0).getProgramName()));
    }
    
    @Test
    public void test_getSchedulePrograms_withEmptyProgramSet() {
        EasyMock.expect(loadControlClientConnection.getAllProgramsSet()).andReturn(emptyPrograms);
        EasyMock.replay(loadControlClientConnection);
        List<ProgramData> futurePrograms = ReflectionTestUtils.invokeMethod(programServiceImplTest, "getProgramsScheduledForNextControlDayAfterToday");
        // futurePrograms will be empty
        assertTrue(futurePrograms.size() == 0);
    }
    
    private GregorianCalendar buildGregorianCalendar(int year) {
        return new GregorianCalendar(year, 6, 27, 16, 16, 47);
    }

    @Test
    public void test_groupProgramsByProgramHistoryId_positive() {
        Map<Integer, List<ProgramControlHistory>> programs = ReflectionTestUtils.invokeMethod(programServiceImplTest, "groupProgramsByProgramHistoryId", programControlHistory);
        assertTrue(programs.get(90).size() == 2);
    }

    @Test
    public void test_groupProgramsByProgramHistoryId_negative() {
        ProgramControlHistory pch2 = new ProgramControlHistory(23, 56);
        pch2.setGearName("SEP Program");
        pch2.setStartDateTime(new Date());
        pch2.setStopDateTime(new Date());
        pch2.setKnownGoodStopDateTime(false);
        programControlHistory.add(pch2);
        Map<Integer, List<ProgramControlHistory>> programs = ReflectionTestUtils.invokeMethod(programServiceImplTest, "groupProgramsByProgramHistoryId", programControlHistory);
        assertFalse(programs.get(23).size() == 2);
    }

    @Test
    public void test_buildProgramData_with_multiple_gear() {
        ProgramData program = ReflectionTestUtils.invokeMethod(programServiceImplTest, "buildProgramData", programControlHistory);
        assertNotNull(program);
        assertTrue(program.getGears().size() == 2);
    }

    @Test
    public void test_buildProgramData_single_gear() {
        ProgramControlHistory pch1 = new ProgramControlHistory(90, 56);
        pch1.setGearName("Ecobee Program");
        pch1.setStartDateTime(new Date());
        pch1.setStopDateTime(new Date());
        pch1.setKnownGoodStopDateTime(true);
        List<ProgramControlHistory> history = new ArrayList<>();
        history.add(pch1);
        ProgramData program = ReflectionTestUtils.invokeMethod(programServiceImplTest, "buildProgramData", history);
        assertNotNull(program);
        assertTrue(program.getGears().size() == 1);
    }

    @Test
    public void test_groupProgramsByStartDate() {
        List<ProgramData> previousDaysProgram = new ArrayList<>();
        ProgramData pd = new ProgramData.ProgramDataBuilder(12)
                .setStartDateTime(new DateTime())
                .build();
        ProgramData pd2 = new ProgramData.ProgramDataBuilder(15)
                .setStartDateTime(new DateTime())
                .build();
        previousDaysProgram.add(pd);
        previousDaysProgram.add(pd2);
        Map<String, List<ProgramData>> programDataByEventTime = ReflectionTestUtils.invokeMethod(programServiceImplTest, "groupProgramsByStartDate", previousDaysProgram, userContext);
        assertTrue(programDataByEventTime.size() == 1);
    }
}
