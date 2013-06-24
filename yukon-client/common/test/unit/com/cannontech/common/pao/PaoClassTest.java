package com.cannontech.common.pao;

import static org.junit.Assert.fail;

import java.util.EnumSet;
import java.util.Iterator;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.springframework.util.StringUtils;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;

public class PaoClassTest {
    
    private Set<PaoClass> allClasses;
    
    @Before
    public void setup() {
        allClasses = EnumSet.allOf(PaoClass.class);
    }

    @Test
    public void testGetPaoClassInt() {
        Iterator<PaoClass> deviceIter = allClasses.iterator();
        Set<PaoClass> illegalTypes = Sets.newHashSet();
        PaoClass nextElement = null;
        Integer deviceId = null;

        while (deviceIter.hasNext()) {
            nextElement = deviceIter.next();
            deviceId = nextElement.getPaoClassId();
            if (!nextElement.getDbString().equals(PaoClass.getPaoClass(deviceId))) {
                illegalTypes.add(nextElement);
            }
        }
        if (!illegalTypes.isEmpty()) {
            fail("PaoClass: " + StringUtils.arrayToCommaDelimitedString(illegalTypes.toArray()) +
                 " does not match PaoClass Strings.");
        }
    }
    
    @Test
    public void testGetPaoClassString() {
        Iterator<PaoClass> deviceIter = allClasses.iterator();
        Set<PaoClass> illegalTypes = Sets.newHashSet();
        PaoClass nextElement = null;
        String deviceId = null;
        
        while (deviceIter.hasNext()) {
            nextElement = deviceIter.next();
            deviceId = nextElement.getDbString();
            if (nextElement.getPaoClassId() != (PaoClass.getPaoClass(deviceId))) {
                illegalTypes.add(nextElement);
            }
        }
        if (!illegalTypes.isEmpty()) {
            fail("PaoClass: " + StringUtils.arrayToCommaDelimitedString(illegalTypes.toArray()) +
                 " does not match PaoClass IDs.");
        }        
    }

}
