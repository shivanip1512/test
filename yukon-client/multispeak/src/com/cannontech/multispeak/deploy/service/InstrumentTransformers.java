/**
 * InstrumentTransformers.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Dec 28, 2007 (09:53:13 CST) WSDL2Java emitter.
 */

package com.cannontech.multispeak.deploy.service;

public class InstrumentTransformers  implements java.io.Serializable {
    private java.lang.Double ct;

    private java.lang.Double pt;

    public InstrumentTransformers() {
    }

    public InstrumentTransformers(
           java.lang.Double ct,
           java.lang.Double pt) {
           this.ct = ct;
           this.pt = pt;
    }


    /**
     * Gets the ct value for this InstrumentTransformers.
     * 
     * @return ct
     */
    public java.lang.Double getCt() {
        return ct;
    }


    /**
     * Sets the ct value for this InstrumentTransformers.
     * 
     * @param ct
     */
    public void setCt(java.lang.Double ct) {
        this.ct = ct;
    }


    /**
     * Gets the pt value for this InstrumentTransformers.
     * 
     * @return pt
     */
    public java.lang.Double getPt() {
        return pt;
    }


    /**
     * Sets the pt value for this InstrumentTransformers.
     * 
     * @param pt
     */
    public void setPt(java.lang.Double pt) {
        this.pt = pt;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof InstrumentTransformers)) return false;
        InstrumentTransformers other = (InstrumentTransformers) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.ct==null && other.getCt()==null) || 
             (this.ct!=null &&
              this.ct.equals(other.getCt()))) &&
            ((this.pt==null && other.getPt()==null) || 
             (this.pt!=null &&
              this.pt.equals(other.getPt())));
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
        if (getCt() != null) {
            _hashCode += getCt().hashCode();
        }
        if (getPt() != null) {
            _hashCode += getPt().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(InstrumentTransformers.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "instrumentTransformers"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("ct");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ct"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "double"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("pt");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "pt"));
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
