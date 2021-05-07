package com.cannontech.common.device.groups.editor.dao.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class DeviceGroupEditorDaoImplTest {

    @BeforeEach
    public void setUp() throws Exception {
    }

    @Test
    public void testGetRelativeGroupSql() {
        DeviceGroupEditorDaoImpl dao = new DeviceGroupEditorDaoImpl();
        String sql;
        for (int count = 0; count < 5; ++count) {
            sql = dao.getRelativeGroupSql(count, false);
            int actual = StringUtils.countMatches(sql, "?");
            assertEquals(count, actual, "Number of ? must equal count");
        }
    }

}
