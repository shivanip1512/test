/**
 * Clearance.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Dec 28, 2007 (09:53:13 CST) WSDL2Java emitter.
 */

package com.cannontech.multispeak.deploy.service;

public class Clearance  extends com.cannontech.multispeak.deploy.service.MspObject  implements java.io.Serializable {
    private java.lang.String purpose;

    private java.lang.String clearanceGivenTo;

    private java.util.Calendar issuedDateTime;

    private java.util.Calendar releasedDateTime;

    public Clearance() {
    }

    public Clearance(
           java.lang.String objectID,
           com.cannontech.multispeak.deploy.service.Action verb,
           java.lang.String errorString,
           java.lang.String replaceID,
           java.lang.String utility,
           com.cannontech.multispeak.deploy.service.Extensions extensions,
           java.lang.String comments,
           com.cannontech.multispeak.deploy.service.ExtensionsItem[] extensionsList,
           java.lang.String purpose,
           java.lang.String clearanceGivenTo,
           java.util.Calendar issuedDateTime,
           java.util.Calendar releasedDateTime) {
        super(
            objectID,
            verb,
            errorString,
            replaceID,
            utility,
            extensions,
            comments,
            extensionsList);
        this.purpose = purpose;
        this.clearanceGivenTo = clearanceGivenTo;
        this.issuedDateTime = issuedDateTime;
        this.releasedDateTime = releasedDateTime;
    }


    /**
     * Gets the purpose value for this Clearance.
     * 
     * @return purpose
     */
    public java.lang.String getPurpose() {
        return purpose;
    }


    /**
     * Sets the purpose value for this Clearance.
     * 
     * @param purpose
     */
    public void setPurpose(java.lang.String purpose) {
        this.purpose = purpose;
    }


    /**
     * Gets the clearanceGivenTo value for this Clearance.
     * 
     * @return clearanceGivenTo
     */
    public java.lang.String getClearanceGivenTo() {
        return clearanceGivenTo;
    }


    /**
     * Sets the clearanceGivenTo value for this Clearance.
     * 
     * @param clearanceGivenTo
     */
    public void setClearanceGivenTo(java.lang.String clearanceGivenTo) {
        this.clearanceGivenTo = clearanceGivenTo;
    }


    /**
     * Gets the issuedDateTime value for this Clearance.
     * 
     * @return issuedDateTime
     */
    public java.util.Calendar getIssuedDateTime() {
        return issuedDateTime;
    }


    /**
     * Sets the issuedDateTime value for this Clearance.
     * 
     * @param issuedDateTime
     */
    public void setIssuedDateTime(java.util.Calendar issuedDateTime) {
        this.issuedDateTime = issuedDateTime;
    }


    /**
     * Gets the releasedDateTime value for this Clearance.
     * 
     * @return releasedDateTime
     */
    public java.util.Calendar getReleasedDateTime() {
        return releasedDateTime;
    }


    /**
     * Sets the releasedDateTime value for this Clearance.
     * 
     * @param releasedDateTime
     */
    public void setReleasedDateTime(java.util.Calendar releasedDateTime) {
        this.releasedDateTime = releasedDateTime;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof Clearance)) return false;
        Clearance other = (Clearance) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = super.equals(obj) && 
            ((this.purpose==null && other.getPurpose()==null) || 
             (this.purpose!=null &&
              this.purpose.equals(other.getPurpose()))) &&
            ((this.clearanceGivenTo==null && other.getClearanceGivenTo()==null) || 
             (this.clearanceGivenTo!=null &&
              this.clearanceGivenTo.equals(other.getClearanceGivenTo()))) &&
            ((this.issuedDateTime==null && other.getIssuedDateTime()==null) || 
             (this.issuedDateTime!=null &&
              this.issuedDateTime.equals(other.getIssuedDateTime()))) &&
            ((this.releasedDateTime==null && other.getReleasedDateTime()==null) || 
             (this.releasedDateTime!=null &&
              this.releasedDateTime.equals(other.getReleasedDateTime())));
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
        if (getPurpose() != null) {
            _hashCode += getPurpose().hashCode();
        }
        if (getClearanceGivenTo() != null) {
            _hashCode += getClearanceGivenTo().hashCode();
        }
        if (getIssuedDateTime() != null) {
            _hashCode += getIssuedDateTime().hashCode();
        }
        if (getReleasedDateTime() != null) {
            _hashCode += getReleasedDateTime().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(Clearance.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "clearance"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("purpose");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "purpose"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("clearanceGivenTo");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "clearanceGivenTo"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("issuedDateTime");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "issuedDateTime"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "dateTime"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("releasedDateTime");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "releasedDateTime"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "dateTime"));
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
