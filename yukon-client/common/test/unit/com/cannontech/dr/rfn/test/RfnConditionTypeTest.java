package com.cannontech.dr.rfn.test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import java.lang.reflect.InvocationTargetException;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;

import com.cannontech.amr.rfn.message.event.RfnConditionType;
import com.cannontech.amr.rfn.service.processor.RfnArchiveRequestProcessor;
import com.cannontech.amr.rfn.service.processor.impl.RfnOutageBlinkEventArchiveRequestProcessor;
import com.cannontech.amr.rfn.service.processor.impl.RfnOutageEventArchiveRequestProcessor;
import com.cannontech.amr.rfn.service.processor.impl.RfnPowerFailureEventArchiveRequestProcessor;
import com.cannontech.amr.rfn.service.processor.impl.RfnRemoteMeterConfigurationFailureEventArchiveRequestProcessor;
import com.cannontech.amr.rfn.service.processor.impl.RfnRemoteMeterConfigurationFinishedEventArchiveRequestProcessor;
import com.cannontech.amr.rfn.service.processor.impl.RfnRestoreBlinkEventArchiveRequestProcessor;
import com.cannontech.amr.rfn.service.processor.impl.RfnRestoreEventArchiveRequestProcessor;
import com.cannontech.amr.rfn.service.processor.impl.RfnReverseRotationEventArchiveRequestProcessor;
import com.cannontech.amr.rfn.service.processor.impl.RfnTamperDetectEventArchiveRequestProcessor;
import com.cannontech.common.pao.attribute.model.AttributeGroup;
import com.cannontech.common.pao.attribute.model.BuiltInAttribute;
import com.google.common.collect.ImmutableMap;

public class RfnConditionTypeTest {
    //  these are special types handled by an RfnArchiveRequestProcessor instead of mapping directly to a BuiltInAttribute:
    //    the OUTAGE/RESTORE conditions go to the OUTAGE_STATUS/RFN_OUTAGE_COUNT attributes
    //    the REMOTE_METER_CONFIGURATION_FAILURE/FINISHED conditions go to the METER_PROGRAMMING_ATTEMPTED attribute
    //    the POWER_FAILURE condition goes to the POWER_FAIL_FLAG attribute
    //    the REVERSE_ROTATION condition goes to the REVERSE_POWER_FLAG attribute
    //    the TAMPER_DETECT condition goes to the TAMPER_FLAG attribute
    private static final Map<RfnConditionType, Class<? extends RfnArchiveRequestProcessor>> specialTypes = ImmutableMap
        .<RfnConditionType, Class<? extends RfnArchiveRequestProcessor>>builder()
        .put(RfnConditionType.OUTAGE,
                RfnOutageEventArchiveRequestProcessor.class)
        .put(RfnConditionType.OUTAGE_BLINK,
                RfnOutageBlinkEventArchiveRequestProcessor.class)
        .put(RfnConditionType.REMOTE_METER_CONFIGURATION_FAILURE,
                RfnRemoteMeterConfigurationFailureEventArchiveRequestProcessor.class)
        .put(RfnConditionType.REMOTE_METER_CONFIGURATION_FINISHED,
                RfnRemoteMeterConfigurationFinishedEventArchiveRequestProcessor.class)
        .put(RfnConditionType.RESTORE,
                RfnRestoreEventArchiveRequestProcessor.class)
        .put(RfnConditionType.RESTORE_BLINK,
                RfnRestoreBlinkEventArchiveRequestProcessor.class)
        .put(RfnConditionType.POWER_FAILURE,
                RfnPowerFailureEventArchiveRequestProcessor.class)
        .put(RfnConditionType.REVERSE_ROTATION,
                RfnReverseRotationEventArchiveRequestProcessor.class)
        .put(RfnConditionType.TAMPER_DETECT,
                RfnTamperDetectEventArchiveRequestProcessor.class)
        .build();

    @Test
    public void rfnArchiveRequestProcessorConditionTypeTest() throws InstantiationException, IllegalAccessException,
            InvocationTargetException, NoSuchMethodException {
        for (var entry : specialTypes.entrySet()) {
            var expectedConditionType = entry.getKey();
            var processorClass = entry.getValue();
            var processor = processorClass.getConstructor().newInstance();

            assertEquals(expectedConditionType,
                    processor.getRfnConditionType(), processorClass.getSimpleName() + ".getConditionType()");
        }
    }

    /*
     * This test ensures that every value of the RfnConditionType enum either
     * appears in the event grouped attribute lists OR is handled by a dedicated event processor.
     */
    @Test
    public void rfnConditionTypeAttributeExistsTest() {
        Map<AttributeGroup, Set<BuiltInAttribute>> eventGroupedAttributes = BuiltInAttribute.getRfnEventGroupedAttributes();

        Set<String> groupedAttributeNames = eventGroupedAttributes.values().stream()
                .flatMap(Set::stream)
                .map(BuiltInAttribute::name)
                .collect(Collectors.toSet());

        for (RfnConditionType rfnConditionType : RfnConditionType.values()) {
            if (specialTypes.containsKey(rfnConditionType)) {
                continue;
            }
                
            boolean found = groupedAttributeNames.contains(rfnConditionType.name());

            if (!found) {
                fail("The rfnConditionType: " + rfnConditionType.name() + 
                        " is in not in BuiltInAttribute rfnEventGroupAttributes(). "
                                + "\nEither:"
                                + "\n  1. add a matching BuiltInAttribute with an AttributeGroup.RFN_*_EVENT, or"
                                + "\n  2. if it has a special processor, add it to the specialTypes map in RfnConditionTypeTest.java");
            }
        }
    }
}
