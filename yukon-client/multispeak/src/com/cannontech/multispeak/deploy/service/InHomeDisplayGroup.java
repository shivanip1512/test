/**
 * InHomeDisplayGroup.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Dec 28, 2007 (09:53:13 CST) WSDL2Java emitter.
 */

package com.cannontech.multispeak.deploy.service;

public class InHomeDisplayGroup  extends com.cannontech.multispeak.deploy.service.MspObject  implements java.io.Serializable {
    private java.lang.String groupName;

    private java.lang.String[] inHomeDisplayList;

    public InHomeDisplayGroup() {
    }

    public InHomeDisplayGroup(
           java.lang.String objectID,
           com.cannontech.multispeak.deploy.service.Action verb,
           java.lang.String errorString,
           java.lang.String replaceID,
           java.lang.String utility,
           com.cannontech.multispeak.deploy.service.Extensions extensions,
           java.lang.String comments,
           com.cannontech.multispeak.deploy.service.ExtensionsItem[] extensionsList,
           java.lang.String groupName,
           java.lang.String[] inHomeDisplayList) {
        super(
            objectID,
            verb,
            errorString,
            replaceID,
            utility,
            extensions,
            comments,
            extensionsList);
        this.groupName = groupName;
        this.inHomeDisplayList = inHomeDisplayList;
    }


    /**
     * Gets the groupName value for this InHomeDisplayGroup.
     * 
     * @return groupName
     */
    public java.lang.String getGroupName() {
        return groupName;
    }


    /**
     * Sets the groupName value for this InHomeDisplayGroup.
     * 
     * @param groupName
     */
    public void setGroupName(java.lang.String groupName) {
        this.groupName = groupName;
    }


    /**
     * Gets the inHomeDisplayList value for this InHomeDisplayGroup.
     * 
     * @return inHomeDisplayList
     */
    public java.lang.String[] getInHomeDisplayList() {
        return inHomeDisplayList;
    }


    /**
     * Sets the inHomeDisplayList value for this InHomeDisplayGroup.
     * 
     * @param inHomeDisplayList
     */
    public void setInHomeDisplayList(java.lang.String[] inHomeDisplayList) {
        this.inHomeDisplayList = inHomeDisplayList;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof InHomeDisplayGroup)) return false;
        InHomeDisplayGroup other = (InHomeDisplayGroup) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = super.equals(obj) && 
            ((this.groupName==null && other.getGroupName()==null) || 
             (this.groupName!=null &&
              this.groupName.equals(other.getGroupName()))) &&
            ((this.inHomeDisplayList==null && other.getInHomeDisplayList()==null) || 
             (this.inHomeDisplayList!=null &&
              java.util.Arrays.equals(this.inHomeDisplayList, other.getInHomeDisplayList())));
        __equalsCalc = null;
        return _equals;
    }

    private boolean __hashCodeCalc = false;
    public synchronized int hashCode() {
        if (__hashCodeCalc) {
            return 0;
        }
        __hashCodeCalc = true;
        int _hashCode = super.hashCode();
        if (getGroupName() != null) {
            _hashCode += getGroupName().hashCode();
        }
        if (getInHomeDisplayList() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getInHomeDisplayList());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getInHomeDisplayList(), i);
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
        new org.apache.axis.description.TypeDesc(InHomeDisplayGroup.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "inHomeDisplayGroup"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("groupName");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "groupName"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("inHomeDisplayList");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "inHomeDisplayList"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        elemField.setItemQName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "inHomeDisplayID"));
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
