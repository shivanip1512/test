/**
 * ServiceLocationChangedNotification.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Dec 28, 2007 (09:53:13 CST) WSDL2Java emitter.
 */

package com.cannontech.multispeak.deploy.service;

public class ServiceLocationChangedNotification  implements java.io.Serializable {
    private com.cannontech.multispeak.deploy.service.ServiceLocation[] changedServiceLocations;

    public ServiceLocationChangedNotification() {
    }

    public ServiceLocationChangedNotification(
           com.cannontech.multispeak.deploy.service.ServiceLocation[] changedServiceLocations) {
           this.changedServiceLocations = changedServiceLocations;
    }


    /**
     * Gets the changedServiceLocations value for this ServiceLocationChangedNotification.
     * 
     * @return changedServiceLocations
     */
    public com.cannontech.multispeak.deploy.service.ServiceLocation[] getChangedServiceLocations() {
        return changedServiceLocations;
    }


    /**
     * Sets the changedServiceLocations value for this ServiceLocationChangedNotification.
     * 
     * @param changedServiceLocations
     */
    public void setChangedServiceLocations(com.cannontech.multispeak.deploy.service.ServiceLocation[] changedServiceLocations) {
        this.changedServiceLocations = changedServiceLocations;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof ServiceLocationChangedNotification)) return false;
        ServiceLocationChangedNotification other = (ServiceLocationChangedNotification) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.changedServiceLocations==null && other.getChangedServiceLocations()==null) || 
             (this.changedServiceLocations!=null &&
              java.util.Arrays.equals(this.changedServiceLocations, other.getChangedServiceLocations())));
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
        if (getChangedServiceLocations() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getChangedServiceLocations());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getChangedServiceLocations(), i);
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
        new org.apache.axis.description.TypeDesc(ServiceLocationChangedNotification.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">ServiceLocationChangedNotification"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("changedServiceLocations");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "changedServiceLocations"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "serviceLocation"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        elemField.setItemQName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "serviceLocation"));
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
