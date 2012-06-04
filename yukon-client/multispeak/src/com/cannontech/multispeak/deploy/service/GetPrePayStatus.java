/**
 * GetPrePayStatus.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Dec 28, 2007 (09:53:13 CST) WSDL2Java emitter.
 */

package com.cannontech.multispeak.deploy.service;

public class GetPrePayStatus  implements java.io.Serializable {
    private java.lang.String[] ppmLocations;

    public GetPrePayStatus() {
    }

    public GetPrePayStatus(
           java.lang.String[] ppmLocations) {
           this.ppmLocations = ppmLocations;
    }


    /**
     * Gets the ppmLocations value for this GetPrePayStatus.
     * 
     * @return ppmLocations
     */
    public java.lang.String[] getPpmLocations() {
        return ppmLocations;
    }


    /**
     * Sets the ppmLocations value for this GetPrePayStatus.
     * 
     * @param ppmLocations
     */
    public void setPpmLocations(java.lang.String[] ppmLocations) {
        this.ppmLocations = ppmLocations;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof GetPrePayStatus)) return false;
        GetPrePayStatus other = (GetPrePayStatus) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.ppmLocations==null && other.getPpmLocations()==null) || 
             (this.ppmLocations!=null &&
              java.util.Arrays.equals(this.ppmLocations, other.getPpmLocations())));
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
        if (getPpmLocations() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getPpmLocations());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getPpmLocations(), i);
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
        new org.apache.axis.description.TypeDesc(GetPrePayStatus.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">GetPrePayStatus"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("ppmLocations");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ppmLocations"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        elemField.setItemQName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "string"));
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
