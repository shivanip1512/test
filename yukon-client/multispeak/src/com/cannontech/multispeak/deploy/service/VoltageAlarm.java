/**
 * VoltageAlarm.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Dec 28, 2007 (09:53:13 CST) WSDL2Java emitter.
 */

package com.cannontech.multispeak.deploy.service;

public class VoltageAlarm  extends com.cannontech.multispeak.deploy.service.MspAlarm  implements java.io.Serializable {
    private com.cannontech.multispeak.deploy.service.VoltageAlarmItem[] voltageAlarmList;

    public VoltageAlarm() {
    }

    public VoltageAlarm(
           java.lang.String objectID,
           com.cannontech.multispeak.deploy.service.Action verb,
           java.lang.String errorString,
           java.lang.String replaceID,
           java.lang.String utility,
           com.cannontech.multispeak.deploy.service.Extensions extensions,
           java.lang.String comments,
           com.cannontech.multispeak.deploy.service.ExtensionsItem[] extensionsList,
           com.cannontech.multispeak.deploy.service.ObjectRef sourceIdentifier,
           java.util.Calendar eventTime,
           com.cannontech.multispeak.deploy.service.MeterEvent eventCode,
           com.cannontech.multispeak.deploy.service.VoltageAlarmItem[] voltageAlarmList) {
        super(
            objectID,
            verb,
            errorString,
            replaceID,
            utility,
            extensions,
            comments,
            extensionsList,
            sourceIdentifier,
            eventTime,
            eventCode);
        this.voltageAlarmList = voltageAlarmList;
    }


    /**
     * Gets the voltageAlarmList value for this VoltageAlarm.
     * 
     * @return voltageAlarmList
     */
    public com.cannontech.multispeak.deploy.service.VoltageAlarmItem[] getVoltageAlarmList() {
        return voltageAlarmList;
    }


    /**
     * Sets the voltageAlarmList value for this VoltageAlarm.
     * 
     * @param voltageAlarmList
     */
    public void setVoltageAlarmList(com.cannontech.multispeak.deploy.service.VoltageAlarmItem[] voltageAlarmList) {
        this.voltageAlarmList = voltageAlarmList;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof VoltageAlarm)) return false;
        VoltageAlarm other = (VoltageAlarm) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = super.equals(obj) && 
            ((this.voltageAlarmList==null && other.getVoltageAlarmList()==null) || 
             (this.voltageAlarmList!=null &&
              java.util.Arrays.equals(this.voltageAlarmList, other.getVoltageAlarmList())));
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
        if (getVoltageAlarmList() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getVoltageAlarmList());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getVoltageAlarmList(), i);
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
        new org.apache.axis.description.TypeDesc(VoltageAlarm.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "voltageAlarm"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("voltageAlarmList");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "voltageAlarmList"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "voltageAlarmItem"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        elemField.setItemQName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "voltageAlarmItem"));
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
