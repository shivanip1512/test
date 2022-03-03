package com.cannontech.amr.phaseDetect.test;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Set;

import org.junit.jupiter.api.Test;

import com.cannontech.amr.phaseDetect.data.DetectedPhase;
import com.cannontech.common.model.Phase;
import com.google.common.collect.Sets;

public class DetectedPhaseTest {

    @Test
    public void testConstructDetectedPhase() {
        Set<Phase> phaseSet = Sets.newHashSet();
        
        DetectedPhase detectedPhase = DetectedPhase.getPhase(phaseSet);
        assertEquals("UNKNOWN", detectedPhase.name(), "DetectedPhase should be DetectedPhase.UNKNOWN for an empty set.");
        
        phaseSet.add(Phase.A);
        detectedPhase = DetectedPhase.getPhase(phaseSet);
        assertEquals("A", detectedPhase.name(), "DetectedPhase should be DetectedPhase.A for a set containing only Phase.A.");
        
        phaseSet.remove(Phase.A);
        phaseSet.add(Phase.B);
        detectedPhase = DetectedPhase.getPhase(phaseSet);
        assertEquals("B", detectedPhase.name(), "DetectedPhase should be DetectedPhase.B for a set containing only Phase.B.");
        
        phaseSet.remove(Phase.B);
        phaseSet.add(Phase.C);
        detectedPhase = DetectedPhase.getPhase(phaseSet);
        assertEquals("C", detectedPhase.name(), "DetectedPhase should be DetectedPhase.C for a set containing only Phase.C.");
        
        phaseSet.add(Phase.A);
        phaseSet.add(Phase.B);
        detectedPhase = DetectedPhase.getPhase(phaseSet);
        assertEquals("ABC", detectedPhase.name(), "DetectedPhase should be DetectedPhase.ABC for a set containing Phase.A, Phase.B, and Phase.C.");
        
        phaseSet.remove(Phase.C);
        detectedPhase = DetectedPhase.getPhase(phaseSet);
        assertEquals(detectedPhase.name(), "AB");
        
        phaseSet.remove(Phase.B);
        phaseSet.add(Phase.C);
        detectedPhase = DetectedPhase.getPhase(phaseSet);
        assertEquals("AC", detectedPhase.name(), "DetectedPhase should be DetectedPhase.AC for a set containing Phase.A, and Phase.C.");
        
        phaseSet.remove(Phase.A);
        phaseSet.add(Phase.B);
        detectedPhase = DetectedPhase.getPhase(phaseSet);
        assertEquals("BC", detectedPhase.name(), "DetectedPhase should be DetectedPhase.BC for a set containing Phase.B, and Phase.C.");
    }
    
}
