/**
 * OvercurrentDeviceBank.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.2 May 03, 2005 (02:20:24 EDT) WSDL2Java emitter.
 */

package com.cannontech.multispeak;

public class OvercurrentDeviceBank  extends com.cannontech.multispeak.MspSwitchingBank  implements java.io.Serializable {
    private com.cannontech.multispeak.MspOverCurrentDeviceList mspOverCurrentDeviceList;

    public OvercurrentDeviceBank() {
    }

    public OvercurrentDeviceBank(
           com.cannontech.multispeak.MspOverCurrentDeviceList mspOverCurrentDeviceList) {
           this.mspOverCurrentDeviceList = mspOverCurrentDeviceList;
    }


    /**
     * Gets the mspOverCurrentDeviceList value for this OvercurrentDeviceBank.
     * 
     * @return mspOverCurrentDeviceList
     */
    public com.cannontech.multispeak.MspOverCurrentDeviceList getMspOverCurrentDeviceList() {
        return mspOverCurrentDeviceList;
    }


    /**
     * Sets the mspOverCurrentDeviceList value for this OvercurrentDeviceBank.
     * 
     * @param mspOverCurrentDeviceList
     */
    public void setMspOverCurrentDeviceList(com.cannontech.multispeak.MspOverCurrentDeviceList mspOverCurrentDeviceList) {
        this.mspOverCurrentDeviceList = mspOverCurrentDeviceList;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof OvercurrentDeviceBank)) return false;
        OvercurrentDeviceBank other = (OvercurrentDeviceBank) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = super.equals(obj) && 
            ((this.mspOverCurrentDeviceList==null && other.getMspOverCurrentDeviceList()==null) || 
             (this.mspOverCurrentDeviceList!=null &&
              this.mspOverCurrentDeviceList.equals(other.getMspOverCurrentDeviceList())));
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
        if (getMspOverCurrentDeviceList() != null) {
            _hashCode += getMspOverCurrentDeviceList().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(OvercurrentDeviceBank.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "overcurrentDeviceBank"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("mspOverCurrentDeviceList");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "mspOverCurrentDeviceList"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "mspOverCurrentDeviceList"));
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
