/**
 * ReadingObject.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.2 May 03, 2005 (02:20:24 EDT) WSDL2Java emitter.
 */

package com.cannontech.multispeak;

public class ReadingObject  extends com.cannontech.multispeak.MspObject  implements java.io.Serializable {
    private com.cannontech.multispeak.ArrayOfSource sourceList;
    private com.cannontech.multispeak.ArrayOfMeterReading readingList;

    public ReadingObject() {
    }

    public ReadingObject(
           com.cannontech.multispeak.ArrayOfSource sourceList,
           com.cannontech.multispeak.ArrayOfMeterReading readingList) {
           this.sourceList = sourceList;
           this.readingList = readingList;
    }


    /**
     * Gets the sourceList value for this ReadingObject.
     * 
     * @return sourceList
     */
    public com.cannontech.multispeak.ArrayOfSource getSourceList() {
        return sourceList;
    }


    /**
     * Sets the sourceList value for this ReadingObject.
     * 
     * @param sourceList
     */
    public void setSourceList(com.cannontech.multispeak.ArrayOfSource sourceList) {
        this.sourceList = sourceList;
    }


    /**
     * Gets the readingList value for this ReadingObject.
     * 
     * @return readingList
     */
    public com.cannontech.multispeak.ArrayOfMeterReading getReadingList() {
        return readingList;
    }


    /**
     * Sets the readingList value for this ReadingObject.
     * 
     * @param readingList
     */
    public void setReadingList(com.cannontech.multispeak.ArrayOfMeterReading readingList) {
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
              this.sourceList.equals(other.getSourceList()))) &&
            ((this.readingList==null && other.getReadingList()==null) || 
             (this.readingList!=null &&
              this.readingList.equals(other.getReadingList())));
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
            _hashCode += getSourceList().hashCode();
        }
        if (getReadingList() != null) {
            _hashCode += getReadingList().hashCode();
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
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ArrayOfSource"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("readingList");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "readingList"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ArrayOfMeterReading"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
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
