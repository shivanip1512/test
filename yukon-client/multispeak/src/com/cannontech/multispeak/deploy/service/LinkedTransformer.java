/**
 * LinkedTransformer.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Dec 28, 2007 (09:53:13 CST) WSDL2Java emitter.
 */

package com.cannontech.multispeak.deploy.service;

public class LinkedTransformer  implements java.io.Serializable {
    private java.lang.String bankID;

    private java.lang.String[] unitList;

    public LinkedTransformer() {
    }

    public LinkedTransformer(
           java.lang.String bankID,
           java.lang.String[] unitList) {
           this.bankID = bankID;
           this.unitList = unitList;
    }


    /**
     * Gets the bankID value for this LinkedTransformer.
     * 
     * @return bankID
     */
    public java.lang.String getBankID() {
        return bankID;
    }


    /**
     * Sets the bankID value for this LinkedTransformer.
     * 
     * @param bankID
     */
    public void setBankID(java.lang.String bankID) {
        this.bankID = bankID;
    }


    /**
     * Gets the unitList value for this LinkedTransformer.
     * 
     * @return unitList
     */
    public java.lang.String[] getUnitList() {
        return unitList;
    }


    /**
     * Sets the unitList value for this LinkedTransformer.
     * 
     * @param unitList
     */
    public void setUnitList(java.lang.String[] unitList) {
        this.unitList = unitList;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof LinkedTransformer)) return false;
        LinkedTransformer other = (LinkedTransformer) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.bankID==null && other.getBankID()==null) || 
             (this.bankID!=null &&
              this.bankID.equals(other.getBankID()))) &&
            ((this.unitList==null && other.getUnitList()==null) || 
             (this.unitList!=null &&
              java.util.Arrays.equals(this.unitList, other.getUnitList())));
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
        if (getBankID() != null) {
            _hashCode += getBankID().hashCode();
        }
        if (getUnitList() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getUnitList());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getUnitList(), i);
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
        new org.apache.axis.description.TypeDesc(LinkedTransformer.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "linkedTransformer"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("bankID");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "bankID"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("unitList");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "unitList"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        elemField.setItemQName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "unitID"));
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
