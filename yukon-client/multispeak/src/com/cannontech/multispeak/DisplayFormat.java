/**
 * DisplayFormat.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.2 May 03, 2005 (02:20:24 EDT) WSDL2Java emitter.
 */

package com.cannontech.multispeak;

public class DisplayFormat  implements java.io.Serializable {
    private java.lang.Boolean supressLeadingZero;
    private org.apache.axis.types.UnsignedByte numberOfDigits;
    private org.apache.axis.types.UnsignedByte precision;
    private java.lang.Double displayMultiplier;

    public DisplayFormat() {
    }

    public DisplayFormat(
           java.lang.Boolean supressLeadingZero,
           org.apache.axis.types.UnsignedByte numberOfDigits,
           org.apache.axis.types.UnsignedByte precision,
           java.lang.Double displayMultiplier) {
           this.supressLeadingZero = supressLeadingZero;
           this.numberOfDigits = numberOfDigits;
           this.precision = precision;
           this.displayMultiplier = displayMultiplier;
    }


    /**
     * Gets the supressLeadingZero value for this DisplayFormat.
     * 
     * @return supressLeadingZero
     */
    public java.lang.Boolean getSupressLeadingZero() {
        return supressLeadingZero;
    }


    /**
     * Sets the supressLeadingZero value for this DisplayFormat.
     * 
     * @param supressLeadingZero
     */
    public void setSupressLeadingZero(java.lang.Boolean supressLeadingZero) {
        this.supressLeadingZero = supressLeadingZero;
    }


    /**
     * Gets the numberOfDigits value for this DisplayFormat.
     * 
     * @return numberOfDigits
     */
    public org.apache.axis.types.UnsignedByte getNumberOfDigits() {
        return numberOfDigits;
    }


    /**
     * Sets the numberOfDigits value for this DisplayFormat.
     * 
     * @param numberOfDigits
     */
    public void setNumberOfDigits(org.apache.axis.types.UnsignedByte numberOfDigits) {
        this.numberOfDigits = numberOfDigits;
    }


    /**
     * Gets the precision value for this DisplayFormat.
     * 
     * @return precision
     */
    public org.apache.axis.types.UnsignedByte getPrecision() {
        return precision;
    }


    /**
     * Sets the precision value for this DisplayFormat.
     * 
     * @param precision
     */
    public void setPrecision(org.apache.axis.types.UnsignedByte precision) {
        this.precision = precision;
    }


    /**
     * Gets the displayMultiplier value for this DisplayFormat.
     * 
     * @return displayMultiplier
     */
    public java.lang.Double getDisplayMultiplier() {
        return displayMultiplier;
    }


    /**
     * Sets the displayMultiplier value for this DisplayFormat.
     * 
     * @param displayMultiplier
     */
    public void setDisplayMultiplier(java.lang.Double displayMultiplier) {
        this.displayMultiplier = displayMultiplier;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof DisplayFormat)) return false;
        DisplayFormat other = (DisplayFormat) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.supressLeadingZero==null && other.getSupressLeadingZero()==null) || 
             (this.supressLeadingZero!=null &&
              this.supressLeadingZero.equals(other.getSupressLeadingZero()))) &&
            ((this.numberOfDigits==null && other.getNumberOfDigits()==null) || 
             (this.numberOfDigits!=null &&
              this.numberOfDigits.equals(other.getNumberOfDigits()))) &&
            ((this.precision==null && other.getPrecision()==null) || 
             (this.precision!=null &&
              this.precision.equals(other.getPrecision()))) &&
            ((this.displayMultiplier==null && other.getDisplayMultiplier()==null) || 
             (this.displayMultiplier!=null &&
              this.displayMultiplier.equals(other.getDisplayMultiplier())));
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
        if (getSupressLeadingZero() != null) {
            _hashCode += getSupressLeadingZero().hashCode();
        }
        if (getNumberOfDigits() != null) {
            _hashCode += getNumberOfDigits().hashCode();
        }
        if (getPrecision() != null) {
            _hashCode += getPrecision().hashCode();
        }
        if (getDisplayMultiplier() != null) {
            _hashCode += getDisplayMultiplier().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(DisplayFormat.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "displayFormat"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("supressLeadingZero");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "supressLeadingZero"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "boolean"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("numberOfDigits");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "numberOfDigits"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "unsignedByte"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("precision");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "precision"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "unsignedByte"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("displayMultiplier");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "displayMultiplier"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "double"));
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
