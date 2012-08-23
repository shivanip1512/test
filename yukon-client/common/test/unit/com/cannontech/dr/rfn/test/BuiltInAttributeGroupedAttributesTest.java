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

    @Test
    public void attributeGroupMembershipTest() {
        ImmutableMap<AttributeGroup, ImmutableSet<BuiltInAttribute>> allGroupedAttributes = BuiltInAttribute.getAllGroupedAttributes();
        List<BuiltInAttribute> allAttributesInAGroup = Lists.newArrayList();

        for (AttributeGroup group : allGroupedAttributes.keySet()) {
            for (BuiltInAttribute attribute : allGroupedAttributes.get(group)) {
                    allAttributesInAGroup.add(attribute);
            }
        }
        for (BuiltInAttribute attribute : BuiltInAttribute.values()) {
            if (!allAttributesInAGroup.contains(attribute)) {
                Assert.fail("The attribute: " + attribute.name() + " in BuiltInAttribute.values() is not in any attribute group.");
            }
        }
    }
}
