package com.cannontech.amr.phaseDetect.test;

import junit.framework.Assert;

import org.junit.Test;

import com.cannontech.common.model.Phase;
import com.cannontech.amr.phaseDetect.data.PhaseDetectVoltageReading;
import com.cannontech.amr.phaseDetect.data.UndefinedPhaseException;
import com.cannontech.amr.phaseDetect.service.impl.PhaseDetectServiceImpl;

public class PhaseDetectServiceImplTest {
    
    @Test
    public void testParsePhase() {
        String porterMsg = "";
        try{
            PhaseDetectServiceImpl.parsePhase(porterMsg);
            Assert.fail("parsePhase should throw UndefinedPhaseException.");
        } catch (UndefinedPhaseException e){}
        
        porterMsg = "Phase = \nA";
        try{
            PhaseDetectServiceImpl.parsePhase(porterMsg);
            Assert.fail("parsePhase should throw UndefinedPhaseException for 'Phase = \nA'.");
        } catch (UndefinedPhaseException e){}
        
        porterMsg = "Phase = A";
        try{
            Phase phase = PhaseDetectServiceImpl.parsePhase(porterMsg);
            Assert.assertEquals("Phase should be A.", "A", phase.name());
        } catch (UndefinedPhaseException e){
            Assert.fail("parsePhase should not throw UndefinedPhaseException for 'Phase = A'.");
        }
        
        porterMsg = "Phase = B";
        try{
            Phase phase = PhaseDetectServiceImpl.parsePhase(porterMsg);
            Assert.assertEquals("Phase should be B.", "B", phase.name());
        } catch (UndefinedPhaseException e){
            Assert.fail("parsePhase should not throw UndefinedPhaseException for 'Phase = B'.");
        }
        
        porterMsg = "Phase = C";
        try{
            Phase phase = PhaseDetectServiceImpl.parsePhase(porterMsg);
            Assert.assertEquals("Phase should be C.", "C", phase.name());
        } catch (UndefinedPhaseException e){
            Assert.fail("parsePhase should not throw UndefinedPhaseException for 'Phase = C'.");
        }
    }
    
    @Test
    public void testParseVoltageReadingPhase() {
        String porterMsg = "sw cart 410IL 1 / Phase = A @ 10/09/2009 15:00:57\nFirst Interval Voltage: 256.4 / Last Interval Voltage: 257.6";
        PhaseDetectVoltageReading reading = PhaseDetectServiceImpl.parseVoltageReading(porterMsg);
        double delta = 1.2;
        double initial = 256.4;
        double last = 257.6;
        Assert.assertEquals("Delta should be 1.2.", delta, reading.getDelta());
        Assert.assertEquals("Initial should be 256.4.", initial, reading.getInitial());
        Assert.assertEquals("Last should be 257.6.", last, reading.getLast());
        
    }
}
