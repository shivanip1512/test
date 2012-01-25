package com.cannontech.common.pao.service.impl;

import java.lang.reflect.Method;

import com.google.common.collect.Lists;

public class DbTableMapping {
    private Class<?> klass;
    private String dbTableName;
    private String dbIdColumnName;
    private Iterable<DbFieldMapping> fields = Lists.newArrayList();
    private Method partGetter;

    public DbTableMapping() {
    }

    public DbTableMapping(DbTableMapping toCopy, Method partGetter) {
        this.klass = toCopy.klass;
        this.dbTableName = toCopy.dbTableName;
        this.dbIdColumnName = toCopy.dbIdColumnName;
        this.fields = toCopy.fields;
        this.partGetter = partGetter;
    }

    @Override
    public String toString() {
        return "DbTableMapping [klass=" + klass + ", dbTableName=" + dbTableName
               + ", dbIdColumnName=" + dbIdColumnName + ", fields=" + fields + "]";
    }

    public Class<?> getKlass() {
        return klass;
    }

    public void setKlass(Class<?> klass) {
        this.klass = klass;
    }

    public String getDbTableName() {
        return dbTableName;
    }

    public void setDbTableName(String dbTableName) {
        this.dbTableName = dbTableName;
    }

    public String getDbIdColumnName() {
        return dbIdColumnName;
    }

    public void setDbIdColumnName(String dbIdColumnName) {
        this.dbIdColumnName = dbIdColumnName;
    }

    public Iterable<DbFieldMapping> getFields() {
        return fields;
    }

    public void setFields(Iterable<DbFieldMapping> fields) {
        this.fields = fields;
    }

    public Method getPartGetter() {
        return partGetter;
    }

    public void setPartGetter(Method partGetter) {
        this.partGetter = partGetter;
    }
}