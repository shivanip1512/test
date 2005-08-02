/**
 * MeterReading.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.2 May 03, 2005 (02:20:24 EDT) WSDL2Java emitter.
 */

package com.cannontech.multispeak;

public class MeterReading  implements java.io.Serializable {
    private java.lang.String meterID;
    private java.lang.String meterNo;
    private java.util.Calendar dateTime;
    private java.util.Calendar acquisitionTime;
    private org.apache.axis.types.UnsignedByte season;
    private org.apache.axis.types.UnsignedInt numberOfResets;
    private com.cannontech.multispeak.ReadingStatus[] status;
    private com.cannontech.multispeak.Registers[] registers;

    public MeterReading() {
    }

    public MeterReading(
           java.lang.String meterID,
           java.lang.String meterNo,
           java.util.Calendar dateTime,
           java.util.Calendar acquisitionTime,
           org.apache.axis.types.UnsignedByte season,
           org.apache.axis.types.UnsignedInt numberOfResets,
           com.cannontech.multispeak.ReadingStatus[] status,
           com.cannontech.multispeak.Registers[] registers) {
           this.meterID = meterID;
           this.meterNo = meterNo;
           this.dateTime = dateTime;
           this.acquisitionTime = acquisitionTime;
           this.season = season;
           this.numberOfResets = numberOfResets;
           this.status = status;
           this.registers = registers;
    }


    /**
     * Gets the meterID value for this MeterReading.
     * 
     * @return meterID
     */
    public java.lang.String getMeterID() {
        return meterID;
    }


    /**
     * Sets the meterID value for this MeterReading.
     * 
     * @param meterID
     */
    public void setMeterID(java.lang.String meterID) {
        this.meterID = meterID;
    }


    /**
     * Gets the meterNo value for this MeterReading.
     * 
     * @return meterNo
     */
    public java.lang.String getMeterNo() {
        return meterNo;
    }


    /**
     * Sets the meterNo value for this MeterReading.
     * 
     * @param meterNo
     */
    public void setMeterNo(java.lang.String meterNo) {
        this.meterNo = meterNo;
    }


    /**
     * Gets the dateTime value for this MeterReading.
     * 
     * @return dateTime
     */
    public java.util.Calendar getDateTime() {
        return dateTime;
    }


    /**
     * Sets the dateTime value for this MeterReading.
     * 
     * @param dateTime
     */
    public void setDateTime(java.util.Calendar dateTime) {
        this.dateTime = dateTime;
    }


    /**
     * Gets the acquisitionTime value for this MeterReading.
     * 
     * @return acquisitionTime
     */
    public java.util.Calendar getAcquisitionTime() {
        return acquisitionTime;
    }


    /**
     * Sets the acquisitionTime value for this MeterReading.
     * 
     * @param acquisitionTime
     */
    public void setAcquisitionTime(java.util.Calendar acquisitionTime) {
        this.acquisitionTime = acquisitionTime;
    }


    /**
     * Gets the season value for this MeterReading.
     * 
     * @return season
     */
    public org.apache.axis.types.UnsignedByte getSeason() {
        return season;
    }


    /**
     * Sets the season value for this MeterReading.
     * 
     * @param season
     */
    public void setSeason(org.apache.axis.types.UnsignedByte season) {
        this.season = season;
    }


    /**
     * Gets the numberOfResets value for this MeterReading.
     * 
     * @return numberOfResets
     */
    public org.apache.axis.types.UnsignedInt getNumberOfResets() {
        return numberOfResets;
    }


    /**
     * Sets the numberOfResets value for this MeterReading.
     * 
     * @param numberOfResets
     */
    public void setNumberOfResets(org.apache.axis.types.UnsignedInt numberOfResets) {
        this.numberOfResets = numberOfResets;
    }


    /**
     * Gets the status value for this MeterReading.
     * 
     * @return status
     */
    public com.cannontech.multispeak.ReadingStatus[] getStatus() {
        return status;
    }


    /**
     * Sets the status value for this MeterReading.
     * 
     * @param status
     */
    public void setStatus(com.cannontech.multispeak.ReadingStatus[] status) {
        this.status = status;
    }

    public com.cannontech.multispeak.ReadingStatus getStatus(int i) {
        return this.status[i];
    }

    public void setStatus(int i, com.cannontech.multispeak.ReadingStatus _value) {
        this.status[i] = _value;
    }


    /**
     * Gets the registers value for this MeterReading.
     * 
     * @return registers
     */
    public com.cannontech.multispeak.Registers[] getRegisters() {
        return registers;
    }


    /**
     * Sets the registers value for this MeterReading.
     * 
     * @param registers
     */
    public void setRegisters(com.cannontech.multispeak.Registers[] registers) {
        this.registers = registers;
    }

    public com.cannontech.multispeak.Registers getRegisters(int i) {
        return this.registers[i];
    }

    public void setRegisters(int i, com.cannontech.multispeak.Registers _value) {
        this.registers[i] = _value;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof MeterReading)) return false;
        MeterReading other = (MeterReading) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.meterID==null && other.getMeterID()==null) || 
             (this.meterID!=null &&
              this.meterID.equals(other.getMeterID()))) &&
            ((this.meterNo==null && other.getMeterNo()==null) || 
             (this.meterNo!=null &&
              this.meterNo.equals(other.getMeterNo()))) &&
            ((this.dateTime==null && other.getDateTime()==null) || 
             (this.dateTime!=null &&
              this.dateTime.equals(other.getDateTime()))) &&
            ((this.acquisitionTime==null && other.getAcquisitionTime()==null) || 
             (this.acquisitionTime!=null &&
              this.acquisitionTime.equals(other.getAcquisitionTime()))) &&
            ((this.season==null && other.getSeason()==null) || 
             (this.season!=null &&
              this.season.equals(other.getSeason()))) &&
            ((this.numberOfResets==null && other.getNumberOfResets()==null) || 
             (this.numberOfResets!=null &&
              this.numberOfResets.equals(other.getNumberOfResets()))) &&
            ((this.status==null && other.getStatus()==null) || 
             (this.status!=null &&
              java.util.Arrays.equals(this.status, other.getStatus()))) &&
            ((this.registers==null && other.getRegisters()==null) || 
             (this.registers!=null &&
              java.util.Arrays.equals(this.registers, other.getRegisters())));
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
        if (getMeterID() != null) {
            _hashCode += getMeterID().hashCode();
        }
        if (getMeterNo() != null) {
            _hashCode += getMeterNo().hashCode();
        }
        if (getDateTime() != null) {
            _hashCode += getDateTime().hashCode();
        }
        if (getAcquisitionTime() != null) {
            _hashCode += getAcquisitionTime().hashCode();
        }
        if (getSeason() != null) {
            _hashCode += getSeason().hashCode();
        }
        if (getNumberOfResets() != null) {
            _hashCode += getNumberOfResets().hashCode();
        }
        if (getStatus() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getStatus());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getStatus(), i);
                if (obj != null &&
                    !obj.getClass().isArray()) {
                    _hashCode += obj.hashCode();
                }
            }
        }
        if (getRegisters() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getRegisters());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getRegisters(), i);
                if (obj != null &&
                    !obj.getClass().isArray()) {
                    _hashCode += obj.hashCode();
                }
            }
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(MeterReading.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "meterReading"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("meterID");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "meterID"));
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
        elemField.setFieldName("dateTime");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "dateTime"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "dateTime"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("acquisitionTime");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "acquisitionTime"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "dateTime"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("season");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "season"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "unsignedByte"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("numberOfResets");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "numberOfResets"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "unsignedInt"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("status");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "status"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "readingStatus"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        elemField.setMaxOccursUnbounded(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("registers");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "registers"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "registers"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        elemField.setMaxOccursUnbounded(true);
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
