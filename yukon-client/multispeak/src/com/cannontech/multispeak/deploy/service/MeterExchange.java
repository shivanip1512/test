/**
 * MeterExchange.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Dec 28, 2007 (09:53:13 CST) WSDL2Java emitter.
 */

package com.cannontech.multispeak.deploy.service;

public class MeterExchange  extends com.cannontech.multispeak.deploy.service.MspObject  implements java.io.Serializable {
    private com.cannontech.multispeak.deploy.service.ServiceLocation serviceLocation;

    private com.cannontech.multispeak.deploy.service.MeterRead outMeterRead;

    private com.cannontech.multispeak.deploy.service.MeterRead inMeterRead;

    public MeterExchange() {
    }

    public MeterExchange(
           java.lang.String objectID,
           com.cannontech.multispeak.deploy.service.Action verb,
           java.lang.String errorString,
           java.lang.String replaceID,
           java.lang.String utility,
           com.cannontech.multispeak.deploy.service.Extensions extensions,
           java.lang.String comments,
           com.cannontech.multispeak.deploy.service.ExtensionsItem[] extensionsList,
           com.cannontech.multispeak.deploy.service.ServiceLocation serviceLocation,
           com.cannontech.multispeak.deploy.service.MeterRead outMeterRead,
           com.cannontech.multispeak.deploy.service.MeterRead inMeterRead) {
        super(
            objectID,
            verb,
            errorString,
            replaceID,
            utility,
            extensions,
            comments,
            extensionsList);
        this.serviceLocation = serviceLocation;
        this.outMeterRead = outMeterRead;
        this.inMeterRead = inMeterRead;
    }


    /**
     * Gets the serviceLocation value for this MeterExchange.
     * 
     * @return serviceLocation
     */
    public com.cannontech.multispeak.deploy.service.ServiceLocation getServiceLocation() {
        return serviceLocation;
    }


    /**
     * Sets the serviceLocation value for this MeterExchange.
     * 
     * @param serviceLocation
     */
    public void setServiceLocation(com.cannontech.multispeak.deploy.service.ServiceLocation serviceLocation) {
        this.serviceLocation = serviceLocation;
    }


    /**
     * Gets the outMeterRead value for this MeterExchange.
     * 
     * @return outMeterRead
     */
    public com.cannontech.multispeak.deploy.service.MeterRead getOutMeterRead() {
        return outMeterRead;
    }


    /**
     * Sets the outMeterRead value for this MeterExchange.
     * 
     * @param outMeterRead
     */
    public void setOutMeterRead(com.cannontech.multispeak.deploy.service.MeterRead outMeterRead) {
        this.outMeterRead = outMeterRead;
    }


    /**
     * Gets the inMeterRead value for this MeterExchange.
     * 
     * @return inMeterRead
     */
    public com.cannontech.multispeak.deploy.service.MeterRead getInMeterRead() {
        return inMeterRead;
    }


    /**
     * Sets the inMeterRead value for this MeterExchange.
     * 
     * @param inMeterRead
     */
    public void setInMeterRead(com.cannontech.multispeak.deploy.service.MeterRead inMeterRead) {
        this.inMeterRead = inMeterRead;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof MeterExchange)) return false;
        MeterExchange other = (MeterExchange) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = super.equals(obj) && 
            ((this.serviceLocation==null && other.getServiceLocation()==null) || 
             (this.serviceLocation!=null &&
              this.serviceLocation.equals(other.getServiceLocation()))) &&
            ((this.outMeterRead==null && other.getOutMeterRead()==null) || 
             (this.outMeterRead!=null &&
              this.outMeterRead.equals(other.getOutMeterRead()))) &&
            ((this.inMeterRead==null && other.getInMeterRead()==null) || 
             (this.inMeterRead!=null &&
              this.inMeterRead.equals(other.getInMeterRead())));
        __equalsCalc = null;
        return _equals;
    }

    private boolean __hashCodeCalc = false;
    public synchronized int hashCode() {
        if (__hashCodeCalc) {
            return 0;
        }
        __hashCodeCalc = true;
        int _hashCode = super.hashCode();
        if (getServiceLocation() != null) {
            _hashCode += getServiceLocation().hashCode();
        }
        if (getOutMeterRead() != null) {
            _hashCode += getOutMeterRead().hashCode();
        }
        if (getInMeterRead() != null) {
            _hashCode += getInMeterRead().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(MeterExchange.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "meterExchange"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("serviceLocation");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "serviceLocation"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "serviceLocation"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("outMeterRead");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "outMeterRead"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "meterRead"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("inMeterRead");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "inMeterRead"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "meterRead"));
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
