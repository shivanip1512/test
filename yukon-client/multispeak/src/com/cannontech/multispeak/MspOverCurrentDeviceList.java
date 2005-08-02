/**
 * MspOverCurrentDeviceList.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.2 May 03, 2005 (02:20:24 EDT) WSDL2Java emitter.
 */

package com.cannontech.multispeak;

public class MspOverCurrentDeviceList  implements java.io.Serializable {
    private com.cannontech.multispeak.MspOverCurrentDevice[] mspOverCurrentDevice;

    public MspOverCurrentDeviceList() {
    }

    public MspOverCurrentDeviceList(
           com.cannontech.multispeak.MspOverCurrentDevice[] mspOverCurrentDevice) {
           this.mspOverCurrentDevice = mspOverCurrentDevice;
    }


    /**
     * Gets the mspOverCurrentDevice value for this MspOverCurrentDeviceList.
     * 
     * @return mspOverCurrentDevice
     */
    public com.cannontech.multispeak.MspOverCurrentDevice[] getMspOverCurrentDevice() {
        return mspOverCurrentDevice;
    }


    /**
     * Sets the mspOverCurrentDevice value for this MspOverCurrentDeviceList.
     * 
     * @param mspOverCurrentDevice
     */
    public void setMspOverCurrentDevice(com.cannontech.multispeak.MspOverCurrentDevice[] mspOverCurrentDevice) {
        this.mspOverCurrentDevice = mspOverCurrentDevice;
    }

    public com.cannontech.multispeak.MspOverCurrentDevice getMspOverCurrentDevice(int i) {
        return this.mspOverCurrentDevice[i];
    }

    public void setMspOverCurrentDevice(int i, com.cannontech.multispeak.MspOverCurrentDevice _value) {
        this.mspOverCurrentDevice[i] = _value;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof MspOverCurrentDeviceList)) return false;
        MspOverCurrentDeviceList other = (MspOverCurrentDeviceList) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.mspOverCurrentDevice==null && other.getMspOverCurrentDevice()==null) || 
             (this.mspOverCurrentDevice!=null &&
              java.util.Arrays.equals(this.mspOverCurrentDevice, other.getMspOverCurrentDevice())));
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
        if (getMspOverCurrentDevice() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getMspOverCurrentDevice());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getMspOverCurrentDevice(), i);
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
        new org.apache.axis.description.TypeDesc(MspOverCurrentDeviceList.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "mspOverCurrentDeviceList"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("mspOverCurrentDevice");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "mspOverCurrentDevice"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "mspOverCurrentDevice"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        elemField.setMaxOccursUnbounded(true);
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
