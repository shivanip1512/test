/**
 * GetEndDeviceShipmentByTransponderIDResponse.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Dec 28, 2007 (09:53:13 CST) WSDL2Java emitter.
 */

package com.cannontech.multispeak.deploy.service;

public class GetEndDeviceShipmentByTransponderIDResponse  implements java.io.Serializable {
    private com.cannontech.multispeak.deploy.service.EndDeviceShipment getEndDeviceShipmentByTransponderIDResult;

    public GetEndDeviceShipmentByTransponderIDResponse() {
    }

    public GetEndDeviceShipmentByTransponderIDResponse(
           com.cannontech.multispeak.deploy.service.EndDeviceShipment getEndDeviceShipmentByTransponderIDResult) {
           this.getEndDeviceShipmentByTransponderIDResult = getEndDeviceShipmentByTransponderIDResult;
    }


    /**
     * Gets the getEndDeviceShipmentByTransponderIDResult value for this GetEndDeviceShipmentByTransponderIDResponse.
     * 
     * @return getEndDeviceShipmentByTransponderIDResult
     */
    public com.cannontech.multispeak.deploy.service.EndDeviceShipment getGetEndDeviceShipmentByTransponderIDResult() {
        return getEndDeviceShipmentByTransponderIDResult;
    }


    /**
     * Sets the getEndDeviceShipmentByTransponderIDResult value for this GetEndDeviceShipmentByTransponderIDResponse.
     * 
     * @param getEndDeviceShipmentByTransponderIDResult
     */
    public void setGetEndDeviceShipmentByTransponderIDResult(com.cannontech.multispeak.deploy.service.EndDeviceShipment getEndDeviceShipmentByTransponderIDResult) {
        this.getEndDeviceShipmentByTransponderIDResult = getEndDeviceShipmentByTransponderIDResult;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof GetEndDeviceShipmentByTransponderIDResponse)) return false;
        GetEndDeviceShipmentByTransponderIDResponse other = (GetEndDeviceShipmentByTransponderIDResponse) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.getEndDeviceShipmentByTransponderIDResult==null && other.getGetEndDeviceShipmentByTransponderIDResult()==null) || 
             (this.getEndDeviceShipmentByTransponderIDResult!=null &&
              this.getEndDeviceShipmentByTransponderIDResult.equals(other.getGetEndDeviceShipmentByTransponderIDResult())));
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
        if (getGetEndDeviceShipmentByTransponderIDResult() != null) {
            _hashCode += getGetEndDeviceShipmentByTransponderIDResult().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(GetEndDeviceShipmentByTransponderIDResponse.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">GetEndDeviceShipmentByTransponderIDResponse"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("getEndDeviceShipmentByTransponderIDResult");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "GetEndDeviceShipmentByTransponderIDResult"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "endDeviceShipment"));
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
