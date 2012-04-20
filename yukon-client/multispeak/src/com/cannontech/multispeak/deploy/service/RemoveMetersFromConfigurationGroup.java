/**
 * RemoveMetersFromConfigurationGroup.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Dec 28, 2007 (09:53:13 CST) WSDL2Java emitter.
 */

package com.cannontech.multispeak.deploy.service;

public class RemoveMetersFromConfigurationGroup  implements java.io.Serializable {
    private java.lang.String[] meterNumbers;

    private java.lang.String meterGroupID;

    private com.cannontech.multispeak.deploy.service.ServiceType serviceType;

    public RemoveMetersFromConfigurationGroup() {
    }

    public RemoveMetersFromConfigurationGroup(
           java.lang.String[] meterNumbers,
           java.lang.String meterGroupID,
           com.cannontech.multispeak.deploy.service.ServiceType serviceType) {
           this.meterNumbers = meterNumbers;
           this.meterGroupID = meterGroupID;
           this.serviceType = serviceType;
    }


    /**
     * Gets the meterNumbers value for this RemoveMetersFromConfigurationGroup.
     * 
     * @return meterNumbers
     */
    public java.lang.String[] getMeterNumbers() {
        return meterNumbers;
    }


    /**
     * Sets the meterNumbers value for this RemoveMetersFromConfigurationGroup.
     * 
     * @param meterNumbers
     */
    public void setMeterNumbers(java.lang.String[] meterNumbers) {
        this.meterNumbers = meterNumbers;
    }


    /**
     * Gets the meterGroupID value for this RemoveMetersFromConfigurationGroup.
     * 
     * @return meterGroupID
     */
    public java.lang.String getMeterGroupID() {
        return meterGroupID;
    }


    /**
     * Sets the meterGroupID value for this RemoveMetersFromConfigurationGroup.
     * 
     * @param meterGroupID
     */
    public void setMeterGroupID(java.lang.String meterGroupID) {
        this.meterGroupID = meterGroupID;
    }


    /**
     * Gets the serviceType value for this RemoveMetersFromConfigurationGroup.
     * 
     * @return serviceType
     */
    public com.cannontech.multispeak.deploy.service.ServiceType getServiceType() {
        return serviceType;
    }


    /**
     * Sets the serviceType value for this RemoveMetersFromConfigurationGroup.
     * 
     * @param serviceType
     */
    public void setServiceType(com.cannontech.multispeak.deploy.service.ServiceType serviceType) {
        this.serviceType = serviceType;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof RemoveMetersFromConfigurationGroup)) return false;
        RemoveMetersFromConfigurationGroup other = (RemoveMetersFromConfigurationGroup) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.meterNumbers==null && other.getMeterNumbers()==null) || 
             (this.meterNumbers!=null &&
              java.util.Arrays.equals(this.meterNumbers, other.getMeterNumbers()))) &&
            ((this.meterGroupID==null && other.getMeterGroupID()==null) || 
             (this.meterGroupID!=null &&
              this.meterGroupID.equals(other.getMeterGroupID()))) &&
            ((this.serviceType==null && other.getServiceType()==null) || 
             (this.serviceType!=null &&
              this.serviceType.equals(other.getServiceType())));
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
        if (getMeterNumbers() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getMeterNumbers());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getMeterNumbers(), i);
                if (obj != null &&
                    !obj.getClass().isArray()) {
                    _hashCode += obj.hashCode();
                }
            }
        }
        if (getMeterGroupID() != null) {
            _hashCode += getMeterGroupID().hashCode();
        }
        if (getServiceType() != null) {
            _hashCode += getServiceType().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(RemoveMetersFromConfigurationGroup.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">RemoveMetersFromConfigurationGroup"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("meterNumbers");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "meterNumbers"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        elemField.setItemQName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "string"));
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("meterGroupID");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "meterGroupID"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("serviceType");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "serviceType"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "serviceType"));
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
