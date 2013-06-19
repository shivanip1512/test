package com.cannontech.common.pao;

import static org.junit.Assert.fail;

import java.util.Iterator;
import java.util.Set;

import org.junit.Test;
import org.springframework.util.StringUtils;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;

public class PaoCategoryTest {

    private static final ImmutableSet<PaoCategory> allCategories =
            ImmutableSet.of(PaoCategory.DEVICE,
                            PaoCategory.ROUTE,
                            PaoCategory.PORT,
                            PaoCategory.CUSTOMER,
                            PaoCategory.CAPCONTROL,
                            PaoCategory.LOADMANAGEMENT);

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

    @Test
    public void testGetPaoCategoryString() {
        Iterator<PaoCategory> deviceIter = allCategories.iterator();
        Set<PaoCategory> illegalTypes = Sets.newHashSet();
        PaoCategory nextElement = null;
        String deviceString = null;
        
        while (deviceIter.hasNext()) {
            nextElement = deviceIter.next();
            deviceString = nextElement.getDbString();
            if (nextElement.getPaoCategoryId() != PaoCategory.getPaoCategory(deviceString)) {
                illegalTypes.add(nextElement);
            }
        }
        if (!illegalTypes.isEmpty()) {
            fail("PaoCategory: " + StringUtils.arrayToCommaDelimitedString(illegalTypes.toArray()) +
                 " does not match PaoCategory IDs.");
        }
    }

}
