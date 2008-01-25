/**
 * ReadingObject.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Dec 28, 2007 (09:53:13 CST) WSDL2Java emitter.
 */

package com.cannontech.multispeak.deploy.service;

public class ReadingObject  extends com.cannontech.multispeak.deploy.service.MspObject  implements java.io.Serializable {
    private com.cannontech.multispeak.deploy.service.Source[] sourceList;

    private com.cannontech.multispeak.deploy.service.MeterReading[] readingList;

    public ReadingObject() {
    }

    public ReadingObject(
           java.lang.String objectID,
           com.cannontech.multispeak.deploy.service.Action verb,
           java.lang.String errorString,
           java.lang.String replaceID,
           java.lang.String utility,
           com.cannontech.multispeak.deploy.service.Extensions extensions,
           java.lang.String comments,
           com.cannontech.multispeak.deploy.service.ExtensionsItem[] extensionsList,
           com.cannontech.multispeak.deploy.service.Source[] sourceList,
           com.cannontech.multispeak.deploy.service.MeterReading[] readingList) {
        super(
            objectID,
            verb,
            errorString,
            replaceID,
            utility,
            extensions,
            comments,
            extensionsList);
        this.sourceList = sourceList;
        this.readingList = readingList;
    }


    /**
     * Gets the sourceList value for this ReadingObject.
     * 
     * @return sourceList
     */
    public com.cannontech.multispeak.deploy.service.Source[] getSourceList() {
        return sourceList;
    }


    /**
     * Sets the sourceList value for this ReadingObject.
     * 
     * @param sourceList
     */
    public void setSourceList(com.cannontech.multispeak.deploy.service.Source[] sourceList) {
        this.sourceList = sourceList;
    }


    /**
     * Gets the readingList value for this ReadingObject.
     * 
     * @return readingList
     */
    public com.cannontech.multispeak.deploy.service.MeterReading[] getReadingList() {
        return readingList;
    }


    /**
     * Sets the readingList value for this ReadingObject.
     * 
     * @param readingList
     */
    public void setReadingList(com.cannontech.multispeak.deploy.service.MeterReading[] readingList) {
        this.readingList = readingList;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof ReadingObject)) return false;
        ReadingObject other = (ReadingObject) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = super.equals(obj) && 
            ((this.sourceList==null && other.getSourceList()==null) || 
             (this.sourceList!=null &&
              java.util.Arrays.equals(this.sourceList, other.getSourceList()))) &&
            ((this.readingList==null && other.getReadingList()==null) || 
             (this.readingList!=null &&
              java.util.Arrays.equals(this.readingList, other.getReadingList())));
        __equalsCalc = null;
        return _equals;
    }

    private boolean __hashCodeCalc = false;
    public synchronized int hashCode() {
        if (__hashCodeCalc) {
            return 0;
        }
        __hashCodeCalc = true;
        int _hashCode = super.hashCode();
        if (getSourceList() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getSourceList());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getSourceList(), i);
                if (obj != null &&
                    !obj.getClass().isArray()) {
                    _hashCode += obj.hashCode();
                }
            }
        }
        if (getReadingList() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getReadingList());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getReadingList(), i);
                if (obj != null &&
                    !obj.getClass().isArray()) {
                    _hashCode += obj.hashCode();
                }
            }
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(ReadingObject.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "readingObject"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("sourceList");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "sourceList"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "source"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        elemField.setItemQName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "source"));
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("readingList");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "readingList"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "meterReading"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        elemField.setItemQName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "meterReading"));
        typeDesc.addFieldDesc(elemField);
    }

    /**
     * Return type metadata object
     */
    public static org.apache.axis.description.TypeDesc getTypeDesc() {
        return typeDesc;
    }

    /**
     * Get Custom Serializer
     */
    public static org.apache.axis.encoding.Serializer getSerializer(
           java.lang.String mechType, 
           java.lang.Class _javaType,  
           javax.xml.namespace.QName _xmlType) {
        return 
          new  org.apache.axis.encoding.ser.BeanSerializer(
            _javaType, _xmlType, typeDesc);
    }

    /**
     * Get Custom Deserializer
     */
    public static org.apache.axis.encoding.Deserializer getDeserializer(
           java.lang.String mechType, 
           java.lang.Class _javaType,  
           javax.xml.namespace.QName _xmlType) {
        return 
          new  org.apache.axis.encoding.ser.BeanDeserializer(
            _javaType, _xmlType, typeDesc);
    }

}
