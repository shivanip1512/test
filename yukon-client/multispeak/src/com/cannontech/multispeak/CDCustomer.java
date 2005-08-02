/**
 * CDCustomer.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.2 May 03, 2005 (02:20:24 EDT) WSDL2Java emitter.
 */

package com.cannontech.multispeak;

public class CDCustomer  implements java.io.Serializable {
    private java.lang.String custID;
    private java.lang.String servLoc;
    private java.lang.String meterID;
    private com.cannontech.multispeak.LoadActionCode loadActionCode;

    public CDCustomer() {
    }

    public CDCustomer(
           java.lang.String custID,
           java.lang.String servLoc,
           java.lang.String meterID,
           com.cannontech.multispeak.LoadActionCode loadActionCode) {
           this.custID = custID;
           this.servLoc = servLoc;
           this.meterID = meterID;
           this.loadActionCode = loadActionCode;
    }


    /**
     * Gets the custID value for this CDCustomer.
     * 
     * @return custID
     */
    public java.lang.String getCustID() {
        return custID;
    }


    /**
     * Sets the custID value for this CDCustomer.
     * 
     * @param custID
     */
    public void setCustID(java.lang.String custID) {
        this.custID = custID;
    }


    /**
     * Gets the servLoc value for this CDCustomer.
     * 
     * @return servLoc
     */
    public java.lang.String getServLoc() {
        return servLoc;
    }


    /**
     * Sets the servLoc value for this CDCustomer.
     * 
     * @param servLoc
     */
    public void setServLoc(java.lang.String servLoc) {
        this.servLoc = servLoc;
    }


    /**
     * Gets the meterID value for this CDCustomer.
     * 
     * @return meterID
     */
    public java.lang.String getMeterID() {
        return meterID;
    }


    /**
     * Sets the meterID value for this CDCustomer.
     * 
     * @param meterID
     */
    public void setMeterID(java.lang.String meterID) {
        this.meterID = meterID;
    }


    /**
     * Gets the loadActionCode value for this CDCustomer.
     * 
     * @return loadActionCode
     */
    public com.cannontech.multispeak.LoadActionCode getLoadActionCode() {
        return loadActionCode;
    }


    /**
     * Sets the loadActionCode value for this CDCustomer.
     * 
     * @param loadActionCode
     */
    public void setLoadActionCode(com.cannontech.multispeak.LoadActionCode loadActionCode) {
        this.loadActionCode = loadActionCode;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof CDCustomer)) return false;
        CDCustomer other = (CDCustomer) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.custID==null && other.getCustID()==null) || 
             (this.custID!=null &&
              this.custID.equals(other.getCustID()))) &&
            ((this.servLoc==null && other.getServLoc()==null) || 
             (this.servLoc!=null &&
              this.servLoc.equals(other.getServLoc()))) &&
            ((this.meterID==null && other.getMeterID()==null) || 
             (this.meterID!=null &&
              this.meterID.equals(other.getMeterID()))) &&
            ((this.loadActionCode==null && other.getLoadActionCode()==null) || 
             (this.loadActionCode!=null &&
              this.loadActionCode.equals(other.getLoadActionCode())));
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
        if (getCustID() != null) {
            _hashCode += getCustID().hashCode();
        }
        if (getServLoc() != null) {
            _hashCode += getServLoc().hashCode();
        }
        if (getMeterID() != null) {
            _hashCode += getMeterID().hashCode();
        }
        if (getLoadActionCode() != null) {
            _hashCode += getLoadActionCode().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(CDCustomer.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "CDCustomer"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("custID");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "custID"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("servLoc");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "servLoc"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("meterID");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "meterID"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("loadActionCode");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "loadActionCode"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "loadActionCode"));
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
