package com.cannontech.common.config;

import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.Sets;

public class MasterConfigEnumsTest {

    private static final Set<String> deprecatedKeys = new HashSet<>();
    private static final Set<String> stringKeys = new HashSet<>();
    private static final Set<String> booleanKeys = new HashSet<>();
    private static final Set<String> doubleKeys = new HashSet<>();

    @Before
    public void setup() {
        for (MasterConfigDeprecatedKey key : MasterConfigDeprecatedKey.values()) {
            deprecatedKeys.add(key.name());
        }

        for (MasterConfigString key : MasterConfigString.values()) {
            stringKeys.add(key.name());
        }

        for (MasterConfigBoolean key : MasterConfigBoolean.values()) {
            booleanKeys.add(key.name());
        }

        for (MasterConfigDoubleKeysEnum key : MasterConfigDoubleKeysEnum.values()) {
            doubleKeys.add(key.name());
        }
    }

    @Test
    public void testAllEnumsAreDisjoint() {
        assertDisjoint(deprecatedKeys, stringKeys);
        assertDisjoint(deprecatedKeys, booleanKeys);
        assertDisjoint(deprecatedKeys, doubleKeys);

        assertDisjoint(stringKeys, deprecatedKeys);
        assertDisjoint(stringKeys, booleanKeys);
        assertDisjoint(stringKeys, doubleKeys);

        assertDisjoint(booleanKeys, deprecatedKeys);
        assertDisjoint(booleanKeys, stringKeys);
        assertDisjoint(booleanKeys, doubleKeys);

        assertDisjoint(doubleKeys, deprecatedKeys);
        assertDisjoint(doubleKeys, booleanKeys);
        assertDisjoint(doubleKeys, stringKeys);
    }

    @Test
    public void testTest() {
        // Just making sure our isDisjoint() method does what we think it does
        Set<String> setA = Sets.newHashSet("hello", "world");
        Set<String> setB = Sets.newHashSet("hello", "mars");
        Set<String> setC = Sets.newHashSet("goodbye", "mars");
        Assert.assertFalse("setA and setB are not disjoint. test method isDisjoint() is broken", isDisjoint(setA, setB));
        Assert.assertFalse("setB and setC are not disjoint. test method isDisjoint() is broken", isDisjoint(setB, setC));
        Assert.assertTrue("setA and setC are disjoint. test method isDisjoint() is broken", isDisjoint(setA, setC));
    }

    public static void assertDisjoint(Set<String> setA, Set<String> setB) {
        boolean isDisjoint = isDisjoint(setA, setB);
        if(!isDisjoint) {
            String failMessage = " failed. The following enums are contained in more than one master config enum: ";
            failMessage += StringUtils.join(Sets.intersection(setA, setB), ", ");
            Assert.fail(failMessage);
        }
    }

    public static boolean isDisjoint(Set<String> setA, Set<String> setB) {
        return Sets.intersection(setA, setB).isEmpty();
    }

}
