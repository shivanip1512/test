<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<%@ attribute name="deviceCollection" required="false" type="java.lang.Object"%>
<%@ attribute name="groupName" required="false" type="java.lang.String"%>

<cti:includeScript link="/JavaScript/showSelectedDevices.js"/>

<cti:uniqueIdentifier var="id" prefix="selectedDevicesPopup_"/>

<cti:msg var="popupTitle" key="yukon.common.device.bulk.selectedDevicesPopup.popupTitle" />
<cti:msg var="warning" key="yukon.common.device.bulk.selectedDevicesPopup.warning" />

<%-- CREATE URL --%>
<c:choose>
	<c:when test="${not empty pageScope.deviceCollection}">
		<c:url var="selectedDevicesTableUrl" value="/spring/bulk/selectedDevicesTableForDeviceCollection">
		    <c:forEach var="deviceCollectionParam" items="${pageScope.deviceCollection.collectionParameters}">
		        <c:param name="${deviceCollectionParam.key}" value="${fn:escapeXml(deviceCollectionParam.value)}"/>
		    </c:forEach>
		</c:url>
	</c:when>

	<c:when test="${not empty pageScope.groupName}">
		<c:url var="selectedDevicesTableUrl" value="/spring/bulk/selectedDevicesTableForGroupName">
	        <c:param name="groupName" value="${fn:escapeXml(pageScope.groupName)}"/>
		</c:url>
	</c:when>
	
	<c:otherwise>
		<div class="errorRed">selectedDevicesPopupTag requires use of either deviceCollection or groupName parameter</div>
	</c:otherwise>

</c:choose>

<%-- POPUP LINK --%>
<div class="dib">
	<a href="javascript:void(0);" title="${warning}" class="icon magnifier f_showSelectedDevices" data-function-arguments="{'id':'${id}', 'url':'${selectedDevicesTableUrl}'}">${warning}</a>
</div>
<tags:simplePopup id="${id}" title="${popupTitle}">
    <div style="height:300px;overflow:auto;">
    <div class="smallBoldLabel" id="${id}InnerDiv" style="text-align:left;"></div>
    </div>
</tags:simplePopup>