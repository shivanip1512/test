/**
 * ExtensionsItem.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.2 May 03, 2005 (02:20:24 EDT) WSDL2Java emitter.
 */

package com.cannontech.multispeak;

public class ExtensionsItem  implements java.io.Serializable {
    private java.lang.String extName;
    private java.lang.String extValue;
    private java.lang.String extType;

    public ExtensionsItem() {
    }

    public ExtensionsItem(
           java.lang.String extName,
           java.lang.String extValue,
           java.lang.String extType) {
           this.extName = extName;
           this.extValue = extValue;
           this.extType = extType;
    }


    /**
     * Gets the extName value for this ExtensionsItem.
     * 
     * @return extName
     */
    public java.lang.String getExtName() {
        return extName;
    }


    /**
     * Sets the extName value for this ExtensionsItem.
     * 
     * @param extName
     */
    public void setExtName(java.lang.String extName) {
        this.extName = extName;
    }


    /**
     * Gets the extValue value for this ExtensionsItem.
     * 
     * @return extValue
     */
    public java.lang.String getExtValue() {
        return extValue;
    }


    /**
     * Sets the extValue value for this ExtensionsItem.
     * 
     * @param extValue
     */
    public void setExtValue(java.lang.String extValue) {
        this.extValue = extValue;
    }


    /**
     * Gets the extType value for this ExtensionsItem.
     * 
     * @return extType
     */
    public java.lang.String getExtType() {
        return extType;
    }


    /**
     * Sets the extType value for this ExtensionsItem.
     * 
     * @param extType
     */
    public void setExtType(java.lang.String extType) {
        this.extType = extType;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof ExtensionsItem)) return false;
        ExtensionsItem other = (ExtensionsItem) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.extName==null && other.getExtName()==null) || 
             (this.extName!=null &&
              this.extName.equals(other.getExtName()))) &&
            ((this.extValue==null && other.getExtValue()==null) || 
             (this.extValue!=null &&
              this.extValue.equals(other.getExtValue()))) &&
            ((this.extType==null && other.getExtType()==null) || 
             (this.extType!=null &&
              this.extType.equals(other.getExtType())));
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
        if (getExtName() != null) {
            _hashCode += getExtName().hashCode();
        }
        if (getExtValue() != null) {
            _hashCode += getExtValue().hashCode();
        }
        if (getExtType() != null) {
            _hashCode += getExtType().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(ExtensionsItem.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "extensionsItem"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("extName");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "extName"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("extValue");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "extValue"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("extType");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "extType"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
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
