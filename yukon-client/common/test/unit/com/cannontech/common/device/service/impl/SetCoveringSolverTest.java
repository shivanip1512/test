package com.cannontech.common.device.service.impl;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.junit.Test;

import com.cannontech.amr.deviceread.dao.impl.SetCoveringSolver;
import com.cannontech.common.util.CtiUtilities;

public class SetCoveringSolverTest {

    @Test
    public void testGetMinimalSet1() {
        SomeCommandObj cmdA = new SomeCommandObj(2f, 2);
        SomeCommandObj cmdB = new SomeCommandObj(2f, 3);
        SomeCommandObj cmdC = new SomeCommandObj(4f, 1,2,3);
        SomeCommandObj cmdD = new SomeCommandObj(8f, 4);
        SomeCommandObj cmdE = new SomeCommandObj(1f, 1);
        
        Set<SomeCommandObj> allPossible = CtiUtilities.asSet(cmdA, cmdB, cmdC, cmdD, cmdE);
        Set<Integer> needed = CtiUtilities.asSet(2,3,4);
        
        Set<SomeCommandObj> minimalSet = SetCoveringSolver.getMinimalSet(allPossible, needed);
        
        // ensure minimal set covers all needed points
        Set<Integer> affected = collectAffected(minimalSet);
        assertTrue(affected.containsAll(needed));
        assertFalse(minimalSet.contains(cmdE));
    }
    
    private Set<Integer> collectAffected(Set<SomeCommandObj> commands) {
        Set<Integer> result = new HashSet<Integer>();
        for (SomeCommandObj obj : commands) {
            result.addAll(obj.getAffected());
        }
        return result;
    }
    
    private static class SomeCommandObj implements SetCoveringSolver.HasWeight<Integer> {
        private Set<Integer> affected;
        private float weight;
        
        public SomeCommandObj(float weight, Integer... affected) {
            super();
            this.weight = weight;
            this.affected = new HashSet<Integer>(Arrays.asList(affected));
        }

        public Set<Integer> getAffected() {
            return affected;
        }

        public float getWeight() {
            return weight;
        }
        
        @Override
        public String toString() {
            return affected + "#" + weight;
        }
        
    }
    
    

}
