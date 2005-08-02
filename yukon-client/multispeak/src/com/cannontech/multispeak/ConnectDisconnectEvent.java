/**
 * ConnectDisconnectEvent.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.2 May 03, 2005 (02:20:24 EDT) WSDL2Java emitter.
 */

package com.cannontech.multispeak;

public class ConnectDisconnectEvent  extends com.cannontech.multispeak.MspObject  implements java.io.Serializable {
    private java.lang.String servLoc;
    private java.lang.String custID;
    private java.lang.String gridLocation;
    private java.lang.String accountNumber;
    private java.lang.String meterID;
    private com.cannontech.multispeak.LoadActionCode loadActionCode;
    private java.lang.Float powerLimitationValue;
    private com.cannontech.multispeak.PowerLimitationUnits powerLimitationUnits;
    private com.cannontech.multispeak.ReasonCode reasonCode;
    private java.lang.Float amountDue;

    public ConnectDisconnectEvent() {
    }

    public ConnectDisconnectEvent(
           java.lang.String servLoc,
           java.lang.String custID,
           java.lang.String gridLocation,
           java.lang.String accountNumber,
           java.lang.String meterID,
           com.cannontech.multispeak.LoadActionCode loadActionCode,
           java.lang.Float powerLimitationValue,
           com.cannontech.multispeak.PowerLimitationUnits powerLimitationUnits,
           com.cannontech.multispeak.ReasonCode reasonCode,
           java.lang.Float amountDue) {
           this.servLoc = servLoc;
           this.custID = custID;
           this.gridLocation = gridLocation;
           this.accountNumber = accountNumber;
           this.meterID = meterID;
           this.loadActionCode = loadActionCode;
           this.powerLimitationValue = powerLimitationValue;
           this.powerLimitationUnits = powerLimitationUnits;
           this.reasonCode = reasonCode;
           this.amountDue = amountDue;
    }


    /**
     * Gets the servLoc value for this ConnectDisconnectEvent.
     * 
     * @return servLoc
     */
    public java.lang.String getServLoc() {
        return servLoc;
    }


    /**
     * Sets the servLoc value for this ConnectDisconnectEvent.
     * 
     * @param servLoc
     */
    public void setServLoc(java.lang.String servLoc) {
        this.servLoc = servLoc;
    }


    /**
     * Gets the custID value for this ConnectDisconnectEvent.
     * 
     * @return custID
     */
    public java.lang.String getCustID() {
        return custID;
    }


    /**
     * Sets the custID value for this ConnectDisconnectEvent.
     * 
     * @param custID
     */
    public void setCustID(java.lang.String custID) {
        this.custID = custID;
    }


    /**
     * Gets the gridLocation value for this ConnectDisconnectEvent.
     * 
     * @return gridLocation
     */
    public java.lang.String getGridLocation() {
        return gridLocation;
    }


    /**
     * Sets the gridLocation value for this ConnectDisconnectEvent.
     * 
     * @param gridLocation
     */
    public void setGridLocation(java.lang.String gridLocation) {
        this.gridLocation = gridLocation;
    }


    /**
     * Gets the accountNumber value for this ConnectDisconnectEvent.
     * 
     * @return accountNumber
     */
    public java.lang.String getAccountNumber() {
        return accountNumber;
    }


    /**
     * Sets the accountNumber value for this ConnectDisconnectEvent.
     * 
     * @param accountNumber
     */
    public void setAccountNumber(java.lang.String accountNumber) {
        this.accountNumber = accountNumber;
    }


    /**
     * Gets the meterID value for this ConnectDisconnectEvent.
     * 
     * @return meterID
     */
    public java.lang.String getMeterID() {
        return meterID;
    }


    /**
     * Sets the meterID value for this ConnectDisconnectEvent.
     * 
     * @param meterID
     */
    public void setMeterID(java.lang.String meterID) {
        this.meterID = meterID;
    }


    /**
     * Gets the loadActionCode value for this ConnectDisconnectEvent.
     * 
     * @return loadActionCode
     */
    public com.cannontech.multispeak.LoadActionCode getLoadActionCode() {
        return loadActionCode;
    }


    /**
     * Sets the loadActionCode value for this ConnectDisconnectEvent.
     * 
     * @param loadActionCode
     */
    public void setLoadActionCode(com.cannontech.multispeak.LoadActionCode loadActionCode) {
        this.loadActionCode = loadActionCode;
    }


    /**
     * Gets the powerLimitationValue value for this ConnectDisconnectEvent.
     * 
     * @return powerLimitationValue
     */
    public java.lang.Float getPowerLimitationValue() {
        return powerLimitationValue;
    }


    /**
     * Sets the powerLimitationValue value for this ConnectDisconnectEvent.
     * 
     * @param powerLimitationValue
     */
    public void setPowerLimitationValue(java.lang.Float powerLimitationValue) {
        this.powerLimitationValue = powerLimitationValue;
    }


    /**
     * Gets the powerLimitationUnits value for this ConnectDisconnectEvent.
     * 
     * @return powerLimitationUnits
     */
    public com.cannontech.multispeak.PowerLimitationUnits getPowerLimitationUnits() {
        return powerLimitationUnits;
    }


    /**
     * Sets the powerLimitationUnits value for this ConnectDisconnectEvent.
     * 
     * @param powerLimitationUnits
     */
    public void setPowerLimitationUnits(com.cannontech.multispeak.PowerLimitationUnits powerLimitationUnits) {
        this.powerLimitationUnits = powerLimitationUnits;
    }


    /**
     * Gets the reasonCode value for this ConnectDisconnectEvent.
     * 
     * @return reasonCode
     */
    public com.cannontech.multispeak.ReasonCode getReasonCode() {
        return reasonCode;
    }


    /**
     * Sets the reasonCode value for this ConnectDisconnectEvent.
     * 
     * @param reasonCode
     */
    public void setReasonCode(com.cannontech.multispeak.ReasonCode reasonCode) {
        this.reasonCode = reasonCode;
    }


    /**
     * Gets the amountDue value for this ConnectDisconnectEvent.
     * 
     * @return amountDue
     */
    public java.lang.Float getAmountDue() {
        return amountDue;
    }


    /**
     * Sets the amountDue value for this ConnectDisconnectEvent.
     * 
     * @param amountDue
     */
    public void setAmountDue(java.lang.Float amountDue) {
        this.amountDue = amountDue;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof ConnectDisconnectEvent)) return false;
        ConnectDisconnectEvent other = (ConnectDisconnectEvent) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = super.equals(obj) && 
            ((this.servLoc==null && other.getServLoc()==null) || 
             (this.servLoc!=null &&
              this.servLoc.equals(other.getServLoc()))) &&
            ((this.custID==null && other.getCustID()==null) || 
             (this.custID!=null &&
              this.custID.equals(other.getCustID()))) &&
            ((this.gridLocation==null && other.getGridLocation()==null) || 
             (this.gridLocation!=null &&
              this.gridLocation.equals(other.getGridLocation()))) &&
            ((this.accountNumber==null && other.getAccountNumber()==null) || 
             (this.accountNumber!=null &&
              this.accountNumber.equals(other.getAccountNumber()))) &&
            ((this.meterID==null && other.getMeterID()==null) || 
             (this.meterID!=null &&
              this.meterID.equals(other.getMeterID()))) &&
            ((this.loadActionCode==null && other.getLoadActionCode()==null) || 
             (this.loadActionCode!=null &&
              this.loadActionCode.equals(other.getLoadActionCode()))) &&
            ((this.powerLimitationValue==null && other.getPowerLimitationValue()==null) || 
             (this.powerLimitationValue!=null &&
              this.powerLimitationValue.equals(other.getPowerLimitationValue()))) &&
            ((this.powerLimitationUnits==null && other.getPowerLimitationUnits()==null) || 
             (this.powerLimitationUnits!=null &&
              this.powerLimitationUnits.equals(other.getPowerLimitationUnits()))) &&
            ((this.reasonCode==null && other.getReasonCode()==null) || 
             (this.reasonCode!=null &&
              this.reasonCode.equals(other.getReasonCode()))) &&
            ((this.amountDue==null && other.getAmountDue()==null) || 
             (this.amountDue!=null &&
              this.amountDue.equals(other.getAmountDue())));
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
        if (getServLoc() != null) {
            _hashCode += getServLoc().hashCode();
        }
        if (getCustID() != null) {
            _hashCode += getCustID().hashCode();
        }
        if (getGridLocation() != null) {
            _hashCode += getGridLocation().hashCode();
        }
        if (getAccountNumber() != null) {
            _hashCode += getAccountNumber().hashCode();
        }
        if (getMeterID() != null) {
            _hashCode += getMeterID().hashCode();
        }
        if (getLoadActionCode() != null) {
            _hashCode += getLoadActionCode().hashCode();
        }
        if (getPowerLimitationValue() != null) {
            _hashCode += getPowerLimitationValue().hashCode();
        }
        if (getPowerLimitationUnits() != null) {
            _hashCode += getPowerLimitationUnits().hashCode();
        }
        if (getReasonCode() != null) {
            _hashCode += getReasonCode().hashCode();
        }
        if (getAmountDue() != null) {
            _hashCode += getAmountDue().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(ConnectDisconnectEvent.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "connectDisconnectEvent"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("servLoc");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "servLoc"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("custID");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "custID"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("gridLocation");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "gridLocation"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("accountNumber");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "accountNumber"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("meterID");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "meterID"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("loadActionCode");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "loadActionCode"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "loadActionCode"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("powerLimitationValue");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "powerLimitationValue"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "float"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("powerLimitationUnits");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "powerLimitationUnits"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "powerLimitationUnits"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("reasonCode");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "reasonCode"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "reasonCode"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("amountDue");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "amountDue"));
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
