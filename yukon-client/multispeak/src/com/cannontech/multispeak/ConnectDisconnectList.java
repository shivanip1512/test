/**
 * ConnectDisconnectList.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.2 May 03, 2005 (02:20:24 EDT) WSDL2Java emitter.
 */

package com.cannontech.multispeak;

public class ConnectDisconnectList  extends com.cannontech.multispeak.MspObject  implements java.io.Serializable {
    private java.util.Calendar disconnectDate;
    private com.cannontech.multispeak.ArrayOfCDCustomer CDList;

    public ConnectDisconnectList() {
    }

    public ConnectDisconnectList(
           java.util.Calendar disconnectDate,
           com.cannontech.multispeak.ArrayOfCDCustomer CDList) {
           this.disconnectDate = disconnectDate;
           this.CDList = CDList;
    }


    /**
     * Gets the disconnectDate value for this ConnectDisconnectList.
     * 
     * @return disconnectDate
     */
    public java.util.Calendar getDisconnectDate() {
        return disconnectDate;
    }


    /**
     * Sets the disconnectDate value for this ConnectDisconnectList.
     * 
     * @param disconnectDate
     */
    public void setDisconnectDate(java.util.Calendar disconnectDate) {
        this.disconnectDate = disconnectDate;
    }


    /**
     * Gets the CDList value for this ConnectDisconnectList.
     * 
     * @return CDList
     */
    public com.cannontech.multispeak.ArrayOfCDCustomer getCDList() {
        return CDList;
    }


    /**
     * Sets the CDList value for this ConnectDisconnectList.
     * 
     * @param CDList
     */
    public void setCDList(com.cannontech.multispeak.ArrayOfCDCustomer CDList) {
        this.CDList = CDList;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof ConnectDisconnectList)) return false;
        ConnectDisconnectList other = (ConnectDisconnectList) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = super.equals(obj) && 
            ((this.disconnectDate==null && other.getDisconnectDate()==null) || 
             (this.disconnectDate!=null &&
              this.disconnectDate.equals(other.getDisconnectDate()))) &&
            ((this.CDList==null && other.getCDList()==null) || 
             (this.CDList!=null &&
              this.CDList.equals(other.getCDList())));
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
        if (getDisconnectDate() != null) {
            _hashCode += getDisconnectDate().hashCode();
        }
        if (getCDList() != null) {
            _hashCode += getCDList().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(ConnectDisconnectList.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "connectDisconnectList"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("disconnectDate");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "disconnectDate"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "dateTime"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("CDList");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "CDList"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ArrayOfCDCustomer"));
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
