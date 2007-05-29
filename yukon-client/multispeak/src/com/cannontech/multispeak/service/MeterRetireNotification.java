/**
 * MeterRetireNotification.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.2 May 03, 2005 (02:20:24 EDT) WSDL2Java emitter.
 */

package com.cannontech.multispeak.service;

public class MeterRetireNotification  implements java.io.Serializable {
    private com.cannontech.multispeak.service.ArrayOfMeter retiredMeters;

    public MeterRetireNotification() {
    }

    public MeterRetireNotification(
           com.cannontech.multispeak.service.ArrayOfMeter retiredMeters) {
           this.retiredMeters = retiredMeters;
    }


    /**
     * Gets the retiredMeters value for this MeterRetireNotification.
     * 
     * @return retiredMeters
     */
    public com.cannontech.multispeak.service.ArrayOfMeter getRetiredMeters() {
        return retiredMeters;
    }


    /**
     * Sets the retiredMeters value for this MeterRetireNotification.
     * 
     * @param retiredMeters
     */
    public void setRetiredMeters(com.cannontech.multispeak.service.ArrayOfMeter retiredMeters) {
        this.retiredMeters = retiredMeters;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof MeterRetireNotification)) return false;
        MeterRetireNotification other = (MeterRetireNotification) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.retiredMeters==null && other.getRetiredMeters()==null) || 
             (this.retiredMeters!=null &&
              this.retiredMeters.equals(other.getRetiredMeters())));
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
        if (getRetiredMeters() != null) {
            _hashCode += getRetiredMeters().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(MeterRetireNotification.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">MeterRetireNotification"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("retiredMeters");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "retiredMeters"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ArrayOfMeter"));
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
