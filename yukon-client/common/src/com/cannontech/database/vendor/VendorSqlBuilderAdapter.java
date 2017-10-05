package com.cannontech.database.vendor;

import com.cannontech.common.util.SqlBuilder;

/**
 * Provides vendor-specific extensions to the plain SqlBuilder.
 */
public abstract class VendorSqlBuilderAdapter {
    protected SqlBuilder builder;
    public VendorSqlBuilderAdapter(SqlBuilder builder) {
        this.builder = builder;
    }
    protected abstract void left (String dbColumn, int chars);
    protected abstract void right(String dbColumn, int chars);
    public VendorSqlBuilderAdapter prefixesEq(String dbColumn1, String dbColumn2, int chars) {
        left(dbColumn1, chars);
        builder.append("="); 
        left(dbColumn2, chars);
        return this;
    }
    public VendorSqlBuilderAdapter prefixEq(String dbColumn, String value) {
        left(dbColumn, value.length());
        builder.eq(value);
        return this;
    }
    public VendorSqlBuilderAdapter suffixEq(String dbColumn, String value) {
        right(dbColumn, value.length());
        builder.eq(value);
        return this;
    }
    public VendorSqlBuilderAdapter append(Object... args) {
        builder.append(args);
        return this;
    }
    public VendorSqlBuilderAdapter eq(Object argument) {
        builder.eq(argument);
        return this;
    }
    public VendorSqlBuilderAdapter eq_k(int constant) {
        builder.eq_k(constant);
        return this;
    }
    public VendorSqlBuilderAdapter eq_k(Enum<?> constant) {
        builder.eq_k(constant);
        return this;
    }
    public VendorSqlBuilderAdapter in(Iterable<?> list) {
        builder.in(list);
        return this;
    }
    public VendorSqlBuilderAdapter in_k(Iterable<? extends Enum<?>> list) {
        builder.in_k(list);
        return this;
    }
    // TODO - add remaining delegates to SqlBuilder methods as needed
}