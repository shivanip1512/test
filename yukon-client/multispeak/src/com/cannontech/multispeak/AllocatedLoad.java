/**
 * AllocatedLoad.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.2 May 03, 2005 (02:20:24 EDT) WSDL2Java emitter.
 */

package com.cannontech.multispeak;

public class AllocatedLoad  implements java.io.Serializable {
    private com.cannontech.multispeak.PhaseCd phase;
    private java.lang.Float kw;
    private java.lang.Float kvar;
    private java.lang.Float noOfCustomers;

    public AllocatedLoad() {
    }

    public AllocatedLoad(
           com.cannontech.multispeak.PhaseCd phase,
           java.lang.Float kw,
           java.lang.Float kvar,
           java.lang.Float noOfCustomers) {
           this.phase = phase;
           this.kw = kw;
           this.kvar = kvar;
           this.noOfCustomers = noOfCustomers;
    }


    /**
     * Gets the phase value for this AllocatedLoad.
     * 
     * @return phase
     */
    public com.cannontech.multispeak.PhaseCd getPhase() {
        return phase;
    }


    /**
     * Sets the phase value for this AllocatedLoad.
     * 
     * @param phase
     */
    public void setPhase(com.cannontech.multispeak.PhaseCd phase) {
        this.phase = phase;
    }


    /**
     * Gets the kw value for this AllocatedLoad.
     * 
     * @return kw
     */
    public java.lang.Float getKw() {
        return kw;
    }


    /**
     * Sets the kw value for this AllocatedLoad.
     * 
     * @param kw
     */
    public void setKw(java.lang.Float kw) {
        this.kw = kw;
    }


    /**
     * Gets the kvar value for this AllocatedLoad.
     * 
     * @return kvar
     */
    public java.lang.Float getKvar() {
        return kvar;
    }


    /**
     * Sets the kvar value for this AllocatedLoad.
     * 
     * @param kvar
     */
    public void setKvar(java.lang.Float kvar) {
        this.kvar = kvar;
    }


    /**
     * Gets the noOfCustomers value for this AllocatedLoad.
     * 
     * @return noOfCustomers
     */
    public java.lang.Float getNoOfCustomers() {
        return noOfCustomers;
    }


    /**
     * Sets the noOfCustomers value for this AllocatedLoad.
     * 
     * @param noOfCustomers
     */
    public void setNoOfCustomers(java.lang.Float noOfCustomers) {
        this.noOfCustomers = noOfCustomers;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof AllocatedLoad)) return false;
        AllocatedLoad other = (AllocatedLoad) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.phase==null && other.getPhase()==null) || 
             (this.phase!=null &&
              this.phase.equals(other.getPhase()))) &&
            ((this.kw==null && other.getKw()==null) || 
             (this.kw!=null &&
              this.kw.equals(other.getKw()))) &&
            ((this.kvar==null && other.getKvar()==null) || 
             (this.kvar!=null &&
              this.kvar.equals(other.getKvar()))) &&
            ((this.noOfCustomers==null && other.getNoOfCustomers()==null) || 
             (this.noOfCustomers!=null &&
              this.noOfCustomers.equals(other.getNoOfCustomers())));
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
        if (getPhase() != null) {
            _hashCode += getPhase().hashCode();
        }
        if (getKw() != null) {
            _hashCode += getKw().hashCode();
        }
        if (getKvar() != null) {
            _hashCode += getKvar().hashCode();
        }
        if (getNoOfCustomers() != null) {
            _hashCode += getNoOfCustomers().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(AllocatedLoad.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "allocatedLoad"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("phase");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "phase"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "phaseCd"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("kw");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "kw"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "float"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("kvar");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "kvar"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "float"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("noOfCustomers");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "noOfCustomers"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "float"));
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
