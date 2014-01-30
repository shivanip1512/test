<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<%@ attribute name="device" required="true" type="com.cannontech.common.pao.YukonDevice"%>
<%@ attribute name="pointId" required="true" type="java.lang.Integer"%>
<%@ attribute name="showHistoricalReadings" type="java.lang.Boolean"%>

<c:choose>
    <c:when test="${pointId == 0}">
        <span class="error">
            <i:inline key="yukon.common.attributes.pointNotFound"/>
        </span>
    </c:when>
    <c:otherwise>
        <span class="wsnw">
            <c:if test="${empty pageScope.showHistoricalReadings || pageScope.showHistoricalReadings}">
                <cti:uniqueIdentifier var="uid" prefix="historicalReadings_" />
                <cti:url var="historyUrl" value="/meter/historicalReadings/view">
                    <cti:param name="div_id" value="${uid}" />
                    <cti:param name="pointId" value="${pointId}" />
                    <cti:param name="deviceId" value="${deviceId}" />
                </cti:url>
                <a class="f-ajaxPage" data-selector="#${uid}" href="${historyUrl}">
                    <cti:pointValue pointId="${pointId}" format="DATE" cssClass="fl"/>
                </a>
            </c:if>
        </span>
    </c:otherwise>
</c:choose>
<div id="${uid}" data-reloadable></div>