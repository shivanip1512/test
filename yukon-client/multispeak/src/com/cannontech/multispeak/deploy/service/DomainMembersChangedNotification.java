/**
 * DomainMembersChangedNotification.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Dec 28, 2007 (09:53:13 CST) WSDL2Java emitter.
 */

package com.cannontech.multispeak.deploy.service;

public class DomainMembersChangedNotification  implements java.io.Serializable {
    private com.cannontech.multispeak.deploy.service.DomainMember[] changedDomainMembers;

    public DomainMembersChangedNotification() {
    }

    public DomainMembersChangedNotification(
           com.cannontech.multispeak.deploy.service.DomainMember[] changedDomainMembers) {
           this.changedDomainMembers = changedDomainMembers;
    }


    /**
     * Gets the changedDomainMembers value for this DomainMembersChangedNotification.
     * 
     * @return changedDomainMembers
     */
    public com.cannontech.multispeak.deploy.service.DomainMember[] getChangedDomainMembers() {
        return changedDomainMembers;
    }


    /**
     * Sets the changedDomainMembers value for this DomainMembersChangedNotification.
     * 
     * @param changedDomainMembers
     */
    public void setChangedDomainMembers(com.cannontech.multispeak.deploy.service.DomainMember[] changedDomainMembers) {
        this.changedDomainMembers = changedDomainMembers;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof DomainMembersChangedNotification)) return false;
        DomainMembersChangedNotification other = (DomainMembersChangedNotification) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.changedDomainMembers==null && other.getChangedDomainMembers()==null) || 
             (this.changedDomainMembers!=null &&
              java.util.Arrays.equals(this.changedDomainMembers, other.getChangedDomainMembers())));
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
        if (getChangedDomainMembers() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getChangedDomainMembers());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getChangedDomainMembers(), i);
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
        new org.apache.axis.description.TypeDesc(DomainMembersChangedNotification.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">DomainMembersChangedNotification"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("changedDomainMembers");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "changedDomainMembers"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "domainMember"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        elemField.setItemQName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "domainMember"));
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
