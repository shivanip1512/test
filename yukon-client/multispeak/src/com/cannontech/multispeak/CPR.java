/**
 * CPR.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.2 May 03, 2005 (02:20:24 EDT) WSDL2Java emitter.
 */

package com.cannontech.multispeak;

public class CPR  implements java.io.Serializable {
    private com.cannontech.multispeak.Extensions extensions;
    private com.cannontech.multispeak.ExtensionsList extensionsList;
    private java.lang.String acctCode;
    private java.lang.Float quantity;
    private com.cannontech.multispeak.UnitActn unitAction;

    public CPR() {
    }

    public CPR(
           com.cannontech.multispeak.Extensions extensions,
           com.cannontech.multispeak.ExtensionsList extensionsList,
           java.lang.String acctCode,
           java.lang.Float quantity,
           com.cannontech.multispeak.UnitActn unitAction) {
           this.extensions = extensions;
           this.extensionsList = extensionsList;
           this.acctCode = acctCode;
           this.quantity = quantity;
           this.unitAction = unitAction;
    }


    /**
     * Gets the extensions value for this CPR.
     * 
     * @return extensions
     */
    public com.cannontech.multispeak.Extensions getExtensions() {
        return extensions;
    }


    /**
     * Sets the extensions value for this CPR.
     * 
     * @param extensions
     */
    public void setExtensions(com.cannontech.multispeak.Extensions extensions) {
        this.extensions = extensions;
    }


    /**
     * Gets the extensionsList value for this CPR.
     * 
     * @return extensionsList
     */
    public com.cannontech.multispeak.ExtensionsList getExtensionsList() {
        return extensionsList;
    }


    /**
     * Sets the extensionsList value for this CPR.
     * 
     * @param extensionsList
     */
    public void setExtensionsList(com.cannontech.multispeak.ExtensionsList extensionsList) {
        this.extensionsList = extensionsList;
    }


    /**
     * Gets the acctCode value for this CPR.
     * 
     * @return acctCode
     */
    public java.lang.String getAcctCode() {
        return acctCode;
    }


    /**
     * Sets the acctCode value for this CPR.
     * 
     * @param acctCode
     */
    public void setAcctCode(java.lang.String acctCode) {
        this.acctCode = acctCode;
    }


    /**
     * Gets the quantity value for this CPR.
     * 
     * @return quantity
     */
    public java.lang.Float getQuantity() {
        return quantity;
    }


    /**
     * Sets the quantity value for this CPR.
     * 
     * @param quantity
     */
    public void setQuantity(java.lang.Float quantity) {
        this.quantity = quantity;
    }


    /**
     * Gets the unitAction value for this CPR.
     * 
     * @return unitAction
     */
    public com.cannontech.multispeak.UnitActn getUnitAction() {
        return unitAction;
    }


    /**
     * Sets the unitAction value for this CPR.
     * 
     * @param unitAction
     */
    public void setUnitAction(com.cannontech.multispeak.UnitActn unitAction) {
        this.unitAction = unitAction;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof CPR)) return false;
        CPR other = (CPR) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.extensions==null && other.getExtensions()==null) || 
             (this.extensions!=null &&
              this.extensions.equals(other.getExtensions()))) &&
            ((this.extensionsList==null && other.getExtensionsList()==null) || 
             (this.extensionsList!=null &&
              this.extensionsList.equals(other.getExtensionsList()))) &&
            ((this.acctCode==null && other.getAcctCode()==null) || 
             (this.acctCode!=null &&
              this.acctCode.equals(other.getAcctCode()))) &&
            ((this.quantity==null && other.getQuantity()==null) || 
             (this.quantity!=null &&
              this.quantity.equals(other.getQuantity()))) &&
            ((this.unitAction==null && other.getUnitAction()==null) || 
             (this.unitAction!=null &&
              this.unitAction.equals(other.getUnitAction())));
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
        if (getExtensions() != null) {
            _hashCode += getExtensions().hashCode();
        }
        if (getExtensionsList() != null) {
            _hashCode += getExtensionsList().hashCode();
        }
        if (getAcctCode() != null) {
            _hashCode += getAcctCode().hashCode();
        }
        if (getQuantity() != null) {
            _hashCode += getQuantity().hashCode();
        }
        if (getUnitAction() != null) {
            _hashCode += getUnitAction().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(CPR.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "CPR"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("extensions");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "extensions"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "extensions"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("extensionsList");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "extensionsList"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "extensionsList"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("acctCode");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "acctCode"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("quantity");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "quantity"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "float"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("unitAction");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "unitAction"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "unitActn"));
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
