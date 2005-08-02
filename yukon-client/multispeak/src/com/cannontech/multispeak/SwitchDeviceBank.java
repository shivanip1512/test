/**
 * SwitchDeviceBank.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.2 May 03, 2005 (02:20:24 EDT) WSDL2Java emitter.
 */

package com.cannontech.multispeak;

public class SwitchDeviceBank  extends com.cannontech.multispeak.MspSwitchingBank  implements java.io.Serializable {
    private com.cannontech.multispeak.MspSwitchDeviceList mspSwitchDeviceList;

    public SwitchDeviceBank() {
    }

    public SwitchDeviceBank(
           com.cannontech.multispeak.MspSwitchDeviceList mspSwitchDeviceList) {
           this.mspSwitchDeviceList = mspSwitchDeviceList;
    }


    /**
     * Gets the mspSwitchDeviceList value for this SwitchDeviceBank.
     * 
     * @return mspSwitchDeviceList
     */
    public com.cannontech.multispeak.MspSwitchDeviceList getMspSwitchDeviceList() {
        return mspSwitchDeviceList;
    }


    /**
     * Sets the mspSwitchDeviceList value for this SwitchDeviceBank.
     * 
     * @param mspSwitchDeviceList
     */
    public void setMspSwitchDeviceList(com.cannontech.multispeak.MspSwitchDeviceList mspSwitchDeviceList) {
        this.mspSwitchDeviceList = mspSwitchDeviceList;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof SwitchDeviceBank)) return false;
        SwitchDeviceBank other = (SwitchDeviceBank) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = super.equals(obj) && 
            ((this.mspSwitchDeviceList==null && other.getMspSwitchDeviceList()==null) || 
             (this.mspSwitchDeviceList!=null &&
              this.mspSwitchDeviceList.equals(other.getMspSwitchDeviceList())));
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
        if (getMspSwitchDeviceList() != null) {
            _hashCode += getMspSwitchDeviceList().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(SwitchDeviceBank.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "switchDeviceBank"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("mspSwitchDeviceList");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "mspSwitchDeviceList"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "mspSwitchDeviceList"));
        elemField.setMinOccurs(0);
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
