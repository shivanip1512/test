/**
 * RequestedNumber.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Dec 28, 2007 (09:53:13 CST) WSDL2Java emitter.
 */

package com.cannontech.multispeak.deploy.service;

public class RequestedNumber  implements java.io.Serializable {
    private java.lang.String primaryNumber;

    private java.lang.String secondaryNumber;

    private com.cannontech.multispeak.deploy.service.NumberType numberType;

    public RequestedNumber() {
    }

    public RequestedNumber(
           java.lang.String primaryNumber,
           java.lang.String secondaryNumber,
           com.cannontech.multispeak.deploy.service.NumberType numberType) {
           this.primaryNumber = primaryNumber;
           this.secondaryNumber = secondaryNumber;
           this.numberType = numberType;
    }


    /**
     * Gets the primaryNumber value for this RequestedNumber.
     * 
     * @return primaryNumber
     */
    public java.lang.String getPrimaryNumber() {
        return primaryNumber;
    }


    /**
     * Sets the primaryNumber value for this RequestedNumber.
     * 
     * @param primaryNumber
     */
    public void setPrimaryNumber(java.lang.String primaryNumber) {
        this.primaryNumber = primaryNumber;
    }


    /**
     * Gets the secondaryNumber value for this RequestedNumber.
     * 
     * @return secondaryNumber
     */
    public java.lang.String getSecondaryNumber() {
        return secondaryNumber;
    }


    /**
     * Sets the secondaryNumber value for this RequestedNumber.
     * 
     * @param secondaryNumber
     */
    public void setSecondaryNumber(java.lang.String secondaryNumber) {
        this.secondaryNumber = secondaryNumber;
    }


    /**
     * Gets the numberType value for this RequestedNumber.
     * 
     * @return numberType
     */
    public com.cannontech.multispeak.deploy.service.NumberType getNumberType() {
        return numberType;
    }


    /**
     * Sets the numberType value for this RequestedNumber.
     * 
     * @param numberType
     */
    public void setNumberType(com.cannontech.multispeak.deploy.service.NumberType numberType) {
        this.numberType = numberType;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof RequestedNumber)) return false;
        RequestedNumber other = (RequestedNumber) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.primaryNumber==null && other.getPrimaryNumber()==null) || 
             (this.primaryNumber!=null &&
              this.primaryNumber.equals(other.getPrimaryNumber()))) &&
            ((this.secondaryNumber==null && other.getSecondaryNumber()==null) || 
             (this.secondaryNumber!=null &&
              this.secondaryNumber.equals(other.getSecondaryNumber()))) &&
            ((this.numberType==null && other.getNumberType()==null) || 
             (this.numberType!=null &&
              this.numberType.equals(other.getNumberType())));
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
        if (getPrimaryNumber() != null) {
            _hashCode += getPrimaryNumber().hashCode();
        }
        if (getSecondaryNumber() != null) {
            _hashCode += getSecondaryNumber().hashCode();
        }
        if (getNumberType() != null) {
            _hashCode += getNumberType().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(RequestedNumber.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "requestedNumber"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("primaryNumber");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "primaryNumber"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("secondaryNumber");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "secondaryNumber"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("numberType");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "numberType"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "numberType"));
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
