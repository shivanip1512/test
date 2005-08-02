/**
 * PowerMonitor.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.2 May 03, 2005 (02:20:24 EDT) WSDL2Java emitter.
 */

package com.cannontech.multispeak;

public class PowerMonitor  extends com.cannontech.multispeak.MspObject  implements java.io.Serializable {
    private java.util.Calendar callTime;
    private java.math.BigInteger recordNum;
    private java.lang.String areaCode;
    private java.lang.String phone;
    private com.cannontech.multispeak.PmEventCode eventCode;
    private java.lang.Float voltage;
    private java.lang.Boolean ack;
    private java.lang.String ackBy;
    private java.util.Calendar ackTime;

    public PowerMonitor() {
    }

    public PowerMonitor(
           java.util.Calendar callTime,
           java.math.BigInteger recordNum,
           java.lang.String areaCode,
           java.lang.String phone,
           com.cannontech.multispeak.PmEventCode eventCode,
           java.lang.Float voltage,
           java.lang.Boolean ack,
           java.lang.String ackBy,
           java.util.Calendar ackTime) {
           this.callTime = callTime;
           this.recordNum = recordNum;
           this.areaCode = areaCode;
           this.phone = phone;
           this.eventCode = eventCode;
           this.voltage = voltage;
           this.ack = ack;
           this.ackBy = ackBy;
           this.ackTime = ackTime;
    }


    /**
     * Gets the callTime value for this PowerMonitor.
     * 
     * @return callTime
     */
    public java.util.Calendar getCallTime() {
        return callTime;
    }


    /**
     * Sets the callTime value for this PowerMonitor.
     * 
     * @param callTime
     */
    public void setCallTime(java.util.Calendar callTime) {
        this.callTime = callTime;
    }


    /**
     * Gets the recordNum value for this PowerMonitor.
     * 
     * @return recordNum
     */
    public java.math.BigInteger getRecordNum() {
        return recordNum;
    }


    /**
     * Sets the recordNum value for this PowerMonitor.
     * 
     * @param recordNum
     */
    public void setRecordNum(java.math.BigInteger recordNum) {
        this.recordNum = recordNum;
    }


    /**
     * Gets the areaCode value for this PowerMonitor.
     * 
     * @return areaCode
     */
    public java.lang.String getAreaCode() {
        return areaCode;
    }


    /**
     * Sets the areaCode value for this PowerMonitor.
     * 
     * @param areaCode
     */
    public void setAreaCode(java.lang.String areaCode) {
        this.areaCode = areaCode;
    }


    /**
     * Gets the phone value for this PowerMonitor.
     * 
     * @return phone
     */
    public java.lang.String getPhone() {
        return phone;
    }


    /**
     * Sets the phone value for this PowerMonitor.
     * 
     * @param phone
     */
    public void setPhone(java.lang.String phone) {
        this.phone = phone;
    }


    /**
     * Gets the eventCode value for this PowerMonitor.
     * 
     * @return eventCode
     */
    public com.cannontech.multispeak.PmEventCode getEventCode() {
        return eventCode;
    }


    /**
     * Sets the eventCode value for this PowerMonitor.
     * 
     * @param eventCode
     */
    public void setEventCode(com.cannontech.multispeak.PmEventCode eventCode) {
        this.eventCode = eventCode;
    }


    /**
     * Gets the voltage value for this PowerMonitor.
     * 
     * @return voltage
     */
    public java.lang.Float getVoltage() {
        return voltage;
    }


    /**
     * Sets the voltage value for this PowerMonitor.
     * 
     * @param voltage
     */
    public void setVoltage(java.lang.Float voltage) {
        this.voltage = voltage;
    }


    /**
     * Gets the ack value for this PowerMonitor.
     * 
     * @return ack
     */
    public java.lang.Boolean getAck() {
        return ack;
    }


    /**
     * Sets the ack value for this PowerMonitor.
     * 
     * @param ack
     */
    public void setAck(java.lang.Boolean ack) {
        this.ack = ack;
    }


    /**
     * Gets the ackBy value for this PowerMonitor.
     * 
     * @return ackBy
     */
    public java.lang.String getAckBy() {
        return ackBy;
    }


    /**
     * Sets the ackBy value for this PowerMonitor.
     * 
     * @param ackBy
     */
    public void setAckBy(java.lang.String ackBy) {
        this.ackBy = ackBy;
    }


    /**
     * Gets the ackTime value for this PowerMonitor.
     * 
     * @return ackTime
     */
    public java.util.Calendar getAckTime() {
        return ackTime;
    }


    /**
     * Sets the ackTime value for this PowerMonitor.
     * 
     * @param ackTime
     */
    public void setAckTime(java.util.Calendar ackTime) {
        this.ackTime = ackTime;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof PowerMonitor)) return false;
        PowerMonitor other = (PowerMonitor) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = super.equals(obj) && 
            ((this.callTime==null && other.getCallTime()==null) || 
             (this.callTime!=null &&
              this.callTime.equals(other.getCallTime()))) &&
            ((this.recordNum==null && other.getRecordNum()==null) || 
             (this.recordNum!=null &&
              this.recordNum.equals(other.getRecordNum()))) &&
            ((this.areaCode==null && other.getAreaCode()==null) || 
             (this.areaCode!=null &&
              this.areaCode.equals(other.getAreaCode()))) &&
            ((this.phone==null && other.getPhone()==null) || 
             (this.phone!=null &&
              this.phone.equals(other.getPhone()))) &&
            ((this.eventCode==null && other.getEventCode()==null) || 
             (this.eventCode!=null &&
              this.eventCode.equals(other.getEventCode()))) &&
            ((this.voltage==null && other.getVoltage()==null) || 
             (this.voltage!=null &&
              this.voltage.equals(other.getVoltage()))) &&
            ((this.ack==null && other.getAck()==null) || 
             (this.ack!=null &&
              this.ack.equals(other.getAck()))) &&
            ((this.ackBy==null && other.getAckBy()==null) || 
             (this.ackBy!=null &&
              this.ackBy.equals(other.getAckBy()))) &&
            ((this.ackTime==null && other.getAckTime()==null) || 
             (this.ackTime!=null &&
              this.ackTime.equals(other.getAckTime())));
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
        if (getCallTime() != null) {
            _hashCode += getCallTime().hashCode();
        }
        if (getRecordNum() != null) {
            _hashCode += getRecordNum().hashCode();
        }
        if (getAreaCode() != null) {
            _hashCode += getAreaCode().hashCode();
        }
        if (getPhone() != null) {
            _hashCode += getPhone().hashCode();
        }
        if (getEventCode() != null) {
            _hashCode += getEventCode().hashCode();
        }
        if (getVoltage() != null) {
            _hashCode += getVoltage().hashCode();
        }
        if (getAck() != null) {
            _hashCode += getAck().hashCode();
        }
        if (getAckBy() != null) {
            _hashCode += getAckBy().hashCode();
        }
        if (getAckTime() != null) {
            _hashCode += getAckTime().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(PowerMonitor.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "powerMonitor"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("callTime");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "callTime"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "dateTime"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("recordNum");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "recordNum"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "integer"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("areaCode");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "areaCode"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("phone");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "phone"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("eventCode");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "eventCode"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "pmEventCode"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("voltage");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "voltage"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "float"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("ack");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ack"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "boolean"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("ackBy");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ackBy"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("ackTime");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ackTime"));
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
