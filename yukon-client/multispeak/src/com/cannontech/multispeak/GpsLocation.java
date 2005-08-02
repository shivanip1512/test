/**
 * GpsLocation.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.2 May 03, 2005 (02:20:24 EDT) WSDL2Java emitter.
 */

package com.cannontech.multispeak;

public class GpsLocation  extends com.cannontech.multispeak.MspObject  implements java.io.Serializable {
    private java.lang.Float altitude;
    private java.lang.Double latitude;
    private java.lang.Double longitude;
    private java.lang.Float easting;
    private java.lang.Float northing;
    private java.lang.String source;
    private java.lang.Long number;
    private java.lang.Boolean isRealTimeDiffCorrection;
    private java.lang.Float hdop;
    private java.lang.Float vdop;
    private java.lang.String diffID;
    private java.util.Calendar collected;
    private java.lang.Long numSat;

    public GpsLocation() {
    }

    public GpsLocation(
           java.lang.Float altitude,
           java.lang.Double latitude,
           java.lang.Double longitude,
           java.lang.Float easting,
           java.lang.Float northing,
           java.lang.String source,
           java.lang.Long number,
           java.lang.Boolean isRealTimeDiffCorrection,
           java.lang.Float hdop,
           java.lang.Float vdop,
           java.lang.String diffID,
           java.util.Calendar collected,
           java.lang.Long numSat) {
           this.altitude = altitude;
           this.latitude = latitude;
           this.longitude = longitude;
           this.easting = easting;
           this.northing = northing;
           this.source = source;
           this.number = number;
           this.isRealTimeDiffCorrection = isRealTimeDiffCorrection;
           this.hdop = hdop;
           this.vdop = vdop;
           this.diffID = diffID;
           this.collected = collected;
           this.numSat = numSat;
    }


    /**
     * Gets the altitude value for this GpsLocation.
     * 
     * @return altitude
     */
    public java.lang.Float getAltitude() {
        return altitude;
    }


    /**
     * Sets the altitude value for this GpsLocation.
     * 
     * @param altitude
     */
    public void setAltitude(java.lang.Float altitude) {
        this.altitude = altitude;
    }


    /**
     * Gets the latitude value for this GpsLocation.
     * 
     * @return latitude
     */
    public java.lang.Double getLatitude() {
        return latitude;
    }


    /**
     * Sets the latitude value for this GpsLocation.
     * 
     * @param latitude
     */
    public void setLatitude(java.lang.Double latitude) {
        this.latitude = latitude;
    }


    /**
     * Gets the longitude value for this GpsLocation.
     * 
     * @return longitude
     */
    public java.lang.Double getLongitude() {
        return longitude;
    }


    /**
     * Sets the longitude value for this GpsLocation.
     * 
     * @param longitude
     */
    public void setLongitude(java.lang.Double longitude) {
        this.longitude = longitude;
    }


    /**
     * Gets the easting value for this GpsLocation.
     * 
     * @return easting
     */
    public java.lang.Float getEasting() {
        return easting;
    }


    /**
     * Sets the easting value for this GpsLocation.
     * 
     * @param easting
     */
    public void setEasting(java.lang.Float easting) {
        this.easting = easting;
    }


    /**
     * Gets the northing value for this GpsLocation.
     * 
     * @return northing
     */
    public java.lang.Float getNorthing() {
        return northing;
    }


    /**
     * Sets the northing value for this GpsLocation.
     * 
     * @param northing
     */
    public void setNorthing(java.lang.Float northing) {
        this.northing = northing;
    }


    /**
     * Gets the source value for this GpsLocation.
     * 
     * @return source
     */
    public java.lang.String getSource() {
        return source;
    }


    /**
     * Sets the source value for this GpsLocation.
     * 
     * @param source
     */
    public void setSource(java.lang.String source) {
        this.source = source;
    }


    /**
     * Gets the number value for this GpsLocation.
     * 
     * @return number
     */
    public java.lang.Long getNumber() {
        return number;
    }


    /**
     * Sets the number value for this GpsLocation.
     * 
     * @param number
     */
    public void setNumber(java.lang.Long number) {
        this.number = number;
    }


    /**
     * Gets the isRealTimeDiffCorrection value for this GpsLocation.
     * 
     * @return isRealTimeDiffCorrection
     */
    public java.lang.Boolean getIsRealTimeDiffCorrection() {
        return isRealTimeDiffCorrection;
    }


    /**
     * Sets the isRealTimeDiffCorrection value for this GpsLocation.
     * 
     * @param isRealTimeDiffCorrection
     */
    public void setIsRealTimeDiffCorrection(java.lang.Boolean isRealTimeDiffCorrection) {
        this.isRealTimeDiffCorrection = isRealTimeDiffCorrection;
    }


    /**
     * Gets the hdop value for this GpsLocation.
     * 
     * @return hdop
     */
    public java.lang.Float getHdop() {
        return hdop;
    }


    /**
     * Sets the hdop value for this GpsLocation.
     * 
     * @param hdop
     */
    public void setHdop(java.lang.Float hdop) {
        this.hdop = hdop;
    }


    /**
     * Gets the vdop value for this GpsLocation.
     * 
     * @return vdop
     */
    public java.lang.Float getVdop() {
        return vdop;
    }


    /**
     * Sets the vdop value for this GpsLocation.
     * 
     * @param vdop
     */
    public void setVdop(java.lang.Float vdop) {
        this.vdop = vdop;
    }


    /**
     * Gets the diffID value for this GpsLocation.
     * 
     * @return diffID
     */
    public java.lang.String getDiffID() {
        return diffID;
    }


    /**
     * Sets the diffID value for this GpsLocation.
     * 
     * @param diffID
     */
    public void setDiffID(java.lang.String diffID) {
        this.diffID = diffID;
    }


    /**
     * Gets the collected value for this GpsLocation.
     * 
     * @return collected
     */
    public java.util.Calendar getCollected() {
        return collected;
    }


    /**
     * Sets the collected value for this GpsLocation.
     * 
     * @param collected
     */
    public void setCollected(java.util.Calendar collected) {
        this.collected = collected;
    }


    /**
     * Gets the numSat value for this GpsLocation.
     * 
     * @return numSat
     */
    public java.lang.Long getNumSat() {
        return numSat;
    }


    /**
     * Sets the numSat value for this GpsLocation.
     * 
     * @param numSat
     */
    public void setNumSat(java.lang.Long numSat) {
        this.numSat = numSat;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof GpsLocation)) return false;
        GpsLocation other = (GpsLocation) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = super.equals(obj) && 
            ((this.altitude==null && other.getAltitude()==null) || 
             (this.altitude!=null &&
              this.altitude.equals(other.getAltitude()))) &&
            ((this.latitude==null && other.getLatitude()==null) || 
             (this.latitude!=null &&
              this.latitude.equals(other.getLatitude()))) &&
            ((this.longitude==null && other.getLongitude()==null) || 
             (this.longitude!=null &&
              this.longitude.equals(other.getLongitude()))) &&
            ((this.easting==null && other.getEasting()==null) || 
             (this.easting!=null &&
              this.easting.equals(other.getEasting()))) &&
            ((this.northing==null && other.getNorthing()==null) || 
             (this.northing!=null &&
              this.northing.equals(other.getNorthing()))) &&
            ((this.source==null && other.getSource()==null) || 
             (this.source!=null &&
              this.source.equals(other.getSource()))) &&
            ((this.number==null && other.getNumber()==null) || 
             (this.number!=null &&
              this.number.equals(other.getNumber()))) &&
            ((this.isRealTimeDiffCorrection==null && other.getIsRealTimeDiffCorrection()==null) || 
             (this.isRealTimeDiffCorrection!=null &&
              this.isRealTimeDiffCorrection.equals(other.getIsRealTimeDiffCorrection()))) &&
            ((this.hdop==null && other.getHdop()==null) || 
             (this.hdop!=null &&
              this.hdop.equals(other.getHdop()))) &&
            ((this.vdop==null && other.getVdop()==null) || 
             (this.vdop!=null &&
              this.vdop.equals(other.getVdop()))) &&
            ((this.diffID==null && other.getDiffID()==null) || 
             (this.diffID!=null &&
              this.diffID.equals(other.getDiffID()))) &&
            ((this.collected==null && other.getCollected()==null) || 
             (this.collected!=null &&
              this.collected.equals(other.getCollected()))) &&
            ((this.numSat==null && other.getNumSat()==null) || 
             (this.numSat!=null &&
              this.numSat.equals(other.getNumSat())));
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
        if (getAltitude() != null) {
            _hashCode += getAltitude().hashCode();
        }
        if (getLatitude() != null) {
            _hashCode += getLatitude().hashCode();
        }
        if (getLongitude() != null) {
            _hashCode += getLongitude().hashCode();
        }
        if (getEasting() != null) {
            _hashCode += getEasting().hashCode();
        }
        if (getNorthing() != null) {
            _hashCode += getNorthing().hashCode();
        }
        if (getSource() != null) {
            _hashCode += getSource().hashCode();
        }
        if (getNumber() != null) {
            _hashCode += getNumber().hashCode();
        }
        if (getIsRealTimeDiffCorrection() != null) {
            _hashCode += getIsRealTimeDiffCorrection().hashCode();
        }
        if (getHdop() != null) {
            _hashCode += getHdop().hashCode();
        }
        if (getVdop() != null) {
            _hashCode += getVdop().hashCode();
        }
        if (getDiffID() != null) {
            _hashCode += getDiffID().hashCode();
        }
        if (getCollected() != null) {
            _hashCode += getCollected().hashCode();
        }
        if (getNumSat() != null) {
            _hashCode += getNumSat().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(GpsLocation.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "gpsLocation"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("altitude");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "altitude"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "float"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("latitude");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "latitude"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "double"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("longitude");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "longitude"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "double"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("easting");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "easting"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "float"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("northing");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "northing"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "float"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("source");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "source"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("number");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "number"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "long"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("isRealTimeDiffCorrection");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "isRealTimeDiffCorrection"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "boolean"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("hdop");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "hdop"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "float"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("vdop");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "vdop"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "float"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("diffID");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "diffID"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("collected");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "collected"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "dateTime"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("numSat");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "numSat"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "long"));
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
