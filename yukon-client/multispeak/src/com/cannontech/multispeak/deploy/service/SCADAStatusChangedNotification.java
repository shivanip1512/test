/**
 * SCADAStatusChangedNotification.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Dec 28, 2007 (09:53:13 CST) WSDL2Java emitter.
 */

package com.cannontech.multispeak.deploy.service;

public class SCADAStatusChangedNotification  implements java.io.Serializable {
    private com.cannontech.multispeak.deploy.service.ScadaStatus[] scadaStatuses;

    public SCADAStatusChangedNotification() {
    }

    public SCADAStatusChangedNotification(
           com.cannontech.multispeak.deploy.service.ScadaStatus[] scadaStatuses) {
           this.scadaStatuses = scadaStatuses;
    }


    /**
     * Gets the scadaStatuses value for this SCADAStatusChangedNotification.
     * 
     * @return scadaStatuses
     */
    public com.cannontech.multispeak.deploy.service.ScadaStatus[] getScadaStatuses() {
        return scadaStatuses;
    }


    /**
     * Sets the scadaStatuses value for this SCADAStatusChangedNotification.
     * 
     * @param scadaStatuses
     */
    public void setScadaStatuses(com.cannontech.multispeak.deploy.service.ScadaStatus[] scadaStatuses) {
        this.scadaStatuses = scadaStatuses;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof SCADAStatusChangedNotification)) return false;
        SCADAStatusChangedNotification other = (SCADAStatusChangedNotification) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.scadaStatuses==null && other.getScadaStatuses()==null) || 
             (this.scadaStatuses!=null &&
              java.util.Arrays.equals(this.scadaStatuses, other.getScadaStatuses())));
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
        if (getScadaStatuses() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getScadaStatuses());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getScadaStatuses(), i);
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
        new org.apache.axis.description.TypeDesc(SCADAStatusChangedNotification.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">SCADAStatusChangedNotification"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("scadaStatuses");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "scadaStatuses"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "scadaStatus"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        elemField.setItemQName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "scadaStatus"));
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
