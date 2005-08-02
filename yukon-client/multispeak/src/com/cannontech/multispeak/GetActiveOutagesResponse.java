/**
 * GetActiveOutagesResponse.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.2 May 03, 2005 (02:20:24 EDT) WSDL2Java emitter.
 */

package com.cannontech.multispeak;

public class GetActiveOutagesResponse  implements java.io.Serializable {
    private com.cannontech.multispeak.ArrayOfString getActiveOutagesResult;

    public GetActiveOutagesResponse() {
    }

    public GetActiveOutagesResponse(
           com.cannontech.multispeak.ArrayOfString getActiveOutagesResult) {
           this.getActiveOutagesResult = getActiveOutagesResult;
    }


    /**
     * Gets the getActiveOutagesResult value for this GetActiveOutagesResponse.
     * 
     * @return getActiveOutagesResult
     */
    public com.cannontech.multispeak.ArrayOfString getGetActiveOutagesResult() {
        return getActiveOutagesResult;
    }


    /**
     * Sets the getActiveOutagesResult value for this GetActiveOutagesResponse.
     * 
     * @param getActiveOutagesResult
     */
    public void setGetActiveOutagesResult(com.cannontech.multispeak.ArrayOfString getActiveOutagesResult) {
        this.getActiveOutagesResult = getActiveOutagesResult;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof GetActiveOutagesResponse)) return false;
        GetActiveOutagesResponse other = (GetActiveOutagesResponse) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.getActiveOutagesResult==null && other.getGetActiveOutagesResult()==null) || 
             (this.getActiveOutagesResult!=null &&
              this.getActiveOutagesResult.equals(other.getGetActiveOutagesResult())));
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
        if (getGetActiveOutagesResult() != null) {
            _hashCode += getGetActiveOutagesResult().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(GetActiveOutagesResponse.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">GetActiveOutagesResponse"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("getActiveOutagesResult");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "GetActiveOutagesResult"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ArrayOfString"));
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
