/**
 * MspOverCurrentDeviceList.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.2 May 03, 2005 (02:20:24 EDT) WSDL2Java emitter.
 */

package com.cannontech.multispeak.service;

public class MspOverCurrentDeviceList  implements java.io.Serializable {
    private com.cannontech.multispeak.service.Recloser recloser;
    private com.cannontech.multispeak.service.Fuse fuse;
    private com.cannontech.multispeak.service.Breaker breaker;
    private com.cannontech.multispeak.service.Sectionalizer sectionalizer;
    private com.cannontech.multispeak.service.MspOverCurrentDevice mspOverCurrentDevice;

    public MspOverCurrentDeviceList() {
    }

    public MspOverCurrentDeviceList(
           com.cannontech.multispeak.service.Recloser recloser,
           com.cannontech.multispeak.service.Fuse fuse,
           com.cannontech.multispeak.service.Breaker breaker,
           com.cannontech.multispeak.service.Sectionalizer sectionalizer,
           com.cannontech.multispeak.service.MspOverCurrentDevice mspOverCurrentDevice) {
           this.recloser = recloser;
           this.fuse = fuse;
           this.breaker = breaker;
           this.sectionalizer = sectionalizer;
           this.mspOverCurrentDevice = mspOverCurrentDevice;
    }


    /**
     * Gets the recloser value for this MspOverCurrentDeviceList.
     * 
     * @return recloser
     */
    public com.cannontech.multispeak.service.Recloser getRecloser() {
        return recloser;
    }


    /**
     * Sets the recloser value for this MspOverCurrentDeviceList.
     * 
     * @param recloser
     */
    public void setRecloser(com.cannontech.multispeak.service.Recloser recloser) {
        this.recloser = recloser;
    }


    /**
     * Gets the fuse value for this MspOverCurrentDeviceList.
     * 
     * @return fuse
     */
    public com.cannontech.multispeak.service.Fuse getFuse() {
        return fuse;
    }


    /**
     * Sets the fuse value for this MspOverCurrentDeviceList.
     * 
     * @param fuse
     */
    public void setFuse(com.cannontech.multispeak.service.Fuse fuse) {
        this.fuse = fuse;
    }


    /**
     * Gets the breaker value for this MspOverCurrentDeviceList.
     * 
     * @return breaker
     */
    public com.cannontech.multispeak.service.Breaker getBreaker() {
        return breaker;
    }


    /**
     * Sets the breaker value for this MspOverCurrentDeviceList.
     * 
     * @param breaker
     */
    public void setBreaker(com.cannontech.multispeak.service.Breaker breaker) {
        this.breaker = breaker;
    }


    /**
     * Gets the sectionalizer value for this MspOverCurrentDeviceList.
     * 
     * @return sectionalizer
     */
    public com.cannontech.multispeak.service.Sectionalizer getSectionalizer() {
        return sectionalizer;
    }


    /**
     * Sets the sectionalizer value for this MspOverCurrentDeviceList.
     * 
     * @param sectionalizer
     */
    public void setSectionalizer(com.cannontech.multispeak.service.Sectionalizer sectionalizer) {
        this.sectionalizer = sectionalizer;
    }


    /**
     * Gets the mspOverCurrentDevice value for this MspOverCurrentDeviceList.
     * 
     * @return mspOverCurrentDevice
     */
    public com.cannontech.multispeak.service.MspOverCurrentDevice getMspOverCurrentDevice() {
        return mspOverCurrentDevice;
    }


    /**
     * Sets the mspOverCurrentDevice value for this MspOverCurrentDeviceList.
     * 
     * @param mspOverCurrentDevice
     */
    public void setMspOverCurrentDevice(com.cannontech.multispeak.service.MspOverCurrentDevice mspOverCurrentDevice) {
        this.mspOverCurrentDevice = mspOverCurrentDevice;
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
            ((this.recloser==null && other.getRecloser()==null) || 
             (this.recloser!=null &&
              this.recloser.equals(other.getRecloser()))) &&
            ((this.fuse==null && other.getFuse()==null) || 
             (this.fuse!=null &&
              this.fuse.equals(other.getFuse()))) &&
            ((this.breaker==null && other.getBreaker()==null) || 
             (this.breaker!=null &&
              this.breaker.equals(other.getBreaker()))) &&
            ((this.sectionalizer==null && other.getSectionalizer()==null) || 
             (this.sectionalizer!=null &&
              this.sectionalizer.equals(other.getSectionalizer()))) &&
            ((this.mspOverCurrentDevice==null && other.getMspOverCurrentDevice()==null) || 
             (this.mspOverCurrentDevice!=null &&
              this.mspOverCurrentDevice.equals(other.getMspOverCurrentDevice())));
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
        if (getRecloser() != null) {
            _hashCode += getRecloser().hashCode();
        }
        if (getFuse() != null) {
            _hashCode += getFuse().hashCode();
        }
        if (getBreaker() != null) {
            _hashCode += getBreaker().hashCode();
        }
        if (getSectionalizer() != null) {
            _hashCode += getSectionalizer().hashCode();
        }
        if (getMspOverCurrentDevice() != null) {
            _hashCode += getMspOverCurrentDevice().hashCode();
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
        elemField.setFieldName("recloser");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "recloser"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "recloser"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("fuse");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "fuse"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "fuse"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("breaker");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "breaker"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "breaker"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("sectionalizer");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "sectionalizer"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "sectionalizer"));
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
