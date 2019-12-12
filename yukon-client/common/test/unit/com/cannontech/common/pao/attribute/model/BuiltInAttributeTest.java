package com.cannontech.common.pao.attribute.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.Properties;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang3.tuple.Pair;
import org.junit.Test;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.context.NoSuchMessageException;

import com.cannontech.common.i18n.MessageSourceAccessor;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

public class BuiltInAttributeTest {
    /*
     * This test's goal is to ensure that every value of the BuiltInAttributes enum
     * appears in one of the grouped attribute lists.  If a new value is added to
     * the enum but not to any attribute group this test will fail an assertion, 
     * alerting the developer that the attribute needs to be added to an attribute
     * group.
     */
    @Test
    public void testAttributeGroupMembership() {
        Set<BuiltInAttribute> allAttributesInAGroup = 
                BuiltInAttribute.getAllGroupedAttributes().values().stream()
                    .flatMap(Set::stream)
                    .collect(Collectors.toSet());

        Set<BuiltInAttribute> allAttributes = 
                EnumSet.allOf(BuiltInAttribute.class);
        
        Sets.difference(allAttributes, allAttributesInAGroup).stream()
            .map(Enum::name)
            .reduce((a,b) -> a + "," + b)
            .ifPresent(attributes -> 
                fail("[" + attributes + "] in BuiltInAttribute.values() " + 
                     "but not included in any attribute group. Add to an existing or new group."));
    }
    
    @Test
    public void testSort() {
        //  Sort the attributes by their reversed codes - essentially by the last character of the enum name.
        MessageSourceAccessor accessor = new MessageSourceAccessor(null, null) {
            @Override
            public String getMessage(MessageSourceResolvable resolvable) throws NoSuchMessageException {
                return new StringBuilder(resolvable.getCodes()[0]).reverse().toString();
            }
        };
        var attributes = Lists.newArrayList(BuiltInAttribute.ALTERNATE_MODE_ENTRY,
                                            BuiltInAttribute.AVERAGE_VOLTAGE_PHASE_C,
                                            BuiltInAttribute.CURRENT_ANGLE_PHASE_A,
                                            BuiltInAttribute.BLINK_COUNT,
                                            BuiltInAttribute.CALC_MEMORY_UTILIZATION);

        var expected = ImmutableList.of(BuiltInAttribute.CURRENT_ANGLE_PHASE_A,
                                        BuiltInAttribute.AVERAGE_VOLTAGE_PHASE_C,
                                        BuiltInAttribute.CALC_MEMORY_UTILIZATION,
                                        BuiltInAttribute.BLINK_COUNT,
                                        BuiltInAttribute.ALTERNATE_MODE_ENTRY); 
        
        BuiltInAttribute.sort(attributes, accessor);
        
        assertEquals(expected, attributes);
    }

    @Test
    public void testPointEntries() throws Exception {
        var pointXml = getClass().getClassLoader().getResourceAsStream("com/cannontech/yukon/common/point.xml");

        var pointEntries = new Properties();
        pointEntries.loadFromXML(pointXml);

        for (var attr : BuiltInAttribute.values()) {
            var pointEntry = pointEntries.get(attr.getFormatKey());
            assertNotNull("No key for " + attr, pointEntry);
            validateNamingConvention((String) pointEntry);
        }
    }

    private static void validateNamingConvention(String pointName) {
        // Quoting from BuiltInAttribute.java:
        // [Net|Sum] [Delivered|Received] [Coincident] [Cumulative] [Peak] [UOM] [(Quadrants # #)] [Frozen] [Rate #|Phase X|Channel #]

        String[][] orderedExclusiveStrings = {
                { "Net", "Sum" },
                { "Delivered", "Received" },
                { "Coincident " },
                { "Cumulative" },
                { "Peak" },
                { "Demand", "kW", "kVA", "Volt" },  // TODO - add complete UOM list?
                { "Quadrant" },
                { "Frozen", "Daily" },
                { "Rate", "Phase", "Channel" } };

        Arrays.stream(pointName.split(" at "))  //  split for "X at Y" coincidental points
            .forEach(subName ->
                Arrays.stream(orderedExclusiveStrings)
                    .map(Arrays::stream)
                    .flatMap(exclusiveStrings -> exclusiveStrings.map(s -> Pair.of(s, subName.indexOf(s)))
                            .filter(p -> p.getValue() >= 0)
                            .reduce((p1, p2) -> {
                                fail("Multiple exclusive strings found in point description for " + pointName + ": " + p1 + p2);
                                return p1;  //  Will never return, since the failure will happen
                            })
                            .stream())
                    .reduce((pos1, pos2) -> {
                        assertTrue("Reversed tokens for " + pointName + ": " + pos1 + pos2, pos1.getValue() < pos2.getValue());
                        return pos2;
                    }));
    }

    @Test
    public void testNamingConventions() {
        Arrays.stream(BuiltInAttribute.values())
                .map(BuiltInAttribute::getDescription)
                .forEach(BuiltInAttributeTest::validateNamingConvention);
    }
}
