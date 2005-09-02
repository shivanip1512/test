/**
 * GetChildCircuitElementsResponse.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.2 May 03, 2005 (02:20:24 EDT) WSDL2Java emitter.
 */

package com.cannontech.multispeak;

public class GetChildCircuitElementsResponse  implements java.io.Serializable {
    private com.cannontech.multispeak.ArrayOfCircuitElement getChildCircuitElementsResult;

    public GetChildCircuitElementsResponse() {
    }

    public GetChildCircuitElementsResponse(
           com.cannontech.multispeak.ArrayOfCircuitElement getChildCircuitElementsResult) {
           this.getChildCircuitElementsResult = getChildCircuitElementsResult;
    }


    /**
     * Gets the getChildCircuitElementsResult value for this GetChildCircuitElementsResponse.
     * 
     * @return getChildCircuitElementsResult
     */
    public com.cannontech.multispeak.ArrayOfCircuitElement getGetChildCircuitElementsResult() {
        return getChildCircuitElementsResult;
    }


    /**
     * Sets the getChildCircuitElementsResult value for this GetChildCircuitElementsResponse.
     * 
     * @param getChildCircuitElementsResult
     */
    public void setGetChildCircuitElementsResult(com.cannontech.multispeak.ArrayOfCircuitElement getChildCircuitElementsResult) {
        this.getChildCircuitElementsResult = getChildCircuitElementsResult;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof GetChildCircuitElementsResponse)) return false;
        GetChildCircuitElementsResponse other = (GetChildCircuitElementsResponse) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.getChildCircuitElementsResult==null && other.getGetChildCircuitElementsResult()==null) || 
             (this.getChildCircuitElementsResult!=null &&
              this.getChildCircuitElementsResult.equals(other.getGetChildCircuitElementsResult())));
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
        if (getGetChildCircuitElementsResult() != null) {
            _hashCode += getGetChildCircuitElementsResult().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(GetChildCircuitElementsResponse.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">GetChildCircuitElementsResponse"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("getChildCircuitElementsResult");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "GetChildCircuitElementsResult"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ArrayOfCircuitElement"));
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
