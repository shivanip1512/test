package com.cannontech.common.pao.attribute.model;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.context.NoSuchMessageException;

import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.common.pao.attribute.model.AttributeGroup;
import com.cannontech.common.pao.attribute.model.BuiltInAttribute;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

public class BuiltInAttributeTest {
    /*
     * This test's goal is to ensure that every value of the BuiltInAttributes enum
     * appears in one of the grouped attribute lists.  If a new value is added to
     * the enum but not to any attribute group this test will fail an assertion, 
     * alerting the developer that the attribute needs to be added to an attribute
     * group.
     */
    @Test
    public void attributeGroupMembershipTest() {
        Map<AttributeGroup, Set<BuiltInAttribute>> allGroupedAttributes = BuiltInAttribute.getAllGroupedAttributes();
        List<BuiltInAttribute> allAttributesInAGroup = allGroupedAttributes.values().stream()
                .flatMap(Set::stream)
                .collect(Collectors.toList());

        for (BuiltInAttribute attribute : BuiltInAttribute.values()) {
            if (!allAttributesInAGroup.contains(attribute)) {
                Assert.fail("The attribute: " + attribute.name() + " is in BuiltInAttribute.values() " + 
                		"but it is not in any attribute group. Add it to an existing group or create a new " +
                        "attribute group for it.");
            }
        }
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
        
        Assert.assertEquals(expected, attributes);
    }
}
