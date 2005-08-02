/**
 * MeterRemoveNotification.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.2 May 03, 2005 (02:20:24 EDT) WSDL2Java emitter.
 */

package com.cannontech.multispeak;

public class MeterRemoveNotification  implements java.io.Serializable {
    private com.cannontech.multispeak.ArrayOfMeter removedMeters;

    public MeterRemoveNotification() {
    }

    public MeterRemoveNotification(
           com.cannontech.multispeak.ArrayOfMeter removedMeters) {
           this.removedMeters = removedMeters;
    }


    /**
     * Gets the removedMeters value for this MeterRemoveNotification.
     * 
     * @return removedMeters
     */
    public com.cannontech.multispeak.ArrayOfMeter getRemovedMeters() {
        return removedMeters;
    }


    /**
     * Sets the removedMeters value for this MeterRemoveNotification.
     * 
     * @param removedMeters
     */
    public void setRemovedMeters(com.cannontech.multispeak.ArrayOfMeter removedMeters) {
        this.removedMeters = removedMeters;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof MeterRemoveNotification)) return false;
        MeterRemoveNotification other = (MeterRemoveNotification) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.removedMeters==null && other.getRemovedMeters()==null) || 
             (this.removedMeters!=null &&
              this.removedMeters.equals(other.getRemovedMeters())));
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
        if (getRemovedMeters() != null) {
            _hashCode += getRemovedMeters().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(MeterRemoveNotification.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">MeterRemoveNotification"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("removedMeters");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "removedMeters"));
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
