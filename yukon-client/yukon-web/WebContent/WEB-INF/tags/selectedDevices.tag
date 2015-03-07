<%@ tag body-content="empty" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<%@ attribute name="id" %>
<%@ attribute name="deviceCollection" required="true" type="java.lang.Object"%>

<c:set var="count" value="${deviceCollection.deviceCount}"/>
<div <c:if test="${not empty pageScope.id}">id="${id}"</c:if>>
    <strong><cti:msg2 key="yukon.common.device.bulk.selectedDevicesPopup.linkLabel"/>:</strong>&nbsp;
    <span class="badge js-count">${count}</span>&nbsp;
    <cti:msg2 key="${deviceCollection.description}"/>
    <c:if test="${count > 0}"><tags:selectedDevicesPopup deviceCollection="${deviceCollection}"/></c:if>
</div>