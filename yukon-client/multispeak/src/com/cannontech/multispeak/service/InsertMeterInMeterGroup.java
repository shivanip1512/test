/**
 * InsertMeterInMeterGroup.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.2 May 03, 2005 (02:20:24 EDT) WSDL2Java emitter.
 */

package com.cannontech.multispeak.service;

public class InsertMeterInMeterGroup  implements java.io.Serializable {
    private com.cannontech.multispeak.service.ArrayOfString meterNumbers;
    private java.lang.String meterGroupID;

    public InsertMeterInMeterGroup() {
    }

    public InsertMeterInMeterGroup(
           com.cannontech.multispeak.service.ArrayOfString meterNumbers,
           java.lang.String meterGroupID) {
           this.meterNumbers = meterNumbers;
           this.meterGroupID = meterGroupID;
    }


    /**
     * Gets the meterNumbers value for this InsertMeterInMeterGroup.
     * 
     * @return meterNumbers
     */
    public com.cannontech.multispeak.service.ArrayOfString getMeterNumbers() {
        return meterNumbers;
    }


    /**
     * Sets the meterNumbers value for this InsertMeterInMeterGroup.
     * 
     * @param meterNumbers
     */
    public void setMeterNumbers(com.cannontech.multispeak.service.ArrayOfString meterNumbers) {
        this.meterNumbers = meterNumbers;
    }


    /**
     * Gets the meterGroupID value for this InsertMeterInMeterGroup.
     * 
     * @return meterGroupID
     */
    public java.lang.String getMeterGroupID() {
        return meterGroupID;
    }


    /**
     * Sets the meterGroupID value for this InsertMeterInMeterGroup.
     * 
     * @param meterGroupID
     */
    public void setMeterGroupID(java.lang.String meterGroupID) {
        this.meterGroupID = meterGroupID;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof InsertMeterInMeterGroup)) return false;
        InsertMeterInMeterGroup other = (InsertMeterInMeterGroup) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.meterNumbers==null && other.getMeterNumbers()==null) || 
             (this.meterNumbers!=null &&
              this.meterNumbers.equals(other.getMeterNumbers()))) &&
            ((this.meterGroupID==null && other.getMeterGroupID()==null) || 
             (this.meterGroupID!=null &&
              this.meterGroupID.equals(other.getMeterGroupID())));
        __equalsCalc = null;
        return _equals;
    }

    private boolean __hashCodeCalc = false;
    public synchronized int hashCode() {
        if (__hashCodeCalc) {
            return 0;
        }
        __hashCodeCalc = true;
        int _hashCode = 1;
        if (getMeterNumbers() != null) {
            _hashCode += getMeterNumbers().hashCode();
        }
        if (getMeterGroupID() != null) {
            _hashCode += getMeterGroupID().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(InsertMeterInMeterGroup.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">InsertMeterInMeterGroup"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("meterNumbers");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "meterNumbers"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ArrayOfString"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("meterGroupID");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "meterGroupID"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
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
