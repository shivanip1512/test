/**
 * FormattedBlock.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.2 May 03, 2005 (02:20:24 EDT) WSDL2Java emitter.
 */

package com.cannontech.multispeak.service;

public class FormattedBlock  implements java.io.Serializable {
    private java.lang.String separator;
    private com.cannontech.multispeak.service.FormattedBlockValSyntax valSyntax;
    private com.cannontech.multispeak.service.FormattedBlockValueList valueList;

    public FormattedBlock() {
    }

    public FormattedBlock(
           java.lang.String separator,
           com.cannontech.multispeak.service.FormattedBlockValSyntax valSyntax,
           com.cannontech.multispeak.service.FormattedBlockValueList valueList) {
           this.separator = separator;
           this.valSyntax = valSyntax;
           this.valueList = valueList;
    }


    /**
     * Gets the separator value for this FormattedBlock.
     * 
     * @return separator
     */
    public java.lang.String getSeparator() {
        return separator;
    }


    /**
     * Sets the separator value for this FormattedBlock.
     * 
     * @param separator
     */
    public void setSeparator(java.lang.String separator) {
        this.separator = separator;
    }


    /**
     * Gets the valSyntax value for this FormattedBlock.
     * 
     * @return valSyntax
     */
    public com.cannontech.multispeak.service.FormattedBlockValSyntax getValSyntax() {
        return valSyntax;
    }


    /**
     * Sets the valSyntax value for this FormattedBlock.
     * 
     * @param valSyntax
     */
    public void setValSyntax(com.cannontech.multispeak.service.FormattedBlockValSyntax valSyntax) {
        this.valSyntax = valSyntax;
    }


    /**
     * Gets the valueList value for this FormattedBlock.
     * 
     * @return valueList
     */
    public com.cannontech.multispeak.service.FormattedBlockValueList getValueList() {
        return valueList;
    }


    /**
     * Sets the valueList value for this FormattedBlock.
     * 
     * @param valueList
     */
    public void setValueList(com.cannontech.multispeak.service.FormattedBlockValueList valueList) {
        this.valueList = valueList;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof FormattedBlock)) return false;
        FormattedBlock other = (FormattedBlock) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.separator==null && other.getSeparator()==null) || 
             (this.separator!=null &&
              this.separator.equals(other.getSeparator()))) &&
            ((this.valSyntax==null && other.getValSyntax()==null) || 
             (this.valSyntax!=null &&
              this.valSyntax.equals(other.getValSyntax()))) &&
            ((this.valueList==null && other.getValueList()==null) || 
             (this.valueList!=null &&
              this.valueList.equals(other.getValueList())));
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
        if (getSeparator() != null) {
            _hashCode += getSeparator().hashCode();
        }
        if (getValSyntax() != null) {
            _hashCode += getValSyntax().hashCode();
        }
        if (getValueList() != null) {
            _hashCode += getValueList().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(FormattedBlock.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "formattedBlock"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("separator");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "separator"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("valSyntax");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "valSyntax"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">formattedBlock>valSyntax"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("valueList");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "valueList"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">formattedBlock>valueList"));
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
