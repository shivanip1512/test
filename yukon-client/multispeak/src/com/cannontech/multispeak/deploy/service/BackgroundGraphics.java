/**
 * BackgroundGraphics.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Dec 28, 2007 (09:53:13 CST) WSDL2Java emitter.
 */

package com.cannontech.multispeak.deploy.service;

public class BackgroundGraphics  extends com.cannontech.multispeak.deploy.service.MspObject  implements java.io.Serializable {
    private com.cannontech.multispeak.deploy.service.GenericAnnotationFeature[] genericAnnotationFeatureList;

    private com.cannontech.multispeak.deploy.service.GenericPointFeature[] genericPointFeatureList;

    private com.cannontech.multispeak.deploy.service.GenericLineFeature[] genericLineFeatureList;

    private com.cannontech.multispeak.deploy.service.GraphicSymbol[] graphicSymbolList;

    public BackgroundGraphics() {
    }

    public BackgroundGraphics(
           java.lang.String objectID,
           com.cannontech.multispeak.deploy.service.Action verb,
           java.lang.String errorString,
           java.lang.String replaceID,
           java.lang.String utility,
           com.cannontech.multispeak.deploy.service.Extensions extensions,
           java.lang.String comments,
           com.cannontech.multispeak.deploy.service.ExtensionsItem[] extensionsList,
           com.cannontech.multispeak.deploy.service.GenericAnnotationFeature[] genericAnnotationFeatureList,
           com.cannontech.multispeak.deploy.service.GenericPointFeature[] genericPointFeatureList,
           com.cannontech.multispeak.deploy.service.GenericLineFeature[] genericLineFeatureList,
           com.cannontech.multispeak.deploy.service.GraphicSymbol[] graphicSymbolList) {
        super(
            objectID,
            verb,
            errorString,
            replaceID,
            utility,
            extensions,
            comments,
            extensionsList);
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
    public com.cannontech.multispeak.deploy.service.GenericAnnotationFeature[] getGenericAnnotationFeatureList() {
        return genericAnnotationFeatureList;
    }


    /**
     * Sets the genericAnnotationFeatureList value for this BackgroundGraphics.
     * 
     * @param genericAnnotationFeatureList
     */
    public void setGenericAnnotationFeatureList(com.cannontech.multispeak.deploy.service.GenericAnnotationFeature[] genericAnnotationFeatureList) {
        this.genericAnnotationFeatureList = genericAnnotationFeatureList;
    }


    /**
     * Gets the genericPointFeatureList value for this BackgroundGraphics.
     * 
     * @return genericPointFeatureList
     */
    public com.cannontech.multispeak.deploy.service.GenericPointFeature[] getGenericPointFeatureList() {
        return genericPointFeatureList;
    }


    /**
     * Sets the genericPointFeatureList value for this BackgroundGraphics.
     * 
     * @param genericPointFeatureList
     */
    public void setGenericPointFeatureList(com.cannontech.multispeak.deploy.service.GenericPointFeature[] genericPointFeatureList) {
        this.genericPointFeatureList = genericPointFeatureList;
    }


    /**
     * Gets the genericLineFeatureList value for this BackgroundGraphics.
     * 
     * @return genericLineFeatureList
     */
    public com.cannontech.multispeak.deploy.service.GenericLineFeature[] getGenericLineFeatureList() {
        return genericLineFeatureList;
    }


    /**
     * Sets the genericLineFeatureList value for this BackgroundGraphics.
     * 
     * @param genericLineFeatureList
     */
    public void setGenericLineFeatureList(com.cannontech.multispeak.deploy.service.GenericLineFeature[] genericLineFeatureList) {
        this.genericLineFeatureList = genericLineFeatureList;
    }


    /**
     * Gets the graphicSymbolList value for this BackgroundGraphics.
     * 
     * @return graphicSymbolList
     */
    public com.cannontech.multispeak.deploy.service.GraphicSymbol[] getGraphicSymbolList() {
        return graphicSymbolList;
    }


    /**
     * Sets the graphicSymbolList value for this BackgroundGraphics.
     * 
     * @param graphicSymbolList
     */
    public void setGraphicSymbolList(com.cannontech.multispeak.deploy.service.GraphicSymbol[] graphicSymbolList) {
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
              java.util.Arrays.equals(this.genericAnnotationFeatureList, other.getGenericAnnotationFeatureList()))) &&
            ((this.genericPointFeatureList==null && other.getGenericPointFeatureList()==null) || 
             (this.genericPointFeatureList!=null &&
              java.util.Arrays.equals(this.genericPointFeatureList, other.getGenericPointFeatureList()))) &&
            ((this.genericLineFeatureList==null && other.getGenericLineFeatureList()==null) || 
             (this.genericLineFeatureList!=null &&
              java.util.Arrays.equals(this.genericLineFeatureList, other.getGenericLineFeatureList()))) &&
            ((this.graphicSymbolList==null && other.getGraphicSymbolList()==null) || 
             (this.graphicSymbolList!=null &&
              java.util.Arrays.equals(this.graphicSymbolList, other.getGraphicSymbolList())));
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
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getGenericAnnotationFeatureList());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getGenericAnnotationFeatureList(), i);
                if (obj != null &&
                    !obj.getClass().isArray()) {
                    _hashCode += obj.hashCode();
                }
            }
        }
        if (getGenericPointFeatureList() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getGenericPointFeatureList());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getGenericPointFeatureList(), i);
                if (obj != null &&
                    !obj.getClass().isArray()) {
                    _hashCode += obj.hashCode();
                }
            }
        }
        if (getGenericLineFeatureList() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getGenericLineFeatureList());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getGenericLineFeatureList(), i);
                if (obj != null &&
                    !obj.getClass().isArray()) {
                    _hashCode += obj.hashCode();
                }
            }
        }
        if (getGraphicSymbolList() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getGraphicSymbolList());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getGraphicSymbolList(), i);
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
        new org.apache.axis.description.TypeDesc(BackgroundGraphics.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "backgroundGraphics"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("genericAnnotationFeatureList");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "genericAnnotationFeatureList"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "genericAnnotationFeature"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        elemField.setItemQName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "genericAnnotationFeature"));
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("genericPointFeatureList");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "genericPointFeatureList"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "genericPointFeature"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        elemField.setItemQName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "genericPointFeature"));
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("genericLineFeatureList");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "genericLineFeatureList"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "genericLineFeature"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        elemField.setItemQName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "genericLineFeature"));
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("graphicSymbolList");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "graphicSymbolList"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "graphicSymbol"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        elemField.setItemQName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "graphicSymbol"));
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
