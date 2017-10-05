package com.cannontech.database.vendor;

import com.cannontech.common.util.SqlBuilder;

public class MicrosoftSqlBuilderAdapter extends VendorSqlBuilderAdapter {
    public MicrosoftSqlBuilderAdapter(SqlBuilder builder) {
        super(builder);
    }
    @Override
    public void left(String dbColumn, int chars) {
        builder.append("left(" + dbColumn + "," + chars + ")");
    }
    @Override
    public void right(String dbColumn, int chars) {
        builder.append("right(" + dbColumn + "," + chars + ")");
    }
}