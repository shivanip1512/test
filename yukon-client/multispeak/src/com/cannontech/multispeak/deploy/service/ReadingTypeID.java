/**
 * ReadingTypeID.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Dec 28, 2007 (09:53:13 CST) WSDL2Java emitter.
 */

package com.cannontech.multispeak.deploy.service;

public class ReadingTypeID  implements java.io.Serializable {
    private java.lang.String name;  // attribute

    private java.lang.String timeAttribute;  // attribute

    private java.lang.String dataQualifier;  // attribute

    private java.lang.String accumulationBehavior;  // attribute

    private java.lang.String flowDirection;  // attribute

    private java.lang.String unitOfMeasure;  // attribute

    private java.lang.String measurementCategory;  // attribute

    private java.lang.String phaseIndex;  // attribute

    private java.lang.String unitsMultiplier;  // attribute

    private java.lang.String displayableUOM;  // attribute

    public ReadingTypeID() {
    }

    public ReadingTypeID(
           java.lang.String name,
           java.lang.String timeAttribute,
           java.lang.String dataQualifier,
           java.lang.String accumulationBehavior,
           java.lang.String flowDirection,
           java.lang.String unitOfMeasure,
           java.lang.String measurementCategory,
           java.lang.String phaseIndex,
           java.lang.String unitsMultiplier,
           java.lang.String displayableUOM) {
           this.name = name;
           this.timeAttribute = timeAttribute;
           this.dataQualifier = dataQualifier;
           this.accumulationBehavior = accumulationBehavior;
           this.flowDirection = flowDirection;
           this.unitOfMeasure = unitOfMeasure;
           this.measurementCategory = measurementCategory;
           this.phaseIndex = phaseIndex;
           this.unitsMultiplier = unitsMultiplier;
           this.displayableUOM = displayableUOM;
    }


    /**
     * Gets the name value for this ReadingTypeID.
     * 
     * @return name
     */
    public java.lang.String getName() {
        return name;
    }


    /**
     * Sets the name value for this ReadingTypeID.
     * 
     * @param name
     */
    public void setName(java.lang.String name) {
        this.name = name;
    }


    /**
     * Gets the timeAttribute value for this ReadingTypeID.
     * 
     * @return timeAttribute
     */
    public java.lang.String getTimeAttribute() {
        return timeAttribute;
    }


    /**
     * Sets the timeAttribute value for this ReadingTypeID.
     * 
     * @param timeAttribute
     */
    public void setTimeAttribute(java.lang.String timeAttribute) {
        this.timeAttribute = timeAttribute;
    }


    /**
     * Gets the dataQualifier value for this ReadingTypeID.
     * 
     * @return dataQualifier
     */
    public java.lang.String getDataQualifier() {
        return dataQualifier;
    }


    /**
     * Sets the dataQualifier value for this ReadingTypeID.
     * 
     * @param dataQualifier
     */
    public void setDataQualifier(java.lang.String dataQualifier) {
        this.dataQualifier = dataQualifier;
    }


    /**
     * Gets the accumulationBehavior value for this ReadingTypeID.
     * 
     * @return accumulationBehavior
     */
    public java.lang.String getAccumulationBehavior() {
        return accumulationBehavior;
    }


    /**
     * Sets the accumulationBehavior value for this ReadingTypeID.
     * 
     * @param accumulationBehavior
     */
    public void setAccumulationBehavior(java.lang.String accumulationBehavior) {
        this.accumulationBehavior = accumulationBehavior;
    }


    /**
     * Gets the flowDirection value for this ReadingTypeID.
     * 
     * @return flowDirection
     */
    public java.lang.String getFlowDirection() {
        return flowDirection;
    }


    /**
     * Sets the flowDirection value for this ReadingTypeID.
     * 
     * @param flowDirection
     */
    public void setFlowDirection(java.lang.String flowDirection) {
        this.flowDirection = flowDirection;
    }


    /**
     * Gets the unitOfMeasure value for this ReadingTypeID.
     * 
     * @return unitOfMeasure
     */
    public java.lang.String getUnitOfMeasure() {
        return unitOfMeasure;
    }


    /**
     * Sets the unitOfMeasure value for this ReadingTypeID.
     * 
     * @param unitOfMeasure
     */
    public void setUnitOfMeasure(java.lang.String unitOfMeasure) {
        this.unitOfMeasure = unitOfMeasure;
    }


    /**
     * Gets the measurementCategory value for this ReadingTypeID.
     * 
     * @return measurementCategory
     */
    public java.lang.String getMeasurementCategory() {
        return measurementCategory;
    }


    /**
     * Sets the measurementCategory value for this ReadingTypeID.
     * 
     * @param measurementCategory
     */
    public void setMeasurementCategory(java.lang.String measurementCategory) {
        this.measurementCategory = measurementCategory;
    }


    /**
     * Gets the phaseIndex value for this ReadingTypeID.
     * 
     * @return phaseIndex
     */
    public java.lang.String getPhaseIndex() {
        return phaseIndex;
    }


    /**
     * Sets the phaseIndex value for this ReadingTypeID.
     * 
     * @param phaseIndex
     */
    public void setPhaseIndex(java.lang.String phaseIndex) {
        this.phaseIndex = phaseIndex;
    }


    /**
     * Gets the unitsMultiplier value for this ReadingTypeID.
     * 
     * @return unitsMultiplier
     */
    public java.lang.String getUnitsMultiplier() {
        return unitsMultiplier;
    }


    /**
     * Sets the unitsMultiplier value for this ReadingTypeID.
     * 
     * @param unitsMultiplier
     */
    public void setUnitsMultiplier(java.lang.String unitsMultiplier) {
        this.unitsMultiplier = unitsMultiplier;
    }


    /**
     * Gets the displayableUOM value for this ReadingTypeID.
     * 
     * @return displayableUOM
     */
    public java.lang.String getDisplayableUOM() {
        return displayableUOM;
    }


    /**
     * Sets the displayableUOM value for this ReadingTypeID.
     * 
     * @param displayableUOM
     */
    public void setDisplayableUOM(java.lang.String displayableUOM) {
        this.displayableUOM = displayableUOM;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof ReadingTypeID)) return false;
        ReadingTypeID other = (ReadingTypeID) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.name==null && other.getName()==null) || 
             (this.name!=null &&
              this.name.equals(other.getName()))) &&
            ((this.timeAttribute==null && other.getTimeAttribute()==null) || 
             (this.timeAttribute!=null &&
              this.timeAttribute.equals(other.getTimeAttribute()))) &&
            ((this.dataQualifier==null && other.getDataQualifier()==null) || 
             (this.dataQualifier!=null &&
              this.dataQualifier.equals(other.getDataQualifier()))) &&
            ((this.accumulationBehavior==null && other.getAccumulationBehavior()==null) || 
             (this.accumulationBehavior!=null &&
              this.accumulationBehavior.equals(other.getAccumulationBehavior()))) &&
            ((this.flowDirection==null && other.getFlowDirection()==null) || 
             (this.flowDirection!=null &&
              this.flowDirection.equals(other.getFlowDirection()))) &&
            ((this.unitOfMeasure==null && other.getUnitOfMeasure()==null) || 
             (this.unitOfMeasure!=null &&
              this.unitOfMeasure.equals(other.getUnitOfMeasure()))) &&
            ((this.measurementCategory==null && other.getMeasurementCategory()==null) || 
             (this.measurementCategory!=null &&
              this.measurementCategory.equals(other.getMeasurementCategory()))) &&
            ((this.phaseIndex==null && other.getPhaseIndex()==null) || 
             (this.phaseIndex!=null &&
              this.phaseIndex.equals(other.getPhaseIndex()))) &&
            ((this.unitsMultiplier==null && other.getUnitsMultiplier()==null) || 
             (this.unitsMultiplier!=null &&
              this.unitsMultiplier.equals(other.getUnitsMultiplier()))) &&
            ((this.displayableUOM==null && other.getDisplayableUOM()==null) || 
             (this.displayableUOM!=null &&
              this.displayableUOM.equals(other.getDisplayableUOM())));
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
        if (getName() != null) {
            _hashCode += getName().hashCode();
        }
        if (getTimeAttribute() != null) {
            _hashCode += getTimeAttribute().hashCode();
        }
        if (getDataQualifier() != null) {
            _hashCode += getDataQualifier().hashCode();
        }
        if (getAccumulationBehavior() != null) {
            _hashCode += getAccumulationBehavior().hashCode();
        }
        if (getFlowDirection() != null) {
            _hashCode += getFlowDirection().hashCode();
        }
        if (getUnitOfMeasure() != null) {
            _hashCode += getUnitOfMeasure().hashCode();
        }
        if (getMeasurementCategory() != null) {
            _hashCode += getMeasurementCategory().hashCode();
        }
        if (getPhaseIndex() != null) {
            _hashCode += getPhaseIndex().hashCode();
        }
        if (getUnitsMultiplier() != null) {
            _hashCode += getUnitsMultiplier().hashCode();
        }
        if (getDisplayableUOM() != null) {
            _hashCode += getDisplayableUOM().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(ReadingTypeID.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "readingTypeID"));
        org.apache.axis.description.AttributeDesc attrField = new org.apache.axis.description.AttributeDesc();
        attrField.setFieldName("name");
        attrField.setXmlName(new javax.xml.namespace.QName("", "name"));
        attrField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        typeDesc.addFieldDesc(attrField);
        attrField = new org.apache.axis.description.AttributeDesc();
        attrField.setFieldName("timeAttribute");
        attrField.setXmlName(new javax.xml.namespace.QName("", "timeAttribute"));
        attrField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        typeDesc.addFieldDesc(attrField);
        attrField = new org.apache.axis.description.AttributeDesc();
        attrField.setFieldName("dataQualifier");
        attrField.setXmlName(new javax.xml.namespace.QName("", "dataQualifier"));
        attrField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        typeDesc.addFieldDesc(attrField);
        attrField = new org.apache.axis.description.AttributeDesc();
        attrField.setFieldName("accumulationBehavior");
        attrField.setXmlName(new javax.xml.namespace.QName("", "accumulationBehavior"));
        attrField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        typeDesc.addFieldDesc(attrField);
        attrField = new org.apache.axis.description.AttributeDesc();
        attrField.setFieldName("flowDirection");
        attrField.setXmlName(new javax.xml.namespace.QName("", "flowDirection"));
        attrField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        typeDesc.addFieldDesc(attrField);
        attrField = new org.apache.axis.description.AttributeDesc();
        attrField.setFieldName("unitOfMeasure");
        attrField.setXmlName(new javax.xml.namespace.QName("", "unitOfMeasure"));
        attrField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        typeDesc.addFieldDesc(attrField);
        attrField = new org.apache.axis.description.AttributeDesc();
        attrField.setFieldName("measurementCategory");
        attrField.setXmlName(new javax.xml.namespace.QName("", "measurementCategory"));
        attrField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        typeDesc.addFieldDesc(attrField);
        attrField = new org.apache.axis.description.AttributeDesc();
        attrField.setFieldName("phaseIndex");
        attrField.setXmlName(new javax.xml.namespace.QName("", "phaseIndex"));
        attrField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        typeDesc.addFieldDesc(attrField);
        attrField = new org.apache.axis.description.AttributeDesc();
        attrField.setFieldName("unitsMultiplier");
        attrField.setXmlName(new javax.xml.namespace.QName("", "unitsMultiplier"));
        attrField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        typeDesc.addFieldDesc(attrField);
        attrField = new org.apache.axis.description.AttributeDesc();
        attrField.setFieldName("displayableUOM");
        attrField.setXmlName(new javax.xml.namespace.QName("", "displayableUOM"));
        attrField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        typeDesc.addFieldDesc(attrField);
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
