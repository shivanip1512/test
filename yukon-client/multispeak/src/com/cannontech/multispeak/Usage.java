/**
 * Usage.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.2 May 03, 2005 (02:20:24 EDT) WSDL2Java emitter.
 */

package com.cannontech.multispeak;

public class Usage  extends com.cannontech.multispeak.MspObject  implements java.io.Serializable {
    private java.lang.String servLoc;
    private java.lang.String custID;
    private java.lang.String gridLocation;
    private java.lang.String accountNumber;
    private java.lang.String meterID;
    private com.cannontech.multispeak.ArrayOfUsageInstance usageList;

    public Usage() {
    }

    public Usage(
           java.lang.String servLoc,
           java.lang.String custID,
           java.lang.String gridLocation,
           java.lang.String accountNumber,
           java.lang.String meterID,
           com.cannontech.multispeak.ArrayOfUsageInstance usageList) {
           this.servLoc = servLoc;
           this.custID = custID;
           this.gridLocation = gridLocation;
           this.accountNumber = accountNumber;
           this.meterID = meterID;
           this.usageList = usageList;
    }


    /**
     * Gets the servLoc value for this Usage.
     * 
     * @return servLoc
     */
    public java.lang.String getServLoc() {
        return servLoc;
    }


    /**
     * Sets the servLoc value for this Usage.
     * 
     * @param servLoc
     */
    public void setServLoc(java.lang.String servLoc) {
        this.servLoc = servLoc;
    }


    /**
     * Gets the custID value for this Usage.
     * 
     * @return custID
     */
    public java.lang.String getCustID() {
        return custID;
    }


    /**
     * Sets the custID value for this Usage.
     * 
     * @param custID
     */
    public void setCustID(java.lang.String custID) {
        this.custID = custID;
    }


    /**
     * Gets the gridLocation value for this Usage.
     * 
     * @return gridLocation
     */
    public java.lang.String getGridLocation() {
        return gridLocation;
    }


    /**
     * Sets the gridLocation value for this Usage.
     * 
     * @param gridLocation
     */
    public void setGridLocation(java.lang.String gridLocation) {
        this.gridLocation = gridLocation;
    }


    /**
     * Gets the accountNumber value for this Usage.
     * 
     * @return accountNumber
     */
    public java.lang.String getAccountNumber() {
        return accountNumber;
    }


    /**
     * Sets the accountNumber value for this Usage.
     * 
     * @param accountNumber
     */
    public void setAccountNumber(java.lang.String accountNumber) {
        this.accountNumber = accountNumber;
    }


    /**
     * Gets the meterID value for this Usage.
     * 
     * @return meterID
     */
    public java.lang.String getMeterID() {
        return meterID;
    }


    /**
     * Sets the meterID value for this Usage.
     * 
     * @param meterID
     */
    public void setMeterID(java.lang.String meterID) {
        this.meterID = meterID;
    }


    /**
     * Gets the usageList value for this Usage.
     * 
     * @return usageList
     */
    public com.cannontech.multispeak.ArrayOfUsageInstance getUsageList() {
        return usageList;
    }


    /**
     * Sets the usageList value for this Usage.
     * 
     * @param usageList
     */
    public void setUsageList(com.cannontech.multispeak.ArrayOfUsageInstance usageList) {
        this.usageList = usageList;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof Usage)) return false;
        Usage other = (Usage) obj;
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
            ((this.usageList==null && other.getUsageList()==null) || 
             (this.usageList!=null &&
              this.usageList.equals(other.getUsageList())));
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
        if (getUsageList() != null) {
            _hashCode += getUsageList().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(Usage.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "usage"));
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
        elemField.setFieldName("usageList");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "usageList"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ArrayOfUsageInstance"));
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
