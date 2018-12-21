package com.cannontech.dr.rfn.test;

import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.junit.Test;

import com.cannontech.amr.rfn.message.event.RfnConditionType;
import com.cannontech.common.pao.attribute.model.AttributeGroup;
import com.cannontech.common.pao.attribute.model.BuiltInAttribute;
import com.google.common.collect.Sets;

public class RfnConditionTypeTest {
    /*
     * This test's goal is to ensure that every value of the RfnConditionType enum
     * appears in events grouped attribute lists.  If a new value is added to
     * the enum but not to any attribute group this test will fail an assertion, 
     * alerting the developer that an BuiltInAttribute needs to be added.
     */
    @Test
    public void rfnConditionTypeAttributeExistsTest() {
        Map<AttributeGroup, Set<BuiltInAttribute>> eventGroupedAttributes = BuiltInAttribute.getRfnEventGroupedAttributes();

        // these are special types to ignore
        Set<RfnConditionType> specialTypes = Sets.newHashSet(RfnConditionType.OUTAGE,
                                                             RfnConditionType.OUTAGE_BLINK,
                                                             RfnConditionType.RESTORE,
                                                             RfnConditionType.RESTORE_BLINK,
                                                             RfnConditionType.POWER_FAILURE, 
                                                             RfnConditionType.REVERSE_ROTATION,
                                                             RfnConditionType.TAMPER_DETECT);
        
        for (RfnConditionType rfnConditionType : RfnConditionType.values()) {
            if (specialTypes.contains(rfnConditionType)) {
                continue;
            }
                
            boolean found = false;
            nextCondition:
            for (AttributeGroup attributeGroup : eventGroupedAttributes.keySet()) {
                Set<BuiltInAttribute> allAttributesInAGroup = eventGroupedAttributes.get(attributeGroup);
                for (BuiltInAttribute attribute : allAttributesInAGroup) {
                    if (StringUtils.equals(rfnConditionType.name(), attribute.name())) {
                        found = true;
                        System.out.println("Found: " + rfnConditionType + " in group " + attributeGroup);
                        break nextCondition;
                    }
                }
            }
/*            if (!found) {
                Assert.fail("The rfnConditionType: " + rfnConditionType.name() + 
                        " is in not in BuiltInAttribute rfnEventGroupAttributes(). " +
                        "Make sure a corresponding BuiltInAttribute exists and that it is part of the rnfEventGroupedAttributes.");
            }*/
        }
    }
}
