/**
 * ArrayOfOutageDetectionEvent.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.2 May 03, 2005 (02:20:24 EDT) WSDL2Java emitter.
 */

package com.cannontech.multispeak;

public class ArrayOfOutageDetectionEvent  implements java.io.Serializable {
    private com.cannontech.multispeak.OutageDetectionEvent[] outageDetectionEvent;

    public ArrayOfOutageDetectionEvent() {
    }

    public ArrayOfOutageDetectionEvent(
           com.cannontech.multispeak.OutageDetectionEvent[] outageDetectionEvent) {
           this.outageDetectionEvent = outageDetectionEvent;
    }


    /**
     * Gets the outageDetectionEvent value for this ArrayOfOutageDetectionEvent.
     * 
     * @return outageDetectionEvent
     */
    public com.cannontech.multispeak.OutageDetectionEvent[] getOutageDetectionEvent() {
        return outageDetectionEvent;
    }


    /**
     * Sets the outageDetectionEvent value for this ArrayOfOutageDetectionEvent.
     * 
     * @param outageDetectionEvent
     */
    public void setOutageDetectionEvent(com.cannontech.multispeak.OutageDetectionEvent[] outageDetectionEvent) {
        this.outageDetectionEvent = outageDetectionEvent;
    }

    public com.cannontech.multispeak.OutageDetectionEvent getOutageDetectionEvent(int i) {
        return this.outageDetectionEvent[i];
    }

    public void setOutageDetectionEvent(int i, com.cannontech.multispeak.OutageDetectionEvent _value) {
        this.outageDetectionEvent[i] = _value;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof ArrayOfOutageDetectionEvent)) return false;
        ArrayOfOutageDetectionEvent other = (ArrayOfOutageDetectionEvent) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.outageDetectionEvent==null && other.getOutageDetectionEvent()==null) || 
             (this.outageDetectionEvent!=null &&
              java.util.Arrays.equals(this.outageDetectionEvent, other.getOutageDetectionEvent())));
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
        if (getOutageDetectionEvent() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getOutageDetectionEvent());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getOutageDetectionEvent(), i);
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
        new org.apache.axis.description.TypeDesc(ArrayOfOutageDetectionEvent.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ArrayOfOutageDetectionEvent"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("outageDetectionEvent");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "outageDetectionEvent"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "outageDetectionEvent"));
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
