<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="dr" tagdir="/WEB-INF/tags/dr" %>

<c:choose>
    <c:when test="${assetTotal <= 0}">
        <span class="empty-list">
            <cti:msg2 key="yukon.web.modules.operator.hardware.assetAvailability.noDevices"/>
        </span>
    </c:when>
    <c:otherwise>
        <dr:assetAvailabilityStatus assetId="${paoId}"
            assetAvailabilitySummary="${assetAvailabilitySummary}" 
            pieJSONData="${pieJSONData}" showDetails="true"/>
    </c:otherwise>
</c:choose>