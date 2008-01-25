/**
 * Fuse.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Dec 28, 2007 (09:53:13 CST) WSDL2Java emitter.
 */

package com.cannontech.multispeak.deploy.service;

public class Fuse  extends com.cannontech.multispeak.deploy.service.MspOverCurrentDevice  implements java.io.Serializable {
    private java.lang.Float linkRtg;

    public Fuse() {
    }

    public Fuse(
           java.lang.String objectID,
           com.cannontech.multispeak.deploy.service.Action verb,
           java.lang.String errorString,
           java.lang.String replaceID,
           java.lang.String utility,
           com.cannontech.multispeak.deploy.service.Extensions extensions,
           java.lang.String comments,
           com.cannontech.multispeak.deploy.service.ExtensionsItem[] extensionsList,
           java.lang.String eaEquipment,
           java.lang.String facilityID,
           com.cannontech.multispeak.deploy.service.PhaseCd phase,
           com.cannontech.multispeak.deploy.service.Position position,
           java.lang.Float ratedVolt,
           java.lang.Float operVolt,
           java.lang.Float maxContAmp,
           java.lang.String manufacturer,
           com.cannontech.multispeak.deploy.service.Mounting mounting,
           java.lang.Boolean bypassExists,
           java.util.Calendar lastService,
           java.lang.Float linkRtg) {
        super(
            objectID,
            verb,
            errorString,
            replaceID,
            utility,
            extensions,
            comments,
            extensionsList,
            eaEquipment,
            facilityID,
            phase,
            position,
            ratedVolt,
            operVolt,
            maxContAmp,
            manufacturer,
            mounting,
            bypassExists,
            lastService);
        this.linkRtg = linkRtg;
    }


    /**
     * Gets the linkRtg value for this Fuse.
     * 
     * @return linkRtg
     */
    public java.lang.Float getLinkRtg() {
        return linkRtg;
    }


    /**
     * Sets the linkRtg value for this Fuse.
     * 
     * @param linkRtg
     */
    public void setLinkRtg(java.lang.Float linkRtg) {
        this.linkRtg = linkRtg;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof Fuse)) return false;
        Fuse other = (Fuse) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = super.equals(obj) && 
            ((this.linkRtg==null && other.getLinkRtg()==null) || 
             (this.linkRtg!=null &&
              this.linkRtg.equals(other.getLinkRtg())));
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
        if (getLinkRtg() != null) {
            _hashCode += getLinkRtg().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(Fuse.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "fuse"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("linkRtg");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "linkRtg"));
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
