package com.cannontech.common.config;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.junit.Assert;
import org.junit.Test;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

public class MasterConfigEnumsTest {

    private static final Set<Class<? extends Enum<?>>> keyClasses = ImmutableSet.of(
                 MasterConfigDeprecatedKey.class,
                 MasterConfigString.class,
                 MasterConfigBoolean.class,
                 MasterConfigDouble.class,
                 MasterConfigLicenseKey.class);

    @Test
    public void testAllEnumsAreDisjoint() {
        var enumNames = Maps.newHashMap(
                            Maps.asMap(keyClasses, keyClass -> Arrays.stream(keyClass.getEnumConstants())
                                                                     .map(Enum::name)
                                                                     .collect(Collectors.toSet())));
        for (var myClass : keyClasses) {
            //  Remove the current class's enum names from the map...
            var myNames = enumNames.remove(myClass);
            
            //  ... and test them against each of the the remaining sets of names.
            //  This tests all possible combinations of 2 from the set.
            enumNames.forEach((theirClass, theirNames) ->
                assertDisjoint(myNames, theirNames));
        }
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
