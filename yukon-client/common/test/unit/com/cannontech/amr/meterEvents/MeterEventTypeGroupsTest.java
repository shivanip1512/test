package com.cannontech.amr.meterEvents;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Set;

import org.junit.jupiter.api.Test;

import com.cannontech.amr.meter.service.impl.MeterEventStatusTypeGroupings;
import com.cannontech.common.pao.attribute.model.BuiltInAttribute;

public class MeterEventTypeGroupsTest {
    
    @Test
    public void checkAllSets() {
        boolean result = true;
        try {
            checkSetAgainstAll(MeterEventStatusTypeGroupings.getGeneral());
            checkSetAgainstAll(MeterEventStatusTypeGroupings.getHardware());
            checkSetAgainstAll(MeterEventStatusTypeGroupings.getTamper());
            checkSetAgainstAll(MeterEventStatusTypeGroupings.getMetering());
            checkSetAgainstAll(MeterEventStatusTypeGroupings.getOutage());

            checkNoOverlap(MeterEventStatusTypeGroupings.getGeneral());
            checkNoOverlap(MeterEventStatusTypeGroupings.getHardware());
            checkNoOverlap(MeterEventStatusTypeGroupings.getTamper());
            checkNoOverlap(MeterEventStatusTypeGroupings.getMetering());
            checkNoOverlap(MeterEventStatusTypeGroupings.getOutage());
        } catch (IllegalArgumentException e) {
            result = false;
        }
        
        assertTrue(result);
    }

    private static void checkSetAgainstAll(Set<BuiltInAttribute> attrs) {
        for (BuiltInAttribute attr : attrs) {
            if (!MeterEventStatusTypeGroupings.getAll().contains(attr)) {
                throw new IllegalArgumentException("Attribute " + attr + " not contained in ALL set");
            }
        }
    }

    private static void checkNoOverlap(Set<BuiltInAttribute> checkAgainst) {
        checkNoOverlap(checkAgainst, MeterEventStatusTypeGroupings.getGeneral());
        checkNoOverlap(checkAgainst, MeterEventStatusTypeGroupings.getHardware());
        checkNoOverlap(checkAgainst, MeterEventStatusTypeGroupings.getTamper());
        checkNoOverlap(checkAgainst, MeterEventStatusTypeGroupings.getMetering());
        checkNoOverlap(checkAgainst, MeterEventStatusTypeGroupings.getOutage());
    }
    
    private static void checkNoOverlap(Set<BuiltInAttribute> one, Set<BuiltInAttribute> two) {
        if (one == two) {
            return;
        }
        for (BuiltInAttribute attr : one) {
            if (two.contains(attr)) {
                throw new IllegalArgumentException("Attribute " + attr + " contained in more than one set");
            }
        }
    }
    
}
