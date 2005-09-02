/**
 * GetUplineMeterConnectivityResponse.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.2 May 03, 2005 (02:20:24 EDT) WSDL2Java emitter.
 */

package com.cannontech.multispeak;

public class GetUplineMeterConnectivityResponse  implements java.io.Serializable {
    private com.cannontech.multispeak.ArrayOfMeterConnectivity getUplineMeterConnectivityResult;

    public GetUplineMeterConnectivityResponse() {
    }

    public GetUplineMeterConnectivityResponse(
           com.cannontech.multispeak.ArrayOfMeterConnectivity getUplineMeterConnectivityResult) {
           this.getUplineMeterConnectivityResult = getUplineMeterConnectivityResult;
    }


    /**
     * Gets the getUplineMeterConnectivityResult value for this GetUplineMeterConnectivityResponse.
     * 
     * @return getUplineMeterConnectivityResult
     */
    public com.cannontech.multispeak.ArrayOfMeterConnectivity getGetUplineMeterConnectivityResult() {
        return getUplineMeterConnectivityResult;
    }


    /**
     * Sets the getUplineMeterConnectivityResult value for this GetUplineMeterConnectivityResponse.
     * 
     * @param getUplineMeterConnectivityResult
     */
    public void setGetUplineMeterConnectivityResult(com.cannontech.multispeak.ArrayOfMeterConnectivity getUplineMeterConnectivityResult) {
        this.getUplineMeterConnectivityResult = getUplineMeterConnectivityResult;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof GetUplineMeterConnectivityResponse)) return false;
        GetUplineMeterConnectivityResponse other = (GetUplineMeterConnectivityResponse) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.getUplineMeterConnectivityResult==null && other.getGetUplineMeterConnectivityResult()==null) || 
             (this.getUplineMeterConnectivityResult!=null &&
              this.getUplineMeterConnectivityResult.equals(other.getGetUplineMeterConnectivityResult())));
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
        if (getGetUplineMeterConnectivityResult() != null) {
            _hashCode += getGetUplineMeterConnectivityResult().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(GetUplineMeterConnectivityResponse.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">GetUplineMeterConnectivityResponse"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("getUplineMeterConnectivityResult");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "GetUplineMeterConnectivityResult"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ArrayOfMeterConnectivity"));
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
