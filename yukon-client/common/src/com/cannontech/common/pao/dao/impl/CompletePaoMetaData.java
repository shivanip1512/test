package com.cannontech.common.pao.dao.impl;

import java.beans.PropertyDescriptor;

import com.cannontech.common.pao.annotation.YukonPao;
import com.cannontech.common.pao.annotation.YukonPaoField;
import com.cannontech.common.pao.annotation.YukonPaoPart;

/**
 * This class is used to represent the metadata in the {@link YukonPao} or 
 * {@link YukonPaoPart} annotations of classes used with the PaoPersistenceDao. 
 * 
 * The purpose of this class is to be used in an ordered list of CompletePaoMetaData objects
 * as a way of defining an entire Complete object for database retrieval, insertions, updates,
 * and retrieves. Each CompletePaoMetaData object describes one link in the chain required
 * to perform an operation on a Complete object.
 * 
 * The <code>klass</code> variable stores the class of the annotated object. 
 * The <code>dbTableName</code> specifies the name of the table backing the class (if any.) 
 * The <code>dbIdColumnName</code> specifies the name of the key column which maps back 
 *      to YukonPaObject's paobjectId column. 
 * The <code>fields</code> list contains a {@link PaoFieldMetaData} for each  of the 
 *      {@link YukonPaoField} annotated members of the class. 
 * The <code>partGetter</code> method is used for classes annotated with the YukonPaoPort
 *      annotation contained within a class annotated with the {@link YukonPao} annotation.
 *      This method is invoked to retrieve the {@link YukonPaoPart} object in order to set
 *      or get its data.
 */
public final class CompletePaoMetaData {
    /**
     * The class of the annotated object.
     */
    private final Class<?> klass;

    /**
     * The name of the table backing the class (if any.)
     */
    private final String dbTableName;

    /**
     * The name of the key column which maps back to YukonPaObject's paobjectId column.
     */
    private final String dbIdColumnName;

    /**
     * A {@link PaoFieldMetaData} for each of the {@link YukonPaoField} annotated members of the class.
     */
    private final Iterable<PaoFieldMetaData> fields;

    /**
     * Used to grab the getter and setter for a {@link YukonPaoField} or {@link YukonPaoPart}.
     */
    private final PropertyDescriptor propertyDescriptor;

    public CompletePaoMetaData(Class<?> klass, String dbTableName, String dbIdColumnName,
            Iterable<PaoFieldMetaData> fields, PropertyDescriptor propertyDescriptor) {
        this.klass = klass;
        this.dbTableName = dbTableName;
        this.dbIdColumnName = dbIdColumnName;
        this.fields = fields;
        this.propertyDescriptor = propertyDescriptor;
    }

    public CompletePaoMetaData withDescriptor(PropertyDescriptor propertyDescriptor) {
        return new CompletePaoMetaData(klass, dbTableName, dbIdColumnName, fields, propertyDescriptor);
    }

    public Class<?> getKlass() {
        return klass;
    }

    public String getDbTableName() {
        return dbTableName;
    }

    public String getDbIdColumnName() {
        return dbIdColumnName;
    }

    public Iterable<PaoFieldMetaData> getFields() {
        return fields;
    }

    public PropertyDescriptor getPropertyDescriptor() {
        return propertyDescriptor;
    }

    @Override
    public String toString() {
        return "CompletePaoMetaData [klass=" + klass + ", dbTableName=" + dbTableName + ", dbIdColumnName="
            + dbIdColumnName + ", fields=" + fields + ", propertyDescriptor=" + propertyDescriptor + "]";
    }
}
