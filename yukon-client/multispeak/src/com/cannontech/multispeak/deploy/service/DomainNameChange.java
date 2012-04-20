/**
 * DomainNameChange.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Dec 28, 2007 (09:53:13 CST) WSDL2Java emitter.
 */

package com.cannontech.multispeak.deploy.service;

public class DomainNameChange  implements java.io.Serializable {
    private java.lang.String domainName;

    private java.lang.String oldDomainName;

    private com.cannontech.multispeak.deploy.service.DomainNameAction action;

    public DomainNameChange() {
    }

    public DomainNameChange(
           java.lang.String domainName,
           java.lang.String oldDomainName,
           com.cannontech.multispeak.deploy.service.DomainNameAction action) {
           this.domainName = domainName;
           this.oldDomainName = oldDomainName;
           this.action = action;
    }


    /**
     * Gets the domainName value for this DomainNameChange.
     * 
     * @return domainName
     */
    public java.lang.String getDomainName() {
        return domainName;
    }


    /**
     * Sets the domainName value for this DomainNameChange.
     * 
     * @param domainName
     */
    public void setDomainName(java.lang.String domainName) {
        this.domainName = domainName;
    }


    /**
     * Gets the oldDomainName value for this DomainNameChange.
     * 
     * @return oldDomainName
     */
    public java.lang.String getOldDomainName() {
        return oldDomainName;
    }


    /**
     * Sets the oldDomainName value for this DomainNameChange.
     * 
     * @param oldDomainName
     */
    public void setOldDomainName(java.lang.String oldDomainName) {
        this.oldDomainName = oldDomainName;
    }


    /**
     * Gets the action value for this DomainNameChange.
     * 
     * @return action
     */
    public com.cannontech.multispeak.deploy.service.DomainNameAction getAction() {
        return action;
    }


    /**
     * Sets the action value for this DomainNameChange.
     * 
     * @param action
     */
    public void setAction(com.cannontech.multispeak.deploy.service.DomainNameAction action) {
        this.action = action;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof DomainNameChange)) return false;
        DomainNameChange other = (DomainNameChange) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.domainName==null && other.getDomainName()==null) || 
             (this.domainName!=null &&
              this.domainName.equals(other.getDomainName()))) &&
            ((this.oldDomainName==null && other.getOldDomainName()==null) || 
             (this.oldDomainName!=null &&
              this.oldDomainName.equals(other.getOldDomainName()))) &&
            ((this.action==null && other.getAction()==null) || 
             (this.action!=null &&
              this.action.equals(other.getAction())));
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
        if (getDomainName() != null) {
            _hashCode += getDomainName().hashCode();
        }
        if (getOldDomainName() != null) {
            _hashCode += getOldDomainName().hashCode();
        }
        if (getAction() != null) {
            _hashCode += getAction().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(DomainNameChange.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "domainNameChange"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("domainName");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "domainName"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("oldDomainName");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "oldDomainName"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("action");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "action"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "domainNameAction"));
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
