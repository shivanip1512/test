/**
 * Jumper.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Dec 28, 2007 (09:53:13 CST) WSDL2Java emitter.
 */

package com.cannontech.multispeak.deploy.service;

public class Jumper  extends com.cannontech.multispeak.deploy.service.MspSwitchingDevice  implements java.io.Serializable {
    private java.util.Calendar dateInstalled;

    public Jumper() {
    }

    public Jumper(
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
           java.util.Calendar dateInstalled) {
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
            mounting);
        this.dateInstalled = dateInstalled;
    }


    /**
     * Gets the dateInstalled value for this Jumper.
     * 
     * @return dateInstalled
     */
    public java.util.Calendar getDateInstalled() {
        return dateInstalled;
    }


    /**
     * Sets the dateInstalled value for this Jumper.
     * 
     * @param dateInstalled
     */
    public void setDateInstalled(java.util.Calendar dateInstalled) {
        this.dateInstalled = dateInstalled;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof Jumper)) return false;
        Jumper other = (Jumper) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = super.equals(obj) && 
            ((this.dateInstalled==null && other.getDateInstalled()==null) || 
             (this.dateInstalled!=null &&
              this.dateInstalled.equals(other.getDateInstalled())));
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
        if (getDateInstalled() != null) {
            _hashCode += getDateInstalled().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(Jumper.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "jumper"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("dateInstalled");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "dateInstalled"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "dateTime"));
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
