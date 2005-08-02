/**
 * HistoryLogChangedNotificationResponse.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.2 May 03, 2005 (02:20:24 EDT) WSDL2Java emitter.
 */

package com.cannontech.multispeak;

public class HistoryLogChangedNotificationResponse  implements java.io.Serializable {
    private com.cannontech.multispeak.ArrayOfErrorObject historyLogChangedNotificationResult;

    public HistoryLogChangedNotificationResponse() {
    }

    public HistoryLogChangedNotificationResponse(
           com.cannontech.multispeak.ArrayOfErrorObject historyLogChangedNotificationResult) {
           this.historyLogChangedNotificationResult = historyLogChangedNotificationResult;
    }


    /**
     * Gets the historyLogChangedNotificationResult value for this HistoryLogChangedNotificationResponse.
     * 
     * @return historyLogChangedNotificationResult
     */
    public com.cannontech.multispeak.ArrayOfErrorObject getHistoryLogChangedNotificationResult() {
        return historyLogChangedNotificationResult;
    }


    /**
     * Sets the historyLogChangedNotificationResult value for this HistoryLogChangedNotificationResponse.
     * 
     * @param historyLogChangedNotificationResult
     */
    public void setHistoryLogChangedNotificationResult(com.cannontech.multispeak.ArrayOfErrorObject historyLogChangedNotificationResult) {
        this.historyLogChangedNotificationResult = historyLogChangedNotificationResult;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof HistoryLogChangedNotificationResponse)) return false;
        HistoryLogChangedNotificationResponse other = (HistoryLogChangedNotificationResponse) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.historyLogChangedNotificationResult==null && other.getHistoryLogChangedNotificationResult()==null) || 
             (this.historyLogChangedNotificationResult!=null &&
              this.historyLogChangedNotificationResult.equals(other.getHistoryLogChangedNotificationResult())));
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
        if (getHistoryLogChangedNotificationResult() != null) {
            _hashCode += getHistoryLogChangedNotificationResult().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(HistoryLogChangedNotificationResponse.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">HistoryLogChangedNotificationResponse"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("historyLogChangedNotificationResult");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "HistoryLogChangedNotificationResult"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ArrayOfErrorObject"));
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
