/**
 * DisplayODMonitoringRequestsResponse.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.2 May 03, 2005 (02:20:24 EDT) WSDL2Java emitter.
 */

package com.cannontech.multispeak;

public class DisplayODMonitoringRequestsResponse  implements java.io.Serializable {
    private com.cannontech.multispeak.ArrayOfObjectRef displayODMonitoringRequestsResult;

    public DisplayODMonitoringRequestsResponse() {
    }

    public DisplayODMonitoringRequestsResponse(
           com.cannontech.multispeak.ArrayOfObjectRef displayODMonitoringRequestsResult) {
           this.displayODMonitoringRequestsResult = displayODMonitoringRequestsResult;
    }


    /**
     * Gets the displayODMonitoringRequestsResult value for this DisplayODMonitoringRequestsResponse.
     * 
     * @return displayODMonitoringRequestsResult
     */
    public com.cannontech.multispeak.ArrayOfObjectRef getDisplayODMonitoringRequestsResult() {
        return displayODMonitoringRequestsResult;
    }


    /**
     * Sets the displayODMonitoringRequestsResult value for this DisplayODMonitoringRequestsResponse.
     * 
     * @param displayODMonitoringRequestsResult
     */
    public void setDisplayODMonitoringRequestsResult(com.cannontech.multispeak.ArrayOfObjectRef displayODMonitoringRequestsResult) {
        this.displayODMonitoringRequestsResult = displayODMonitoringRequestsResult;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof DisplayODMonitoringRequestsResponse)) return false;
        DisplayODMonitoringRequestsResponse other = (DisplayODMonitoringRequestsResponse) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.displayODMonitoringRequestsResult==null && other.getDisplayODMonitoringRequestsResult()==null) || 
             (this.displayODMonitoringRequestsResult!=null &&
              this.displayODMonitoringRequestsResult.equals(other.getDisplayODMonitoringRequestsResult())));
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
        if (getDisplayODMonitoringRequestsResult() != null) {
            _hashCode += getDisplayODMonitoringRequestsResult().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(DisplayODMonitoringRequestsResponse.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", ">DisplayODMonitoringRequestsResponse"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("displayODMonitoringRequestsResult");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "DisplayODMonitoringRequestsResult"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "ArrayOfObjectRef"));
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
