/**
 * FormattedBlockValSyntax.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.2 May 03, 2005 (02:20:24 EDT) WSDL2Java emitter.
 */

package com.cannontech.multispeak.service;

public class FormattedBlockValSyntax  implements java.io.Serializable {
    private com.cannontech.multispeak.service.SyntaxItem[] syntaxItem;

    public FormattedBlockValSyntax() {
    }

    public FormattedBlockValSyntax(
           com.cannontech.multispeak.service.SyntaxItem[] syntaxItem) {
           this.syntaxItem = syntaxItem;
    }


    /**
     * Gets the syntaxItem value for this FormattedBlockValSyntax.
     * 
     * @return syntaxItem
     */
    public com.cannontech.multispeak.service.SyntaxItem[] getSyntaxItem() {
        return syntaxItem;
    }


    /**
     * Sets the syntaxItem value for this FormattedBlockValSyntax.
     * 
     * @param syntaxItem
     */
    public void setSyntaxItem(com.cannontech.multispeak.service.SyntaxItem[] syntaxItem) {
        this.syntaxItem = syntaxItem;
    }

    public com.cannontech.multispeak.service.SyntaxItem getSyntaxItem(int i) {
        return this.syntaxItem[i];
    }

    public void setSyntaxItem(int i, com.cannontech.multispeak.service.SyntaxItem _value) {
        this.syntaxItem[i] = _value;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof FormattedBlockValSyntax)) return false;
        FormattedBlockValSyntax other = (FormattedBlockValSyntax) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.syntaxItem==null && other.getSyntaxItem()==null) || 
             (this.syntaxItem!=null &&
              java.util.Arrays.equals(this.syntaxItem, other.getSyntaxItem())));
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
        if (getSyntaxItem() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getSyntaxItem());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getSyntaxItem(), i);
                if (obj != null &&
                    !obj.getClass().isArray()) {
                    _hashCode += obj.hashCode();
                }
            }
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(FormattedBlockValSyntax.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">formattedBlock>valSyntax"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("syntaxItem");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "syntaxItem"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "syntaxItem"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        elemField.setMaxOccursUnbounded(true);
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
