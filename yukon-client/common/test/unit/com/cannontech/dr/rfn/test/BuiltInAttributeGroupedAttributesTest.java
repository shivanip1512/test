package com.cannontech.dr.rfn.test;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import com.cannontech.common.pao.attribute.model.AttributeGroup;
import com.cannontech.common.pao.attribute.model.BuiltInAttribute;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
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
        ImmutableMap<AttributeGroup, ImmutableSet<BuiltInAttribute>> allGroupedAttributes = 
                BuiltInAttribute.getAllGroupedAttributes();
        List<BuiltInAttribute> allAttributesInAGroup = Lists.newArrayList();

        for (AttributeGroup group : allGroupedAttributes.keySet()) {
            for (BuiltInAttribute attribute : allGroupedAttributes.get(group)) {
                    allAttributesInAGroup.add(attribute);
            }
        }
        for (BuiltInAttribute attribute : BuiltInAttribute.values()) {
            if (!allAttributesInAGroup.contains(attribute)) {
                Assert.fail("The attribute: " + attribute.name() + " is in BuiltInAttribute.values() " + 
                		"but it is not in any attribute group. Add it to an existing group or create a new " +
                        "attribute group for it.");
            }
        }
    }
}
