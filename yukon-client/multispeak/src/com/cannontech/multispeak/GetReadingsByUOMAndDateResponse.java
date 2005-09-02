/**
 * GetReadingsByUOMAndDateResponse.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.2 May 03, 2005 (02:20:24 EDT) WSDL2Java emitter.
 */

package com.cannontech.multispeak;

public class GetReadingsByUOMAndDateResponse  implements java.io.Serializable {
    private com.cannontech.multispeak.ArrayOfMeterRead getReadingsByUOMAndDateResult;

    public GetReadingsByUOMAndDateResponse() {
    }

    public GetReadingsByUOMAndDateResponse(
           com.cannontech.multispeak.ArrayOfMeterRead getReadingsByUOMAndDateResult) {
           this.getReadingsByUOMAndDateResult = getReadingsByUOMAndDateResult;
    }


    /**
     * Gets the getReadingsByUOMAndDateResult value for this GetReadingsByUOMAndDateResponse.
     * 
     * @return getReadingsByUOMAndDateResult
     */
    public com.cannontech.multispeak.ArrayOfMeterRead getGetReadingsByUOMAndDateResult() {
        return getReadingsByUOMAndDateResult;
    }


    /**
     * Sets the getReadingsByUOMAndDateResult value for this GetReadingsByUOMAndDateResponse.
     * 
     * @param getReadingsByUOMAndDateResult
     */
    public void setGetReadingsByUOMAndDateResult(com.cannontech.multispeak.ArrayOfMeterRead getReadingsByUOMAndDateResult) {
        this.getReadingsByUOMAndDateResult = getReadingsByUOMAndDateResult;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof GetReadingsByUOMAndDateResponse)) return false;
        GetReadingsByUOMAndDateResponse other = (GetReadingsByUOMAndDateResponse) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.getReadingsByUOMAndDateResult==null && other.getGetReadingsByUOMAndDateResult()==null) || 
             (this.getReadingsByUOMAndDateResult!=null &&
              this.getReadingsByUOMAndDateResult.equals(other.getGetReadingsByUOMAndDateResult())));
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
        if (getGetReadingsByUOMAndDateResult() != null) {
            _hashCode += getGetReadingsByUOMAndDateResult().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(GetReadingsByUOMAndDateResponse.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">GetReadingsByUOMAndDateResponse"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("getReadingsByUOMAndDateResult");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "GetReadingsByUOMAndDateResult"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ArrayOfMeterRead"));
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
