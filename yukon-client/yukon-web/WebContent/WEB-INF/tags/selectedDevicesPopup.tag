<%@ tag trimDirectiveWhitespaces="true" body-content="empty" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<%@ attribute name="deviceCollection" type="java.lang.Object" %>
<%@ attribute name="groupName" %>
<%@ attribute name="type" description="'link' or 'button'. Default: 'link'" %>
<%@ attribute name="icon" description="Icon to use. Default: 'icon-magnifier'" %>

<cti:default var="icon" value="icon-magnifier"/>
<cti:default var="type" value="link"/>

<cti:includeScript link="/JavaScript/yukon.device.collection.js"/>

<cti:uniqueIdentifier var="id" prefix="selectedDevicesPopup_"/>

<cti:msg2 var="popupTitle" key="yukon.common.device.bulk.selectedDevicesPopup.popupTitle"/>
<cti:msg2 var="warning" key="yukon.common.device.bulk.selectedDevicesPopup.warning"/>

<%-- CREATE URL --%>
<c:choose>
    <c:when test="${not empty pageScope.deviceCollection}">
        <c:url var="selectedDevicesTableUrl" value="/bulk/selectedDevicesTableForDeviceCollection">
            <c:forEach var="deviceCollectionParam" items="${pageScope.deviceCollection.collectionParameters}">
                <c:param name="${deviceCollectionParam.key}" value="${fn:escapeXml(deviceCollectionParam.value)}"/>
            </c:forEach>
        </c:url>
    </c:when>

    <c:when test="${not empty pageScope.groupName}">
        <c:url var="selectedDevicesTableUrl" value="/bulk/selectedDevicesTableForGroupName">
            <c:param name="groupName" value="${fn:escapeXml(pageScope.groupName)}"/>
        </c:url>
    </c:when>
    
    <c:otherwise>
        <div class="error">selectedDevicesPopupTag requires use of either deviceCollection or groupName parameter</div>
    </c:otherwise>
</c:choose>

<c:choose>
    <c:when test="${type == 'link'}">
        <a href="javascript:void(0);" 
            title="${warning}" 
            class="f-showSelectedDevices dib" 
            data-function-arguments="{'id':'${id}', 'url':'${selectedDevicesTableUrl}'}">
            <cti:icon icon="${icon}"/>
        </a>
    </c:when>
    <c:otherwise>
        <cti:button renderMode="buttonImage" 
            icon="${icon}"
            title="${warning}"
            classes="f-showSelectedDevices dib" 
            data-function-arguments="{'id':'${id}', 'url':'${selectedDevicesTableUrl}'}"/>
    </c:otherwise>
</c:choose>
<div title="${popupTitle}" id="${id}" class="dn"></div>