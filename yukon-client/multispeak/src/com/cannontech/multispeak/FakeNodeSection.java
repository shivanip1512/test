/**
 * FakeNodeSection.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.2 May 03, 2005 (02:20:24 EDT) WSDL2Java emitter.
 */

package com.cannontech.multispeak;

public class FakeNodeSection  extends com.cannontech.multispeak.MspElectricPoint  implements java.io.Serializable {
    private java.lang.Boolean ldAlloc;
    private java.lang.Boolean isRequired;
    private java.math.BigInteger cktLvl;
    private java.lang.Boolean isMultiparent;

    public FakeNodeSection() {
    }

    public FakeNodeSection(
           java.lang.Boolean ldAlloc,
           java.lang.Boolean isRequired,
           java.math.BigInteger cktLvl,
           java.lang.Boolean isMultiparent) {
           this.ldAlloc = ldAlloc;
           this.isRequired = isRequired;
           this.cktLvl = cktLvl;
           this.isMultiparent = isMultiparent;
    }


    /**
     * Gets the ldAlloc value for this FakeNodeSection.
     * 
     * @return ldAlloc
     */
    public java.lang.Boolean getLdAlloc() {
        return ldAlloc;
    }


    /**
     * Sets the ldAlloc value for this FakeNodeSection.
     * 
     * @param ldAlloc
     */
    public void setLdAlloc(java.lang.Boolean ldAlloc) {
        this.ldAlloc = ldAlloc;
    }


    /**
     * Gets the isRequired value for this FakeNodeSection.
     * 
     * @return isRequired
     */
    public java.lang.Boolean getIsRequired() {
        return isRequired;
    }


    /**
     * Sets the isRequired value for this FakeNodeSection.
     * 
     * @param isRequired
     */
    public void setIsRequired(java.lang.Boolean isRequired) {
        this.isRequired = isRequired;
    }


    /**
     * Gets the cktLvl value for this FakeNodeSection.
     * 
     * @return cktLvl
     */
    public java.math.BigInteger getCktLvl() {
        return cktLvl;
    }


    /**
     * Sets the cktLvl value for this FakeNodeSection.
     * 
     * @param cktLvl
     */
    public void setCktLvl(java.math.BigInteger cktLvl) {
        this.cktLvl = cktLvl;
    }


    /**
     * Gets the isMultiparent value for this FakeNodeSection.
     * 
     * @return isMultiparent
     */
    public java.lang.Boolean getIsMultiparent() {
        return isMultiparent;
    }


    /**
     * Sets the isMultiparent value for this FakeNodeSection.
     * 
     * @param isMultiparent
     */
    public void setIsMultiparent(java.lang.Boolean isMultiparent) {
        this.isMultiparent = isMultiparent;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof FakeNodeSection)) return false;
        FakeNodeSection other = (FakeNodeSection) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = super.equals(obj) && 
            ((this.ldAlloc==null && other.getLdAlloc()==null) || 
             (this.ldAlloc!=null &&
              this.ldAlloc.equals(other.getLdAlloc()))) &&
            ((this.isRequired==null && other.getIsRequired()==null) || 
             (this.isRequired!=null &&
              this.isRequired.equals(other.getIsRequired()))) &&
            ((this.cktLvl==null && other.getCktLvl()==null) || 
             (this.cktLvl!=null &&
              this.cktLvl.equals(other.getCktLvl()))) &&
            ((this.isMultiparent==null && other.getIsMultiparent()==null) || 
             (this.isMultiparent!=null &&
              this.isMultiparent.equals(other.getIsMultiparent())));
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
        if (getLdAlloc() != null) {
            _hashCode += getLdAlloc().hashCode();
        }
        if (getIsRequired() != null) {
            _hashCode += getIsRequired().hashCode();
        }
        if (getCktLvl() != null) {
            _hashCode += getCktLvl().hashCode();
        }
        if (getIsMultiparent() != null) {
            _hashCode += getIsMultiparent().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(FakeNodeSection.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "fakeNodeSection"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("ldAlloc");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ldAlloc"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "boolean"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("isRequired");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "isRequired"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "boolean"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("cktLvl");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "cktLvl"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "integer"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("isMultiparent");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "isMultiparent"));
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
