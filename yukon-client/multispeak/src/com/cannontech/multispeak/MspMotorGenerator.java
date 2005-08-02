/**
 * MspMotorGenerator.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.2 May 03, 2005 (02:20:24 EDT) WSDL2Java emitter.
 */

package com.cannontech.multispeak;

public abstract class MspMotorGenerator  extends com.cannontech.multispeak.MspElectricPoint  implements java.io.Serializable {
    private java.lang.String ssDesc;
    private java.lang.String tranDesc;
    private java.lang.String stDesc;
    private java.lang.Float rtdVolts;

    public MspMotorGenerator() {
    }

    public MspMotorGenerator(
           java.lang.String ssDesc,
           java.lang.String tranDesc,
           java.lang.String stDesc,
           java.lang.Float rtdVolts) {
           this.ssDesc = ssDesc;
           this.tranDesc = tranDesc;
           this.stDesc = stDesc;
           this.rtdVolts = rtdVolts;
    }


    /**
     * Gets the ssDesc value for this MspMotorGenerator.
     * 
     * @return ssDesc
     */
    public java.lang.String getSsDesc() {
        return ssDesc;
    }


    /**
     * Sets the ssDesc value for this MspMotorGenerator.
     * 
     * @param ssDesc
     */
    public void setSsDesc(java.lang.String ssDesc) {
        this.ssDesc = ssDesc;
    }


    /**
     * Gets the tranDesc value for this MspMotorGenerator.
     * 
     * @return tranDesc
     */
    public java.lang.String getTranDesc() {
        return tranDesc;
    }


    /**
     * Sets the tranDesc value for this MspMotorGenerator.
     * 
     * @param tranDesc
     */
    public void setTranDesc(java.lang.String tranDesc) {
        this.tranDesc = tranDesc;
    }


    /**
     * Gets the stDesc value for this MspMotorGenerator.
     * 
     * @return stDesc
     */
    public java.lang.String getStDesc() {
        return stDesc;
    }


    /**
     * Sets the stDesc value for this MspMotorGenerator.
     * 
     * @param stDesc
     */
    public void setStDesc(java.lang.String stDesc) {
        this.stDesc = stDesc;
    }


    /**
     * Gets the rtdVolts value for this MspMotorGenerator.
     * 
     * @return rtdVolts
     */
    public java.lang.Float getRtdVolts() {
        return rtdVolts;
    }


    /**
     * Sets the rtdVolts value for this MspMotorGenerator.
     * 
     * @param rtdVolts
     */
    public void setRtdVolts(java.lang.Float rtdVolts) {
        this.rtdVolts = rtdVolts;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof MspMotorGenerator)) return false;
        MspMotorGenerator other = (MspMotorGenerator) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = super.equals(obj) && 
            ((this.ssDesc==null && other.getSsDesc()==null) || 
             (this.ssDesc!=null &&
              this.ssDesc.equals(other.getSsDesc()))) &&
            ((this.tranDesc==null && other.getTranDesc()==null) || 
             (this.tranDesc!=null &&
              this.tranDesc.equals(other.getTranDesc()))) &&
            ((this.stDesc==null && other.getStDesc()==null) || 
             (this.stDesc!=null &&
              this.stDesc.equals(other.getStDesc()))) &&
            ((this.rtdVolts==null && other.getRtdVolts()==null) || 
             (this.rtdVolts!=null &&
              this.rtdVolts.equals(other.getRtdVolts())));
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
        if (getSsDesc() != null) {
            _hashCode += getSsDesc().hashCode();
        }
        if (getTranDesc() != null) {
            _hashCode += getTranDesc().hashCode();
        }
        if (getStDesc() != null) {
            _hashCode += getStDesc().hashCode();
        }
        if (getRtdVolts() != null) {
            _hashCode += getRtdVolts().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(MspMotorGenerator.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "mspMotorGenerator"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("ssDesc");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ssDesc"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("tranDesc");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "tranDesc"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("stDesc");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "stDesc"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("rtdVolts");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "rtdVolts"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "float"));
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
