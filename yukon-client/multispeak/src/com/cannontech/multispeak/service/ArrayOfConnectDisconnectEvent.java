/**
 * ArrayOfConnectDisconnectEvent.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.2 May 03, 2005 (02:20:24 EDT) WSDL2Java emitter.
 */

package com.cannontech.multispeak.service;

public class ArrayOfConnectDisconnectEvent  implements java.io.Serializable {
    private com.cannontech.multispeak.service.ConnectDisconnectEvent[] connectDisconnectEvent;

    public ArrayOfConnectDisconnectEvent() {
    }

    public ArrayOfConnectDisconnectEvent(
           com.cannontech.multispeak.service.ConnectDisconnectEvent[] connectDisconnectEvent) {
           this.connectDisconnectEvent = connectDisconnectEvent;
    }


    /**
     * Gets the connectDisconnectEvent value for this ArrayOfConnectDisconnectEvent.
     * 
     * @return connectDisconnectEvent
     */
    public com.cannontech.multispeak.service.ConnectDisconnectEvent[] getConnectDisconnectEvent() {
        return connectDisconnectEvent;
    }


    /**
     * Sets the connectDisconnectEvent value for this ArrayOfConnectDisconnectEvent.
     * 
     * @param connectDisconnectEvent
     */
    public void setConnectDisconnectEvent(com.cannontech.multispeak.service.ConnectDisconnectEvent[] connectDisconnectEvent) {
        this.connectDisconnectEvent = connectDisconnectEvent;
    }

    public com.cannontech.multispeak.service.ConnectDisconnectEvent getConnectDisconnectEvent(int i) {
        return this.connectDisconnectEvent[i];
    }

    public void setConnectDisconnectEvent(int i, com.cannontech.multispeak.service.ConnectDisconnectEvent _value) {
        this.connectDisconnectEvent[i] = _value;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof ArrayOfConnectDisconnectEvent)) return false;
        ArrayOfConnectDisconnectEvent other = (ArrayOfConnectDisconnectEvent) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.connectDisconnectEvent==null && other.getConnectDisconnectEvent()==null) || 
             (this.connectDisconnectEvent!=null &&
              java.util.Arrays.equals(this.connectDisconnectEvent, other.getConnectDisconnectEvent())));
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
        if (getConnectDisconnectEvent() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getConnectDisconnectEvent());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getConnectDisconnectEvent(), i);
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
        new org.apache.axis.description.TypeDesc(ArrayOfConnectDisconnectEvent.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ArrayOfConnectDisconnectEvent"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("connectDisconnectEvent");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "connectDisconnectEvent"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "connectDisconnectEvent"));
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
