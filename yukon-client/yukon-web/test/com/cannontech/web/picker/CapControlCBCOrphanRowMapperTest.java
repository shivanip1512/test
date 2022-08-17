package com.cannontech.web.picker;

import static org.junit.Assert.*;

import org.junit.Test;
import com.cannontech.capcontrol.service.impl.CbcHelperServiceImpl;
import com.cannontech.common.util.SqlFragmentSource;
import com.cannontech.database.vendor.DatabaseVendor;
import com.cannontech.database.vendor.VendorSpecificSqlBuilderFactory;
import com.google.common.collect.Iterables;

public class CapControlCBCOrphanRowMapperTest {

    private static final String prefixExpected =             
            "Query:  WITH OverridePointName AS ("
            + " SELECT dcdm.DeviceID as PAObjectID, dcci_pt.ItemValue as POINTNAME, dcci_at.ItemValue as Attribute"
            + " FROM DeviceConfigurationDeviceMap dcdm"
            + " JOIN DeviceConfigCategoryMap dccm ON dcdm.DeviceConfigurationId=dccm.DeviceConfigurationId"
            + " JOIN DeviceConfigCategory dcc ON dccm.DeviceConfigCategoryId=dcc.DeviceConfigCategoryId"
            + " JOIN DeviceConfigCategoryItem dcci_at ON dcc.DeviceConfigCategoryId=dcci_at.DeviceConfigCategoryId"
            + " JOIN DeviceConfigCategoryItem dcci_pt ON dcc.DeviceConfigCategoryId=dcci_pt.DeviceConfigCategoryId"
            + " WHERE dcc.CategoryType = ?";
        private static final String suffixExpected =             
              " AND dcci_at.ItemValue = ? )"
            + " SELECT * FROM ("
            + " SELECT y.PAOName, y.PAObjectID, y.Type, opn.POINTNAME"
            + " FROM YukonPAObject y"
            + " JOIN OverridePointName opn ON opn.PAObjectID=y.PAObjectID"
            + " JOIN POINT p ON y.PAObjectID=p.PAObjectID AND opn.POINTNAME=p.POINTNAME"
            + " WHERE y.Type in ( 'CBC Logical' )"
            + " UNION"
            + " SELECT y.PAOName, y.PAObjectID, y.Type, COALESCE(op.POINTNAME, p.POINTNAME) AS PointName"
            + " FROM YukonPAObject y"
            + " LEFT JOIN POINT p ON y.PAObjectID=p.PAObjectID AND p.PointOffset = 1 AND p.PointType = 'Status'"
            + " LEFT JOIN OverridePointName opn ON opn.PAObjectID=y.PAObjectID"
            + " LEFT JOIN POINT op ON y.PAObjectID=op.PAObjectID AND opn.POINTNAME=op.POINTNAME"
            + " WHERE y.Type in ( 'CBC Expresscom' , 'CBC Versacom' , 'CBC 7010' , 'CBC 7011' , 'CBC 7012' , 'CBC 7020' , 'CBC 7022' , 'CBC 7023' , 'CBC 7024' , 'CBC 8020' , 'CBC 8024' , 'CBC DNP' , 'CBC FP-2800' )"
            + " AND (op.POINTNAME IS NOT NULL OR p.POINTNAME IS NOT NULL) ) results "
            + " Arguments: [cbcAttributeMapping, attributeMappings., attribute, attributeMappings., pointName, CONTROL_POINT]";
    
    private CapControlCBCOrphanRowMapper createMapper(DatabaseVendor dbVendor) throws Exception {
        VendorSpecificSqlBuilderFactory vssbf = new VendorSpecificSqlBuilderFactory();
        vssbf.setDatabaseConnectionVendorResolver(() -> dbVendor);
        vssbf.init();  //  reinitialize to get the new DB vendor

        return new CapControlCBCOrphanRowMapper(new CbcHelperServiceImpl(vssbf));
    }
        
    @Test
    public void test_getOracleSql() throws Exception {
        CapControlCBCOrphanRowMapper mapper = createMapper(Iterables.getFirst(DatabaseVendor.getOracleDatabases(), null));
        
        SqlFragmentSource oracleSql = mapper.getBaseQuery();

        assertEquals( 
            prefixExpected 
            + " AND substr(dcci_at.ItemName,1,18) = ?"
            + " AND substr(dcci_at.ItemName,-9) = ?"
            + " AND substr(dcci_pt.ItemName,1,18) = ?"
            + " AND substr(dcci_pt.ItemName,-9) = ?"
            + " AND substr(dcci_at.ItemName,1,20) = substr(dcci_pt.ItemName,1,20)"
            + suffixExpected,
            oracleSql.getDebugSql());
    }

    @Test
    public void test_getMicrosoftSql() throws Exception {
        CapControlCBCOrphanRowMapper mapper = createMapper(Iterables.getFirst(DatabaseVendor.getMsDatabases(), null));
        
        SqlFragmentSource microsoftSql = mapper.getBaseQuery();

        assertEquals(
            prefixExpected
            + " AND left(dcci_at.ItemName,18) = ?"
            + " AND right(dcci_at.ItemName,9) = ?"
            + " AND left(dcci_pt.ItemName,18) = ?"
            + " AND right(dcci_pt.ItemName,9) = ?"
            + " AND left(dcci_at.ItemName,20) = left(dcci_pt.ItemName,20)"
            + suffixExpected,
            microsoftSql.getDebugSql());
    }
}
