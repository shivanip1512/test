<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<%@ attribute name="deviceCollection" required="true" type="java.lang.Object"%>

<cti:includeScript link="/JavaScript/bulkOperations.js"/>

<cti:uniqueIdentifier var="id" prefix="selectedDevicesPopup_"/>

<cti:msg var="popupTitle" key="yukon.common.device.bulk.selectedDevicesPopup.popupTitle" />
<cti:msg var="warning" key="yukon.common.device.bulk.selectedDevicesPopup.warning" />

<%-- CREATE URL --%>
<c:url var="selectedDevicesTableUrl" value="/spring/bulk/selectedDevicesTable">
    <c:forEach var="deviceCollectionParam" items="${deviceCollection.collectionParameters}">
        <c:param name="${deviceCollectionParam.key}" value="${fn:escapeXml(deviceCollectionParam.value)}"/>
    </c:forEach>
</c:url>

<%-- POPUP LINK --%>
<c:url var="mag" value="/WebConfig/yukon/Icons/magnifier.gif"/>
<c:url var="mag_over" value="/WebConfig/yukon/Icons/magnifier_zoom_in.gif"/>

<img onclick="javascript:showSelectedDevices('${id}', '${id}InnerDiv', '${selectedDevicesTableUrl}');" title="${warning}" src="${mag}" onmouseover="javascript:this.src='${mag_over}'" onmouseout="javascript:this.src='${mag}'">
<tags:simplePopup id="${id}" title="${popupTitle}" onClose="closeSelectedDevices('${id}');">
    <div style="height:300px;overflow:auto;">
    <div class="smallBoldLabel" id="${id}InnerDiv"></div>
    </div>
</tags:simplePopup>