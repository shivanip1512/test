<%@ attribute name="device" required="true" type="com.cannontech.common.pao.YukonDevice"%>
<%@ attribute name="pointId" required="true" type="java.lang.Integer"%>
<%@ attribute name="showHistoricalReadings" required="false" type="java.lang.Boolean"%>

<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>

<c:choose>
    <c:when test="${pointId == 0}">
        <span class="errorMessage">
            <i:inline key="yukon.common.attributes.pointNotFound"/>
        </span>
    </c:when>
    <c:otherwise>
        <span class="wsnw">
            <c:if test="${empty pageScope.showHistoricalReadings || pageScope.showHistoricalReadings}">
                <cti:uniqueIdentifier var="uid" prefix="historicalReadings_" />
                <cti:url var="showHistoricalReadingsUrl" value="/meter/historicalReadings/view">
                    <cti:param name="div_id" value="${uid}" />
                    <cti:param name="pointId" value="${pointId}" />
                    <cti:param name="deviceId" value="${deviceId}" />
                </cti:url>
                <a class="f-ajaxPage pv_history" 
                    data-selector="#${uid}" 
                    href="${showHistoricalReadingsUrl}">
                    <cti:pointValue pointId="${pointId}" format="DATE" cssClass="fl"/>
                </a>
                <div id="${uid}"></div>
            </c:if>
        </span>
    </c:otherwise>
</c:choose>
