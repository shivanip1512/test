/**
 * GetHistoryLogsByDateAndEventCodeResponse.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.2 May 03, 2005 (02:20:24 EDT) WSDL2Java emitter.
 */

package com.cannontech.multispeak;

public class GetHistoryLogsByDateAndEventCodeResponse  implements java.io.Serializable {
    private com.cannontech.multispeak.ArrayOfHistoryLog getHistoryLogsByDateAndEventCodeResult;

    public GetHistoryLogsByDateAndEventCodeResponse() {
    }

    public GetHistoryLogsByDateAndEventCodeResponse(
           com.cannontech.multispeak.ArrayOfHistoryLog getHistoryLogsByDateAndEventCodeResult) {
           this.getHistoryLogsByDateAndEventCodeResult = getHistoryLogsByDateAndEventCodeResult;
    }


    /**
     * Gets the getHistoryLogsByDateAndEventCodeResult value for this GetHistoryLogsByDateAndEventCodeResponse.
     * 
     * @return getHistoryLogsByDateAndEventCodeResult
     */
    public com.cannontech.multispeak.ArrayOfHistoryLog getGetHistoryLogsByDateAndEventCodeResult() {
        return getHistoryLogsByDateAndEventCodeResult;
    }


    /**
     * Sets the getHistoryLogsByDateAndEventCodeResult value for this GetHistoryLogsByDateAndEventCodeResponse.
     * 
     * @param getHistoryLogsByDateAndEventCodeResult
     */
    public void setGetHistoryLogsByDateAndEventCodeResult(com.cannontech.multispeak.ArrayOfHistoryLog getHistoryLogsByDateAndEventCodeResult) {
        this.getHistoryLogsByDateAndEventCodeResult = getHistoryLogsByDateAndEventCodeResult;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof GetHistoryLogsByDateAndEventCodeResponse)) return false;
        GetHistoryLogsByDateAndEventCodeResponse other = (GetHistoryLogsByDateAndEventCodeResponse) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.getHistoryLogsByDateAndEventCodeResult==null && other.getGetHistoryLogsByDateAndEventCodeResult()==null) || 
             (this.getHistoryLogsByDateAndEventCodeResult!=null &&
              this.getHistoryLogsByDateAndEventCodeResult.equals(other.getGetHistoryLogsByDateAndEventCodeResult())));
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
        if (getGetHistoryLogsByDateAndEventCodeResult() != null) {
            _hashCode += getGetHistoryLogsByDateAndEventCodeResult().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(GetHistoryLogsByDateAndEventCodeResponse.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">GetHistoryLogsByDateAndEventCodeResponse"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("getHistoryLogsByDateAndEventCodeResult");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "GetHistoryLogsByDateAndEventCodeResult"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ArrayOfHistoryLog"));
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
