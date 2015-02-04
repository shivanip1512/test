/**
 * AsFound.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Dec 28, 2007 (09:53:13 CST) WSDL2Java emitter.
 */

package com.cannontech.multispeak.deploy.service;

public class AsFound  implements java.io.Serializable {
    private java.math.BigInteger kWhReading;

    private java.lang.Float kWReading;

    private java.lang.Float kVArReading;

    private java.lang.Float powerFactorReading;

    private java.lang.Float resultHigh;

    private java.lang.Float resultLow;

    private java.lang.Float resultLL;

    private java.lang.Float kWResult;

    private java.lang.Float kVArResult;

    public AsFound() {
    }

    public AsFound(
           java.math.BigInteger kWhReading,
           java.lang.Float kWReading,
           java.lang.Float kVArReading,
           java.lang.Float powerFactorReading,
           java.lang.Float resultHigh,
           java.lang.Float resultLow,
           java.lang.Float resultLL,
           java.lang.Float kWResult,
           java.lang.Float kVArResult) {
           this.kWhReading = kWhReading;
           this.kWReading = kWReading;
           this.kVArReading = kVArReading;
           this.powerFactorReading = powerFactorReading;
           this.resultHigh = resultHigh;
           this.resultLow = resultLow;
           this.resultLL = resultLL;
           this.kWResult = kWResult;
           this.kVArResult = kVArResult;
    }


    /**
     * Gets the kWhReading value for this AsFound.
     * 
     * @return kWhReading
     */
    public java.math.BigInteger getKWhReading() {
        return kWhReading;
    }


    /**
     * Sets the kWhReading value for this AsFound.
     * 
     * @param kWhReading
     */
    public void setKWhReading(java.math.BigInteger kWhReading) {
        this.kWhReading = kWhReading;
    }


    /**
     * Gets the kWReading value for this AsFound.
     * 
     * @return kWReading
     */
    public java.lang.Float getKWReading() {
        return kWReading;
    }


    /**
     * Sets the kWReading value for this AsFound.
     * 
     * @param kWReading
     */
    public void setKWReading(java.lang.Float kWReading) {
        this.kWReading = kWReading;
    }


    /**
     * Gets the kVArReading value for this AsFound.
     * 
     * @return kVArReading
     */
    public java.lang.Float getKVArReading() {
        return kVArReading;
    }


    /**
     * Sets the kVArReading value for this AsFound.
     * 
     * @param kVArReading
     */
    public void setKVArReading(java.lang.Float kVArReading) {
        this.kVArReading = kVArReading;
    }


    /**
     * Gets the powerFactorReading value for this AsFound.
     * 
     * @return powerFactorReading
     */
    public java.lang.Float getPowerFactorReading() {
        return powerFactorReading;
    }


    /**
     * Sets the powerFactorReading value for this AsFound.
     * 
     * @param powerFactorReading
     */
    public void setPowerFactorReading(java.lang.Float powerFactorReading) {
        this.powerFactorReading = powerFactorReading;
    }


    /**
     * Gets the resultHigh value for this AsFound.
     * 
     * @return resultHigh
     */
    public java.lang.Float getResultHigh() {
        return resultHigh;
    }


    /**
     * Sets the resultHigh value for this AsFound.
     * 
     * @param resultHigh
     */
    public void setResultHigh(java.lang.Float resultHigh) {
        this.resultHigh = resultHigh;
    }


    /**
     * Gets the resultLow value for this AsFound.
     * 
     * @return resultLow
     */
    public java.lang.Float getResultLow() {
        return resultLow;
    }


    /**
     * Sets the resultLow value for this AsFound.
     * 
     * @param resultLow
     */
    public void setResultLow(java.lang.Float resultLow) {
        this.resultLow = resultLow;
    }


    /**
     * Gets the resultLL value for this AsFound.
     * 
     * @return resultLL
     */
    public java.lang.Float getResultLL() {
        return resultLL;
    }


    /**
     * Sets the resultLL value for this AsFound.
     * 
     * @param resultLL
     */
    public void setResultLL(java.lang.Float resultLL) {
        this.resultLL = resultLL;
    }


    /**
     * Gets the kWResult value for this AsFound.
     * 
     * @return kWResult
     */
    public java.lang.Float getKWResult() {
        return kWResult;
    }


    /**
     * Sets the kWResult value for this AsFound.
     * 
     * @param kWResult
     */
    public void setKWResult(java.lang.Float kWResult) {
        this.kWResult = kWResult;
    }


    /**
     * Gets the kVArResult value for this AsFound.
     * 
     * @return kVArResult
     */
    public java.lang.Float getKVArResult() {
        return kVArResult;
    }


    /**
     * Sets the kVArResult value for this AsFound.
     * 
     * @param kVArResult
     */
    public void setKVArResult(java.lang.Float kVArResult) {
        this.kVArResult = kVArResult;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof AsFound)) return false;
        AsFound other = (AsFound) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.kWhReading==null && other.getKWhReading()==null) || 
             (this.kWhReading!=null &&
              this.kWhReading.equals(other.getKWhReading()))) &&
            ((this.kWReading==null && other.getKWReading()==null) || 
             (this.kWReading!=null &&
              this.kWReading.equals(other.getKWReading()))) &&
            ((this.kVArReading==null && other.getKVArReading()==null) || 
             (this.kVArReading!=null &&
              this.kVArReading.equals(other.getKVArReading()))) &&
            ((this.powerFactorReading==null && other.getPowerFactorReading()==null) || 
             (this.powerFactorReading!=null &&
              this.powerFactorReading.equals(other.getPowerFactorReading()))) &&
            ((this.resultHigh==null && other.getResultHigh()==null) || 
             (this.resultHigh!=null &&
              this.resultHigh.equals(other.getResultHigh()))) &&
            ((this.resultLow==null && other.getResultLow()==null) || 
             (this.resultLow!=null &&
              this.resultLow.equals(other.getResultLow()))) &&
            ((this.resultLL==null && other.getResultLL()==null) || 
             (this.resultLL!=null &&
              this.resultLL.equals(other.getResultLL()))) &&
            ((this.kWResult==null && other.getKWResult()==null) || 
             (this.kWResult!=null &&
              this.kWResult.equals(other.getKWResult()))) &&
            ((this.kVArResult==null && other.getKVArResult()==null) || 
             (this.kVArResult!=null &&
              this.kVArResult.equals(other.getKVArResult())));
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
        if (getKWhReading() != null) {
            _hashCode += getKWhReading().hashCode();
        }
        if (getKWReading() != null) {
            _hashCode += getKWReading().hashCode();
        }
        if (getKVArReading() != null) {
            _hashCode += getKVArReading().hashCode();
        }
        if (getPowerFactorReading() != null) {
            _hashCode += getPowerFactorReading().hashCode();
        }
        if (getResultHigh() != null) {
            _hashCode += getResultHigh().hashCode();
        }
        if (getResultLow() != null) {
            _hashCode += getResultLow().hashCode();
        }
        if (getResultLL() != null) {
            _hashCode += getResultLL().hashCode();
        }
        if (getKWResult() != null) {
            _hashCode += getKWResult().hashCode();
        }
        if (getKVArResult() != null) {
            _hashCode += getKVArResult().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(AsFound.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "asFound"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("KWhReading");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "kWhReading"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "integer"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("KWReading");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "kWReading"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "float"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("KVArReading");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "kVArReading"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "float"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("powerFactorReading");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "powerFactorReading"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "float"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("resultHigh");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "resultHigh"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "float"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("resultLow");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "resultLow"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "float"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("resultLL");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "resultLL"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "float"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("KWResult");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "kWResult"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "float"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("KVArResult");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "kVArResult"));
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
