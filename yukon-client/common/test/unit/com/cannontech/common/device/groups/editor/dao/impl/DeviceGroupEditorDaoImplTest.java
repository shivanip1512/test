package com.cannontech.common.device.groups.editor.dao.impl;

import org.apache.commons.lang3.StringUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class DeviceGroupEditorDaoImplTest {

    @Before
    public void setUp() throws Exception {
    }

    @Test
    public void testGetRelativeGroupSql() {
        DeviceGroupEditorDaoImpl dao = new DeviceGroupEditorDaoImpl();
        String sql;
        for (int count = 0; count < 5; ++count) {
            sql = dao.getRelativeGroupSql(count, false);
            int actual = StringUtils.countMatches(sql, "?");
            Assert.assertEquals("Number of ? must equal count", count, actual);
        }
    }

}
