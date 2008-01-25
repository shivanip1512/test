/**
 * PpmMeterExchange.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Dec 28, 2007 (09:53:13 CST) WSDL2Java emitter.
 */

package com.cannontech.multispeak.deploy.service;

public class PpmMeterExchange  extends com.cannontech.multispeak.deploy.service.MspObject  implements java.io.Serializable {
    private java.lang.String ppmLocationID;

    private com.cannontech.multispeak.deploy.service.MeterRead outMeterRead;

    private com.cannontech.multispeak.deploy.service.MeterRead inMeterRead;

    public PpmMeterExchange() {
    }

    public PpmMeterExchange(
           java.lang.String objectID,
           com.cannontech.multispeak.deploy.service.Action verb,
           java.lang.String errorString,
           java.lang.String replaceID,
           java.lang.String utility,
           com.cannontech.multispeak.deploy.service.Extensions extensions,
           java.lang.String comments,
           com.cannontech.multispeak.deploy.service.ExtensionsItem[] extensionsList,
           java.lang.String ppmLocationID,
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
        this.ppmLocationID = ppmLocationID;
        this.outMeterRead = outMeterRead;
        this.inMeterRead = inMeterRead;
    }


    /**
     * Gets the ppmLocationID value for this PpmMeterExchange.
     * 
     * @return ppmLocationID
     */
    public java.lang.String getPpmLocationID() {
        return ppmLocationID;
    }


    /**
     * Sets the ppmLocationID value for this PpmMeterExchange.
     * 
     * @param ppmLocationID
     */
    public void setPpmLocationID(java.lang.String ppmLocationID) {
        this.ppmLocationID = ppmLocationID;
    }


    /**
     * Gets the outMeterRead value for this PpmMeterExchange.
     * 
     * @return outMeterRead
     */
    public com.cannontech.multispeak.deploy.service.MeterRead getOutMeterRead() {
        return outMeterRead;
    }


    /**
     * Sets the outMeterRead value for this PpmMeterExchange.
     * 
     * @param outMeterRead
     */
    public void setOutMeterRead(com.cannontech.multispeak.deploy.service.MeterRead outMeterRead) {
        this.outMeterRead = outMeterRead;
    }


    /**
     * Gets the inMeterRead value for this PpmMeterExchange.
     * 
     * @return inMeterRead
     */
    public com.cannontech.multispeak.deploy.service.MeterRead getInMeterRead() {
        return inMeterRead;
    }


    /**
     * Sets the inMeterRead value for this PpmMeterExchange.
     * 
     * @param inMeterRead
     */
    public void setInMeterRead(com.cannontech.multispeak.deploy.service.MeterRead inMeterRead) {
        this.inMeterRead = inMeterRead;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof PpmMeterExchange)) return false;
        PpmMeterExchange other = (PpmMeterExchange) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = super.equals(obj) && 
            ((this.ppmLocationID==null && other.getPpmLocationID()==null) || 
             (this.ppmLocationID!=null &&
              this.ppmLocationID.equals(other.getPpmLocationID()))) &&
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
        if (getPpmLocationID() != null) {
            _hashCode += getPpmLocationID().hashCode();
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
        new org.apache.axis.description.TypeDesc(PpmMeterExchange.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ppmMeterExchange"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("ppmLocationID");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ppmLocationID"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
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
