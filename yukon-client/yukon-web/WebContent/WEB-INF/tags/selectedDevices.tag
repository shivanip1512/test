<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<%@ attribute name="id" required="true" type="java.lang.String"%>
<%@ attribute name="deviceCollection" required="true" type="java.lang.Object"%>

<%-- I18N TEXT --%>
<cti:msg var="linkLabel" key="yukon.common.device.bulk.selectedDevicesPopup.linkLabel" />
<c:set value="${deviceCollection.deviceCount}" var="deviceCount"/>

${linkLabel}: <cti:msg key="${deviceCollection.description}"/>
<c:if test="${deviceCount > 0}">
    <tags:selectedDevicesPopup  deviceCollection="${deviceCollection}" />
</c:if>

<br>

<cti:msg key="yukon.common.device.bulk.selectedDevicesPopup.count" argument="${deviceCount}"/>