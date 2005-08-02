/**
 * JointUse.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.2 May 03, 2005 (02:20:24 EDT) WSDL2Java emitter.
 */

package com.cannontech.multispeak;

public class JointUse  implements java.io.Serializable {
    private java.lang.String companyID;
    private java.lang.String jUseType;
    private java.lang.Float jUseHeight;
    private java.lang.Boolean isGuyed;

    public JointUse() {
    }

    public JointUse(
           java.lang.String companyID,
           java.lang.String jUseType,
           java.lang.Float jUseHeight,
           java.lang.Boolean isGuyed) {
           this.companyID = companyID;
           this.jUseType = jUseType;
           this.jUseHeight = jUseHeight;
           this.isGuyed = isGuyed;
    }


    /**
     * Gets the companyID value for this JointUse.
     * 
     * @return companyID
     */
    public java.lang.String getCompanyID() {
        return companyID;
    }


    /**
     * Sets the companyID value for this JointUse.
     * 
     * @param companyID
     */
    public void setCompanyID(java.lang.String companyID) {
        this.companyID = companyID;
    }


    /**
     * Gets the jUseType value for this JointUse.
     * 
     * @return jUseType
     */
    public java.lang.String getJUseType() {
        return jUseType;
    }


    /**
     * Sets the jUseType value for this JointUse.
     * 
     * @param jUseType
     */
    public void setJUseType(java.lang.String jUseType) {
        this.jUseType = jUseType;
    }


    /**
     * Gets the jUseHeight value for this JointUse.
     * 
     * @return jUseHeight
     */
    public java.lang.Float getJUseHeight() {
        return jUseHeight;
    }


    /**
     * Sets the jUseHeight value for this JointUse.
     * 
     * @param jUseHeight
     */
    public void setJUseHeight(java.lang.Float jUseHeight) {
        this.jUseHeight = jUseHeight;
    }


    /**
     * Gets the isGuyed value for this JointUse.
     * 
     * @return isGuyed
     */
    public java.lang.Boolean getIsGuyed() {
        return isGuyed;
    }


    /**
     * Sets the isGuyed value for this JointUse.
     * 
     * @param isGuyed
     */
    public void setIsGuyed(java.lang.Boolean isGuyed) {
        this.isGuyed = isGuyed;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof JointUse)) return false;
        JointUse other = (JointUse) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.companyID==null && other.getCompanyID()==null) || 
             (this.companyID!=null &&
              this.companyID.equals(other.getCompanyID()))) &&
            ((this.jUseType==null && other.getJUseType()==null) || 
             (this.jUseType!=null &&
              this.jUseType.equals(other.getJUseType()))) &&
            ((this.jUseHeight==null && other.getJUseHeight()==null) || 
             (this.jUseHeight!=null &&
              this.jUseHeight.equals(other.getJUseHeight()))) &&
            ((this.isGuyed==null && other.getIsGuyed()==null) || 
             (this.isGuyed!=null &&
              this.isGuyed.equals(other.getIsGuyed())));
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
        if (getCompanyID() != null) {
            _hashCode += getCompanyID().hashCode();
        }
        if (getJUseType() != null) {
            _hashCode += getJUseType().hashCode();
        }
        if (getJUseHeight() != null) {
            _hashCode += getJUseHeight().hashCode();
        }
        if (getIsGuyed() != null) {
            _hashCode += getIsGuyed().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(JointUse.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "jointUse"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("companyID");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "companyID"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("JUseType");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "jUseType"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("JUseHeight");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "jUseHeight"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "float"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("isGuyed");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "isGuyed"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "boolean"));
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
