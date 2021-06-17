package com.cannontech.amr.phaseDetect.test;

import com.cannontech.common.model.Phase;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import org.junit.jupiter.api.Test;

import com.cannontech.amr.phaseDetect.data.PhaseDetectVoltageReading;
import com.cannontech.amr.phaseDetect.data.UndefinedPhaseException;
import com.cannontech.amr.phaseDetect.service.impl.PhaseDetectServiceImpl;

public class PhaseDetectServiceImplTest {
    
    @Test
    public void testParsePhase() {
        String porterMsg = "";
        try{
            PhaseDetectServiceImpl.parsePhase(porterMsg);
            fail("parsePhase should throw UndefinedPhaseException.");
        } catch (UndefinedPhaseException e){}
        
        porterMsg = "Phase = \nA";
        try{
            PhaseDetectServiceImpl.parsePhase(porterMsg);
            fail("parsePhase should throw UndefinedPhaseException for 'Phase = \nA'.");
        } catch (UndefinedPhaseException e){}
        
        porterMsg = "Phase = A";
        try{
            Phase phase = PhaseDetectServiceImpl.parsePhase(porterMsg);
            assertEquals("A", phase.name(), "Phase should be A.");
        } catch (UndefinedPhaseException e){
            fail("parsePhase should not throw UndefinedPhaseException for 'Phase = A'.");
        }
        
        porterMsg = "Phase = B";
        try{
            Phase phase = PhaseDetectServiceImpl.parsePhase(porterMsg);
            assertEquals("B", phase.name(), "Phase should be B.");
        } catch (UndefinedPhaseException e){
            fail("parsePhase should not throw UndefinedPhaseException for 'Phase = B'.");
        }
        
        porterMsg = "Phase = C";
        try{
            Phase phase = PhaseDetectServiceImpl.parsePhase(porterMsg);
            assertEquals("C", phase.name(), "Phase should be C.");
        } catch (UndefinedPhaseException e){
            fail("parsePhase should not throw UndefinedPhaseException for 'Phase = C'.");
        }
    }
    
    @Test
    public void testParseVoltageReadingPhase() {
        String porterMsg = "sw cart 410IL 1 / Phase = A @ 10/09/2009 15:00:57\nFirst Interval Voltage: 256.4 / Last Interval Voltage: 257.6";
        PhaseDetectVoltageReading reading = PhaseDetectServiceImpl.parseVoltageReading(porterMsg);
        double delta = 1.2;
        double initial = 256.4;
        double last = 257.6;
        assertEquals(delta, reading.getDelta(), "Delta should be 1.2.");
        assertEquals(initial, reading.getInitial(), "Initial should be 256.4.");
        assertEquals(last, reading.getLast(), "Last should be 257.6.");
        
    }
}
