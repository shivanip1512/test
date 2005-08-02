/**
 * GetServiceLocationByGridLocationResponse.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.2 May 03, 2005 (02:20:24 EDT) WSDL2Java emitter.
 */

package com.cannontech.multispeak;

public class GetServiceLocationByGridLocationResponse  implements java.io.Serializable {
    private com.cannontech.multispeak.ArrayOfServiceLocation getServiceLocationByGridLocationResult;

    public GetServiceLocationByGridLocationResponse() {
    }

    public GetServiceLocationByGridLocationResponse(
           com.cannontech.multispeak.ArrayOfServiceLocation getServiceLocationByGridLocationResult) {
           this.getServiceLocationByGridLocationResult = getServiceLocationByGridLocationResult;
    }


    /**
     * Gets the getServiceLocationByGridLocationResult value for this GetServiceLocationByGridLocationResponse.
     * 
     * @return getServiceLocationByGridLocationResult
     */
    public com.cannontech.multispeak.ArrayOfServiceLocation getGetServiceLocationByGridLocationResult() {
        return getServiceLocationByGridLocationResult;
    }


    /**
     * Sets the getServiceLocationByGridLocationResult value for this GetServiceLocationByGridLocationResponse.
     * 
     * @param getServiceLocationByGridLocationResult
     */
    public void setGetServiceLocationByGridLocationResult(com.cannontech.multispeak.ArrayOfServiceLocation getServiceLocationByGridLocationResult) {
        this.getServiceLocationByGridLocationResult = getServiceLocationByGridLocationResult;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof GetServiceLocationByGridLocationResponse)) return false;
        GetServiceLocationByGridLocationResponse other = (GetServiceLocationByGridLocationResponse) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.getServiceLocationByGridLocationResult==null && other.getGetServiceLocationByGridLocationResult()==null) || 
             (this.getServiceLocationByGridLocationResult!=null &&
              this.getServiceLocationByGridLocationResult.equals(other.getGetServiceLocationByGridLocationResult())));
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
        if (getGetServiceLocationByGridLocationResult() != null) {
            _hashCode += getGetServiceLocationByGridLocationResult().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(GetServiceLocationByGridLocationResponse.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">GetServiceLocationByGridLocationResponse"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("getServiceLocationByGridLocationResult");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "GetServiceLocationByGridLocationResult"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ArrayOfServiceLocation"));
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
