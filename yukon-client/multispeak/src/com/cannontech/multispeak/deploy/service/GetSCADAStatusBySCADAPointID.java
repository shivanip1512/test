/**
 * GetSCADAStatusBySCADAPointID.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Dec 28, 2007 (09:53:13 CST) WSDL2Java emitter.
 */

package com.cannontech.multispeak.deploy.service;

public class GetSCADAStatusBySCADAPointID  implements java.io.Serializable {
    private java.lang.String scadaPointID;

    public GetSCADAStatusBySCADAPointID() {
    }

    public GetSCADAStatusBySCADAPointID(
           java.lang.String scadaPointID) {
           this.scadaPointID = scadaPointID;
    }


    /**
     * Gets the scadaPointID value for this GetSCADAStatusBySCADAPointID.
     * 
     * @return scadaPointID
     */
    public java.lang.String getScadaPointID() {
        return scadaPointID;
    }


    /**
     * Sets the scadaPointID value for this GetSCADAStatusBySCADAPointID.
     * 
     * @param scadaPointID
     */
    public void setScadaPointID(java.lang.String scadaPointID) {
        this.scadaPointID = scadaPointID;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof GetSCADAStatusBySCADAPointID)) return false;
        GetSCADAStatusBySCADAPointID other = (GetSCADAStatusBySCADAPointID) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.scadaPointID==null && other.getScadaPointID()==null) || 
             (this.scadaPointID!=null &&
              this.scadaPointID.equals(other.getScadaPointID())));
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
        if (getScadaPointID() != null) {
            _hashCode += getScadaPointID().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(GetSCADAStatusBySCADAPointID.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">GetSCADAStatusBySCADAPointID"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("scadaPointID");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "scadaPointID"));
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
