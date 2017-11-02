package com.cannontech.capcontrol.service.impl;

import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.attribute.model.BuiltInAttribute;
import com.cannontech.database.data.point.PointType;
import com.cannontech.database.vendor.DatabaseVendor;
import com.cannontech.database.vendor.MicrosoftSqlBuilderAdapter;
import com.cannontech.database.vendor.OracleSqlBuilderAdapter;
import com.cannontech.database.vendor.VendorSpecificSqlBuilder;
import com.cannontech.database.vendor.VendorSqlBuilderAdapter;

final class CbcQueryHelper {

    private static final Map<Boolean, List<PaoType>> isLogicalCbc = 
            PaoType.getCbcTypes().stream()
                    .collect(Collectors.partitioningBy(
                            PaoType::isLogicalCBC));

    public static void appendOrphanQuery(VendorSpecificSqlBuilder builder) {
        appendVendorSpecificQueries(builder, ba -> appendOrphanQuery(ba));
    }

    public static void appendDeviceControlPointQuery(Integer controlDeviceID, VendorSpecificSqlBuilder builder) {
        appendVendorSpecificQueries(builder, ba -> appendDeviceControlPointQuery(controlDeviceID, ba));
    }

    private static void appendVendorSpecificQueries(VendorSpecificSqlBuilder builder, Consumer<VendorSqlBuilderAdapter> queryAppender) {
        VendorSqlBuilderAdapter mssqlBuilder  = new MicrosoftSqlBuilderAdapter(builder.buildFor(DatabaseVendor.getMsDatabases()));
        VendorSqlBuilderAdapter oracleBuilder = new OracleSqlBuilderAdapter   (builder.buildFor(DatabaseVendor.getOracleDatabases()));
        
        queryAppender.accept(mssqlBuilder);
        queryAppender.accept(oracleBuilder);
    }
    
    private static void appendOrphanQuery(VendorSqlBuilderAdapter builder) {
        appendWithOverridePointName(builder);
        builder.append("SELECT *");
        builder.append("FROM (");
        LogicalCbc.appendOrphanQuery(builder);
        builder.append("UNION");
        ConcreteCbc.appendOrphanQuery(builder);
        builder.append(") results");
    }

    private static void appendDeviceControlPointQuery(Integer controlDeviceID, VendorSqlBuilderAdapter builder) {
        appendWithOverridePointName(builder);
        builder.append("SELECT POINTID");
        builder.append("FROM (");
        LogicalCbc.appendControlPointQuery(builder);
        builder.append("UNION");
        ConcreteCbc.appendControlPointQuery(builder);
        builder.append(") results");
        builder.append("WHERE");
        builder.append(    "PAObjectId").eq(controlDeviceID);
    }

    private static void appendWithOverridePointName(VendorSqlBuilderAdapter builder) {
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
        builder.append(   "AND dcci_at.ItemValue").eq(BuiltInAttribute.CONTROL_POINT);
        builder.append(")");
    }

    private static class LogicalCbc {
        private static void appendControlPointQuery(VendorSqlBuilderAdapter builder) {
            builder.append("SELECT");
            builder.append(    "y.PAObjectID, p.POINTID");
            appendFromWhere(builder);
        }
    
        private static void appendOrphanQuery(VendorSqlBuilderAdapter builder) {
            builder.append("SELECT");
            builder.append(    "y.PAOName, y.PAObjectID, y.Type, opn.POINTNAME");
            appendFromWhere(builder);
        }
    
        private static void appendFromWhere(VendorSqlBuilderAdapter builder) {
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
        }
    }

    private static class ConcreteCbc {
        private static void appendControlPointQuery(VendorSqlBuilderAdapter builder) {
            builder.append("SELECT"); 
            builder.append(    "y.PAObjectID, COALESCE(op.POINTID, p.POINTID) AS PointId");
            appendFromWhere(builder);
        }
        
        private static void appendOrphanQuery(VendorSqlBuilderAdapter builder) {
            builder.append("SELECT"); 
            builder.append(    "y.PAOName, y.PAObjectID, y.Type, COALESCE(op.POINTNAME, p.POINTNAME) AS PointName");
            appendFromWhere(builder);
        }
    
        private static void appendFromWhere(VendorSqlBuilderAdapter builder) {
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
        }
    }
}
