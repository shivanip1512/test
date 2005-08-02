/**
 * GetModifiedServiceLocationsResponse.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.2 May 03, 2005 (02:20:24 EDT) WSDL2Java emitter.
 */

package com.cannontech.multispeak;

public class GetModifiedServiceLocationsResponse  implements java.io.Serializable {
    private com.cannontech.multispeak.ArrayOfServiceLocation getModifiedServiceLocationsResult;

    public GetModifiedServiceLocationsResponse() {
    }

    public GetModifiedServiceLocationsResponse(
           com.cannontech.multispeak.ArrayOfServiceLocation getModifiedServiceLocationsResult) {
           this.getModifiedServiceLocationsResult = getModifiedServiceLocationsResult;
    }


    /**
     * Gets the getModifiedServiceLocationsResult value for this GetModifiedServiceLocationsResponse.
     * 
     * @return getModifiedServiceLocationsResult
     */
    public com.cannontech.multispeak.ArrayOfServiceLocation getGetModifiedServiceLocationsResult() {
        return getModifiedServiceLocationsResult;
    }


    /**
     * Sets the getModifiedServiceLocationsResult value for this GetModifiedServiceLocationsResponse.
     * 
     * @param getModifiedServiceLocationsResult
     */
    public void setGetModifiedServiceLocationsResult(com.cannontech.multispeak.ArrayOfServiceLocation getModifiedServiceLocationsResult) {
        this.getModifiedServiceLocationsResult = getModifiedServiceLocationsResult;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof GetModifiedServiceLocationsResponse)) return false;
        GetModifiedServiceLocationsResponse other = (GetModifiedServiceLocationsResponse) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.getModifiedServiceLocationsResult==null && other.getGetModifiedServiceLocationsResult()==null) || 
             (this.getModifiedServiceLocationsResult!=null &&
              this.getModifiedServiceLocationsResult.equals(other.getGetModifiedServiceLocationsResult())));
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
        if (getGetModifiedServiceLocationsResult() != null) {
            _hashCode += getGetModifiedServiceLocationsResult().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(GetModifiedServiceLocationsResponse.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">GetModifiedServiceLocationsResponse"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("getModifiedServiceLocationsResult");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "GetModifiedServiceLocationsResult"));
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
