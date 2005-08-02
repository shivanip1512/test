/**
 * ArrayOfWarehouseLocation.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.2 May 03, 2005 (02:20:24 EDT) WSDL2Java emitter.
 */

package com.cannontech.multispeak;

public class ArrayOfWarehouseLocation  implements java.io.Serializable {
    private com.cannontech.multispeak.WarehouseLocation[] warehouseLocation;

    public ArrayOfWarehouseLocation() {
    }

    public ArrayOfWarehouseLocation(
           com.cannontech.multispeak.WarehouseLocation[] warehouseLocation) {
           this.warehouseLocation = warehouseLocation;
    }


    /**
     * Gets the warehouseLocation value for this ArrayOfWarehouseLocation.
     * 
     * @return warehouseLocation
     */
    public com.cannontech.multispeak.WarehouseLocation[] getWarehouseLocation() {
        return warehouseLocation;
    }


    /**
     * Sets the warehouseLocation value for this ArrayOfWarehouseLocation.
     * 
     * @param warehouseLocation
     */
    public void setWarehouseLocation(com.cannontech.multispeak.WarehouseLocation[] warehouseLocation) {
        this.warehouseLocation = warehouseLocation;
    }

    public com.cannontech.multispeak.WarehouseLocation getWarehouseLocation(int i) {
        return this.warehouseLocation[i];
    }

    public void setWarehouseLocation(int i, com.cannontech.multispeak.WarehouseLocation _value) {
        this.warehouseLocation[i] = _value;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof ArrayOfWarehouseLocation)) return false;
        ArrayOfWarehouseLocation other = (ArrayOfWarehouseLocation) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.warehouseLocation==null && other.getWarehouseLocation()==null) || 
             (this.warehouseLocation!=null &&
              java.util.Arrays.equals(this.warehouseLocation, other.getWarehouseLocation())));
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
        if (getWarehouseLocation() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getWarehouseLocation());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getWarehouseLocation(), i);
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
        new org.apache.axis.description.TypeDesc(ArrayOfWarehouseLocation.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ArrayOfWarehouseLocation"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("warehouseLocation");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "warehouseLocation"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "warehouseLocation"));
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
