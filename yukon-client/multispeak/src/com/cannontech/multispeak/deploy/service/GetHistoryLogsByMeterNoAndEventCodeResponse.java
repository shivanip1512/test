/**
 * GetHistoryLogsByMeterNoAndEventCodeResponse.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Dec 28, 2007 (09:53:13 CST) WSDL2Java emitter.
 */

package com.cannontech.multispeak.deploy.service;

public class GetHistoryLogsByMeterNoAndEventCodeResponse  implements java.io.Serializable {
    private com.cannontech.multispeak.deploy.service.HistoryLog[] getHistoryLogsByMeterNoAndEventCodeResult;

    public GetHistoryLogsByMeterNoAndEventCodeResponse() {
    }

    public GetHistoryLogsByMeterNoAndEventCodeResponse(
           com.cannontech.multispeak.deploy.service.HistoryLog[] getHistoryLogsByMeterNoAndEventCodeResult) {
           this.getHistoryLogsByMeterNoAndEventCodeResult = getHistoryLogsByMeterNoAndEventCodeResult;
    }


    /**
     * Gets the getHistoryLogsByMeterNoAndEventCodeResult value for this GetHistoryLogsByMeterNoAndEventCodeResponse.
     * 
     * @return getHistoryLogsByMeterNoAndEventCodeResult
     */
    public com.cannontech.multispeak.deploy.service.HistoryLog[] getGetHistoryLogsByMeterNoAndEventCodeResult() {
        return getHistoryLogsByMeterNoAndEventCodeResult;
    }


    /**
     * Sets the getHistoryLogsByMeterNoAndEventCodeResult value for this GetHistoryLogsByMeterNoAndEventCodeResponse.
     * 
     * @param getHistoryLogsByMeterNoAndEventCodeResult
     */
    public void setGetHistoryLogsByMeterNoAndEventCodeResult(com.cannontech.multispeak.deploy.service.HistoryLog[] getHistoryLogsByMeterNoAndEventCodeResult) {
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
              java.util.Arrays.equals(this.getHistoryLogsByMeterNoAndEventCodeResult, other.getGetHistoryLogsByMeterNoAndEventCodeResult())));
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
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getGetHistoryLogsByMeterNoAndEventCodeResult());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getGetHistoryLogsByMeterNoAndEventCodeResult(), i);
                if (obj != null &&
                    !obj.getClass().isArray()) {
                    _hashCode += obj.hashCode();
                }
            }
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
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "historyLog"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        elemField.setItemQName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "historyLog"));
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
