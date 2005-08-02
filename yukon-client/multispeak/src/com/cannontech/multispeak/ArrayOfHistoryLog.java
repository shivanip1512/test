/**
 * ArrayOfHistoryLog.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.2 May 03, 2005 (02:20:24 EDT) WSDL2Java emitter.
 */

package com.cannontech.multispeak;

public class ArrayOfHistoryLog  implements java.io.Serializable {
    private com.cannontech.multispeak.HistoryLog[] historyLog;

    public ArrayOfHistoryLog() {
    }

    public ArrayOfHistoryLog(
           com.cannontech.multispeak.HistoryLog[] historyLog) {
           this.historyLog = historyLog;
    }


    /**
     * Gets the historyLog value for this ArrayOfHistoryLog.
     * 
     * @return historyLog
     */
    public com.cannontech.multispeak.HistoryLog[] getHistoryLog() {
        return historyLog;
    }


    /**
     * Sets the historyLog value for this ArrayOfHistoryLog.
     * 
     * @param historyLog
     */
    public void setHistoryLog(com.cannontech.multispeak.HistoryLog[] historyLog) {
        this.historyLog = historyLog;
    }

    public com.cannontech.multispeak.HistoryLog getHistoryLog(int i) {
        return this.historyLog[i];
    }

    public void setHistoryLog(int i, com.cannontech.multispeak.HistoryLog _value) {
        this.historyLog[i] = _value;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof ArrayOfHistoryLog)) return false;
        ArrayOfHistoryLog other = (ArrayOfHistoryLog) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.historyLog==null && other.getHistoryLog()==null) || 
             (this.historyLog!=null &&
              java.util.Arrays.equals(this.historyLog, other.getHistoryLog())));
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
        if (getHistoryLog() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getHistoryLog());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getHistoryLog(), i);
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
        new org.apache.axis.description.TypeDesc(ArrayOfHistoryLog.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ArrayOfHistoryLog"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("historyLog");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "historyLog"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "historyLog"));
        elemField.setMinOccurs(0);
        elemField.setNillable(true);
        elemField.setMaxOccursUnbounded(true);
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
