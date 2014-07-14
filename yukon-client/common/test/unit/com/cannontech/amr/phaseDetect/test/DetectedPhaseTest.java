package com.cannontech.amr.phaseDetect.test;

import java.util.Set;

import junit.framework.Assert;

import org.junit.Test;

import com.cannontech.amr.phaseDetect.data.DetectedPhase;
import com.cannontech.common.model.Phase;
import com.google.common.collect.Sets;

public class DetectedPhaseTest {

    @Test
    public void testConstructDetectedPhase() {
        Set<Phase> phaseSet = Sets.newHashSet();
        
        DetectedPhase detectedPhase = DetectedPhase.getPhase(phaseSet);
        Assert.assertEquals("DetectedPhase should be DetectedPhase.UNKNOWN for an empty set.", "UNKNOWN", detectedPhase.name());
        
        phaseSet.add(Phase.A);
        detectedPhase = DetectedPhase.getPhase(phaseSet);
        Assert.assertEquals("DetectedPhase should be DetectedPhase.A for a set containing only Phase.A.", "A", detectedPhase.name());
        
        phaseSet.remove(Phase.A);
        phaseSet.add(Phase.B);
        detectedPhase = DetectedPhase.getPhase(phaseSet);
        Assert.assertEquals("DetectedPhase should be DetectedPhase.B for a set containing only Phase.B.", "B", detectedPhase.name());
        
        phaseSet.remove(Phase.B);
        phaseSet.add(Phase.C);
        detectedPhase = DetectedPhase.getPhase(phaseSet);
        Assert.assertEquals("DetectedPhase should be DetectedPhase.C for a set containing only Phase.C.", "C", detectedPhase.name());
        
        phaseSet.add(Phase.A);
        phaseSet.add(Phase.B);
        detectedPhase = DetectedPhase.getPhase(phaseSet);
        Assert.assertEquals("DetectedPhase should be DetectedPhase.ABC for a set containing Phase.A, Phase.B, and Phase.C.", "ABC", detectedPhase.name());
        
        phaseSet.remove(Phase.C);
        detectedPhase = DetectedPhase.getPhase(phaseSet);
        Assert.assertEquals("DetectedPhase should be DetectedPhase.AB for a set containing Phase.A, and Phase.B.", "AB", detectedPhase.name());
        
        phaseSet.remove(Phase.B);
        phaseSet.add(Phase.C);
        detectedPhase = DetectedPhase.getPhase(phaseSet);
        Assert.assertEquals("DetectedPhase should be DetectedPhase.AC for a set containing Phase.A, and Phase.C.", "AC", detectedPhase.name());
        
        phaseSet.remove(Phase.A);
        phaseSet.add(Phase.B);
        detectedPhase = DetectedPhase.getPhase(phaseSet);
        Assert.assertEquals("DetectedPhase should be DetectedPhase.BC for a set containing Phase.B, and Phase.C.", "BC", detectedPhase.name());
    }
    
}
