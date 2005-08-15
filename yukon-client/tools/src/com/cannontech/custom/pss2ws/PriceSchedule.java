/**
 * PriceSchedule.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.2 May 03, 2005 (02:20:24 EDT) WSDL2Java emitter.
 */

package com.cannontech.custom.pss2ws;

public class PriceSchedule  implements java.io.Serializable {
    private double currentPriceDPKWH;
    private com.cannontech.custom.pss2ws.PriceScheduleEntry[] entries;

    public PriceSchedule() {
    }

    public PriceSchedule(
           double currentPriceDPKWH,
           com.cannontech.custom.pss2ws.PriceScheduleEntry[] entries) {
           this.currentPriceDPKWH = currentPriceDPKWH;
           this.entries = entries;
    }


    /**
     * Gets the currentPriceDPKWH value for this PriceSchedule.
     * 
     * @return currentPriceDPKWH
     */
    public double getCurrentPriceDPKWH() {
        return currentPriceDPKWH;
    }


    /**
     * Sets the currentPriceDPKWH value for this PriceSchedule.
     * 
     * @param currentPriceDPKWH
     */
    public void setCurrentPriceDPKWH(double currentPriceDPKWH) {
        this.currentPriceDPKWH = currentPriceDPKWH;
    }


    /**
     * Gets the entries value for this PriceSchedule.
     * 
     * @return entries
     */
    public com.cannontech.custom.pss2ws.PriceScheduleEntry[] getEntries() {
        return entries;
    }


    /**
     * Sets the entries value for this PriceSchedule.
     * 
     * @param entries
     */
    public void setEntries(com.cannontech.custom.pss2ws.PriceScheduleEntry[] entries) {
        this.entries = entries;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof PriceSchedule)) return false;
        PriceSchedule other = (PriceSchedule) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            this.currentPriceDPKWH == other.getCurrentPriceDPKWH() &&
            ((this.entries==null && other.getEntries()==null) || 
             (this.entries!=null &&
              java.util.Arrays.equals(this.entries, other.getEntries())));
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
        _hashCode += new Double(getCurrentPriceDPKWH()).hashCode();
        if (getEntries() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getEntries());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getEntries(), i);
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
        new org.apache.axis.description.TypeDesc(PriceSchedule.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://pss2.lbl.gov", "PriceSchedule"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("currentPriceDPKWH");
        elemField.setXmlName(new javax.xml.namespace.QName("", "currentPriceDPKWH"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "double"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("entries");
        elemField.setXmlName(new javax.xml.namespace.QName("", "entries"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://pss2.lbl.gov", "PriceScheduleEntry"));
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
