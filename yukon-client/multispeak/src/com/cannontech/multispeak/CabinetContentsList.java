/**
 * CabinetContentsList.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.2 May 03, 2005 (02:20:24 EDT) WSDL2Java emitter.
 */

package com.cannontech.multispeak;

public class CabinetContentsList  implements java.io.Serializable {
    private com.cannontech.multispeak.ObjectRef[] cabinetContentsItem;

    public CabinetContentsList() {
    }

    public CabinetContentsList(
           com.cannontech.multispeak.ObjectRef[] cabinetContentsItem) {
           this.cabinetContentsItem = cabinetContentsItem;
    }


    /**
     * Gets the cabinetContentsItem value for this CabinetContentsList.
     * 
     * @return cabinetContentsItem
     */
    public com.cannontech.multispeak.ObjectRef[] getCabinetContentsItem() {
        return cabinetContentsItem;
    }


    /**
     * Sets the cabinetContentsItem value for this CabinetContentsList.
     * 
     * @param cabinetContentsItem
     */
    public void setCabinetContentsItem(com.cannontech.multispeak.ObjectRef[] cabinetContentsItem) {
        this.cabinetContentsItem = cabinetContentsItem;
    }

    public com.cannontech.multispeak.ObjectRef getCabinetContentsItem(int i) {
        return this.cabinetContentsItem[i];
    }

    public void setCabinetContentsItem(int i, com.cannontech.multispeak.ObjectRef _value) {
        this.cabinetContentsItem[i] = _value;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof CabinetContentsList)) return false;
        CabinetContentsList other = (CabinetContentsList) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.cabinetContentsItem==null && other.getCabinetContentsItem()==null) || 
             (this.cabinetContentsItem!=null &&
              java.util.Arrays.equals(this.cabinetContentsItem, other.getCabinetContentsItem())));
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
        if (getCabinetContentsItem() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getCabinetContentsItem());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getCabinetContentsItem(), i);
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
        new org.apache.axis.description.TypeDesc(CabinetContentsList.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "cabinetContentsList"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("cabinetContentsItem");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "cabinetContentsItem"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "objectRef"));
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
