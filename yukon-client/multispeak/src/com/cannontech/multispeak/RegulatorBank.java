/**
 * RegulatorBank.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.2 May 03, 2005 (02:20:24 EDT) WSDL2Java emitter.
 */

package com.cannontech.multispeak;

public class RegulatorBank  extends com.cannontech.multispeak.MspBankObject  implements java.io.Serializable {
    private java.lang.String regType;
    private com.cannontech.multispeak.MspPhase ctrlPhase;
    private java.lang.String wdgType;
    private com.cannontech.multispeak.ArrayOfRegulator regulatorList;

    public RegulatorBank() {
    }

    public RegulatorBank(
           java.lang.String regType,
           com.cannontech.multispeak.MspPhase ctrlPhase,
           java.lang.String wdgType,
           com.cannontech.multispeak.ArrayOfRegulator regulatorList) {
           this.regType = regType;
           this.ctrlPhase = ctrlPhase;
           this.wdgType = wdgType;
           this.regulatorList = regulatorList;
    }


    /**
     * Gets the regType value for this RegulatorBank.
     * 
     * @return regType
     */
    public java.lang.String getRegType() {
        return regType;
    }


    /**
     * Sets the regType value for this RegulatorBank.
     * 
     * @param regType
     */
    public void setRegType(java.lang.String regType) {
        this.regType = regType;
    }


    /**
     * Gets the ctrlPhase value for this RegulatorBank.
     * 
     * @return ctrlPhase
     */
    public com.cannontech.multispeak.MspPhase getCtrlPhase() {
        return ctrlPhase;
    }


    /**
     * Sets the ctrlPhase value for this RegulatorBank.
     * 
     * @param ctrlPhase
     */
    public void setCtrlPhase(com.cannontech.multispeak.MspPhase ctrlPhase) {
        this.ctrlPhase = ctrlPhase;
    }


    /**
     * Gets the wdgType value for this RegulatorBank.
     * 
     * @return wdgType
     */
    public java.lang.String getWdgType() {
        return wdgType;
    }


    /**
     * Sets the wdgType value for this RegulatorBank.
     * 
     * @param wdgType
     */
    public void setWdgType(java.lang.String wdgType) {
        this.wdgType = wdgType;
    }


    /**
     * Gets the regulatorList value for this RegulatorBank.
     * 
     * @return regulatorList
     */
    public com.cannontech.multispeak.ArrayOfRegulator getRegulatorList() {
        return regulatorList;
    }


    /**
     * Sets the regulatorList value for this RegulatorBank.
     * 
     * @param regulatorList
     */
    public void setRegulatorList(com.cannontech.multispeak.ArrayOfRegulator regulatorList) {
        this.regulatorList = regulatorList;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof RegulatorBank)) return false;
        RegulatorBank other = (RegulatorBank) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = super.equals(obj) && 
            ((this.regType==null && other.getRegType()==null) || 
             (this.regType!=null &&
              this.regType.equals(other.getRegType()))) &&
            ((this.ctrlPhase==null && other.getCtrlPhase()==null) || 
             (this.ctrlPhase!=null &&
              this.ctrlPhase.equals(other.getCtrlPhase()))) &&
            ((this.wdgType==null && other.getWdgType()==null) || 
             (this.wdgType!=null &&
              this.wdgType.equals(other.getWdgType()))) &&
            ((this.regulatorList==null && other.getRegulatorList()==null) || 
             (this.regulatorList!=null &&
              this.regulatorList.equals(other.getRegulatorList())));
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
        if (getRegType() != null) {
            _hashCode += getRegType().hashCode();
        }
        if (getCtrlPhase() != null) {
            _hashCode += getCtrlPhase().hashCode();
        }
        if (getWdgType() != null) {
            _hashCode += getWdgType().hashCode();
        }
        if (getRegulatorList() != null) {
            _hashCode += getRegulatorList().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(RegulatorBank.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "regulatorBank"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("regType");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "regType"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("ctrlPhase");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ctrlPhase"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "mspPhase"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("wdgType");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "wdgType"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("regulatorList");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "regulatorList"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ArrayOfRegulator"));
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
