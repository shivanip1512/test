/**
 * ReadingSchedule.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Dec 28, 2007 (09:53:13 CST) WSDL2Java emitter.
 */

package com.cannontech.multispeak.deploy.service;

public class ReadingSchedule  extends com.cannontech.multispeak.deploy.service.Schedule  implements java.io.Serializable {
    private com.cannontech.multispeak.deploy.service.MeterRef[] meterRefList;

    private com.cannontech.multispeak.deploy.service.MeterGroup[] meterGroupList;

    private java.lang.String[] configurationGroupList;

    private com.cannontech.multispeak.deploy.service.ReadingType[] readingTypeList;

    public ReadingSchedule() {
    }

    public ReadingSchedule(
           java.lang.String objectID,
           com.cannontech.multispeak.deploy.service.Action verb,
           java.lang.String errorString,
           java.lang.String replaceID,
           java.lang.String utility,
           com.cannontech.multispeak.deploy.service.Extensions extensions,
           java.lang.String comments,
           com.cannontech.multispeak.deploy.service.ExtensionsItem[] extensionsList,
           java.lang.Float maximumRuntime,
           com.cannontech.multispeak.deploy.service.TimePeriod effectiveWindow,
           java.lang.Boolean isEnabled,
           java.lang.Float offset,
           com.cannontech.multispeak.deploy.service.TimePoint[] absoluteTimeSchedule,
           com.cannontech.multispeak.deploy.service.PeriodicSchedule periodicSchedule,
           java.lang.String purpose,
           com.cannontech.multispeak.deploy.service.MeterRef[] meterRefList,
           com.cannontech.multispeak.deploy.service.MeterGroup[] meterGroupList,
           java.lang.String[] configurationGroupList,
           com.cannontech.multispeak.deploy.service.ReadingType[] readingTypeList) {
        super(
            objectID,
            verb,
            errorString,
            replaceID,
            utility,
            extensions,
            comments,
            extensionsList,
            maximumRuntime,
            effectiveWindow,
            isEnabled,
            offset,
            absoluteTimeSchedule,
            periodicSchedule,
            purpose);
        this.meterRefList = meterRefList;
        this.meterGroupList = meterGroupList;
        this.configurationGroupList = configurationGroupList;
        this.readingTypeList = readingTypeList;
    }


    /**
     * Gets the meterRefList value for this ReadingSchedule.
     * 
     * @return meterRefList
     */
    public com.cannontech.multispeak.deploy.service.MeterRef[] getMeterRefList() {
        return meterRefList;
    }


    /**
     * Sets the meterRefList value for this ReadingSchedule.
     * 
     * @param meterRefList
     */
    public void setMeterRefList(com.cannontech.multispeak.deploy.service.MeterRef[] meterRefList) {
        this.meterRefList = meterRefList;
    }


    /**
     * Gets the meterGroupList value for this ReadingSchedule.
     * 
     * @return meterGroupList
     */
    public com.cannontech.multispeak.deploy.service.MeterGroup[] getMeterGroupList() {
        return meterGroupList;
    }


    /**
     * Sets the meterGroupList value for this ReadingSchedule.
     * 
     * @param meterGroupList
     */
    public void setMeterGroupList(com.cannontech.multispeak.deploy.service.MeterGroup[] meterGroupList) {
        this.meterGroupList = meterGroupList;
    }


    /**
     * Gets the configurationGroupList value for this ReadingSchedule.
     * 
     * @return configurationGroupList
     */
    public java.lang.String[] getConfigurationGroupList() {
        return configurationGroupList;
    }


    /**
     * Sets the configurationGroupList value for this ReadingSchedule.
     * 
     * @param configurationGroupList
     */
    public void setConfigurationGroupList(java.lang.String[] configurationGroupList) {
        this.configurationGroupList = configurationGroupList;
    }


    /**
     * Gets the readingTypeList value for this ReadingSchedule.
     * 
     * @return readingTypeList
     */
    public com.cannontech.multispeak.deploy.service.ReadingType[] getReadingTypeList() {
        return readingTypeList;
    }


    /**
     * Sets the readingTypeList value for this ReadingSchedule.
     * 
     * @param readingTypeList
     */
    public void setReadingTypeList(com.cannontech.multispeak.deploy.service.ReadingType[] readingTypeList) {
        this.readingTypeList = readingTypeList;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof ReadingSchedule)) return false;
        ReadingSchedule other = (ReadingSchedule) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = super.equals(obj) && 
            ((this.meterRefList==null && other.getMeterRefList()==null) || 
             (this.meterRefList!=null &&
              java.util.Arrays.equals(this.meterRefList, other.getMeterRefList()))) &&
            ((this.meterGroupList==null && other.getMeterGroupList()==null) || 
             (this.meterGroupList!=null &&
              java.util.Arrays.equals(this.meterGroupList, other.getMeterGroupList()))) &&
            ((this.configurationGroupList==null && other.getConfigurationGroupList()==null) || 
             (this.configurationGroupList!=null &&
              java.util.Arrays.equals(this.configurationGroupList, other.getConfigurationGroupList()))) &&
            ((this.readingTypeList==null && other.getReadingTypeList()==null) || 
             (this.readingTypeList!=null &&
              java.util.Arrays.equals(this.readingTypeList, other.getReadingTypeList())));
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
        if (getMeterGroupList() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getMeterGroupList());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getMeterGroupList(), i);
                if (obj != null &&
                    !obj.getClass().isArray()) {
                    _hashCode += obj.hashCode();
                }
            }
        }
        if (getConfigurationGroupList() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getConfigurationGroupList());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getConfigurationGroupList(), i);
                if (obj != null &&
                    !obj.getClass().isArray()) {
                    _hashCode += obj.hashCode();
                }
            }
        }
        if (getReadingTypeList() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getReadingTypeList());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getReadingTypeList(), i);
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
        new org.apache.axis.description.TypeDesc(ReadingSchedule.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "readingSchedule"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("meterRefList");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "meterRefList"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "meterRef"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        elemField.setItemQName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "meterRef"));
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("meterGroupList");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "meterGroupList"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "meterGroup"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        elemField.setItemQName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "meterGroup"));
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("configurationGroupList");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "configurationGroupList"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        elemField.setItemQName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "configurationGroupID"));
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("readingTypeList");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "readingTypeList"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "readingType"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        elemField.setItemQName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "readingType"));
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
