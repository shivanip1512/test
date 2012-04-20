/**
 * CapacitorBank.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Dec 28, 2007 (09:53:13 CST) WSDL2Java emitter.
 */

package com.cannontech.multispeak.deploy.service;

public class CapacitorBank  extends com.cannontech.multispeak.deploy.service.MspBankObject  implements java.io.Serializable {
    private com.cannontech.multispeak.deploy.service.ConnectionCode connectionCd;

    private com.cannontech.multispeak.deploy.service.SwType swType;

    private com.cannontech.multispeak.deploy.service.SwStatus swStatus;

    private java.lang.Float swOn;

    private java.lang.Float swOff;

    private com.cannontech.multispeak.deploy.service.ObjectRef cntrCkt;

    private java.lang.Float bankKvar;

    private java.lang.Float volts;

    private com.cannontech.multispeak.deploy.service.Capacitor[] capacitorList;

    private com.cannontech.multispeak.deploy.service.GPS GPS;

    public CapacitorBank() {
    }

    public CapacitorBank(
           java.lang.String objectID,
           com.cannontech.multispeak.deploy.service.Action verb,
           java.lang.String errorString,
           java.lang.String replaceID,
           java.lang.String utility,
           com.cannontech.multispeak.deploy.service.Extensions extensions,
           java.lang.String comments,
           com.cannontech.multispeak.deploy.service.ExtensionsItem[] extensionsList,
           com.cannontech.multispeak.deploy.service.PointType mapLocation,
           java.lang.String gridLocation,
           java.lang.Float rotation,
           java.lang.String facilityID,
           com.cannontech.multispeak.deploy.service.GraphicSymbol[] graphicSymbol,
           com.cannontech.multispeak.deploy.service.GenericAnnotationFeature[] annotationList,
           com.cannontech.multispeak.deploy.service.NodeIdentifier fromNodeID,
           java.lang.String sectionID,
           com.cannontech.multispeak.deploy.service.NodeIdentifier toNodeID,
           com.cannontech.multispeak.deploy.service.ObjectRef parentSectionID,
           com.cannontech.multispeak.deploy.service.PhaseCd phaseCode,
           com.cannontech.multispeak.deploy.service.MspLoadGroup load,
           com.cannontech.multispeak.deploy.service.ConnectionCode connectionCd,
           com.cannontech.multispeak.deploy.service.SwType swType,
           com.cannontech.multispeak.deploy.service.SwStatus swStatus,
           java.lang.Float swOn,
           java.lang.Float swOff,
           com.cannontech.multispeak.deploy.service.ObjectRef cntrCkt,
           java.lang.Float bankKvar,
           java.lang.Float volts,
           com.cannontech.multispeak.deploy.service.Capacitor[] capacitorList,
           com.cannontech.multispeak.deploy.service.GPS GPS) {
        super(
            objectID,
            verb,
            errorString,
            replaceID,
            utility,
            extensions,
            comments,
            extensionsList,
            mapLocation,
            gridLocation,
            rotation,
            facilityID,
            graphicSymbol,
            annotationList,
            fromNodeID,
            sectionID,
            toNodeID,
            parentSectionID,
            phaseCode,
            load);
        this.connectionCd = connectionCd;
        this.swType = swType;
        this.swStatus = swStatus;
        this.swOn = swOn;
        this.swOff = swOff;
        this.cntrCkt = cntrCkt;
        this.bankKvar = bankKvar;
        this.volts = volts;
        this.capacitorList = capacitorList;
        this.GPS = GPS;
    }


    /**
     * Gets the connectionCd value for this CapacitorBank.
     * 
     * @return connectionCd
     */
    public com.cannontech.multispeak.deploy.service.ConnectionCode getConnectionCd() {
        return connectionCd;
    }


    /**
     * Sets the connectionCd value for this CapacitorBank.
     * 
     * @param connectionCd
     */
    public void setConnectionCd(com.cannontech.multispeak.deploy.service.ConnectionCode connectionCd) {
        this.connectionCd = connectionCd;
    }


    /**
     * Gets the swType value for this CapacitorBank.
     * 
     * @return swType
     */
    public com.cannontech.multispeak.deploy.service.SwType getSwType() {
        return swType;
    }


    /**
     * Sets the swType value for this CapacitorBank.
     * 
     * @param swType
     */
    public void setSwType(com.cannontech.multispeak.deploy.service.SwType swType) {
        this.swType = swType;
    }


    /**
     * Gets the swStatus value for this CapacitorBank.
     * 
     * @return swStatus
     */
    public com.cannontech.multispeak.deploy.service.SwStatus getSwStatus() {
        return swStatus;
    }


    /**
     * Sets the swStatus value for this CapacitorBank.
     * 
     * @param swStatus
     */
    public void setSwStatus(com.cannontech.multispeak.deploy.service.SwStatus swStatus) {
        this.swStatus = swStatus;
    }


    /**
     * Gets the swOn value for this CapacitorBank.
     * 
     * @return swOn
     */
    public java.lang.Float getSwOn() {
        return swOn;
    }


    /**
     * Sets the swOn value for this CapacitorBank.
     * 
     * @param swOn
     */
    public void setSwOn(java.lang.Float swOn) {
        this.swOn = swOn;
    }


    /**
     * Gets the swOff value for this CapacitorBank.
     * 
     * @return swOff
     */
    public java.lang.Float getSwOff() {
        return swOff;
    }


    /**
     * Sets the swOff value for this CapacitorBank.
     * 
     * @param swOff
     */
    public void setSwOff(java.lang.Float swOff) {
        this.swOff = swOff;
    }


    /**
     * Gets the cntrCkt value for this CapacitorBank.
     * 
     * @return cntrCkt
     */
    public com.cannontech.multispeak.deploy.service.ObjectRef getCntrCkt() {
        return cntrCkt;
    }


    /**
     * Sets the cntrCkt value for this CapacitorBank.
     * 
     * @param cntrCkt
     */
    public void setCntrCkt(com.cannontech.multispeak.deploy.service.ObjectRef cntrCkt) {
        this.cntrCkt = cntrCkt;
    }


    /**
     * Gets the bankKvar value for this CapacitorBank.
     * 
     * @return bankKvar
     */
    public java.lang.Float getBankKvar() {
        return bankKvar;
    }


    /**
     * Sets the bankKvar value for this CapacitorBank.
     * 
     * @param bankKvar
     */
    public void setBankKvar(java.lang.Float bankKvar) {
        this.bankKvar = bankKvar;
    }


    /**
     * Gets the volts value for this CapacitorBank.
     * 
     * @return volts
     */
    public java.lang.Float getVolts() {
        return volts;
    }


    /**
     * Sets the volts value for this CapacitorBank.
     * 
     * @param volts
     */
    public void setVolts(java.lang.Float volts) {
        this.volts = volts;
    }


    /**
     * Gets the capacitorList value for this CapacitorBank.
     * 
     * @return capacitorList
     */
    public com.cannontech.multispeak.deploy.service.Capacitor[] getCapacitorList() {
        return capacitorList;
    }


    /**
     * Sets the capacitorList value for this CapacitorBank.
     * 
     * @param capacitorList
     */
    public void setCapacitorList(com.cannontech.multispeak.deploy.service.Capacitor[] capacitorList) {
        this.capacitorList = capacitorList;
    }


    /**
     * Gets the GPS value for this CapacitorBank.
     * 
     * @return GPS
     */
    public com.cannontech.multispeak.deploy.service.GPS getGPS() {
        return GPS;
    }


    /**
     * Sets the GPS value for this CapacitorBank.
     * 
     * @param GPS
     */
    public void setGPS(com.cannontech.multispeak.deploy.service.GPS GPS) {
        this.GPS = GPS;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof CapacitorBank)) return false;
        CapacitorBank other = (CapacitorBank) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = super.equals(obj) && 
            ((this.connectionCd==null && other.getConnectionCd()==null) || 
             (this.connectionCd!=null &&
              this.connectionCd.equals(other.getConnectionCd()))) &&
            ((this.swType==null && other.getSwType()==null) || 
             (this.swType!=null &&
              this.swType.equals(other.getSwType()))) &&
            ((this.swStatus==null && other.getSwStatus()==null) || 
             (this.swStatus!=null &&
              this.swStatus.equals(other.getSwStatus()))) &&
            ((this.swOn==null && other.getSwOn()==null) || 
             (this.swOn!=null &&
              this.swOn.equals(other.getSwOn()))) &&
            ((this.swOff==null && other.getSwOff()==null) || 
             (this.swOff!=null &&
              this.swOff.equals(other.getSwOff()))) &&
            ((this.cntrCkt==null && other.getCntrCkt()==null) || 
             (this.cntrCkt!=null &&
              this.cntrCkt.equals(other.getCntrCkt()))) &&
            ((this.bankKvar==null && other.getBankKvar()==null) || 
             (this.bankKvar!=null &&
              this.bankKvar.equals(other.getBankKvar()))) &&
            ((this.volts==null && other.getVolts()==null) || 
             (this.volts!=null &&
              this.volts.equals(other.getVolts()))) &&
            ((this.capacitorList==null && other.getCapacitorList()==null) || 
             (this.capacitorList!=null &&
              java.util.Arrays.equals(this.capacitorList, other.getCapacitorList()))) &&
            ((this.GPS==null && other.getGPS()==null) || 
             (this.GPS!=null &&
              this.GPS.equals(other.getGPS())));
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
        if (getConnectionCd() != null) {
            _hashCode += getConnectionCd().hashCode();
        }
        if (getSwType() != null) {
            _hashCode += getSwType().hashCode();
        }
        if (getSwStatus() != null) {
            _hashCode += getSwStatus().hashCode();
        }
        if (getSwOn() != null) {
            _hashCode += getSwOn().hashCode();
        }
        if (getSwOff() != null) {
            _hashCode += getSwOff().hashCode();
        }
        if (getCntrCkt() != null) {
            _hashCode += getCntrCkt().hashCode();
        }
        if (getBankKvar() != null) {
            _hashCode += getBankKvar().hashCode();
        }
        if (getVolts() != null) {
            _hashCode += getVolts().hashCode();
        }
        if (getCapacitorList() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getCapacitorList());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getCapacitorList(), i);
                if (obj != null &&
                    !obj.getClass().isArray()) {
                    _hashCode += obj.hashCode();
                }
            }
        }
        if (getGPS() != null) {
            _hashCode += getGPS().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(CapacitorBank.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "capacitorBank"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("connectionCd");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "connectionCd"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "connectionCode"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("swType");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "swType"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "swType"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("swStatus");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "swStatus"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "swStatus"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("swOn");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "swOn"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "float"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("swOff");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "swOff"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "float"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("cntrCkt");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "cntrCkt"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "objectRef"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("bankKvar");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "bankKvar"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "float"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("volts");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "volts"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "float"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("capacitorList");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "capacitorList"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "capacitor"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        elemField.setItemQName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "capacitor"));
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("GPS");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "GPS"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "GPS"));
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
