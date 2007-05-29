/**
 * LinkedTransformerUnitList.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.2 May 03, 2005 (02:20:24 EDT) WSDL2Java emitter.
 */

package com.cannontech.multispeak.service;

public class LinkedTransformerUnitList  implements java.io.Serializable {
    private java.lang.String[] unitID;

    public LinkedTransformerUnitList() {
    }

    public LinkedTransformerUnitList(
           java.lang.String[] unitID) {
           this.unitID = unitID;
    }


    /**
     * Gets the unitID value for this LinkedTransformerUnitList.
     * 
     * @return unitID
     */
    public java.lang.String[] getUnitID() {
        return unitID;
    }


    /**
     * Sets the unitID value for this LinkedTransformerUnitList.
     * 
     * @param unitID
     */
    public void setUnitID(java.lang.String[] unitID) {
        this.unitID = unitID;
    }

    public java.lang.String getUnitID(int i) {
        return this.unitID[i];
    }

    public void setUnitID(int i, java.lang.String _value) {
        this.unitID[i] = _value;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof LinkedTransformerUnitList)) return false;
        LinkedTransformerUnitList other = (LinkedTransformerUnitList) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.unitID==null && other.getUnitID()==null) || 
             (this.unitID!=null &&
              java.util.Arrays.equals(this.unitID, other.getUnitID())));
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
        if (getUnitID() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getUnitID());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getUnitID(), i);
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
        new org.apache.axis.description.TypeDesc(LinkedTransformerUnitList.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">linkedTransformer>unitList"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("unitID");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "unitID"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
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
