/**
 * MeterRead.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.2 May 03, 2005 (02:20:24 EDT) WSDL2Java emitter.
 */

package com.cannontech.multispeak;

public class MeterRead  extends com.cannontech.multispeak.MspObject  implements java.io.Serializable {
    private java.lang.String meterNo;
    private java.lang.String deviceID;
    private java.util.Calendar readingDate;
    private java.math.BigInteger posKWh;
    private java.math.BigInteger negKWh;
    private java.lang.Float kW;
    private java.lang.Float kVAr;
    private java.util.Calendar kWDateTime;
    private com.cannontech.multispeak.PhaseCd phase;
    private java.math.BigInteger momentaryOutages;
    private java.math.BigInteger momentaryEvents;
    private java.math.BigInteger sustainedOutages;
    private com.cannontech.multispeak.MeterReadTOUReadings TOUReadings;
    private com.cannontech.multispeak.MeterReadReadingValues readingValues;

    public MeterRead() {
    }

    public MeterRead(
           java.lang.String meterNo,
           java.lang.String deviceID,
           java.util.Calendar readingDate,
           java.math.BigInteger posKWh,
           java.math.BigInteger negKWh,
           java.lang.Float kW,
           java.lang.Float kVAr,
           java.util.Calendar kWDateTime,
           com.cannontech.multispeak.PhaseCd phase,
           java.math.BigInteger momentaryOutages,
           java.math.BigInteger momentaryEvents,
           java.math.BigInteger sustainedOutages,
           com.cannontech.multispeak.MeterReadTOUReadings TOUReadings,
           com.cannontech.multispeak.MeterReadReadingValues readingValues) {
           this.meterNo = meterNo;
           this.deviceID = deviceID;
           this.readingDate = readingDate;
           this.posKWh = posKWh;
           this.negKWh = negKWh;
           this.kW = kW;
           this.kVAr = kVAr;
           this.kWDateTime = kWDateTime;
           this.phase = phase;
           this.momentaryOutages = momentaryOutages;
           this.momentaryEvents = momentaryEvents;
           this.sustainedOutages = sustainedOutages;
           this.TOUReadings = TOUReadings;
           this.readingValues = readingValues;
    }


    /**
     * Gets the meterNo value for this MeterRead.
     * 
     * @return meterNo
     */
    public java.lang.String getMeterNo() {
        return meterNo;
    }


    /**
     * Sets the meterNo value for this MeterRead.
     * 
     * @param meterNo
     */
    public void setMeterNo(java.lang.String meterNo) {
        this.meterNo = meterNo;
    }


    /**
     * Gets the deviceID value for this MeterRead.
     * 
     * @return deviceID
     */
    public java.lang.String getDeviceID() {
        return deviceID;
    }


    /**
     * Sets the deviceID value for this MeterRead.
     * 
     * @param deviceID
     */
    public void setDeviceID(java.lang.String deviceID) {
        this.deviceID = deviceID;
    }


    /**
     * Gets the readingDate value for this MeterRead.
     * 
     * @return readingDate
     */
    public java.util.Calendar getReadingDate() {
        return readingDate;
    }


    /**
     * Sets the readingDate value for this MeterRead.
     * 
     * @param readingDate
     */
    public void setReadingDate(java.util.Calendar readingDate) {
        this.readingDate = readingDate;
    }


    /**
     * Gets the posKWh value for this MeterRead.
     * 
     * @return posKWh
     */
    public java.math.BigInteger getPosKWh() {
        return posKWh;
    }


    /**
     * Sets the posKWh value for this MeterRead.
     * 
     * @param posKWh
     */
    public void setPosKWh(java.math.BigInteger posKWh) {
        this.posKWh = posKWh;
    }


    /**
     * Gets the negKWh value for this MeterRead.
     * 
     * @return negKWh
     */
    public java.math.BigInteger getNegKWh() {
        return negKWh;
    }


    /**
     * Sets the negKWh value for this MeterRead.
     * 
     * @param negKWh
     */
    public void setNegKWh(java.math.BigInteger negKWh) {
        this.negKWh = negKWh;
    }


    /**
     * Gets the kW value for this MeterRead.
     * 
     * @return kW
     */
    public java.lang.Float getKW() {
        return kW;
    }


    /**
     * Sets the kW value for this MeterRead.
     * 
     * @param kW
     */
    public void setKW(java.lang.Float kW) {
        this.kW = kW;
    }


    /**
     * Gets the kVAr value for this MeterRead.
     * 
     * @return kVAr
     */
    public java.lang.Float getKVAr() {
        return kVAr;
    }


    /**
     * Sets the kVAr value for this MeterRead.
     * 
     * @param kVAr
     */
    public void setKVAr(java.lang.Float kVAr) {
        this.kVAr = kVAr;
    }


    /**
     * Gets the kWDateTime value for this MeterRead.
     * 
     * @return kWDateTime
     */
    public java.util.Calendar getKWDateTime() {
        return kWDateTime;
    }


    /**
     * Sets the kWDateTime value for this MeterRead.
     * 
     * @param kWDateTime
     */
    public void setKWDateTime(java.util.Calendar kWDateTime) {
        this.kWDateTime = kWDateTime;
    }


    /**
     * Gets the phase value for this MeterRead.
     * 
     * @return phase
     */
    public com.cannontech.multispeak.PhaseCd getPhase() {
        return phase;
    }


    /**
     * Sets the phase value for this MeterRead.
     * 
     * @param phase
     */
    public void setPhase(com.cannontech.multispeak.PhaseCd phase) {
        this.phase = phase;
    }


    /**
     * Gets the momentaryOutages value for this MeterRead.
     * 
     * @return momentaryOutages
     */
    public java.math.BigInteger getMomentaryOutages() {
        return momentaryOutages;
    }


    /**
     * Sets the momentaryOutages value for this MeterRead.
     * 
     * @param momentaryOutages
     */
    public void setMomentaryOutages(java.math.BigInteger momentaryOutages) {
        this.momentaryOutages = momentaryOutages;
    }


    /**
     * Gets the momentaryEvents value for this MeterRead.
     * 
     * @return momentaryEvents
     */
    public java.math.BigInteger getMomentaryEvents() {
        return momentaryEvents;
    }


    /**
     * Sets the momentaryEvents value for this MeterRead.
     * 
     * @param momentaryEvents
     */
    public void setMomentaryEvents(java.math.BigInteger momentaryEvents) {
        this.momentaryEvents = momentaryEvents;
    }


    /**
     * Gets the sustainedOutages value for this MeterRead.
     * 
     * @return sustainedOutages
     */
    public java.math.BigInteger getSustainedOutages() {
        return sustainedOutages;
    }


    /**
     * Sets the sustainedOutages value for this MeterRead.
     * 
     * @param sustainedOutages
     */
    public void setSustainedOutages(java.math.BigInteger sustainedOutages) {
        this.sustainedOutages = sustainedOutages;
    }


    /**
     * Gets the TOUReadings value for this MeterRead.
     * 
     * @return TOUReadings
     */
    public com.cannontech.multispeak.MeterReadTOUReadings getTOUReadings() {
        return TOUReadings;
    }


    /**
     * Sets the TOUReadings value for this MeterRead.
     * 
     * @param TOUReadings
     */
    public void setTOUReadings(com.cannontech.multispeak.MeterReadTOUReadings TOUReadings) {
        this.TOUReadings = TOUReadings;
    }


    /**
     * Gets the readingValues value for this MeterRead.
     * 
     * @return readingValues
     */
    public com.cannontech.multispeak.MeterReadReadingValues getReadingValues() {
        return readingValues;
    }


    /**
     * Sets the readingValues value for this MeterRead.
     * 
     * @param readingValues
     */
    public void setReadingValues(com.cannontech.multispeak.MeterReadReadingValues readingValues) {
        this.readingValues = readingValues;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof MeterRead)) return false;
        MeterRead other = (MeterRead) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = super.equals(obj) && 
            ((this.meterNo==null && other.getMeterNo()==null) || 
             (this.meterNo!=null &&
              this.meterNo.equals(other.getMeterNo()))) &&
            ((this.deviceID==null && other.getDeviceID()==null) || 
             (this.deviceID!=null &&
              this.deviceID.equals(other.getDeviceID()))) &&
            ((this.readingDate==null && other.getReadingDate()==null) || 
             (this.readingDate!=null &&
              this.readingDate.equals(other.getReadingDate()))) &&
            ((this.posKWh==null && other.getPosKWh()==null) || 
             (this.posKWh!=null &&
              this.posKWh.equals(other.getPosKWh()))) &&
            ((this.negKWh==null && other.getNegKWh()==null) || 
             (this.negKWh!=null &&
              this.negKWh.equals(other.getNegKWh()))) &&
            ((this.kW==null && other.getKW()==null) || 
             (this.kW!=null &&
              this.kW.equals(other.getKW()))) &&
            ((this.kVAr==null && other.getKVAr()==null) || 
             (this.kVAr!=null &&
              this.kVAr.equals(other.getKVAr()))) &&
            ((this.kWDateTime==null && other.getKWDateTime()==null) || 
             (this.kWDateTime!=null &&
              this.kWDateTime.equals(other.getKWDateTime()))) &&
            ((this.phase==null && other.getPhase()==null) || 
             (this.phase!=null &&
              this.phase.equals(other.getPhase()))) &&
            ((this.momentaryOutages==null && other.getMomentaryOutages()==null) || 
             (this.momentaryOutages!=null &&
              this.momentaryOutages.equals(other.getMomentaryOutages()))) &&
            ((this.momentaryEvents==null && other.getMomentaryEvents()==null) || 
             (this.momentaryEvents!=null &&
              this.momentaryEvents.equals(other.getMomentaryEvents()))) &&
            ((this.sustainedOutages==null && other.getSustainedOutages()==null) || 
             (this.sustainedOutages!=null &&
              this.sustainedOutages.equals(other.getSustainedOutages()))) &&
            ((this.TOUReadings==null && other.getTOUReadings()==null) || 
             (this.TOUReadings!=null &&
              this.TOUReadings.equals(other.getTOUReadings()))) &&
            ((this.readingValues==null && other.getReadingValues()==null) || 
             (this.readingValues!=null &&
              this.readingValues.equals(other.getReadingValues())));
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
        if (getMeterNo() != null) {
            _hashCode += getMeterNo().hashCode();
        }
        if (getDeviceID() != null) {
            _hashCode += getDeviceID().hashCode();
        }
        if (getReadingDate() != null) {
            _hashCode += getReadingDate().hashCode();
        }
        if (getPosKWh() != null) {
            _hashCode += getPosKWh().hashCode();
        }
        if (getNegKWh() != null) {
            _hashCode += getNegKWh().hashCode();
        }
        if (getKW() != null) {
            _hashCode += getKW().hashCode();
        }
        if (getKVAr() != null) {
            _hashCode += getKVAr().hashCode();
        }
        if (getKWDateTime() != null) {
            _hashCode += getKWDateTime().hashCode();
        }
        if (getPhase() != null) {
            _hashCode += getPhase().hashCode();
        }
        if (getMomentaryOutages() != null) {
            _hashCode += getMomentaryOutages().hashCode();
        }
        if (getMomentaryEvents() != null) {
            _hashCode += getMomentaryEvents().hashCode();
        }
        if (getSustainedOutages() != null) {
            _hashCode += getSustainedOutages().hashCode();
        }
        if (getTOUReadings() != null) {
            _hashCode += getTOUReadings().hashCode();
        }
        if (getReadingValues() != null) {
            _hashCode += getReadingValues().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(MeterRead.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "meterRead"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("meterNo");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "meterNo"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("deviceID");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "deviceID"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("readingDate");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "readingDate"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "dateTime"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("posKWh");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "posKWh"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "integer"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("negKWh");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "negKWh"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "integer"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("KW");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "kW"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "float"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("KVAr");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "kVAr"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "float"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("KWDateTime");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "kWDateTime"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "dateTime"));
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
        elemField.setFieldName("momentaryOutages");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "momentaryOutages"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "integer"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("momentaryEvents");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "momentaryEvents"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "integer"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("sustainedOutages");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "sustainedOutages"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "integer"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("TOUReadings");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "TOUReadings"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "meterReadTOUReadings"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("readingValues");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "readingValues"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "meterReadReadingValues"));
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
