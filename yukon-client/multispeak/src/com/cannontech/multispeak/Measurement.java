/**
 * Measurement.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.2 May 03, 2005 (02:20:24 EDT) WSDL2Java emitter.
 */

package com.cannontech.multispeak;

public class Measurement  extends com.cannontech.multispeak.MspObject  implements java.io.Serializable {
    private java.lang.String measurementDeviceID;
    private com.cannontech.multispeak.ArrayOfMeasurementType measurementTypeList;

    public Measurement() {
    }

    public Measurement(
           java.lang.String measurementDeviceID,
           com.cannontech.multispeak.ArrayOfMeasurementType measurementTypeList) {
           this.measurementDeviceID = measurementDeviceID;
           this.measurementTypeList = measurementTypeList;
    }


    /**
     * Gets the measurementDeviceID value for this Measurement.
     * 
     * @return measurementDeviceID
     */
    public java.lang.String getMeasurementDeviceID() {
        return measurementDeviceID;
    }


    /**
     * Sets the measurementDeviceID value for this Measurement.
     * 
     * @param measurementDeviceID
     */
    public void setMeasurementDeviceID(java.lang.String measurementDeviceID) {
        this.measurementDeviceID = measurementDeviceID;
    }


    /**
     * Gets the measurementTypeList value for this Measurement.
     * 
     * @return measurementTypeList
     */
    public com.cannontech.multispeak.ArrayOfMeasurementType getMeasurementTypeList() {
        return measurementTypeList;
    }


    /**
     * Sets the measurementTypeList value for this Measurement.
     * 
     * @param measurementTypeList
     */
    public void setMeasurementTypeList(com.cannontech.multispeak.ArrayOfMeasurementType measurementTypeList) {
        this.measurementTypeList = measurementTypeList;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof Measurement)) return false;
        Measurement other = (Measurement) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = super.equals(obj) && 
            ((this.measurementDeviceID==null && other.getMeasurementDeviceID()==null) || 
             (this.measurementDeviceID!=null &&
              this.measurementDeviceID.equals(other.getMeasurementDeviceID()))) &&
            ((this.measurementTypeList==null && other.getMeasurementTypeList()==null) || 
             (this.measurementTypeList!=null &&
              this.measurementTypeList.equals(other.getMeasurementTypeList())));
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
        if (getMeasurementDeviceID() != null) {
            _hashCode += getMeasurementDeviceID().hashCode();
        }
        if (getMeasurementTypeList() != null) {
            _hashCode += getMeasurementTypeList().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(Measurement.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "measurement"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("measurementDeviceID");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "measurementDeviceID"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("measurementTypeList");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "measurementTypeList"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ArrayOfMeasurementType"));
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
