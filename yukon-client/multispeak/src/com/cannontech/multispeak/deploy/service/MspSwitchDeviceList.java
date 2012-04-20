/**
 * MspSwitchDeviceList.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Dec 28, 2007 (09:53:13 CST) WSDL2Java emitter.
 */

package com.cannontech.multispeak.deploy.service;

public class MspSwitchDeviceList  implements java.io.Serializable {
    private com.cannontech.multispeak.deploy.service.Cut cut;

    private com.cannontech.multispeak.deploy.service.MspOverCurrentDevice mspOverCurrentDevice;

    private com.cannontech.multispeak.deploy.service.MspSwitchingDevice _switch;

    private com.cannontech.multispeak.deploy.service.Jumper jumper;

    private com.cannontech.multispeak.deploy.service.Elbow elbow;

    private com.cannontech.multispeak.deploy.service.MspSwitchingDevice mspSwitchingDevice;

    public MspSwitchDeviceList() {
    }

    public MspSwitchDeviceList(
           com.cannontech.multispeak.deploy.service.Cut cut,
           com.cannontech.multispeak.deploy.service.MspOverCurrentDevice mspOverCurrentDevice,
           com.cannontech.multispeak.deploy.service.MspSwitchingDevice _switch,
           com.cannontech.multispeak.deploy.service.Jumper jumper,
           com.cannontech.multispeak.deploy.service.Elbow elbow,
           com.cannontech.multispeak.deploy.service.MspSwitchingDevice mspSwitchingDevice) {
           this.cut = cut;
           this.mspOverCurrentDevice = mspOverCurrentDevice;
           this._switch = _switch;
           this.jumper = jumper;
           this.elbow = elbow;
           this.mspSwitchingDevice = mspSwitchingDevice;
    }


    /**
     * Gets the cut value for this MspSwitchDeviceList.
     * 
     * @return cut
     */
    public com.cannontech.multispeak.deploy.service.Cut getCut() {
        return cut;
    }


    /**
     * Sets the cut value for this MspSwitchDeviceList.
     * 
     * @param cut
     */
    public void setCut(com.cannontech.multispeak.deploy.service.Cut cut) {
        this.cut = cut;
    }


    /**
     * Gets the mspOverCurrentDevice value for this MspSwitchDeviceList.
     * 
     * @return mspOverCurrentDevice
     */
    public com.cannontech.multispeak.deploy.service.MspOverCurrentDevice getMspOverCurrentDevice() {
        return mspOverCurrentDevice;
    }


    /**
     * Sets the mspOverCurrentDevice value for this MspSwitchDeviceList.
     * 
     * @param mspOverCurrentDevice
     */
    public void setMspOverCurrentDevice(com.cannontech.multispeak.deploy.service.MspOverCurrentDevice mspOverCurrentDevice) {
        this.mspOverCurrentDevice = mspOverCurrentDevice;
    }


    /**
     * Gets the _switch value for this MspSwitchDeviceList.
     * 
     * @return _switch
     */
    public com.cannontech.multispeak.deploy.service.MspSwitchingDevice get_switch() {
        return _switch;
    }


    /**
     * Sets the _switch value for this MspSwitchDeviceList.
     * 
     * @param _switch
     */
    public void set_switch(com.cannontech.multispeak.deploy.service.MspSwitchingDevice _switch) {
        this._switch = _switch;
    }


    /**
     * Gets the jumper value for this MspSwitchDeviceList.
     * 
     * @return jumper
     */
    public com.cannontech.multispeak.deploy.service.Jumper getJumper() {
        return jumper;
    }


    /**
     * Sets the jumper value for this MspSwitchDeviceList.
     * 
     * @param jumper
     */
    public void setJumper(com.cannontech.multispeak.deploy.service.Jumper jumper) {
        this.jumper = jumper;
    }


    /**
     * Gets the elbow value for this MspSwitchDeviceList.
     * 
     * @return elbow
     */
    public com.cannontech.multispeak.deploy.service.Elbow getElbow() {
        return elbow;
    }


    /**
     * Sets the elbow value for this MspSwitchDeviceList.
     * 
     * @param elbow
     */
    public void setElbow(com.cannontech.multispeak.deploy.service.Elbow elbow) {
        this.elbow = elbow;
    }


    /**
     * Gets the mspSwitchingDevice value for this MspSwitchDeviceList.
     * 
     * @return mspSwitchingDevice
     */
    public com.cannontech.multispeak.deploy.service.MspSwitchingDevice getMspSwitchingDevice() {
        return mspSwitchingDevice;
    }


    /**
     * Sets the mspSwitchingDevice value for this MspSwitchDeviceList.
     * 
     * @param mspSwitchingDevice
     */
    public void setMspSwitchingDevice(com.cannontech.multispeak.deploy.service.MspSwitchingDevice mspSwitchingDevice) {
        this.mspSwitchingDevice = mspSwitchingDevice;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof MspSwitchDeviceList)) return false;
        MspSwitchDeviceList other = (MspSwitchDeviceList) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.cut==null && other.getCut()==null) || 
             (this.cut!=null &&
              this.cut.equals(other.getCut()))) &&
            ((this.mspOverCurrentDevice==null && other.getMspOverCurrentDevice()==null) || 
             (this.mspOverCurrentDevice!=null &&
              this.mspOverCurrentDevice.equals(other.getMspOverCurrentDevice()))) &&
            ((this._switch==null && other.get_switch()==null) || 
             (this._switch!=null &&
              this._switch.equals(other.get_switch()))) &&
            ((this.jumper==null && other.getJumper()==null) || 
             (this.jumper!=null &&
              this.jumper.equals(other.getJumper()))) &&
            ((this.elbow==null && other.getElbow()==null) || 
             (this.elbow!=null &&
              this.elbow.equals(other.getElbow()))) &&
            ((this.mspSwitchingDevice==null && other.getMspSwitchingDevice()==null) || 
             (this.mspSwitchingDevice!=null &&
              this.mspSwitchingDevice.equals(other.getMspSwitchingDevice())));
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
        if (getCut() != null) {
            _hashCode += getCut().hashCode();
        }
        if (getMspOverCurrentDevice() != null) {
            _hashCode += getMspOverCurrentDevice().hashCode();
        }
        if (get_switch() != null) {
            _hashCode += get_switch().hashCode();
        }
        if (getJumper() != null) {
            _hashCode += getJumper().hashCode();
        }
        if (getElbow() != null) {
            _hashCode += getElbow().hashCode();
        }
        if (getMspSwitchingDevice() != null) {
            _hashCode += getMspSwitchingDevice().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(MspSwitchDeviceList.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "mspSwitchDeviceList"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("cut");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "cut"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "cut"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("mspOverCurrentDevice");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "mspOverCurrentDevice"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "mspOverCurrentDevice"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("_switch");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "switch"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "mspSwitchingDevice"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("jumper");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "jumper"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "jumper"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("elbow");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "elbow"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "elbow"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("mspSwitchingDevice");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "mspSwitchingDevice"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "mspSwitchingDevice"));
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
