<%@ tag trimDirectiveWhitespaces="true" body-content="empty" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<%@ attribute name="id" required="true" %>
<%@ attribute name="deviceCollection" required="true" type="java.lang.Object"%>

<c:set value="${deviceCollection.deviceCount}" var="deviceCount"/>
<div class="note">
    <div>
        <strong><cti:msg2 key="yukon.common.device.bulk.selectedDevicesPopup.linkLabel"/>:&nbsp;&nbsp;</strong><cti:msg2 key="${deviceCollection.description}"/>
        <c:if test="${deviceCount > 0}">
            <tags:selectedDevicesPopup deviceCollection="${deviceCollection}"/>
        </c:if>
    </div>
    <div>
        <strong><cti:msg2 key="yukon.common.device.bulk.selectedDevicesPopup.deviceCount"/>:&nbsp;&nbsp;</strong>${deviceCount}
    </div>
</div>