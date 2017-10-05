package com.cannontech.web.picker;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.cannontech.common.bulk.filter.AbstractRowMapperWithBaseQuery;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.util.SimpleSqlFragment;
import com.cannontech.common.util.SqlBuilder;
import com.cannontech.common.util.SqlFragmentSource;
import com.cannontech.database.YukonResultSet;
import com.cannontech.database.data.point.PointType;
import com.cannontech.database.vendor.DatabaseVendor;
import com.cannontech.database.vendor.MicrosoftSqlBuilderAdapter;
import com.cannontech.database.vendor.OracleSqlBuilderAdapter;
import com.cannontech.database.vendor.VendorSpecificSqlBuilder;
import com.cannontech.database.vendor.VendorSpecificSqlBuilderFactory;
import com.cannontech.database.vendor.VendorSqlBuilderAdapter;

class CapControlCBCOrphanRowMapper extends AbstractRowMapperWithBaseQuery<Map<String, Object>> {

    VendorSpecificSqlBuilderFactory vendorSpecificSqlBuilderFactory;

    public CapControlCBCOrphanRowMapper(VendorSpecificSqlBuilderFactory vendorSpecificSqlBuilderFactory) {
        this.vendorSpecificSqlBuilderFactory = vendorSpecificSqlBuilderFactory;
    }

    private static void appendBaseQuery(VendorSqlBuilderAdapter builder) {
        Map<Boolean, List<PaoType>> isLogicalCbc = PaoType.getCbcTypes().stream().collect(Collectors.partitioningBy(PaoType::isLogicalCBC));

        builder.append("WITH OverridePointName AS (");
        builder.append("SELECT");
        builder.append(   "dcdm.DeviceID as PAObjectID, dcci_pt.ItemValue as POINTNAME");
        builder.append("FROM");
        builder.append(   "DeviceConfigurationDeviceMap dcdm"); 
        builder.append(   "JOIN DeviceConfigCategoryMap dccm");
        builder.append(       "ON dcdm.DeviceConfigurationId=dccm.DeviceConfigurationId");
        builder.append(   "JOIN DeviceConfigCategory dcc"); 
        builder.append(       "ON dccm.DeviceConfigCategoryId=dcc.DeviceConfigCategoryId");
        builder.append(   "JOIN DeviceConfigCategoryItem dcci_at"); 
        builder.append(       "ON dcc.DeviceConfigCategoryId=dcci_at.DeviceConfigCategoryId");
        builder.append(   "JOIN DeviceConfigCategoryItem dcci_pt"); 
        builder.append(       "ON dcc.DeviceConfigCategoryId=dcci_pt.DeviceConfigCategoryId");
        builder.append("WHERE");
        builder.append(   "dcc.CategoryType").eq("cbcAttributeMapping");
        builder.append(   "AND").prefixEq("dcci_at.ItemName", "attributeMappings.");
        builder.append(   "AND").suffixEq("dcci_at.ItemName", "attribute");
        builder.append(   "AND").prefixEq("dcci_pt.ItemName", "attributeMappings.");
        builder.append(   "AND").suffixEq("dcci_pt.ItemName", "pointName");
        // Supports up to 99 attributes - compares "attributeMappings.N." or "attributeMappings.NN" 
        builder.append(   "AND").prefixesEq("dcci_at.ItemName", "dcci_pt.ItemName", 20);  
        builder.append(   "AND dcci_at.ItemValue").eq("CONTROL_POINT");
        builder.append(")");
        
        builder.append("SELECT * FROM (");

        builder.append("SELECT"); 
        builder.append(    "y.PAOName, y.PAObjectID, y.Type, opn.POINTNAME");
        builder.append("FROM"); 
        builder.append(    "YukonPAObject y");
        builder.append(        "JOIN OverridePointName opn"); 
        builder.append(            "ON opn.PAObjectID=y.PAObjectID");
        builder.append(        "JOIN POINT p"); 
        builder.append(            "ON CONCAT(CONCAT(CONCAT('*Logical<',y.PAOName),'> '), opn.POINTNAME)=p.POINTNAME");
        builder.append(        "JOIN YukonPAObject rtu"); 
        builder.append(            "ON p.PAObjectID=rtu.PAObjectID");
        builder.append("WHERE");
        builder.append(    "y.Type").in_k(isLogicalCbc.get(true));
        builder.append(    "AND rtu.type").eq_k(PaoType.RTU_DNP);

        builder.append("UNION");

        builder.append("SELECT"); 
        builder.append(    "y.PAOName, y.PAObjectID, y.Type, COALESCE(op.POINTNAME, p.POINTNAME) AS PointName");
        builder.append("FROM"); 
        builder.append(    "YukonPAObject y"); 
        builder.append(    "LEFT JOIN POINT p"); 
        builder.append(        "ON y.PAObjectID=p.PAObjectID");
        builder.append(    "LEFT JOIN OverridePointName opn"); 
        builder.append(        "ON opn.PAObjectID=y.PAObjectID");
        builder.append(    "LEFT JOIN POINT op");
        builder.append(        "ON y.PAObjectID=op.PAObjectID");
        builder.append(        "AND opn.POINTNAME=op.POINTNAME");
        builder.append("WHERE");
        builder.append(    "y.Type").in(isLogicalCbc.get(false));
        builder.append(    "AND p.PointOffset").eq_k(1);
        builder.append(    "AND p.PointType").eq_k(PointType.Status);
        builder.append(    "AND (op.POINTNAME IS NOT NULL OR p.POINTNAME IS NOT NULL)");

        builder.append(") results");
    }

    @Override
    public SqlFragmentSource getBaseQuery() {
        VendorSpecificSqlBuilder builder = vendorSpecificSqlBuilderFactory.create();

        SqlBuilder mssqlBuilder  = builder.buildFor(DatabaseVendor.getMsDatabases());
        SqlBuilder oracleBuilder = builder.buildFor(DatabaseVendor.getOracleDatabases());
        
        appendBaseQuery(new MicrosoftSqlBuilderAdapter(mssqlBuilder));
        appendBaseQuery(new OracleSqlBuilderAdapter(oracleBuilder));
        
        return builder;
    }
    
    @Override
    public SqlFragmentSource getOrderBy() {
        return new SimpleSqlFragment("ORDER BY PaoName");
    }

    @Override
    public boolean needsWhere() {
        return true;
    }

    @Override
    public Map<String, Object> mapRow(YukonResultSet rs) throws SQLException {
        Map<String, Object> map = new HashMap<>();
        map.put("paoName", rs.getString("PaoName"));
        map.put("pointName", rs.getString("PointName"));
        map.put("type", rs.getString("Type"));
        map.put("paoId", rs.getString("PaObjectId"));
        return map;
    }
}