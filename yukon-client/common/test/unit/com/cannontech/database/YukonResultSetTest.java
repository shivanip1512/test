package com.cannontech.database;

import static org.junit.Assert.*;

import java.sql.SQLException;

import org.junit.Test;

import com.cannontech.common.pao.PaoClass;
import com.cannontech.common.point.PointQuality;
import com.cannontech.database.vendor.DatabaseVendor;

public class YukonResultSetTest {

    private YukonResultSet resultSet = new YukonResultSet(new TestResultSet() {
        @Override
        public String getString(String columnLabel) throws SQLException {
            return columnLabel;
        }
    });

    @Test
    public void test_bad_enum() throws SQLException {
        try {
            resultSet.getEnum("MS1234", DatabaseVendor.class);
            fail();
        } catch (Exception e) {
        }
    }
    
    @Test
    public void test_good_enum() throws SQLException {
        DatabaseVendor enum1 = resultSet.getEnum("MS2008", DatabaseVendor.class);
        assertEquals(DatabaseVendor.MS2008, enum1);
    }
    
    @Test
    public void test_bad_drs_enum() throws SQLException {
        try {
            resultSet.getEnum("foo", PaoClass.class);
            fail();
        } catch (Exception e) {
        }
    }

    @Test
    public void test_good_drs_enum() throws SQLException {
        PaoClass enum1 = resultSet.getEnum("METER", PaoClass.class);
        assertEquals(PaoClass.METER, enum1);
    }
    
    @Test
    public void test_good_drs_numeric_enum() throws SQLException {
        PointQuality enum1 = resultSet.getEnum("3", PointQuality.class);
        assertEquals(PointQuality.NonUpdated, enum1);
    }
    
    @Test
    public void test_bad_drs_numeric_enum_1() throws SQLException {
        try {
            resultSet.getEnum("456", PointQuality.class);
            fail();
        } catch (Exception e) {
        }
    }
    
    @Test
    public void test_bad_drs_numeric_enum_2() throws SQLException {
        try {
            resultSet.getEnum("NonUpdated", PointQuality.class);
            fail();
        } catch (Exception e) {
        }
    }
    
}
