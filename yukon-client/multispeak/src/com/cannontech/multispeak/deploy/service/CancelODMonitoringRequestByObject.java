/**
 * CancelODMonitoringRequestByObject.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Dec 28, 2007 (09:53:13 CST) WSDL2Java emitter.
 */

package com.cannontech.multispeak.deploy.service;

public class CancelODMonitoringRequestByObject  implements java.io.Serializable {
    private com.cannontech.multispeak.deploy.service.ObjectRef[] objectRef;

    private java.util.Calendar requestDate;

    public CancelODMonitoringRequestByObject() {
    }

    public CancelODMonitoringRequestByObject(
           com.cannontech.multispeak.deploy.service.ObjectRef[] objectRef,
           java.util.Calendar requestDate) {
           this.objectRef = objectRef;
           this.requestDate = requestDate;
    }


    /**
     * Gets the objectRef value for this CancelODMonitoringRequestByObject.
     * 
     * @return objectRef
     */
    public com.cannontech.multispeak.deploy.service.ObjectRef[] getObjectRef() {
        return objectRef;
    }


    /**
     * Sets the objectRef value for this CancelODMonitoringRequestByObject.
     * 
     * @param objectRef
     */
    public void setObjectRef(com.cannontech.multispeak.deploy.service.ObjectRef[] objectRef) {
        this.objectRef = objectRef;
    }


    /**
     * Gets the requestDate value for this CancelODMonitoringRequestByObject.
     * 
     * @return requestDate
     */
    public java.util.Calendar getRequestDate() {
        return requestDate;
    }


    /**
     * Sets the requestDate value for this CancelODMonitoringRequestByObject.
     * 
     * @param requestDate
     */
    public void setRequestDate(java.util.Calendar requestDate) {
        this.requestDate = requestDate;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof CancelODMonitoringRequestByObject)) return false;
        CancelODMonitoringRequestByObject other = (CancelODMonitoringRequestByObject) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.objectRef==null && other.getObjectRef()==null) || 
             (this.objectRef!=null &&
              java.util.Arrays.equals(this.objectRef, other.getObjectRef()))) &&
            ((this.requestDate==null && other.getRequestDate()==null) || 
             (this.requestDate!=null &&
              this.requestDate.equals(other.getRequestDate())));
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
        if (getObjectRef() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getObjectRef());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getObjectRef(), i);
                if (obj != null &&
                    !obj.getClass().isArray()) {
                    _hashCode += obj.hashCode();
                }
            }
        }
        if (getRequestDate() != null) {
            _hashCode += getRequestDate().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(CancelODMonitoringRequestByObject.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">CancelODMonitoringRequestByObject"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("objectRef");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ObjectRef"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "objectRef"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        elemField.setItemQName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "objectRef"));
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("requestDate");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "requestDate"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "dateTime"));
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
