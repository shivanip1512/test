package com.cannontech.common.pao;

import static org.junit.jupiter.api.Assertions.fail;

import java.util.EnumSet;
import java.util.Iterator;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.util.StringUtils;

import com.google.common.collect.Sets;

public class PaoCategoryTest {

    private Set<PaoCategory> allCategories;

    @BeforeEach
    public void setup() {
        allCategories = EnumSet.allOf(PaoCategory.class);
    }

    @Test
    public void testGetPaoCategoryInt() {
        Iterator<PaoCategory> deviceIter = allCategories.iterator();
        Set<PaoCategory> illegalTypes = Sets.newHashSet();
        PaoCategory nextElement = null;
        Integer deviceId = null;

        while (deviceIter.hasNext()) {
            nextElement = deviceIter.next();
            deviceId = nextElement.getPaoCategoryId();
            if (!nextElement.getDbString().equals(PaoCategory.getPaoCategory(deviceId))) {
                illegalTypes.add(nextElement);
            }
        }
        if (!illegalTypes.isEmpty()) {
            fail("PaoCategory: " + StringUtils.arrayToCommaDelimitedString(illegalTypes.toArray()) +
                 " does not match PaoCategory Strings.");
        }
    }
}
