<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>

<c:choose>
    <c:when test="${parentZone.zoneType == singlePhase}">
        ${fn:escapeXml(parentZone.name)} - <i:inline key="${parentZone.zoneType}"/>: 
        <i:inline key="${regulator.phase}"/>
    </c:when>
    <c:otherwise>
        ${fn:escapeXml(parentZone.name)} - <i:inline key="${parentZone.zoneType}"/>
    </c:otherwise>
</c:choose>