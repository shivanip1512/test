<%@ tag body-content="empty" trimDirectiveWhitespaces="true" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<%@ attribute name="classes" description="CSS class names applied to the anchor element." %>
<%@ attribute name="pao" required="true" type="com.cannontech.common.pao.YukonPao" %>
<%@ attribute name="pointId" type="java.lang.Integer" %>
<%@ attribute name="qualityIndicator" required="false" type="com.cannontech.core.service.PointFormattingService.Format" %>

<c:choose>
    <c:when test="${pointId == 0}">
        <span class="error"><i:inline key="yukon.common.attributes.pointNotFound"/></span>
    </c:when>
    <c:otherwise>
        <cti:uniqueIdentifier var="popupId" prefix="historical-readings-"/>
        <cti:url var="historyUrl" value="/meter/historicalReadings/view">
            <cti:param name="pointId" value="${pointId}"/>
            <cti:param name="deviceId" value="${pao.paoIdentifier.paoId}"/>
        </cti:url>
        <a href="javascript:void(0);" data-popup="#${popupId}" class="${pageScope.classes}">
            <c:choose>
                <c:when test="${empty qualityIndicator}">
                    <cti:pointValue pointId="${pointId}" format="DATE"/>
                </c:when>
                <c:otherwise>
                    <cti:pointValue pointId="${pointId}" format="${qualityIndicator}"/>
                </c:otherwise>
            </c:choose>
        </a>
        <div id="${popupId}" data-width="500" data-height="400" data-url="${historyUrl}" class="dn"></div>

    </c:otherwise>
</c:choose>