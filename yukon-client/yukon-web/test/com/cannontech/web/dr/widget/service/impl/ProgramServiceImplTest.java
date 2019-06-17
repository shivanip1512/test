package com.cannontech.web.dr.widget.service.impl;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
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
import com.cannontech.loadcontrol.dao.LmProgramGearHistory;
import com.cannontech.loadcontrol.data.LMProgramBase;
import com.cannontech.loadcontrol.data.LMProgramDirect;
import com.cannontech.user.SimpleYukonUserContext;
public class ProgramServiceImplTest {

    private LoadControlClientConnection loadControlClientConnection ;
    private ProgramServiceImpl programServiceImplTest;
    private SimpleYukonUserContext userContext;
    
    private Set<LMProgramBase> programs = new HashSet<>();
    private Set<LMProgramBase> emptyPrograms = new HashSet<>();
    private List<LmProgramGearHistory> programGearHistory = new ArrayList<>();
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
        
        LmProgramGearHistory lpgh1 = new LmProgramGearHistory();
        lpgh1.setProgramGearHistoryId(120);
        lpgh1.setProgramHistoryId(90);
        lpgh1.setGearName("Ecobee Program");
        lpgh1.setAction("Gear Change");
        lpgh1.setGearId(15);
        LmProgramGearHistory lpgh2 = new LmProgramGearHistory();
        lpgh2.setProgramGearHistoryId(46);
        lpgh2.setAction("Start");
        lpgh2.setProgramHistoryId(23);
        lpgh2.setGearName("SEP Pragram");
        lpgh2.setGearId(37);
        LmProgramGearHistory lpgh3 = new LmProgramGearHistory();
        lpgh3.setProgramGearHistoryId(153);
        lpgh3.setAction("Stop");
        lpgh3.setProgramHistoryId(90);
        lpgh3.setGearName("SEP Pragram");
        lpgh3.setGearId(37);
        programGearHistory.add(lpgh1);
        programGearHistory.add(lpgh2);
        programGearHistory.add(lpgh3);
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
        Map<Integer, List<LmProgramGearHistory>> programs = ReflectionTestUtils.invokeMethod(programServiceImplTest, "groupProgramsByProgramHistoryId", programGearHistory);
        assertTrue(programs.get(90).size() == 2);
    }

    @Test
    public void test_groupProgramsByProgramHistoryId_negative() {
        Map<Integer, List<LmProgramGearHistory>> programs = ReflectionTestUtils.invokeMethod(programServiceImplTest, "groupProgramsByProgramHistoryId", programGearHistory);
        assertFalse(programs.get(23).size() == 2);
    }

    @Test
    public void test_buildProgramData_with_multiple_gear() {
        ProgramData program = ReflectionTestUtils.invokeMethod(programServiceImplTest, "buildProgramData", programGearHistory, userContext);
        assertNotNull(program);
        assertTrue(program.getGears().size() == 2);
    }

    @Test
    public void test_buildProgramData_single_gear() {
        LmProgramGearHistory lpgh1 = new LmProgramGearHistory();
        lpgh1.setProgramGearHistoryId(120);
        lpgh1.setProgramHistoryId(90);
        lpgh1.setGearName("Ecobee Program");
        lpgh1.setAction("Gear Change");
        lpgh1.setGearId(15);
        List<LmProgramGearHistory> history = new ArrayList<>();
        history.add(lpgh1);
        ProgramData program = ReflectionTestUtils.invokeMethod(programServiceImplTest, "buildProgramData", history, userContext);
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
