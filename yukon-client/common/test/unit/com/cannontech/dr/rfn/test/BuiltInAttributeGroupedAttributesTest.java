package com.cannontech.dr.rfn.test;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.junit.Assert;
import org.junit.Test;

import com.cannontech.common.pao.attribute.model.AttributeGroup;
import com.cannontech.common.pao.attribute.model.BuiltInAttribute;
import com.google.common.collect.Lists;

public class BuiltInAttributeGroupedAttributesTest {
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
}
