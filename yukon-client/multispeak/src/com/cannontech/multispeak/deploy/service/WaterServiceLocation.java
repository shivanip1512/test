/**
 * WaterServiceLocation.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Dec 28, 2007 (09:53:13 CST) WSDL2Java emitter.
 */

package com.cannontech.multispeak.deploy.service;

public class WaterServiceLocation  extends com.cannontech.multispeak.deploy.service.MspServiceLocation  implements java.io.Serializable {
    public WaterServiceLocation() {
    }

    public WaterServiceLocation(
           java.lang.String objectID,
           com.cannontech.multispeak.deploy.service.Action verb,
           java.lang.String errorString,
           java.lang.String replaceID,
           java.lang.String utility,
           com.cannontech.multispeak.deploy.service.Extensions extensions,
           java.lang.String comments,
           com.cannontech.multispeak.deploy.service.ExtensionsItem[] extensionsList,
           com.cannontech.multispeak.deploy.service.PointType mapLocation,
           java.lang.String gridLocation,
           java.lang.Float rotation,
           java.lang.String facilityID,
           com.cannontech.multispeak.deploy.service.GraphicSymbol[] graphicSymbol,
           com.cannontech.multispeak.deploy.service.GenericAnnotationFeature[] annotationList,
           java.lang.String custID,
           java.lang.String accountNumber,
           java.lang.String facilityName,
           java.lang.String siteID,
           com.cannontech.multispeak.deploy.service.Address serviceAddress,
           java.lang.String revenueClass,
           java.lang.String servStatus,
           java.lang.String billingCycle,
           java.lang.String route,
           java.lang.String budgBill,
           java.lang.Float acRecvBal,
           java.lang.Float acRecvCur,
           java.lang.Float acRecv30,
           java.lang.Float acRecv60,
           java.lang.Float acRecv90,
           java.util.Calendar paymentDueDate,
           java.util.Calendar lastPaymentDate,
           java.lang.Float lastPaymentAmount,
           java.util.Calendar billDate,
           java.util.Calendar shutOffDate,
           java.util.Calendar connectDate,
           java.util.Calendar disconnectDate,
           com.cannontech.multispeak.deploy.service.MspNetwork network,
           java.lang.String SIC,
           java.lang.String woNumber,
           java.lang.String soNumber) {
        super(
            objectID,
            verb,
            errorString,
            replaceID,
            utility,
            extensions,
            comments,
            extensionsList,
            mapLocation,
            gridLocation,
            rotation,
            facilityID,
            graphicSymbol,
            annotationList,
            custID,
            accountNumber,
            facilityName,
            siteID,
            serviceAddress,
            revenueClass,
            servStatus,
            billingCycle,
            route,
            budgBill,
            acRecvBal,
            acRecvCur,
            acRecv30,
            acRecv60,
            acRecv90,
            paymentDueDate,
            lastPaymentDate,
            lastPaymentAmount,
            billDate,
            shutOffDate,
            connectDate,
            disconnectDate,
            network,
            SIC,
            woNumber,
            soNumber);
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof WaterServiceLocation)) return false;
        WaterServiceLocation other = (WaterServiceLocation) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = super.equals(obj);
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
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(WaterServiceLocation.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.multispeak.org/Version_3.0", "waterServiceLocation"));
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
