/**
 * TestedElectricMeter.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Dec 28, 2007 (09:53:13 CST) WSDL2Java emitter.
 */

package com.cannontech.multispeak.deploy.service;

public class TestedElectricMeter  extends com.cannontech.multispeak.deploy.service.ReceivedElectricMeter  implements java.io.Serializable {
    private com.cannontech.multispeak.deploy.service.MeterTest[] meterTestList;

    public TestedElectricMeter() {
    }

    public TestedElectricMeter(
           java.lang.String objectID,
           com.cannontech.multispeak.deploy.service.Action verb,
           java.lang.String errorString,
           java.lang.String replaceID,
           java.lang.String utility,
           com.cannontech.multispeak.deploy.service.Extensions extensions,
           java.lang.String comments,
           com.cannontech.multispeak.deploy.service.ExtensionsItem[] extensionsList,
           java.lang.String meterNo,
           java.lang.String manufacturer,
           java.lang.String catalogNumber,
           java.lang.String serialNumber,
           java.lang.String metrologyFirmwareVersion,
           java.lang.String metrologyFirmwareRevision,
           java.lang.String meterType,
           java.lang.String AMRDeviceType,
           java.lang.String AMRVendor,
           java.lang.String transponderID,
           com.cannontech.multispeak.deploy.service.Module[] moduleList,
           com.cannontech.multispeak.deploy.service.ElectricNameplate electricNameplate,
           com.cannontech.multispeak.deploy.service.MeterTest[] meterTestList) {
        super(
            objectID,
            verb,
            errorString,
            replaceID,
            utility,
            extensions,
            comments,
            extensionsList,
            meterNo,
            manufacturer,
            catalogNumber,
            serialNumber,
            metrologyFirmwareVersion,
            metrologyFirmwareRevision,
            meterType,
            AMRDeviceType,
            AMRVendor,
            transponderID,
            moduleList,
            electricNameplate);
        this.meterTestList = meterTestList;
    }


    /**
     * Gets the meterTestList value for this TestedElectricMeter.
     * 
     * @return meterTestList
     */
    public com.cannontech.multispeak.deploy.service.MeterTest[] getMeterTestList() {
        return meterTestList;
    }


    /**
     * Sets the meterTestList value for this TestedElectricMeter.
     * 
     * @param meterTestList
     */
    public void setMeterTestList(com.cannontech.multispeak.deploy.service.MeterTest[] meterTestList) {
        this.meterTestList = meterTestList;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof TestedElectricMeter)) return false;
        TestedElectricMeter other = (TestedElectricMeter) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = super.equals(obj) && 
            ((this.meterTestList==null && other.getMeterTestList()==null) || 
             (this.meterTestList!=null &&
              java.util.Arrays.equals(this.meterTestList, other.getMeterTestList())));
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
        if (getMeterTestList() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getMeterTestList());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getMeterTestList(), i);
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
        new org.apache.axis.description.TypeDesc(TestedElectricMeter.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "testedElectricMeter"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("meterTestList");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "meterTestList"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "meterTest"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        elemField.setItemQName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "meterTest"));
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
