<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="dt" tagdir="/WEB-INF/tags/dateTime" %>

<cti:msgScope paths="modules.operator.hardware">

<tags:setFormEditMode mode="${mode}"/>

<tags:nameValueContainer2>

    <form:form commandName="hardware">

     <tags:nameValue2 nameKey="${displayTypeKey}">
        ${fn:escapeXml(hardware.displayType)}
    </tags:nameValue2>
    
   <%-- For switchs and tstat's, show serial number. If is also a device, show device name--%>
    <c:choose>
        <c:when test="${showSerialNumber}">
            <tags:inputNameValue nameKey=".serialNumber" path="serialNumber" inputClass="js-focus"></tags:inputNameValue>
        </c:when>
        <c:otherwise>
            <tags:nameValue2 nameKey=".meterNumber">
                <form:hidden path="meterNumber"/>
                ${fn:escapeXml(hardware.meterNumber)}
            </tags:nameValue2>
        </c:otherwise>
    </c:choose>
  
    <c:if test="${hardware.deviceId > 0}">
        <tags:nameValue2 nameKey=".displayName">
            <cti:url var="inventoryUrl" value="/stars/operator/inventory/view">
                <cti:param name="inventoryId" value="${hardware.inventoryId}"/>
            </cti:url>
            <c:if test="${hardware.accountId > 0}">
                <cti:url var="inventoryUrl" value="/stars/operator/hardware/view">
                    <cti:param name="inventoryId" value="${hardware.inventoryId}"/>
                    <cti:param name="accountId" value="${hardware.accountId}"/>
                </cti:url>
            </c:if>
            <a href="${inventoryUrl}"><cti:deviceName deviceId="${hardware.deviceId}"/></a>
        </tags:nameValue2>
    </c:if>
        
    <c:if test="${showInstallCode}">
        <tags:inputNameValue nameKey=".installCode" path="installCode" disabled="false" maxlength="23" size="25"/>
    </c:if>
    
    <c:if test="${showMacAddress}">
        <tags:inputNameValue nameKey=".macAddress" path="macAddress" maxlength="23" size="25"/>
   </c:if>
 
    <c:if test="${showDeviceVendorUserId}">
        <tags:inputNameValue nameKey=".deviceVendorUserId" path="deviceVendorUserId" maxlength="23" size="25"/>
    </c:if>
 
    <c:if test="${showFirmwareVersion}">
        <tags:inputNameValue nameKey=".firmwareVersion" path="firmwareVersion"></tags:inputNameValue>
    </c:if>
    
    <tags:inputNameValue nameKey=".label" path="displayLabel"/>
    
    <tags:inputNameValue nameKey=".altTrackingNumber" path="altTrackingNumber"/>

</form:form>

</tags:nameValueContainer2>

</cti:msgScope>