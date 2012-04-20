/**
 * GpsPoint.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Dec 28, 2007 (09:53:13 CST) WSDL2Java emitter.
 */

package com.cannontech.multispeak.deploy.service;

public class GpsPoint  implements java.io.Serializable {
    private double latitude;

    private double longitude;

    private java.lang.Float altitude;

    public GpsPoint() {
    }

    public GpsPoint(
           double latitude,
           double longitude,
           java.lang.Float altitude) {
           this.latitude = latitude;
           this.longitude = longitude;
           this.altitude = altitude;
    }


    /**
     * Gets the latitude value for this GpsPoint.
     * 
     * @return latitude
     */
    public double getLatitude() {
        return latitude;
    }


    /**
     * Sets the latitude value for this GpsPoint.
     * 
     * @param latitude
     */
    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }


    /**
     * Gets the longitude value for this GpsPoint.
     * 
     * @return longitude
     */
    public double getLongitude() {
        return longitude;
    }


    /**
     * Sets the longitude value for this GpsPoint.
     * 
     * @param longitude
     */
    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }


    /**
     * Gets the altitude value for this GpsPoint.
     * 
     * @return altitude
     */
    public java.lang.Float getAltitude() {
        return altitude;
    }


    /**
     * Sets the altitude value for this GpsPoint.
     * 
     * @param altitude
     */
    public void setAltitude(java.lang.Float altitude) {
        this.altitude = altitude;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof GpsPoint)) return false;
        GpsPoint other = (GpsPoint) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            this.latitude == other.getLatitude() &&
            this.longitude == other.getLongitude() &&
            ((this.altitude==null && other.getAltitude()==null) || 
             (this.altitude!=null &&
              this.altitude.equals(other.getAltitude())));
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
        _hashCode += new Double(getLatitude()).hashCode();
        _hashCode += new Double(getLongitude()).hashCode();
        if (getAltitude() != null) {
            _hashCode += getAltitude().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(GpsPoint.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "gpsPoint"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("latitude");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "latitude"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "double"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("longitude");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "longitude"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "double"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("altitude");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "altitude"));
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
