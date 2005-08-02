/**
 * MspSwitchingBank.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.2 May 03, 2005 (02:20:24 EDT) WSDL2Java emitter.
 */

package com.cannontech.multispeak;

public abstract class MspSwitchingBank  extends com.cannontech.multispeak.MspBankObject  implements java.io.Serializable {
    private java.lang.Boolean isGanged;
    private com.cannontech.multispeak.ObjectRef partner;
    private java.lang.Long ldPoint;

    public MspSwitchingBank() {
    }

    public MspSwitchingBank(
           java.lang.Boolean isGanged,
           com.cannontech.multispeak.ObjectRef partner,
           java.lang.Long ldPoint) {
           this.isGanged = isGanged;
           this.partner = partner;
           this.ldPoint = ldPoint;
    }


    /**
     * Gets the isGanged value for this MspSwitchingBank.
     * 
     * @return isGanged
     */
    public java.lang.Boolean getIsGanged() {
        return isGanged;
    }


    /**
     * Sets the isGanged value for this MspSwitchingBank.
     * 
     * @param isGanged
     */
    public void setIsGanged(java.lang.Boolean isGanged) {
        this.isGanged = isGanged;
    }


    /**
     * Gets the partner value for this MspSwitchingBank.
     * 
     * @return partner
     */
    public com.cannontech.multispeak.ObjectRef getPartner() {
        return partner;
    }


    /**
     * Sets the partner value for this MspSwitchingBank.
     * 
     * @param partner
     */
    public void setPartner(com.cannontech.multispeak.ObjectRef partner) {
        this.partner = partner;
    }


    /**
     * Gets the ldPoint value for this MspSwitchingBank.
     * 
     * @return ldPoint
     */
    public java.lang.Long getLdPoint() {
        return ldPoint;
    }


    /**
     * Sets the ldPoint value for this MspSwitchingBank.
     * 
     * @param ldPoint
     */
    public void setLdPoint(java.lang.Long ldPoint) {
        this.ldPoint = ldPoint;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof MspSwitchingBank)) return false;
        MspSwitchingBank other = (MspSwitchingBank) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = super.equals(obj) && 
            ((this.isGanged==null && other.getIsGanged()==null) || 
             (this.isGanged!=null &&
              this.isGanged.equals(other.getIsGanged()))) &&
            ((this.partner==null && other.getPartner()==null) || 
             (this.partner!=null &&
              this.partner.equals(other.getPartner()))) &&
            ((this.ldPoint==null && other.getLdPoint()==null) || 
             (this.ldPoint!=null &&
              this.ldPoint.equals(other.getLdPoint())));
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
        if (getIsGanged() != null) {
            _hashCode += getIsGanged().hashCode();
        }
        if (getPartner() != null) {
            _hashCode += getPartner().hashCode();
        }
        if (getLdPoint() != null) {
            _hashCode += getLdPoint().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(MspSwitchingBank.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "mspSwitchingBank"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("isGanged");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "isGanged"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "boolean"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("partner");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "partner"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "objectRef"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("ldPoint");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ldPoint"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "long"));
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
