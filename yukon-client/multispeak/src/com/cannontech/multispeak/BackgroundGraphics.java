/**
 * BackgroundGraphics.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.2 May 03, 2005 (02:20:24 EDT) WSDL2Java emitter.
 */

package com.cannontech.multispeak;

public class BackgroundGraphics  extends com.cannontech.multispeak.MspObject  implements java.io.Serializable {
    private com.cannontech.multispeak.ArrayOfGenericAnnotationFeature genericAnnotationFeatureList;
    private com.cannontech.multispeak.ArrayOfGenericPointFeature genericPointFeatureList;
    private com.cannontech.multispeak.ArrayOfGenericLineFeature genericLineFeatureList;
    private com.cannontech.multispeak.ArrayOfGraphicSymbol graphicSymbolList;

    public BackgroundGraphics() {
    }

    public BackgroundGraphics(
           com.cannontech.multispeak.ArrayOfGenericAnnotationFeature genericAnnotationFeatureList,
           com.cannontech.multispeak.ArrayOfGenericPointFeature genericPointFeatureList,
           com.cannontech.multispeak.ArrayOfGenericLineFeature genericLineFeatureList,
           com.cannontech.multispeak.ArrayOfGraphicSymbol graphicSymbolList) {
           this.genericAnnotationFeatureList = genericAnnotationFeatureList;
           this.genericPointFeatureList = genericPointFeatureList;
           this.genericLineFeatureList = genericLineFeatureList;
           this.graphicSymbolList = graphicSymbolList;
    }


    /**
     * Gets the genericAnnotationFeatureList value for this BackgroundGraphics.
     * 
     * @return genericAnnotationFeatureList
     */
    public com.cannontech.multispeak.ArrayOfGenericAnnotationFeature getGenericAnnotationFeatureList() {
        return genericAnnotationFeatureList;
    }


    /**
     * Sets the genericAnnotationFeatureList value for this BackgroundGraphics.
     * 
     * @param genericAnnotationFeatureList
     */
    public void setGenericAnnotationFeatureList(com.cannontech.multispeak.ArrayOfGenericAnnotationFeature genericAnnotationFeatureList) {
        this.genericAnnotationFeatureList = genericAnnotationFeatureList;
    }


    /**
     * Gets the genericPointFeatureList value for this BackgroundGraphics.
     * 
     * @return genericPointFeatureList
     */
    public com.cannontech.multispeak.ArrayOfGenericPointFeature getGenericPointFeatureList() {
        return genericPointFeatureList;
    }


    /**
     * Sets the genericPointFeatureList value for this BackgroundGraphics.
     * 
     * @param genericPointFeatureList
     */
    public void setGenericPointFeatureList(com.cannontech.multispeak.ArrayOfGenericPointFeature genericPointFeatureList) {
        this.genericPointFeatureList = genericPointFeatureList;
    }


    /**
     * Gets the genericLineFeatureList value for this BackgroundGraphics.
     * 
     * @return genericLineFeatureList
     */
    public com.cannontech.multispeak.ArrayOfGenericLineFeature getGenericLineFeatureList() {
        return genericLineFeatureList;
    }


    /**
     * Sets the genericLineFeatureList value for this BackgroundGraphics.
     * 
     * @param genericLineFeatureList
     */
    public void setGenericLineFeatureList(com.cannontech.multispeak.ArrayOfGenericLineFeature genericLineFeatureList) {
        this.genericLineFeatureList = genericLineFeatureList;
    }


    /**
     * Gets the graphicSymbolList value for this BackgroundGraphics.
     * 
     * @return graphicSymbolList
     */
    public com.cannontech.multispeak.ArrayOfGraphicSymbol getGraphicSymbolList() {
        return graphicSymbolList;
    }


    /**
     * Sets the graphicSymbolList value for this BackgroundGraphics.
     * 
     * @param graphicSymbolList
     */
    public void setGraphicSymbolList(com.cannontech.multispeak.ArrayOfGraphicSymbol graphicSymbolList) {
        this.graphicSymbolList = graphicSymbolList;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof BackgroundGraphics)) return false;
        BackgroundGraphics other = (BackgroundGraphics) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = super.equals(obj) && 
            ((this.genericAnnotationFeatureList==null && other.getGenericAnnotationFeatureList()==null) || 
             (this.genericAnnotationFeatureList!=null &&
              this.genericAnnotationFeatureList.equals(other.getGenericAnnotationFeatureList()))) &&
            ((this.genericPointFeatureList==null && other.getGenericPointFeatureList()==null) || 
             (this.genericPointFeatureList!=null &&
              this.genericPointFeatureList.equals(other.getGenericPointFeatureList()))) &&
            ((this.genericLineFeatureList==null && other.getGenericLineFeatureList()==null) || 
             (this.genericLineFeatureList!=null &&
              this.genericLineFeatureList.equals(other.getGenericLineFeatureList()))) &&
            ((this.graphicSymbolList==null && other.getGraphicSymbolList()==null) || 
             (this.graphicSymbolList!=null &&
              this.graphicSymbolList.equals(other.getGraphicSymbolList())));
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
        if (getGenericAnnotationFeatureList() != null) {
            _hashCode += getGenericAnnotationFeatureList().hashCode();
        }
        if (getGenericPointFeatureList() != null) {
            _hashCode += getGenericPointFeatureList().hashCode();
        }
        if (getGenericLineFeatureList() != null) {
            _hashCode += getGenericLineFeatureList().hashCode();
        }
        if (getGraphicSymbolList() != null) {
            _hashCode += getGraphicSymbolList().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(BackgroundGraphics.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "backgroundGraphics"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("genericAnnotationFeatureList");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "genericAnnotationFeatureList"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ArrayOfGenericAnnotationFeature"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("genericPointFeatureList");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "genericPointFeatureList"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ArrayOfGenericPointFeature"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("genericLineFeatureList");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "genericLineFeatureList"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ArrayOfGenericLineFeature"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("graphicSymbolList");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "graphicSymbolList"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ArrayOfGraphicSymbol"));
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
