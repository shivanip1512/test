package com.cannontech.database;

import static com.cannontech.core.roleproperties.InputTypeFactory.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.sql.SQLException;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.cannontech.common.pao.PaoClass;
import com.cannontech.common.point.PointQuality;
import com.cannontech.common.util.DatabaseRepresentationSource;
import com.cannontech.core.roleproperties.InputTypeFactory;
import com.cannontech.database.vendor.DatabaseVendor;
import com.cannontech.web.input.type.DateType;
import com.cannontech.web.input.type.DoubleType;

public class YukonResultSetTest {

    private YukonResultSet resultSet = new YukonResultSet(new TestResultSet() {
        @Override
        public String getString(String columnLabel) throws SQLException {
            return columnLabel;
        }
        
        @Override
        public int getInt(String columnLabel) throws SQLException {
        	return Integer.valueOf(columnLabel);
        }
        
        @Override
        public double getDouble(String columnLabel) throws SQLException {
        	return Double.valueOf(columnLabel);
        }
    });
    
    private enum PlanOldEnum {
    	APPLE,
    	ORANGE
    }
    
    private enum EnumWithDatabaseRep implements DatabaseRepresentationSource {
		APPLE,
		BANANA;

		@Override
		public Object getDatabaseRepresentation() {
			return name() + "_DBREP";
		}
    	
    }

    @Test
    public void test_bad_enum() throws SQLException {
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            resultSet.getEnum("MS1234", DatabaseVendor.class);
        });
    }
    
    @Test
    public void test_good_enum() throws SQLException {
        DatabaseVendor enum1 = resultSet.getEnum("MS2008", DatabaseVendor.class);
        assertEquals(DatabaseVendor.MS2008, enum1);
    }
    
    @Test
    public void test_bad_drs_enum() throws SQLException {
        Assertions.assertThrows(SQLException.class, () -> {
            resultSet.getEnum("foo", PaoClass.class);
        });
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
        Assertions.assertThrows(SQLException.class, () -> {
            resultSet.getEnum("456", PointQuality.class);
        });
    }
    
    @Test
    public void test_bad_drs_numeric_enum_2() throws SQLException {
        Assertions.assertThrows(SQLException.class, () -> {
            resultSet.getEnum("NonUpdated", PointQuality.class);
        });
    }

    @Test
    public void test_getObjectByInputType_booleanType() throws SQLException {
    	Boolean trueBoolean = (Boolean) resultSet.getObjectOfInputType("true", booleanType())
    						&& (Boolean) resultSet.getObjectOfInputType("1", booleanType())
    						&& (Boolean) resultSet.getObjectOfInputType("y", booleanType());
    	assertTrue(trueBoolean);

    	Boolean falseBoolean = (Boolean) resultSet.getObjectOfInputType("false", booleanType())
							|| (Boolean) resultSet.getObjectOfInputType("0", booleanType())
							|| (Boolean) resultSet.getObjectOfInputType("n", booleanType());
    	assertFalse(falseBoolean);

    	assertNull(resultSet.getObjectOfInputType("", booleanType()));

    	try {
        	resultSet.getObjectOfInputType("abc", booleanType());
        	fail("'abc' should not return a valid boolean");
    	} catch (IllegalArgumentException e) {
    		/* expected */
    	}
    }

    @Test
    public void test_getObjectByInputType_stringType() throws SQLException {
        String abc = (String) resultSet.getObjectOfInputType("", stringType());
        assertNull(abc);
 
    	abc = (String) resultSet.getObjectOfInputType("abc", stringType());
    	assertTrue(abc.equals("abc"));
    }

    @Test
    public void test_getObjectByInputType_integerType() throws SQLException {
        Integer nullNum = (Integer) resultSet.getObjectOfInputType("", integerType());
        assertNull(nullNum);

    	Integer fortyTwo = (Integer) resultSet.getObjectOfInputType("42", integerType());
    	assertTrue(fortyTwo.equals(42));
    }

    @Test
    public void test_getObjectByInputType_doubleType() throws SQLException {
    	Double fortyTwo = (Double) resultSet.getObjectOfInputType("42.421", new DoubleType());
    	assertTrue(fortyTwo.equals(42.421));

    	fortyTwo = (Double) resultSet.getObjectOfInputType("42", new DoubleType());
    	assertTrue(fortyTwo.equals(42d));
    }

    @Test
    public void test_getObjectByInputType_enumType() throws SQLException {
    	PlanOldEnum apple =  (PlanOldEnum)resultSet.getObjectOfInputType("APPLE", 
    			InputTypeFactory.enumType(PlanOldEnum.class));

    	assertSame(apple, PlanOldEnum.APPLE);

    	try {
    		resultSet.getObjectOfInputType("GRAPE", InputTypeFactory.enumType(PlanOldEnum.class));
        	fail("'GRAPE' shouldn't return a valid PlainOldEnum type");
    	} catch (IllegalArgumentException e) {
    		/* expected */
    	}

    	EnumWithDatabaseRep apple2 = 
    			(EnumWithDatabaseRep)resultSet.getObjectOfInputType("APPLE_DBREP", 
    					InputTypeFactory.enumType(EnumWithDatabaseRep.class));

    	assertSame(apple2, EnumWithDatabaseRep.APPLE);

    	try {
    		resultSet.getObjectOfInputType("APPLE", InputTypeFactory.enumType(EnumWithDatabaseRep.class));
        	fail("'APPLE' doesn't match the dbRepresentationSource and shouldn't return a valid EnumWithDatabaseRep type");
    	} catch (SQLException e) {
    		/* expected */
    	}
    }

    @Test
    public void test_getObjectByInputType_unsupportedType() throws SQLException {
        Assertions.assertThrows(UnsupportedOperationException.class, () -> {
            resultSet.getObjectOfInputType("anything", new DateType());
        });
    }
}
