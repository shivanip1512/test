package com.cannontech.web.dr.widget.service.impl;

import static org.junit.Assert.assertTrue;

import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.easymock.EasyMock;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.springframework.test.util.ReflectionTestUtils;

import com.cannontech.common.program.widget.model.ProgramData;
import com.cannontech.dr.program.service.impl.ProgramServiceImpl;
import com.cannontech.loadcontrol.LoadControlClientConnection;
import com.cannontech.loadcontrol.data.LMProgramBase;
import com.cannontech.loadcontrol.data.LMProgramDirect;
public class ProgramServiceImplTest {

    private LoadControlClientConnection loadControlClientConnection ;
    private ProgramServiceImpl programServiceImplTest;
    
    private Set<LMProgramBase> programs = new HashSet<>(); 
    private Set<LMProgramBase> emptyPrograms = new HashSet<>(); 
    @Before
    public void setUp() throws Exception {
        programServiceImplTest = new ProgramServiceImpl();
        loadControlClientConnection = EasyMock.createMock(LoadControlClientConnection.class);
        ReflectionTestUtils.setField(programServiceImplTest, "loadControlClientConnection",
            loadControlClientConnection);
        
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
    }
    
    @Test
    public void test_getTodaysProgram() {
        EasyMock.expect(loadControlClientConnection.getAllProgramsSet()).andReturn(programs);
        EasyMock.replay(loadControlClientConnection);
        List<ProgramData> todaysPrograms = ReflectionTestUtils.invokeMethod(programServiceImplTest, "getAllTodaysProgram");
        // todaysPrograms should contain program which will execute today i.e todaysProgram
        assertTrue(todaysPrograms.size() == 1 && "todaysProgram".equals(todaysPrograms.get(0).getProgramName()));
    }
    
    @Test
    public void test_getTodaysProgram_withEmptyProgramSet() {
        EasyMock.expect(loadControlClientConnection.getAllProgramsSet()).andReturn(emptyPrograms);
        EasyMock.replay(loadControlClientConnection);
        List<ProgramData> todaysPrograms = ReflectionTestUtils.invokeMethod(programServiceImplTest, "getAllTodaysProgram");
        // todaysPrograms will be empty
        assertTrue(todaysPrograms.size() == 0);
    }
    
    @Test
    public void test_getSchedulePrograms() {
        EasyMock.expect(loadControlClientConnection.getAllProgramsSet()).andReturn(programs);
        EasyMock.replay(loadControlClientConnection);
        List<ProgramData> futurePrograms = ReflectionTestUtils.invokeMethod(programServiceImplTest, "getAllNearestDayScheduledProgram");
        // futurePrograms should contain program which will execute on 1st in future i.e futureProgram1
        assertTrue(futurePrograms.size() == 1 && "futureProgram1".equals(futurePrograms.get(0).getProgramName()));
    }
    
    @Test
    public void test_getSchedulePrograms_withEmptyProgramSet() {
        EasyMock.expect(loadControlClientConnection.getAllProgramsSet()).andReturn(emptyPrograms);
        EasyMock.replay(loadControlClientConnection);
        List<ProgramData> futurePrograms = ReflectionTestUtils.invokeMethod(programServiceImplTest, "getAllNearestDayScheduledProgram");
        // futurePrograms will be empty
        assertTrue(futurePrograms.size() == 0);
    }
    
    private GregorianCalendar buildGregorianCalendar(int year) {
        return new GregorianCalendar(year, 6, 27, 16, 16, 47);
    }
}
