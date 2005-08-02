/**
 * MspSwitchingDevice.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.2 May 03, 2005 (02:20:24 EDT) WSDL2Java emitter.
 */

package com.cannontech.multispeak;

public class MspSwitchingDevice  extends com.cannontech.multispeak.MspObject  implements java.io.Serializable {
    private java.lang.String eaEquipment;
    private java.lang.String facilityID;
    private com.cannontech.multispeak.PhaseCd phase;
    private com.cannontech.multispeak.Position position;
    private java.lang.Float ratedVolt;
    private java.lang.Float operVolt;
    private java.lang.Float maxContAmp;
    private java.lang.String manufacturer;
    private com.cannontech.multispeak.Mounting mounting;

    public MspSwitchingDevice() {
    }

    public MspSwitchingDevice(
           java.lang.String eaEquipment,
           java.lang.String facilityID,
           com.cannontech.multispeak.PhaseCd phase,
           com.cannontech.multispeak.Position position,
           java.lang.Float ratedVolt,
           java.lang.Float operVolt,
           java.lang.Float maxContAmp,
           java.lang.String manufacturer,
           com.cannontech.multispeak.Mounting mounting) {
           this.eaEquipment = eaEquipment;
           this.facilityID = facilityID;
           this.phase = phase;
           this.position = position;
           this.ratedVolt = ratedVolt;
           this.operVolt = operVolt;
           this.maxContAmp = maxContAmp;
           this.manufacturer = manufacturer;
           this.mounting = mounting;
    }


    /**
     * Gets the eaEquipment value for this MspSwitchingDevice.
     * 
     * @return eaEquipment
     */
    public java.lang.String getEaEquipment() {
        return eaEquipment;
    }


    /**
     * Sets the eaEquipment value for this MspSwitchingDevice.
     * 
     * @param eaEquipment
     */
    public void setEaEquipment(java.lang.String eaEquipment) {
        this.eaEquipment = eaEquipment;
    }


    /**
     * Gets the facilityID value for this MspSwitchingDevice.
     * 
     * @return facilityID
     */
    public java.lang.String getFacilityID() {
        return facilityID;
    }


    /**
     * Sets the facilityID value for this MspSwitchingDevice.
     * 
     * @param facilityID
     */
    public void setFacilityID(java.lang.String facilityID) {
        this.facilityID = facilityID;
    }


    /**
     * Gets the phase value for this MspSwitchingDevice.
     * 
     * @return phase
     */
    public com.cannontech.multispeak.PhaseCd getPhase() {
        return phase;
    }


    /**
     * Sets the phase value for this MspSwitchingDevice.
     * 
     * @param phase
     */
    public void setPhase(com.cannontech.multispeak.PhaseCd phase) {
        this.phase = phase;
    }


    /**
     * Gets the position value for this MspSwitchingDevice.
     * 
     * @return position
     */
    public com.cannontech.multispeak.Position getPosition() {
        return position;
    }


    /**
     * Sets the position value for this MspSwitchingDevice.
     * 
     * @param position
     */
    public void setPosition(com.cannontech.multispeak.Position position) {
        this.position = position;
    }


    /**
     * Gets the ratedVolt value for this MspSwitchingDevice.
     * 
     * @return ratedVolt
     */
    public java.lang.Float getRatedVolt() {
        return ratedVolt;
    }


    /**
     * Sets the ratedVolt value for this MspSwitchingDevice.
     * 
     * @param ratedVolt
     */
    public void setRatedVolt(java.lang.Float ratedVolt) {
        this.ratedVolt = ratedVolt;
    }


    /**
     * Gets the operVolt value for this MspSwitchingDevice.
     * 
     * @return operVolt
     */
    public java.lang.Float getOperVolt() {
        return operVolt;
    }


    /**
     * Sets the operVolt value for this MspSwitchingDevice.
     * 
     * @param operVolt
     */
    public void setOperVolt(java.lang.Float operVolt) {
        this.operVolt = operVolt;
    }


    /**
     * Gets the maxContAmp value for this MspSwitchingDevice.
     * 
     * @return maxContAmp
     */
    public java.lang.Float getMaxContAmp() {
        return maxContAmp;
    }


    /**
     * Sets the maxContAmp value for this MspSwitchingDevice.
     * 
     * @param maxContAmp
     */
    public void setMaxContAmp(java.lang.Float maxContAmp) {
        this.maxContAmp = maxContAmp;
    }


    /**
     * Gets the manufacturer value for this MspSwitchingDevice.
     * 
     * @return manufacturer
     */
    public java.lang.String getManufacturer() {
        return manufacturer;
    }


    /**
     * Sets the manufacturer value for this MspSwitchingDevice.
     * 
     * @param manufacturer
     */
    public void setManufacturer(java.lang.String manufacturer) {
        this.manufacturer = manufacturer;
    }


    /**
     * Gets the mounting value for this MspSwitchingDevice.
     * 
     * @return mounting
     */
    public com.cannontech.multispeak.Mounting getMounting() {
        return mounting;
    }


    /**
     * Sets the mounting value for this MspSwitchingDevice.
     * 
     * @param mounting
     */
    public void setMounting(com.cannontech.multispeak.Mounting mounting) {
        this.mounting = mounting;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof MspSwitchingDevice)) return false;
        MspSwitchingDevice other = (MspSwitchingDevice) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = super.equals(obj) && 
            ((this.eaEquipment==null && other.getEaEquipment()==null) || 
             (this.eaEquipment!=null &&
              this.eaEquipment.equals(other.getEaEquipment()))) &&
            ((this.facilityID==null && other.getFacilityID()==null) || 
             (this.facilityID!=null &&
              this.facilityID.equals(other.getFacilityID()))) &&
            ((this.phase==null && other.getPhase()==null) || 
             (this.phase!=null &&
              this.phase.equals(other.getPhase()))) &&
            ((this.position==null && other.getPosition()==null) || 
             (this.position!=null &&
              this.position.equals(other.getPosition()))) &&
            ((this.ratedVolt==null && other.getRatedVolt()==null) || 
             (this.ratedVolt!=null &&
              this.ratedVolt.equals(other.getRatedVolt()))) &&
            ((this.operVolt==null && other.getOperVolt()==null) || 
             (this.operVolt!=null &&
              this.operVolt.equals(other.getOperVolt()))) &&
            ((this.maxContAmp==null && other.getMaxContAmp()==null) || 
             (this.maxContAmp!=null &&
              this.maxContAmp.equals(other.getMaxContAmp()))) &&
            ((this.manufacturer==null && other.getManufacturer()==null) || 
             (this.manufacturer!=null &&
              this.manufacturer.equals(other.getManufacturer()))) &&
            ((this.mounting==null && other.getMounting()==null) || 
             (this.mounting!=null &&
              this.mounting.equals(other.getMounting())));
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
        if (getEaEquipment() != null) {
            _hashCode += getEaEquipment().hashCode();
        }
        if (getFacilityID() != null) {
            _hashCode += getFacilityID().hashCode();
        }
        if (getPhase() != null) {
            _hashCode += getPhase().hashCode();
        }
        if (getPosition() != null) {
            _hashCode += getPosition().hashCode();
        }
        if (getRatedVolt() != null) {
            _hashCode += getRatedVolt().hashCode();
        }
        if (getOperVolt() != null) {
            _hashCode += getOperVolt().hashCode();
        }
        if (getMaxContAmp() != null) {
            _hashCode += getMaxContAmp().hashCode();
        }
        if (getManufacturer() != null) {
            _hashCode += getManufacturer().hashCode();
        }
        if (getMounting() != null) {
            _hashCode += getMounting().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(MspSwitchingDevice.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "mspSwitchingDevice"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("eaEquipment");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "eaEquipment"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("facilityID");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "facilityID"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("phase");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "phase"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "phaseCd"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("position");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "position"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "position"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("ratedVolt");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ratedVolt"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "float"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("operVolt");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "operVolt"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "float"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("maxContAmp");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "maxContAmp"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "float"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("manufacturer");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "manufacturer"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("mounting");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "mounting"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "mounting"));
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
