/**
 * GetEndDeviceShipmentByTransponderID.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Dec 28, 2007 (09:53:13 CST) WSDL2Java emitter.
 */

package com.cannontech.multispeak.deploy.service;

public class GetEndDeviceShipmentByTransponderID  implements java.io.Serializable {
    private java.lang.String transponderID;

    public GetEndDeviceShipmentByTransponderID() {
    }

    public GetEndDeviceShipmentByTransponderID(
           java.lang.String transponderID) {
           this.transponderID = transponderID;
    }


    /**
     * Gets the transponderID value for this GetEndDeviceShipmentByTransponderID.
     * 
     * @return transponderID
     */
    public java.lang.String getTransponderID() {
        return transponderID;
    }


    /**
     * Sets the transponderID value for this GetEndDeviceShipmentByTransponderID.
     * 
     * @param transponderID
     */
    public void setTransponderID(java.lang.String transponderID) {
        this.transponderID = transponderID;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof GetEndDeviceShipmentByTransponderID)) return false;
        GetEndDeviceShipmentByTransponderID other = (GetEndDeviceShipmentByTransponderID) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.transponderID==null && other.getTransponderID()==null) || 
             (this.transponderID!=null &&
              this.transponderID.equals(other.getTransponderID())));
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
        if (getTransponderID() != null) {
            _hashCode += getTransponderID().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(GetEndDeviceShipmentByTransponderID.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">GetEndDeviceShipmentByTransponderID"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("transponderID");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "transponderID"));
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
