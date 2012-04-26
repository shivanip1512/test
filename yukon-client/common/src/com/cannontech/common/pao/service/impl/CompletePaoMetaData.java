package com.cannontech.common.pao.service.impl;

import java.beans.PropertyDescriptor;

import com.cannontech.common.pao.annotation.YukonPao;
import com.cannontech.common.pao.annotation.YukonPaoField;
import com.cannontech.common.pao.annotation.YukonPaoPart;
import com.cannontech.common.pao.service.PaoPersistenceService;
import com.google.common.collect.Lists;

/**
 * This class is used to represent the metadata in the {@link YukonPao} or 
 * {@link YukonPaoPart} annotations of classes used with the {@link PaoPersistenceService}. 
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
/*package*/ class CompletePaoMetaData {
    
    /**
     * Stores the class of the annotated object. 
     */
    private Class<?> klass;
    
    /**
     * Specifies the name of the table backing the class (if any.) 
     */
    private String dbTableName;
    
    /**
     * Specifies the name of the key column which maps back to YukonPaObject's paobjectId column. 
     */
    private String dbIdColumnName;
    
    /**
     * Contains a {@link PaoFieldMetaData} for each of the {@link YukonPaoField} annotated 
     * members of the class.
     */
    private Iterable<PaoFieldMetaData> fields = Lists.newArrayList();
    
    /**
     * Used to grab the getter and setter for a {@link YukonPaoField} or {@link YukonPaoPart}.
     */
    private PropertyDescriptor propertyDescriptor;

    public CompletePaoMetaData() {
    }

    public CompletePaoMetaData(CompletePaoMetaData toCopy, PropertyDescriptor propertyDescriptor) {
        this.klass = toCopy.klass;
        this.dbTableName = toCopy.dbTableName;
        this.dbIdColumnName = toCopy.dbIdColumnName;
        this.fields = toCopy.fields;
        this.propertyDescriptor = propertyDescriptor;
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

    public Iterable<PaoFieldMetaData> getFields() {
        return fields;
    }

    public void setFields(Iterable<PaoFieldMetaData> fields) {
        this.fields = fields;
    }
    
    public void setPropertyDescriptor(PropertyDescriptor propertyDescriptor) {
        this.propertyDescriptor = propertyDescriptor;
    }
    
    public PropertyDescriptor getPropertyDescriptor() {
        return propertyDescriptor;
    }

    @Override
    public String toString() {
        return "CompletePaoMetaData [klass=" + klass + ", dbTableName=" + dbTableName
               + ", dbIdColumnName=" + dbIdColumnName + ", fields=" + fields
               + ", propertyDescriptor=" + propertyDescriptor + "]";
    }
}