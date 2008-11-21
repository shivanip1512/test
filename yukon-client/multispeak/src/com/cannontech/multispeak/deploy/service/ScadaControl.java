/**
 * ScadaControl.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Dec 28, 2007 (09:53:13 CST) WSDL2Java emitter.
 */

package com.cannontech.multispeak.deploy.service;

public class ScadaControl  implements java.io.Serializable {
    private java.lang.String scadaPointID;

    private java.lang.String controlKey;

    private com.cannontech.multispeak.deploy.service.ScadaControlFunction function;

    private com.cannontech.multispeak.deploy.service.ScadaControlRelayType relayType;

    private com.cannontech.multispeak.deploy.service.ScadaControlControlCode controlCode;

    private java.lang.Float onTime;

    private java.lang.Float offTime;

    private java.math.BigInteger count;

    private com.cannontech.multispeak.deploy.service.ScadaControlControlStatus controlStatus;

    public ScadaControl() {
    }

    public ScadaControl(
           java.lang.String scadaPointID,
           java.lang.String controlKey,
           com.cannontech.multispeak.deploy.service.ScadaControlFunction function,
           com.cannontech.multispeak.deploy.service.ScadaControlRelayType relayType,
           com.cannontech.multispeak.deploy.service.ScadaControlControlCode controlCode,
           java.lang.Float onTime,
           java.lang.Float offTime,
           java.math.BigInteger count,
           com.cannontech.multispeak.deploy.service.ScadaControlControlStatus controlStatus) {
           this.scadaPointID = scadaPointID;
           this.controlKey = controlKey;
           this.function = function;
           this.relayType = relayType;
           this.controlCode = controlCode;
           this.onTime = onTime;
           this.offTime = offTime;
           this.count = count;
           this.controlStatus = controlStatus;
    }


    /**
     * Gets the scadaPointID value for this ScadaControl.
     * 
     * @return scadaPointID
     */
    public java.lang.String getScadaPointID() {
        return scadaPointID;
    }


    /**
     * Sets the scadaPointID value for this ScadaControl.
     * 
     * @param scadaPointID
     */
    public void setScadaPointID(java.lang.String scadaPointID) {
        this.scadaPointID = scadaPointID;
    }


    /**
     * Gets the controlKey value for this ScadaControl.
     * 
     * @return controlKey
     */
    public java.lang.String getControlKey() {
        return controlKey;
    }


    /**
     * Sets the controlKey value for this ScadaControl.
     * 
     * @param controlKey
     */
    public void setControlKey(java.lang.String controlKey) {
        this.controlKey = controlKey;
    }


    /**
     * Gets the function value for this ScadaControl.
     * 
     * @return function
     */
    public com.cannontech.multispeak.deploy.service.ScadaControlFunction getFunction() {
        return function;
    }


    /**
     * Sets the function value for this ScadaControl.
     * 
     * @param function
     */
    public void setFunction(com.cannontech.multispeak.deploy.service.ScadaControlFunction function) {
        this.function = function;
    }


    /**
     * Gets the relayType value for this ScadaControl.
     * 
     * @return relayType
     */
    public com.cannontech.multispeak.deploy.service.ScadaControlRelayType getRelayType() {
        return relayType;
    }


    /**
     * Sets the relayType value for this ScadaControl.
     * 
     * @param relayType
     */
    public void setRelayType(com.cannontech.multispeak.deploy.service.ScadaControlRelayType relayType) {
        this.relayType = relayType;
    }


    /**
     * Gets the controlCode value for this ScadaControl.
     * 
     * @return controlCode
     */
    public com.cannontech.multispeak.deploy.service.ScadaControlControlCode getControlCode() {
        return controlCode;
    }


    /**
     * Sets the controlCode value for this ScadaControl.
     * 
     * @param controlCode
     */
    public void setControlCode(com.cannontech.multispeak.deploy.service.ScadaControlControlCode controlCode) {
        this.controlCode = controlCode;
    }


    /**
     * Gets the onTime value for this ScadaControl.
     * 
     * @return onTime
     */
    public java.lang.Float getOnTime() {
        return onTime;
    }


    /**
     * Sets the onTime value for this ScadaControl.
     * 
     * @param onTime
     */
    public void setOnTime(java.lang.Float onTime) {
        this.onTime = onTime;
    }


    /**
     * Gets the offTime value for this ScadaControl.
     * 
     * @return offTime
     */
    public java.lang.Float getOffTime() {
        return offTime;
    }


    /**
     * Sets the offTime value for this ScadaControl.
     * 
     * @param offTime
     */
    public void setOffTime(java.lang.Float offTime) {
        this.offTime = offTime;
    }


    /**
     * Gets the count value for this ScadaControl.
     * 
     * @return count
     */
    public java.math.BigInteger getCount() {
        return count;
    }


    /**
     * Sets the count value for this ScadaControl.
     * 
     * @param count
     */
    public void setCount(java.math.BigInteger count) {
        this.count = count;
    }


    /**
     * Gets the controlStatus value for this ScadaControl.
     * 
     * @return controlStatus
     */
    public com.cannontech.multispeak.deploy.service.ScadaControlControlStatus getControlStatus() {
        return controlStatus;
    }


    /**
     * Sets the controlStatus value for this ScadaControl.
     * 
     * @param controlStatus
     */
    public void setControlStatus(com.cannontech.multispeak.deploy.service.ScadaControlControlStatus controlStatus) {
        this.controlStatus = controlStatus;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof ScadaControl)) return false;
        ScadaControl other = (ScadaControl) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.scadaPointID==null && other.getScadaPointID()==null) || 
             (this.scadaPointID!=null &&
              this.scadaPointID.equals(other.getScadaPointID()))) &&
            ((this.controlKey==null && other.getControlKey()==null) || 
             (this.controlKey!=null &&
              this.controlKey.equals(other.getControlKey()))) &&
            ((this.function==null && other.getFunction()==null) || 
             (this.function!=null &&
              this.function.equals(other.getFunction()))) &&
            ((this.relayType==null && other.getRelayType()==null) || 
             (this.relayType!=null &&
              this.relayType.equals(other.getRelayType()))) &&
            ((this.controlCode==null && other.getControlCode()==null) || 
             (this.controlCode!=null &&
              this.controlCode.equals(other.getControlCode()))) &&
            ((this.onTime==null && other.getOnTime()==null) || 
             (this.onTime!=null &&
              this.onTime.equals(other.getOnTime()))) &&
            ((this.offTime==null && other.getOffTime()==null) || 
             (this.offTime!=null &&
              this.offTime.equals(other.getOffTime()))) &&
            ((this.count==null && other.getCount()==null) || 
             (this.count!=null &&
              this.count.equals(other.getCount()))) &&
            ((this.controlStatus==null && other.getControlStatus()==null) || 
             (this.controlStatus!=null &&
              this.controlStatus.equals(other.getControlStatus())));
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
        if (getScadaPointID() != null) {
            _hashCode += getScadaPointID().hashCode();
        }
        if (getControlKey() != null) {
            _hashCode += getControlKey().hashCode();
        }
        if (getFunction() != null) {
            _hashCode += getFunction().hashCode();
        }
        if (getRelayType() != null) {
            _hashCode += getRelayType().hashCode();
        }
        if (getControlCode() != null) {
            _hashCode += getControlCode().hashCode();
        }
        if (getOnTime() != null) {
            _hashCode += getOnTime().hashCode();
        }
        if (getOffTime() != null) {
            _hashCode += getOffTime().hashCode();
        }
        if (getCount() != null) {
            _hashCode += getCount().hashCode();
        }
        if (getControlStatus() != null) {
            _hashCode += getControlStatus().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(ScadaControl.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "scadaControl"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("scadaPointID");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "scadaPointID"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("controlKey");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "controlKey"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("function");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "function"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">scadaControl>function"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("relayType");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "relayType"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">scadaControl>relayType"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("controlCode");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "controlCode"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">scadaControl>controlCode"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("onTime");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "onTime"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "float"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("offTime");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "offTime"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "float"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("count");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "count"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "integer"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("controlStatus");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "controlStatus"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">scadaControl>controlStatus"));
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
