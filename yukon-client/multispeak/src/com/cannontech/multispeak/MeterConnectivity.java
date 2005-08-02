/**
 * MeterConnectivity.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.2 May 03, 2005 (02:20:24 EDT) WSDL2Java emitter.
 */

package com.cannontech.multispeak;

public class MeterConnectivity  extends com.cannontech.multispeak.MspObject  implements java.io.Serializable {
    private java.lang.String outageRecordID;
    private java.util.Calendar eventTime;
    private java.lang.String servLoc;
    private java.lang.String meterNo;
    private java.lang.String oldSubstation;
    private java.lang.String oldFeeder;
    private com.cannontech.multispeak.PhaseCd oldPhase;
    private java.lang.String newSubstation;
    private java.lang.String newFeeder;
    private com.cannontech.multispeak.PhaseCd newPhase;

    public MeterConnectivity() {
    }

    public MeterConnectivity(
           java.lang.String outageRecordID,
           java.util.Calendar eventTime,
           java.lang.String servLoc,
           java.lang.String meterNo,
           java.lang.String oldSubstation,
           java.lang.String oldFeeder,
           com.cannontech.multispeak.PhaseCd oldPhase,
           java.lang.String newSubstation,
           java.lang.String newFeeder,
           com.cannontech.multispeak.PhaseCd newPhase) {
           this.outageRecordID = outageRecordID;
           this.eventTime = eventTime;
           this.servLoc = servLoc;
           this.meterNo = meterNo;
           this.oldSubstation = oldSubstation;
           this.oldFeeder = oldFeeder;
           this.oldPhase = oldPhase;
           this.newSubstation = newSubstation;
           this.newFeeder = newFeeder;
           this.newPhase = newPhase;
    }


    /**
     * Gets the outageRecordID value for this MeterConnectivity.
     * 
     * @return outageRecordID
     */
    public java.lang.String getOutageRecordID() {
        return outageRecordID;
    }


    /**
     * Sets the outageRecordID value for this MeterConnectivity.
     * 
     * @param outageRecordID
     */
    public void setOutageRecordID(java.lang.String outageRecordID) {
        this.outageRecordID = outageRecordID;
    }


    /**
     * Gets the eventTime value for this MeterConnectivity.
     * 
     * @return eventTime
     */
    public java.util.Calendar getEventTime() {
        return eventTime;
    }


    /**
     * Sets the eventTime value for this MeterConnectivity.
     * 
     * @param eventTime
     */
    public void setEventTime(java.util.Calendar eventTime) {
        this.eventTime = eventTime;
    }


    /**
     * Gets the servLoc value for this MeterConnectivity.
     * 
     * @return servLoc
     */
    public java.lang.String getServLoc() {
        return servLoc;
    }


    /**
     * Sets the servLoc value for this MeterConnectivity.
     * 
     * @param servLoc
     */
    public void setServLoc(java.lang.String servLoc) {
        this.servLoc = servLoc;
    }


    /**
     * Gets the meterNo value for this MeterConnectivity.
     * 
     * @return meterNo
     */
    public java.lang.String getMeterNo() {
        return meterNo;
    }


    /**
     * Sets the meterNo value for this MeterConnectivity.
     * 
     * @param meterNo
     */
    public void setMeterNo(java.lang.String meterNo) {
        this.meterNo = meterNo;
    }


    /**
     * Gets the oldSubstation value for this MeterConnectivity.
     * 
     * @return oldSubstation
     */
    public java.lang.String getOldSubstation() {
        return oldSubstation;
    }


    /**
     * Sets the oldSubstation value for this MeterConnectivity.
     * 
     * @param oldSubstation
     */
    public void setOldSubstation(java.lang.String oldSubstation) {
        this.oldSubstation = oldSubstation;
    }


    /**
     * Gets the oldFeeder value for this MeterConnectivity.
     * 
     * @return oldFeeder
     */
    public java.lang.String getOldFeeder() {
        return oldFeeder;
    }


    /**
     * Sets the oldFeeder value for this MeterConnectivity.
     * 
     * @param oldFeeder
     */
    public void setOldFeeder(java.lang.String oldFeeder) {
        this.oldFeeder = oldFeeder;
    }


    /**
     * Gets the oldPhase value for this MeterConnectivity.
     * 
     * @return oldPhase
     */
    public com.cannontech.multispeak.PhaseCd getOldPhase() {
        return oldPhase;
    }


    /**
     * Sets the oldPhase value for this MeterConnectivity.
     * 
     * @param oldPhase
     */
    public void setOldPhase(com.cannontech.multispeak.PhaseCd oldPhase) {
        this.oldPhase = oldPhase;
    }


    /**
     * Gets the newSubstation value for this MeterConnectivity.
     * 
     * @return newSubstation
     */
    public java.lang.String getNewSubstation() {
        return newSubstation;
    }


    /**
     * Sets the newSubstation value for this MeterConnectivity.
     * 
     * @param newSubstation
     */
    public void setNewSubstation(java.lang.String newSubstation) {
        this.newSubstation = newSubstation;
    }


    /**
     * Gets the newFeeder value for this MeterConnectivity.
     * 
     * @return newFeeder
     */
    public java.lang.String getNewFeeder() {
        return newFeeder;
    }


    /**
     * Sets the newFeeder value for this MeterConnectivity.
     * 
     * @param newFeeder
     */
    public void setNewFeeder(java.lang.String newFeeder) {
        this.newFeeder = newFeeder;
    }


    /**
     * Gets the newPhase value for this MeterConnectivity.
     * 
     * @return newPhase
     */
    public com.cannontech.multispeak.PhaseCd getNewPhase() {
        return newPhase;
    }


    /**
     * Sets the newPhase value for this MeterConnectivity.
     * 
     * @param newPhase
     */
    public void setNewPhase(com.cannontech.multispeak.PhaseCd newPhase) {
        this.newPhase = newPhase;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof MeterConnectivity)) return false;
        MeterConnectivity other = (MeterConnectivity) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = super.equals(obj) && 
            ((this.outageRecordID==null && other.getOutageRecordID()==null) || 
             (this.outageRecordID!=null &&
              this.outageRecordID.equals(other.getOutageRecordID()))) &&
            ((this.eventTime==null && other.getEventTime()==null) || 
             (this.eventTime!=null &&
              this.eventTime.equals(other.getEventTime()))) &&
            ((this.servLoc==null && other.getServLoc()==null) || 
             (this.servLoc!=null &&
              this.servLoc.equals(other.getServLoc()))) &&
            ((this.meterNo==null && other.getMeterNo()==null) || 
             (this.meterNo!=null &&
              this.meterNo.equals(other.getMeterNo()))) &&
            ((this.oldSubstation==null && other.getOldSubstation()==null) || 
             (this.oldSubstation!=null &&
              this.oldSubstation.equals(other.getOldSubstation()))) &&
            ((this.oldFeeder==null && other.getOldFeeder()==null) || 
             (this.oldFeeder!=null &&
              this.oldFeeder.equals(other.getOldFeeder()))) &&
            ((this.oldPhase==null && other.getOldPhase()==null) || 
             (this.oldPhase!=null &&
              this.oldPhase.equals(other.getOldPhase()))) &&
            ((this.newSubstation==null && other.getNewSubstation()==null) || 
             (this.newSubstation!=null &&
              this.newSubstation.equals(other.getNewSubstation()))) &&
            ((this.newFeeder==null && other.getNewFeeder()==null) || 
             (this.newFeeder!=null &&
              this.newFeeder.equals(other.getNewFeeder()))) &&
            ((this.newPhase==null && other.getNewPhase()==null) || 
             (this.newPhase!=null &&
              this.newPhase.equals(other.getNewPhase())));
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
        if (getOutageRecordID() != null) {
            _hashCode += getOutageRecordID().hashCode();
        }
        if (getEventTime() != null) {
            _hashCode += getEventTime().hashCode();
        }
        if (getServLoc() != null) {
            _hashCode += getServLoc().hashCode();
        }
        if (getMeterNo() != null) {
            _hashCode += getMeterNo().hashCode();
        }
        if (getOldSubstation() != null) {
            _hashCode += getOldSubstation().hashCode();
        }
        if (getOldFeeder() != null) {
            _hashCode += getOldFeeder().hashCode();
        }
        if (getOldPhase() != null) {
            _hashCode += getOldPhase().hashCode();
        }
        if (getNewSubstation() != null) {
            _hashCode += getNewSubstation().hashCode();
        }
        if (getNewFeeder() != null) {
            _hashCode += getNewFeeder().hashCode();
        }
        if (getNewPhase() != null) {
            _hashCode += getNewPhase().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(MeterConnectivity.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "meterConnectivity"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("outageRecordID");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "outageRecordID"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("eventTime");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "eventTime"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "dateTime"));
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
        elemField.setFieldName("meterNo");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "meterNo"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("oldSubstation");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "oldSubstation"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("oldFeeder");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "oldFeeder"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("oldPhase");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "oldPhase"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "phaseCd"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("newSubstation");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "newSubstation"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("newFeeder");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "newFeeder"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("newPhase");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "newPhase"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "phaseCd"));
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
