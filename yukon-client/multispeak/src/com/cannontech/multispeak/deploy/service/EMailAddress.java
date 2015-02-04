/**
 * EMailAddress.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Dec 28, 2007 (09:53:13 CST) WSDL2Java emitter.
 */

package com.cannontech.multispeak.deploy.service;

public class EMailAddress  implements java.io.Serializable {
    private java.lang.String eMail;

    private com.cannontech.multispeak.deploy.service.EMailAddressEMailType eMailType;

    private java.math.BigInteger priorityOrder;

    public EMailAddress() {
    }

    public EMailAddress(
           java.lang.String eMail,
           com.cannontech.multispeak.deploy.service.EMailAddressEMailType eMailType,
           java.math.BigInteger priorityOrder) {
           this.eMail = eMail;
           this.eMailType = eMailType;
           this.priorityOrder = priorityOrder;
    }


    /**
     * Gets the eMail value for this EMailAddress.
     * 
     * @return eMail
     */
    public java.lang.String getEMail() {
        return eMail;
    }


    /**
     * Sets the eMail value for this EMailAddress.
     * 
     * @param eMail
     */
    public void setEMail(java.lang.String eMail) {
        this.eMail = eMail;
    }


    /**
     * Gets the eMailType value for this EMailAddress.
     * 
     * @return eMailType
     */
    public com.cannontech.multispeak.deploy.service.EMailAddressEMailType getEMailType() {
        return eMailType;
    }


    /**
     * Sets the eMailType value for this EMailAddress.
     * 
     * @param eMailType
     */
    public void setEMailType(com.cannontech.multispeak.deploy.service.EMailAddressEMailType eMailType) {
        this.eMailType = eMailType;
    }


    /**
     * Gets the priorityOrder value for this EMailAddress.
     * 
     * @return priorityOrder
     */
    public java.math.BigInteger getPriorityOrder() {
        return priorityOrder;
    }


    /**
     * Sets the priorityOrder value for this EMailAddress.
     * 
     * @param priorityOrder
     */
    public void setPriorityOrder(java.math.BigInteger priorityOrder) {
        this.priorityOrder = priorityOrder;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof EMailAddress)) return false;
        EMailAddress other = (EMailAddress) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.eMail==null && other.getEMail()==null) || 
             (this.eMail!=null &&
              this.eMail.equals(other.getEMail()))) &&
            ((this.eMailType==null && other.getEMailType()==null) || 
             (this.eMailType!=null &&
              this.eMailType.equals(other.getEMailType()))) &&
            ((this.priorityOrder==null && other.getPriorityOrder()==null) || 
             (this.priorityOrder!=null &&
              this.priorityOrder.equals(other.getPriorityOrder())));
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
        if (getEMail() != null) {
            _hashCode += getEMail().hashCode();
        }
        if (getEMailType() != null) {
            _hashCode += getEMailType().hashCode();
        }
        if (getPriorityOrder() != null) {
            _hashCode += getPriorityOrder().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(EMailAddress.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "eMailAddress"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("EMail");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "eMail"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("EMailType");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "eMailType"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">eMailAddress>eMailType"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("priorityOrder");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "priorityOrder"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "integer"));
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
