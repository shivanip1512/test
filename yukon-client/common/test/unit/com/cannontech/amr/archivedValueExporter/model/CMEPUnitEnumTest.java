package com.cannontech.amr.archivedValueExporter.model;

import static org.junit.Assert.*;
import com.cannontech.common.pao.attribute.model.Attribute;
import com.cannontech.common.pao.attribute.service.IllegalUseOfAttribute;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import org.junit.Test;

public class CMEPUnitEnumTest {
    @Test
    public void findDuplicateAttribute() {
        List<CMEPUnitEnum> enumValues = Arrays.asList(CMEPUnitEnum.values());
        List<Attribute> attributes = new ArrayList<Attribute>();
        for (CMEPUnitEnum enumVal : enumValues) {
            try {
                if (enumVal.getAttribute() != null) {
                    attributes.add(enumVal.getAttribute());
                }
            } catch (IllegalUseOfAttribute e) {
            }
        }
        int duplicateCount = attributes.stream()
                .distinct().collect(Collectors.toList()).size();
        assertTrue("Duplicate attributes in enum", attributes.size() == duplicateCount);
    }
}
