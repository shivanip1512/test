/**
 * IntiateLoadManagementEvent.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Dec 28, 2007 (09:53:13 CST) WSDL2Java emitter.
 */

package com.cannontech.multispeak.deploy.service;

public class IntiateLoadManagementEvent  implements java.io.Serializable {
    private com.cannontech.multispeak.deploy.service.LoadManagementEvent theLMEvent;

    public IntiateLoadManagementEvent() {
    }

    public IntiateLoadManagementEvent(
           com.cannontech.multispeak.deploy.service.LoadManagementEvent theLMEvent) {
           this.theLMEvent = theLMEvent;
    }


    /**
     * Gets the theLMEvent value for this IntiateLoadManagementEvent.
     * 
     * @return theLMEvent
     */
    public com.cannontech.multispeak.deploy.service.LoadManagementEvent getTheLMEvent() {
        return theLMEvent;
    }


    /**
     * Sets the theLMEvent value for this IntiateLoadManagementEvent.
     * 
     * @param theLMEvent
     */
    public void setTheLMEvent(com.cannontech.multispeak.deploy.service.LoadManagementEvent theLMEvent) {
        this.theLMEvent = theLMEvent;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof IntiateLoadManagementEvent)) return false;
        IntiateLoadManagementEvent other = (IntiateLoadManagementEvent) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.theLMEvent==null && other.getTheLMEvent()==null) || 
             (this.theLMEvent!=null &&
              this.theLMEvent.equals(other.getTheLMEvent())));
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
        if (getTheLMEvent() != null) {
            _hashCode += getTheLMEvent().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(IntiateLoadManagementEvent.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">IntiateLoadManagementEvent"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("theLMEvent");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "theLMEvent"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "loadManagementEvent"));
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
