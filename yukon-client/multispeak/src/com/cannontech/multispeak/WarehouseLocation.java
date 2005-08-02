/**
 * WarehouseLocation.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.2 May 03, 2005 (02:20:24 EDT) WSDL2Java emitter.
 */

package com.cannontech.multispeak;

public class WarehouseLocation  implements java.io.Serializable {
    private java.lang.String warehouseID;
    private java.lang.String aisle;
    private java.lang.String bin;

    public WarehouseLocation() {
    }

    public WarehouseLocation(
           java.lang.String warehouseID,
           java.lang.String aisle,
           java.lang.String bin) {
           this.warehouseID = warehouseID;
           this.aisle = aisle;
           this.bin = bin;
    }


    /**
     * Gets the warehouseID value for this WarehouseLocation.
     * 
     * @return warehouseID
     */
    public java.lang.String getWarehouseID() {
        return warehouseID;
    }


    /**
     * Sets the warehouseID value for this WarehouseLocation.
     * 
     * @param warehouseID
     */
    public void setWarehouseID(java.lang.String warehouseID) {
        this.warehouseID = warehouseID;
    }


    /**
     * Gets the aisle value for this WarehouseLocation.
     * 
     * @return aisle
     */
    public java.lang.String getAisle() {
        return aisle;
    }


    /**
     * Sets the aisle value for this WarehouseLocation.
     * 
     * @param aisle
     */
    public void setAisle(java.lang.String aisle) {
        this.aisle = aisle;
    }


    /**
     * Gets the bin value for this WarehouseLocation.
     * 
     * @return bin
     */
    public java.lang.String getBin() {
        return bin;
    }


    /**
     * Sets the bin value for this WarehouseLocation.
     * 
     * @param bin
     */
    public void setBin(java.lang.String bin) {
        this.bin = bin;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof WarehouseLocation)) return false;
        WarehouseLocation other = (WarehouseLocation) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.warehouseID==null && other.getWarehouseID()==null) || 
             (this.warehouseID!=null &&
              this.warehouseID.equals(other.getWarehouseID()))) &&
            ((this.aisle==null && other.getAisle()==null) || 
             (this.aisle!=null &&
              this.aisle.equals(other.getAisle()))) &&
            ((this.bin==null && other.getBin()==null) || 
             (this.bin!=null &&
              this.bin.equals(other.getBin())));
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
        if (getWarehouseID() != null) {
            _hashCode += getWarehouseID().hashCode();
        }
        if (getAisle() != null) {
            _hashCode += getAisle().hashCode();
        }
        if (getBin() != null) {
            _hashCode += getBin().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(WarehouseLocation.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "warehouseLocation"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("warehouseID");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "warehouseID"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("aisle");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "aisle"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("bin");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "bin"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
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
