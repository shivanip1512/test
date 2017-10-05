package com.cannontech.database.vendor;

import com.cannontech.common.util.SqlBuilder;

public class OracleSqlBuilderAdapter extends VendorSqlBuilderAdapter {
    public OracleSqlBuilderAdapter(SqlBuilder builder) {
        super(builder);
    }
    @Override
    public void left(String dbColumn, int chars) {
        builder.append("substr(" + dbColumn + ",1," + chars + ")");
    }
    @Override
    public void right(String dbColumn, int chars) {
        builder.append("substr(" + dbColumn + ",-" + chars + ")");
    }
}