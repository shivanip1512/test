/**
 * ConfigurationGroup.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Dec 28, 2007 (09:53:13 CST) WSDL2Java emitter.
 */

package com.cannontech.multispeak.deploy.service;

public class ConfigurationGroup  extends com.cannontech.multispeak.deploy.service.MspObject  implements java.io.Serializable {
    private java.lang.String groupName;

    private com.cannontech.multispeak.deploy.service.MeterRef[] meterRefList;

    private com.cannontech.multispeak.deploy.service.ConfiguredReadingType[] configuredReadingTypes;

    public ConfigurationGroup() {
    }

    public ConfigurationGroup(
           java.lang.String objectID,
           com.cannontech.multispeak.deploy.service.Action verb,
           java.lang.String errorString,
           java.lang.String replaceID,
           java.lang.String utility,
           com.cannontech.multispeak.deploy.service.Extensions extensions,
           java.lang.String comments,
           com.cannontech.multispeak.deploy.service.ExtensionsItem[] extensionsList,
           java.lang.String groupName,
           com.cannontech.multispeak.deploy.service.MeterRef[] meterRefList,
           com.cannontech.multispeak.deploy.service.ConfiguredReadingType[] configuredReadingTypes) {
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
        this.meterRefList = meterRefList;
        this.configuredReadingTypes = configuredReadingTypes;
    }


    /**
     * Gets the groupName value for this ConfigurationGroup.
     * 
     * @return groupName
     */
    public java.lang.String getGroupName() {
        return groupName;
    }


    /**
     * Sets the groupName value for this ConfigurationGroup.
     * 
     * @param groupName
     */
    public void setGroupName(java.lang.String groupName) {
        this.groupName = groupName;
    }


    /**
     * Gets the meterRefList value for this ConfigurationGroup.
     * 
     * @return meterRefList
     */
    public com.cannontech.multispeak.deploy.service.MeterRef[] getMeterRefList() {
        return meterRefList;
    }


    /**
     * Sets the meterRefList value for this ConfigurationGroup.
     * 
     * @param meterRefList
     */
    public void setMeterRefList(com.cannontech.multispeak.deploy.service.MeterRef[] meterRefList) {
        this.meterRefList = meterRefList;
    }


    /**
     * Gets the configuredReadingTypes value for this ConfigurationGroup.
     * 
     * @return configuredReadingTypes
     */
    public com.cannontech.multispeak.deploy.service.ConfiguredReadingType[] getConfiguredReadingTypes() {
        return configuredReadingTypes;
    }


    /**
     * Sets the configuredReadingTypes value for this ConfigurationGroup.
     * 
     * @param configuredReadingTypes
     */
    public void setConfiguredReadingTypes(com.cannontech.multispeak.deploy.service.ConfiguredReadingType[] configuredReadingTypes) {
        this.configuredReadingTypes = configuredReadingTypes;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof ConfigurationGroup)) return false;
        ConfigurationGroup other = (ConfigurationGroup) obj;
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
            ((this.meterRefList==null && other.getMeterRefList()==null) || 
             (this.meterRefList!=null &&
              java.util.Arrays.equals(this.meterRefList, other.getMeterRefList()))) &&
            ((this.configuredReadingTypes==null && other.getConfiguredReadingTypes()==null) || 
             (this.configuredReadingTypes!=null &&
              java.util.Arrays.equals(this.configuredReadingTypes, other.getConfiguredReadingTypes())));
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
        if (getMeterRefList() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getMeterRefList());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getMeterRefList(), i);
                if (obj != null &&
                    !obj.getClass().isArray()) {
                    _hashCode += obj.hashCode();
                }
            }
        }
        if (getConfiguredReadingTypes() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getConfiguredReadingTypes());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getConfiguredReadingTypes(), i);
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
        new org.apache.axis.description.TypeDesc(ConfigurationGroup.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "configurationGroup"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("groupName");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "groupName"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("meterRefList");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "meterRefList"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "meterRef"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        elemField.setItemQName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "meterRef"));
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("configuredReadingTypes");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "configuredReadingTypes"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "configuredReadingType"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        elemField.setItemQName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "configuredReadingType"));
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
