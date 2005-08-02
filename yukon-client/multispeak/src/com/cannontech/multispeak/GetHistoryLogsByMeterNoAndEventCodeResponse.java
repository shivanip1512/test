/**
 * GetHistoryLogsByMeterNoAndEventCodeResponse.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.2 May 03, 2005 (02:20:24 EDT) WSDL2Java emitter.
 */

package com.cannontech.multispeak;

public class GetHistoryLogsByMeterNoAndEventCodeResponse  implements java.io.Serializable {
    private com.cannontech.multispeak.ArrayOfHistoryLog getHistoryLogsByMeterNoAndEventCodeResult;

    public GetHistoryLogsByMeterNoAndEventCodeResponse() {
    }

    public GetHistoryLogsByMeterNoAndEventCodeResponse(
           com.cannontech.multispeak.ArrayOfHistoryLog getHistoryLogsByMeterNoAndEventCodeResult) {
           this.getHistoryLogsByMeterNoAndEventCodeResult = getHistoryLogsByMeterNoAndEventCodeResult;
    }


    /**
     * Gets the getHistoryLogsByMeterNoAndEventCodeResult value for this GetHistoryLogsByMeterNoAndEventCodeResponse.
     * 
     * @return getHistoryLogsByMeterNoAndEventCodeResult
     */
    public com.cannontech.multispeak.ArrayOfHistoryLog getGetHistoryLogsByMeterNoAndEventCodeResult() {
        return getHistoryLogsByMeterNoAndEventCodeResult;
    }


    /**
     * Sets the getHistoryLogsByMeterNoAndEventCodeResult value for this GetHistoryLogsByMeterNoAndEventCodeResponse.
     * 
     * @param getHistoryLogsByMeterNoAndEventCodeResult
     */
    public void setGetHistoryLogsByMeterNoAndEventCodeResult(com.cannontech.multispeak.ArrayOfHistoryLog getHistoryLogsByMeterNoAndEventCodeResult) {
        this.getHistoryLogsByMeterNoAndEventCodeResult = getHistoryLogsByMeterNoAndEventCodeResult;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof GetHistoryLogsByMeterNoAndEventCodeResponse)) return false;
        GetHistoryLogsByMeterNoAndEventCodeResponse other = (GetHistoryLogsByMeterNoAndEventCodeResponse) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.getHistoryLogsByMeterNoAndEventCodeResult==null && other.getGetHistoryLogsByMeterNoAndEventCodeResult()==null) || 
             (this.getHistoryLogsByMeterNoAndEventCodeResult!=null &&
              this.getHistoryLogsByMeterNoAndEventCodeResult.equals(other.getGetHistoryLogsByMeterNoAndEventCodeResult())));
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
        if (getGetHistoryLogsByMeterNoAndEventCodeResult() != null) {
            _hashCode += getGetHistoryLogsByMeterNoAndEventCodeResult().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(GetHistoryLogsByMeterNoAndEventCodeResponse.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">GetHistoryLogsByMeterNoAndEventCodeResponse"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("getHistoryLogsByMeterNoAndEventCodeResult");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "GetHistoryLogsByMeterNoAndEventCodeResult"));
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
