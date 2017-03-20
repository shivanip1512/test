<%@ tag trimDirectiveWhitespaces="true" body-content="empty" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<%@ attribute name="deviceCollection" type="java.lang.Object" 
              description="The device collection: any implementation of the DeviceCollection interface.
                           Either 'deviceCollection' or 'groupName' are required and only one should be used." %>
<%@ attribute name="groupName"
              description="The device group name, used when referencing a device group based collection.
                           Either 'deviceCollection' or 'groupName' are required and only one should be used." %>
                           
<%@ attribute name="type" description="'link' or 'button'. Default: 'link'" %>
<%@ attribute name="icon" description="Icon to use. Default: 'icon-magnifier'" %>

<cti:default var="icon" value="icon-magnifier"/>
<cti:default var="type" value="link"/>

<cti:uniqueIdentifier var="id" prefix="selected-devices-popup-"/>

<cti:msg2 var="popupTitle" key="yukon.common.device.bulk.selectedDevicesPopup.popupTitle"/>
<cti:msg2 var="targetTitle" key="yukon.common.device.bulk.selectedDevicesPopup.warning"/>

<%-- CREATE URL --%>
<c:choose>
    <c:when test="${not empty pageScope.deviceCollection}">
        <c:url var="url" value="/bulk/selectedDevicesTableForDeviceCollection">
            <c:forEach var="deviceCollectionParam" items="${deviceCollection.collectionParameters}">
                <c:param name="${deviceCollectionParam.key}" value="${fn:escapeXml(deviceCollectionParam.value)}"/>
            </c:forEach>
        </c:url>
    </c:when>
    
    <c:when test="${not empty pageScope.groupName}">
        <c:url var="url" value="/bulk/selectedDevicesTableForGroupName">
            <c:param name="groupName" value="${fn:escapeXml(pageScope.groupName)}"/>
        </c:url>
    </c:when>
    
    <c:otherwise>
        <div class="error">selectedDevicesPopupTag requires use of either deviceCollection or groupName parameter</div>
    </c:otherwise>
</c:choose>

<c:choose>
    <c:when test="${type == 'link'}">
        <cti:icon icon="${icon}" data-popup="#${id}" data-popup-toggle="" classes="cp fn pull-icon-down" title="${targetTitle}"/>
    </c:when>
    <c:otherwise>
        <cti:button renderMode="buttonImage" icon="${icon}" title="${targetTitle}" data-popup="#${id}" data-popup-toggle=""/>
    </c:otherwise>
</c:choose>

<div data-title="${popupTitle}" id="${id}" class="dn" data-url="${url}" data-width="500" data-height="300"></div>