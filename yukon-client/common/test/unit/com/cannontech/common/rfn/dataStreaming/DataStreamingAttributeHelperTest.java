package com.cannontech.common.rfn.dataStreaming;

import java.util.Collection;
import java.util.EnumMap;
import java.util.Set;

import org.junit.Assert;
import org.junit.Test;

import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.attribute.model.BuiltInAttribute;
import com.cannontech.common.rfn.dataStreaming.DataStreamingAttributeHelper.DataStreamingPaoAttributes;
import com.google.common.collect.Maps;

public class DataStreamingAttributeHelperTest {

    @Test
    public void testConsistentPaoTypes() {
        EnumMap<PaoType, Set<BuiltInAttribute>> typeToSupportedAttributes = Maps.newEnumMap(PaoType.class);
        for (DataStreamingPaoAttributes dspa : DataStreamingPaoAttributes.values()) {
            Collection<BuiltInAttribute> existingAttributes = typeToSupportedAttributes.get(dspa.getPaoType());
            if (existingAttributes != null) {
                Assert.assertEquals("Pao type " + dspa.getPaoType() + " not consistent when adding " + dspa, existingAttributes, dspa.getSupportedAttributes());
            } else {
                typeToSupportedAttributes.put(dspa.getPaoType(), dspa.getSupportedAttributes());
            }
        }
    }
}
